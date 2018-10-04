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

import {Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AssetGroupObservableService} from '../../core/services/asset-group-observable.service';

@Component({
  selector: 'app-change-default-asset-group',
  templateUrl: './change-default-asset-group.component.html',
  styleUrls: ['./change-default-asset-group.component.css']
})
export class ChangeDefaultAssetGroupComponent implements OnInit {

  constructor(private router: Router,
              private activatedRoute: ActivatedRoute,
              private assetGroupObservableService: AssetGroupObservableService) { }

  ngOnInit() {
  }

  closeAssetGroupSelectionModal (value: String) {

      const navigationParams = {
          relativeTo: this.activatedRoute.parent // <-- Parent activated route
      };

      const agValue = value ? value : '';

      if (agValue) {
          navigationParams['queryParams'] = {'ag': agValue};
      } else {
          navigationParams['queryParamsHandling'] = 'merge';
      }

      this.router.navigate(
      [
        // No relative path pagination
        {
          outlets: {
            modal: null
          }
        }
      ],
          navigationParams
    );

  }
}
