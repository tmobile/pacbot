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

/**
 * Created by sauravdutta on 27/11/17.
 */

import { Injectable, Inject } from '@angular/core';
import { Observable } from 'rxjs/Rx';
import 'rxjs/add/operator/toPromise';
import { HttpService } from '../../shared/services/http-response.service';
import { ErrorHandlingService } from '../../shared/services/error-handling.service';
import { UtilsService } from '../../shared/services/utils.service';

@Injectable()
export class AllPatchingProgressService {

    dataArray: any = {};
    monthQuarter: any;
    dayQuarter: any;
    weekVal: any= [];
    weekNum: any= [];
    dataArrayList: any= [];
    lastDate: any;
    year: any;
    monthValue: any;
    endDate: any;
    startDate: any;
    firstDate: any;

    getMethod: any;

    constructor( @Inject(HttpService) private httpService: HttpService,
                private errorHandling: ErrorHandlingService,
                private utilService: UtilsService) { }

    getData(data, url, method, assetGroup, year?: any): Observable<any> {
        try {
            if (method !== 'GET') {
              this.year = year !== undefined ? year : data[0].year;
              data.forEach( yearlyData => {
                  if (this.year === yearlyData.year) {
                      data = yearlyData;
                  }
              });
              const patchingProgressUrl = url;
              const patchingProgressMethod = method;
              const payload = {};
              let currentPayload;
              const queryParams = {
                'ag': assetGroup,
                'filter': {},
                'year': data.year
              };
              const quarterArray = data.quarters;
              this.getMethod = patchingProgressMethod;
              const allObservables: Observable<any>[] = [];
              quarterArray.forEach((quarters) => {
                  currentPayload = Object.assign({}, queryParams);
                  currentPayload['quarter'] = quarters;
                  allObservables.push(
                      this.httpService.getHttpResponse(patchingProgressUrl, patchingProgressMethod, currentPayload, {})
                          .map(response => this.massageDataYearly(quarters, response))
                          .catch(error => this.handleCombiningError(quarters, error))
                  );
              });
              return allObservables.length > 0 ? Observable.combineLatest(allObservables) : Observable.of([]);
            } else {
              this.year = 2018;
              try {
                  return this.httpService.getHttpResponse(url, method, {}, {})
                          .map(response => {
                              return [this.massageDataYearly(1, response)];
                          })
                          .catch(error => this.handleCombiningError(1, error));
              } catch (error) {
                  this.errorHandling.handleJavascriptError(error);
              }
            }
        } catch (error) {
            this.errorHandling.handleJavascriptError(error);
        }
    }

    getQuarterData(payload, url, method, queryParams): Observable<any> {
        try {
            return this.httpService.getHttpResponse(url, method, payload, queryParams)
                    .map(response => {
                        return this.massageQuarterData(response);
                    });
        } catch (error) {
            this.errorHandling.handleJavascriptError(error);
        }
    }

    massageQuarterData(data): any {
        // Sort the data in decreasing order
        data = data.data.response;
        data.sort(function(a, b) {
           return b.year - a.year; // For descending order
        });

        data.forEach(yearData => {
            yearData.quarters.sort();
        });
        return data;
    }

