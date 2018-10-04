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
  Author :santoshi
  Modified Date: Jan 31, 2018

 **/
package com.tmobile.pacman.api.compliance.repository;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.tmobile.pacman.api.commons.exception.DataException;

/**
 * The Interface FAQRepository.
 */
@Repository
public interface FAQRepository {

    /**
     * If method receives widgetId, it gives the
     *         list of map details from the ES.If method
     *         receives widgetId and domainId, it gives the list of map details
     *         from the ES for the given widgetId and domainId.
     *
     * @param widgetId the widget id
     * @param domainId the domain id
     * @return List<Map<String, Object>>
     * @throws DataException the data exception
     */
    public List<Map<String, Object>> getFAQSFromEs(String widgetId,
            String domainId) throws DataException;

    /**
     * Gets the relevant FAQS from es.If method receives
     *         widgetId,domainId,tag,faqId,then it gives the list of map faqName
     *         and faqAnswer.
     *
     * @param widgetId the widget id
     * @param domainId the domain id
     * @param tags the tags
     * @param faqId the faq id
     * @param releventfaqsByWidgetIdList the releventfaqs by widget id list
     * @return List<Map<String, Object>>
     * @throws DataException the data exception
     */
    public List<Map<String, Object>> getRelevantFAQSFromEs(String widgetId,
            String domainId, List<String> tags, List<String> faqId,
            List<Map<String, Object>> releventfaqsByWidgetIdList)
            throws DataException;
}
