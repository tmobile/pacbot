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
import { PoliciesComponent } from './policies/policies.component';
import { AdminComponent } from './admin.component';
import { AdminRoutingModule } from './admin-routing.module';
import { RulesComponent } from './rules/rules.component';
import { CreateRuleComponent } from './create-rule/create-rule.component';
import { CreateEditPolicyComponent } from './create-edit-policy/create-edit-policy.component';
import { SelectModule } from 'ng2-select';
import { CreateJobExecutionManagerComponent } from './job-execution-manager/create-job-execution-manager/create-job-execution-manager.component';
import { JobExecutionManagerComponent } from './job-execution-manager/job-execution-manager.component';
import { UpdateRuleComponent } from './update-rule/update-rule.component';
import { InvokeRuleComponent } from './invoke-rule/invoke-rule.component';
import { CreateAssetGroupsComponent } from './asset-groups/create-asset-groups/create-asset-groups.component';
import { EnableDisableRuleComponent } from './enable-disable-rule/enable-disable-rule.component';
import { AssetGroupsComponent } from './asset-groups/asset-groups.component';
import { StickyExceptionsComponent } from './sticky-exceptions/sticky-exceptions.component';
import { DomainsComponent } from './asset-groups/domains/domains.component';
import { TargetTypesComponent } from './asset-groups/target-types/target-types.component';
import { CreateUpdateDomainComponent } from './asset-groups/domains/create-update-domain/create-update-domain.component';
import { CreateUpdateTargetTypesComponent } from './asset-groups/target-types/create-update-target-types/create-update-target-types.component';
import { CreateStickyExceptionsComponent } from './sticky-exceptions/create-sticky-exceptions/create-sticky-exceptions.component';
import { NgDatepickerModule } from 'ng2-datepicker';
import { RolesComponent } from './roles/roles.component';
import { RolesAllocationComponent } from './roles-allocation/roles-allocation.component';
import { CreateUpdateRolesComponent } from './roles/create-update-roles/create-update-roles.component';
import { DeleteStickyExceptionsComponent } from './sticky-exceptions/delete-sticky-exceptions/delete-sticky-exceptions.component';
import { DeleteAssetGroupsComponent } from './asset-groups/delete-asset-groups/delete-asset-groups.component';
import { ConfigUsersComponent } from './roles/config-users/config-users.component';
import { UpdateJobExecutionManagerComponent } from './job-execution-manager/update-job-execution-manager/update-job-execution-manager.component';
import { PacmanLoaderComponent } from './commons/pacman-loader/pacman-loader.component';
import { AccountManagementComponent } from './account-management/account-management.component';
import { AccountManagementDetailsComponent } from './account-management-details/account-management-details.component';
import { PluginManagementDetailsComponent } from './plugin-management-details/plugin-management-details.component';
import { PluginManagementComponent } from './plugin-management/plugin-management.component';
import { SystemManagementComponent } from './system-management/system-management.component';
import { ConfigManagementComponent } from './config-management/config-management.component';

@NgModule({
  imports: [
    CommonModule, SharedModule, AdminRoutingModule, SelectModule, NgDatepickerModule
  ],
  declarations: [
    AdminComponent,
    PoliciesComponent,
    RulesComponent,
    CreateRuleComponent,
    UpdateRuleComponent,
    InvokeRuleComponent,
    CreateEditPolicyComponent,
    JobExecutionManagerComponent,
    CreateJobExecutionManagerComponent,
    CreateAssetGroupsComponent,
    EnableDisableRuleComponent,
    AssetGroupsComponent,
    StickyExceptionsComponent,
    CreateStickyExceptionsComponent,
    DomainsComponent,
    CreateUpdateDomainComponent,
    TargetTypesComponent,
    CreateUpdateTargetTypesComponent,
    RolesComponent,
    CreateUpdateRolesComponent,
    RolesAllocationComponent,
    DeleteStickyExceptionsComponent,
    DeleteAssetGroupsComponent,
    ConfigUsersComponent,
    UpdateJobExecutionManagerComponent,
    PacmanLoaderComponent,
    AccountManagementComponent,
    AccountManagementDetailsComponent,
    PluginManagementDetailsComponent,
    PluginManagementComponent,
    SystemManagementComponent,
    ConfigManagementComponent
  ]
})
export class AdminModule { }
