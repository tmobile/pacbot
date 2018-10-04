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

import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { LoggerService } from '../../../shared/services/logger.service';

@Component({
  selector: 'app-aws-app-tile',
  templateUrl: './aws-app-tile.component.html',
  styleUrls: ['./aws-app-tile.component.css'],
  providers: [LoggerService]
})
export class AwsAppTileComponent implements OnInit {

  @Input() awsResourceName = 'AWS';
  @Input() resourceInstances = '3122';
  @Input() active = false;
  @Input() recommendationAvailable = false;
  @Input() awsResource: any;
  @Input() i: number;

  @Output() awsResourceSelected = new EventEmitter<string>();
  imagePathFound = true;

  private imagePath: any;
  colors: any = ['#833E9E', 'red', '#0D77B2', '#CF0D3D', '#4F9D20', '#f09e06', '#ed0295'];

  numberWithCommas(number) {
    return number === undefined ? number : number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
  }

  awsResourseSelected() {
    this.awsResourceSelected.emit(this.awsResourceName);
  }

  constructor( private logger: LoggerService) { }

  ngOnInit() {
    try {
        if (this.awsResource) {
            // Adding commas to numbers depending on the number
            this.resourceInstances = this.numberWithCommas(this.resourceInstances);
            this.imagePath = this.awsResource.iconPath;
        }
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  getRandomColor() {
    return '#3C5079'; // To set background color of the circle to show name when image path is not available
  }

  getInitial(name) {
    let initials = name.replace(' ', '-');
    initials = initials.replace('_', '-');
    const wordSplit = initials.split('-');
    let newinitial = '';
    for (let i = 0; i < wordSplit.length; i += 1) {
        newinitial = newinitial + wordSplit[i].charAt(0);
    }
    return newinitial.substring(0, 3);
  }

  setImagePathNotFound() {
    this.imagePathFound = false;
  }

}
