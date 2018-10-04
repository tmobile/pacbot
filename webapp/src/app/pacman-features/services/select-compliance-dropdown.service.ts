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
 * Created by adityaagarwal on 10/11/17.
 */

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { ReplaySubject } from 'rxjs/ReplaySubject';

@Injectable()

export class SelectComplianceDropdown {
    private subject = new ReplaySubject<any>(0);

    updateCompliance(dropDownObj: any) {
        if (dropDownObj) {
            this.subject.next(dropDownObj);
        }
    }
    getCompliance(): Observable<any> {
        return this.subject.asObservable();
    }
}
