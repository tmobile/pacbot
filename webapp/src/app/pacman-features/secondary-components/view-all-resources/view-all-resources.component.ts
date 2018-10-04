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
import { ICONS } from './../../../shared/constants/icons-mapping';
import { ActivatedRoute, Router } from '@angular/router';
import { AwsResourceTypeSelectionService } from './../../services/aws-resource-type-selection.service';
import { Subscription } from 'rxjs/Subscription';
import { LoggerService } from '../../../shared/services/logger.service';

@Component({
  selector: 'app-view-all-resources',
  templateUrl: './view-all-resources.component.html',
  styleUrls: ['./view-all-resources.component.css']
})
export class ViewAllResourcesComponent implements OnInit, OnDestroy {

  private awsResources: any = [];
  private activeTileIndex: any = 0;
  private categories = [];
  private categoryNames = [];
  private filteredResources: any = [];
  private selectedResource: any;
  private activeFilterCategory: any;

  private resourceTypeSelectionSubscription: Subscription;

  awsTileClicked (resource, index) {
    this.activeTileIndex = index;
    this.selectedResource = resource;
  }

  saveSelectedResource() {
    this.awsResourceTypeSelectionService.awsResourceSelected(this.selectedResource);
    this.closeViewAllModal();
  }

  filterByCategory(category) {
    this.activeFilterCategory = category;
    const resources = this.awsResources.slice();
    this.filteredResources = [];
    resources.forEach(element => {
      if (element.category === category.name || category.name === 'All') {
        this.filteredResources.push(element);
      }
    });
  }

  getAllCategories() {
    this.categories = [];

    // Temporary array to store unique categories
    this.categoryNames = [];

    this.awsResources.forEach(resource => {
      const category = ICONS.categories[resource.category] === undefined ? 'Extra' : resource['category'];
      resource.category = category;   // Update the category of current resource depending on the result of the above line
      if (this.categoryNames.indexOf(category) === -1) {
        this.categoryNames.push(category);
        const obj = {
          'name'   : category,
          'color'  : ICONS.categories[category]
        };
        this.categories.push(obj);
      }
    });

    // If extra categories are present, push them to the end
    if (this.categoryNames.indexOf('Extra') > -1) {
        const extraCategory = this.categories.splice(this.categoryNames.indexOf('Extra'), 1)[0];
        this.categories.push(extraCategory);
    }

    // Push 'All' type at the front if categories array
    this.categories.unshift({
      'name' : 'All',
      'color': '#333333'
    });

    this.activeFilterCategory = this.categories[0];
  }

  addIconsToResources() {
    this.awsResources.forEach(resource => {
      const name = this.categoryNames.indexOf(resource.category) > -1 ? resource.type : 'Extra';
      resource['iconPath'] = ICONS.awsResources[name];
    });
  }

  getAwsResources() {
    if (this.resourceTypeSelectionSubscription) { this.resourceTypeSelectionSubscription.unsubscribe(); }
    this.resourceTypeSelectionSubscription = this.awsResourceTypeSelectionService.getAllAwsResources().subscribe(
      allAwsResources => {
        this.filteredResources = [];
        this.awsResources = allAwsResources;
        this.getAllCategories();
        this.addIconsToResources();
        this.filteredResources = this.awsResources.slice();
      },
      error => {
        this.closeViewAllModal();
      }
    );
  }

  closeViewAllModal () {

    this.router.navigate(
        [
          {
            outlets: {
              modal: null
            }
          }
        ],
        {
          relativeTo: this.activatedRoute.parent, // <-- Parent activated route
          queryParamsHandling: 'merge'
        }
    );

  }

  constructor(
              private router: Router, private logger: LoggerService,
              private awsResourceTypeSelectionService: AwsResourceTypeSelectionService,
              private activatedRoute: ActivatedRoute) { }

  ngOnInit() {
      this.getAwsResources();
  }

  ngOnDestroy() {
    try {
      this.resourceTypeSelectionSubscription.unsubscribe();
    } catch (error) {
      this.logger.log('error', '--- Error while unsubscribing ---');
    }
  }

}
