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
package com.tmobile.pacman.api.compliance.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.compliance.repository.FAQRepository;

/**
 * The Class FAQServiceImpl.
 */
@Service
public class FAQServiceImpl implements FAQService, Constants {

    /** The repository. */
    @Autowired
    private FAQRepository repository;

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.FAQService#getFAQSByWidget(java.lang.String, java.lang.String)
     */
    public Map<String, Object> getFAQSByWidget(String widgetId, String domainId)
            throws ServiceException {
        List<Map<String, Object>> faqs = new ArrayList<>();

        List<Map<String, Object>> faqByWidgetIdList;
        List<Map<String, Object>> releventfaqsByWidgetIdList = new ArrayList<>();
        try{
        faqByWidgetIdList= repository.getFAQSFromEs(
                widgetId, domainId);

        }catch(DataException e){
            throw new ServiceException(e);
        }
        List<String> faqId = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        faqByWidgetIdList.parallelStream().forEach(faqDetailsMap -> {

            synchronized (faqId) {
                faqId.add('"' + faqDetailsMap.get("faqid").toString() + '"');
            }
            synchronized (tags) {
                tags.add('"' + faqDetailsMap.get("tag").toString() + '"');
            }

            Map<String, Object> faqDetails = new HashMap<>();
            faqDetails.put("faqName", faqDetailsMap.get("faqname"));
            faqDetails.put("faqAnswer", faqDetailsMap.get("answer"));
            synchronized (faqs) {
                faqs.add(faqDetails);
            }

        });
      try{  releventfaqsByWidgetIdList = repository.getRelevantFAQSFromEs(widgetId,
                domainId, tags, faqId, releventfaqsByWidgetIdList);
        }catch(DataException e){
            throw new ServiceException(e);
        }
        Map<String, Object> respoMap = new HashMap<>();
        respoMap.put("faq", faqs);
        respoMap.put("releventFaq", releventfaqsByWidgetIdList);
        if(respoMap.isEmpty()){
            throw new ServiceException(NO_DATA_FOUND);
        }
        return respoMap;
    }
}
