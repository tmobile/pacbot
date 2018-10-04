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
package com.tmobile.cloud.awsrules.compliance;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Dedicated class for checking the kernel compliance via SSH
 * 
 * @author u26405
 *
 */
public class SSHManager {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SSHManager.class);
    private static JSch jschSSHChannel;
    private static String strUserName;
    private static String strConnectionIP;
    private static int intConnectionPort;
    private static String strPassword;
    private Session sesConnection;
    private int intTimeOut;
    private static String errorMessage = null;

    private static void doCommonConstructorActions(String userName,
            String password, String connectionIP, String knownHostsFileName) {
        jschSSHChannel = new JSch();

        try {
            jschSSHChannel.setKnownHosts(knownHostsFileName);
        } catch (JSchException jschX) {
            LOGGER.error(jschX.getMessage());
            errorMessage = jschX.getMessage();
        }

        strUserName = userName;
        strPassword = password;
        strConnectionIP = connectionIP;
    }

    public SSHManager() {

    }

    public SSHManager(String userName, String password, String connectionIP,
            String knownHostsFileName, int connectionPort) {
        doCommonConstructorActions(userName, password, connectionIP,
                knownHostsFileName);
        intConnectionPort = connectionPort;
        intTimeOut = 1000; // 10 seconds
    }

    public String connect() {
        String errorMessageStr = null;
        try {
            sesConnection = jschSSHChannel.getSession(strUserName,
                    strConnectionIP, intConnectionPort);
            sesConnection.setPassword(strPassword);
            sesConnection.setConfig("StrictHostKeyChecking", "no");
            // ANY PROXY SETTING [IF ANY] NEEDS TO BE ADDED HERE

            sesConnection.setConfig("PreferredAuthentications",
                    "publickey,keyboard-interactive,password");
            sesConnection.connect(intTimeOut);

        } catch (JSchException jschX) {
            LOGGER.error(jschX.getMessage());
            errorMessageStr = jschX.getMessage();
        }

        return errorMessageStr;
    }

    private static String logError(String errorMessage) {
        if (errorMessage != null) {
            LOGGER.error(errorMessage);
        }
        return errorMessage;
    }

    private String logWarning(String warnMessage) {
        if (warnMessage != null) {
            LOGGER.warn(warnMessage);
        }
        return warnMessage;
    }

    public String sendCommand(String command) {
        StringBuilder outputBuffer = new StringBuilder();

        try {
            Channel channel = sesConnection.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            InputStream commandOutput = channel.getInputStream();
            channel.connect();
            int readByte = commandOutput.read();

            while (readByte != 0xffffffff) {
                outputBuffer.append((char) readByte);
                readByte = commandOutput.read();
            }
            channel.disconnect();
        } catch (IOException ioX) {
            logWarning(ioX.getMessage());
            LOGGER.error("error while getting kernelversion from SSH", ioX);
            return null;
        } catch (JSchException jschX) {
            LOGGER.error(
                    "error while getting kernelversion from JSchException SSH",
                    jschX);
            logWarning(jschX.getMessage());
            return null;
        }

        return outputBuffer.toString();
    }

    public void close() {
        sesConnection.disconnect();
    }

    /**
     * This method used to get the Kernel Version of an instance.
     * 
     * @param userName
     * @param password
     * @param ipAddress
     * @param connectionPort
     * @return String, if kernel version available else null
     */
    public static String getkernelDetailsViaSSH(String userName,
            String password, String ipAddress, int connectionPort) {
        String result = null;
        try {
            SSHManager instance = new SSHManager(userName, password, ipAddress,
                    "", connectionPort);
            if (Strings.isNullOrEmpty(errorMessage)) {
                errorMessage = instance.connect();

                if (errorMessage != null) {
                    logError(errorMessage);
                } else {
                    result = instance.sendCommand("uname -r");
                    // close only after all commands are sent
                    instance.close();
                }
            } else {
                logError(errorMessage);
            }
        } catch (Exception e) {
            LOGGER.error("error while getting kernelversion from SSH", e);
        }

        return result;
    }
}
