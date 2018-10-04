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
package com.tmobile.pacman.api.asset.domain;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * The Class ResourceResponse.
 */
public class ResourceResponse {

    @SerializedName("hits")
    @Expose
    private Hits hits;

    /**
     * Gets the hits.
     *
     * @return the hits
     */
    public Hits getHits() {
        return hits;
    }

    /**
     * Sets the hits.
     *
     * @param hits the new hits
     */
    public void setHits(Hits hits) {
        this.hits = hits;
    }

    /**
     * The Class Hits.
     */
    public class Hits {

        /** The total. */
        @SerializedName("total")
        @Expose
        private Integer total;

        /** The hits. */
        @SerializedName("hits")
        @Expose
        private List<Hit> hits = null;

        /**
         * Gets the total.
         *
         * @return the total
         */
        public Integer getTotal() {
            return total;
        }

        /**
         * Sets the total.
         *
         * @param total the new total
         */
        public void setTotal(Integer total) {
            this.total = total;
        }

        /**
         * Gets the hits.
         *
         * @return the hits
         */
        public List<Hit> getHits() {
            return hits;
        }

        /**
         * Sets the hits.
         *
         * @param hits the new hits
         */
        public void setHits(List<Hit> hits) {
            this.hits = hits;
        }
    }

    /**
     * The Class SessionContext.
     */
    public class SessionContext {

        /** The attributes. */
        @SerializedName("attributes")
        @Expose
        private Attributes attributes;

        /**
         * Gets the attributes.
         *
         * @return the attributes
         */
        public Attributes getAttributes() {
            return attributes;
        }

        /**
         * Sets the attributes.
         *
         * @param attributes the new attributes
         */
        public void setAttributes(Attributes attributes) {
            this.attributes = attributes;
        }
    }

    /**
     * The Class Source.
     */
    public class Source {

        /** The detail. */
        @SerializedName("detail")
        @Expose
        private Detail detail;

        /** The time. */
        @SerializedName("time")
        @Expose
        private String time;

        /**
         * Gets the detail.
         *
         * @return the detail
         */
        public Detail getDetail() {
            return detail;
        }

        /**
         * Sets the detail.
         *
         * @param detail the new detail
         */
        public void setDetail(Detail detail) {
            this.detail = detail;
        }

        /**
         * Gets the time.
         *
         * @return the time
         */
        public String getTime() {
            return time;
        }

        /**
         * Sets the time.
         *
         * @param time the new time
         */
        public void setTime(String time) {
            this.time = time;
        }
    }

    /**
     * The Class UserIdentity.
     */
    public class UserIdentity {

        /** The session context. */
        @SerializedName("sessionContext")
        @Expose
        private SessionContext sessionContext;

        /**
         * Gets the session context.
         *
         * @return the session context
         */
        public SessionContext getSessionContext() {
            return sessionContext;
        }

        /**
         * Sets the session context.
         *
         * @param sessionContext the new session context
         */
        public void setSessionContext(SessionContext sessionContext) {
            this.sessionContext = sessionContext;
        }
    }

    /**
     * The Class Attributes.
     */
    public class Attributes {

        /** The creation date. */
        @SerializedName("creationDate")
        @Expose
        private String creationDate;

        /**
         * Gets the creation date.
         *
         * @return the creation date
         */
        public String getCreationDate() {
            return creationDate;
        }

        /**
         * Sets the creation date.
         *
         * @param creationDate the new creation date
         */
        public void setCreationDate(String creationDate) {
            this.creationDate = creationDate;
        }
    }

    /**
     * The Class Detail.
     */
    public class Detail {

        /** The event time. */
        @SerializedName("eventTime")
        @Expose
        private String eventTime;

        /** The user identity. */
        @SerializedName("userIdentity")
        @Expose
        private UserIdentity userIdentity;

        /**
         * Gets the user identity.
         *
         * @return the user identity
         */
        public UserIdentity getUserIdentity() {
            return userIdentity;
        }

        /**
         * Sets the user identity.
         *
         * @param userIdentity the new user identity
         */
        public void setUserIdentity(UserIdentity userIdentity) {
            this.userIdentity = userIdentity;
        }

        /**
         * Gets the event time.
         *
         * @return the event time
         */
        public String getEventTime() {
            return eventTime;
        }

        /**
         * Sets the event time.
         *
         * @param eventTime the new event time
         */
        public void setEventTime(String eventTime) {
            this.eventTime = eventTime;
        }
    }

    /**
     * The Class Hit.
     */
    public class Hit {

        /** The source. */
        @SerializedName("_source")
        @Expose
        private Source source;

        /**
         * Gets the source.
         *
         * @return the source
         */
        public Source getSource() {
            return source;
        }

        /**
         * Sets the source.
         *
         * @param source the new source
         */
        public void setSource(Source source) {
            this.source = source;
        }
    }
}
