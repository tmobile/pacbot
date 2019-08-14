import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import * as _ from 'lodash';
import { environment } from './../../../../../environments/environment';
import { WorkflowService } from '../../../../core/services/workflow.service';
import { FormGroup, FormControl, Validators, NgForm } from '@angular/forms';
import { LoggerService } from '../../../../shared/services/logger.service';
import { DataCacheService } from '../../../../core/services/data-cache.service';
import { FilterManagementService } from '../../../../shared/services/filter-management.service';
import { ErrorHandlingService } from '../../../../shared/services/error-handling.service';
import { AssetGroupObservableService } from '../../../../core/services/asset-group-observable.service';
import { DomainTypeObservableService } from '../../../../core/services/domain-type-observable.service';
import { UtilsService } from '../../../../shared/services/utils.service';
import { RouterUtilityService } from '../../../../shared/services/router-utility.service';
import { RefactorFieldsService } from '../../../../shared/services/refactor-fields.service';
import { CommonResponseService } from '../../../../shared/services/common-response.service';
import { WindowRefService } from '../../../services/window.service';
import { FormService } from '../../../../shared/services/form.service';
@Component({
  selector: 'app-account-management-details',
  templateUrl: './account-management-details.component.html',
  styleUrls: ['./account-management-details.component.css']
})

export class AccountManagementDetailsComponent implements OnInit, OnDestroy {
  isCreateFlow = true;
  isupdated;
  isSuccess = false;
  pageTitle: String = 'Create Account';
  fieldArray = [];
  tableSubscription: Subscription;
  breadcrumbDetails = {
    breadcrumbArray: ['Admin', 'Account Management'],
    breadcrumbLinks: ['policies', 'account-management'],
    breadcrumbPresent: 'Details',
  };
  // Reactive-forms
  private accountManagementForm: FormGroup;
  public formErrors;

  backButtonRequired: boolean;
  url;
  pageLevel = 0;
  errorMessage: string;
  errorValue = 0;
  accountValue;
  agAndDomain = {};

  isFilterRquiredOnPage = false;
  appliedFilters = {
    queryParamsWithoutFilter: {}, /* Stores the query parameter ibject without filter */
    pageLevelAppliedFilters: {} /* Stores the query parameter ibject without filter */
  };
  filterArray = []; /* Stores the page applied filter array */

  routeSubscription: Subscription;

  constructor(private router: Router,
    private activatedRoute: ActivatedRoute,
    private workflowService: WorkflowService,
    private logger: LoggerService,
    private dataStore: DataCacheService,
    private commonResponseService: CommonResponseService,
    private filterManagementService: FilterManagementService,
    private errorHandling: ErrorHandlingService,
    private assetGroupObservableService: AssetGroupObservableService,
    private domainObservableService: DomainTypeObservableService,
    private utils: UtilsService,
    private routerUtilityService: RouterUtilityService,
    private refactorFieldsService: RefactorFieldsService,
    private formService: FormService) {

    this.assetGroupObservableService.getAssetGroup().subscribe(
      assetGroupName => {
          this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(this.pageLevel);
    });

    this.buildForm();

  }

  ngOnInit() {
    this.routeSubscription = this.activatedRoute.params.subscribe(params => {
      // Fetch the required params from this object.
      this.accountValue = params.id;
      if (!this.accountValue) {
        this.isCreateFlow = true;
        this.errorValue = 1;
      } else {
        this.isCreateFlow = false;
      }
    });
    this.reset();
    if (!this.isCreateFlow) {
      this.updateFields(this.accountValue);
    }
  }

  reset() {
    /* Reset the page */
    this.filterArray = [];
  }

  buildForm() {
    this.accountManagementForm = new FormGroup({
      'accountNumber': new FormControl('', Validators.compose([
        Validators.required,
        Validators.pattern('[0-9]*')
      ])),
      'accountName': new FormControl('', Validators.compose([
        Validators.required,
        Validators.pattern('[0-9A-Za-z]+')
      ])),
      'accountDescription': new FormControl('')
    });

    this.formErrors = {
      accountNumber: '',
      accountName: '',
      accountDescription: '',
    };

    this.accountManagementForm.valueChanges.subscribe((data) => {
      this.formErrors = this.formService.validateForm(this.accountManagementForm, this.formErrors, true);
      console.log(this.formErrors);
    });
  }

  updateFields(fieldValue) {
    /* Update the value of inputfileds */
    try {
      if (this.tableSubscription) {
        this.tableSubscription.unsubscribe();
      }
      const payload = {};
      const queryParams = {};
      this.errorValue = 0;
      const accountUrl = environment.getAccounts.url;
      const accountApiMethod = environment.getAccounts.method;
      this.tableSubscription = this.commonResponseService
        .getData(accountUrl, accountApiMethod, payload, queryParams)
        .subscribe(
          response => {
            try {
              this.errorValue = 1;
              if (this.utils.checkIfAPIReturnedDataIsEmpty(response)) {
                this.errorValue = -1;
                this.errorMessage = 'noDataAvailable';
              }
              if (response.length > 0) {
                for (let i = 0; i < response.length; i++) {
                  if (response[i].id === fieldValue) {
                    this.fieldArray = response[i];
                    this.accountManagementForm.setValue({
                      'accountNumber': response[i].accountId,
                      'accountName': response[i].accountName,
                      'accountDescription': response[i].accountDesc
                    });
                  }
                }

              }
            } catch (e) {
              this.errorValue = 0;
              this.errorValue = -1;
              this.errorMessage = 'jsError';
            }
          },
          error => {
            this.errorValue = -1;
            this.errorMessage = 'apiResponseError';
          }
        );
    } catch (error) {

      this.logger.log('error', error);
    }
  }


