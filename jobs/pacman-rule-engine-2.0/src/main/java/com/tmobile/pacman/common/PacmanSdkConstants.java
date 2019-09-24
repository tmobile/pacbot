/*******************************************************************************
 * Copyright 2018 T Mobile, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/


package com.tmobile.pacman.common;


// TODO: Auto-generated Javadoc
/**
 * The Interface PacmanSdkConstants.
 */
public interface PacmanSdkConstants extends com.tmobile.pacman.commons.PacmanSdkConstants {

    /** The client. */
    String CLIENT = "client";

    /** The temporary creds valid seconds. */
    Integer TEMPORARY_CREDS_VALID_SECONDS = 3600;

    /** The default session name. */
    String DEFAULT_SESSION_NAME = "PAC_GET_DATA_SESSION";

    /** The pacman dev profile name. */
    String PACMAN_DEV_PROFILE_NAME = "pacman-dev";

    /** The pacman dev env variable. */
    String PACMAN_DEV_ENV_VARIABLE = "PACMAN_DEV";

    /** The run time argument name. */
    String RUN_TIME_ARGUMENT_NAME = "params";

    /** The pacman resource srv url env var name. */
    String PACMAN_RESOURCE_SRV_URL_ENV_VAR_NAME = "pacman_resource_srv_url";

    /** The role arn prefix. */
    String ROLE_ARN_PREFIX = "arn:aws:iam::";

    /** The Role IDENTIFYIN G STRING. */
    String Role_IDENTIFYING_STRING = "roleIdentifyingString";

    /** The mendetory tags key. */
    String MENDETORY_TAGS_KEY = "mandatoryTags";

    /** The splitter char. */
    String SPLITTER_CHAR = "splitterChar";

    /** The description. */
    String DESCRIPTION = "desc";

    /** The exception. */
    String EXCEPTION = "Exception";

    /** The target type. */
    String TARGET_TYPE = "targetType";

    /** The annotation pk. */
    String ANNOTATION_PK = "annotationid";

    /** The x api key. */
    String X_API_KEY = "x-api-key";

    /** The env variable name for environment. */
    String ENV_VARIABLE_NAME_FOR_ENVIRONMENT = "PAC_ENV";

    /** The staging env prefix. */
    String STAGING_ENV_PREFIX = "stg";

    /** The type. */
    String TYPE = "type";

    /** The tz utc. */
    String TZ_UTC = "UTC";

    /** The created date. */
    String CREATED_DATE = "createdDate";

    /** The modified date. */
    String MODIFIED_DATE = "modifiedDate";

    /** The exemption expiring on. */
    String EXEMPTION_EXPIRING_ON = "exemption-expiring-on";

    /** The exemption id. */
    String EXEMPTION_ID = "exemptionId";

    /** The sev high. */
    String SEV_HIGH = "high";

    /** The sev medium. */
    String SEV_MEDIUM = "medium";

    /** The sev low. */
    String SEV_LOW = "low";

    /** The financial. */
    String FINANCIAL = "financial";

    /** The security. */
    String SECURITY = "security";

    /** The governance. */
    String GOVERNANCE = "governance";

    /** The pacman. */
    String PACMAN = "pacman";

    /** The pac time zone. */
    String PAC_TIME_ZONE = "UTC";

    /** The issue status key. */
    String ISSUE_STATUS_KEY = "issueStatus";

    /** The rule category. */
    String RULE_CATEGORY = "ruleCategory";

    /** The rule severity. */
    String RULE_SEVERITY = "severity";

    /** The updated success. */
    String UPDATED_SUCCESS = "Successfully Updated";

    /** The updated failure. */
    String UPDATED_FAILURE = "Updation Failed";

    /** The creation failure. */
    String CREATION_FAILURE = "Failure In Adding New Item";

    /** The creation success. */
    String CREATION_SUCCESS = "Successfully Added New Item";

    /** The data source key. */
    String DATA_SOURCE_KEY = "pac_ds";

    /** The base aws account env var name. */
    String BASE_AWS_ACCOUNT_ENV_VAR_NAME = "BASE_AWS_ACCOUNT";

    /** The es doc id key. */
    String ES_DOC_ID_KEY = "_id";

