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

import { Component, OnInit, OnDestroy } from '@angular/core';
import { TitleCasePipe } from '@angular/common';
import { environment } from '../../../../../environments/environment';
import { CommonResponseService } from '../../../../shared/services/common-response.service';
import { Router, ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import { LoggerService } from '../../../../shared/services/logger.service';

@Component({
  selector: 'app-system-management',
  templateUrl: './system-management.component.html',
  styleUrls: ['./system-management.component.css'],
  providers: [TitleCasePipe]
})
export class SystemManagementComponent implements OnInit, OnDestroy {
  pageTitle = 'System Management';
  breadcrumbArray: any = ['Admin'];
  breadcrumbLinks: any = ['policies'];
  breadcrumbPresent: any = 'System Management';
  isCheckedRules = false;
  isCheckedJobs = false;
  inputValue;
  OpenModal = false;
  selectedValue;
  showLoader = false;
  showPageLoader = 1;
  errorMessage = '';
  errorMsg = 'apiResponseError';
  errorVal = 0;
  modalTitle = 'Confirmation Required';
  private systemSubscription: Subscription;
  private systemStatusSubscription: Subscription;
  constructor(
    private commonResponseService: CommonResponseService,
    private router: Router,
    private logger: LoggerService,
    private titleCasePipe: TitleCasePipe
  ) { }

  getJobStatus() {
    const url = environment.systemJobStatus.url;
    const method = environment.systemJobStatus.method;

    this.systemStatusSubscription = this.commonResponseService
      .getData(url, method, {}, {}).subscribe(
        response => {
          if(!response) return;
          this.isCheckedRules = response.rule === 'ENABLED' ? false : true;
          this.isCheckedJobs  = response.job === 'ENABLED' ? false : true;
          this.showPageLoader = 0;
        },
        error => {
          this.showPageLoader = -1;
          this.errorMessage = error;
        }
      )
  }

  ontoggleAccess(e, selectToggle) {
    e.preventDefault();
    this.OpenModal = true;
    this.selectedValue = selectToggle;
  }

  submitToCheckConfirm() {
    this.showLoader = true;
    if ( this.inputValue.toLowerCase() === 'confirm') {
      if (this.selectedValue === 'rule') {
          this.postOperations(this.selectedValue, this.isCheckedRules);
        }else if (this.selectedValue === 'job') {
          this.postOperations(this.selectedValue, this.isCheckedJobs);
      }
      // this.OpenModal = false;
    }
    // this.inputValue = '';
    // this.showLoader = false;
  }

  closeModal() {
    if (this.systemSubscription) {
      this.systemSubscription.unsubscribe();
    }
    this.OpenModal = false;
    this.inputValue = '';
    this.errorVal = 0;
    this.modalTitle = 'Confirmation Required';
    this.showLoader = false;
  }

  postOperations(jobType, jobAction) {
    this.showLoader = true;
    if (this.systemSubscription) {
      this.systemSubscription.unsubscribe();
      this.systemStatusSubscription.unsubscribe();
    }
    const url = environment.systemOperations.url;
    const method = environment.systemOperations.method;
    let operation;
      operation = jobAction ? 'enable' : 'disable';
      // below is right way - commented currently to prevent accidental shutdown
      // operation = jobAction === false ? 'disable' : 'enable';
    const queryParams = {
      'operation' : operation,
      'job': jobType
    };

    this.systemSubscription = this.commonResponseService
      .getData(url, method, {}, queryParams)
      .subscribe(
        response => {
          let custom_message = this.titleCasePipe.transform(jobType) + 's operation is performed successfully.';
          if(response) {
            custom_message = response.data;
          }
          this.errorMsg = custom_message
          this.errorVal = 1;
          this.modalTitle = 'Success';
          this.showLoader = false;
          this.toggleBtnOnSuccess(jobType);
        } , error => {
          this.errorVal = -1;
          this.modalTitle = 'Error';
          this.showLoader = false;
          error.toLowerCase() !== 'apiresponseerror' ? this.errorMsg = error : this.errorMsg = 'Oops! An error occurred while performing the  ' +  jobType + ' batches operation.';
        }
      );
  }

  toggleBtnOnSuccess(jobType) {
    if (jobType === 'rule') {
      this.isCheckedRules = !this.isCheckedRules;
    } else {
      this.isCheckedJobs = !this.isCheckedJobs;
    }

  }

  ngOnInit() {
    this.getJobStatus();
  }

  ngOnDestroy() {
    try {
      if (this.systemSubscription) {
        this.systemSubscription.unsubscribe();
      }
      if (this.systemStatusSubscription) {
        this.systemStatusSubscription.unsubscribe();
      }
    } catch (error) {
      this.logger.log('error', '--- Error while unsubscribing ---');
    }
  }


}
