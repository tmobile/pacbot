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

import { Injectable } from '@angular/core';
import * as _ from 'lodash';
import * as moment from 'moment';
import { QUARTER } from './../constants/quarter';
import { LoggerService } from './logger.service';
import { RefactorFieldsService } from './refactor-fields.service';

@Injectable()
export class UtilsService {
  constructor(private logger: LoggerService,
              private refactorFieldsService: RefactorFieldsService) {}

  setTimeoutPromise(milliseconds) {
    const promise = new Promise((resolve: any, reject: any) => {
      setTimeout(() => {
        resolve(resolve, reject);
      }, milliseconds);
    });
    return promise;
  }

  isObjectEmpty = obj => {
    return Object.keys(obj).length === 0 && obj.constructor === Object;
  }

  debounce(func, wait, immediate) {
    let timeout;
    return function() {
      const context = this,
        args = arguments;
        const later = function() {
        timeout = null;
        if (!immediate) { func.apply(context, args); }
      };
      const callNow = immediate && !timeout;
      clearTimeout(timeout);
      timeout = setTimeout(later, wait);
      if (callNow) { func.apply(context, args); }
    };
  }

  isObject(val) {
    if (val === null) {
      return false;
    }
    return typeof val === 'function' || typeof val === 'object';
  }

  clickClearDropdown() {
    setTimeout(function() {
      const clear = document.getElementsByClassName(
        'btn btn-xs btn-link pull-right'
      );
      for (let len = 0; len < clear.length; len++) {
        const element: HTMLElement = clear[len] as HTMLElement;
        element.click();
      }
    }, 10);
  }

  objectToArray(object, keyLabel = 'key', valueLabel = 'value') {
    return _.map(object, (element, key, array) => {
      const arrayElement: any = {};
      arrayElement[keyLabel] = key;
      arrayElement[valueLabel] = element;
      return arrayElement;
    });
  }

  arrayToCommaSeparatedString(arr) {
    let str = '';
    for (let i = 0; i < arr.length; i++) {
      i === arr.length - 1 ? str = str + arr[i] : str = str + arr[i] + ',';
    }
    return str;
  }

  massageTableData(data) {
    /*
       * added by Trinanjan 14/02/2017
       * the funciton replaces keys of the table header data to a readable format
     */
    const refactoredService = this.refactorFieldsService;
    const newData = [];
    data.map(function(responseData) {
      const KeysTobeChanged = Object.keys(responseData);
      let newObj = {};
      KeysTobeChanged.forEach(element => {
        const elementnew =
          refactoredService.getDisplayNameForAKey(
            element.toLocaleLowerCase()
          ) || element;
        newObj = Object.assign(newObj, { [elementnew]: responseData[element] });
      });
      newData.push(newObj);
    });
    return newData;
  }

  addOrReplaceElement(array, toAddElement, comparator) {
    const i = _.findIndex(array, (element, index, _array) => {
      return comparator(element, index, _array);
    });

    if (i >= 0) {
      array.splice(i, 1, toAddElement);
    } else {
      array.push(toAddElement);
    }
  }

  getParamsFromUrlSnippet(urlSnippet) {
    const split = urlSnippet.split('?');
    const url = split[0];
    const inputParams = split[1].split('&');
    const params = {};
    _.each(inputParams, arg => {
      const key = arg.substring(0, arg.indexOf('='));
      const value = arg.substring(arg.indexOf('=') + 1, arg.length);
      params[key] = value;
    });

    return {
      url: url,
      params: params
    };
  }

  arrayToObject(array, keyLabel = 'key', valueLabel = 'value') {
    const object = {};
    _.each(array, (element, index, list) => {
      object[element[keyLabel]] = element[valueLabel];
    });
    return object;
  }

  /**
   * Funciton added by trinanjan on 30.01.2018
   * This function process the queryparams from router snapshot and
   * passes the required formate obj for filter parameter
   */

  processFilterObj(data) {
    let object = {};

    if (data.filter !== '' && data.filter !== undefined  ) {
      const eachFilterObj = data.filter.split('*');
      _.each(eachFilterObj, (element, index) => {
        const eachFilterParam = element.split('=');
        const key = eachFilterParam[0];
        const value = eachFilterParam[1];
        object[key] = value;
      });
    } else {
      object = {};
    }
    return object;
  }

  /**
   * Funciton added by trinanjan on 31.01.2018
   * This function process filter parameter to be passes to required format
   * Example Input --> {'tagged':'true','targetType':'ec2'}
   * Example Output --> {'filter': 'tagged=true*targetType=ec2'}
   */

  makeFilterObj(data) {
    try {
      let object = {};
      const localArray = [];
      if (Object.keys(data).length === 0 && data.constructor === Object) {
        return object;
      } else {
        const localObjKeys = Object.keys(data);
        _.each(localObjKeys, (element, index) => {
          if (typeof data[element] !== 'undefined') {
            const localValue = data[element].toString();
            const localKeys = element.toString();
            const localObj = localKeys + '=' + localValue;
            localArray.push(localObj);
          }
        });
        object = { filter: localArray.join('*') };
        return object;
      }
    } catch (error) {
      this.logger.log('error', 'js error - ' + error);
    }
  }

