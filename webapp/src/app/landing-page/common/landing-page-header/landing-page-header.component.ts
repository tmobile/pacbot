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

import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
import {loginRouterTransition} from '../animations/animations';

@Component({
  selector: 'app-landing-page-header',
  templateUrl: './landing-page-header.component.html',
  styleUrls: ['./landing-page-header.component.css'],
  animations: [loginRouterTransition]

})

export class LandingPageHeaderComponent implements OnInit {

  constructor(
  ) { }

  @Input() item1: any;
  @Input() item2: any;
  @Input() item3: any;
  @Input() item4: any;
  @Input() item5: any;
  @Output() loginEvent = new EventEmitter();

  ngOnInit() {

  }

  goTo(location: string): void {
    window.location.hash = '';
    window.location.hash = location;
  }

  openLoginMenu(event) {
    this.loginEvent.emit(event);
  }

}
