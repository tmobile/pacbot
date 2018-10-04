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
package com.tmobile.cso.pacman.inventory.log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClientBuilder;
import com.amazonaws.services.logs.model.CreateLogGroupRequest;
import com.amazonaws.services.logs.model.CreateLogStreamRequest;
import com.amazonaws.services.logs.model.DescribeLogGroupsRequest;
import com.amazonaws.services.logs.model.DescribeLogGroupsResult;
import com.amazonaws.services.logs.model.DescribeLogStreamsRequest;
import com.amazonaws.services.logs.model.DescribeLogStreamsResult;
import com.amazonaws.services.logs.model.InputLogEvent;
import com.amazonaws.services.logs.model.LogGroup;
import com.amazonaws.services.logs.model.LogStream;
import com.amazonaws.services.logs.model.PutLogEventsRequest;
import com.amazonaws.services.logs.model.PutLogEventsResult;
import com.amazonaws.services.logs.model.ResourceNotFoundException;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;

/**
 * The Class CloudwatchAppender.
 */
@Plugin(name = "CloudwatchAppender", category = "Core", elementType = "appender", printObject = true)
public class CloudwatchAppender extends AbstractAppender {

	/** The dev mode. */
	private boolean devMode = System.getProperty("PIC_DEV_MODE")==null?false:true;
	
	/** The aws log group name. */
	private String awsLogGroupName  ;
	
	/** The aws log stream name. */
	private String awsLogStreamName ;
	
	/** The sequence token. */
	private String sequenceToken ;
	
	/** The aws logs client. */
	private AWSLogs awsLogsClient = null;
    
	/**
	 * Instantiates a new cloudwatch appender.
	 *
	 * @param name the name
	 * @param filter the filter
	 * @param layout the layout
	 * @param logGroup the log group
	 * @param logStream the log stream
	 * @param ignoreExceptions the ignore exceptions
	 */
	protected CloudwatchAppender(String name, Filter filter, Layout<? extends Serializable> layout, String logGroup, String logStream, final boolean ignoreExceptions) {
		super(name, filter, layout, ignoreExceptions);
		
		if(devMode){
		    String accessKey = System.getProperty("ACCESS_KEY"); 
            String secretKey = System.getProperty("SECRET_KEY"); 
            String baseAccount = System.getProperty("base-account");
            String roleName =   System.getProperty("s3-role");
            String region =   System.getProperty("base-region");
            
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
            AWSSecurityTokenServiceClientBuilder stsBuilder = AWSSecurityTokenServiceClientBuilder.standard().withCredentials( new AWSStaticCredentialsProvider(awsCreds)).withRegion(region);
            AWSSecurityTokenService sts = stsBuilder.build();
            AssumeRoleRequest assumeRequest = new AssumeRoleRequest().withRoleArn("arn:aws:iam::"+baseAccount+":role/"+roleName).withRoleSessionName("pic-base-log-ro");
            AssumeRoleResult assumeResult = sts.assumeRole(assumeRequest);
            BasicSessionCredentials  tempCredntials =  new  BasicSessionCredentials(
                    assumeResult.getCredentials().getAccessKeyId(), assumeResult.getCredentials().getSecretAccessKey(),
                    assumeResult.getCredentials().getSessionToken());
            
		    awsLogsClient = AWSLogsClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(tempCredntials)).withRegion(region).build();
		}else{
			awsLogsClient = AWSLogsClientBuilder.defaultClient();
		}
		