    /** The date format. */
    String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /** The es uri env var name. */
    String ES_URI_ENV_VAR_NAME = "ES_URI";

    /** The doc id. */
    String DOC_ID = "_docid";

    /** The data source attr. */
    String DATA_SOURCE_ATTR = "datasource";

    /** The audit date. */
    String AUDIT_DATE = "auditdate";

    /** The  audit date. */
    String _AUDIT_DATE = "_auditdate";

    /** The audit index. */
    String AUDIT_INDEX = "issueaudit";

    /** The audit type. */
    String AUDIT_TYPE = "audittrail";

    /** The execution id. */
    String EXECUTION_ID = "executionId";

    /** The rule type serverless. */
    String RULE_TYPE_SERVERLESS = "Serverless";

    /** The rule type classic. */
    String RULE_TYPE_CLASSIC = "classic";

    /** The rule type. */
    String RULE_TYPE = "ruleType";

    /** The rule key. */
    String RULE_KEY = "ruleKey";


    /** The rule url key. */
    String RULE_URL_KEY = "ruleRestUrl";

    /** The es page size. */
    Integer ES_PAGE_SIZE = 10000;

    /** The es page scroll ttl. */
    String ES_PAGE_SCROLL_TTL = "2m";

    /** The es source fields key. */
    String ES_SOURCE_FIELDS_KEY = "es_source_fields";

    /** The account name. */
    String ACCOUNT_NAME = "accountname";

    /** The run on multi thread key. */
    String RUN_ON_MULTI_THREAD_KEY = "threadsafe";

    /** The scan time out. */
    Long SCAN_TIME_OUT = 180L;

    /** The thread name prefix. */
    String THREAD_NAME_PREFIX = "pacman-rule-execution-engine";

    /** The es doc parent key. */
    String ES_DOC_PARENT_KEY = "_parent";

    /** The es doc routing key. */
    String ES_DOC_ROUTING_KEY = "_routing";

    /** The es max bulk post size. */
    Long ES_MAX_BULK_POST_SIZE = 5L;

    /** The status key. */
    String STATUS_KEY = "status";

    /** The status running. */
    String STATUS_RUNNING = "running";

    /** The status finished. */
    String STATUS_FINISHED = "finished";

    /** The status open. */
    String STATUS_OPEN = "open";

    /** The status close. */
    String STATUS_CLOSE = "closed";

    /** The status exempted. */
    String STATUS_EXEMPTED = "exempted";

    /** The status success. */
    String STATUS_SUCCESS = "success";

    /** The status success. */
    Integer STATUS_SUCCESS_CODE = 0;

    /** The status success. */
    Integer STATUS_FAILURE_CODE = -1;

    /** The status failure. */
    String STATUS_FAILURE = "fail";

    /** The status unknown. */
    String STATUS_UNKNOWN = "unknown";

    /** The status unknown message. */
    String STATUS_UNKNOWN_MESSAGE = "unable to determine for this resource";

    /** The max rule executor threads. */
    Integer MAX_RULE_EXECUTOR_THREADS = 100;

    /** The worker thread count. */
    String WORKER_THREAD_COUNT = "workerThreadCount";

    /** The env pac re max workers. */
    String ENV_PAC_RE_MAX_WORKERS = "PAC_RE_MAX_RULE_EXECUTORS";

    /** The error desc key. */
    String ERROR_DESC_KEY = "errorDesc";

    /** The unable to execute error desc. */
    String UNABLE_TO_EXECUTE_ERROR_DESC = "unable to evaluvate for this resource ";

    /** The max http con. */
    Integer MAX_HTTP_CON = 50;

    /** The issue closed date. */
    String ISSUE_CLOSED_DATE = "closeddate";

    /** The serverless check failed message key. */
    Object SERVERLESS_CHECK_FAILED_MESSAGE_KEY = "message";

    /** The issue status exempted value. */
    String ISSUE_STATUS_EXEMPTED_VALUE = "exempted";

    /** The reason to close key. */
    String REASON_TO_CLOSE_KEY = "reason-to-close";

    /** The reason to exempt key. */
    String REASON_TO_EXEMPT_KEY = "reason-to-exempt";

