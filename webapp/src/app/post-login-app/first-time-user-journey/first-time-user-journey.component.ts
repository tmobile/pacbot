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

import { Component } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-first-time-user-journey',
  templateUrl: './first-time-user-journey.component.html',
  styleUrls: ['./first-time-user-journey.component.css']
})
export class FirstTimeUserJourneyComponent {

  constructor(private authService: AuthService) { }

  images: any = [
    '../assets/images/know-your-dashboard/know-your-dashboard-1.png',
    '../assets/images/know-your-dashboard/know-your-dashboard-2.png',
    '../assets/images/know-your-dashboard/know-your-dashboard-3.png',
    '../assets/images/know-your-dashboard/know-your-dashboard-4.png',
    '../assets/images/know-your-dashboard/know-your-dashboard-5.png',
    '../assets/images/know-your-dashboard/know-your-dashboard-6.png'];

  selectedSlide = 0;

  closeAssetGroup (assetGroupName) {
     this.authService.redirectPostLogin(assetGroupName);
  }

}
