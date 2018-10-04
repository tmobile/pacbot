/*******************************************************************************
 * Copyright 2018 T Mobile, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
/**
  Copyright (C) 2017 T Mobile Inc - All Rights Reserve
  Purpose:
  Author :kkumar
  Modified Date: Oct 20, 2017

 **/
package com.tmobile.pacman.api.compliance.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.slice.SliceBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Range;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;

/**
 * The Class RangeGenerator.
 */
public class RangeGenerator implements Constants {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(RangeGenerator.class);

    /**
     * Generate trend.
     *
     * @param esClusterName the es cluster name
     * @param esHosts the es hosts
     * @param port the port
     * @param dataSource the data source
     * @param esType the es type
     * @param esDateFromFieldToCreateTrendOn the es date from field to create trend on
     * @param esDateToFieldToCreateTrendOn the es date to field to create trend on
     * @param mustNotFilter the must not filter
     * @param mustFilter the must filter
     * @param dateFormat the date format
     * @return the map
     * @throws DataException the data exception
     */
    @SuppressWarnings("resource")
    public Map<String, Long> generateTrend(String esClusterName,
            List<String> esHosts, int port, String dataSource, String esType,
            String esDateFromFieldToCreateTrendOn,
            String esDateToFieldToCreateTrendOn,
            Map<String, String> mustNotFilter, Map<String, String> mustFilter,
            String dateFormat) throws DataException {
        long startTime = System.currentTimeMillis();

        Settings settings = Settings.builder()
                .put("cluster.name", esClusterName)
                .put("client.transport.sniff", true).build();
        TransportClient client = new PreBuiltTransportClient(settings);
        if (esHosts != null && !esHosts.isEmpty()) {
            esHosts.forEach(host -> {
                try {
                    client.addTransportAddress(new InetSocketTransportAddress(
                            InetAddress.getByName(host), port));
                } catch (UnknownHostException e) {
                    LOGGER.error(e.toString());
                }
            });
        } else {
            throw new DataException("host not found");
        }

        final BoolQueryBuilder query = new BoolQueryBuilder();
        query.must(QueryBuilders.termQuery("type", esType)).must(
                QueryBuilders.rangeQuery("modifiedDate").gte("now-15d/d"));
        query.mustNot(QueryBuilders.termQuery("issueStatus.keyword", "unknown"));
        for (Map.Entry<String, String> entry : mustFilter.entrySet()) {
            query.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
        }

        SearchResponse response1 = client.prepareSearch(dataSource)
                .setQuery(query).setSize(0)
                // Don't return any documents, we don't need them.
                .get();

        SearchHits hits = response1.getHits();
        long hitsCount = hits.getTotalHits();
        if (hitsCount > 0) {
            // ES needs minimum 2 slices, if records are less , we need to slice
            // accordingly
            final int scrollSize = hitsCount > 10000 ? 10000
                    : (int) (hitsCount / 2) + 1;
            final int slices = hitsCount > scrollSize ? (int) hitsCount
                    / scrollSize + 1 : 2;

            TimeValue scrollTimeout = new TimeValue(SIXTY * THOUSAND);
            EventBus eventBus = new EventBus();
            List<EventListener> listeners = new ArrayList<>();
            EventListener el = null;
            Calendar localCalendar = Calendar
                    .getInstance(TimeZone.getDefault());
            int currentDayOfYear = localCalendar.get(Calendar.DAY_OF_YEAR);

            for (int i = 1; i <= currentDayOfYear; i++) {
                el = new EventListener(i, dateFormat);
                listeners.add(el);
                eventBus.register(el);
            }

            SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder
                    .searchSource().fetchSource(Boolean.TRUE);

            searchSourceBuilder.query(query);
            IntStream
                    .range(0, slices)
                    .parallel()
                    .forEach(i -> {
                        // prepare search
                            SliceBuilder sliceBuilder = new SliceBuilder(i,
                                    slices);
                            SearchResponse searchResponse = client
                                    .prepareSearch(dataSource)
                                    .setSource(searchSourceBuilder)
                                    .setScroll(scrollTimeout)
                                    .slice(sliceBuilder)
                                    .setSize(scrollSize)
                                    .setFetchSource(
                                            new String[] {
                                                    esDateFromFieldToCreateTrendOn,
                                                    esDateToFieldToCreateTrendOn },
                                            null).get();
                            LOGGER.debug("time taken to fetch the data  -- > "
                                    + (System.currentTimeMillis() - startTime));
                            Arrays.stream(searchResponse.getHits().getHits())
                                    .map(SearchHit::getSourceAsMap)
                                    .map(RangeGenerator.Document::new)
                                    .parallel().forEach(e -> eventBus.post(e));
                        });
            client.close();

            if (LOGGER.isDebugEnabled()) {
                listeners.parallelStream().forEach(
                        obj -> {
                            if (obj.eventsHandled > 0) {
                                LOGGER.debug(obj.date + "------->"
                                        + obj.eventsHandled);
                            }
                        });
            }

            return listeners.stream().collect(
                    Collectors.toMap(EventListener::getDate,
                            EventListener::getEventsHandled));
        } else {

            throw new DataException(NO_DATA_FOUND);
        }
    }

