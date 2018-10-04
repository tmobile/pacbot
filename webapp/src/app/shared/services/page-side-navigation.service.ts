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
 * Created by Mohammed Furqan on 05/12/17.
 */

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

@Injectable()

export class PageSideNavigationService {

    private currLinkNeighbours = new BehaviorSubject<any>(1);

    private stateLinks = {
        'compliance' : {
            'next' : 'assets',
            'prev' : 'omnisearch'
        },
        'assets': {
            'next' : 'tools',
            'prev' : 'compliance'
        },
        'tools': {
            'next' : 'omnisearch',
            'prev' : 'assets'
        },
        'omnisearch': {
            'next' : 'compliance',
            'prev' : 'tools'
        },
        'admin': {
            'next' : 'omnisearch',
            'prev' : 'omnisearch'
        }
    };

    setSideNavigationLinks(pageName: string) {
        // By default current Link details are set for compliance page
        this.currLinkNeighbours.next(this.stateLinks[`${pageName}`]);
    }

    getNeighboursForCurrentPage(): Observable<any>  {
        return this.currLinkNeighbours.asObservable();
    }

    changeSideNavigationState(pageName: string) {
        this.currLinkNeighbours.next(this.stateLinks[`${pageName}`]);
    }

}
