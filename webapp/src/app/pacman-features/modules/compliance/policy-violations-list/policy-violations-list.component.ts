import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { Subscription } from 'rxjs/Subscription';

import * as _ from 'lodash';

import { WorkflowService } from './../../../../core/services/workflow.service';
import { LoggerService } from '../../../../shared/services/logger.service';
import { DownloadService } from '../../../../shared/services/download.service';
import { CommonResponseService } from '../../../../shared/services/common-response.service';
import { DataCacheService } from './../../../../core/services/data-cache.service';
import { FilterManagementService } from '../../../../shared/services/filter-management.service';
import { ErrorHandlingService } from './../../../../shared/services/error-handling.service';
import { AssetGroupObservableService } from './../../../../core/services/asset-group-observable.service';
import { DomainTypeObservableService } from './../../../../core/services/domain-type-observable.service';
import { UtilsService } from './../../../../shared/services/utils.service';
import { RouterUtilityService } from './../../../../shared/services/router-utility.service';
import { RefactorFieldsService } from './../../../../shared/services/refactor-fields.service';
import { environment } from './../../../../../environments/environment';
import { IssueListingService } from './../../../services/issue-listing.service';

@Component({
  selector: 'app-policy-violations-list',
  templateUrl: './policy-violations-list.component.html',
  styleUrls: ['./policy-violations-list.component.css']
})
export class PolicyViolationsListComponent implements OnInit, OnDestroy {
  assetGroupSubscription: Subscription;
  domainSubscription: Subscription;
  issueListingSubscription: Subscription;
  selectedAssetGroup: string;
  firstPaginator = 1;
  currentPointer = 0;
  selectedDomain: string;
  outerArr: any = [];
  filterText: any;
  seekdata = false;
  currentBucket: any = [];
  allColumns: any = [];
  lastPaginator: number;
  pageTitle = 'Policy Violations';
  breadcrumbDetails = {
    breadcrumbArray: ['Compliance'],
    breadcrumbLinks: ['compliance-dashboard'],
    breadcrumbPresent: 'PolicyViolations'
  };
  backButtonRequired: boolean;
  pageLevel = 0;
  errorMessage = 'apiResponseError';
  errorValue = 0;
  agAndDomain = {};

  isFilterRquiredOnPage = true;
  appliedFilters = {
    queryParamsWithoutFilter: {} /* Stores the query parameter ibject without filter */,
    pageLevelAppliedFilters: {} /* Stores the query parameter ibject without filter */
  };
  filterArray = []; /* Stores the page applied filter array */

