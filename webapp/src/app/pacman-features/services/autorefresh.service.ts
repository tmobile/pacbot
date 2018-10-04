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
 * Created by adityaagarwal on 18/10/17.
 */

import { Injectable } from '@angular/core';
import 'rxjs/add/operator/toPromise';

@Injectable()
export class AutorefreshService {

    durationParams: any;
    autoRefresh: boolean;

    getDuration(): Promise<AutorefreshService[]> {
        const absUrl = window.location.href;
        const sURLVariables = absUrl.split('?');
        const urlObj = {};

        if (sURLVariables[1] !== undefined) {
        const valuesParams = sURLVariables[1].split('&');

        for (let i = 0; i < valuesParams.length; i++) {
            const paramsName = valuesParams[i].split('=');
            urlObj[paramsName[0]] = paramsName[1];
        }

        if (urlObj.hasOwnProperty('autorefresh') ) {
            if (urlObj['autorefresh'] === true || urlObj['autorefresh'] === 'true' || urlObj['autorefresh'] === '' || urlObj['autorefresh'] == null || urlObj['autorefresh'] === ' ') {
                this.autoRefresh = true;
            }
        }

        if (urlObj.hasOwnProperty('interval') ) {
            if (urlObj['interval'] === '' || urlObj['interval'] == null || urlObj['interval'] === undefined || urlObj['interval'] === ' ') {
                urlObj['interval'] = 1;
            }
        } else {
            urlObj['interval'] = 1;
        }
        this.durationParams = urlObj['interval'] * 60000;
        return this.durationParams;
    }
  }
}
