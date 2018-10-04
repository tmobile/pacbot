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
 * Created by adityaagarwal on 11/13/17.
 */

import * as _ from 'lodash';
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'orderBySum' })
export class OrderBySumPipe implements PipeTransform {
  transform(records: Array<any>, args?: any): any {
    if (!args) {
      return records;
    }
    const list = _.orderBy(
      records,
      record => {
        const total = _.reduce(
          record[args.propertyKey],
          (sum, countedObject) => {
            return sum + countedObject[args.countKey];
          },
          0
        );
        return total;
      },
      args.direction
    );
    return list;
  }
}
