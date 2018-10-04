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
 * Created by Trinanjan on 13/03/17.
 */

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { ReplaySubject } from 'rxjs/ReplaySubject';
import { DataCacheService } from './data-cache.service';

@Injectable()

export class DomainTypeObservableService {
    private subject = new ReplaySubject<string>(0);
    // listOfDomains
    private domainListSubject = new ReplaySubject<string>(0);
    constructor(private dataCache: DataCacheService) {}
    // This function updates the domain type on selection of asset group
    updateDomainType(domainName: string, assetGroupNameAsSessionStorageKey: string) {
        if (domainName) {
            this.dataCache.setCurrentSelectedDomain(domainName, assetGroupNameAsSessionStorageKey);
            this.subject.next(domainName);
        }
    }

    // This function returns the updated domain type
    getDomainType(): Observable<any> {
        return this.subject.asObservable();
    }

    updateListOfDomains(domainList: string) {
        if (domainList) {
            this.dataCache.setCurrentSelectedDomainList(domainList);
            this.domainListSubject.next(domainList);
        }
    }

    getDomainListForAAssetGroup(): Observable<any> {
        return this.domainListSubject.asObservable();
    }
}
