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

import { browser, by, element , $, $$} from 'protractor';

export class OmniSearch {
    navigateToOmniSearch() {
        return (element(by.xpath('//app-root/app-post-login-app/div[1]/header/app-post-login-header/div[1]/div[2]/nav/app-nav-icon[3]/a')));
    }
    NavigateToDropDown() {
        return (element(by.xpath('//app-searchbar-dropdown/section/div[1]/div/app-searchable-dropdown/div/ng-select/div/input')));
    }
  AssetDropDownCheck() {
    return  element(by.xpath('//app-searchable-dropdown/div/ng-select/div/ul/li[1]/div/a/div'));
}

 PolicyViolationsDropDownCheck() {
    return element(by.xpath('//app-searchable-dropdown/div/ng-select/div/ul/li[2]/div/a/div'));
}
navigateToDropDownButton() {
    return element(by.xpath('//app-omnisearch/div/div/div/app-omni-search-page/section/div/app-searchbar-dropdown/section/div[1]/div/app-searchable-dropdown/div/ng-select/div/div[2]/span/span'));
}
searchFunction() {
    return element(by.xpath('//app-omnisearch/div/div/div/app-omni-search-page/section/div/app-searchbar-dropdown/section/div[1]/input'));
}
searchResult() {
    return element(by.xpath('//app-omnisearch/div/div/div/app-omni-search-details/section/div[2]/div[2]/section/div[2]'));
}
Attributes() {
    return element(by.xpath('//app-assets/div/div/div[2]/app-asset-details/div/div[1]/div[2]/section/ul/li[3]/div/div[2]/app-attribute/article'));

}
accountName() {
    return element(by.xpath('//app-assets/div/div/div[2]/app-asset-details/div/div[1]/div[2]/section/ul/li[3]/div/div[2]/app-attribute/article/section/div/div[10]/div/div/div[2]/div'));

}

clickButton() {
    return element(by.xpath('//app-omnisearch/div/div/div/app-omni-search-page/section/div/app-searchbar-dropdown/section/div[2]'));
}
clickSearchButton() {
    return element(by.xpath('//app-omnisearch/div/div/div/app-omni-search-details/section/div[1]/app-searchbar-dropdown/section/div[3]/button'));
}
checkDeletedAssets() {
    return element(by.xpath('//app-omnisearch/div/div/div/app-omni-search-details/section/div[1]/app-searchbar-dropdown/section/div[2]/app-check-box-btn/label/span'));
}
AssetList() {
    return element(by.xpath('//app-omnisearch/div/div/div/app-omni-search-details/section/div[2]/div[2]'));
}
goToAssetDetails() {
    return element(by.xpath('//app-omnisearch/div/div/div/app-omni-search-details/section/div[2]/div[2]/section/div[2]'));
}
DropDown() {
    return element(by.xpath('//app-omni-search-details/section/div[1]/app-searchbar-dropdown/section/div[1]/div/app-searchable-dropdown/div/ng-select/div/div[2]/span/span'));
}
checkHeading() {
    return element(by.xpath('//app-assets/div/div/div[2]/app-asset-details/div/div[1]/div[1]/h1'));
}
PolicyViolationsDetails() {
    return element(by.xpath('//app-omnisearch/div/div/div/app-omni-search-details/section/div[2]/div[2]/section/div[2]'));
}
checkHeadingPolicyViolations() {
    return element(by.xpath('//*[@id="searchtext"]/header/div[2]'));
}
FilterClick() {
    return element(by.xpath('//app-omni-search-details/section/div[2]/div[1]/app-main-filter/section/div[1]'));
}
filterContainer() {
 return element(by.xpath('//app-omnisearch/div/div/div/app-omni-search-details/section/div[2]/div[1]/app-main-filter/section/div[2]'));
}
checkAssets() {
    return element(by.xpath('//app-omnisearch/div/div/div/app-omni-search-details/section/div[1]/app-searchbar-dropdown/section/div[2]/app-check-box-btn/label'));
}
filterContent() {
    return element(by.xpath('//app-omnisearch/div/div/div/app-omni-search-details/section/div[2]/div[1]/app-main-filter/section/div[2]/section/div[1]/ul/li'));
}
refineCount() {
    return $$('.each-filter-desc .each-filter-options');
}

refineCountEle() {
    return $('.each-filter-desc .each-filter-options');
}
CountOriginal() {
    return element(by.xpath('//app-omnisearch/div/div/div/app-omni-search-details/section/div[2]/div[1]/app-main-filter/section/div[2]/section/div[1]/ul/li/p[2]'));

}

}
