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

import {ComplianceDashboardComponent} from '../../pacman-features/modules/compliance/compliance-dashboard/compliance-dashboard.component';
import {IssueListingComponent} from '../../pacman-features/modules/compliance/issue-listing/issue-listing.component';
import {IssueDetailsComponent} from '../../pacman-features/modules/compliance/issue-details/issue-details.component';
import {VulnerabilitiesComplianceComponent} from '../../pacman-features/modules/compliance/vulnerabilities-compliance/vulnerabilities-compliance.component';
import {PatchingComplianceComponent} from '../../pacman-features/modules/compliance/patching-compliance/patching-compliance.component';
import {TaggingComplianceComponent} from '../../pacman-features/modules/compliance/tagging-compliance/tagging-compliance.component';
import {CloudNotificationsComponent} from '../../pacman-features/modules/compliance/cloud-notifications/cloud-notifications.component';
import {EventDetailsComponent} from '../../pacman-features/modules/compliance/event-details/event-details.component';
import {CertificateComplianceComponent} from '../../pacman-features/modules/compliance/certificate-compliance/certificate-compliance.component';
import {PolicyDetailsComponent} from '../../pacman-features/modules/compliance/policy-details/policy-details.component';
import {PolicyKnowledgebaseComponent} from '../../pacman-features/modules/compliance/policy-knowledgebase/policy-knowledgebase.component';
import {PolicyKnowledgebaseDetailsComponent} from '../../pacman-features/modules/compliance/policy-knowledgebase-details/policy-knowledgebase-details.component';
import {CertificatesComponent} from '../../pacman-features/modules/compliance/certificates/certificates.component';
import {VulnerabilitiesComponent} from '../../pacman-features/modules/compliance/vulnerabilities/vulnerabilities.component';
import {AssetDashboardComponent} from '../../pacman-features/modules/assets/asset-dashboard/asset-dashboard.component';
import {ViewAllResourcesComponent} from '../../pacman-features/secondary-components/view-all-resources/view-all-resources.component';
import {AssetDetailsComponent} from '../../pacman-features/modules/assets/asset-details/asset-details.component';
import {AssetListComponent} from '../../pacman-features/modules/assets/asset-list/asset-list.component';
import {OnpremAssetsComponent} from '../../pacman-features/modules/assets/onprem-assets/onprem-assets.component';
import {AwsNotificationsComponent} from '../../pacman-features/modules/assets/aws-notifications/aws-notifications.component';
import {ToolsLandingPageComponent} from '../../pacman-features/modules/tools/tools-landing-page/tools-landing-page.component';
import {PermissionGuardService} from '../../core/services/permission-guard.service';
import {PatchingProjectionsComponent} from '../../pacman-features/modules/compliance/patching-projections/patching-projections.component';
import {DigitalDevDashboardComponent} from '../../pacman-features/modules/compliance/digital-dev-dashboard/digital-dev-dashboard.component';
import {PoliciesComponent} from '../../pacman-features/modules/admin/policies/policies.component';
import {RulesComponent} from '../../pacman-features/modules/admin/rules/rules.component';
import {CreateRuleComponent} from '../../pacman-features/modules/admin/create-rule/create-rule.component';
import {CreateEditPolicyComponent} from '../../pacman-features/modules/admin/create-edit-policy/create-edit-policy.component';
import {CreateJobExecutionManagerComponent } from '../../pacman-features/modules/admin/job-execution-manager/create-job-execution-manager/create-job-execution-manager.component';
import {JobExecutionManagerComponent} from '../../pacman-features/modules/admin/job-execution-manager/job-execution-manager.component';
import {AuthGuardService} from '../services/auth-guard.service';
import { UpdateRuleComponent } from '../../pacman-features/modules/admin/update-rule/update-rule.component';
import { InvokeRuleComponent } from '../../pacman-features/modules/admin/invoke-rule/invoke-rule.component';
import { CreateAssetGroupsComponent } from '../../pacman-features/modules/admin/asset-groups/create-asset-groups/create-asset-groups.component';
import { EnableDisableRuleComponent } from '../../pacman-features/modules/admin/enable-disable-rule/enable-disable-rule.component';
import { AssetGroupsComponent } from '../../pacman-features/modules/admin/asset-groups/asset-groups.component';
import { StickyExceptionsComponent } from '../../pacman-features/modules/admin/sticky-exceptions/sticky-exceptions.component';
import { DomainsComponent } from '../../pacman-features/modules/admin/asset-groups/domains/domains.component';
import { TargetTypesComponent } from '../../pacman-features/modules/admin/asset-groups/target-types/target-types.component';
import { CreateUpdateDomainComponent } from '../../pacman-features/modules/admin/asset-groups/domains/create-update-domain/create-update-domain.component';
import { CreateUpdateTargetTypesComponent } from '../../pacman-features/modules/admin/asset-groups/target-types/create-update-target-types/create-update-target-types.component';
import { CreateStickyExceptionsComponent } from '../../pacman-features/modules/admin/sticky-exceptions/create-sticky-exceptions/create-sticky-exceptions.component';
import { VulnerabilityDetailsComponent } from '../../pacman-features/modules/compliance/vulnerability-details/vulnerability-details.component';
import { OmniSearchPageComponent } from '../../pacman-features/modules/omnisearch/omni-search-page/omni-search-page.component';
import { OmniSearchDetailsComponent } from '../../pacman-features/modules/omnisearch/omni-search-details/omni-search-details.component';
import { RolesComponent } from '../../pacman-features/modules/admin/roles/roles.component';
import { RolesAllocationComponent } from '../../pacman-features/modules/admin/roles-allocation/roles-allocation.component';
import { CreateUpdateRolesComponent } from '../../pacman-features/modules/admin/roles/create-update-roles/create-update-roles.component';
import { DeleteStickyExceptionsComponent } from '../../pacman-features/modules/admin/sticky-exceptions/delete-sticky-exceptions/delete-sticky-exceptions.component';
import { DeleteAssetGroupsComponent } from '../../pacman-features/modules/admin/asset-groups/delete-asset-groups/delete-asset-groups.component';
import { UpdateJobExecutionManagerComponent } from '../../pacman-features/modules/admin/job-execution-manager/update-job-execution-manager/update-job-execution-manager.component';
import { ConfigUsersComponent } from '../../pacman-features/modules/admin/roles/config-users/config-users.component';
import { AccountManagementComponent } from '../../pacman-features/modules/admin/account-management/account-management.component';
import { AccountManagementDetailsComponent } from '../../pacman-features/modules/admin/account-management-details/account-management-details.component';
import { PluginManagementComponent } from '../../pacman-features/modules/admin/plugin-management/plugin-management.component';
import { PluginManagementDetailsComponent } from '../../pacman-features/modules/admin/plugin-management-details/plugin-management-details.component';
import { SystemManagementComponent } from '../../pacman-features/modules/admin/system-management/system-management.component';
import { ConfigManagementComponent } from '../../pacman-features/modules/admin/config-management/config-management.component';
import { RecommendationsComponent} from '../../pacman-features/modules/compliance/recommendations/recommendations.component';
import { RecommendationsDetailsComponent } from '../../pacman-features/modules/compliance/recommendations-details/recommendations-details.component';


