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

export const environment = {
    production: true,
    base: '{{baseUrl}}',
    envName: 'dev',
    version: '2.2.0',
    users: {
        url: '{{cloudBaseUrl}}/api/platform/ad/users',
        method: 'GET'
    },
    createJira: {
        url: '{{cloudBaseUrl}}/api/pacman/create-jira-ticket',
        method: 'POST'
    },
    findJira: {
        url: '{{cloudBaseUrl}}/api/pacman/find-jira-ticket',
        method: 'POST'
    },
    login: {
        url: '{{baseUrl}}/auth/user/login',
        method: 'POST'
    },
    pacmanIssues: {
        url: '{{baseUrl}}/compliance/v1/issues/distribution',
        method: 'GET'
    },
    targetType: {
        url: '{{baseUrl}}/asset/v1/list/targettype',
        method: 'GET'
    },
    environments: {
        url: '{{baseUrl}}/asset/v1/list/environment',
        method: 'GET'
    },
    application: {
        url: '{{baseUrl}}/asset/v1/list/application',
        method: 'GET'
    },
    policyViolationGraph: {
        url: '{{baseUrl}}/compliance/v1/policyviolations/summary/{assetGroup}/{resourceType}/{resourceId}',
        method: 'GET'
    },
    PullReqLineMetrics: {
        url: '{{baseUrl}}/devstandards/v1/pullrequests/state-trend',
        method: 'GET'
    },
    updateRecentAG: {
        url: '{{baseUrl}}/asset/v1/appendToRecentlyViewedAG',
        method: 'POST'
    },
    openPorts: {
        url: '{{baseUrl}}/asset/v1/{assetGroup}/{resourceType}/{resourceId}/open-ports',
        method: 'GET'
    },
    hostVulnerabilitiesTable: {
        url: '{{baseUrl}}/vulnerability/v1/vulnerabilities/detail/{resourceId}',
        method: 'GET'
    },
    InventoryTraker: {
        url: '{{baseUrl}}/compliance/v1/tagging',
        method: 'GET'
    },
    patchingSummary: {
        url: '{{baseUrl}}/compliance/v1/patching',
        method: 'GET'
    },
    patchingProgress: {
        url: '{{baseUrl}}/compliance/v1/patching/progress',
        method: 'POST'
    },
    patchingState: {
        url: '{{baseUrl}}/compliance/v1/patching/topnoncompliantapps',
        method: 'GET'
    },
    patchingStateexec: {
        url: '{{baseUrl}}/compliance/v1/patching/topnoncompliantexecs',
        method: 'GET'
    },
    vulnerabilityTrend: {
        url: '{{baseUrl}}/vulnerability/v1/vulnerabilities/trend',
        method: 'POST'
    },
    ruleDesc: {
        url: '{{baseUrl}}/compliance/v1/policyViolationReason',
        method: 'GET'
    },
    patchingTable: {
        url: '{{baseUrl}}/compliance/v1/patching/detail',
        method: 'POST'
    },
    entity: {
        url: '{{baseUrl}}/compliance/v1/ruleParams',
        method: 'GET'
    },
    resourceDetails: {
        url: '{{baseUrl}}/compliance/v1/resourcedetails',
        method: 'GET'
    },
    email: {
        url: '{{baseUrl}}/notifications/send-mail-with-template',
        method: 'POST'
    },
    issueAudit: {
        url: '{{baseUrl}}/compliance/v1/issueauditlog',
        method: 'POST'
    },
    MultilineChartNew: {
        url: '{{baseUrl}}/asset/v1/trend/minmax',
        method: 'GET'
    },
    MultilineChartCpu: {
        url: '{{baseUrl}}/statistics/v1/cpu-utilization',
        method: 'GET'
    },
    MultilineChartNetwork: {
        url: '{{baseUrl}}/statistics/v1/network-utilization',
        method: 'GET'
    },
    MultilineChartDisk: {
        url: '{{baseUrl}}/statistics/v1/disk-utilization',
        method: 'GET'
    },
    AssetGroupApplication: {
        url: '{{baseUrl}}/asset/v1/list/application',
        method: 'GET'
    },
    AssetGroupCirtificate: {
        url: '{{baseUrl}}/compliance/v1/certificates',
        method: 'GET'
    },
    AssetGroupWaf: {
        url: '{{baseUrl}}/asset/v1/count',
        method: 'GET'
    },
    AssetGroupCrop: {
        url: '{{baseUrl}}/asset/v1/count',
        method: 'GET'
    },
    AssetDistribution: {
        url: '{{baseUrl}}/asset/v1/count/byapplication',
        method: 'GET'
    },
    complianceCategories: {
        complianceCategoriesData: {
            Vulnerabilities: {
                url: '{{baseUrl}}/vulnerability/v1/vulnerabilites',
                method: 'GET'
            },
      Tagging: {
                url: '{{baseUrl}}/compliance/v1/tagging',
                method: 'GET'
            },
      Certificates: {
                url: '{{baseUrl}}/compliance/v1/certificates',
                method: 'GET'
            },
      Patching: {
                url: '{{baseUrl}}/compliance/v1/patching',
                method: 'GET'
            }
        }
    },
    issueTrends: {
        url: '{{baseUrl}}/compliance/v1/trend/issueTrend',
        method: 'GET'
    },
    issueOverviewTrend: {
        url: '{{baseUrl}}/compliance/v1/trend/issues',
        method: 'POST'
    },
    contribution: {
        url: '{{baseUrl}}/statistics/v1/issues/rule-wise-conribution',
        method: 'GET'
    },
    severity: {
        url: '{{baseUrl}}/compliance/v1/issues/distribution',
        method: 'GET'
    },
    openIssues: {
        url: '{{baseUrl}}/compliance/v1/openissuesbyrule',
        method: 'GET'
    },
    overallCompliance: {
        url: '{{baseUrl}}/compliance/v1/overallcompliance',
        method: 'GET'
    },
    assetTiles: {
        url: '{{baseUrl}}/asset/v1/list/assetgroup',
        method: 'GET'
    },
    assetTilesdata: {
        url: '{{baseUrl}}/asset/v1/assetgroup',
        method: 'GET'
    },
    resourceCount: {
        url: '{{baseUrl}}/asset/v1/count',
        method: 'GET'
    },
    onPremGraph: {
        url: '{{baseUrl}}/compliance/v1/getPatchingAndProjectionProgress',
        method: 'GET'
    },
    resourceCategories: {
        url: '{{baseUrl}}/asset/v1/list/targettype',
        method: 'GET'
    },
    recommendationStatus: {
        url: '{{baseUrl}}/compliance/v1/recommendations',
        method: 'GET'
    },
    vulnerabilitySummary: {
        url: '{{baseUrl}}/vulnerability/v1/vulnerabilities/summary',
        method: 'GET'
    },
    issueListing: {
        url: '{{baseUrl}}/compliance/v1/issues',
        method: 'POST'
    },
    vulnerabilityAcrossApplication: {
        url: '{{baseUrl}}/vulnerability/v1/vulnerabilities/summarybyapplication',
        method: 'GET'
    },
    allVulnerability: {
        url: '{{baseUrl}}/vulnerability/v1/vulnerabilities/detail',
        method: 'POST'
    },
    saveDefaultAssetGroup: {
        url: '{{baseUrl}}/asset/v1/save-or-update/assetgroup',
        method: 'POST'
    },
    issueFilter: {
        url: '{{baseUrl}}/compliance/v1/filters',
        method: 'GET'
    },
    onpremData: {
        url: '{{baseUrl}}/asset/v1/listing/assets',
        method: 'POST'
    },
    onpremDataUpdate: {
        url: '{{baseUrl}}/asset/v1/update-asset',
        method: 'POST'
    },
    complianceTable: {
        url: '{{baseUrl}}/compliance/v1/noncompliancepolicy',
        method: 'POST'
    },
    certificateSummary: {
        url: '{{baseUrl}}/compliance/v1/certificates/summary',
        method: 'GET'
    },
    certificateStage: {
        url: '{{baseUrl}}/compliance/v1/certificates/expirybyapplication',
        method: 'GET'
    },
    certificateTable: {
        url: '{{baseUrl}}/compliance/v1/certificates/detail',
        method: 'POST'
    },
    taggingSummary: {
        url: '{{baseUrl}}/compliance/v1/tagging',
        method: 'GET'
    },
    policyViolation: {
        url: '{{baseUrl}}/compliance/v1/issues',
        method: 'POST'
    },
    taggingCompliance: {
        url: '{{baseUrl}}/compliance/v1/tagging/compliance',
        method: 'GET'
    },
    policySummary: {
        url: '{{baseUrl}}/compliance/v1/noncompliancepolicy',
        method: 'POST'
    },
    cloudNotifications : {
        url: '{{baseUrl}}/asset/v1/cloud/notifications',
        method: 'POST'
    },
    getEventDescription : {
        url: '{{baseUrl}}/asset/v1/cloud/notifications/info',
        method: 'GET'
    },
    getEventDetails : {
        url: '{{baseUrl}}/asset/v1/cloud/notifications/detail',
        method: 'GET'
    },
    getAutofixDetails : {
        url: '{{baseUrl}}/asset/v1/autofix/notifications/detail',
        method: 'POST'
    },
    cloudNotifSummary : {
        url: '{{baseUrl}}/asset/v1/cloud/notifications/summary',
        method: 'GET'
    },
    policyContentSlider: {
        url: '{{baseUrl}}/compliance/v1/policydescription',
        method: 'GET'
    },
    policyAcrossApplication: {
        url: '{{baseUrl}}/compliance/v1/policydetailsbyapplication',
        method: 'GET'
    },
    policyAcrossEnv: {
        url: '{{baseUrl}}/compliance/v1/policydetailsbyenvironment',
        method: 'GET'
    },
    complianceOverview: {
        url: '{{baseUrl}}/compliance/v1/trend/compliance',
        method: 'POST'
    },
    taggingSummaryByTargetType: {
        url: '{{baseUrl}}/compliance/v1/tagging/summarybytargettype',
        method: 'POST'
    },
    taggingSummaryByApplication: {
        url: '{{baseUrl}}/compliance/v1/tagging/summarybyapplication',
        method: 'POST'
    },
    recommendDetails: {
        url: '{{baseUrl}}/compliance/v1/recommendations/actions',
        method: 'GET'
    },
    policyTrend: {
        url: '{{baseUrl}}/compliance/v1/trend/compliancebyrule',
        method: 'POST'
    },
    statspage: {
        url: '{{baseUrl}}/statistics/v1/statsdetails',
        method: 'GET'
    },
    statspagePliciesWithAutoFixes: {
        url: '{{baseUrl}}/statistics/v1/autofixstats',
        method: 'GET'
    },
    patchingQuarter: {
        url: '{{baseUrl}}/compliance/v1/patching/quarters',
        method: 'POST'
    },
    taggingComplianceTrend: {
        url: '{{baseUrl}}/compliance/v1/trend/compliance/tagging',
        method: 'POST'
    },
    vulnerabilityComplianceTrend: {
        url: '{{baseUrl}}/vulnerability/v1/trend/compliance/vulnerabilities',
        method: 'POST'
    },
    certificatesComplianceTrend: {
        url: '{{baseUrl}}/compliance/v1/trend/compliance/certificates',
        method: 'POST'
    },
    download: {
        url: '{{baseUrl}}/compliance/v1/download/services',
        method: 'POST'
    },
    pacmanPolicyViolations: {
        url: '{{baseUrl}}/compliance/v1/policyevaluations/{assetGroup}/{resourceType}/{resourceId}',
        method: 'GET'
    },
    diskUtilizationGraph: {
        url: '{{baseUrl}}/asset/v1/{assetGroup}/{resourceType}/{resourceId}/disk-utilization',
        method: 'GET'
    },
    hostVulnerabilitiesGraph: {
        url: '{{baseUrl}}/vulnerability/v1/vulnerabilities/summary/{resourceId}',
        method: 'GET'
    },
    cpuUtilizationGraph: {
        url: '{{baseUrl}}/asset/v1/{assetGroup}/{resourceType}/{resourceId}/cpu-utilization',
        method: 'GET'
    },
    assetList: {
        url: '{{baseUrl}}/asset/v1/list/assets',
        method: 'POST'
    },
    assetListTaggable: {
        url: '{{baseUrl}}/asset/v1/list/assets/taggable',
        method: 'POST'
    },
    assetListPatchable: {
        url: '{{baseUrl}}/asset/v1/list/assets/patchable',
        method: 'POST'
    },
    assetListScanned: {
        url: '{{baseUrl}}/asset/v1/list/assets/scanned',
        method: 'POST'
    },
    assetListVulnerable: {
        url: '{{baseUrl}}/asset/v1/list/assets/vulnerable',
        method: 'POST'
    },
    vulnerabilityAcrossEnv: {
        url: '{{baseUrl}}/vulnerability/v1/vulnerabilities/summarybyenvironment',
        method: 'GET'
    },
    assetDetails: {
        url: '{{baseUrl}}/asset/v1/{assetGroup}/{resourceType}/{resourceId}/details',
        method: 'GET'
    },
    awsNotifications: {
        url: '{{baseUrl}}/asset/v1/{assetGroup}/{resourceType}/{resourceId}/aws-notifications/summary',
        method: 'GET'
    },
    installedSoftware: {
        url: '{{baseUrl}}/asset/v1/{assetGroup}/{resourceType}/{resourceId}/installed-softwares',
        method: 'GET'
    },
    assetSummary: {
        url: '{{baseUrl}}/asset/v1/{assetGroup}/{resourceType}/{resourceId}/summary',
        method: 'GET'
    },
    help: {
        url: '{{baseUrl}}/compliance/v1/faqs',
        method: 'GET'
    },
    logout: {
        url: '{{baseUrl}}/auth/user/logout-session',
        method: 'GET'
    },
    refresh: {
        url: '{{baseUrl}}/auth/user/refresh',
        method: 'POST'
    },
    assetCost: {
        url: '{{baseUrl}}/asset/v1/{assetGroup}/{resourceType}/{resourceId}/cost',
        method: 'GET'
    },
    accessGroup: {
        url: '{{baseUrl}}/asset/v1/{assetGroup}/{resourceType}/{resourceId}/ad-groups',
        method: 'GET'
    },
    omniSearch: {
        url: '{{baseUrl}}/asset/v1/search',
        method: 'POST'
    },
    awsNotificationsDetails: {
        url: '{{baseUrl}}/asset/v1/{assetGroup}/{resourceType}/{resourceId}/aws-notifications/details',
        method: 'POST'
    },
    omniSearchCategories: {
        url: '{{baseUrl}}/asset/v1/search/categories',
        method: 'GET'
    },
    patchingSnapshot: {
        url: '{{baseUrl}}/compliance/v1/getPatchingProgressByDirector',
        method: 'POST'
    },
    patchingProjections: {
        url: '{{baseUrl}}/compliance/v1/getprojection',
        method: 'GET'
    },
    updateProjections: {
        url: '{{baseUrl}}/compliance/v1/updateprojection',
        method: 'POST'
    },
    patchingSponsors: {
        url: '{{baseUrl}}/compliance/v1/getPatchingProgressByExecutiveSponsor',
        method: 'POST'
    },
    complianceTargetType: {
        url: '{{baseUrl}}/compliance/v1/targetType',
        method: 'GET'
    },
    openVulnerabilityTable: {
        url: '{{baseUrl}}/vulnerability/v1/vulnerabilities/distributionsummary',
        method: 'GET'
    },
    devStrategyDist: {
        url: '{{baseUrl}}/devstandards/v1/repository/strategies',
        method: 'GET'
    },
    devApplicationDist: {
        url: '{{baseUrl}}/devstandards/v1/repository/metrics-by-applications',
        method: 'GET'
    },
    vulnerabilityAgingSummary: {
        url: '{{baseUrl}}/vulnerability/v1/vulnerabilities/aging/summary',
        method: 'GET'
    },
    vulnerabilityAgingDistributionSummary: {
        url: '{{baseUrl}}/vulnerability/v1/vulnerabilities/aging/distributionsummary',
        method: 'GET'
    },
    devStandardPullRequestAge: {
        url: '{{baseUrl}}/devstandards/v1/pullrequests/age-range',
        method: 'GET'
    },
    devStandardBranchAgeRange: {
        url: '{{baseUrl}}/devstandards/v1/branch/stale/age-range',
        method: 'GET'
    },
    devStandardPullRequestApplications: {
        url: '{{baseUrl}}/devstandards/v1/pullrequests/metrics-by-applications',
        method: 'GET'
    },
    devTotalStaleBranches: {
        url: '{{baseUrl}}/devstandards/v1/branch/stale',
        method: 'GET'
    },
    devStandardStaleBranchApplications: {
        url: '{{baseUrl}}/devstandards/v1/branch/stale/metrics-by-applications',
        method: 'GET'
    },

    enableDisableRuleOrJob: {
        url: '{{baseUrl}}/admin/enable-disable',
        method: 'POST'
    },
    listUsers: {
        url: '{{baseUrl}}/admin/users/list-users',
        method: 'GET'
    },
    jobDetailsById: {
        url: '{{baseUrl}}/admin/job-execution-manager/details-by-id',
         method: 'GET'
    },
    allJobIdList: {
        url: '{{baseUrl}}/admin/job-execution-manager/job-ids',
        method: 'GET'
    },
    updateJob: {
        url: '{{baseUrl}}/admin/job-execution-manager/update',
        method: 'POST'
    },
    createRule: {
        url: '{{baseUrl}}/admin/rule/create',
        method: 'POST'
    },
    getRuleById: {
        url: '{{baseUrl}}/admin/rule/details-by-id',
        method: 'GET'
    },
    policyDetails: {
        url: '{{baseUrl}}/admin/policy/list',
        method: 'GET'
    },
    allPolicyIds: {
        url: '{{baseUrl}}/admin/policy/list-ids',
        method: 'GET'
    },
    ruleDetails: {
        url: '{{baseUrl}}/admin/rule/list',
        method: 'GET'
    },
    targetTypesByDatasource: {
        url: '{{baseUrl}}/admin/target-types/list-names-by-datasource',
        method: 'GET'
    },
    assetGroupNames: {
        url: '{{baseUrl}}/admin/asset-group/list-names',
        method: 'GET'
    },
    datasourceDetails: {
        url: '{{baseUrl}}/admin/datasource/list',
        method: 'GET'
    },
    allAlexaKeywords: {
        url: '{{baseUrl}}/admin/rule/alexa-keywords',
        method: 'GET'
    },
    allJobSchedulerList: {
        url: '{{baseUrl}}/admin/job-execution-manager/list',
        method: 'GET'
    },
    createJob: {
        url: '{{baseUrl}}/admin/job-execution-manager/create',
        method: 'POST'
    },
    updateRule: {
        url: '{{baseUrl}}/admin/rule/update',
        method: 'POST'
    },
    invokeRule: {
        url: '{{baseUrl}}/admin/rule/invoke',
        method: 'POST'
    },
    createPolicy: {
        url: '{{baseUrl}}/admin/policy/create',
        method: 'POST'
    },
    updatePolicy: {
        url: '{{baseUrl}}/admin/policy/update',
        method: 'POST'
    },
    getPolicyById: {
        url: '{{baseUrl}}/admin/policy/details-by-id',
        method: 'GET'
    },
    enableDisableRule: {
        url: '{{baseUrl}}/admin/rule/enable-disable',
        method: 'POST'
    },
    assetGroups: {
        url: '{{baseUrl}}/admin/asset-group/list',
        method: 'GET'
    },
    domains: {
        url: '{{baseUrl}}/admin/domains/list',
        method: 'GET'
    },
    domainsDetails: {
        url: '{{baseUrl}}/admin/domains/list-details',
        method: 'GET'
    },
    createDomain: {
        url: '{{baseUrl}}/admin/domains/create',
        method: 'POST'
    },
    updateDomain: {
        url: '{{baseUrl}}/admin/domains/update',
        method: 'POST'
    },
    domainDetailsByName: {
        url: '{{baseUrl}}/admin/domains/list-by-domain-name',
        method: 'GET'
    },
    targetTypesDetails: {
        url: '{{baseUrl}}/admin/target-types/list',
        method: 'GET'
    },
    targetTypesByDomains: {
        url: '{{baseUrl}}/admin/target-types/list-by-domains',
        method: 'POST'
    },
    targetTypesAttributes: {
        url: '{{baseUrl}}/admin/target-types/list-target-type-attributes',
        method: 'POST'
    },
    getTargetTypesByAssetGroupName: {
        url: '{{baseUrl}}/admin/target-types/list-by-asset-group-name',
        method: 'GET'
    },
    getTargetTypesCategories: {
        url: '{{baseUrl}}/admin/target-types/list-categories',
        method: 'GET'
    },
    getTargetTypesByName: {
        url: '{{baseUrl}}/admin/target-types/list-by-target-type-name',
        method: 'GET'
    },
    createTargetType: {
        url: '{{baseUrl}}/admin/target-types/create',
        method: 'POST'
    },
    updateTargetType: {
        url: '{{baseUrl}}/admin/target-types/update',
        method: 'POST'
    },
    getAllAssetGroupExceptionDetails: {
        url: '{{baseUrl}}/admin/asset-group-exception/list',
        method: 'GET'
    },
    createAssetGroups: {
        url: '{{baseUrl}}/admin/asset-group/create',
        method: 'POST'
    },
    listTargetTypeAttributeValues: {
        url: '{{baseUrl}}/admin/target-types/list-target-type-attributes-values',
        method: 'POST'
    },
    roles: {
        url: '{{baseUrl}}/admin/roles/list',
        method: 'GET'
    },
    createRole : {
        url: '{{baseUrl}}/admin/roles/create',
        method: 'POST'
    },
    updateRole : {
        url: '{{baseUrl}}/admin/roles/update',
        method: 'POST'
    },
    rolesAllocation: {
        url: '{{baseUrl}}/admin/users-roles/list',
        method: 'GET'
    },
    configUserRolesAllocation: {
        url: '{{baseUrl}}/admin/users-roles/allocate',
        method: 'POST'
    },
    configureStickyException : {
        url: '{{baseUrl}}/admin/asset-group-exception/configure',
        method: 'POST'
    },
    deleteStickyException : {
        url: '{{baseUrl}}/admin/asset-group-exception/delete',
        method: 'POST'
    },
    getAllStickyExceptionDetails: {
        url: '{{baseUrl}}/admin/asset-group-exception/list-by-name-and-datasource',
        method: 'GET'
    },
    getAllStickyExceptionNames: {
        url: '{{baseUrl}}/admin/asset-group-exception/exception-names',
        method: 'GET'
    },
    getAllDomainNames: {
        url: '{{baseUrl}}/admin/domains/domain-names',
        method: 'GET'
    },
    getAllRuleIds: {
        url: '{{baseUrl}}/admin/rule/rule-ids',
        method: 'GET'
    },
    assetGroupDetailsById: {
        url: '{{baseUrl}}/admin/asset-group/list-by-id-and-datasource',
        method: 'GET'
    },
    updateAssetGroups: {
        url: '{{baseUrl}}/admin/asset-group/update',
        method: 'POST'
    },
    deleteAssetGroups: {
        url: '{{baseUrl}}/admin/asset-group/delete',
        method: 'POST'
    },
    getRoleById: {
        url: '{{baseUrl}}/admin/roles/details-by-id',
        method: 'GET'
    },
    vulnerabilityQidDetails: {
        url: '{{baseUrl}}/vulnerability/v1/vulnerabilities/qids',
        method: 'GET'
    },
    applicationUntagged: {
        url: '{{baseUrl}}/compliance/v1/tagging/taggingByApplication',
        method: 'GET'
    },
    PullReqLineTrend : {
        url: '{{baseUrl}}/devstandards/v1/pullrequests/asset/bystates',
        method: 'POST'
    },
    PullReqAge : {
        url: '{{baseUrl}}/devstandards/v1/pullrequests/assets/openstate',
        method: 'POST'
    },
    VulnerabilitiesDistributionEnv: {
        url: '{{baseUrl}}/vulnerability/v1/vulnerabilities/distribution-env',
        method: 'GET'
    },
    VulnerabilitiesDistributionInfra: {
        url: '{{baseUrl}}/vulnerability/v1/vulnerabilities/distribution-infra',
        method: 'GET'
    },
    VulnerabilitiesDistributionVulnType: {
        url: '{{baseUrl}}/vulnerability/v1/vulnerabilities/distribution-vulntype',
        method: 'GET'
    },
    remediationTable : {
        url: '{{baseUrl}}/vulnerability/v1/vulnerabilities/remediations/summary',
        method: 'GET'
    },
    postPlugins : {
        url: '{{baseUrl}}/admin/plugin/v1/updateplugins',
        method: 'POST'
    },
    performersTable : {
        url: '{{baseUrl}}/vulnerability/v2/vulnerabilities/performers',
        method: 'GET'
    },
    vulnReportGraph : {
      url: '{{baseUrl}}/vulnerability/v1/vulnerabilities/trend/open-new',
      method: 'POST'
    },
    getVulnTrendNotes : {
        url: '{{baseUrl}}/vulnerability/v1/vulnerabilities/trend/notes',
        method: 'GET'
    },
    postVulnTrendNotes : {
        url: '{{baseUrl}}/vulnerability/v1/vulnerabilities/trend/notes',
        method: 'POST'
    },
    azureAuthorize : {
        url: '{{baseUrl}}/auth/user/authorize',
        method: 'POST'
    },
    deleteVulnNote : {
        url: '{{baseUrl}}/vulnerability/v1/vulnerabilities/trend/notes',
        method: 'DELETE'
    },
    devDistribution: {
        url: '{{baseUrl}}/devstandards/v1/repositories/assets/repositoryorapplicationdistribution',
        method: 'POST'
    },
    revokeIssueException : {
        url: '{{baseUrl}}/compliance/v2/issue/revoke-exception',
        method: 'POST'
    },
    addIssueException : {
        url: '{{baseUrl}}/compliance/v2/issue/add-exception',
        method: 'POST'
    },
    getPlugins : {
        url: '{{baseUrl}}/admin/plugin/v1/plugins',
        method: 'GET'
    },
    getAccounts : {
        url: '{{baseUrl}}/admin/awsaccounts',
        method: 'GET'
    },
    updateAccount : {
        url: '{{baseUrl}}/admin/awsaccounts',
        method: 'PUT'
    },
    createAccount : {
        url: '{{baseUrl}}/admin/awsaccounts',
        method: 'POST'
    },
    deleteAccounts : {
        url: '{{baseUrl}}/admin/awsaccounts/{{accountId}}',
        method: 'DELETE'
    },
    roleAndDefaultAssetGroup : {
        url: '{{baseUrl}}/admin/users/list',
        method: 'GET'
    },
    ruleCategory : {
        url: '{{baseUrl}}/admin/rule/categories',
        method: 'GET'
    },
    systemOperations: {
        url: '{{baseUrl}}/admin/operations',
        method: 'POST'
    },
    systemJobStatus: {
        url: '{{baseUrl}}/admin/system/status',
        method: 'GET'
    },
    getConfigProperties : {
        url: '{{baseUrl}}/admin/config-properties',
        method: 'GET'
    },
    updateConfigProperties: {
        url: '{{baseUrl}}/admin/config-properties',
        method: 'PUT'
    },
    createConfigProperties: {
        url: '{{baseUrl}}/admin/config-properties',
        method: 'POST'
    },
    getConfigkeys: {
        url: '{{baseUrl}}/admin/config-properties/keys',
        method: 'GET'
    },
    deleteConfigKey: {
        url: '{{baseUrl}}/admin/config-properties',
        method: 'DELETE'
    },
    auditTrailConfigProperties: {
        url: '{{baseUrl}}/admin/config-properties/audittrail',
        method: 'GET'
    },
    rollbackConfigProperties: {
        url: '{{baseUrl}}/admin/config-properties/rollback',
        method: 'PUT'
    },
    recommendationSummary : {
        url: '{{baseUrl}}/asset/v1/recommendations/summary',
        method: 'GET'
    },
    recommendationApplication : {
        url: '{{baseUrl}}/asset/v1/recommendations/summaryByApplication',
        method: 'GET'
    },
    recommendations : {
        url: '{{baseUrl}}/asset/v1/recommendations',
        method: 'POST'
    },
    costApplications : {
        url: '{{baseUrl}}/asset/v1/costByApplication',
        method: 'GET'
    },
    recommendationsInfo : {
        url: '{{baseUrl}}/asset/v1/recommendations/info',
        method: 'GET'
    },
    recommendationsDetails: {
        url: '{{baseUrl}}/asset/v1/recommendations/detail',
        method: 'POST'
    },
    vulnerabilityGraphSummary: {
        url: '{{baseUrl}}/vulnerability/v1/vulnerabilities/summarybyassets',
        method: 'GET'
    }
};
