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
 * Created by adiagrwl on 11/Jan/18.
 */

import { Injectable, Inject } from '@angular/core';
import { Observable } from 'rxjs/Rx';
import 'rxjs/add/operator/toPromise';
import { HttpService } from './http-response.service';
import { ErrorHandlingService } from './error-handling.service';
import { ReplaySubject } from 'rxjs/ReplaySubject';
import { ToastObservableService } from '../../post-login-app/common/services/toast-observable.service';
import { LoggerService } from './logger.service';


@Injectable()
export class DownloadService {

    private subject = new ReplaySubject<any>(0);

    constructor(@Inject(HttpService) private httpService: HttpService,
                private errorHandling: ErrorHandlingService,
                private toastObservableService: ToastObservableService,
                private loggerService: LoggerService) { }


    requestForDownload(queryParam, downloadUrl, downloadMethod, downloadRequest, pageTitle, dataLength) {

        const fileType = 'csv';
        let downloadSubscription;

        try {

            if (dataLength === 0) {
                this.toastObservableService.postMessage(
                  'The requested data isn\'t available'
                );
                return;
            } else if (dataLength > 100000) {
                this.toastObservableService.postMessage(
                    'We are sorry, only 100k records can be downloaded at this time.\nPlease filter your results and try again.',
                    7
                  );
                  return;
            } else {
                this.toastObservableService.postMessage(
                  'The download has been requested'
                );
            }

        this.animateDownload(true);
        downloadSubscription = this.downloadData(
            queryParam,
            downloadUrl,
            downloadMethod,
            downloadRequest,
            pageTitle
          )
          .subscribe(
            response => {
              this.animateDownload(false);
              downloadSubscription.unsubscribe();
            },
            error => {
              this.loggerService.log('error', error);
              this.animateDownload(false);
              this.toastObservableService.postMessage(
                'Download failed. Please try later'
              );
              downloadSubscription.unsubscribe();
            }
          );
        } catch (error) {
            this.animateDownload(false);
            this.loggerService.log('error', error);
            downloadSubscription.unsubscribe();
            this.toastObservableService.postMessage('Download failed. Please try later');
        }
    }

    downloadData ( queryParam, downloadUrl, downloadMethod, downloadRequest, pageTitle ): Observable<any> {

        const url = downloadUrl;
        const method = downloadMethod;
        const queryParams = queryParam;
        const payload = downloadRequest;

        try {
            return this.httpService.getBlobResponse(url, method, payload, queryParams)
                    .map(response => {
                        const downloadResponse = response['_body'] || response;

                        const downloadUrlBlob = URL.createObjectURL(downloadResponse);

                        const file = document.createElement('a');
                        file.href = downloadUrlBlob;
                         if (response['type'] === 'text/csv') {
                            file.download = pageTitle + '.csv';
                        } else {
                            file.download = pageTitle + '.xls';
                        }

                        document.body.appendChild(file);
                        file.click();
                        setTimeout(function () {
                            document.body.removeChild(file);
                        }, 10);
                        return 'downloaded';
                    });
        } catch (error) {
            this.errorHandling.handleJavascriptError(error);
        }

    }

    animateDownload (msg: boolean) {
        this.subject.next(msg);
    }

    getDownloadStatus(): Observable<any> {
        return this.subject.asObservable();
    }
}
