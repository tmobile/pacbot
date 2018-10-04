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
import { Routes,
    RouterModule } from '@angular/router';

import { HomePageComponent } from './home-page/home-page.component';
import { LoginComponent } from './login/login.component';

const routes: Routes = [
    {
        path: 'home',
        component: HomePageComponent,
        children: [
            {
                path: 'login',
                component: LoginComponent
            }
        ]
    },
    {
        path: 'home-page',
        redirectTo: '/home',
        pathMatch: 'full'
    },
    {
        path: 'home-page/login',
        redirectTo: '/home/login',
        pathMatch: 'full'
    },
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class LandingPageRoutingModule { }
