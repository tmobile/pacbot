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

import {BrowserModule} from '@angular/platform-browser';
import {NgModule, ErrorHandler} from '@angular/core';
import {RouterModule, Routes, RouterLink, RouterLinkActive} from '@angular/router';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatSelectModule} from '@angular/material';
import {AngularFontAwesomeModule} from 'angular-font-awesome/angular-font-awesome';
import {HttpClientModule} from '@angular/common/http';
import 'rxjs/add/operator/toPromise';
import {AppComponent} from './app.component';

/* Feature modules */
import {LandingPageModule} from './landing-page/landing-page.module';
import {PostLoginAppModule} from './post-login-app/post-login-app.module';
import {AppRoutingModule} from './app-routing.module';
import {CoreModule} from './core/core.module';
import { ErrorHandlingService } from './shared/services/error-handling.service';

@NgModule({
    declarations: [
        AppComponent
    ],
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        MatSelectModule,
        FormsModule,
        ReactiveFormsModule,
        HttpClientModule,
        RouterModule,
        AngularFontAwesomeModule,
        AppRoutingModule,
        LandingPageModule,
        PostLoginAppModule,
        CoreModule
    ],
    providers: [],
    bootstrap: [AppComponent]
})

export class AppModule {
}
