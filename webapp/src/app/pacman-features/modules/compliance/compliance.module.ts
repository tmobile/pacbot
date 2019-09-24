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
import { SharedModule } from './../../../shared/shared.module';
import { ComplianceDashboardComponent } from './compliance-dashboard/compliance-dashboard.component';
import { ComplianceRoutingModule } from './compliance-routing.module';
import { ComplianceComponent } from './compliance.component';
import { ComplianceIssuesComponent } from './../../secondary-components/compliance-issues/compliance-issues.component';
import { PacmanIssuesComponent } from './../../secondary-components/pacman-issues/pacman-issues.component';
import { AutofixScheduleComponent } from './../../secondary-components/autofix-schedule/autofix-schedule.component';
import { MultilineBrushZoomComponent } from './../../secondary-components/multiline-brush-zoom/multiline-brush-zoom.component';
import { MultiBandDonutComponent } from './../../secondary-components/multi-band-donut/multi-band-donut.component';
import { IssuesCategoryComponent } from './../../secondary-components/issues-category/issues-category.component';
import { OverviewPatchingComponent } from './../../secondary-components/overview-patching/overview-patching.component';
import { OverviewTaggingComponent } from './../../secondary-components/overview-tagging/overview-tagging.component';
import { OverviewCertificatesComponent } from './../../secondary-components/overview-certificates/overview-certificates.component';
import { OverviewVulnerabilitiesComponent } from './../../secondary-components/overview-vulnerabilities/overview-vulnerabilities.component';
import { SelectComplianceDropdown } from './../../services/select-compliance-dropdown.service';
import { IssueListingComponent } from './issue-listing/issue-listing.component';
import { IssueDetailsComponent } from './issue-details/issue-details.component';
import { VulnerabilitiesComplianceComponent } from './vulnerabilities-compliance/vulnerabilities-compliance.component';
import { IssueBlocksComponent } from './../../secondary-components/issue-blocks/issue-blocks.component';
import { DetailsInfoComponent } from './../../secondary-components/details-info/details-info.component';
import { VulnerabilityIssueComponent } from './../../secondary-components/vulnerability-issue/vulnerability-issue.component';
import { VulnerabilityOverallComponent } from './../../secondary-components/vulnerability-overall/vulnerability-overall.component';
import { VulnerabilityAcrossApplicationComponent } from './../../secondary-components/vulnerability-across-application/vulnerability-across-application.component';
import { ListTableComponent } from './../../secondary-components/list-table/list-table.component';
import { IssuesTrendHistoryComponent } from './../../secondary-components/issues-trend-history/issues-trend-history.component';
import { AllVulnerabilityTableComponent } from './../../secondary-components/all-vulnerability-table/all-vulnerability-table.component';
import { VulnerabilityTrendComponent } from './../../secondary-components/vulnerability-trend/vulnerability-trend.component';
import { PatchingComplianceComponent } from './patching-compliance/patching-compliance.component';
import { TaggingComplianceComponent } from './tagging-compliance/tagging-compliance.component';
import { CloudNotificationsComponent } from './cloud-notifications/cloud-notifications.component';
import { EventDetailsComponent } from './event-details/event-details.component';
import { CertificateComplianceComponent } from './certificate-compliance/certificate-compliance.component';
import { WindowRefService } from './../../services/window.service';
import { AllPatchingTableComponent } from './../../secondary-components/all-patching-table/all-patching-table.component';
import { PatchingIssueComponent } from './../../secondary-components/patching-issue/patching-issue.component';
import { PatchingCurrentStateComponent } from './../../secondary-components/patching-current-state/patching-current-state.component';
import { StateTableComponent } from './../../secondary-components/state-table/state-table.component';
import { OverallComplianceComponent } from './../../secondary-components/overall-compliance/overall-compliance.component';
import { PatchingTrendComponent } from './../../secondary-components/patching-trend/patching-trend.component';
import { PatchingGraphComponent } from './../../secondary-components/patching-graph/patching-graph.component';
import { HighlightModule } from 'ngx-highlight';
import { CertificateSummaryComponent } from './../../secondary-components/certificate-summary/certificate-summary.component';
import { CertificateStageComponent } from './../../secondary-components/certificate-stage/certificate-stage.component';
import { ProgressSummaryComponent } from './../../secondary-components/progress-summary/progress-summary.component';
import { AllCertificateTableComponent } from './../../secondary-components/all-certificate-table/all-certificate-table.component';
import { CertificateTrendComponent } from './../../secondary-components/certificate-trend/certificate-trend.component';
import { QuarterGraphComponent } from './../../secondary-components/quarter-graph/quarter-graph.component';
import { PolicyDetailsComponent } from './policy-details/policy-details.component';
import { PolicySummaryComponent } from './../../secondary-components/policy-summary/policy-summary.component';
import { PolicyAcrossApplicationComponent } from './../../secondary-components/policy-across-application/policy-across-application.component';
import { AllPolicyViolationsComponent } from './../../secondary-components/all-policy-violations/all-policy-violations.component';
import { PolicyTrendComponent } from './../../secondary-components/policy-trend/policy-trend.component';
import { TaggingTrendComponent } from './../../secondary-components/tagging-trend/tagging-trend.component';
import { TaggingSummaryComponent } from './../../secondary-components/tagging-summary/tagging-summary.component';
import { TotalTagComplianceComponent } from './../../secondary-components/total-tag-compliance/total-tag-compliance.component';
import { PolicyContentSliderComponent } from './../../secondary-components/policy-content-slider/policy-content-slider.component';
import { TargetTypeTaggingTileComponent } from './../../secondary-components/target-type-tagging-tile/target-type-tagging-tile.component';
import { TaggingAcrossTargetTypeComponent } from './../../secondary-components/tagging-across-target-type/tagging-across-target-type.component';
import { TaggingInstancesTableComponent } from './../../secondary-components/tagging-instances-table/tagging-instances-table.component';
import { ComplianceOverviewTrendComponent } from './../../secondary-components/compliance-overview-trend/compliance-overview-trend.component';
import { TaggingComplianceTrendComponent } from './../../secondary-components/tagging-compliance-trend/tagging-compliance-trend.component';
import { VulnerabilitiesComplianceTrendComponent } from './../../secondary-components/vulnerabilities-compliance-trend/vulnerabilities-compliance-trend.component';
import { CertificatesComplianceTrendComponent } from './../../secondary-components/certificates-compliance-trend/certificates-compliance-trend.component';
import { PolicyKnowledgebaseComponent } from './policy-knowledgebase/policy-knowledgebase.component';
import { PolicyKnowledgebaseDetailsComponent } from './policy-knowledgebase-details/policy-knowledgebase-details.component';
import { CertificateAssetsTrendComponent } from './../../secondary-components/certificate-assets-trend/certificate-assets-trend.component';
import { TaggingAssetsTrendComponent } from './../../secondary-components/tagging-assets-trend/tagging-assets-trend.component';
import { VulnerabilityAssetsTrendComponent } from './../../secondary-components/vulnerability-assets-trend/vulnerability-assets-trend.component';
import { PolicyAssetsTrendComponent } from './../../secondary-components/policy-assets-trend/policy-assets-trend.component';
import { VulnerabilitiesComponent } from './vulnerabilities/vulnerabilities.component';
import { CertificatesComponent } from './certificates/certificates.component';
import { OnpremPatchingGraphComponent } from './../../secondary-components/onprem-patching-graph/onprem-patching-graph.component';
import { PatchingSponsorComponent } from './../../secondary-components/patching-sponsor/patching-sponsor.component';
import { PatchingSnapshotComponent } from './../../secondary-components/patching-snapshot/patching-snapshot.component';
import { PatchingProjectionsComponent } from './patching-projections/patching-projections.component';
import { VulnerabilitySummaryTableComponent } from './../../secondary-components/vulnerability-summary-table/vulnerability-summary-table.component';
import { AgGridModule } from 'ag-grid-angular/main';
import { DigitalDevDashboardComponent } from './digital-dev-dashboard/digital-dev-dashboard.component';
import { PullRequestLineMetricsComponent } from './../../secondary-components/pull-request-line-metrics/pull-request-line-metrics.component';
import { DigitalApplicationDistributionComponent } from './../../secondary-components/digital-application-distribution/digital-application-distribution.component';
import { DigitalDevStrategyDistributionComponent } from './../../secondary-components/digital-dev-strategy-distribution/digital-dev-strategy-distribution.component';
import { VulnerabilityAgingGraphComponent } from './../../secondary-components/vulnerability-aging-graph/vulnerability-aging-graph.component';
import { DevStandardPullRequestAgeComponent } from './../../secondary-components/dev-standard-pull-request-age/dev-standard-pull-request-age.component';
import { DevStandardStaleBranchAgeComponent } from './../../secondary-components/dev-standard-stale-branch-age/dev-standard-stale-branch-age.component';
import { DevStandardTotalStaleBranchesComponent } from './../../secondary-components/dev-standard-total-stale-branches/dev-standard-total-stale-branches.component';
import { DevPullRequestApplicationsComponent } from './../../secondary-components/dev-pull-request-applications/dev-pull-request-applications.component';
import { DevStaleBranchApplicationsComponent } from './../../secondary-components/dev-stale-branch-applications/dev-stale-branch-applications.component';
import { DigitalDevManagementService } from '../../services/digital-dev-management.service';
import { VulnerabilityDetailsComponent } from './vulnerability-details/vulnerability-details.component';
import { PolicyViolationDescComponent } from './../../secondary-components/policy-violation-desc/policy-violation-desc.component';
import { IssueExceptionComponent } from './../../secondary-components/issue-exception/issue-exception.component';
import { PolicyViolationsListComponent } from './policy-violations-list/policy-violations-list.component';
import { IssueListingService } from '../../services/issue-listing.service';
import { RecommendationsComponent} from '../../modules/compliance/recommendations/recommendations.component';
import { RecommandCategoryComponent } from '../../secondary-components/recommand-category/recommand-category.component';
import { RecommendationsDetailsComponent } from './recommendations-details/recommendations-details.component';
import { OverallVulnerabilitiesComponent } from './../../secondary-components/overall-vulnerabilities/overall-vulnerabilities.component';

