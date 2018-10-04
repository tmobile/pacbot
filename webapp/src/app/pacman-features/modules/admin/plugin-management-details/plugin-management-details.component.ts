import { Component, OnInit, OnDestroy } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs/Subscription';
import { environment } from './../../../../../environments/environment';
import {WorkflowService} from '../../../../core/services/workflow.service';
import {LoggerService} from '../../../../shared/services/logger.service';
import { CommonResponseService } from '../../../../shared/services/common-response.service';
import {DataCacheService} from '../../../../core/services/data-cache.service';
import { FormService } from '../../../../shared/services/form.service';
import {FilterManagementService} from '../../../../shared/services/filter-management.service';
import {ErrorHandlingService} from '../../../../shared/services/error-handling.service';
import {AssetGroupObservableService} from '../../../../core/services/asset-group-observable.service';
import {DomainTypeObservableService} from '../../../../core/services/domain-type-observable.service';
import {UtilsService} from '../../../../shared/services/utils.service';
import {RouterUtilityService} from '../../../../shared/services/router-utility.service';
import { FormGroup, FormControl, Validators, NgForm } from '@angular/forms';
import {RefactorFieldsService} from '../../../../shared/services/refactor-fields.service';

@Component({
  selector: 'app-plugin-management-details',
  templateUrl: './plugin-management-details.component.html',
  styleUrls: ['./plugin-management-details.component.css']
})

export class PluginManagementDetailsComponent implements OnInit, OnDestroy {

  pageTitle: String = 'Plugin Management Details';
  breadcrumbDetails = {
    breadcrumbArray: ['Admin', 'Plugin Management'],
    breadcrumbLinks: ['policies', 'plugin-management'],
    breadcrumbPresent: 'Details'
  };
  backButtonRequired: boolean;
  pageLevel = 0;
  errorValue = 0;
  agAndDomain = {};
  tableSubscription: Subscription;
  pluginId;
  formData;
  isFilterRquiredOnPage = false;
  appliedFilters = {
    queryParamsWithoutFilter: {}, /* Stores the query parameter ibject without filter */
    pageLevelAppliedFilters: {} /* Stores the query parameter ibject without filter */
  };
  filterArray = []; /* Stores the page applied filter array */

  // Reactive-forms
  private pluginManagementForm: FormGroup;
  public formErrors = {};
  public formGroup = {};
  private FetchedPlugin;
  errorMessage = 'apiResponseError';

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

              }

  ngOnInit() {
    this.reset();
    this.init();
  }

  reset() {
    /* Reset the page */
    this.filterArray = [];
  }

  init() {
    /* Initialize */
    this.routerParam();

    this.routeSubscription = this.activatedRoute.params.subscribe(params => {
      // Fetch the required params from this object.
      this.pluginId = params.pluginId;
    });

    this.updateComponent();
  }

  updateComponent() {
    /* Updates the whole component */
    this.getData();
  }

  getData() {
    try {
      if (this.tableSubscription) {
        this.tableSubscription.unsubscribe();
      }
      const payload = {};
      const queryParams = {
        pluginId: this.pluginId
      };

      this.errorValue = 0;
      const pluginUrl = environment.getPlugins.url;
      const pluginMethod = environment.getPlugins.method;
      this.tableSubscription = this.commonResponseService
        .getData(pluginUrl, pluginMethod, payload, queryParams)
        .subscribe(
          response => {
            try {
              const plugin = response.plugins;
              if (this.utils.checkIfAPIReturnedDataIsEmpty(plugin)) {
                this.errorValue = -1;
                this.errorMessage = 'noDataAvailable';
                return;
              }
              this.FetchedPlugin = plugin[0];
              this.buildForm(this.FetchedPlugin);
              this.errorValue = 1;
            } catch (e) {
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
      this.errorValue = -1;
      this.logger.log('error', error);
      this.errorMessage = 'jsError';
    }
  }

  buildForm(plugin) {
    const pluginName = plugin.pluginName;
    const pluginDetails = plugin.pluginDetails;

    this.formData = [];
    this.formGroup = {};

    const pluginNameFieldObj = {
      formControlName: 'pluginName',
      formControlDisplayName: 'Plugin Name'
    };

    this.formGroup['pluginName'] = new FormControl(pluginName, Validators.required);
    this.formErrors['pluginName'] = '';
    this.formData.push(pluginNameFieldObj);

    for (let i = 0; i < pluginDetails.length; i++) {
      const individualPluginField = {
        formControlName: pluginDetails[i].key,
        formControlDisplayName:
        this.refactorFieldsService.getDisplayNameForAKey(
          pluginDetails[i].key.toLocaleLowerCase()
        ) || pluginDetails[i].key
      };
      this.formData.push(individualPluginField);
      this.formGroup[pluginDetails[i].key] = new FormControl(pluginDetails[i].value, Validators.required);
      this.formErrors[pluginDetails[i].key] = '';
    }

    this.pluginManagementForm = new FormGroup(this.formGroup);

    this.pluginManagementForm.valueChanges.subscribe((data) => {
      this.formErrors = this.formService.validateForm(this.pluginManagementForm, this.formErrors, true);
    });

  }

  register(myForm: NgForm) {
    // mark all fields as touched
    this.formService.markFormGroupTouched(this.pluginManagementForm);
    if (this.pluginManagementForm.valid) {
      this.operateList(myForm.value);
    }
  }

  operateList(updatedValues) {
    {
      /* Update the value of inputfileds */
      try {

        const existingPluginObj = this.FetchedPlugin;
        existingPluginObj.pluginName = updatedValues.pluginName;
        const updatedPluginDetailsObj = existingPluginObj.pluginDetails.map((pluginDetailField, index) => {
          const key = pluginDetailField.key;
          pluginDetailField.value = updatedValues[key] || pluginDetailField.value;
          return pluginDetailField;
        });

        existingPluginObj.pluginDetails = updatedPluginDetailsObj;

        const payload = {
          'plugins' : [existingPluginObj]
        };

        if (this.tableSubscription) {
          this.tableSubscription.unsubscribe();
        }

        const queryParams = {};
        const pluginUrl = environment.postPlugins.url;
        const pluginMethod = environment.postPlugins.method;
        this.errorValue = 0;
        this.tableSubscription = this.commonResponseService
          .getData(pluginUrl, pluginMethod, payload, queryParams)
          .subscribe(
            response => {
              try {
                this.errorValue = 2;
              } catch (e) {
                this.logger.log('error', 'JS error in update plugin');
                this.errorMessage = 'jsError';
                this.errorValue = -1;
              }
            },
            error => {
              this.logger.log('error', 'Error in update plugin - ' + error);
              this.errorMessage = error;
              this.errorValue = -1;
            }
          );
      } catch (error) {
        this.logger.log('error', error);
      }
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
    this.router.navigate(['pl/admin/plugin-management'], {
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

  getMessages(errorValue) {
    // error value = 2 -> Success
    // error value = -1 -> Error
    const obj = {
      type: '',
      title: '',
      message: ''
    };
    if (this.errorValue === 2) {
      obj.type = 'success';
      obj.title = 'Success!';
      obj.message = 'You have succesfully updated plugin: ' + this.pluginId;
    } else if (this.errorValue === -1) {
      obj.type = 'error';
      obj.title = 'Error!';
      obj.message = 'Updating account :' + this.pluginId;
    }
    return obj;
  }

  ngOnDestroy() {
    try {
      if (this.tableSubscription) {
        this.tableSubscription.unsubscribe();
      }
    } catch (error) {
      this.logger.log('error', 'JS Error - ' + error);
    }
  }

}

