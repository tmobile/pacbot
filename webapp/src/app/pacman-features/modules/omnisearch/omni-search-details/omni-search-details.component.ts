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
import { DataCacheService } from '../../../../core/services/data-cache.service';
import { Router, ActivatedRoute } from '@angular/router';
import { AssetGroupObservableService } from '../../../../core/services/asset-group-observable.service';
import { OmniSearchDataService } from '../../../services/omni-search-data.service';
import { environment } from './../../../../../environments/environment';
import { Subscription } from 'rxjs/Subscription';
import { AutorefreshService } from '../../../services/autorefresh.service';
import { LoggerService } from '../../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../../shared/services/error-handling.service';
import { DatepickerOptions } from 'ng2-datepicker';
import {
  trigger,
  state,
  style,
  transition,
  animate
} from '@angular/animations';
import { WorkflowService } from '../../../../core/services/workflow.service';
import { UtilsService } from '../../../../shared/services/utils.service';
import { DomainTypeObservableService } from '../../../../core/services/domain-type-observable.service';
import { ICONS } from './../../../../shared/constants/icons-mapping';
import {
  FormControl,
  FormGroup,
  FormBuilder,
  Validators
} from '@angular/forms';

@Component({
  selector: 'app-omni-search-details',
  templateUrl: './omni-search-details.component.html',
  styleUrls: ['./omni-search-details.component.css'],
  providers: [
    OmniSearchDataService,
    AutorefreshService,
    LoggerService,
    ErrorHandlingService
  ],
  animations: []
})

export class OmniSearchDetailsComponent implements OnInit, OnDestroy {
  /*
    ***************  Component details  **********************
    * @author Trinanjan
   * @desc add description here about the component
    ************ Component details ends here  ******************
   */
  dropdownData = []; // -> Stores the data for dropdown in array
  filterData = {}; // -> filter data is stored to paint the entire filter
  filterQuery = {}; // -> gets the applied filter from the main filter component
  searchResultsData = []; // ->results cards data in array
  searchText: any = ''; // -> search text entered/typed
  searchboxValueSelected: any = ''; // -> search category selected
  dataSubscription: Subscription; // -> subscription for results card + (results + filter)
  resultsDataSubscription: Subscription; // -> subscription for only results card
  subscriptionToAssetGroup: Subscription; // -> subscription for assetgroup
  omniSearchCategorySubscription: Subscription; // -> subscription for omni search category
  routeSubscription: Subscription; // -> router subscriptionbs
  subscriptionDomain: Subscription; // ->subscription for domain
  filterSubscription: Subscription; // --> filter subscription
  selectedAssetGroup: string; // -> asset group selected
  selectedDomain: any; // -> domain group selected
  errorMessage: any; // -> error messages
  // variables to show hide based on empty data or error
  dataComing = true;
  showLoader = true;
  seekdata = false;
  // autorefresh variables
  durationParams: any;
  autoRefresh: boolean;
  datacoming;
  options: DatepickerOptions = {
    displayFormat: 'MMM D[,] YYYY',
    minDate: new Date()
  };
  autorefreshInterval;
  filterPresent = false; // -> To show and hide filter block
  stopPreviousDataSubscription = false; // -> this flag is used to cancel previous data subscription when multiple filter values are clicked
  numOfCardShown = 50; // -> To show number of cards appeared in the view(infinite scroll)
  totalNumOfCards: number; // -> To show total num of results
  infiniteScrollCalled = false; // -> to call the infiniteScroll
  // variables used in back Btn
  urlToRedirect: any = ''; // ->
  pageLevel = 0; // ->
  SearchDataFromCache; // <-- used to get and store omnisearch data from the session storage
  showOverlay = false; // -> to show an overlay when filter is opened
  filterClicked = false; // -> to know if any of the filter value is clicked
  tagetTypeImagePath; // ->  used to show targettype image in results card
  terminatedIsChecked = false; // -> used to store the value of terminated check box
  pageLoad = false; // -> To know if the page is loaded first time(reqired in cacheinf)
  searchClicked = true; // -> to detect if the search is clicked (based on this resultsDataSubscription is getting called)
  filterDataIsRequested = true; // whenever searchbtn is clicked we pass this variable to main filter to show loader
  user: FormGroup; // Formgroup added for mandatory fields to be verified.