export const COMPLIANCE_ROUTES = [
    {
        path: 'compliance-dashboard',
        component: ComplianceDashboardComponent,
        data: {
            title: 'Overview'
        },
        canActivate: [AuthGuardService]
    },
    {
        path: 'issue-listing',
        component: IssueListingComponent,
        data: {
            title: 'Policy Violations'
        },
        canActivate: [AuthGuardService]
    },
    {
        path: 'issue-details/:issueId',
        component: IssueDetailsComponent,
        data: {
            title: 'Policy Violation Details'
        },
        canActivate: [AuthGuardService]
    },
    {
        path: 'vulnerabilities-compliance',
        component: VulnerabilitiesComplianceComponent,
        data: {
            title: 'Vulnerabilities',
            tileName: 'app-overview-vulnerabilities'
        },
        canActivate: [AuthGuardService]
    },
    {
        path: 'patching-compliance',
        component: PatchingComplianceComponent,
        data: {
            title: 'Patching Compliance',
            tileName: 'app-overview-patching'
        },
        canActivate: [AuthGuardService]
    },
    {
        path: 'tagging-compliance',
        component: TaggingComplianceComponent,
        data: {
            title: 'Tagging Compliance',
            tileName: 'app-overview-tagging'
        },
        canActivate: [AuthGuardService]
    },
    {
        path: 'certificate-compliance',
        component: CertificateComplianceComponent,
        data: {
            title: 'Certificate Compliance',
            tileName: 'app-overview-certificates'
        },
        canActivate: [AuthGuardService]
    },
    {
        path: 'policy-details/:ruleID',
        component: PolicyDetailsComponent,
        data: {
            title: 'Policy Compliance View'
        },
        canActivate: [AuthGuardService]
    },
    {
        path: 'policy-knowledgebase',
        component: PolicyKnowledgebaseComponent,
        data: {
            title: 'Policy Knowledgebase'
        },
        canActivate: [AuthGuardService]
    },
    {
        path: 'policy-knowledgebase-details/:ruleID/:autoFix',
        component: PolicyKnowledgebaseDetailsComponent,
        data: {
            title: 'Policy Details'
        },
        canActivate: [AuthGuardService]
    },
    {
        path: 'health-notifications',
        component: CloudNotificationsComponent,
        data: {
            title: 'Health Notifications'
        },
        canActivate: [AuthGuardService]
    },
    {
        path: 'event-details/:arn',
        component: EventDetailsComponent,
        data: {
            title: 'Event Details'
        },
        canActivate: [AuthGuardService]
    },
    {
        path: 'certificates',
        component: CertificatesComponent,
        data: {
            title: 'Certificates List'
        },
        canActivate: [AuthGuardService]
    },
    {
        path: 'vulnerabilities',
        component: VulnerabilitiesComponent,
        data: {
            title: 'Vulnerabilities List'
        },
        canActivate: [AuthGuardService]
    },
    {
        path: 'patching-projections',
        component: PatchingProjectionsComponent,
        data: {
            title: 'Patching Projections',
            roles: ['ROLE_ONPREM_ADMIN']
        },
        canActivate: [AuthGuardService]
    },
    {
        path: 'dev-standard-dashboard',
        component: DigitalDevDashboardComponent,
        data: {
            title: 'Digital Dev Dashboard'
        },
        canActivate: [AuthGuardService]
    },
    {
        path: 'vulnerabilities/vulnerability-details/:qid',
        component: VulnerabilityDetailsComponent,
        data: {
            title: 'Vulnerability Details'
        },
        canActivate: [AuthGuardService]
    },
    {
        path: 'vulnerabilities-compliance/:details',
        component: VulnerabilitiesComponent,
        data: {
            title: 'Vulnerabilities'
        },
        canActivate: [AuthGuardService]
    },
    {
        path: 'recommendations',
        component: RecommendationsComponent,
        data: {
            title: 'Recommendations',
            pageLevel: 0
        },
        canActivate: [AuthGuardService]
    },
    {
            path: 'recommendations-detail/:recommendationId/:name/:general',
            component: RecommendationsDetailsComponent,
            data: {
                title: 'Recommendations Detail',
            },
            canActivate: [AuthGuardService]
    },
];

