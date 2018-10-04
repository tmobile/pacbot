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

import { Component, OnInit, Input, Output, EventEmitter, OnDestroy, ElementRef, HostListener } from '@angular/core';
import { LoggerService } from '../../../shared/services/logger.service';
import {
  FormControl,
  FormGroup,
  FormBuilder,
  Validators
} from '@angular/forms';
import { Subscription } from 'rxjs/Subscription';
import { environment } from './../../../../environments/environment';
import { CommonResponseService } from '../../../shared/services/common-response.service';
import { DataCacheService } from '../../../core/services/data-cache.service';
import { CONTENT } from './../../../../config/static-content';
import { CopytoClipboardService } from '../../../shared/services/copy-to-clipboard.service';
import { ToastObservableService } from '../../../post-login-app/common/services/toast-observable.service';
import { UtilsService } from '../../../shared/services/utils.service';

@Component({
  selector: 'app-issue-exception',
  templateUrl: './issue-exception.component.html',
  styleUrls: ['./issue-exception.component.css'],
  providers: [LoggerService, DataCacheService, CommonResponseService, CopytoClipboardService, UtilsService]
})
export class IssueExceptionComponent implements OnInit, OnDestroy {

  @Input() issueList: any = [];
  @Input() exceptionAction: any;
  @Output() closeExceptionalModal = new EventEmitter();
  @Output() refreshDataTable = new EventEmitter();
  showExceptionModal = false; // Check to show Add Exception modal
  showTransaction = false; // Remains True till exception/revoke api response received which is used to show loader in exception modal
  showLoadComplete = false; // Remains true after exception api response received which is used to success/error check mark in exception modal
  user: FormGroup; // Formgroup added for mandatory fields to be verified.
  endDate: any; // To display end date of exception on modal
  actionComplete = false; // to check success/error exception api response.
  search_card: any; // get current search tile object
  showRevokeExceptionmodal = false; // Check to show Revoke Exception modal
  addExceptionDetails = false; // check to show form for adding exception
  totalIssues = []; // total issues selected
  openIssueList = []; // total open issues selected
  exemptedIssueList = []; // total exempted issues selected
  failedIssueIds = []; // total failed issues list received from api
  maxIssues = 10; // max no. of issues can be processed at a time.
  public content; // get static config content
  public errorMessage: any;
  private exceptionSubscription: Subscription;
  constructor(
    private logger: LoggerService,
    private formBuilder: FormBuilder,
    private commonResponseService: CommonResponseService,
    private dataCacheService: DataCacheService,
    private copytoClipboardService: CopytoClipboardService,
    private toastObservableService: ToastObservableService,
    private utilsService: UtilsService
  ) {
    this.content = CONTENT;
   }

  ngOnInit() {
    this.user = new FormGroup({
      name: new FormControl('', [
        Validators.required,
        Validators.minLength(1)
      ])
    });
    if (this.exceptionAction === 'addException') {
      this.showExceptionModal = true;
    } else if (this.exceptionAction === 'revokeException') {
      this.showRevokeExceptionmodal = true;
    }
    this.getData();
  }

  closeModal() {
    this.closeExceptionalModal.emit();
  }

  getData() {
    if (this.issueList.length > 0 ) {
      this.openIssueList = [];
      this.exemptedIssueList = [];
      this.totalIssues = [];
      for (let i = 0; i < this.issueList.length; i++) {
        this.totalIssues.push(this.issueList[i]['Issue ID'].text);
        if (this.issueList[i]['Status'].text === 'open') {
          this.openIssueList.push(this.issueList[i]['Issue ID'].text);
        } else {
          this.exemptedIssueList.push(this.issueList[i]['Issue ID'].text);
        }
      }
    }
  }

/**
   * @func getDateData
   * @desc this funtion is called to set end date.
   */
  getDateData(date: any): any {
    try {
      this.endDate = date;
    } catch (e) {
      this.logger.log('error', e);
    }
  }

/**
   * @func onExceptionSubmit
   * @desc this funtion adds exception by api call with valid reason
   * and end date selected.
   */
  onExceptionSubmit({ value, valid }: { value; valid: boolean }) {
    try {
      this.failedIssueIds = [];
      this.showTransaction = true;
      const date = new Date();
      const endDateValue = this.utilsService.getUTCDate(this.endDate);
      const grantedDateValue = this.utilsService.getUTCDate(date);
      const payload = {
        createdBy: this.dataCacheService.getUserDetailsValue().getUserId(),
        exceptionEndDate: endDateValue,
        exceptionGrantedDate: grantedDateValue,
        exceptionReason: value.name,
        issueIds: this.totalIssues
      };
      const exceptionUrl = environment.addIssueException.url;
      const exceptionMethod = environment.addIssueException.method;
      this.exceptionSubscription = this.commonResponseService
        .getData(exceptionUrl, exceptionMethod, payload, {})
        .subscribe(
          response => {
            const data = response.data;
            this.showLoadComplete = true;
            this.verifyResponse(data);
          },
          error => {
            this.showLoadComplete = true;
            this.actionComplete = false;
            this.errorMessage = 'apiResponseError';
            this.updateExceptionPostResponse();
          }
        );
    } catch (e) {
      this.logger.log('error', e);
    }
  }

/**
   * @func updateExceptionPostResponse
   * @desc this funtion is called after api response of add/revoke exception.
   */
  updateExceptionPostResponse() {
    try {
      this.showLoadComplete = true;
      // show modal for 5s after receiving response
      setTimeout(() => {
        this.closeExceptionalModal.emit();
        this.showTransaction = false;
        this.showLoadComplete = false;
        }, 5000);
      this.user.reset();
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  verifyResponse(data) {
      if (data.status === 'Success' && data.failedIssueIds.length === 0) {
        this.actionComplete = true;
        this.updateExceptionPostResponse();
        this.refreshDataTable.emit();
      } else if ((data.status === 'Partial Success' || data.status === 'Failed') && data.failedIssueIds.length > 0) {
        this.actionComplete = false;
        this.failedIssueIds = data.failedIssueIds;
      } else {
        this.actionComplete = false;
      }
  }

/**
   * @func revokeException
   * @desc this funtion revokes added exception by api call.
   */
  revokeException() {
    try {
      this.failedIssueIds = [];
      this.showTransaction = true;
      const Url = environment.revokeIssueException.url;
      const Method = environment.revokeIssueException.method;
      const payload = {
        issueIds: this.exemptedIssueList
      };
      this.exceptionSubscription = this.commonResponseService
        .getData(Url, Method, payload, {})
        .subscribe(
          response => {
            this.showLoadComplete = true;
            const data = response.data;
            this.verifyResponse(data);
          },
          error => {
            this.showLoadComplete = true;
            this.actionComplete = false;
            this.errorMessage = 'apiResponseError';
            this.updateExceptionPostResponse();
          }
        );
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  proceedWithException() {
    if (this.showExceptionModal) {
      this.addExceptionDetails = true;
    } else {
      this.showTransaction = true;
      this.revokeException();
    }
  }

  goToSlackLink(link) {
    window.open(link, '_blank');
  }

  copytext() {
    const copyText = document.getElementById('ids-list');
    this.copytoClipboardService.copy(copyText.innerText);
    this.toastObservableService.postMessage(
      'Issue IDs copied to clipboard'
    );

  }

  ngOnDestroy() {
    try {
      if (this.exceptionSubscription) {
        this.exceptionSubscription.unsubscribe();
      }
    } catch (error) {
      this.logger.log('error', '--- Error while unsubscribing ---');
    }
  }

}
