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

import { Component, OnInit, Input, ViewEncapsulation, Output, EventEmitter } from '@angular/core';
import { ICONS } from '../../../shared/constants/icons-mapping';
import { LoggerService } from '../../../shared/services/logger.service';

@Component({
  selector: 'app-target-type-tagging-tile',
  templateUrl: './target-type-tagging-tile.component.html',
  styleUrls: ['./target-type-tagging-tile.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class TargetTypeTaggingTileComponent implements OnInit {
  @Input() awsResourceDetails: any  = {};

  imagePath: any;
  private untagged: any = '30%';
  private tagged: any = '70%';
  @Output() navigatePage: EventEmitter<any> = new EventEmitter();
  imagePathFound = true;

  constructor(private logger: LoggerService) { }

  ngOnInit() {
    try {

      this.untagged = ((parseInt(this.awsResourceDetails.untagged, 10) / parseInt(this.awsResourceDetails.assetCount , 10)) * 100) + '%';
      this.tagged = ((parseInt(this.awsResourceDetails.tagged, 10) / parseInt(this.awsResourceDetails.assetCount, 10)) * 100) + '%';

      // Check if the icon mapped to the AWS resource type exists
      if (ICONS.awsResources[this.awsResourceDetails.name] !== undefined) {
        this.imagePath = ICONS.awsResources[this.awsResourceDetails.name];
      }
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  instructParentToNavigate(data1, data2) {
    const localObj = {
      'appName' : data1,
      'taggedState' : data2
    };

    this.navigatePage.emit(localObj);
  }

  getRandomColor() {
    return '#3C5079'; // To set background color of the circle to show name when image path is not available
  }

  getInitial(name) {
    let initials = name.replace(' ', '-');
    initials = initials.replace('_', '-');
    const wordSplit = initials.split('-');
    let newinitial = '';
    for ( let i = 0; i < wordSplit.length; i += 1) {
      newinitial = newinitial + wordSplit[i].charAt(0);
    }
    return newinitial.substring(0, 3);
  }

  setImagePathNotFound() {
    this.imagePathFound = false;
  }
}
