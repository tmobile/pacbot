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

import { NgModule, Optional, SkipSelf, ErrorHandler } from '@angular/core';
import { CommonModule } from '@angular/common';
import {SharedModule} from './../shared/shared.module';
import {WorkflowService} from './services/workflow.service';
import {DataCacheService} from './services/data-cache.service';
import {AssetGroupObservableService} from './services/asset-group-observable.service';
import {AssetTilesService} from './services/asset-tiles.service';
import { OnPremAuthenticationService } from './services/onprem-authentication.service';
import {DomainTypeObservableService} from './services/domain-type-observable.service';
import {DomainMappingService} from './services/domain-mapping.service';
import {ThemeObservableService} from './services/theme-observable.service';
import {PermissionGuardService} from './services/permission-guard.service';
import {RoutingService} from './services/routing.service';
import { AuthService } from './services/auth.service';
import {AdalService} from './services/adal.service';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { RequestInterceptorService } from './services/request-interceptor.service';
import { AuthSessionStorageService } from './services/auth-session-storage.service';
import {RecentlyViewedObservableService} from './services/recently-viewed-observable.service';

@NgModule({
  imports: [
    CommonModule,
    SharedModule
  ],
  declarations: [],
  exports: [],
  providers: [
    WorkflowService,
    DataCacheService,
    AssetGroupObservableService,
    AssetTilesService,
    OnPremAuthenticationService,
    DomainTypeObservableService,
    DomainMappingService,
    ThemeObservableService,
    PermissionGuardService,
    RoutingService,
    AuthService,
    AdalService,
    AuthSessionStorageService,
    RecentlyViewedObservableService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: RequestInterceptorService,
      multi: true
    }
  ]
})

export class CoreModule {
  constructor (@Optional() @SkipSelf() parentModule: CoreModule) {
    if (parentModule) {
      throw new Error(
          'CoreModule is already loaded. Import it in the AppModule only');
    }
  }
}
