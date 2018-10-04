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
 * Created by sauravdutta on 10/18/17.
 */

import { Observable } from 'rxjs/Rx';
import { Injectable, Inject } from '@angular/core';
import 'rxjs/add/operator/toPromise';
import { HttpService } from '../../shared/services/http-response.service';
import { ErrorHandlingService } from '../../shared/services/error-handling.service';
import {DataCacheService} from './data-cache.service';
import {environment} from './../../../environments/environment';
import {AssetGroupObservableService} from './asset-group-observable.service';
import {LoggerService} from '../../shared/services/logger.service';
import { DomainTypeObservableService } from './domain-type-observable.service';

@Injectable()
export class AssetTilesService {

    constructor(@Inject(HttpService) private httpService: HttpService,
                private errorHandlingService: ErrorHandlingService,
                private dataStore: DataCacheService,
                private assetGroupObservableService: AssetGroupObservableService,
                private logger: LoggerService,
                private domainTypeObservableService: DomainTypeObservableService) { }

    getAssetdetailTiles(queryParams, assetDetailUrl, assetDetailMethod): Observable<any> {
        const url = assetDetailUrl;
        const method = assetDetailMethod;
        const payload = {};

        try {
          return Observable.combineLatest(
            this.httpService.getHttpResponse(url, method, payload, queryParams)
            .map(response => this.massageData(response) )
          );
        } catch (error) {
            this.errorHandlingService.handleJavascriptError(error);
        }
    }

    getAssetTiles(assetUrl, assetMethod): Observable<any> {

        const url = assetUrl;
        const method = assetMethod;
        const payload = {};
        try {
          return Observable.combineLatest(
            this.httpService.getHttpResponse(url, method, payload)
            .map(response => this.massageData(response) )
          );
        } catch (error) {
            this.errorHandlingService.handleJavascriptError(error);
        }
    }

    updateDefaultAssetGroupForUser(url, method, groupName, userId): Observable<any> {
        try {
            const payload = {
                'defaultAssetGroup': groupName,
                'userId': userId
            };
            return this.httpService.getHttpResponse(url, method, payload)
                   .map(response => response);
        } catch (error) {
            this.errorHandlingService.handleJavascriptError(error);
        }
    }

    massageData(data): any {
        return data;
    }

    getAssetGroupDisplayName(assetGroupName) {
        try {
            return new Observable(observer => {
                try {
                    const assetGroupListSessionStored = this.dataStore.getListOfAssetGroups();
                    if (!assetGroupListSessionStored) {
                        this.getAssetGroupList().subscribe(response => {
                            const assetGroupObject = this.filterAssetGroupObjectFromList(assetGroupName, response);
                            const assetGroupDisplayName = assetGroupObject ? assetGroupObject.displayname : assetGroupName;
                            observer.next(assetGroupDisplayName);
                            observer.complete();
                        });
                    } else {
                        /* If list is availbe then get asset group display name, domain list from a matchign asset group name */
                        const assetGroupList = JSON.parse(assetGroupListSessionStored);
                        const assetGroupObject = this.filterAssetGroupObjectFromList(assetGroupName, assetGroupList);
                        const assetGroupDisplayName = assetGroupObject ? assetGroupObject.displayname : assetGroupName;
                        observer.next(assetGroupDisplayName);
                        observer.complete();
                    }
                } catch (error) {
                    this.logger.log('error', 'js error - ' + error);
                }
            });
        } catch (error) {
            this.logger.log('error', 'js error - ' + error);
        }
    }

    filterAssetGroupObjectFromList(assetGroupName, assetGroupList) {
        try {
            let isAgPresent = false;
            let assetGroupObject;
            const filteredArray = assetGroupList.filter(element => element.name === assetGroupName);

            if (filteredArray.length) {
                isAgPresent = true;
                assetGroupObject = filteredArray[0];
            }

            return assetGroupObject;
        } catch (error) {
            this.logger.log('error', 'js error - ' + error);
        }
    }

    getAssetGroupList() {
        try {
            return new Observable(observer => {
                const assetUrl = environment.assetTiles.url;
                const assetMethod = environment.assetTiles.method;

                this.getAssetTiles(assetUrl, assetMethod).subscribe(
                    response => {
                        const assetGroupList = response[0];
                        if (assetGroupList) {
                            /* Store the list in stringify format */
                            this.dataStore.setListOfAssetGroups(JSON.stringify(assetGroupList));
                            observer.next(assetGroupList);
                            observer.complete();
                        }
                    },
                    error => {
                        observer.next([]);
                        observer.complete();
                        this.logger.log('error', error);
                    }
                );
            });
        } catch (error) {
            this.logger.log('error', 'js error - ' + error);
        }
    }

    getListOfDomainsAssociatedWithAssetGroup(assetGroupName) {
        try {
            return new Observable(observer => {
                let listOfDomains = this.dataStore.getCurrentSelectedDomainList() ? this.dataStore.getCurrentSelectedDomainList() : '';
                if (listOfDomains) {
                    observer.next(listOfDomains);
                    observer.complete();
                } else {
                    this.getAssetGroupList().subscribe(response => {
                        const assetGroupObject = this.filterAssetGroupObjectFromList(assetGroupName, response);
                        listOfDomains = assetGroupObject.domains ? assetGroupObject.domains : [];
                        this.domainTypeObservableService.updateListOfDomains(listOfDomains.join('~'));
                        observer.next(listOfDomains.join('~'));
                        observer.complete();
                    });
                }
            });
        } catch (error) {
            this.logger.log('error', 'js error - ' + error);
        }
    }
}