export const ASSETS_ROUTES = [
    {
        path: 'asset-dashboard',
        component: AssetDashboardComponent,
        children: [
            {
                path: 'all-resources',
                component: ViewAllResourcesComponent,
                outlet: 'modal'
            }
        ],
        data: {
            title: 'Asset Dashboard'
        },
        canActivate: [AuthGuardService]
    },
    {
        path: 'assets-details/:resourceType/:resourceId',
        component: AssetDetailsComponent,
        data: {
            title: 'Asset 360'
        },
        canActivate: [AuthGuardService]
    },
    {
        path: 'asset-list',
        component: AssetListComponent,
        data: {
            title: 'Asset List'
        },
        canActivate: [AuthGuardService]
    },
    {
        path: 'update-assets',
        component: OnpremAssetsComponent,
        canActivate: [AuthGuardService, PermissionGuardService],
        data: {
            title: 'Update Asset Data',
            roles: ['ROLE_ONPREM_ADMIN']
        },
    },
    {
        path: 'assets-details/:resourceType/:resourceId/aws-notifications',
        component: AwsNotificationsComponent,
        data: {
            title: 'Aws Notifications List'
        },
        canActivate: [AuthGuardService]
    }
];

export const TOOLS_ROUTES = [
    {
        path: 'tools-landing',
        component: ToolsLandingPageComponent,
        data: {
            title: 'Tools Overview'
        },
        canActivate: [AuthGuardService]
    }
];

