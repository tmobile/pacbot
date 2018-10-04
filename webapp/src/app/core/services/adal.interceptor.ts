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

import { Injectable, Injector } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { mergeMap } from 'rxjs/operators';

import { AdalService } from './adal.service';

@Injectable()
export class AdalInterceptor implements HttpInterceptor {

    constructor(private adal: AdalService) { }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

        // if the endpoint is not registered then pass
        // the request as it is to the next handler
        const resource = this.adal.GetResourceForEndpoint(req.url);
        if (!resource) {
            return next.handle(req.clone());
        }

        // if the user is not authenticated then drop the request
        if (!this.adal.userInfo.authenticated) {
            throw new Error('Cannot send request to registered endpoint if the user is not authenticated.');
        }

        // if the endpoint is registered then acquire and inject token
        let headers = req.headers || new HttpHeaders();
        return this.adal.acquireToken(resource).pipe(
            mergeMap((token: string) => {
                // inject the header
                headers = headers.append('Authorization', 'Bearer ' + token);
                return next.handle(req.clone({ headers: headers }));
            }
            )
        );
    }
}
