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
 * Created by adityaagarwal on 10/23/17.
 */

import { Pipe, PipeTransform, Output, EventEmitter } from '@angular/core';
import { LoggerService } from '../services/logger.service';

@Pipe({ name: 'searchFilter' })
export class SearchFilterPipe implements PipeTransform {
  @Output() pipeError = new EventEmitter();

  constructor(private loggerService: LoggerService) {}

  transform(input: any, searchQuery: any): any {
    try {
      return input.filter(item => {
        for (const key in item) {
          if (
            (
              '' +
              JSON.stringify(item[key])
                .toString()
                .toLowerCase()
            ).includes(searchQuery.toString().toLowerCase())
          ) {
            return true;
          }
        }
        return false;
      });
    } catch (error) {
      this.loggerService.log('infor', 'error in pipe' + error);
      return false;
    }
  }
}
