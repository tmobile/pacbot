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
 * @desc Logger, this service is used to log errors, log info for debugging, log silent javascript errors.
 * @author Puneet Baser
 */


import { Injectable } from '@angular/core';

@Injectable()
export class LoggerService {

    constructor() {}

    log (level, message) {

        const timestamp = new Date().toISOString();

        if (level === 'error') {
            console.log(timestamp, 'ERROR \t', message);
            /*
             If required, add custom actions here; Such as send a mail, etc...
             */
        } else if (level === 'info') {
            console.log(timestamp, 'INFO  \t', message);
        } else if (level === 'debug') {
            console.log(timestamp, 'DEBUG  \t', message);
        } else {
            console.log(timestamp, level, '\t', message);
        }
    }
}
