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

import {Component, OnInit, Input} from '@angular/core';
import {horizontalLineTravel, verticalLineTravel} from './../animations/animations';
import {UtilsService} from '../../../shared/services/utils.service';

@Component({
    selector: 'app-glow-traveller',
    templateUrl: './glow-traveller.component.html',
    styleUrls: ['./glow-traveller.component.css'],
    providers: [UtilsService],
    animations: [verticalLineTravel, horizontalLineTravel]
})
export class GlowTravellerComponent implements OnInit {

    constructor(utils: UtilsService) {
    }

    @Input() animationState;
    @Input() classInner: string;
    @Input() orientationVertical: boolean;

    ngOnInit() {
    }

}
