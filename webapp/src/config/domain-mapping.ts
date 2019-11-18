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

import {THEMES} from './themes';

export const COMMON_PAGES = [
    'compliance-dashboard',
    'policy-details',
    'issue-listing',
    'issue-details',
    'policy-knowledgebase',
    'policy-knowledgebase-details',
    'asset-dashboard',
    'asset-list',
    'tools-landing',
    'omni-search-page'
];

export const  DOMAIN_MAPPING = [
    {
        'theme': THEMES.default,
        'dashboards': [
            {
                'moduleName': 'compliance',
                'dashboards': [
                    {
                        'route': 'compliance-dashboard',
                        'sequence': 0
                    },
                    {
                        'route': 'issue-listing',
                        'sequence': 1
                    },
                    {
                        'route': 'policy-knowledgebase',
                        'sequence': 2
                    },
                    {
                        'route': 'recommendations',
                        'sequence': 3,
                        'groupBy': 'compliance-dashboard'
                    },
                    {
                        'route': 'vulnerabilities-compliance',
                        'sequence': 4,
                        'groupBy': 'compliance-dashboard'
                    },
                ]
            },
            {
                'moduleName': 'assets',
                'dashboards': [
                    {
                        'route': 'asset-dashboard',
                        'sequence': 0
                    },
                    {
                        'route': 'asset-list',
                        'sequence': 1
                    }
                ]
            },
            {
                'moduleName': 'tools',
                'dashboards': [
                    {
                        'route': 'tools-landing',
                        'sequence': 0
                    }
                ]
            },
            {
                'moduleName': 'omnisearch',
                'dashboards': [
                    {
                        'route': 'omni-search-page',
                        'sequence': 0
                    },
                    {
                        'route': 'omni-search-details',
                        'sequence': 1
                    }
                ]
            },
            {
                'moduleName': 'admin',
                'dashboards': [
                    {
                        'route': 'policies',
                        'sequence': 0
                    },
                    {
                        'route': 'rules',
                        'sequence': 1
                    },
                    {
                        'route': 'job-execution-manager',
                        'sequence': 2
                    },
                    {
                        'route': 'domains',
                        'sequence': 3
                    },
                    {
                        'route': 'target-types',
                        'sequence': 4
                    },
                    {
                        'route': 'asset-groups',
                        'sequence': 5
                    },
                    {
                        'route': 'sticky-exceptions',
                        'sequence': 6
                    },
                    {
                        'route': 'roles',
                        'sequence': 7
                    },
                    {
                        'route': 'config-management',
                        'sequence': 8
                    },
                    /*{
                        'route': 'account-management',
                        'sequence': 8
                    },
                    {
                        'route': 'plugin-management',
                        'sequence': 9
                    }*/
                    {
                        'route': 'system-management',
                        'sequence': 10
                    }
                ]
            }
        ],
        'domain': ''
    },
    {
        'theme': THEMES.default,
        'dashboards': [
            {
                'moduleName': 'compliance',
                'dashboards': [
                    {
                        'route': 'tagging-compliance',
                        'sequence': 2
                    },
                    {
                        'route': 'health-notifications',
                        'sequence': 3,
                        'cloudSpecific': true
                    }
                ]
            },
            {
                'moduleName': 'assets',
                'dashboards': [
                ]
            }
        ],
        'domain': 'Infra & Platforms'
    },
    {
        'theme': THEMES.theme1,
        'dashboards': [
            {
                'moduleName': 'compliance',
                'dashboards': [

                ]
            }
        ],
        'domain': 'SOX'
    },
    {
        'theme': THEMES.theme2,
        'dashboards': [
            {
                'moduleName': 'compliance',
                'dashboards': [
                    {
                        'route': 'dev-standard-dashboard',
                        'sequence': 2
                    }
                ]
            }
        ],
        'domain': 'Dev Standards'
    }
];