export const OMNISEARCH_ROUTES = [
    {
        path: 'omni-search-page',
        component: OmniSearchPageComponent,
        canActivate: [AuthGuardService]
    },
    {
        path: 'omni-search-details/:filterValue/:searchText',
        component: OmniSearchDetailsComponent,
        canActivate: [AuthGuardService]
    }
];


export const ADMIN_ROUTES = [
    {
        path: 'policies',
        component: PoliciesComponent,
        data: {
            title: 'Policies',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'rules',
        component: RulesComponent,
        data: {
            title: 'Rules',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'create-rule',
        component: CreateRuleComponent,
        data: {
            title: 'Create Rule',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'update-rule',
        component: UpdateRuleComponent,
        data: {
            title: 'Update Rule',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'invoke-rule',
        component: InvokeRuleComponent,
        data: {
            title: 'Invoke Rule',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'create-edit-policy',
        component: CreateEditPolicyComponent,
        data: {
            title: 'Create Edit Policy',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'job-execution-manager',
        component: JobExecutionManagerComponent,
        data: {
            title: 'Job Execution Manager',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'create-job-execution-manager',
        component: CreateJobExecutionManagerComponent,
        data: {
            title: 'Create Job Execution Manager',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'create-asset-groups',
        component: CreateAssetGroupsComponent,
        data: {
            title: 'Create Asset Groups',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'asset-groups',
        component: AssetGroupsComponent,
        data: {
            title: 'Asset Groups',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'delete-asset-groups',
        component: DeleteAssetGroupsComponent,
        data: {
            title: 'Delete Asset Groups',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'roles',
        component: RolesComponent,
        data: {
            title: 'Roles',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'create-update-roles',
        component: CreateUpdateRolesComponent,
        data: {
            title: 'Create Update Roles',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'roles-allocation',
        component: RolesAllocationComponent,
        data: {
            title: 'User Roles Allocation',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'sticky-exceptions',
        component: StickyExceptionsComponent,
        data: {
            title: 'Sticky Exceptions',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'create-sticky-exceptions',
        component: CreateStickyExceptionsComponent,
        data: {
            title: 'Create Sticky Exceptions',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'delete-sticky-exceptions',
        component: DeleteStickyExceptionsComponent,
        data: {
            title: 'Delete Sticky Exceptions',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'enable-disable-rule',
        component: EnableDisableRuleComponent,
        data: {
            title: 'Enable Disable Rule',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'domains',
        component: DomainsComponent,
        data: {
            title: 'Domains',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'create-update-domain',
        component: CreateUpdateDomainComponent,
        data: {
            title: 'Create Update Domain',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'target-types',
        component: TargetTypesComponent,
        data: {
            title: 'Target Types',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'create-update-target-type',
        component: CreateUpdateTargetTypesComponent,
        data: {
            title: 'Create Update Target Type',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'update-job-execution-manager',
        component: UpdateJobExecutionManagerComponent,
        data: {
            title: 'Update Job Execution Manager',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'config-users',
        component: ConfigUsersComponent,
        data: {
            title: 'Config Users',
            roles: ['ROLE_ADMIN']
        }
    },
    /*{
        path: 'account-management',
        component: AccountManagementComponent,
        data: {
            title: 'Account Management',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'account-management-details/:id',
        component: AccountManagementDetailsComponent,
        data: {
            title: 'Account Management Details',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'account-management-create',
        component: AccountManagementDetailsComponent,
        data: {
            title: 'Account Management Details',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'plugin-management',
        component: PluginManagementComponent,
        data: {
            title: 'Plugin Management',
            roles: ['ROLE_ADMIN']
        }
    },
    {
        path: 'plugin-management-details/:pluginId',
        component: PluginManagementDetailsComponent,
        data: {
            title: 'Plugin Management Details',
            roles: ['ROLE_ADMIN']
        }
    }*/
    {
        path: 'config-management',
        component: ConfigManagementComponent,
        data: {
            title: 'Configuration Management',
            roles: ['ROLE_ADMIN'],
            pageLevel: 0
        }
    },
    {
        path: 'system-management',
        component: SystemManagementComponent,
        data: {
            title: 'System Management',
            roles: ['ROLE_ADMIN']
        }
    }
];