    /**
     * The listener interface for receiving event events.
     * The class that is interested in processing a event
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addEventListener<code> method. When
     * the event event occurs, that object's appropriate
     * method is invoked.
     *
     * @author kkumar
     */
    static class EventListener {

        /** The events handled. */
        private long eventsHandled;

        /** The id. */
        private int _id;

        /** The date. */
        private String date;

        /**
         * Instantiates a new event listener.
         *
         * @param i the i
         * @param dateFormat the date format
         */
        public EventListener(int i, String dateFormat) {
            this._id = i;
            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_YEAR, i);
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            this.date = sdf.format(c.getTime());
        }

        /**
         * Event.
         *
         * @param event the event
         */
        @Subscribe
        public void event(RangeGenerator.Document event) {

            if (event.docRange.contains(_id)) {
                eventsHandled++;
            }
        }

        /**
         * Gets the events handled.
         *
         * @return the events handled
         */
        public long getEventsHandled() {
            return eventsHandled;
        }

        /**
         * Sets the events handled.
         *
         * @param eventsHandled the new events handled
         */
        public void setEventsHandled(int eventsHandled) {
            this.eventsHandled = eventsHandled;
        }

        /**
         * Gets the date.
         *
         * @return the date
         */
        public String getDate() {
            return date;
        }

        /**
         * Sets the date.
         *
         * @param date the new date
         */
        public void setDate(String date) {
            this.date = date;
        }

    }

    /**
     * The Class Document.
     *
     * @author kkumar
     */
    static class Document {

        /** The doc range. */
        Range<Integer> docRange;

        /** The from dt. */
        String fromDt;

        /** The to dt. */
        String toDt;

        /**
         * Instantiates a new document.
         *
         * @param dateMap the date map
         */
        public Document(Map<String, Object> dateMap) {
            this(dateMap.get("createdDate").toString(), dateMap.get(
                    "modifiedDate").toString());
        }

        /**
         * Instantiates a new document.
         *
         * @param fromDate the from date
         * @param toDate the to date
         */
        public Document(String fromDate, String toDate) {
            this.fromDt = fromDate;
            this.toDt = toDate;
            try {
                Calendar cal = new GregorianCalendar();
                cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(fromDate));
                int start = cal.get(Calendar.DAY_OF_YEAR);
                cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(toDate));
                int end = cal.get(Calendar.DAY_OF_YEAR);
                docRange = Range.closed(start, end);
            } catch (Exception e) {
                LOGGER.error(e.toString());
            }
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return fromDt + "," + toDt + "," + docRange;
        }

    }

}
