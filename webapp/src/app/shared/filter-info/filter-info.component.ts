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

import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-filter-info',
  templateUrl: './filter-info.component.html',
  styleUrls: ['./filter-info.component.css']
})
export class FilterInfoComponent implements OnInit {

  @Input() tags: any;
  @Input() header: any;
  @Input() avoidTags = false;

  showHeader= false;
  tagsFilter: any= {};
  tagsArray= [];

  constructor() { }

  ngOnInit() {
    this.tagsArray = [];
    const keys = Object.keys(this.tags);
    let filterdTags = [];
    keys.forEach(element => {
      if (element.indexOf('tags') > -1) {
        filterdTags.push(element);
      }
    });

    if (!this.avoidTags) {
      filterdTags = keys;
    }

    for (let i = 0; i < filterdTags.length; i++) {
      const splittedName = filterdTags[i].split('.');
      splittedName[1] = splittedName[1] || filterdTags[i];
      let obj = {};
      obj = {
        'name': splittedName[1],
        'value': this.tags[filterdTags[i]]
      };
      this.tagsArray.push(obj);
    }

    if (this.header !== undefined) {
      this.showHeader = true;
    } else {
      this.showHeader = false;
    }
  }
}
