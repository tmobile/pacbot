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

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../../../shared/shared.module';
import { ToolsComponent } from './tools.component';
import { ToolsLandingPageComponent } from './tools-landing-page/tools-landing-page.component';
import { ToolsRoutingModule } from './tools-routing.module';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    ToolsRoutingModule
  ],
  declarations: [ToolsComponent, ToolsLandingPageComponent]
})

export class ToolsModule { }
