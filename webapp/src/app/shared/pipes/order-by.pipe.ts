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

import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'orderBy' })
export class OrderByPipe implements PipeTransform {
  transform(records: Array<any>, args?: any): any {
    if (!args.childProperty) {
      return records.sort(function(a, b) {
        try {
          if (isNaN(a[args.property]) || isNaN(b[args.property])) {
            if (
              a[args.property].toLowerCase().trim() < b[args.property].toLowerCase().trim()
            ) {
              return -1 * args.direction;
            } else if (
              a[args.property].toLowerCase().trim() > b[args.property].toLowerCase().trim()
            ) {
              return 1 * args.direction;
            } else {
              return 0;
            }
          } else {
            if (a[args.property] < b[args.property]) {
              return -1 * args.direction;
            } else if (a[args.property] > b[args.property]) {
              return 1 * args.direction;
            } else {
              return 0;
            }
          }
        } catch (e) {
          return 0;
        }
      });
    } else if (args.childProperty && args.property) {
      return records.sort(function(a, b) {
        try {
          if (
            isNaN(a[args.property][args.childProperty]) ||
            isNaN(b[args.property][args.childProperty])
          ) {
            if (
              a[args.property][args.childProperty].toLowerCase().trim() <
              b[args.property][args.childProperty].toLowerCase().trim()
            ) {
              return -1 * args.direction;
            } else if (
              a[args.property][args.childProperty].toLowerCase().trim() >
              b[args.property][args.childProperty].toLowerCase().trim()
            ) {
              return 1 * args.direction;
            } else {
              return 0;
            }
          } else {
            if (
              a[args.property][args.childProperty] <
              b[args.property][args.childProperty]
            ) {
              return -1 * args.direction;
            } else if (
              a[args.property][args.childProperty] >
              b[args.property][args.childProperty]
            ) {
              return 1 * args.direction;
            } else {
              return 0;
            }
          }
        } catch (error) {
          return 0;
        }
      });
    } else {
      return records.sort(function(a, b) {
        return 0;
      });
    }
  }
}