  bucketNumber = 0;
  paginatorSize = 25; // How many rows to be shown in one table
  searchTxt; // Text searched by user
  issueListingdata; // to store fetched data
  totalRows;     // paginationData
  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private workflowService: WorkflowService,
    private logger: LoggerService,
    private downloadService: DownloadService,
    private commonResponseService: CommonResponseService,
    private filterManagementService: FilterManagementService,
    private errorHandling: ErrorHandlingService,
    private assetGroupObservableService: AssetGroupObservableService,
    private domainObservableService: DomainTypeObservableService,
    private utils: UtilsService,
    private routerUtilityService: RouterUtilityService,
    private issueListingService: IssueListingService
  ) {
    this.subscribeToAssetGroup();
    this.subscribeToDomain();
  }

  ngOnInit() {
    this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(
      this.pageLevel
    );
  }

  subscribeToAssetGroup() {
    this.assetGroupSubscription = this.assetGroupObservableService
      .getAssetGroup()
      .subscribe(assetGroup => {
        if (assetGroup) {
          this.agAndDomain['ag'] = assetGroup;
        }
      });
  }

  subscribeToDomain() {
    this.domainSubscription = this.domainObservableService
      .getDomainType()
      .subscribe(domain => {
        if (domain) {
          this.agAndDomain['domain'] = domain;
        }

        this.reset();
        this.init();
        this.updateComponent();
      });
  }

  reset() {
    /* Reset the page */
    this.filterArray = [];
  }

  init() {
    /* Initialize */
    this.routerParam();
  }

  updateComponent() {
    /* Updates the whole component */
    this.searchTxt = '';
    this.getData();
  }
  searchTrigger() {
    this.bucketNumber = 0;
    this.currentBucket = [];
    this.getData();
  }
  searchEntered(search) {
    this.searchTxt = search;
  }
  getData() {
    try {
      if (this.issueListingSubscription) {
        this.issueListingSubscription.unsubscribe();
      }

      this.errorValue = 0;

      // if (this.currentBucket[this.currentPointer]) {
      //   this.issueListingdata = this.currentBucket[this.currentPointer];
      // } else {

      const filterToBePassed = this.getFilterObject(this.filterArray);
      filterToBePassed['domain'] = this.agAndDomain['domain'];
      if (!filterToBePassed['include_exempt']) {
        filterToBePassed['include_exempt'] = 'yes';
      }
      const payload = {
        ag: this.agAndDomain['ag'],
        filter: filterToBePassed,
        from: this.bucketNumber * this.paginatorSize,
        searchtext: this.searchTxt,
        size: this.paginatorSize
      };

      this.issueListingSubscription = this.commonResponseService
        .getData( environment.issueListing.url, environment.issueListing.method, payload, {})
        .subscribe(
          response => {
            if (!response || !this.utils.checkIfAPIReturnedDataIsEmpty(response.data.response)) {

              // Data is available
              this.errorValue = 1;
              this.issueListingdata = this.massageData(response.data.response);
              this.totalRows = response.data.total;
              this.firstPaginator =
                this.bucketNumber * this.paginatorSize + 1;
              this.lastPaginator =
                this.bucketNumber * this.paginatorSize + this.paginatorSize;
              this.currentPointer = this.bucketNumber;
              if (this.lastPaginator > this.totalRows) {
                this.lastPaginator = this.totalRows;
              }
              this.currentBucket[this.bucketNumber] = this.issueListingdata;
            } else {
              // Data not available
              // This error message code should change beased on whther it triggered on page load or through search
              this.errorMessage = 'noDataAvailable'; // OR 'dataTableMessage';
              this.logger.log('info', 'No data available');
              this.errorValue = -1;

            }
          },
          error => {
            this.errorMessage = error;
            this.errorValue = -1;
            this.logger.log('error', error);
            this.errorMessage = 'apiResponseError';
          }
        );
      // }
    } catch (error) {
      this.errorValue = -1;
      this.logger.log('error', error);
      this.errorMessage = 'jsError';
    }
  }


  searchCalled(search) {
    this.searchTxt = search;
  }
  prevPg() {
    try {
      this.currentPointer--;
      this.issueListingdata = this.currentBucket[this.currentPointer];
      this.firstPaginator = this.currentPointer * this.paginatorSize + 1;
      this.lastPaginator =
        this.currentPointer * this.paginatorSize + this.paginatorSize;
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }
  nextPg() {
    try {
      if (this.currentPointer < this.bucketNumber) {
        this.currentPointer++;
        this.issueListingdata = this.currentBucket[this.currentPointer];
        this.firstPaginator = this.currentPointer * this.paginatorSize + 1;
        this.lastPaginator =
          this.currentPointer * this.paginatorSize + this.paginatorSize;
        if (this.lastPaginator > this.totalRows) {
          this.lastPaginator = this.totalRows;
        }
      } else {
        this.bucketNumber++;
        this.getData();
      }
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }
  handlePopClick(rowText) {
    const fileType = 'csv';

    try {

        let queryParams;

        queryParams = {
          fileFormat: 'csv',
          serviceId: 1,
          fileType: fileType
        };

        const filterToBePassed = this.filterText;
        filterToBePassed.domain = this.selectedDomain;

        const downloadRequest = {
          ag: this.selectedAssetGroup,
          filter: filterToBePassed,
          from: 0,
          searchtext: '',
          size: this.totalRows
        };

        const downloadUrl = environment.download.url;
        const downloadMethod = environment.download.method;

        this.downloadService.requestForDownload(
            queryParams,
            downloadUrl,
            downloadMethod,
            downloadRequest,
            'Open Violations',
            this.totalRows
          );
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  massageData(data) {
    for (let i = 0; i < data.length; i++) {
      delete data[i]['nonDisplayableAttributes'];
    }
    return data;
  }

  getFilterObject(filterArray) {

    // Convert filters array to filter object to be passed in api payload

    const filterText = this.utils.arrayToObject(
        this.filterArray,
        'filterkey',
        'value'
    ); // <-- TO update the queryparam which is passed in the filter of the api

    return filterText;
  }

  routerParam() {
    try {
      const currentQueryParams = this.routerUtilityService.getQueryParametersFromSnapshot(
        this.router.routerState.snapshot.root
      );

      if (currentQueryParams) {
        this.appliedFilters.queryParamsWithoutFilter = JSON.parse(
          JSON.stringify(currentQueryParams)
        );
        delete this.appliedFilters.queryParamsWithoutFilter['filter'];

        this.appliedFilters.pageLevelAppliedFilters = this.utils.processFilterObj(
          currentQueryParams
        );

        this.filterArray = this.filterManagementService.getFilterArray(
          this.appliedFilters.pageLevelAppliedFilters
        );
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
    this.appliedFilters.pageLevelAppliedFilters = this.utils.makeFilterObj(
      this.appliedFilters.pageLevelAppliedFilters
    );

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

    this.router
      .navigate([], {
        relativeTo: this.activatedRoute,
        queryParams: updatedFilters
      })
      .then(success => {
        this.routerParam();
      });
  }

  navigateBack() {
    try {
      this.workflowService.goBackToLastOpenedPageAndUpdateLevel(
        this.router.routerState.snapshot.root
      );
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  ngOnDestroy() {
    try {
      if (this.assetGroupSubscription) {
        this.assetGroupSubscription.unsubscribe();
      }
      if (this.domainSubscription) {
        this.domainSubscription.unsubscribe();
      }
    } catch (error) {
      this.logger.log('error', 'JS Error - ' + error);
    }
  }
}
