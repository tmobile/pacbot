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
 * Created by Puneet Baser on 20/11/17.
 */

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';

@Injectable()

export class MainRoutingAnimationEventService {
    private subject = new Subject<any>();

    updateAnimationStatus(animationStatus: boolean) {
        this.subject.next(animationStatus);
    }

    getAnimationStatus(): Observable<any> {
        return this.subject.asObservable();
    }
}