  updateComponent() {
    /* Updates the whole component */
  }

  routerParam() {
    try {

      /* Check query parameters */
      const currentQueryParams = this.routerUtilityService.getQueryParametersFromSnapshot(this.router.routerState.snapshot.root);

      if (currentQueryParams) {

        this.appliedFilters.queryParamsWithoutFilter = JSON.parse(JSON.stringify(currentQueryParams));
        delete this.appliedFilters.queryParamsWithoutFilter['filter'];

        this.appliedFilters.pageLevelAppliedFilters = this.utils.processFilterObj(currentQueryParams);

        this.filterArray = this.filterManagementService.getFilterArray(this.appliedFilters.pageLevelAppliedFilters);
      }
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  updateUrlWithNewFilters(filterArr) {
    this.appliedFilters.pageLevelAppliedFilters = this.utils.arrayToObject(
      this.filterArray,
      'filterkey',
      'value'
    ); // <-- TO update the queryparam which is passed in the filter of the api
    this.appliedFilters.pageLevelAppliedFilters = this.utils.makeFilterObj(this.appliedFilters.pageLevelAppliedFilters);

    /**
     * To change the url
     * with the deleted filter value along with the other existing paramter(ex-->tv:true)
     */

    const updatedFilters = Object.assign(
      this.appliedFilters.pageLevelAppliedFilters,
      this.appliedFilters.queryParamsWithoutFilter
    );

    /*
     Update url with new filters
     */

    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: updatedFilters
    }).then(success => {
      this.routerParam();
    });
  }

  register(myForm: NgForm) {
    this.errorValue = 0;
    // mark all fields as touched
    this.formService.markFormGroupTouched(this.accountManagementForm);
    if (this.accountManagementForm.valid) {
      this.addUpdateAccount(myForm.value);
    }
  }

  takeActionPostTransaction(event) {
    if (event === 'back') {
      this.backToEditOrCreate();
    } else {
      this.navigateToOrigin();
    }
  }

  navigateToOrigin() {
    this.router.navigate(['pl/admin/account-management'], {
      queryParamsHandling: 'merge',
      queryParams: {
      }
    });
  }

  navigateBack() {
    try {
      this.workflowService.goBackToLastOpenedPageAndUpdateLevel(this.router.routerState.snapshot.root);
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  backToEditOrCreate() {
    this.errorValue = 1;
  }

  addUpdateAccount(details) {
    {
      /* Update the value of inputfileds */

      try {

        if (this.tableSubscription) {
          this.tableSubscription.unsubscribe();
        }

        const userDetails = JSON.parse(this.dataStore.getCurrentUserLoginDetails());
        const payload = {
          'accountDesc': details.accountDescription,
          'id': this.accountValue,
          'accountId': details.accountNumber,
          'accountName': details.accountName,
          'user': userDetails.userInfo.userId
        };
        const queryParams = {};
        let accountUrl;
        let accountApiMethod;
        if (!this.isCreateFlow) {
          accountUrl = environment.updateAccount.url;
          accountApiMethod = environment.updateAccount.method;
        } else {
          accountUrl = environment.createAccount.url;
          accountApiMethod = environment.createAccount.method;
        }
        this.tableSubscription = this.commonResponseService
          .getData(accountUrl, accountApiMethod, payload, queryParams)
          .subscribe(
            response => {
              try {
                this.errorValue = 2;
              } catch (e) {

              }
            },
            error => {
              this.errorValue = -1;
            }
          );
      } catch (error) {
        this.logger.log('error', error);
      }
    }
  }
  allMessages() {
    const obj = {
      type: '',
      title: '',
      message: ''
    };
    if (this.errorValue === 2) {
      obj.type = 'success';
      if (this.isCreateFlow) {
        obj.title = 'Success!';
        obj.message = 'You have succesfully created an account';
        return obj;
      } else {
        if (!this.isCreateFlow) {
          obj.title = 'Success!';
          obj.message = 'You have succesfully updated an account';
          return obj;
        }
      }
    } else {
      if (this.errorValue === -1) {
        obj.type = 'error';
        if (this.isCreateFlow) {
          obj.title = 'Error!';
          obj.message = 'Creating a new account';
          return (obj);
        } else {
          if (!this.isCreateFlow) {
            obj.title = 'Error!';
            obj.message = 'Updating account :' + this.accountValue;
            return (obj);
          }
        }
      }
    }
  }
  ngOnDestroy() {
    try {
    } catch (error) {
      this.logger.log('error', 'JS Error - ' + error);
    }
  }

}

