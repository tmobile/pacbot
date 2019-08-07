
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
 * Created by ritesh on 06/19.
 */

import { Injectable } from '@angular/core';
import 'rxjs/add/operator/toPromise';
import { ErrorHandlingService } from './error-handling.service';
import { ToastObservableService } from './toast-observable.service';
import { LoggerService } from './logger.service';

@Injectable()
export class CopyElementService {


    constructor(
                private errorHandling: ErrorHandlingService,
                private toastObservableService: ToastObservableService,
                private loggerService: LoggerService) { }


    textCopyMessage(message, time_duration, type, image) {

        try {
            this.toastObservableService.postMessage(
              message , time_duration, type , image
            );
            return;
        } catch (error) {
            this.errorHandling.handleJavascriptError(error);
        }

    }
}