    massageDataYearly(quarters, data): any {
      if (quarters === 'null') {
        return data;
      } else {
        const tempData = JSON.parse(JSON.stringify(data));
        const response = tempData.data.response;

        const numberOfWeeks = this.utilService.getNumberOfWeeks(response.year, quarters);

        const length = response.patching_progress.length;
        const newLength = response.patching_progress[length - 1].patching_info.length;
        const additionalWeeks = [];
        let objPatched;
        let objTotal;
        const valuesPatched = [];
        const valuesTotal = [];
        const valuesProjection = [];
        let amiavail_dateQuarter;
        let end_dateQuarter;
        let internal_targetQuarter;

          // Extracting the 3 important dates......

          if (response.amiavail_date !== undefined) {
            amiavail_dateQuarter = response.amiavail_date;
          }
          if (response.end_date !== undefined) {
            end_dateQuarter = response.end_date;
          }
          if (response.internal_target !== undefined) {
             internal_targetQuarter = response.internal_target;
          }
          const complianceData = response.patching_progress[length - 1].compliance;
          const patchedData = response.patched_instances;
          const unPatchedData = response.unpatched_instances;
          const tomorrow = new Date();
          /* If last date of a quarter is less than the actual end date of the quarter,
          then fill up the extra dates with -1 */

          if (end_dateQuarter !== response.patching_progress[length - 1].patching_info[newLength - 1].date) {
              this.lastDate = response.patching_progress[length - 1].patching_info[newLength - 1].date;
              if (response.patching_progress[length - 1].patching_info.length < 7) {
                const lastDay = new Date(response.patching_progress[length - 1].patching_info[newLength - 1].date);
                for (let s = 1; s <= (7 - response.patching_progress[length - 1].patching_info.length); s++) {
                  const extraDays = tomorrow.setTime(lastDay.getTime() + (24 * 60 * 60 * 1000 * s));
                  const DaysValue = new Date(extraDays);
                  this.monthQuarter = DaysValue.getMonth();
                  this.monthQuarter++;
                  if (this.monthQuarter < 10) {
                    this.monthQuarter = '0' + this.monthQuarter;
                  }
                  this.dayQuarter = DaysValue.getDate();
                  if (this.dayQuarter < 10) {
                    this.dayQuarter = '0' + this.dayQuarter;
                  }
                  const year = DaysValue.getFullYear().toString();
                  const returnDate =  year + '-' + this.monthQuarter + '-' + this.dayQuarter;
                  const additionalDays = {
                    'date': returnDate,
                    'patched_instances': -1,
                    'total_instances': -1,
                    'target_date': -1
                  };
                  additionalWeeks.push(additionalDays);
                }
                response.patching_progress[length - 1].patching_info.push.apply(response.patching_progress[length - 1].patching_info, additionalWeeks);
              }
           }

          /* If the number of weeks in a quarter is less than 13,
          then fill up the rest of the weeks with -1 */
          if (length < numberOfWeeks) {
            let restDays;
            const countValue = 0;
            let countWeek = 0;
            const weekCount = 0;
            let restObj = {};
            let restArray = [];
            const remainingWeeks = numberOfWeeks - length;
            for (let q = 0; q < remainingWeeks; q++) {
              const value = new Date(response.patching_progress[length - 1].end_date);
              const utcTime = value.getTime() + (value.getTimezoneOffset() * 60 * 1000);
              const date2Value = new Date(utcTime);
              countWeek = date2Value.getTime() + (24 * 60 * 60 * 1000 * q * 7);
              const dateValue = tomorrow.setTime(countWeek);
              this.endDate = new Date(dateValue);
              this.monthValue = this.endDate.getMonth();
              for (let t = 1; t <= 7; t++) {
                const nextDay = tomorrow.setTime(this.endDate.getTime() + (24 * 60 * 60 * 1000 * t));
                restDays = {
                  'patched_instances': -1,
                  'total_instances': -1,
                  'target_date': -1,
                  'date': new Date(nextDay)
                };
                restArray.push(restDays);
              }

              restObj = {
                'week': length + q + 1,
                'compliance': '',
                'patching_info': restArray
              };

              restArray = [];
              response.patching_progress.push(restObj);
            }
            let lastWeekResponse = response.patching_progress[12].patching_info;
            let indexValue = 0;
            for (let i = 0; i < lastWeekResponse.length; i++) {
              if (new Date(lastWeekResponse[i].date).getMonth() === new Date(response.end_date).getMonth()) {
                if (new Date(lastWeekResponse[i].date).getDate() === new Date(response.end_date).getDate()) {
                  indexValue = i;
                }
              }
            }
            lastWeekResponse = lastWeekResponse.splice(0, indexValue + 1);
            response.patching_progress[12].patching_info = lastWeekResponse;
          }

         // Making the Array of dates.....

         this.weekVal = [];
         this.weekNum = [];
         this.startDate = response.patching_progress[0].start_date;
         for (let i = 0; i < response.patching_progress.length; i++) {
          this.weekVal.push(response.patching_progress[i].compliance);
          this.weekNum.push(response.patching_progress[i].week);
          for (let j = 0; j < response.patching_progress[i].patching_info.length; j++) {
              objPatched = {
                  'date': new Date(response.patching_progress[i].patching_info[j].date),
                  'value': response.patching_progress[i].patching_info[j].patched_instances
              };
              valuesPatched.push(objPatched);

              objTotal = {
                  'date': new Date(response.patching_progress[i].patching_info[j].date),
                  'value': response.patching_progress[i].patching_info[j].total_instances
              };
              valuesTotal.push(objTotal);

          }
        }

        // Making the array of patching_info/projection count till the given weeks
        const sortedWeeks = response.projection_info;

        sortedWeeks.map(element => {
          element.week = Number(element.week);
        });

        sortedWeeks.sort(function(a, b) {
          return (a.week) - (b.week);
        });

        let objProjection = {};
        const tomorrowDate = new Date();
        this.firstDate = new Date(this.startDate);
        if (response.projection_info !== undefined) {
          if (response.projection_info.length > 0) {
            for (let i = 0; i < response.projection_info.length; i++) {
              const firstDateValue = new Date(tomorrowDate.setTime(this.firstDate.getTime() + 86400000 * (7 * i)));
              for (let j = 0; j < 7; j++) {
                objProjection = {
                  'date': new Date(tomorrowDate.setTime(firstDateValue.getTime() + 86400000 * j)),
                  'value': response.projection_info[i].count
                };
                valuesProjection.push(objProjection);
              }

            }
          }
        }

        valuesProjection.splice(-1, 1);

        let temp = {};
        temp = {
          'ami': amiavail_dateQuarter,
          'int': internal_targetQuarter,
          'end': end_dateQuarter,
          'weekVal': this.weekVal,
          'weekNum': this.weekNum,
          'key': quarters,
          'lastDate': this.lastDate,
          'year': this.year,
          'compliance': complianceData,
          'patched': patchedData,
          'unpatched': unPatchedData,
          'id': 'q0',
          'data':
            [{
                'values': valuesPatched
             }, {
                'values': valuesTotal
             }, {
                'values': valuesProjection
             }
            ]
        };

        if (valuesProjection.length > 0) {
          temp[`projectedTarget`] = 'valid';
        } else {
          temp[`projectedTarget`] = 'invalid';
        }
        this.dataArrayList = [];
        this.dataArrayList.push(temp);

        return temp;
      }
    }


    massageData(data): any {
        if (this.getMethod === 'POST') {
            const response = data;
            this.dataArray = response;
            return this.dataArray;
        } else {
            let dataArray = [];
            const valuesTotal = [];
            const valuesPatched = [];
            let objPatched;
            let objTotal;
            for (let i = 0; i < data.patchingprogress.length; i++) {
                objPatched = {
                    'date': new Date(data.patchingprogress[i].date),
                    'value': data.patchingprogress[i].patched
                };
                valuesPatched.push(objPatched);
                objTotal = {
                    'date': new Date(data.patchingprogress[i].date),
                    'value': data.patchingprogress[i].total
                };
                valuesTotal.push(objTotal);
            }
            dataArray = [{
                'values': valuesPatched
            }, {
                'values': valuesTotal
            }];
            return dataArray;
        }
    }

    handleCombiningError(quarters, error: any): Observable<any> {
        if (quarters === 'null') {
          return Observable.of(this.massageDataYearly('null', []));
        } else {
          return Observable.of(this.massageDataYearly(quarters, []));
        }
    }

}
