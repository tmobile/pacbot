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
 * Created by adityaagarwal on 25/10/17.
 */

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { ReplaySubject } from 'rxjs/ReplaySubject';
import { DataCacheService } from './data-cache.service';

@Injectable()

export class AssetGroupObservableService {

    private subject = new ReplaySubject<string>(0);

    private updateTriggerStatus;

    constructor(private dataCacheService: DataCacheService) {
    }

    updateAssetGroup (groupName: string) {
        const previousAssetGroup = this.dataCacheService.getCurrentSelectedAssetGroup();
        const shouldNotUpdate = (previousAssetGroup === groupName && this.updateTriggerStatus) ? true : false;

        // Pass data only when there is valid asset group.
        if (groupName && !shouldNotUpdate) {
            this.dataCacheService.setCurrentSelectedAssetGroup(groupName);
            this.subject.next(groupName);
            this.updateTriggerStatus = true;
        }
    }

    getAssetGroup(): Observable<any> {
        return this.subject.asObservable();
    }

}
