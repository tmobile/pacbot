[![Latest release](https://img.shields.io/github/release/tmobile/pacbot.svg)](https://github.com/tmobile/pacbot/releases/latest)
[![Build Status](https://travis-ci.com/tmobile/pacbot.svg?token=k3NCeDUn4HM7urPbq4oz&branch=master)](https://travis-ci.com/tmobile/pacbot)
[![GitHub license](https://github.com/tmobile/pacbot/blob/master/wiki/license_apache.svg)](https://github.com/tmobile/pacbot/blob/master/LICENSE)
[![GitHub contributors](https://img.shields.io/github/contributors/tmobile/pacbot.svg)](https://github.com/tmobile/pacbot/graphs/contributors)
[![Gitter](https://github.com/tmobile/pacbot/blob/master/wiki/images/chat.svg)](https://gitter.im/TMO-OSS/PacBot)

 
<img src="./wiki/images/banner_magenta.png">

# Introduction
Policy as Code Bot (PacBot) is a platform for continuous compliance monitoring, compliance reporting and security automation for the cloud. In PacBot, security and compliance policies are implemented as code. All resources discovered by PacBot are evaluated against these policies to gauge policy conformance. The PacBot auto-fix framework provides the ability to automatically respond to policy violations by taking predefined actions. PacBot packs in powerful visualization features, giving a simplified view of compliance and making it easy to analyze and remediate policy violations. PacBot is more than a tool to manage cloud misconfiguration, it is a generic platform that can be used to do continuous compliance monitoring and reporting for any domain.

## More Than Cloud Compliance Assessment
PacBot's plugin-based data ingestion architecture allows ingesting data from multiple sources. We have built plugins to pull data from Qualys Vulnerability Assessment Platform, Bitbucket, TrendMicro Deep Security, Tripwire, Venafi Certificate Management, Redhat Satellite, Spacewalk, Active Directory and several other custom-built internal solutions. We are working to open source these plugins and other tools as well. You could write rules based on data collected by these plugins to get a complete picture of your ecosystem and not just cloud misconfigurations. For example, within T-Mobile we have implemented a policy to mark all EC2 instances having one or more severity 5 (CVSS score > 7) vulnerabilities as non-compliant.

## Quick Demo

[![](http://img.youtube.com/vi/_WnuSU5tfcs/0.jpg)](https://www.youtube.com/embed/_WnuSU5tfcs "")



## How Does It Work?
**Assess -> Report -> Remediate -> Repeat**

Assess -> Report -> Remediate -> Repeat is PacBot's philosophy. PacBot discovers resources and assesses them against the policies implemented as code. All policy violations are recorded as an issue. Whenever an Auto-Fix hook is available with the policies, those auto-fixes are executed when the resources fail the evaluation. Policy violations cannot be closed manually, the issue has to be fixed at the source and PacBot will mark it closed in the next scan. Exceptions can be added to policy violations. Sticky exceptions (Exception based on resource attribute matching criteria) can be added to exempt similar resources that may be created in future.


PacBot's Asset Groups are a powerful way to visualize compliance. Asset Groups are created by defining one or more target resource's attribute matching criteria. For example, you could create an Asset Group of all running assets by defining criteria to match all EC2 instances with attribute instancestate.name=running. Any new EC2 instance launched after the creation of the Asset Group will be automatically included in the group. In PacBot UI you can select the scope of the portal to a specific asset group. All the data points shown in the PacBot portal will be confined to the selected Asset Group. Teams using cloud can set the scope of the portal to their application or org and focus only on their policy violations. This reduces noise and provides a clear picture to cloud users. At T-Mobile, we create an Asset Groups per stakeholder, per application, per AWS account, per Environment etc.

Asset groups can also be used to define the scope of rule executions as well. PacBot policies are implemented as one or more rules. These rules can be configured to run against all resources or a specific Asset Group. The rules will evaluate all resources in the asset group configured as the scope for the rule. This provides an opportunity to write policies which are very specific to an application or org. For example, some teams would like to enforce additional tagging standards apart from the global standards set for all of the cloud. They can implement such policies with custom rules and configure these rules to run only on their assets.


## PacBot Key Capabilities

* Continuous compliance assessment.
* Detailed compliance reporting.
* Auto-Fix for policy violations.
* Omni Search - Ability to search all discovered resources.
* Simplified policy violation tracking.
* Self-Service portal.
* Custom policies and custom auto-fix actions.
* Dynamic asset grouping to view compliance.
* Ability to create multiple compliance domains.
* Exception management.
* Email Digests.
* Supports multiple AWS accounts.
* Completely automated installer.
* Customizable dashboards.
* OAuth Support.
* Azure AD integration for login.
* Role-based access control.
* Asset 360 degree.


## Technology Stack
* Front End - Angular
* Backend End APIs, Jobs, Rules - Java
* Installer - Python and Terraform
  
## Deployment Stack
* AWS ECS & ECR - For hosting UI and APIs
* AWS Batch - For rules and resource collection jobs
* AWS CloudWatch Rules - For rule trigger, scheduler
* AWS Redshift - Data warehouse for all the inventory collected from multiple sources
* AWS Elastic Search - Primary data store used by the web application
* AWS RDS - For admin CRUD functionalities
* AWS S3 - For storing inventory files and persistent storage of historical data
* AWS Lambda - For gluing few components of PacBot

PacBot installer automatically launches all of these services and configures them. For detailed instruction on installation look at the installation documentation. 

## PacBot UI Dashboards & Widgets 

* ##### Asset Group Selection Widget
    <img src=./wiki/images/asset-group-applications.png>

* ##### Compliance Dashboard
    <img src=./wiki/images/compliance.png>
    <img src=./wiki/images/compliance2.png>

* ##### Policy Compliance Page - S3 buckets public read access   
    <img src=./wiki/images/policy-compliance.png>

* ##### Policy Compliance Trend Over Time
    <img src=./wiki/images/compliance-trend.png>

* ##### Asset Dashboard
    <img src=./wiki/images/assets.png>

* ##### Asset Dashboard - With Recommendations
    <img src=./wiki/images/asset-recommendation.png>

* ##### Asset 360 / Asset Details Page
<img src=./wiki/images/asset-details.png>

* ##### Linux Server Quarterly Patch Compliance
    <img src=./wiki/images/linux-patch-compliance.png>

* ##### Omni-Search Page   
    <img src=./wiki/images/omni-search.png>

* ##### Search Results Page With Results filtering
    <img src=./wiki/images/search-results.png>

* ##### Tagging Compliance Summary Widget
    <img src=./wiki/images/tagging-summary.png> 


## Installation

Detailed installation instructions are available [here](https://github.com/tmobile/pacbot/wiki/Install)


## Usage

The installer will launch required AWS services listed in the [installation instructions](https://github.com/tmobile/pacbot/wiki/Install). After successful installation, open the UI load balancer URL. Log into the application using the credentials supplied during the installation. The results from the policy evaluation will start getting populated within an hour. Trendline widgets will be populated when there are at least two data points.


When you install PacBot, the AWS account where you install is the base account. PacBot installed on the base account can monitor other target AWS accounts. Refer to the instructions [here](https://github.com/tmobile/pacbot/wiki/Install#adding-new-aws-accounts-to-pacbot-to-monitor) to add new accounts to PacBot. By default base account will be monitored by PacBot.


Login as Admin user and go to the Admin page from the top menu. In the Admin section, you can 
1. Create/Manage Policies
2. Create/Manage Rules and associate Rules with Policies
3. Create/Manage Asset Groups
4. Create/Manage Sticky Exception
5. Manage Jobs
6. Create/Manage Access Roles
7. Manage PacBot Configurations

See detailed instruction with screenshots on how to use the admin feature [here](https://github.com/tmobile/pacbot/wiki/Admin-Features
)

## User Guide / Wiki
Wiki is [here](https://github.com/tmobile/pacbot/wiki). 

## Announcement Blog Post
[Introducing PacBot](https://opensource.t-mobile.com/blog/posts/introducing-pacbot/)
## License
PacBot is open-sourced under the terms of section 7 of the Apache 2.0 license and is released AS-IS WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND.