  calculateDate(_JSDate) {
        if (!_JSDate) {
            return 'No Data';
        }
        const date = new Date(_JSDate);
        const year = date.getFullYear().toString();
        const month = date.getMonth() + 1;
        let monthString;
        if (month < 10) {
            monthString = '0' + month.toString();
        } else {
            monthString = month.toString();
        }
        const day = date.getDate();
        let dayString;
        if (day < 10) {
            dayString = '0' + day.toString();
        } else {
            dayString = day.toString();
        }
        return monthString + '-' + dayString + '-' + year ;
    }

    calculateDateAndTime(_JSDate) {

      const monthsList = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sept', 'Oct', 'Nov', 'Dec'];
      const date = new Date(_JSDate);
      const year = date.getFullYear();
      const month = date.getMonth();
      const day = date.getDate();
      const monthValue = monthsList[month];
      let hours = date.getHours();
      const minutes = date.getMinutes();
      const seconds = date.getSeconds();
      const ampm = (hours >= 12) ? 'PM' : 'AM';
        if (ampm === 'PM') {
          hours = hours - 12;
        }
        return monthValue + ' ' + day + ',' + ' ' + year + ' ' + hours + ':' + minutes + ':' + seconds + ' ' + ampm;
    }

    getNumberOfWeeks = function(year, quarter) {
      const currentQuarter =  QUARTER.quarterObj[quarter];
      const fromDate = moment(year + '-' + currentQuarter.fromMonth + '-' + currentQuarter.fromDay);
      let weeks = 14;
      if (+quarter === 1 ) {
        // if year is leap year and first quarter starts with Sunday.
        if (moment([year]).isLeapYear() && fromDate.weekday() === 0) {
          weeks = 13;
        // if first quarter starts with Sunday or Monday.
        } else if (fromDate.weekday() === 0 || fromDate.weekday() === 1) {
          weeks = 13;
        }
      // if second quarter starts with Sunday.
      } else if (+quarter === 2 && fromDate.weekday() === 0) {
        weeks = 13;
      }

      return weeks;
    };

    checkIfAPIReturnedDataIsEmpty(data) {
    // There can be multiple scenarios:
    /*
     - data can be an empty object
     - data can be an empty array
     - data can be undefined
     */

    let isEmpty = false;

        if (data) {
            if (Array.isArray(data) && data.length === 0) {
                isEmpty = true;
            } else if (Object.keys(data).length === 0 && data.constructor === Object) {
                isEmpty = true;
            }
        } else {
            isEmpty = true;
        }
        return isEmpty;
    }

    strToBool(str) {
        // will match one and only one of the string 'true','1', or 'on' rerardless
        // of capitalization and regardless off surrounding white-space.
        //
        const regex = /^\s*(true|1|on)\s*$/i;

        return regex.test(str);
    }

    extractNumbersFromString(str) {
      const numb = str.match(/\d/g);
      const number = numb.join('');

      return number;
    }

    capitalizeFirstLetter(string): any {
      return string.charAt(0).toUpperCase() + string.slice(1);
    }

    findValueInArray(array, valueToBeFound) {

      return array.findIndex(eachValue => {
        return (eachValue && eachValue.toLowerCase()) === (valueToBeFound && valueToBeFound.toLowerCase());
      });
    }

    getContextUrlExceptDomain(url) {
      const parser = this.parseUrl(url);

      const pathname = parser.pathname || '';
      const query = parser.search || '';
      const fragments = parser.hash || '';

      return pathname + query + fragments;
    }

    parseUrl(url) {

      let parser;

      if (url && url !== '') {
        parser = document.createElement('a');
        parser.href = url;

        /*
        parser.protocol; // => "http:"
        parser.hostname; // => "example.com"
        parser.port;     // => "3000"
        parser.pathname; // => "/pathname/"
        parser.search;   // => "?search=test"
        parser.hash;     // => "#hash"
        parser.host;     // => "example.com:3000"
        */
      }

      return parser;

    }

    getDateAndTime(dateWithoutTime, assumeUTCEndOfDay = true) {
      const date = new Date(dateWithoutTime);

      const hours = assumeUTCEndOfDay ? 23 : 0;
      const minutes = assumeUTCEndOfDay ? 59 : 0;
      const seconds = 0;

      const date_utc = this.convertDateToUTCEndOfDay(date, hours, minutes, seconds);

      const date_currentTimeZone = new Date(date_utc);

      return date_currentTimeZone;
    }

    convertDateToUTCEndOfDay(date, hours = 23, minutes = 59, seconds = 0) {
      return Date.UTC(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate(), hours, minutes, seconds);
    }

    getUTCDate(date) {
      // get UTC Date in YYYY-MM-DD format
      const utcDate = moment.utc(date).format('YYYY-MM-DD');
      return utcDate;
  }

}
