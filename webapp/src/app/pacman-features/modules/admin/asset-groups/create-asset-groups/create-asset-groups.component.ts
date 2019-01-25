/*
 *Copyright 2018 T Mobile, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License'); You may not use
 * this file except in compliance with the License. A copy of the License is located at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the 'license' file accompanying this file. This file is distributed on
 * an 'AS IS' BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or
 * implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Component, OnInit, OnDestroy, ViewChild, trigger, state, style, transition, animate } from '@angular/core';
import { environment } from './../../../../../../environments/environment';

import { Router } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import * as _ from 'lodash';
import { UtilsService } from '../../../../../shared/services/utils.service';
import { LoggerService } from '../../../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../../../shared/services/error-handling.service';
import 'rxjs/add/operator/filter';
import 'rxjs/add/operator/pairwise';
import { WorkflowService } from '../../../../../core/services/workflow.service';
import { RouterUtilityService } from '../../../../../shared/services/router-utility.service';
import { AdminService } from '../../../../services/all-admin.service';
import { SelectComponent } from 'ng2-select';
import { UploadFileService } from '../../../../services/upload-file-service';

@Component({
  selector: 'app-admin-create-asset-groups',
  templateUrl: './create-asset-groups.component.html',
  styleUrls: ['./create-asset-groups.component.css'],
  animations: [
    trigger('slideInOut', [
      state('in', style({
        transform: 'translate3d(0, 0, 0)'
      })),
      state('out', style({
        transform: 'translate3d(100%, 0, 0)'
      })),
      transition('in => out', animate('400ms ease-in-out')),
      transition('out => in', animate('400ms ease-in-out'))
    ]),
    trigger('fadeInOut', [
      state('open', style({ 'z-index': 2, opacity: 1 })),
      state('closed', style({ 'z-index': -1, opacity: 0 })),
      transition('open <=> closed', animate('500ms')),
    ])
  ],
  providers: [
    LoggerService,
    ErrorHandlingService,
    UploadFileService,
    AdminService
  ]
})
export class CreateAssetGroupsComponent implements OnInit, OnDestroy {
  @ViewChild('attributeValueElement') attributeValueElement: SelectComponent;
  @ViewChild('targetType') targetTypeElement: SelectComponent;

  pageTitle = 'Create Asset Group';
  breadcrumbArray = ['Admin', 'Asset Groups'];
  breadcrumbLinks = ['policies', 'asset-groups'];
  breadcrumbPresent;
  highlightedText;
  progressText;
  outerArr = [];
  filters = [];
  isGroupNameValid = -1;
  targetTypeValue = [];
  assetForm = {
    dataSourceName: 'aws',
    groupName: '',
    displayName: '',
    type: '',
    createdBy: '',
    description: '',
    visible: true,
    targetTypes: []
  };
  isCreate = false;
  highlightName = '';
  groupName = '';
  assetLoaderTitle = '';
  assetLoader = false;
  assetLoaderFailure = false;
  attributeName = [];
  attributeValue;
  targetTypeSelectedValue = '';
  selectedAttributes = [];

  allOptionalRuleParams = [];
  isAssetGroupFailed = false;
  isAssetGroupSuccess = false;
  ruleContentLoader = true;
  assetGroupLoader = false;
  invocationId = '';
  paginatorSize = 25;
  isLastPage;
  assetGroupNames;
  isFirstPage;
  totalPages;
  pageNumber = 0;
  showLoader = true;
  showWidget = true;
  remainingTargetTypes;
  remainingTargetTypesFullDetails;
  targetTypeAttributeValues = [];
  errorMessage;
  searchTerm = '';

  hideContent = false;
  pageContent = [
    { title: 'Enter Group Details', hide: false, isChanged: false },
    { title: 'Select Domains', hide: true, isChanged: false },
    { title: 'Select Targets', hide: true, isChanged: false },
    { title: 'Configure Attributes', hide: true, isChanged: false }
  ];

  availChoosedItems = {};
  availChoosedSelectedItems = {};
  availChoosedItemsCount = 0;

  selectChoosedItems = {};
  selectChoosedSelectedItems = {};
  selectChoosedItemsCount = 0;

  availableItems = [];
  selectedItems = [];

  availableItemsBackUp = [];
  selectedItemsBackUp = [];

  availableItemsCopy = [];
  selectedItemsCopy = [];

  searchSelectedDomainTerms = '';
  searchAvailableDomainTerms = '';


  // Target Details //
  availTdChoosedItems = {};
  availTdChoosedSelectedItems = {};
  availTdChoosedItemsCount = 0;
  state = 'closed';
  menuState = 'out';
  selectedIndex = -1;
  selectedAttributeDetails= [];
  selectedAttributeIndex = '';

  selectTdChoosedItems = {};
  selectTdChoosedSelectedItems = {};
  selectTdChoosedItemsCount = 0;

  availableTdItems = [];
  selectedTdItems = [];
  selectedTdItemsCopyForPrevNext = [];

  availableTdItemsBackUp = [];
  selectedTdItemsBackUp = [];

  availableTdItemsCopy = [];
  selectedTdItemsCopy = [];

  searchSelectedTargetTerms = '';
  searchAvailableTargetTerms = '';

  stepIndex = 0;
  stepTitle = this.pageContent[this.stepIndex].title;
  allAttributeDetails = [];
  allAttributeDetailsCopy = [];
  allAttributeDetailsCopyForPrevNext = [];
  allSelectedAttributeDetailsCopy = [];

  filterText = {};
  errorValue = 0;
  urlID = '';
  groupId = '';
  successTitleStart = '';
  successTitleEnd = '';

  failedTitleStart = '';
  isAttributeAlreadyAdded = -1;
  failedTitleEnd = '';

  FullQueryParams;
  queryParamsWithoutFilter;
  urlToRedirect = '';
  mandatory;

  public labels;
  private previousUrl = '';
  private routeSubscription: Subscription;
  private getKeywords: Subscription;
  private previousUrlSubscription: Subscription;
  private downloadSubscription: Subscription;

  constructor(
    private router: Router,
    private utils: UtilsService,
    private logger: LoggerService,
    private errorHandling: ErrorHandlingService,
    private workflowService: WorkflowService,
    private routerUtilityService: RouterUtilityService,
    private adminService: AdminService
  ) {

    this.routerParam();
    this.updateComponent();
  }

  ngOnInit() {
    this.urlToRedirect = this.router.routerState.snapshot.url;
  }


  closeAttributeConfigure() {
    this.state = 'closed';
    this.menuState = 'out';
    this.selectedIndex = -1;
  }

  openAttributeConfigure(attributeDetail, index) {
    if (!attributeDetail.includeAll) {
      this.attributeValue = '';
      this.attributeName = [];
      this.state = 'open';
      this.menuState = 'in';
      this.selectedIndex = index;
      console.log('attributeDetail==============>', attributeDetail);
      this.selectedAttributeDetails = attributeDetail.allAttributesName;
      this.selectedAttributes = attributeDetail.attributes;
      this.selectedAttributeIndex = '/aws_' +attributeDetail.targetName+ '/_search?filter_path=aggregations.alldata.buckets.key';
    }
  }

  includeAllAttributes(attributeDetail, index) {
    this.allAttributeDetails[index].includeAll = !this.allAttributeDetails[index].includeAll;
  }

  isGroupNameAvailable(alexaKeyword) {
    if (alexaKeyword.length === 0) {
      this.isGroupNameValid = -1;
    } else {
      const isKeywordExits = this.assetGroupNames.findIndex(item => alexaKeyword.toLowerCase() === item.toLowerCase());
      if (isKeywordExits === -1) {
        this.isGroupNameValid = 1;
      } else {
        this.isGroupNameValid = 0;
      }
    }
  }


  getAllAssetGroupNames() {
    this.hideContent = true;
    this.assetGroupLoader = true;
    this.progressText = 'Loading details';
    this.isAssetGroupFailed = false;
    this.isAssetGroupSuccess = false;
    const url = environment.assetGroupNames.url;
    const method = environment.assetGroupNames.method;
    this.adminService.executeHttpAction(url, method, {}, {}).subscribe(reponse => {
      this.hideContent = false;
      this.assetGroupLoader = false;
      this.showLoader = false;
      this.assetGroupNames = reponse[0];
    },
      error => {
        this.assetGroupNames = [];
        this.errorMessage = 'apiResponseError';
        this.showLoader = false;
      });
  }

  public selectAttributes(value: any): void {
    this.checkAttributeAlreadyTaken(value.text);
  }


  public typedAttributes(value: any): void {
    this.attributeValue = [{ text: value, id: value }];
    this.checkAttributeAlreadyTaken(value);
  }

  checkAttributeAlreadyTaken(attributeValue) {
    const attributeSearchedResult = _.find(this.allAttributeDetails[this.selectedIndex].attributes, { name: this.attributeName[0].text, value: attributeValue });
    if (attributeSearchedResult === undefined) {
      this.isAttributeAlreadyAdded = -1;
    } else {
      this.isAttributeAlreadyAdded = 0;
    }
  }

  public getAttributeValues(value: any): void {
    this.attributeValue = [{ text: value, id: value }];
  }


  getTargetTypeAttributeValues(attributeName) {
    const attrNameObj: any = {};
    attrNameObj.size = 0;
    attrNameObj.aggs = {};
    attrNameObj.aggs.alldata = {};
    attrNameObj.aggs.alldata.terms = {};
    attrNameObj.aggs.alldata.terms.field = attributeName + '.keyword';
    attrNameObj.aggs.alldata.terms.size = 10000;
    this.isAttributeAlreadyAdded = -1;
    this.attributeValueElement.disabled = true;
    this.attributeValueElement.placeholder = 'Loading Values...';
    this.attributeValue = [];
    this.targetTypeAttributeValues = [];
    const url = environment.listTargetTypeAttributeValues.url;
    const method = environment.listTargetTypeAttributeValues.method;
    let queryParams = { index: this.selectedAttributeIndex, payload: JSON.stringify(attrNameObj) };
    console.log('queryParams=============>', queryParams);
    this.adminService.executeHttpAction(url, method, queryParams, {}).subscribe(attributeValues => {
      if (attributeValues.length > 0) {

        if (attributeValues[0].hasOwnProperty('data')) {
          if (attributeValues[0].data.hasOwnProperty('aggregations')) {
            if (attributeValues[0].data.aggregations.alldata.hasOwnProperty('buckets')) {
              const allAttributeValues = attributeValues[0].data.aggregations.alldata.buckets;
              allAttributeValues.forEach((attrValue) => {
                const allCurrentAttributeValues = {};
                allCurrentAttributeValues['text'] = attrValue.key;
                allCurrentAttributeValues['id'] = attrValue.key;
                this.targetTypeAttributeValues.push(allCurrentAttributeValues);
              });
              this.attributeValueElement.items = this.targetTypeAttributeValues;
            }
          }
        }
        this.attributeValueElement.disabled = false;
        this.attributeValueElement.placeholder = 'Select Value';

      } else {
        this.attributeValueElement.disabled = false;
        this.attributeValueElement.placeholder = '';
      }
    },
      error => {
        this.targetTypeAttributeValues = [];
        this.attributeValueElement.disabled = false;
        this.attributeValueElement.placeholder = 'Select Value';
      });
  }

  addTagetType(targetTypeValue) {
    const targetTypeName = targetTypeValue[0].text;
    const targetTypeDetails1 = _.find(this.remainingTargetTypesFullDetails, { targetName: targetTypeName });
    const targetTypeDetails2 = _.find(this.remainingTargetTypes, { id: targetTypeName });
    this.allAttributeDetails.push(targetTypeDetails1);
    const itemIndex2 = this.remainingTargetTypes.indexOf(targetTypeDetails2);
    this.remainingTargetTypes.splice(itemIndex2, 1);
    this.targetTypeElement.items = this.remainingTargetTypes;
    this.targetTypeValue = [];
  }

  addAttributes(attributeName, attributeValue) {
    this.allAttributeDetails[this.selectedIndex].attributes.push({ name: attributeName[0].text, value: attributeValue[0].text });
    this.attributeValue = [];
    this.attributeName = [];
  }

  deleteAttributes(attributeName, itemIndex) {
    this.allAttributeDetails[this.selectedIndex].attributes.splice(itemIndex, 1);
  }

  nextPage() {
    try {
      if (!this.isLastPage) {
        this.pageNumber++;
        this.showLoader = true;
      }
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  prevPage() {
    try {
      if (!this.isFirstPage) {
        this.pageNumber--;
        this.showLoader = true;
      }

    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  nextStep() {
    if (!this.isCreate) {
      this.goToNextStep();
    } else {
      if (this.stepIndex + 1 === 1) {
        if (!this.pageContent[this.stepIndex].isChanged) {
          this.assetLoaderFailure = false;
          this.assetLoader = true;
          this.assetLoaderTitle = 'Domain';
          this.pageContent[0].hide = true;
          const url = environment.domains.url;
          const method = environment.domains.method;
          this.adminService.executeHttpAction(url, method, {}, {}).subscribe(reponse => {
            this.assetLoader = false;
            this.showLoader = false;
            if (reponse !== undefined) {
              this.availableItems = reponse[0];
              this.selectedItems = [];
              this.availableItemsBackUp = _.cloneDeep(this.availableItems);
              this.selectedItemsBackUp = _.cloneDeep(this.selectedItems);
              this.availableItemsCopy = _.cloneDeep(this.availableItems);
              this.selectedItemsCopy = _.cloneDeep(this.selectedItems);
              this.searchAvailableDomains();
              this.searchSelectedDomains();
              this.goToNextStep();
            }
          },
            error => {
              this.assetLoader = false;
              this.assetLoaderFailure = true;
              this.errorValue = -1;
              this.outerArr = [];
              this.errorMessage = 'apiResponseError';
              this.showLoader = false;
            });
        } else {
          this.searchAvailableDomains();
          this.searchSelectedDomains();
          this.goToNextStep();
        }
      } else if (this.stepIndex + 1 === 2) {
        if (!this.pageContent[this.stepIndex].isChanged) {
          this.assetLoaderTitle = 'Target';
          this.assetLoaderFailure = false;
          this.assetLoader = true;
          this.pageContent[1].hide = true;
          const url = environment.targetTypesByDomains.url;
          const method = environment.targetTypesByDomains.method;
          const domainList = this.selectedItems.map(domain => domain.domainName);
          this.adminService.executeHttpAction(url, method, domainList, {}).subscribe(reponse => {
            this.assetLoader = false;
            this.showLoader = false;
            if (reponse !== undefined) {
              this.availableTdItems = reponse[0].data;
              this.selectedTdItems = [];
              if (this.selectedTdItemsCopyForPrevNext.length > 0) {
                this.selectedTdItemsCopyForPrevNext.forEach(tdItem => {
                  const availableTdSearchedResult = _.find(this.availableTdItems, { targetName: tdItem.targetName });
                  const itemIndex = this.availableTdItems.indexOf(availableTdSearchedResult);
                  if (itemIndex !== -1) {
                    this.availableTdItems.splice(itemIndex, 1);
                    this.selectedTdItems.push(availableTdSearchedResult);
                  }
                });
              }
              this.availableTdItemsBackUp = _.cloneDeep(this.availableTdItems);
              this.selectedTdItemsBackUp = _.cloneDeep(this.selectedTdItems);
              this.availableTdItemsCopy = _.cloneDeep(this.availableTdItems);
              this.selectedTdItemsCopy = _.cloneDeep(this.selectedTdItems);
              this.searchAvailableTargets();
              this.searchSelectedTargets();
              this.goToNextStep();
            }
          },
            error => {
              this.assetLoader = false;
              this.assetLoaderFailure = true;
              this.errorValue = -1;
              this.outerArr = [];
              this.errorMessage = 'apiResponseError';
              this.showLoader = false;
            });
        } else {
          this.searchAvailableDomains();
          this.searchSelectedDomains();
          this.goToNextStep();
        }
      } else if (this.stepIndex + 1 === 3) {
        if (!this.pageContent[this.stepIndex].isChanged) {
          this.assetLoaderTitle = 'Target Attributes';
          this.assetLoaderFailure = false;
          this.assetLoader = true;
          this.pageContent[2].hide = true;
          const url = environment.targetTypesAttributes.url;
          const method = environment.targetTypesAttributes.method;
          this.adminService.executeHttpAction(url, method, this.selectedTdItems, {}).subscribe(reponse => {
            this.assetLoader = false;
            this.showLoader = false;
            if (reponse !== undefined) {
              this.allAttributeDetails = reponse[0].data;
              if (this.allAttributeDetailsCopyForPrevNext.length > 0) {
                this.allAttributeDetailsCopyForPrevNext.forEach(attrElement => {
                  const attributeSearchedResult = _.find(this.allAttributeDetails, { targetName: attrElement.targetName });
                  const itemIndex = this.allAttributeDetails.indexOf(attributeSearchedResult);
                  if (itemIndex !== -1) {
                    this.allAttributeDetails[itemIndex] = attrElement;
                  }
                });
              }
              this.allSelectedAttributeDetailsCopy = _.cloneDeep(this.allAttributeDetails);
              this.goToNextStep();
            }
          },
            error => {
              this.assetLoader = false;
              this.assetLoaderFailure = true;
              this.errorValue = -1;
              this.outerArr = [];
              this.errorMessage = 'apiResponseError';
              this.showLoader = false;
            });
          } else {
            this.goToNextStep();
          }
      } else {
        this.goToNextStep();
      }
    }
  }

  goToNextStep() {
    this.pageContent[this.stepIndex].hide = true;
    this.pageContent[this.stepIndex].isChanged = true;
    if (this.isCreate) {
      this.stepIndex++;
    } else {
      this.stepIndex += 3;
    }
    this.stepTitle = this.pageContent[this.stepIndex].title;
    this.pageContent[this.stepIndex].hide = false;
  }

  prevStep() {
    this.assetLoaderFailure = false;
    this.assetLoader = false;
    this.pageContent[this.stepIndex].hide = true;
    if (this.isCreate) {
      this.stepIndex--;
    } else {
      this.stepIndex -= 3;
    }
    this.stepTitle = this.pageContent[this.stepIndex].title;
    this.pageContent[this.stepIndex].hide = false;
    if (this.stepIndex + 1 === 3) {
      this.allAttributeDetailsCopyForPrevNext = _.cloneDeep(this.allAttributeDetails);
    }
    if (this.stepIndex + 1 === 2) {
      this.selectedTdItemsCopyForPrevNext = _.cloneDeep(this.selectedTdItems);
    }
  }

  closeAssetErrorMessage() {
    this.assetLoaderFailure = false;
    this.assetLoader = false;
    this.pageContent[this.stepIndex].hide = false;
  }

  update(newAssetGroupDetails) {
    this.showWidget = false;
    newAssetGroupDetails.targetTypes = this.allAttributeDetails;
    this.highlightedText = newAssetGroupDetails.groupName;
    this.progressText = 'Updating Asset Group';
    this.hideContent = true;
    this.assetGroupLoader = true;
    this.isAssetGroupFailed = false;
    this.isAssetGroupSuccess = false;
    const url = environment.updateAssetGroups.url;
    const method = environment.updateAssetGroups.method;
    this.adminService.executeHttpAction(url, method, newAssetGroupDetails, {}).subscribe(reponse => {
      this.assetGroupLoader = false;
      this.isAssetGroupSuccess = true;
      this.successTitleStart = 'Asset Group';
      this.successTitleEnd = 'has been successfully updated !!!';
    },
      error => {
        this.assetGroupLoader = false;
        this.isAssetGroupFailed = true;
        this.failedTitleStart = 'Failed in updating Asset Group';
        this.failedTitleEnd = '!!!';
      });
  }

  create(newAssetGroupDetails) {
    this.showWidget = false;
    newAssetGroupDetails.targetTypes = this.allAttributeDetails;
    this.highlightedText = newAssetGroupDetails.groupName;
    this.progressText = 'Creating Asset Group';
    this.hideContent = true;
    this.assetGroupLoader = true;
    this.isAssetGroupFailed = false;
    this.isAssetGroupSuccess = false;
    const url = environment.createAssetGroups.url;
    const method = environment.createAssetGroups.method;
    this.adminService.executeHttpAction(url, method, newAssetGroupDetails, {}).subscribe(reponse => {
      this.assetGroupLoader = false;
      this.isAssetGroupSuccess = true;
      this.successTitleStart = 'Asset Group';
      this.successTitleEnd = 'has been successfully created !!!';
    },
      error => {
        this.assetGroupLoader = false;
        this.isAssetGroupFailed = true;
        this.failedTitleStart = 'Failed in creating Asset Group !!!';
        this.failedTitleEnd = '!!!';
      });
  }

  isStepDisabled(stepIndex) {
    if (stepIndex === 0) {
      if (this.assetForm.groupName !== '' && this.assetForm.displayName !== '' &&
        this.assetForm.type !== '' && this.assetForm.createdBy !== '' && this.isGroupNameValid === 1) {
        return false;
      }
    } else if (stepIndex === 1) {
      return (this.selectedItems.length === 0);
    } else if (stepIndex === 2) {
      return (this.selectedTdItems.length === 0);
    }
    return true;
  }

  closeErrorMessage() {
    this.showWidget = true;
    this.isAssetGroupFailed = false;
    this.hideContent = false;
  }

  searchAttribute() {
    const term = this.searchTerm;
    this.allAttributeDetails = this.allSelectedAttributeDetailsCopy.filter(function (tag) {
      return tag.targetName.indexOf(term) >= 0;
    });
  }

  onClickAvailableItem(index, availableItem, key) {
    if (this.availChoosedItems.hasOwnProperty(index)) {
      this.availChoosedItems[index] = !this.availChoosedItems[index];
      if (this.availChoosedItems[index]) {
        this.availChoosedSelectedItems[key] = availableItem;
      } else {
        delete this.availChoosedSelectedItems[key];
      }

    } else {
      this.availChoosedItems[index] = true;
      this.availChoosedSelectedItems[key] = availableItem;
    }
    this.availChoosedItemsCount = Object.keys(this.availChoosedSelectedItems).length;
  }

  onClickSelectedItem(index, selectedItem, key) {
    if (this.selectChoosedItems.hasOwnProperty(index)) {
      this.selectChoosedItems[index] = !this.selectChoosedItems[index];
      if (this.selectChoosedItems[index]) {
        this.selectChoosedSelectedItems[key] = selectedItem;
      } else {
        delete this.selectChoosedSelectedItems[key];
      }
    } else {
      this.selectChoosedItems[index] = true;
      this.selectChoosedSelectedItems[key] = selectedItem;
    }
    this.selectChoosedItemsCount = Object.keys(this.selectChoosedSelectedItems).length;
  }

  moveAllItemsToLeft() {
    this.pageContent[this.stepIndex].isChanged = false;
    this.pageContent[2].isChanged = false;
    if (this.searchSelectedDomainTerms.length === 0) {
      this.availableItems = _.cloneDeep(this.availableItemsBackUp);
      this.availableItemsCopy = _.cloneDeep(this.availableItemsBackUp);
      this.selectedItems = [];
      this.selectedItemsCopy = [];
      this.selectChoosedItems = {};
      this.selectChoosedSelectedItems = {};
      this.selectChoosedItemsCount = 0;
      this.searchAvailableDomains();
      this.searchSelectedDomains();
    } else {
      this.selectChoosedSelectedItems = {};
      this.selectedItems.forEach((element) => {

        this.selectChoosedSelectedItems[element.domainName] = element;
      });
      this.moveItemToLeft();
    }
  }

  moveAllItemsToRight() {
    this.pageContent[this.stepIndex].isChanged = false;
    this.pageContent[2].isChanged = false;
    if (this.searchAvailableDomainTerms.length === 0) {
      this.selectedItems = _.cloneDeep(this.availableItemsBackUp);
      this.selectedItemsCopy = _.cloneDeep(this.availableItemsBackUp);
      this.availableItemsCopy = [];
      this.availableItems = [];
      this.availChoosedItems = {};
      this.availChoosedSelectedItems = {};
      this.availChoosedItemsCount = 0;
      this.searchAvailableDomains();
      this.searchSelectedDomains();
    } else {
      this.availChoosedSelectedItems = {};
      this.availableItems.forEach((element) => {
        this.availChoosedSelectedItems[element.domainName] = element;
      });
      this.moveItemToRight();
    }
  }

  moveItemToRight() {
    this.pageContent[this.stepIndex].isChanged = false;
    this.pageContent[2].isChanged = false;
    const selectedItemsCopy = this.selectedItemsCopy;
    const availableItemsCopy = this.availableItemsCopy;
    for (const choosedSelectedKey in this.availChoosedSelectedItems) {
      if (this.availChoosedSelectedItems.hasOwnProperty(choosedSelectedKey)) {
        selectedItemsCopy.push(this.availChoosedSelectedItems[choosedSelectedKey]);
        const filterIndex = availableItemsCopy.indexOf(this.availChoosedSelectedItems[choosedSelectedKey]);
        availableItemsCopy.splice(filterIndex, 1);
      }
    }

    this.availableItems = availableItemsCopy;
    if (this.searchAvailableDomainTerms.length !== 0) {
      this.searchAvailableDomains();
    }

    this.selectedItems = selectedItemsCopy;
    if (this.searchSelectedDomainTerms.length !== 0) {
      this.searchSelectedDomains();
    }

    this.availChoosedItems = {};
    this.availChoosedSelectedItems = {};
    this.availChoosedItemsCount = 0;
  }

  moveItemToLeft() {
    this.pageContent[this.stepIndex].isChanged = false;
    this.pageContent[2].isChanged = false;
    const selectedItemsCopy = this.selectedItemsCopy;
    const availableItemsCopy = this.availableItemsCopy;
    for (const choosedSelectedKey in this.selectChoosedSelectedItems) {
      if (this.selectChoosedSelectedItems.hasOwnProperty(choosedSelectedKey)) {
        availableItemsCopy.push(this.selectChoosedSelectedItems[choosedSelectedKey]);
        const filterIndex = selectedItemsCopy.indexOf(this.selectChoosedSelectedItems[choosedSelectedKey]);
        selectedItemsCopy.splice(filterIndex, 1);
      }
    }

    this.availableItems = availableItemsCopy;
    if (this.searchAvailableDomainTerms.length !== 0) {
      this.searchAvailableDomains();
    }

    this.selectedItems = selectedItemsCopy;
    if (this.searchSelectedDomainTerms.length !== 0) {
      this.searchSelectedDomains();
    }

    this.selectChoosedItems = {};
    this.selectChoosedSelectedItems = {};
    this.selectChoosedItemsCount = 0;
  }


  searchAvailableDomains() {
    const term = this.searchAvailableDomainTerms;
    this.availableItems = this.availableItemsCopy.filter(function (tag) {
      return tag.domainName.toLowerCase().indexOf(term.toLowerCase()) >= 0;
    });
  }

  searchSelectedDomains() {
    const term = this.searchSelectedDomainTerms;
    this.selectedItems = this.selectedItemsCopy.filter(function (tag) {
      return tag.domainName.toLowerCase().indexOf(term.toLowerCase()) >= 0;
    });
  }
  /*
  *
        TARGET DETAILS
  *
  */
  onClickAvailableTdItem(index, availableItem, key) {
    if (this.availTdChoosedItems.hasOwnProperty(index)) {
      this.availTdChoosedItems[index] = !this.availTdChoosedItems[index];
      if (this.availTdChoosedItems[index]) {
        this.availTdChoosedSelectedItems[key] = availableItem;
      } else {
        delete this.availTdChoosedSelectedItems[key];
      }

    } else {
      this.availTdChoosedItems[index] = true;
      this.availTdChoosedSelectedItems[key] = availableItem;
    }
    this.availTdChoosedItemsCount = Object.keys(this.availTdChoosedSelectedItems).length;
  }

  onClickSelectedTdItem(index, selectedItem, key) {
    if (this.selectTdChoosedItems.hasOwnProperty(index)) {
      this.selectTdChoosedItems[index] = !this.selectTdChoosedItems[index];
      if (this.selectTdChoosedItems[index]) {
        this.selectTdChoosedSelectedItems[key] = selectedItem;
      } else {
        delete this.selectTdChoosedSelectedItems[key];
      }
    } else {
      this.selectTdChoosedItems[index] = true;
      this.selectTdChoosedSelectedItems[key] = selectedItem;
    }
    this.selectTdChoosedItemsCount = Object.keys(this.selectTdChoosedSelectedItems).length;
  }

  moveTdAllItemsToLeft() {
    this.pageContent[this.stepIndex].isChanged = false;
    if (this.searchSelectedTargetTerms.length === 0) {
      this.availableTdItems = _.cloneDeep(this.availableTdItemsBackUp);
      this.availableTdItemsCopy = _.cloneDeep(this.availableTdItemsBackUp);
      this.selectedTdItems = [];
      this.selectedTdItemsCopy = [];
      this.selectTdChoosedItems = {};
      this.selectTdChoosedSelectedItems = {};
      this.selectTdChoosedItemsCount = 0;
      this.searchAvailableTargets();
      this.searchSelectedTargets();
    } else {
      this.selectTdChoosedSelectedItems = {};
      this.selectedTdItems.forEach((element) => {

        this.selectTdChoosedSelectedItems[element.targetName] = element;
      });
      this.moveTdItemToLeft();
    }
  }

  moveTdAllItemsToRight() {
    this.pageContent[this.stepIndex].isChanged = false;
    if (this.searchAvailableTargetTerms.length === 0) {
      this.selectedTdItems = _.cloneDeep(this.availableTdItemsBackUp);
      this.selectedTdItemsCopy = _.cloneDeep(this.availableTdItemsBackUp);
      this.availableTdItemsCopy = [];
      this.availableTdItems = [];
      this.availTdChoosedItems = {};
      this.availTdChoosedSelectedItems = {};
      this.availTdChoosedItemsCount = 0;
      this.searchAvailableTargets();
      this.searchSelectedTargets();
    } else {
      this.availTdChoosedSelectedItems = {};
      this.availableTdItems.forEach((element) => {
        this.availTdChoosedSelectedItems[element.targetName] = element;
      });
      this.moveTdItemToRight();
    }
  }

  moveTdItemToRight() {
    this.pageContent[this.stepIndex].isChanged = false;
    const selectedTdItemsCopy = this.selectedTdItemsCopy;
    const availableTdItemsCopy = this.availableTdItemsCopy;
    for (const choosedTdSelectedKey in this.availTdChoosedSelectedItems) {
      if (this.availTdChoosedSelectedItems.hasOwnProperty(choosedTdSelectedKey)) {
        selectedTdItemsCopy.push(this.availTdChoosedSelectedItems[choosedTdSelectedKey]);
        const filterIndex = availableTdItemsCopy.indexOf(this.availTdChoosedSelectedItems[choosedTdSelectedKey]);
        availableTdItemsCopy.splice(filterIndex, 1);
      }
    }

    this.availableTdItems = availableTdItemsCopy;
    if (this.searchAvailableTargetTerms.length !== 0) {
      this.searchAvailableTargets();
    }

    this.selectedTdItems = selectedTdItemsCopy;
    if (this.searchSelectedTargetTerms.length !== 0) {
      this.searchSelectedTargets();
    }

    this.availTdChoosedItems = {};
    this.availTdChoosedSelectedItems = {};
    this.availTdChoosedItemsCount = 0;
  }

  moveTdItemToLeft() {
    this.pageContent[this.stepIndex].isChanged = false;
    const selectedTdItemsCopy = this.selectedTdItemsCopy;
    const availableTdItemsCopy = this.availableTdItemsCopy;
    for (const choosedTdSelectedKey in this.selectTdChoosedSelectedItems) {
      if (this.selectTdChoosedSelectedItems.hasOwnProperty(choosedTdSelectedKey)) {
        availableTdItemsCopy.push(this.selectTdChoosedSelectedItems[choosedTdSelectedKey]);
        const filterIndex = selectedTdItemsCopy.indexOf(this.selectTdChoosedSelectedItems[choosedTdSelectedKey]);
        selectedTdItemsCopy.splice(filterIndex, 1);
      }
    }

    this.availableTdItems = availableTdItemsCopy;
    if (this.searchAvailableTargetTerms.length !== 0) {
      this.searchAvailableTargets();
    }

    this.selectedTdItems = selectedTdItemsCopy;
    if (this.searchSelectedTargetTerms.length !== 0) {
      this.searchSelectedTargets();
    }

    this.selectTdChoosedItems = {};
    this.selectTdChoosedSelectedItems = {};
    this.selectTdChoosedItemsCount = 0;
  }


  searchAvailableTargets() {
    const term = this.searchAvailableTargetTerms;
    this.availableTdItems = this.availableTdItemsCopy.filter(function (tag) {
      return tag.targetName.toLowerCase().indexOf(term.toLowerCase()) >= 0;
    });
  }

  searchSelectedTargets() {
    const term = this.searchSelectedTargetTerms;
    this.selectedTdItems = this.selectedTdItemsCopy.filter(function (tag) {
      return tag.targetName.toLowerCase().indexOf(term.toLowerCase()) >= 0;
    });
  }


  getAssetGroupDetails() {
    this.hideContent = true;
    this.assetGroupLoader = true;
    this.progressText = 'Loading';
    this.isAssetGroupFailed = false;
    this.isAssetGroupSuccess = false;
    this.isGroupNameValid = 1;
    const url = environment.assetGroupDetailsById.url;
    const method = environment.assetGroupDetailsById.method;
    this.adminService.executeHttpAction(url, method, {}, { assetGroupId: this.groupId, dataSource: 'aws' }).subscribe(assetGroupReponse => {
      this.hideContent = false;
      this.assetGroupLoader = false;
      this.isAssetGroupSuccess = false;
      this.allAttributeDetails = assetGroupReponse[0];
      this.allSelectedAttributeDetailsCopy = assetGroupReponse[0];
      this.assetForm = {
        dataSourceName: 'aws',
        groupName: assetGroupReponse[0].groupName,
        displayName: assetGroupReponse[0].displayName,
        type: assetGroupReponse[0].type,
        createdBy: assetGroupReponse[0].createdBy,
        description: assetGroupReponse[0].description,
        visible: assetGroupReponse[0].visible,
        targetTypes: assetGroupReponse[0].targetTypes
      };

      this.allAttributeDetails = assetGroupReponse[0].targetTypes;
      this.allSelectedAttributeDetailsCopy = assetGroupReponse[0].targetTypes;
      this.remainingTargetTypes = assetGroupReponse[0].remainingTargetTypes;
      this.remainingTargetTypesFullDetails = assetGroupReponse[0].remainingTargetTypesFullDetails;
    },
      error => {
        this.assetGroupLoader = false;
        this.isAssetGroupFailed = true;
        this.failedTitleStart = 'Failed in loading Asset Group';
        this.failedTitleEnd = '!!!';
      });
  }

  /*
    * This function gets the urlparameter and queryObj
    *based on that different apis are being hit with different queryparams
    */
  routerParam() {
    try {
      // this.filterText saves the queryparam
      const currentQueryParams = this.routerUtilityService.getQueryParametersFromSnapshot(this.router.routerState.snapshot.root);
      if (currentQueryParams) {

        this.FullQueryParams = currentQueryParams;
        this.groupId = this.FullQueryParams.groupId;
        this.groupName = this.FullQueryParams.groupName;
        this.queryParamsWithoutFilter = JSON.parse(JSON.stringify(this.FullQueryParams));
        delete this.queryParamsWithoutFilter['filter'];
        if (this.groupId) {
          this.pageTitle = 'Edit Asset Group';
          this.breadcrumbPresent = 'Edit Asset Group';
          this.isCreate = false;
          this.highlightName = this.groupName;
          this.highlightedText = this.groupName;
          this.getAssetGroupDetails();
          this.stepIndex = 0;
          this.pageContent[0].hide = true;
          this.pageContent[1].hide = true;
          this.pageContent[2].hide = true;

          this.pageContent[this.stepIndex].hide = false;
          this.stepTitle = 'Update Group Details - ' + this.groupName;
        } else {
          this.getAllAssetGroupNames();
          this.pageTitle = 'Create Asset Group';
          this.breadcrumbPresent = 'Create Asset Group';
          this.isCreate = true;
        }
        /**
         * The below code is added to get URLparameter and queryparameter
         * when the page loads ,only then this function runs and hits the api with the
         * filterText obj processed through processFilterObj function
         */
        this.filterText = this.utils.processFilterObj(
          this.FullQueryParams
        );

        this.urlID = this.FullQueryParams.TypeAsset;
        // check for mandatory filters.
        if (this.FullQueryParams.mandatory) {
          this.mandatory = this.FullQueryParams.mandatory;
        }

      }
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  /**
   * This function get calls the keyword service before initializing
   * the filter array ,so that filter keynames are changed
   */

  updateComponent() {
    this.outerArr = [];
    this.showLoader = true;
    this.errorValue = 0;
  }

  navigateBack() {
    try {
      this.workflowService.goBackToLastOpenedPageAndUpdateLevel(this.router.routerState.snapshot.root);
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  ngOnDestroy() {
    try {
      if (this.routeSubscription) {
        this.routeSubscription.unsubscribe();
      }
      if (this.previousUrlSubscription) {
        this.previousUrlSubscription.unsubscribe();
      }
    } catch (error) {
      this.logger.log('error', '--- Error while unsubscribing ---');
    }
  }
}