    /** The reason to close value. */
    String REASON_TO_CLOSE_VALUE = "resource not found";

    /** The es keyword key. */
    String ES_KEYWORD_KEY = "keyword";

    /** The status reason. */
    String STATUS_REASON = "status-reason";

    /** The status unable to determine. */
    String STATUS_UNABLE_TO_DETERMINE = "unable to determine";

    /** The asset group key. */
    String ASSET_GROUP_KEY = "assetGroup";

    /** The rule uuid key. */
    String RULE_UUID_KEY = "ruleUUID";

    /** The invocation id. */
    String INVOCATION_ID = "invocationId";

    /** The application tag key. */
    String APPLICATION_TAG_KEY = "tags.Application";

    /** The env tag key. */
    String ENV_TAG_KEY = "tags.Environment";

    /** The http post retry interval. */
    Long HTTP_POST_RETRY_INTERVAL = 2000L;

    /** The http max retry count. */
    Integer HTTP_MAX_RETRY_COUNT = 3;

    /** The resource init delay. */
    String RESOURCE_INIT_DELAY = "resource_init_delay";

    /** The autofix cutoff date. */
    String AUTOFIX_CUTOFF_DATE = "autofix.cufoff.date";

    /** The autofix exempted types key. */
    String AUTOFIX_EXEMPTED_TYPES_KEY = "pacman.autofix.exempted.types.for.cutoff.data";

    /** The autofix whitelist accounts prefix. */
    String AUTOFIX_WHITELIST_ACCOUNTS_PREFIX = "autofix.whitelist.accounts.";

    /** The mm dd yyyy. */
    String MM_DD_YYYY = "MM/dd/yyyy";

    /** The yyyy mm dd t hh mm ss z. */
    String YYYY_MM_DD_T_HH_MM_SS_Z = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /** The backup asset config. */
    String BACKUP_ASSET_CONFIG = "api.backup.asset.config";

    /** The resource creationdate. */
    String RESOURCE_CREATIONDATE = "api.resource.creationdate";


    /** The resource creationdate. */
    String AUTO_FIX_ROLE_NAME = "pacman.auto.fix.role.name";

    /** The auto fix type. */
    String AUTO_FIX_TYPE = "pacman.autofix.fix.type";

    /** The auto fix type silent. */
    String AUTO_FIX_TYPE_SILENT = "silent";

    /** The resource get lastaction. */
    String RESOURCE_GET_LASTACTION = "api.getlastaction";

    /** The resource post lastaction. */
    String RESOURCE_POST_LASTACTION = "api.postlastaction";

    /** The pacman auto fix tag name. */
    String PACMAN_AUTO_FIX_TAG_NAME = "pacman.auto.fix.tag.name";

    /** The empty. */
    String EMPTY = "";

    /** The read access. */
    String READ_ACCESS = "Read";

    /** The all s3 user uri. */
    String ALL_S3_USER_URI = "http://acs.amazonaws.com/groups/global/AllUsers";

    /** The any s3 authenticated user uri. */
    String ANY_S3_AUTHENTICATED_USER_URI = "http://acs.amazonaws.com/groups/global/AuthenticatedUsers";

    /** The write access. */
    String WRITE_ACCESS = "write";

    /** The read acp access. */
    String READ_ACP_ACCESS = "READ_ACP";

    /** The write acp access. */
    String WRITE_ACP_ACCESS = "WRITE_ACP";

    /** The full control. */
    String FULL_CONTROL = "FULL_CONTROL";

    /** The autofix max emails. */
    String AUTOFIX_MAX_EMAILS = "pacman.auto.fix.max.email.notifications";

    /** The fix only matching resource pattern. */
    String FIX_ONLY_MATCHING_RESOURCE_PATTERN = "pacman.auto.fix.resource.name.filter.pattern";

    /** The stats index name key. */
    String STATS_INDEX_NAME_KEY = "pacman.es.stats.index";

    /** The stats type name key. */
    String STATS_TYPE_NAME_KEY = "pacman.es.stats.type";

    /** The auto fix tran index name key. */
    String AUTO_FIX_TRAN_INDEX_NAME_KEY = "pacman.es.auto.fix.transaction.index";

