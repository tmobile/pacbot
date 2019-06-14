import { Component, OnInit, OnDestroy } from '@angular/core';
import {Subscription} from 'rxjs/Subscription';
import { environment } from './../../../../../environments/environment';
import {LoggerService} from '../../../../shared/services/logger.service';
import {UtilsService} from '../../../../shared/services/utils.service';
import { CommonResponseService } from '../../../../shared/services/common-response.service';
import {RefactorFieldsService} from '../../../../shared/services/refactor-fields.service';
import { FormGroup, FormControl, Validators, NgForm } from '@angular/forms';
import { FormService } from '../../../../shared/services/form.service';
import { HttpHeaders } from '@angular/common/http';
import {WorkflowService} from '../../../../core/services/workflow.service';
import { ToastObservableService } from '../../../../post-login-app/common/services/toast-observable.service';
import * as _ from 'lodash';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-config-management',
  templateUrl: './config-management.component.html',
  styleUrls: ['./config-management.component.css']
})


export class ConfigManagementComponent implements OnInit, OnDestroy {

  pageTitle: String = 'Configuration Management';
  breadcrumbDetails = {
    breadcrumbArray: ['Admin'],
    breadcrumbLinks: ['policies'],
    breadcrumbPresent: 'Configuration'
  };
  errorMessage = 'apiResponseError';
  dataSubscription: Subscription;
  configPropertySubscription: Subscription;
  updateSubscription: Subscription;
  applyActive1 =  -1;
  applyActive2 = -1;
  applyActive3 = -1;
  activeColumn = 0;
  editProperty= false;
  showPreview = false;
  formData;
  overrideFormData = [];
  editData: any;
  originalData: any;
  previousEditedData: any;
  errorValue = 0;
  // Reactive-forms
  private configManagementForm: FormGroup;
  public overrideFormGroup = {};
  public formErrors = {};
  public formGroup = {};
  firstLevelIndex: any;
  secondLevelIndex: any;
  thirdLevelIndex: any;
  fourthLevelIndex: any;
  firstLevelData = {};
  secondaryLevelData = {};
  tertiaryLevelData = {};
  applicationBreadcrumb = [];
  showConfBox = {
    'value': false,
    'modified': {
    'columnIndex': 0
    },
    'isConfigColumnModified': false,
    'isOverridenAlreadyExist': false,
    'showContinueBtn': 1,
    'isEditPageCancelBtnClick': false,
    'confirmDelete': false,
    'resetToView': false
  };
  editColumnIndex = -1;
  overiddenData = [];
  filterData: {};
  showLoader = true;
  showError = false;
  createNew = false;
  transactionObj = {
    type: '',
    title: '',
    message: '',
  obj1: {
    type: '',
    title: '',
    message: ''
  }
};
transactionResponse = {
  updatedConfig: {
    success: false,
    error: false
  },
  createdConfig: {
    success: false,
    error: false
  },
  deleteConfig: {
    success: false,
    error: false
  },
  rollbackConfig: {
    success: false,
    error: false
  },
  responseCount: 0
};
configKeys = {
  errorValue: 0,
  value: [],
  errorMessage: 'apiResponseError'
};
configAudit = {
  errorValue: 0,
  value: [],
  errorMessage: 'apiResponseError'
};
deletePayLoadOnhold = {};
deleteFieldInProgress = false;
updateInProgress = false;
deleteTransactionResponse;
confirmationMessage;
historyPreview: any;
showHistoryPreview = false;
showDropdown = false;
activeHistoryTile = -1;
userRemarks = '';
showUserRemarks = false;
backButtonRequired: boolean;
pageLevel = 0;
  public checkBoxSelectedCount = new Array<number>();
  constructor(private router: Router,
              private workflowService: WorkflowService,
              private commonResponseService: CommonResponseService,
              private logger: LoggerService,
              private utils: UtilsService,
              private refactorFieldsService: RefactorFieldsService,
              private formService: FormService,
              private toastObservableService: ToastObservableService) {
      this.getConfigKeys();
      this.updateComponent();

  }

  ngOnInit() {
    this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(this.pageLevel);
  }

  updateComponent() {
    /* Updates the whole component */
    this.reset();
    this.getHistory();
    this.getData();
  }

