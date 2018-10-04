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


import { Injectable, ElementRef } from '@angular/core';

@Injectable()
export class CopytoClipboardService {

    constructor() {
    }

        // Implementation of 'copy to clipboard' feature

        copy (keyValue) {
            const key = keyValue;

            // create temp element
            const copyElement = document.createElement('span');
            copyElement.appendChild(document.createTextNode(key));
            copyElement.id = 'tempCopyToClipboard';
            document.body.appendChild(copyElement);

            // select the text
            const range = document.createRange();
            range.selectNode(copyElement);
            window.getSelection().removeAllRanges();
            window.getSelection().addRange(range);

            // copy & cleanup
            document.execCommand('copy');
            window.getSelection().removeAllRanges();
            copyElement.remove();
        }
}