    /** The auto fix tran type name key. */
    String AUTO_FIX_TRAN_TYPE_NAME_KEY = "pacman.es.auto.fix.transaction.type";

    /** The pac es host key. */
    String PAC_ES_HOST_KEY = "pacman.es.host";

    /** The pac es port key. */
    String PAC_ES_PORT_KEY = "pacman.es.port";

    /** The send email cc key. */
    String SEND_EMAIL_CC_KEY = "pacman.auto.fix.mail.cc.to";

    /** The orphan resource owner email. */
    String ORPHAN_RESOURCE_OWNER_EMAIL = "pacman.auto.fix.orphan.resource.owner";

    /** The rule param auto fix key name. */
    String RULE_PARAM_AUTO_FIX_KEY_NAME = "autofix";

    /** The send email from. */
    String SEND_EMAIL_FROM = "pacman.auto.fix.mail.from";

    /** The send email fix subject prefix. */
    String SEND_EMAIL_FIX_SUBJECT_PREFIX = "pacman.auto.fix.mail.subject.";
    /** The send email fix subject prefix. */
    String SEND_EMAIL_SILENT_FIX_ADMIN = "pacman.autofix.fix.notify.";
    /** The send email warning subject prefix. */
    String SEND_EMAIL_WARNING_SUBJECT_PREFIX = "pacman.auto.warning.mail.subject.";

    /** The policy url prefix key. */
    String POLICY_URL_PREFIX_KEY = "pacman.autofix.policy.url.";

    /** The email service url. */
    String EMAIL_SERVICE_URL = "pacman.api.sendmail";

    /** The email violation message prefix. */
    String EMAIL_VIOLATION_MESSAGE_PREFIX = "pacman.autofix.rule.violation.message.";

    /** The email warning message prefix. */
    String EMAIL_WARNING_MESSAGE_PREFIX = "pacman.autofix.rule.warning.message.";

    /** The email fix message prefix. */
    String EMAIL_FIX_MESSAGE_PREFIX = "pacman.autofix.rule.post.fix.message.";

    /** The pac auto tag salt key. */
    String PAC_AUTO_TAG_SALT_KEY = "pacman.auto.fix.tag.salt";

    /** The pac auto tag encryption algorithm. */
    String PAC_AUTO_TAG_ENCRYPTION_ALGORITHM = "pacman.auto.fix.tag.encyption.algorithm";

    /** The pac auto tag non taggable services. */
    String PAC_AUTO_TAG_NON_TAGGABLE_SERVICES = "pacman.autofix.non.taggable.services";

    /** The pac auto fix min pwd length. */
    String PAC_AUTO_FIX_MIN_PWD_LENGTH = "pacman.autofix.policy.min.pwd.length.";

    /** The pac auto fix req symbls. */
    String PAC_AUTO_FIX_REQ_SYMBLS = "pacman.autofix.policy.required.symbols.";

    /** The pac auto fix req numbers. */
    String PAC_AUTO_FIX_REQ_NUMBERS = "pacman.autofix.policy.required.numbers.";

    /** The pac auto fix req uppercase. */
    String PAC_AUTO_FIX_REQ_UPPERCASE = "pacman.autofix.policy.required.uppercase.";

    /** The pac auto fix req lwrcase. */
    String PAC_AUTO_FIX_REQ_LWRCASE = "pacman.autofix.policy.required.lowercase.";

    /** The pac auto fix chng pwd allow. */
    String PAC_AUTO_FIX_CHNG_PWD_ALLOW = "pacman.autofix.policy.allow.user.to.change.pwd.";

    /** The pac auto fix max pwd age. */
    String PAC_AUTO_FIX_MAX_PWD_AGE = "pacman.autofix.policy.max.pwd.age.";

    /** The pac auto fix pwd reuse prevent. */
    String PAC_AUTO_FIX_PWD_REUSE_PREVENT = "pacman.autofix.policy.pwd.reuse.prevention.";

    /** The pac auto fix pwd hard expiry. */
    String PAC_AUTO_FIX_PWD_HARD_EXPIRY = "pacman.autofix.policy.pwd.hard.expiry.";

