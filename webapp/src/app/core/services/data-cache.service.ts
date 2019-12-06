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
 * @type Service
 * @desc Data Caching Service - Shared service for storing and retriving data.
 As of now data is stored locally. It can be replaced by a more persistent storage.
 * @author Puneet Baser
 */

import { Injectable } from '@angular/core';
// import { Session } from "selenium-webdriver";
import { UtilsService } from './../../shared/services/utils.service';

@Injectable()
export class DataCacheService {
  private store = sessionStorage;

  constructor(private utils: UtilsService) {}

  // Add an entry to the store
  public set(key: string, value: any) {
    if (key === undefined || key === null || key === '') {
      return;
    }

    // save the value locally
    this.store[key] = value;
  }

  // Get an entry from the store
  public get(key: string) {
    // get the value
    if (key === undefined || key === null || key === '') {
      return null;
    }

    return this.store[key];
  }

  // Delete an entry from the store
  public clear(key: string) {
    // clear an entry
    if (key === undefined || key === null || key === '') {
      return;
    }

    this.store[key] = undefined;
  }

  // Clear store
  public clearAll() {
    this.store.clear();
    // clear all cache
  }

  public getStorage() {
    return this.store;
  }

  public setCurrentUserLoginDetails(value) {
    const key = 'currentUserLoginDetails';
    this.set(key, value);
  }

  public getCurrentUserLoginDetails() {
    const key = 'currentUserLoginDetails';
    return this.get(key);
  }

  public getUserDetailsValue() {
    let secretData = this.getCurrentUserLoginDetails();
    if (secretData) {
      secretData = JSON.parse(secretData);
      return {
        /* Deprecated: getAuthToken - function is not used anymore */
        getAuthToken() {
          const authTokenValues = {
            'token_type': secretData.token_type,
            'access_token': secretData.access_token,
            'refresh_token': secretData.refresh_token,
            'expires_in': secretData.expires_in
          };
          return authTokenValues;
        },

        getRoles() {
          let roles = [];
          roles = secretData.userInfo.userRoles;
          return roles;
        },

        getEmail() {
          const email = secretData.userInfo.email;
          return email;
        },

        getFirstName() {
          const firstName = secretData.userInfo.firstName;
          return firstName;
        },

        getLastName() {
          const firstName = secretData.userInfo.lastName;
          return firstName;
        },

        getUserName() {
          const userName = secretData.userInfo.userName;
          return userName;
        },

        getUserId() {
          const userId = secretData.userInfo.userId;
          return userId;
        },

        /* Deprecated: getRefreshToken - function is not used anymore */
        getRefreshToken() {
          const refreshToken = secretData.refresh_token;
          return refreshToken;
        },

        /* Deprecated: getClientCredentials - function is not used anymore */
        getClientCredentials() {
          const clientCredentials = secretData.clientCredentials;
          return clientCredentials;
        },

        isAuthenticated() {
          return secretData.success;
        }
      };
    }
  }

  public getCurrentSelectedAssetGroup() {
    const key = 'currentSelectedAssetGroup';
    return this.get(key);
  }

  public setCurrentSelectedAssetGroup(assetGroup) {
    const key = 'currentSelectedAssetGroup';
    if (assetGroup) { this.set(key, assetGroup); }
  }

  public getCurrentSelectedDomain(assetGroupNameAsKey) {
    const key = assetGroupNameAsKey;
    return this.get(key);
  }

  public setCurrentSelectedDomain(domainName, assetGroupNameAsKey) {
    const key = assetGroupNameAsKey;
    if (domainName) { this.set(key, domainName); }
  }

  public setCurrentSelectedDomainList(domainList) {
    const key = 'domainList';
    if (domainList) { this.set(key, domainList); }
  }

  public getRecentlyViewedAssetGroups() {
    const key = 'recentlyViewedAssetGroups';
    return this.get(key);
  }
  public setRecentlyViewedAssetGroups(recentlyViewedAssetGroups) {
    const key = 'recentlyViewedAssetGroups';
    if (recentlyViewedAssetGroups) { this.set(key, recentlyViewedAssetGroups); }
  }


  public getCurrentSelectedDomainList() {
    const key = 'domainList';
    return this.get(key);
  }

  public setListOfAssetGroups(allAssetGroups) {
    const key = 'allAssetGroups';
    if (allAssetGroups) { this.set(key, allAssetGroups); }
  }

  public getListOfAssetGroups() {
    const key = 'allAssetGroups';
    return this.get(key);
  }

  public setUserDefaultAssetGroup(defaultAssetGroup) {
    const key = 'userDefaultAssetGroup';
    if (defaultAssetGroup) { this.set(key, defaultAssetGroup); }
  }

  public setUserCurrentAssetGroupObj(userDefaultAssetGroupObj) {
    const key = 'userDefaultAssetGroupObj';
    if (userDefaultAssetGroupObj) {
      this.set(key, JSON.stringify(userDefaultAssetGroupObj));
    }
  }

  public getUserDefaultAssetGroup() {
    const key = 'userDefaultAssetGroup';
    return this.get(key);
  }