  reset() {
    this.errorValue = 0;
    this.firstLevelData = {};
    this.secondaryLevelData = {};
    this.tertiaryLevelData = {};
    this.applyActive1 =  -1;
    this.activeColumn = 0;
    this.editProperty = false;
    this.showPreview = false;
    this.editData = [];
    this.originalData = [];
    this.previousEditedData = [];
    this.applicationBreadcrumb = [];
    this.resetEditConfig();
    this.transactionResponse = {
      updatedConfig: {
        success: false,
        error: false
      },
      createdConfig: {
        success: false,
        error: false
      },
      deleteConfig: {
        success: false,
        error: false
      },
      rollbackConfig: {
        success: false,
        error: false
      },
      responseCount: 0
    };

    this.transactionObj = {
      type: '',
      title: '',
      message: '',
      obj1: {
        type: '',
        title: '',
        message: ''
      }
    };
    this.confirmationMessage = '';
    this.deleteTransactionResponse = null;
    this.showHistoryPreview = false;
    this.activeHistoryTile = -1;
    this.userRemarks = '';
    this.showUserRemarks = false;
  }

  navigateBack() {
    try {
      this.workflowService.goBackToLastOpenedPageAndUpdateLevel(this.router.routerState.snapshot.root);
    } catch (error) {
      this.logger.log('error', error);
    }

  }

  getData() {
      try {
        const configUrl = environment.getConfigProperties.url;
        const configMethod = environment.getConfigProperties.method;
        this.dataSubscription = this.commonResponseService
          .getData(configUrl, configMethod, {}, {})
          .subscribe(
            response => {
                try {
                  if (this.utils.checkIfAPIReturnedDataIsEmpty(response)) {
                    this.errorValue = -1;
                    this.errorMessage = 'noDataAvailable';
                  } else {
                    this.errorValue = 1;
                    this.filterData = response.applications;
                    this.storeFirstLevel(this.filterData[0], 0);
                  }
                } catch (e) {
                  this.errorValue = -1;
                  this.errorMessage = 'jsError';
                  this.logger.log('error', e);
                }
              },
              error => {
                this.errorValue = -1;
                this.errorMessage = 'apiResponseError';
                this.logger.log('error', error);
              });
            } catch (error) {
              this.logger.log('error', error);
            }
  }

