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

const PLACEHOLDER_USERNAME = 'User Name';
const PLACEHOLDER_PASSWORD = 'Password';
const APP_NAME = 'PacBot';

export const CONTENT = {
    'homePage': {
        'productLogo': '/assets/icons/application-logo.svg',
        'companyLogo': '/assets/icons/tmo-black.svg',
        'heading': APP_NAME,
        'subHeading': 'Policy as Code Manager',
        'awsLogo': '/assets/icons/aws-color.svg',
        'azureLogo': '/assets/icons/azure-color.svg',
        'productBrief': 'A platform for continuous compliance monitoring, compliance reporting and security automation for Cloud.',
        'applicationCore' : [
            {
                'title': 'Assess',
                'description': APP_NAME + ' continuously assess the state of the cloud and changes in real-time to assess our cloud security and governance policy compliance.',
                'icon': '/assets/icons/assess-icon.svg'
              },
              {
                'title': 'Report',
                'description': APP_NAME + ' provides an easy way to view, monitor and report on the security and compliance of the entire cloud eco-system.',
                'icon': '/assets/icons/report-icon.svg'
              },
              {
                'title': 'Remediate',
                'description': 'With detailed contextual information about the compliance violation, remediation is quick with ' + APP_NAME + '. ' + APP_NAME + ' provides auto-fixes and one click fix options.',
                'icon' : '/assets/icons/remediate-icon.svg'
              }
        ],
        'featureDetails': {
            'securityWide': {
                'image': '/assets/images/security-wide.svg',
                'Title': 'Security That Goes Wide',
                'Subtitle': 'See what’s happening across all your applications, environments, AWS accounts, regions and services.'
            },
            'securityDeep': {
                'image': '/assets/images/security-deep.svg',
                'Title': 'Security That Goes Deep',
                'Subtitle': 'Check your environment against hundreds of customizable security best practices.'
            },
            'automation': {
                'image': '/assets/images/security-auto.svg',
                'Title': 'Security Automation',
                'Subtitle': 'Continuous visibility, automated data collection, clear visualization and alerting accelerates incident response and mitigates further risks.'
            },
            'monitoring': {
                'image': '/assets/images/monitor.svg',
                'Title': 'Continuous Monitoring',
                'Subtitle': 'Real-time & continuous monitoring to detect changes as they happen.'
            },
            'reporting': {
                'image': '/assets/images/reporting.svg',
                'Title': 'Compliance Reporting',
                'Subtitle': 'Easily produce compliance reports with latest results from continuous compliance monitoring.'
            }
        },
        'keyComponents': {
            'title': '5 Key Components of ' + APP_NAME,
            'components':
            [
                {
                    'title': 'Rule Engine',
                    'description': APP_NAME + ' rule engine is 100% serverless, built using Cloudwatch events, Lambda & Elastic Container Service'
                },
                {
                    'title': 'UI',
                    'description': 'Single page application built using Angular'
                },
                {
                    'title': 'Search Engine',
                    'description': 'All analytics and omni search is powered by Elastic Search, ' + APP_NAME + ' Data Collectors and Aggregator'
                },
                {
                    'title': 'Scheduled Data Collector - AWS Batch',
                    'description': 'Scheduled jobs run in AWS batch to collect cloud inventory'
                },
                {
                    'title': 'Data Lake',
                    'description': APP_NAME + ' data lake uses a variety of storage technologies offered by AWS, they include S3, Redshift, RDS and Elastic Search'
                }
            ]
        },
        'contactUs': {
        },
        'footer': {
        }
    },
    'login': {
        'heading': 'Login with your ' + APP_NAME + ' ID',
        'usernamePlaceholder': PLACEHOLDER_USERNAME,
        'passwordPlaceholder': PLACEHOLDER_PASSWORD
    },
    'tools': {
    }
};
