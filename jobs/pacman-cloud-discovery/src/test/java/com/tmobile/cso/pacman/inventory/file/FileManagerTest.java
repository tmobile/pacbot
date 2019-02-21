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
package com.tmobile.cso.pacman.inventory.file;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyNew;

import java.io.File;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.amazonaws.auth.AWSStaticCredentialsProvider;


/**
 * The Class FileManagerTest.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FileGenerator.class,File.class})
@PowerMockIgnore("javax.management.*")
public class FileManagerTest {

    /** The file manager. */
    @InjectMocks
    FileManager fileManager;
    
    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    /**
     * Initialise test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void initialiseTest() throws Exception{
        
        File file = PowerMockito.mock(File.class);
        PowerMockito.whenNew(File.class).withAnyArguments().thenReturn(file);
        when(file,"mkdirs").thenReturn(true);
        
        mockStatic(FileGenerator.class);
        PowerMockito.doNothing().when(FileGenerator.class,"writeToFile",anyString(),anyString(),anyBoolean());
        
        fileManager.initialise("src/test/resources/testfolder");
    }
    
    /**
     * Generate files test.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void generateFilesTest() throws Exception{
        
        mockStatic(FileGenerator.class);
        PowerMockito.doNothing().when(FileGenerator.class,"writeToFile",anyString(),anyString(),anyBoolean());
        
        fileManager.generateInstanceFiles(new HashMap<>());
        fileManager.generateNwInterfaceFiles(new HashMap<>());
        fileManager.generateAsgFiles(new HashMap<>());
        fileManager.generateCloudFormationStackFiles(new HashMap<>());
        fileManager.generateDynamoDbFiles(new HashMap<>());
        fileManager.generateEfsFiles(new HashMap<>());
        fileManager.generateEmrFiles(new HashMap<>());
        fileManager.generateLamdaFiles(new HashMap<>());
        fileManager.generateClassicElbFiles(new HashMap<>());
        fileManager.generateApplicationElbFiles(new HashMap<>());
        fileManager.generateNatGatewayFiles(new HashMap<>());
        fileManager.generateRDSClusterFiles(new HashMap<>());
        fileManager.generateRDSInstanceFiles(new HashMap<>());
        fileManager.generateS3Files(new HashMap<>());
        fileManager.generateSubnetFiles(new HashMap<>());
        fileManager.generateRedshiftFiles(new HashMap<>());
        fileManager.generatefetchVolumeFiles(new HashMap<>());
        fileManager.generateSnapshotFiles(new HashMap<>());
        fileManager.generateVpcFiles(new HashMap<>());
        fileManager.generateApiGatewayFiles(new HashMap<>());
        fileManager.generateIamUserFiles(new HashMap<>());
        fileManager.generateRDSSnapshotFiles(new HashMap<>());
        fileManager.generateIamRoleFiles(new HashMap<>());
        fileManager.generateKMSFiles(new HashMap<>());
        fileManager.generateCloudFrontFiles(new HashMap<>());
        fileManager.generateEBSFiles(new HashMap<>());
        fileManager.generatePHDFiles(new HashMap<>());
        fileManager.generateErrorFile(new HashMap<>());
        fileManager.generateEC2RouteTableFiles(new HashMap<>());
        fileManager.generateNetworkAclFiles(new HashMap<>());
        fileManager.generateElasticIPFiles(new HashMap<>());
        fileManager.generateLaunchConfigurationsFiles(new HashMap<>());
        fileManager.generateInternetGatewayFiles(new HashMap<>());
        fileManager.generateVPNGatewayFiles(new HashMap<>());
        fileManager.generateScalingPolicies(new HashMap<>());
        fileManager.generateSNSTopics(new HashMap<>());
        fileManager.generateEgressGateway(new HashMap<>());
        fileManager.generateDhcpOptions(new HashMap<>());
        fileManager.generatePeeringConnections(new HashMap<>());
        fileManager.generateCustomerGateway(new HashMap<>());
        fileManager.generateVpnConnection(new HashMap<>());
        fileManager.generateDirectConnection(new HashMap<>());
        fileManager.generateDirectConnectionVirtualInterfaces(new HashMap<>());
        fileManager.generateESDomain(new HashMap<>());
        fileManager.generateReservedInstances(new HashMap<>());
        fileManager.generateSsmFiles(new HashMap<>());
        fileManager.generateTargetGroupFiles(new HashMap<>());
        fileManager.generateSecGroupFile(new HashMap<>());
        fileManager.generateTrustedAdvisorFiles(new HashMap<>());
    }
}