  storeFirstLevel(data, index, event?) {
    try {
      this.applyActive1 = index;
      if (this.editProperty) {
        this.showConfBox.modified = {
          'columnIndex': 1
        };
        this.showConfBox.isConfigColumnModified = true;
        this.showConfBox.isOverridenAlreadyExist = false;
        this.confirmationMessage = this.confirmationCondition();
        this.showConfBox.value = true;
        return;
      }
      this.activeColumn = 1;
      this.firstLevelIndex = index;
        this.firstLevelData = {};
        this.secondaryLevelData = {};
        this.applicationBreadcrumb = [];
        if (data == null || data.length === 0) {
          this.firstLevelData = {};
        } else {
          this.firstLevelData = data;
          // remove duplicates from properties
          this.firstLevelData['properties'] = _.uniqBy(this.firstLevelData['properties'], 'key');
          this.applicationBreadcrumb.push(this.firstLevelData['name']);
        }
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  storeSecondLevel(data, index) {
    try {
      this.applyActive2 = index;
      if (this.editProperty) {
        this.showConfBox.resetToView = true;
        this.showConfBox.modified = {
          'columnIndex': 2
        };
        this.showConfBox.isConfigColumnModified = true;
        this.showConfBox.isOverridenAlreadyExist = false;
        this.confirmationMessage = this.confirmationCondition();
        this.showConfBox.value = true;
        return;
      }
      this.activeColumn = 2;
      this.checkBoxSelectedCount = [];
      this.secondLevelIndex = index;
      this.secondaryLevelData = {};
      this.applicationBreadcrumb = [];
      if (data == null || data.length === 0) {
        this.secondaryLevelData = {};
      } else {
        this.secondaryLevelData = data;
        // remove duplicates from properties
        this.secondaryLevelData['properties'] = _.uniqBy(this.secondaryLevelData['properties'], 'key');
        this.applicationBreadcrumb.push(
          this.firstLevelData['name'],
          this.secondaryLevelData['name']
        );
      }
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  storeThirdLevel(data, index) {
    try {
      this.applyActive3 = index;
      if (this.editProperty) {
        this.showConfBox.resetToView = true;
        this.showConfBox.modified = {
          'columnIndex': 3
        };
        this.showConfBox.isConfigColumnModified = true;
        this.showConfBox.isOverridenAlreadyExist = false;
        this.confirmationMessage = this.confirmationCondition();
        this.showConfBox.value = true;
        return;
      }
      this.activeColumn = 3;
      this.thirdLevelIndex = index;
      this.tertiaryLevelData = data;
      // remove duplicates from properties
      this.tertiaryLevelData['properties'] = _.uniqBy(this.tertiaryLevelData['properties'], 'key');
      this.applicationBreadcrumb = [];
      this.applicationBreadcrumb.push(
        this.firstLevelData['name'],
        this.secondaryLevelData['name'],
        this.tertiaryLevelData['name']
      );
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  buildForm(application, index) {
    this.originalData = JSON.parse(JSON.stringify(application));
    this.editData = JSON.parse(JSON.stringify(application));
    const data = application.properties;
    this.editColumnIndex = index;
    this.formData = [];
    this.formGroup = {};

    for (let i = 0; i < data.length; i++) {
      let individualField = {
        formControlName: data[i].key,
        formControlDisplayName:
        this.refactorFieldsService.getDisplayNameForAKey(
          data[i].key.toLocaleLowerCase()
        ) || data[i].key
      };
      if (data[i].description) {
        individualField = Object.assign({'metadata': data[i].description}, individualField);
      }
      this.formData.push(individualField);
      this.formGroup[data[i].key] = new FormControl(data[i].value, Validators.required);
      this.formErrors[data[i].key] = '';
    }

    this.configManagementForm = new FormGroup ({
      edit: new FormGroup(this.formGroup)
    });

    this.configManagementForm.valueChanges.subscribe(() => {
      this.formErrors = this.formService.validateForm(this.configManagementForm, this.formErrors, true);
    });

  }

  register(myForm: NgForm) {
    // mark all fields as touched
    if (this.configManagementForm.controls.hasOwnProperty('edit')) {
      const editFormGroup = this.configManagementForm['controls']['edit'] as FormGroup;
      this.formService.markFormGroupTouched(editFormGroup);
      if (editFormGroup.valid) {
        this.previewEditList(editFormGroup.value);
      }
    }
    if (this.configManagementForm.controls.hasOwnProperty('overidden')) {
      const overridenFormGroup = this.configManagementForm['controls']['overidden'] as FormGroup;
      this.formService.markFormGroupTouched(overridenFormGroup);
      if (overridenFormGroup.valid) {
        this.previewOverideList(overridenFormGroup.value);
      }
    }
  }

  previewOverideList(updatedValues) {
    this.overiddenData = [];
    const keys = Object.keys(updatedValues);
    for (let i = 0; i < keys.length; i++) {
      const obj = {
        key: keys[i],
        value: updatedValues[keys[i]]
      };
      this.overiddenData.push(obj);
    }
  }

  previewEditList (updatedValues) {
    const modifiedValue = [];
    const keys = Object.keys(updatedValues);
    this.previousEditedData = JSON.parse(JSON.stringify(this.originalData));
    this.previousEditedData.properties = [];
    for (let i = 0; i < this.originalData.properties.length; i++) {
      if (this.originalData.properties[i].key === keys[i]) {
          if (this.originalData.properties[i].value !== updatedValues[keys[i]]) {
            const obj = {
              key: keys[i],
              value: updatedValues[keys[i]],
              description: this.originalData.properties[i].description,
              oldValue: this.originalData.properties[i].value
            };
            modifiedValue.push(obj);
            this.previousEditedData.properties.push(obj);
          } else {
            this.previousEditedData.properties.push(this.originalData.properties[i]);
          }
      } else {
        const obj = {
          key: keys[i],
          value: updatedValues[keys[i]]
        };
        modifiedValue.push(obj);
      }

    }
    this.editData.properties = modifiedValue;
    this.showPreview = true;
  }

  backToEditedData() {
    this.editData = this.previousEditedData;
    this.showPreview = false;
  }

  UpdateProperty() {
    this.showUserRemarks = false;
    this.transactionResponse.responseCount = 0;
    this.errorValue = 10;
    if (this.editData.properties.length > 0) {
      this.updateEditedProperty();
    }
    if (this.overiddenData.length > 0) {
      this.updateOveriddenProperty();
    }
  }

  updateOveriddenProperty() {
    this.updateInProgress = true;
    const updateData = [];
    for (let i = 0; i < this.overiddenData.length; i++) {
    updateData.push({
      'application': this.editData.name,
      'configKey': this.overiddenData[i]['key'],
      'configValue': this.overiddenData[i]['value']
    });
  }
  const payload = {
    'configProperties': updateData
  };
  const queryParams = {
    'userMessage': this.userRemarks
  };
  this.postConfigData(payload, queryParams);
  }

  updateEditedProperty() {
    this.updateInProgress = true;
    const updateData = [];
    try {
      const url = environment.updateConfigProperties.url;
      const method = environment.updateConfigProperties.method;
      for (let i = 0; i < this.editData.properties.length; i++) {
        updateData.push({
          'application': this.editData.name,
          'configKey': this.editData.properties[i]['key'],
          'configValue': this.editData.properties[i]['value']
        });
      }
      const payload = {
        'configProperties': updateData
      };
      const queryParams = {
        'userMessage': this.userRemarks
      };
      this.dataSubscription = this.commonResponseService
        .getData(url, method, payload, queryParams)
        .subscribe(
          response => {
              try {
                  this.transactionResponse.updatedConfig.success = true;
                  this.transactionResponse.responseCount++;
              } catch (e) {
                this.transactionResponse.updatedConfig.error = true;
                this.transactionResponse.responseCount++;
                this.errorMessage = 'jsError';
                this.logger.log('error', e);
              }
            },
            error => {
              this.transactionResponse.updatedConfig.error = true;
              this.transactionResponse.responseCount++;
              this.errorMessage = 'apiResponseError';
              this.logger.log('error', error);
            });
          } catch (error) {
            this.logger.log('error', error);
          }
  }

  postConfigData(payload, queryParams) {
    try {
      this.errorValue = 10;
      const url = environment.createConfigProperties.url;
      const method = environment.createConfigProperties.method;
      this.updateSubscription = this.commonResponseService
        .getData(url, method, payload, queryParams)
        .subscribe(
          response => {
              try {
                this.transactionResponse.createdConfig.success = true;
                this.transactionResponse.responseCount++;
              } catch (e) {
                this.transactionResponse.createdConfig.error = true;
                this.transactionResponse.responseCount++;
                this.errorMessage = 'jsError';
                this.logger.log('error', e);
              }
            },
            error => {
              this.transactionResponse.createdConfig.error = true;
              this.transactionResponse.responseCount++;
              this.errorMessage = 'apiResponseError';
              this.logger.log('error', error);
            });
          } catch (error) {
            this.logger.log('error', error);
          }
  }

  cancelEdit() {
    this.showConfBox.isEditPageCancelBtnClick = true;
    this.showConfBox.isConfigColumnModified = true;
    this.showConfBox.resetToView = true;
    this.showConfBox.isOverridenAlreadyExist = false;
    this.confirmationMessage = this.confirmationCondition();
    this.showConfBox.value = true;
  }

  resetEditConfig() {
    this.showPreview = false;
    this.editProperty = false;
    this.editColumnIndex = -1;
    this.overiddenData = [];
    this.overrideFormData = [];
    this.overrideFormGroup = {};
    this.formData = [];
    this.showConfBox.isOverridenAlreadyExist = false;
    this.showConfBox.isConfigColumnModified = false;
    this.showConfBox.confirmDelete = false;
    this.showConfBox.resetToView = false;
    this.deletePayLoadOnhold = {};
  }

  transactionalMessage() {
    if (this.updateInProgress) {
      if (this.transactionResponse.updatedConfig.success) {
        this.transactionObj.type = 'success';
        this.transactionObj.title = 'Success!';
        this.transactionObj.message = 'Succesfully updated configuration data for ' + this.editData.name;
      } else if (this.transactionResponse.updatedConfig.error) {
        this.transactionObj.type = 'error';
        this.transactionObj.title = 'Error!';
        this.transactionObj.message = 'Updating configuration data for ' + this.editData.name;
      }

      if (this.transactionResponse.createdConfig.success) {
        this.transactionObj.obj1.type = 'success';
        this.transactionObj.obj1.title = 'Success!';
        this.transactionObj.obj1.message = 'Succesfully created configuration data for ' + this.editData.name;
      } else if (this.transactionResponse.createdConfig.error) {
        this.transactionObj.obj1.type = 'error';
        this.transactionObj.obj1.title = 'Error!';
        this.transactionObj.obj1.message = 'Creating configuration data for ' + this.editData.name;
      }

      if (this.transactionResponse.rollbackConfig.success) {
        this.transactionObj.type = 'success';
        this.transactionObj.title = 'Success!';
        this.transactionObj.message = 'Succesfully reverted configuration data.';
      } else if (this.transactionResponse.rollbackConfig.error) {
        this.transactionObj.type = 'error';
        this.transactionObj.title = 'Error!';
        this.transactionObj.message = 'Reverting configuration data.';
      }
    }
    return this.transactionObj;
  }

    allMessages() {
      if (this.editData &&  this.editData.properties && this.editData.properties.length > 0 && this.overiddenData.length > 0) {
        if (this.transactionResponse.responseCount >= 2) {
            return this.transactionalMessage();
        }
      } else  {
        return this.transactionalMessage();
      }
    }

  takeActionPostTransaction(event) {
    if (event === 'back') {
      this.errorValue = 1;
      this.backToEditedData();
    } else {
      this.updateComponent();
    }
  }

  confirmationCondition() {
    if ( this.editProperty && this.showConfBox.isOverridenAlreadyExist) {
      return 'This field already exist in ' + this.editData['name'];
    } else if (this.editProperty && this.showConfBox.isConfigColumnModified) {
      return 'Changes are not saved. Are you sure you want to discard your changes?';
    } else if (this.editProperty && this.showConfBox.confirmDelete) {
      return 'Configuration removals may impact the system to come down. Are you sure you want to remove this field?';
    }
    return '';
  }

  continueConfirmBox() {

    if (this.editProperty) {
      // This condition if when confirmation is to ask user to reset the edit.
      if (this.showConfBox.resetToView) {
        this.resetEditConfig();
        if (this.showConfBox.isEditPageCancelBtnClick) {
          this.showConfBox.isEditPageCancelBtnClick = false;
        } else {
          if (this.showConfBox.modified.columnIndex === 1) {
                this.storeFirstLevel(this.filterData[0], this.applyActive1);
              } else if (this.showConfBox.modified.columnIndex === 2) {
                this.storeSecondLevel(this.firstLevelData['children'][this.applyActive2], this.applyActive2);
              } else if (this.showConfBox.modified.columnIndex === 3) {
                this.storeThirdLevel(this.secondaryLevelData['children'][this.applyActive3], this.applyActive3);
              }
        }
        this.showConfBox.value = false;
        this.confirmationMessage = '';
      } else {
        if (this.showConfBox.confirmDelete) {
          this.deleteFieldOnConfirm(this.deletePayLoadOnhold);
        }
      }
    }
  }

  cancelConfirmBox() {
    this.showConfBox.value = false;
    this.confirmationMessage = '';
    this.showConfBox.showContinueBtn = 1;
    if (this.showConfBox.modified.columnIndex === 1) {
     this.applyActive1 = this.firstLevelIndex;
    } else if (this.showConfBox.modified.columnIndex === 2) {
      this.applyActive2 = this.secondLevelIndex;
    } else if (this.showConfBox.modified.columnIndex === 3) {
      this.applyActive3 = this.thirdLevelIndex;
    }
  }

  onOverride(data) {
      for (let i = 0; i < this.editData.properties.length; i++) {
        if (this.editData.properties[i].key === data.key) {
          this.showConfBox.isOverridenAlreadyExist = true;
          this.confirmationMessage = this.confirmationCondition();
          this.showConfBox.value = true;
          this.showConfBox.showContinueBtn = -1;
          return;
        }
      }
      for (let i = 0; i < this.overrideFormData.length; i++) {
        if (this.overrideFormData[i].formControlName === data.key) {
          this.showConfBox.isOverridenAlreadyExist = true;
          this.confirmationMessage = this.confirmationCondition();
          this.showConfBox.value = true;
          this.showConfBox.showContinueBtn = -1;
          return;
        }
      }
      let individualField = {
        formControlName: data.key,
        formControlDisplayName:
        this.refactorFieldsService.getDisplayNameForAKey(
          data.key.toLocaleLowerCase()
        ) || data.key
      };
      if (data.description) {
        individualField = Object.assign({'metadata': data.description}, individualField);
      }
      this.overrideFormData.push(individualField);
      this.overrideFormGroup[data.key] = new FormControl(data.value, Validators.required);
      this.formErrors[data.key] = '';
      this.configManagementForm = new FormGroup ({
        edit: new FormGroup(this.formGroup),
        overidden: new FormGroup(this.overrideFormGroup)
      });
    this.configManagementForm.valueChanges.subscribe(() => {
      this.formErrors = this.formService.validateForm(this.configManagementForm, this.formErrors, true);
    });
    // provide toast message for overridden field
    this.toastObservableService.postMessage('Field added to ' + this.editData.name + ' to override ', 3);
  }

  onDelete(fieldType, input) {

    try {
      this.deletePayLoadOnhold = {
        'fieldType': fieldType,
        'input': input,
        'payload': {
          'application': this.editData.name,
          'configKey': input.formControlName,
          'configValue': this.configManagementForm.controls[fieldType]['controls'][input.formControlName].value
        }
      };

      this.showConfBox.confirmDelete = true;
      this.showConfBox.isOverridenAlreadyExist = false;
      this.showConfBox.isConfigColumnModified = false;
      this.confirmationMessage = this.confirmationCondition();
      this.showConfBox.value = true;

    } catch (error) {
      this.logger.log('error', 'JS Error, Error deleting field');
    }
  }

  onDeleteNotSavedField(fieldType, input) {
    try {
      const childFormGroup: FormGroup =  <FormGroup>this.configManagementForm.get(fieldType);
      childFormGroup.removeControl(input.formControlName);
      this.overrideFormData = this.overrideFormData.filter(item => item.formControlName !== input.formControlName);
    } catch (error) {
      this.logger.log('error', 'js error, error deleting field not saved by user');
    }
  }

  deleteFieldOnConfirm(deleteObj) {

    const options = {
      headers: new HttpHeaders({
      'Content-Type': 'application/json',
      }),
      body: deleteObj['payload']
    };

    this.deleteFieldInProgress = true;
    this.commonResponseService.getData(
      environment.deleteConfigKey.url,
      environment.deleteConfigKey.method,
      {}, {}, options).subscribe(response => {
        this.transactionResponse.deleteConfig.success = true;
        this.deleteActionComplete();

        // Success message will not be required.
        /*if (!this.deleteTransactionResponse) {
          this.deleteTransactionResponse = {};
        }

        this.deleteTransactionResponse.type = 'success';
        this.deleteTransactionResponse.title = 'Success!';
        this.deleteTransactionResponse.message = 'Succesfully deleted configuration: ' + this.deletePayLoadOnhold['payload']['configKey'];*/
      },
      error => {
        this.transactionResponse.deleteConfig.error = true;

        if (!this.deleteTransactionResponse) {
          this.deleteTransactionResponse = {};
        }

        this.deleteTransactionResponse.type = 'error';
        this.deleteTransactionResponse.title = 'Error!';
        this.deleteTransactionResponse.message = 'Deleting configuration ' + this.deletePayLoadOnhold['payload']['configKey'];
        this.logger.log('error', 'Server error deleting the field');
      });
  }

  deleteActionComplete() {
    this.showConfBox.value = false;
    this.deleteFieldInProgress = false;
    this.confirmationMessage = '';
    this.transactionResponse.deleteConfig.success = false;
    this.transactionResponse.deleteConfig.error = false;
    this.deletePayLoadOnhold = {};
    this.deleteTransactionResponse = null;
    this.updateComponent();
  }

  getConfigKeys() {
    try {
      const configUrl = environment.getConfigkeys.url;
      const configMethod = environment.getConfigkeys.method;
      this.configPropertySubscription = this.commonResponseService
        .getData(configUrl, configMethod, {}, {})
        .subscribe(
          response => {
              try {
                if (this.utils.checkIfAPIReturnedDataIsEmpty(response)) {
                  this.configKeys.errorValue = -1;
                  this.configKeys.errorMessage = 'noDataAvailable';
                } else {
                  this.configKeys.errorValue = 1;
                  this.configKeys.value = response;
                }
              } catch (e) {
                this.configKeys.errorValue = -1;
                this.configKeys.errorMessage = 'jsError';
                this.logger.log('error', e);
              }
            },
            error => {
              this.configKeys.errorValue = -1;
              this.configKeys.errorMessage = 'apiResponseError';
              this.logger.log('error', error);
            });
          } catch (error) {
            this.logger.log('error', error);
          }
  }

  createNewField(newObj) {
    this.createNew = false;

    const object = {
      key: Object.keys(newObj)[0],
      value: Object.values(newObj)[0]
    };
    this.onOverride(object);
  }

  getHistory() {
    try {
      const historyUrl = environment.auditTrailConfigProperties.url;
      const historyMethod = environment.auditTrailConfigProperties.method;
      this.configPropertySubscription = this.commonResponseService
        .getData(historyUrl, historyMethod, {}, {})
        .subscribe(
          response => {
              try {
                if (this.utils.checkIfAPIReturnedDataIsEmpty(response.configPropertyAudit)) {
                  this.configAudit.errorValue = -1;
                  this.configAudit.errorMessage = 'noDataAvailable';
                } else {
                  this.configAudit.errorValue = 1;
                  const configData = response.configPropertyAudit;
                  for (let i = 0; i < configData.length; i++) {
                    configData[i] = this.processSelectedHistoryData(configData[i]);
                  }
                  this.configAudit.value = configData;
                }
              } catch (e) {
                this.configAudit.errorValue = -1;
                this.configAudit.errorMessage = 'jsError';
                this.logger.log('error', e);
              }
            },
            error => {
              this.configAudit.errorValue = -1;
              this.configAudit.errorMessage = 'apiResponseError';
              this.logger.log('error', error);
            });
          } catch (error) {
            this.logger.log('error', error);
          }
  }

  getHistoryPreview(selectedCommit) {
    this.activeHistoryTile = selectedCommit.activeIndex;
    this.historyPreview = selectedCommit.value;
    this.showDropdown = false;
    this.showHistoryPreview = true;
  }

  historyDropdownCloseEvent(value) {
    this.showDropdown = value;
  }

  processSelectedHistoryData(data) {
    const historyArray = [];
    const appArray = [];
    const property = data.configPropertyChangeList;
    for (let i = 0; i < property.length; i++) {
      if (appArray.indexOf(property[i].application) !== -1) {
        const index = appArray.indexOf(property[i].application);
        historyArray[index].configProperties.push({
          'configKey': property[i].configKey,
          'newConfigValue': property[i].newConfigValue,
          'oldConfigValue': property[i].oldConfigValue
        });
      } else {
        const appObj = {
          'application': property[i].application,
          configProperties: [{
            'configKey': property[i].configKey,
            'newConfigValue': property[i].newConfigValue,
            'oldConfigValue': property[i].oldConfigValue
          }]
        };
        historyArray.push(appObj);
        appArray.push(property[i].application);
      }
    }
    data.configPropertyChangeList = historyArray;
    return data;
  }

  updateRollback() {
    try {
      this.errorValue = 10;
      this.updateInProgress = true;
      const url = environment.rollbackConfigProperties.url;
      const method = environment.rollbackConfigProperties.method;
      const queryParams = {
        'timestamp': this.historyPreview.auditTimeStamp,
        'userMessage': this.userRemarks
      };
      this.updateSubscription = this.commonResponseService
        .getData(url, method, {}, queryParams)
        .subscribe(
          response => {
              try {
                this.transactionResponse.rollbackConfig.success = true;
                this.transactionResponse.responseCount++;
              } catch (e) {
                this.transactionResponse.rollbackConfig.error = true;
                this.transactionResponse.responseCount++;
                this.errorMessage = 'jsError';
                this.logger.log('error', e);
              }
            },
            error => {
              this.transactionResponse.rollbackConfig.error = true;
              this.transactionResponse.responseCount++;
              this.errorMessage = 'apiResponseError';
              this.logger.log('error', error);
            });
          } catch (error) {
            this.logger.log('error', error);
          }
  }

  resetUserRemarks() {
    this.userRemarks = '';
  }

  ngOnDestroy() {
    try {
      if (this.dataSubscription) {
        this.dataSubscription.unsubscribe();
      }
      if (this.configPropertySubscription) {
        this.configPropertySubscription.unsubscribe();
      }
      if (this.updateSubscription) {
        this.updateSubscription.unsubscribe();
      }
    } catch (error) {
      this.logger.log('error', 'JS Error - ' + error);
    }
  }


}
