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
 * Created by adityaagarwal on 23/10/17.
 */

import { Injectable } from '@angular/core';
import 'rxjs/add/operator/toPromise';

@Injectable()
export class DashboardRouteService {
    routeTo: any = {
        'Dashboard': '/pl/my-dashboard',
        'Compliance': '/pl/compliance',
        'Assets': '/pl/assets',
        'Tools': '/pl/tools',
        'Omnisearch': '/pl/omnisearch',
        'Admin': '/pl/admin'
        };

    getRoute() {
        return this.routeTo;
    }

    updateRoute(routePage, routeLink): Promise<DashboardRouteService[]> {
        this.routeTo[routePage] = routeLink;
        return;
    }
 }
