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
 * Author: Puneet Baser
 * Description: ServerErrorsInterceptor service will intercept all failed http request and retry
 * before returning error
 */

// server-errors.interceptor.ts
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/retry';

@Injectable()
export class ServerErrorsInterceptor implements HttpInterceptor {

  intercept(request: HttpRequest<any>, next: HttpHandler):     Observable<HttpEvent<any>> {
    // If the call fails, retry until 5 times before throwing an error
    return next.handle(request).retry(3);
  }
}