@NgModule({
  imports: [
    CommonModule,
    ComplianceRoutingModule,
    SharedModule,
    HighlightModule,
    AgGridModule.withComponents([
      PatchingSnapshotComponent,
      PatchingSponsorComponent,
      VulnerabilitySummaryTableComponent,
      DevPullRequestApplicationsComponent,
      DevStaleBranchApplicationsComponent
    ])
  ],
  declarations: [
    ComplianceIssuesComponent,
    PacmanIssuesComponent,
    MultilineBrushZoomComponent,
    MultiBandDonutComponent,
    IssuesCategoryComponent,
    ComplianceComponent,
    ComplianceDashboardComponent,
    OverviewPatchingComponent,
    OverviewTaggingComponent,
    OverviewCertificatesComponent,
    OverviewVulnerabilitiesComponent,
    IssueListingComponent,
    IssueDetailsComponent,
    VulnerabilitiesComplianceComponent,
    AutofixScheduleComponent,
    IssueBlocksComponent,
    DetailsInfoComponent,
    VulnerabilityIssueComponent,
    VulnerabilityOverallComponent,
    VulnerabilityAcrossApplicationComponent,
    ListTableComponent,
    IssuesTrendHistoryComponent,
    AllVulnerabilityTableComponent,
    VulnerabilityTrendComponent,
    PatchingComplianceComponent,
    TaggingComplianceComponent,
    CloudNotificationsComponent,
    CertificateComplianceComponent,
    EventDetailsComponent,
    AllPatchingTableComponent,
    PatchingIssueComponent,
    PatchingCurrentStateComponent,
    StateTableComponent,
    OverallComplianceComponent,
    PatchingTrendComponent,
    PatchingGraphComponent,
    CertificateSummaryComponent,
    CertificateStageComponent,
    ProgressSummaryComponent,
    AllCertificateTableComponent,
    CertificateTrendComponent,
    QuarterGraphComponent,
    PolicyDetailsComponent,
    PolicySummaryComponent,
    PolicyAcrossApplicationComponent,
    AllPolicyViolationsComponent,
    PolicyTrendComponent,
    TaggingTrendComponent,
    TaggingSummaryComponent,
    TotalTagComplianceComponent,
    PolicyContentSliderComponent,
    TargetTypeTaggingTileComponent,
    TaggingAcrossTargetTypeComponent,
    TaggingInstancesTableComponent,
    ComplianceOverviewTrendComponent,
    TaggingComplianceTrendComponent,
    VulnerabilitiesComplianceTrendComponent,
    CertificatesComplianceTrendComponent,
    PolicyKnowledgebaseComponent,
    PolicyKnowledgebaseDetailsComponent,
    CertificateAssetsTrendComponent,
    TaggingAssetsTrendComponent,
    OnpremPatchingGraphComponent,
    PullRequestLineMetricsComponent,
    VulnerabilityAssetsTrendComponent,
    PolicyAssetsTrendComponent,
    VulnerabilitiesComponent,
    CertificatesComponent,
    PatchingSnapshotComponent,
    PatchingProjectionsComponent,
    PatchingSponsorComponent,
    VulnerabilitySummaryTableComponent,
    DigitalDevDashboardComponent,
    DigitalApplicationDistributionComponent,
    DigitalDevStrategyDistributionComponent,
    VulnerabilityAgingGraphComponent,
    DevStandardPullRequestAgeComponent,
    DevStandardStaleBranchAgeComponent,
    DevStandardTotalStaleBranchesComponent,
    DevPullRequestApplicationsComponent,
    DevStaleBranchApplicationsComponent,
    VulnerabilityDetailsComponent,
    PolicyViolationDescComponent,
    IssueExceptionComponent,
    PolicyViolationsListComponent,
    RecommendationsComponent,
    RecommandCategoryComponent,
    RecommendationsDetailsComponent,
    OverallVulnerabilitiesComponent
  ],
  providers: [
    SelectComplianceDropdown,
    WindowRefService,
    DigitalDevManagementService,
    IssueListingService
  ]
})
export class ComplianceModule {}