  constructor(
    private omniSearchDataService: OmniSearchDataService,
    private dataStore: DataCacheService,
    private activatedRoute: ActivatedRoute,
    private assetGroupObservableService: AssetGroupObservableService,
    private autorefreshService: AutorefreshService,
    private logger: LoggerService,
    private errorHandling: ErrorHandlingService,
    private workflowService: WorkflowService,
    private router: Router,
    private utils: UtilsService,
    private domainObservableService: DomainTypeObservableService
  ) {
    try {
      this.getRuleId();
      // asset group subscription
      this.subscriptionToAssetGroup = this.assetGroupObservableService
        .getAssetGroup()
        .subscribe(assetGroupName => {
          this.selectedAssetGroup = assetGroupName;
        });
      // domain subscription
      this.subscriptionDomain = this.domainObservableService
        .getDomainType()
        .subscribe(domain => {
          this.selectedDomain = domain;
          // getting the omni search category from the session storage
          if (this.dataStore.get('OmniSearchCategories')) {
            this.dropdownData = JSON.parse(
              this.dataStore.get('OmniSearchCategories')
            );
            if (this.utils.isObjectEmpty(this.dropdownData)) {
              this.setOmniSearchCategory();
            }
          } else {
            this.setOmniSearchCategory();
          }
          // caliing update component
          this.updateComponent();
        });
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  ngOnInit() {
    try {
      // saving url
      this.urlToRedirect = this.router.routerState.snapshot.url;
      this.durationParams = this.autorefreshService.getDuration();
      // calling autorefresh
      this.durationParams = parseInt(this.durationParams, 10);
      this.autoRefresh = this.autorefreshService.autoRefresh;
      if (this.autoRefresh !== undefined) {
        if (
          this.autoRefresh === true ||
          this.autoRefresh.toString() === 'true'
        ) {
          this.autorefreshInterval = setInterval(function () {
            this.fetchOmniSearchData();
          }, this.durationParams);
        }
      }
      this.user = new FormGroup({
        name: new FormControl('', [
          Validators.required,
          Validators.minLength(1)
        ])
      });

    } catch (error) {
      this.logger.log('error', error);
    }
  }

  getRuleId() {
    /*
    * this funtion stores the URL params
    */
    try {
      this.routeSubscription = this.activatedRoute.params.subscribe(params => {
        this.searchboxValueSelected = params.filterValue;
        this.searchText = params.searchText;
      });
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  updateComponent() {
    /* All functions variables which are required to be set for component to be reloaded should go here */
    try {
      if (!this.infiniteScrollCalled) {
        this.showLoader = true;
        this.dataComing = false;
        this.seekdata = false;
      }
      this.getData();
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  /* Function to get Data */
  getData() {
    /* All functions to get data should go here */
    try {
      // get the terminated-cliked/omnisearchLastAppliedFilter from cacheing(needed for cacheing)
      if (
        !(
          this.dataStore.get('terminated-cliked') === undefined ||
          this.dataStore.get('terminated-cliked') === 'undefined'
        )
      ) {
        this.terminatedIsChecked = JSON.parse(
          this.dataStore.get('terminated-cliked')
        );
      } else {
        this.terminatedIsChecked = false;
      }
      if (
        !(
          this.dataStore.get('omnisearchLastAppliedFilter') === undefined ||
          this.dataStore.get('omnisearchLastAppliedFilter') === 'undefined'
        )
      ) {
        this.filterQuery = JSON.parse(
          this.dataStore.get('omnisearchLastAppliedFilter')
        );
      }
      // get the searchdata from cache(returns no data if not available)
      // searchtext,search category , last filter applied , doamin , asset group and terminated-clicked are the parameters to store and retrieve data
      this.SearchDataFromCache = this.dataStore.getOmniSeachData(
        this.searchText,
        this.searchboxValueSelected,
        this.selectedAssetGroup,
        this.selectedDomain,
        this.terminatedIsChecked,
        this.filterQuery
      );
      // Based on if the data is available or not in the cacheing either call apis or show existing data
      if (!this.pageLoad) {
        if (
          this.SearchDataFromCache.toString().toLowerCase() === 'no data' ||
          this.SearchDataFromCache.data === undefined
        ) {
          this.fetchOmniSearchData();
          if (!this.utils.isObjectEmpty(this.filterQuery)) {
            this.processFilterOptions(this.filterQuery);
          } else {
            this.fetchFilterdata();
          }
        } else {
          this.processHttpResponse(this.SearchDataFromCache);
          if (!this.utils.isObjectEmpty(this.filterQuery)) {
            this.processFilterOptions(this.filterQuery);
          } else {
            this.fetchFilterdata();
          }
        }
      } else {
        /* When user has not clicked on filter option */
        if (!this.filterClicked) {
          this.resetSearch();
          this.resetResults();
          this.resetFilter();
        }
        this.fetchOmniSearchData();
      }
      // change the pageload variable to true after first time.it indicated that the page is not loading
      this.pageLoad = true;
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  // updateRequestPayload(rowDetails) {
  //   this.cbArr = [{'Issue ID': {text: rowDetails._id }, 'Status': {text: rowDetails.issueStatus}}];
  // }
  fetchOmniSearchData() {
    /**
     * Omnisearch subscription is divided into 2 parts
     * This is done to load data faster as it takes more time to return fitler + results
     * First part--> fetchResultData -->This is for results and runs everytime filter is clicked/search btn is clicked/
     * Second part--> fetchFilterdata --> This is to fetch filter data and runs on pageload or search btn click
     */

    if (this.resultsDataSubscription) {
      this.resultsDataSubscription.unsubscribe();
    }
    this.stopPreviousDataSubscription = true;
    // this subscription holds data for results
    this.fetchResultData();
    // this subscription holds data for filter
    // on page load filterQuery is empty and on search btn click we are making filterQuery empty obj
    /* Get filter data when it is not available */
    if (
      Object.keys(this.filterQuery).length === 0 &&
      this.filterQuery.constructor === Object
    ) {
      this.fetchFilterdata();
    }
  }
  // **************************** results data subscription code starts here  *************************** */
  /**
   * @func fetchResultData
   * @desc this funtions calls the search api to get results only
   * doNotReturnFilter: true, to get only results data
   */
  fetchResultData() {

    const omniSearchUrl = environment.omniSearch.url;
    const omniSearchMethod = environment.omniSearch.method;
    let omniSearchPayload;
    if (
      Object.keys(this.filterQuery).length === 0 &&
      this.filterQuery.constructor === Object
    ) {
      // if the infinite scroll is called filter is not hidden

      if (this.infiniteScrollCalled) {
        this.filterPresent = true;
      } else {
        this.filterPresent = false;
      }

      // payload when particular option is selected in the dropdown
      omniSearchPayload = {
        ag: this.selectedAssetGroup,
        domain: this.selectedDomain,
        doNotReturnFilter: true,
        includeAllAssets: this.terminatedIsChecked,
        from: 0,
        searchText: this.searchText,
        size: this.numOfCardShown,
        filter: {
          groupBy: {
            type: 'searchFilterAttributeGroup',
            name: 'Group',
            values: [
              {
                type: 'searchFilterAttribute',
                name: this.searchboxValueSelected,
                applied: true
              }
            ]
          }
        }
      };
    } else {
      //  payload when filter options is selected
      // filterQuery holds the payload which is passed from main filter component
      // everytime filter is clicked payload is sent with doNotReturnFilter: true to get only results data
      this.filterPresent = true;
      omniSearchPayload = {
        ag: this.selectedAssetGroup,
        domain: this.selectedDomain,
        doNotReturnFilter: true,
        includeAllAssets: this.terminatedIsChecked,
        from: 0,
        filter: this.filterQuery,
        searchText: this.searchText,
        size: this.numOfCardShown
      };
    }

    this.dataSubscription = this.omniSearchDataService.getOmniSearchData(
      omniSearchUrl,
      omniSearchMethod,
      omniSearchPayload
    ).subscribe(
      response => {
        try {
          // storing the data in session storage
          this.stopPreviousDataSubscription = false;
          this.dataStore.setOmniSeachData(
            this.searchText,
            this.searchboxValueSelected,
            this.selectedAssetGroup,
            this.selectedDomain,
            this.terminatedIsChecked,
            this.filterQuery,
            response
          );
          if (this.utils.checkIfAPIReturnedDataIsEmpty(response.data)) {
            this.getErrorValues();
            this.errorMessage = 'noDataAvailable';
          } else {
            this.processHttpResponse(response);
          }
        } catch (e) {
          this.errorMessage = this.errorHandling.handleJavascriptError(e);
          this.getErrorValues();
        }
      },
      error => {
        this.errorMessage = error;
        this.getErrorValues();
      }
    );
  }
  // **************************** results data subscription code ends here  *************************** */
  // *****************************************************************************************************************/
  // **************************** filter data subscription function code starts here  *************************** */
  /**
   * @func fetchFilterdata
   * @desc this funtions calls the search api to get filters
   * doNotReturnFilter: false, to get results + filter data
   */
  fetchFilterdata() {
    this.filterDataIsRequested = true;
    if (this.filterSubscription) {
      this.filterSubscription.unsubscribe();
    }
    const omniSearchUrl = environment.omniSearch.url;
    const omniSearchMethod = environment.omniSearch.method;
    let omniSearchResultsPayload;
    omniSearchResultsPayload = {
      ag: this.selectedAssetGroup,
      domain: this.selectedDomain,
      doNotReturnFilter: false,
      from: 0,
      searchText: this.searchText,
      size: this.numOfCardShown,
      includeAllAssets: this.terminatedIsChecked,
      filter: {
        groupBy: {
          type: 'searchFilterAttributeGroup',
          name: 'Group',
          values: [
            {
              type: 'searchFilterAttribute',
              name: this.searchboxValueSelected,
              applied: true
            }
          ]
        }
      }
    };
    this.searchClicked = false;
    this.filterSubscription = this.omniSearchDataService.getOmniSearchData(
      omniSearchUrl,
      omniSearchMethod,
      omniSearchResultsPayload
    ).subscribe(
      response => {
        try {
          this.stopPreviousDataSubscription = false;
          this.filterDataIsRequested = false;

          if (!this.utils.checkIfAPIReturnedDataIsEmpty(response.data)) {
            if (
              this.utils.checkIfAPIReturnedDataIsEmpty(
                response.data.filter.groupBy
              )
            ) {
              this.filterData = {};
            } else {
              this.dataStore.setOmniSeachData(
                this.searchText,
                this.searchboxValueSelected,
                this.selectedAssetGroup,
                this.selectedDomain,
                this.terminatedIsChecked,
                response.data.filter,
                response
              );
              this.dataStore.set(
                'omnisearchLastAppliedFilter',
                JSON.stringify(response.data.filter)
              );
              this.processFilterOptions(response.data.filter);
            }
          } else {
            this.filterData = {};
          }
        } catch (e) {
          this.errorHandling.handleJavascriptError(e);
          this.filterData = { value: 'errorInApiCall' };
        }
      },
      error => {
        this.filterData = { value: 'errorInApiCall' };
        this.filterDataIsRequested = false;
      }
    );
  }
  // **************************** filter data subscription function code ends here  *************************** */
  /**
   * @function omniSearchCategory
   * @param httpresponse
   * @desc This function is called to process the response
   */
  processHttpResponse(response) {
    try {
      if (this.utils.checkIfAPIReturnedDataIsEmpty(response.data)) {
        this.processFilterOptions(this.filterQuery);
        this.getErrorValues();
        this.errorMessage = 'noDataAvailable';
      } else {
        this.showLoader = false;
        this.seekdata = false;
        this.dataComing = true;
        this.totalNumOfCards = response.data.total; // shows the total num in the bottom
        this.numOfCardShown = response.data.results.length; // shows the total result available in one scroll view
        this.infiniteScrollCalled = false;
        this.processSearchResults(response.data);
      }
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  // this below funciton gets the options in the dropdown menu
  setOmniSearchCategory() {
    try {
      const omniSearchomniSearchCategoriesUrl =
        environment.omniSearchCategories.url;
      const omniSearchCategoriesMethod = environment.omniSearchCategories.method;
      const queryParam = {
        domain: this.selectedDomain
      };

      this.omniSearchCategorySubscription = this.omniSearchDataService.getOmniSearchCategories(
        omniSearchomniSearchCategoriesUrl,
        omniSearchCategoriesMethod,
        queryParam
      ).subscribe(
        response => {
          try {
            if (response.length === 0) {
              // need to add code By Trinanjan
              // what will happen if no options are coming
            } else {
              this.dropdownData = response;
              this.dropdownData.splice(0, 0);

            }
          } catch (e) {
            this.errorMessage = this.errorHandling.handleJavascriptError(e);
            this.getErrorValues();
          }
        },
        error => {
          this.errorMessage = error;
          this.getErrorValues();
        }
      );
    } catch (error) {
      this.logger.log('error', error);
    }
  }
  // assign error values...

  getErrorValues(): void {
    this.showLoader = false;
    this.dataComing = false;
    this.seekdata = true;
    this.stopPreviousDataSubscription = false;
    this.infiniteScrollCalled = false;
  }

  /**
   * @function processFilterOptions
   * @param data filter data
   * @desc Processes the data to paint filter
   */
  processFilterOptions(data) {
    try {
      this.filterDataIsRequested = false;
      if (!this.utils.isObjectEmpty(data)) {
        this.filterPresent = true;
        this.filterData = data;
        /**
         *  save the Filter Obj to seesion storage to load first next time
         * We can trace this Obj by a search text and category
         */
      } else {
        this.filterPresent = false;
      }
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  /**
   * @function processSearchResults
   * @param data results  data
   * @desc Processes the data to paint results card
   */
  processSearchResults(data) {
    /**
     * save the results Obj to seesion storage to load first next time
     *  We can trace this Obj by a search text and category and filter applied obj(stringify)
     */

    //  call the imagepath funciton
    // this.getImagePathforTargetType(data)
    try {
      this.searchResultsData = this.getImagePathforTargetType(data);
    } catch (error) {
      this.logger.log('error', error);
    }
  }
  getImagePathforTargetType(data) {
    try {
      const searchData = data.results;
      const targetTypeImagePath = ICONS.awsResources;
      searchData.forEach(function (eachObj) {
        if (eachObj.hasOwnProperty('_entitytype')) {
          const targetType = eachObj['_entitytype'];
          if (targetTypeImagePath.hasOwnProperty(targetType)) {
            eachObj['imagePath'] = targetTypeImagePath[targetType];
          }
        }
      });

      return searchData;
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  /*
    *This function capture the filter click event on checkbox and radioBtn
   */
  filterOptionClicked(event) {
    try {
      // cancel previous data call if multiple filters are applied togethre
      this.infiniteScrollCalled = false;
      this.filterClicked = true;
      if (this.stopPreviousDataSubscription) {
        this.dataSubscription.unsubscribe();
      }
      this.filterQuery = event;
      this.dataStore.set(
        'omnisearchLastAppliedFilter',
        JSON.stringify(this.filterQuery)
      );
      this.numOfCardShown = 50; // size of payload results , everytime filter is clicked we are resetting the variable
      this.updateComponent();
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  /**
   * @function searchCalled
   * @param data searchtext,search category,show terminated
   * @desc runs everytime we click the search btn
   */
  searchCalled(event) {
    try {
      this.resetSearch();
      this.resetResults();
      this.resetFilter();

      this.terminatedIsChecked = event.terminatedIsChecked;
      this.searchClicked = true;

      if (this.stopPreviousDataSubscription) {
        this.dataSubscription.unsubscribe();
      }
      // If Dropdown is not selected By Default send already selected option in the queryparams
      if (this.utils.isObjectEmpty(event.searchValue)) {
        event.searchValue = {
          id: this.searchboxValueSelected,
          text: this.searchboxValueSelected
        };
      }

      const queryData = {
        filterValue: event.searchValue.value.toString(),
        searchText: event.filterValue.toString()
      };
      // update the url with new searchtext and search category
      this.router.navigate(
        ['../../', queryData.filterValue, queryData.searchText],
        {
          relativeTo: this.activatedRoute,
          queryParamsHandling: 'merge'
        }
      );
      /**
       * SearchText/searchboxValueSelected should get updated by getRuleId func
       * right now getruleid() is getting called after this.updatecomponent
       * so search is happening with updated values
       */
      this.searchText = queryData.searchText;
      this.searchboxValueSelected = queryData.filterValue;

      /**
       * Make the filterQuery empty as filterQuery is a input value form maiin filter component
       * We are checking whether filterQuery is empty or not to call differnt api payload
       * If fiterQuery is empty entire page is refreshed
       */

      this.updateComponent();
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  /**
   * @function onScroll
   * @desc calls infinite scroll
   */

  onScroll() {
    try {
      // on every scroll we are loading 50 more values
      if (this.totalNumOfCards > this.numOfCardShown) {
        this.infiniteScrollCalled = true;

        const remainNumOfCards = this.totalNumOfCards - this.numOfCardShown;

        if (remainNumOfCards / 50 >= 1) {
          this.numOfCardShown = this.numOfCardShown + 50;
        } else {
          this.numOfCardShown = this.numOfCardShown + remainNumOfCards;
        }
        /* Fetch Results Data */
        this.fetchResultData();
      } else {
        this.logger.log('', 'All Data Shown');
      }
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  // based on whether the filter is open or close we show and hide overlay

  isfilterOpen(event) {
    try {
      if (event === true) {
        this.showOverlay = false;
      } else {
        this.showOverlay = true;
      }
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  /**
   * @func navigateTo
   * @data gets the data needed to navigate to the link
   * @desc this fucntion takes care of the navigation to different links when clicked on particular type of card
   */

  navigateTo(data) {
    try {
      // this.workflowService.addToLevel(this.urlToRedirect, this.pageLevel);
      this.workflowService.addRouterSnapshotToLevel(
        this.router.routerState.snapshot.root
      );
      let resourceID;
      let resourceType;
      if (data['_id']) {
        resourceID = encodeURIComponent(data['_id']);
      }
      if (data['_entitytype']) {
        resourceType = encodeURIComponent(data['_entitytype']);
      }

      if (data['searchCategory'].toLowerCase() === 'assets') {

          this.router.navigate(
            ['../../../../assets/assets-details', resourceType, resourceID],
            { relativeTo: this.activatedRoute, queryParamsHandling: 'merge' }
          ).then(response => {
            this.logger.log('info', 'Successfully navigated to asset details page: ' + response);
          })
          .catch(error => {
            this.logger.log('error', 'Error in navigation - ' + error);
          });

      } else if (data['searchCategory'].toLowerCase() === 'policy violations') {
          this.router.navigate(['../../../../compliance/issue-details', resourceID], {
            relativeTo: this.activatedRoute,
            queryParamsHandling: 'merge'
          }).then(response => {
            this.logger.log('info', 'Successfully navigated to issue details page: ' + response);
          })
          .catch(error => {
            this.logger.log('error', 'Error in navigation - ' + error);
          });
      } else if (data['searchCategory'].toLowerCase() === 'vulnerabilities') {
          const apiTarget = { TypeAsset: 'vulnerable' };
          const eachParams = { qid: resourceID }; // resourceID is qid here
          let newParams = this.utils.makeFilterObj(eachParams);
          newParams = Object.assign(newParams, apiTarget);
          newParams['mandatory'] = 'qid';
          this.router.navigate(['../../../../', 'assets', 'asset-list'], {
            relativeTo: this.activatedRoute,
            queryParams: newParams,
            queryParamsHandling: 'merge'
          })
          .then(response => {
            this.logger.log('info', 'Successfully navigated to issue details page: ' + response);
          })
          .catch(error => {
            this.logger.log('error', 'Error in navigation - ' + error);
          });
      }
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  resetSearch() {
    this.filterClicked = false;
    this.showOverlay = false;
  }

  /* Resettting results variables */
  resetResults() {
    this.numOfCardShown = 50;
  }

  /* Reset variables related to Filter function */
  resetFilter() {
    this.filterQuery = {};
    this.showOverlay = false;
    this.filterClicked = false;

    // clearing omnisearchLastAppliedFilter,OmniSearchFirstLevelIndex,OmniSearchSecondLevelIndex,omniSearchFilterRefineByCount
    this.dataStore.clear('OmniSearchFirstLevelIndex');
    this.dataStore.clear('OmniSearchSecondLevelIndex');
    this.dataStore.clear('omniSearchFilterRefineByCount');
    this.dataStore.clear('omnisearchLastAppliedFilter');
  }


  ngOnDestroy() {
    // unsubscribing on ngOnDestroy
    try {
      if (this.dataSubscription) {
        this.dataSubscription.unsubscribe();
      }
      if (this.subscriptionToAssetGroup) {
        this.subscriptionToAssetGroup.unsubscribe();
      }
      if (this.subscriptionDomain) {
        this.subscriptionDomain.unsubscribe();
      }
      if (this.resultsDataSubscription) {
        this.resultsDataSubscription.unsubscribe();
      }
      if (this.filterSubscription) {
        this.filterSubscription.unsubscribe();
      }
      if (this.routeSubscription) {
        this.routeSubscription.unsubscribe();
      }
      if (this.omniSearchCategorySubscription) {
        this.omniSearchCategorySubscription.unsubscribe();
      }
    } catch (error) {
      this.logger.log('error', error);
    }
  }
}