  public getUserCurrentAssetGroupObj() {
    const key = 'userDefaultAssetGroupObj';
    return JSON.parse(this.get(key));
  }

  public setHashedIdOfUser(hashedKey) {
    const key = 'hashedKey';
    this.set(key, hashedKey);
  }

  public getHashedIdOfUser() {
    const key = 'hashedKey';
    return this.get(key);
  }

  /**
   * @author Trinanjan added on 09.04.2018
   * @func setOmniSeachData
   * @param searchTextToCompare gets the searchText based on which data will be returned
   * @param searchCategoryToCompare gets the searchCategory based on which data will be returned
   * @param ag asset group selected,
   * @param domain domain selected,
   * @param showTerminated terminated checkbox,
   * @param filterQueryToCompare applied filter
   * @param response saves the api response for the omnisearch
   * @desc stores the omni search data along with searchText and searchCategory
   */
  public setOmniSeachData(
    searchText,
    searchCategory,
    ag,
    domian,
    showTerminated,
    filterQuery,
    response
  ) {
    const key = 'OmniSearchData';
    let keyToBeStored;
    let finalKeyToBeStored;
    if (this.utils.isObjectEmpty(filterQuery)) {
      keyToBeStored = [searchText, searchCategory, ag, domian, showTerminated];
    } else {
      keyToBeStored = [
        searchText,
        searchCategory,
        ag,
        domian,
        showTerminated,
        JSON.stringify(filterQuery)
      ];
    }
    finalKeyToBeStored = keyToBeStored.join('*');
    const dataToBeStored = {};
    dataToBeStored[finalKeyToBeStored] = response;
    if (JSON.stringify(dataToBeStored)) {
      this.set(key, JSON.stringify(dataToBeStored));
    }
  }
  /**
   * @author Trinanjan added on 09.04.2018
   * @func getOmniSeachData
   * @param searchTextToCompare gets the searchText based on which data will be returned
   * @param searchCategoryToCompare gets the searchCategory based on which data will be returned
   * @param ag asset group selected,
   * @param domain domain selected,
   * @param showTerminated terminated checkbox,
   * @param filterQueryToCompare applied filter
   * @returns the saved data or empty data (if user logs in firstime or searchText/searchCategory isn't matching)
   */
  public getOmniSeachData(
    searchTextToCompare,
    searchCategoryToCompare,
    ag,
    domain,
    showTerminated,
    filterQueryToCompare
  ) {
    const key = 'OmniSearchData';
    let dataToBeChecked;
    let keyToBeChecked;
    let finalKeyToBeChecked;
    if (this.utils.isObjectEmpty(filterQueryToCompare)) {
      keyToBeChecked = [
        searchTextToCompare,
        searchCategoryToCompare,
        ag,
        domain,
        showTerminated
      ];
    } else {
      keyToBeChecked = [
        searchTextToCompare,
        searchCategoryToCompare,
        ag,
        domain,
        showTerminated,
        JSON.stringify(filterQueryToCompare)
      ];
    }
    finalKeyToBeChecked = keyToBeChecked.join('*');

    if (this.get(key)) {
      dataToBeChecked = JSON.parse(this.get(key));
    }
    if (dataToBeChecked) {
      if (finalKeyToBeChecked in dataToBeChecked) {
        return dataToBeChecked[finalKeyToBeChecked];
      } else {
        return 'no data';
      }
    } else {
      return 'no data';
    }
  }
  /**
   * @author Trinanjan added on 29.05.2018
   * @func setSearhCriteria
   * @param searchCategory gets the search category
   * @param searchText gets the searchText
   * @param checkBoxState  gets the terminated checkbox state
   * @desc saves the search Criteria
   */
  public setSearhCriteria(searchCategory, searchText, checkBoxState) {
    const key = 'searchCategory';
    const keyToBeStored = [searchCategory, searchText, checkBoxState];
    const finalKeyToBeStored = keyToBeStored.join('*');
    this.set(key, finalKeyToBeStored);
  }
  /**
   * @author Trinanjan added on 29.05.2018
   * @func getSearhCriteria
   * @param searchCategory gets the search category
   * @param searchText gets the searchText
   * @param checkBoxState  gets the terminated checkbox state
   * @desc checks whether search criteria is changed based on the params and returns true/false based on which search button is highlighted
   */
  public getSearhCriteria(searchCategory, searchText, checkBoxState) {
    const key = 'searchCategory';
    const keyToBeChecked = [searchCategory, searchText, checkBoxState];
    const finalKeyToBeChecked = keyToBeChecked.join('*');
    let oldKeyStored;
    if (this.get(key)) {
      oldKeyStored = this.get(key);
    }
    if (finalKeyToBeChecked === oldKeyStored) {
      return true;
    } else {
      return false;
    }
  }

  public setRedirectUrl(url) {
    const key = 'redirectUrl';
    this.set(key, url);
  }

  public getRedirectUrl() {
    const key = 'redirectUrl';
    return this.get(key);
  }

}
