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
import {ActivatedRoute, Router} from '@angular/router';


@Component({
  selector: 'app-know-your-dashboard',
  templateUrl: './know-your-dashboard.component.html',
  styleUrls: ['./know-your-dashboard.component.css']
})
export class KnowYourDashboardComponent implements OnInit {

  constructor( private router: Router,
               private activatedRoute: ActivatedRoute ) { }

  @Input() images: any = [
   '../assets/images/know-your-dashboard/know-your-dashboard-1.png',
   '../assets/images/know-your-dashboard/know-your-dashboard-2.png',
   '../assets/images/know-your-dashboard/know-your-dashboard-3.png',
   '../assets/images/know-your-dashboard/know-your-dashboard-4.png',
   '../assets/images/know-your-dashboard/know-your-dashboard-5.png',
   '../assets/images/know-your-dashboard/know-your-dashboard-6.png'];
  @Input() selectedSlide = 0;

  @Output() lastSlideReached = new EventEmitter();

  ngOnInit() {
  }

  checkForLast() {
    if (this.images.length === this.selectedSlide ) {
      this.lastSlideReached.emit();
      this.closeModal();
    }
  }

  closeModal () {
    this.router.navigate(
      [
        // No relative path pagination
        {
          outlets: {
            kydModal: null
          }
        }
      ],
        {
          relativeTo: this.activatedRoute.parent, // <-- Parent activated route,
          queryParamsHandling: 'merge'
        }
    );

  }

}
