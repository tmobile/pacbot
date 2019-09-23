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
 * @type Service
 * @desc All functions which are used across application related to routing go here
 * @author Puneet Baser
 */


import { Injectable } from '@angular/core';
import {ActivatedRouteSnapshot, Params, Router} from '@angular/router';
import {LoggerService} from './logger.service';
import {COMMON_PAGES} from '../../../config/domain-mapping';


@Injectable()
export class RouterUtilityService {

    constructor(private loggerService: LoggerService) {}

    public getDeepestPageTitle(routeSnapshot: ActivatedRouteSnapshot) {
        let title = routeSnapshot.data ? routeSnapshot.data['title'] : '';
        if (routeSnapshot.firstChild) {
            title = this.getDeepestPageTitle(routeSnapshot.firstChild) || title;
        }
        return title;
    }

    public getDeepestPageUrl(routeSnapshot: ActivatedRouteSnapshot) {
        let url = routeSnapshot.url;
        if (routeSnapshot.firstChild) {
            url = this.getDeepestPageUrl(routeSnapshot.firstChild) || url;
        }
        return url;
    }

    public getJointDeepestPageUrl(routeSnapshot: ActivatedRouteSnapshot) {
        const urlSegment = this.getDeepestPageUrl(routeSnapshot);

        let urlPath = '';
        urlSegment.forEach(function(pathObj){
            urlPath += pathObj.path + '/';
        });
        urlPath = urlPath.slice(0, -1);

        return urlPath;
    }

    public getPageUrlSegmentFromSnapshot(routerSnapshot: ActivatedRouteSnapshot) {
        let urlSegment = routerSnapshot.url;
        if (routerSnapshot.firstChild) {
            urlSegment = urlSegment.concat(this.getPageUrlSegmentFromSnapshot(routerSnapshot.firstChild));
        }

        return urlSegment;
    }

    public getFullUrlFromSnapshopt(routerSnapshopt: ActivatedRouteSnapshot) {
        const url = this.getPageUrlSegmentFromSnapshot(routerSnapshopt);
        let urlPath = '';
        url.forEach(function(pathObj){
            urlPath += pathObj.path + '/';
        });
        urlPath = urlPath.slice(0, -1);

        return urlPath;
    }

    public getQueryParametersFromSnapshot(routerSnapshopt: ActivatedRouteSnapshot) {
        let queryParams = routerSnapshopt.queryParams;
        if (routerSnapshopt.firstChild) {
            queryParams = this.getQueryParametersFromSnapshot(routerSnapshopt.firstChild) || queryParams;
        }
        return queryParams;
    }

    public getQueryParams(routerSnapshot: ActivatedRouteSnapshot) {
        return  routerSnapshot.queryParams;
    }

    public getLandingPageInAModule(moduleName) {

        const landingPageModuleMapping = {
            'compliance': 'pl/compliance/compliance-dashboard',
            'assets': 'pl/assets/asset-dashboard',
            'tools': 'pl/tools/tools-landing',
            'omnisearch': 'pl/omnisearch/omni-search-page',
            'admin': 'pl/admin/policies'
        };

        return landingPageModuleMapping[moduleName];
    }

    public getModuleNameFromCurrentRoute(routerSnapshot) {
        try {
            let url = routerSnapshot.url;
            if (routerSnapshot.firstChild) {
                url = routerSnapshot.firstChild.url;
            }
            return url[0].path;
        } catch (error) {
            this.loggerService.log('error', 'JS error - ' + error);
        }
    }

    public getpageLevel(routerSnapshot: ActivatedRouteSnapshot) {
        const children = routerSnapshot.children;
        let pageLevel = routerSnapshot.data ? routerSnapshot.data['pageLevel'] : '';
        if (children.length > 0) {
            const index = children.length - 1;
            const child = children[index];
            pageLevel = this.getpageLevel(child);
        }
        return pageLevel;
    }

    public getpageTitle(routerSnapshot: ActivatedRouteSnapshot) {
        const children = routerSnapshot.children;
        let title = routerSnapshot.data ? routerSnapshot.data['pageTitle'] || routerSnapshot.data['title'] : '';
        if (children.length > 0) {
            const index = children.length - 1;
            const child = children[index];
            title = this.getpageTitle(child) || title;
        }
        return title;
    }

    public checkIfCurrentRouteBelongsToCommonPages(route) {
        let index = -1;

        for ( let i = 0; i < COMMON_PAGES.length; i++) {
            const eachRoute = COMMON_PAGES[i];
            index = route.indexOf(eachRoute);

            if (index >= 0) { return true; }
        }
        return false;
    }

    public getFullPageUrlSegmentFromSnapshot(routerSnapshot: ActivatedRouteSnapshot) {
        let urlSegmentDetails = [];

        const urlSegment = routerSnapshot.url;
        const outlet = routerSnapshot.outlet;

        urlSegmentDetails.push({
            'urlSegment': urlSegment,
            'outlet': outlet
        });

        const children = routerSnapshot.children;
        if (children) {
            for (let i = 0; i < children.length; i++ ) {
                const child = children[i];
                urlSegmentDetails = urlSegmentDetails.concat(this.getFullPageUrlSegmentFromSnapshot(child));
            }
        }

        return urlSegmentDetails;
    }
}