    /** The send email exempted subject. */
    String SEND_EMAIL_EXEMPTED_SUBJECT = "pacman.exempted.mail.subject";

    /** application tag name*. */
    String APPLICATION_TAG_NAME = "Application";


    /** The target type alias. */
    String TARGET_TYPE_ALIAS = "pacman.target.type.alias";


    /** The rule contact. */
    String RULE_CONTACT = "ruleOwner";
    
    /** The config credentials. */
    String CONFIG_CREDENTIALS = "CONFIG_CREDENTIALS";
    
    /** The config service url. */
    String CONFIG_SERVICE_URL = "CONFIG_SERVICE_URL";
    
    /** The missing configuration. */
    String MISSING_CONFIGURATION = "Missing value in the env configuration";
   
    /** The missing db configuration. */
    String MISSING_DB_CONFIGURATION = "Missing db configurations";
    
    /** The name. */
    String NAME = "name";
    
    /** The source. */
    String SOURCE = "source";
    
    /**  *. */
    String AUTH_API_OWNER_SLACK_HANDLE = "api.auth.owner.slack.handle";
    
    /**  default string *. */
    String PAC_DEFAULT = "default";
    
    /** default delay key*. */
    String PAC_AUTO_FIX_DELAY_KEY= "pacman.autofix.waittime";
    
    /**  *. */
    String TYPE_FOR_AUTO_FIX_RECORD  = "autofix";
    
    /**  *. */
    String TRANSACTION_ID = "transactionId";
    
    /** *. */
    String TRANSACTION_TIME = "transationTime";
    
    /** The pacman mail template columns. */
    String PACMAN_MAIL_TEMPLATE_COLUMNS = "pacman.auto.fix.mail.template.columns.";
    
    /**  *. */
    String JOB_ID = "AWS_BATCH_JOB_ID";
    
    /** The square one slack channel. */
    String SQUARE_ONE_SLACK_CHANNEL = "square.one.slack.channel";
    
    /**  white list *. */
    String WHITELIST = ".account.whitelist";
    
    /** The events index name key. */
    String EVENTS_INDEX_NAME_KEY = "pacman.es.reactors.index";

    /** The events registry key. */
    String EVENTS_REGISTRY_KEY = "pacman.es.reactors.registry";

    /** The event id. */
    String EVENT_ID = "eventId";

    /** The event data key. */
    String EVENT_DATA_KEY = "eventData";

    /** The event receive time. */
    String EVENT_RECEIVE_TIME = "eventReceiveTime";

    /** The event processed time. */
    String EVENT_PROCESSED_TIME = "eventProcessedTime";

    /** The event name. */
    String EVENT_NAME = "evetName";
    
    /** The reactor category. */
    String REACTOR_CATEGORY = "reactorCategory";
    
    /** The account. */
    String ACCOUNT="account";
    
   
    /** The auth header. */
    String AUTH_HEADER = "Authorization";
    
    /** The pacman host. */
    String PACMAN_HOST = "pacman.host";
    
    /** The pacman login user name. */
    String PACMAN_LOGIN_USER_NAME = "pacman.login.user.name";
    
    /** The pacman login password. */
    String PACMAN_LOGIN_PASSWORD = "pacman.login.password";
    
    /** The email banner. */
    String EMAIL_BANNER = "email.banner";
    
    /** The pacbot autofix resourceowner fallback mail. */
    String PACBOT_AUTOFIX_RESOURCE_OWNER_FALLBACK_MAIL = "pacbot.autofix.resourceowner.fallbak.email";
    
    /** The policy url path. */
    String POLICY_URL_PATH = "pacman.autofix.policy.url.path";
    
    /** The app elb arn attribute name. */
    String APP_ELB_ARN_ATTRIBUTE_NAME="resourceDisplayId";
    
    /** The pac monitor slack user. */
    String PAC_MONITOR_SLACK_USER = "pacman.monitoring.slack.user";
    
    /**  *. */
    String PACBOT_CREATED_SG_DESC = "PacBot created SG During Autofix";
    
    /**  ALLOCATIONID KEY. */
    String ALLOCATION_ID = "allocationid";
    
    /** The boolean true. */
    String BOOLEAN_TRUE = "true";

}
