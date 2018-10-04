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

import {NgModule, Optional, SkipSelf} from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';
import { LandingPageRoutingModule } from './landing-page.routing.module';

import {HomePageComponent} from './home-page/home-page.component';
import {LoginComponent} from './login/login.component';
import {FeatureComponent} from './common/feature/feature.component';
import {PulseBallComponent} from './common/pulse-ball/pulse-ball.component';
import {GlowTravellerComponent} from './common/glow-traveller/glow-traveller.component';
import {LandingPageHeaderComponent} from './common/landing-page-header/landing-page-header.component';
import {LandingDiamondComponent} from './common/landing-diamond/landing-diamond.component';
import {TopGlowTravellerComponent} from './common/top-glow-traveller/top-glow-traveller.component';



@NgModule({
  imports: [
    CommonModule, LandingPageRoutingModule, SharedModule
  ],
  declarations: [
    HomePageComponent,
    LoginComponent,
    FeatureComponent,
    PulseBallComponent,
    GlowTravellerComponent,
    LandingPageHeaderComponent,
    LandingDiamondComponent,
    TopGlowTravellerComponent
  ]
})
export class LandingPageModule {
  constructor (@Optional() @SkipSelf() parentModule: LandingPageModule) {
    if (parentModule) {
      throw new Error(
          'LandingPageModule is already loaded. Import it in the AppModule only');
    }
  }
}
