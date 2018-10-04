/*
 *Copyright 2018 T Mobile, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not use
 * this file except in compliance with the License. A copy of the License is located at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or
 * implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { browser, by, element, $, $$ } from 'protractor';
const fs = require('fs');

export class DigitalDevDashboard {

  getDigitalDashboardHeaderText() {
    return element(by.xpath('//div/h1[text()=\'Digital Dev\']'));
  }

  getRepositoryDistributionDonutlegend() {
    return $$('.digital-dev-strategy-distribution-wrapper .graph-legend-cont .legend-each');
  }

  getRepositoryDistributionDonutGraph() {
    return $('.digital-dev-strategy-distribution-wrapper svg#donut-chart');
  }

  getPRMetricsCreatedBarChartTextLink() {
    return $$('.pull-request-matrix-donut-chart text.bar.bar-links').first();
  }

  getPRMetricsMergedBarChartTextLink() {
    return $$('.pull-request-matrix-donut-chart .show-links text.bar.bar-links').get(1);
  }

  getPRMetricsDeclinedBarChartTextLink() {
    return $$('.pull-request-matrix-donut-chart .show-links text.bar.bar-links').get(2);
  }

  getPRMetricsOpenBarChartTextLink() {
    return $$('.pull-request-matrix-donut-chart .show-links text.bar.bar-links').get(3);
  }

  getPRAgeBarChartTextLink() {
    return $$('app-dev-standard-pull-request-age .show-links text.bar.bar-links').first();
  }

  getStaleBranchDonutGraph() {
    return $('.donut-container-staleBranchDonut svg#donut-chart');
  }

  getStaleBranchDonutlegend() {
    return $$('.donut-container-staleBranchDonut .graph-legend-cont .legend-each');
  }

  getTotalOpenPR() {
    return $('app-dev-standard-pull-request-age .total-count');
  }

  getPRAgeBarGraph() {
    return $$('app-dev-standard-pull-request-age .show-links text.bar.bar-links');
  }

  getStaleBranchesDonutlegendCount() {
    return $$('.donut-container-staleBranchDonut .legend-each .legend-text-right').get(1);
  }

  getStaleBranchBarGraph() {
    return $$('app-dev-standard-stale-branch-age text.bar.bar-links');
  }

  getFilterArrow() {
    return element(by.css('.drop-options .ui-select-container.dropdown'));
  }

  getFilterType() {
    return element(by.css('.drop-options .ui-select-container.dropdown.open .ui-select-choices-row.active .dropdown-item div'));
  }

  getFilterTags() {
    return element(by.css('.drop-search-box .ui-select-container.dropdown.open .ui-select-choices-row.active .dropdown-item div'));
  }

  getFilterTagInput() {
    return element(by.css('.drop-search-box .ui-select-container.dropdown'));
  }

  getFilterSelected() {
    return element(by.css('.each-filter'));
  }

  getClearAllFilter() {
    return element(by.css('.clear-filter'));
  }

  verifyFilterTagValues() {
    return element(by.css('.drop-search-box .ui-select-container.dropdown .ui-select-match'));
  }

  getQuarterSelector() {
    return $('.quarter-selector');
  }

  getQuarterDisplay() {
    return $('.quarter-desc .pp-time');
  }

  getQuarterViewButton() {
    return $$('.view-btn').first();
  }
    getQuarterCloseButton() {
      return element(by.xpath('//app-compliance/div/div/div[2]/app-digital-dev-dashboard/div/div/div[3]/section[2]/ul/li[1]/div/app-pull-request-line-metrics/div/div[2]/div[3]/ul[2]/li[1]/div'));
    }

  getFirstQuaterTotalCreatedCount() {
    return $$('.outer-quarter-wrap .each-quarter-row').first().$$(' .each-quarter-detail-wrap .each-desc .pp-stats-txt').first();
  }

  getFirstQuaterTotalMergedCount() {
    return $$('.outer-quarter-wrap .each-quarter-row').first().$$(' .each-quarter-detail-wrap .each-desc .pp-stats-txt').get(1);
  }

  getFirstQuaterTotalDeclinedCount() {
    return $$('.outer-quarter-wrap .each-quarter-row').first().$$(' .each-quarter-detail-wrap .each-desc .pp-stats-txt').get(2);
  }

  getFirstQuaterTotalOpenCount() {
    return $$('.outer-quarter-wrap .each-quarter-row').first().$$(' .each-quarter-detail-wrap .each-desc .pp-stats-txt').get(3);
  }

  getTotalWeeksInSelectedQuarter() {
    return $('.pull-request-matrix-donut-chart .selected-week');
  }

  getAllWeeksColumn() {
    return $$('.pull-request-matrix-line-chart .flex z-3 .each-column');
  }

}