		awsLogGroupName = logGroup;
		awsLogStreamName = logStream;
		sequenceToken = createLogGroupAndLogStreamIfNeeded(awsLogGroupName,awsLogStreamName);
		
	}

	/**
	 * Creates the appender.
	 *
	 * @param name the name
	 * @param logGroup the log group
	 * @param logStream the log stream
	 * @param layout the layout
	 * @param filter the filter
	 * @param otherAttribute the other attribute
	 * @return the cloudwatch appender
	 */
	@PluginFactory
	public static CloudwatchAppender createAppender(
			@PluginAttribute("name") String name,
			@PluginAttribute("logGroup") String logGroup,
			@PluginAttribute("logStream") String logStream,
			@PluginElement("Layout") Layout<? extends Serializable> layout,
			@PluginElement("Filter") final Filter filter,@PluginAttribute("otherAttribute") String otherAttribute) {
		
		if (name == null) {
			LOGGER.error("No name provided for MyCustomAppenderImp");
			return null;
		}
		if (layout == null) {
			layout = PatternLayout.createDefaultLayout();
		}
		
		return new CloudwatchAppender(name, filter, layout,logGroup,logStream, true);

	}

	/* (non-Javadoc)
	 * @see org.apache.logging.log4j.core.Appender#append(org.apache.logging.log4j.core.LogEvent)
	 */
	public void append(LogEvent event) {
		try {
			final byte[] logMessage = getLayout().toByteArray(event);
	      
	        PutLogEventsRequest putLogEventsRequest = new PutLogEventsRequest(); 
	        putLogEventsRequest.setLogGroupName(awsLogGroupName);
	        putLogEventsRequest.setLogStreamName(awsLogStreamName);
	        putLogEventsRequest.setSequenceToken(sequenceToken);
	        
	        Calendar calendar = Calendar.getInstance();
	        InputLogEvent inputLogEvent = new InputLogEvent();
	        inputLogEvent.setMessage(new String(logMessage, "UTF-8"));
	        inputLogEvent.setTimestamp(calendar.getTimeInMillis());
	        ArrayList<InputLogEvent> logEvents = new ArrayList<>();
	        logEvents.add(inputLogEvent);
	        
	        putLogEventsRequest.setLogEvents(logEvents);
	       
	      
	        try {
	        	PutLogEventsResult putLogEventsResult = awsLogsClient.putLogEvents(putLogEventsRequest);
	        	sequenceToken = putLogEventsResult.getNextSequenceToken();
	        	
	        }catch(ResourceNotFoundException ex){
	        	LOGGER.info(ex);
	        	createLogGroupAndLogStreamIfNeeded(awsLogGroupName,awsLogStreamName);
	        }
	    
			
		} catch (Exception ex) {
			if (!ignoreExceptions()) {
				throw new AppenderLoggingException(ex);
			}
		}
	}
	
	/**
	 * Creates the log group and log stream if needed.
	 *
	 * @param logGroupName the log group name
	 * @param logStreamName the log stream name
	 * @return the string
	 */
	private String  createLogGroupAndLogStreamIfNeeded(String logGroupName, String logStreamName) {
		
        final DescribeLogGroupsResult describeLogGroupsResult = awsLogsClient.describeLogGroups(new DescribeLogGroupsRequest().withLogGroupNamePrefix(logGroupName)); 
        boolean createLogGroup = true;
        
        if (describeLogGroupsResult != null && describeLogGroupsResult.getLogGroups() != null && !describeLogGroupsResult.getLogGroups().isEmpty()) { 
            for (final LogGroup lg : describeLogGroupsResult.getLogGroups()) { 
                if (logGroupName.equals(lg.getLogGroupName())) { 
                    createLogGroup = false; 
                    break; 
                } 
            } 
        } 
        if (createLogGroup) { 
            final CreateLogGroupRequest createLogGroupRequest = new CreateLogGroupRequest(logGroupName); 
            awsLogsClient.createLogGroup(createLogGroupRequest); 
        } 
        
       
        final DescribeLogStreamsRequest describeLogStreamsRequest = new DescribeLogStreamsRequest(logGroupName).withLogStreamNamePrefix(logStreamName); 
        final DescribeLogStreamsResult describeLogStreamsResult = awsLogsClient.describeLogStreams(describeLogStreamsRequest); 
        if (describeLogStreamsResult != null && describeLogStreamsResult.getLogStreams() != null && !describeLogStreamsResult.getLogStreams().isEmpty()) { 
            for (final LogStream logStream : describeLogStreamsResult.getLogStreams()) { 
                if (logStreamName.equals(logStream.getLogStreamName())) { 
                    return logStream.getUploadSequenceToken();
                } 
            } 
        } 
 
        CreateLogStreamRequest createLogStreamRequest = new CreateLogStreamRequest(logGroupName, logStreamName); 
        awsLogsClient.createLogStream(createLogStreamRequest); 
        
        return null;
     
    }
}
