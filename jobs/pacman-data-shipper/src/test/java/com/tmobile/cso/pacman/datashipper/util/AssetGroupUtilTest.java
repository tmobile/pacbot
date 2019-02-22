package com.tmobile.cso.pacman.datashipper.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ HttpUtil.class })
public class AssetGroupUtilTest {

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(HttpUtil.class); 
    }

    @Test
    public void testFetchAssetGroups() throws Exception {

        String listAgJson = "{\"data\":[{\"name\":\"abcservices\",\"domains\":[\"Infra & Platforms\"]},{\"name\":\"adapt\",\"domains\":[\"Infra & Platforms\"]},{\"name\":\"adapt-workload\",\"domains\":[\"Infra & Platforms\"]}]}";
        when(HttpUtil.get(anyString(),anyString())).thenReturn(listAgJson);

        Map<String, List<String>> agInfo = AssetGroupUtil.fetchAssetGroups("","");
        assertThat(agInfo.size(), is(3));
    }

    @Test
    public void testFetcTypeCounts() throws Exception {
        String typeCountJson = "{\"data\":{\"ag\":\"aws-all\",\"assetcount\":[{\"count\":1949,\"type\":\"subnet\"},{\"count\":5885,\"type\":\"stack\"},{\"count\":714,\"type\":\"asgpolicy\"},{\"count\":3926,\"type\":\"rdssnapshot\"},{\"count\":84,\"type\":\"rdscluster\"},{\"count\":1320,\"type\":\"cert\"},{\"count\":481,\"type\":\"internetgateway\"},{\"count\":419,\"type\":\"rdsdb\"}]}}";
        when(HttpUtil.get(anyString(),anyString())).thenReturn(typeCountJson);

        List<Map<String, Object>> typeCounts = AssetGroupUtil.fetchTypeCounts("", "","");
        assertThat(typeCounts.size(), is(8));
    }
    
    @Test(expected=Exception.class)
    public void testFetcTypeCountsException() throws Exception {
        String typeCountJson = "{\"data1\":{\"ag\":\"aws-all\",\"assetcount\":[{\"count\":1949,\"type\":\"subnet\"},{\"count\":5885,\"type\":\"stack\"},{\"count\":714,\"type\":\"asgpolicy\"},{\"count\":3926,\"type\":\"rdssnapshot\"},{\"count\":84,\"type\":\"rdscluster\"},{\"count\":1320,\"type\":\"cert\"},{\"count\":481,\"type\":\"internetgateway\"},{\"count\":419,\"type\":\"rdsdb\"}]}}";
        when(HttpUtil.get(anyString(),anyString())).thenReturn(typeCountJson);
        List<Map<String, Object>> typeCounts = AssetGroupUtil.fetchTypeCounts("", "","");
        assertThat(typeCounts.isEmpty(),is(true));
    }

    @Test
    public void testFetchPatchingCompliance() throws Exception {
        String patchingResponse = "{\"data\":{\"output\":{\"unpatched_instances\":4748,\"patched_instances\":1330,\"total_instances\":6078,\"patching_percentage\":21}},\"message\":\"success\"}";
        when(HttpUtil.get(anyString(),anyString())).thenReturn(patchingResponse);
        Map<String, Object> patchingInfo = AssetGroupUtil.fetchPatchingCompliance("", "","");
        assertThat(patchingInfo.get("patching_percentage").toString(), is("21"));
    }
    
    @Test(expected=Exception.class)
    public void testFetchPatchingComplianceException() throws Exception {
        String patchingResponse = "{\"data1\":{\"output\":{\"unpatched_instances\":4748,\"patched_instances\":1330,\"total_instances\":6078,\"patching_percentage\":21}},\"message\":\"success\"}";
        when(HttpUtil.get(anyString(),anyString())).thenReturn(patchingResponse);
        AssetGroupUtil.fetchPatchingCompliance("", "","");
      
    }

    @Test
    public void testFetchVulnDistribution() throws Exception {
        String vulnResponse = "{\"data\":{\"response\":[{\"application\":\"PacMan\",\"applicationInfo\":[{\"environment\":\"Production\",\"vulnerabilities\":308,\"severityInfo\":[{\"severity\":\"S3\",\"vulnInstanceCount\":128,\"count\":128,\"severitylevel\":3},{\"severity\":\"S4\",\"vulnInstanceCount\":170,\"count\":170,\"severitylevel\":4},{\"severity\":\"S5\",\"vulnInstanceCount\":10,\"count\":10,\"severitylevel\":5}]},{\"environment\":\"Non Production\",\"vulnerabilities\":0,\"severityInfo\":[{\"severity\":\"S3\",\"vulnInstanceCount\":0,\"count\":0,\"severitylevel\":3},{\"severity\":\"S4\",\"vulnInstanceCount\":0,\"count\":0,\"severitylevel\":4},{\"severity\":\"S5\",\"vulnInstanceCount\":0,\"count\":0,\"severitylevel\":5}]}]}]},\"message\":\"success\"}";
        when(HttpUtil.get(anyString(),anyString())).thenReturn(vulnResponse);
        List<Map<String, Object>> vulnDistribution = AssetGroupUtil.fetchVulnDistribution("", "","");
        assertThat(vulnDistribution.get(0).get("tags.Application").toString(), is("PacMan"));
        assertThat(vulnDistribution.size(), is(6));
    }

    @Test(expected=Exception.class)
    public void testFetchVulnDistributionException() throws Exception {
        String vulnResponse = "{\"data1\":{\"response\":[{\"application\":\"PacMan\",\"applicationInfo\":[{\"environment\":\"Production\",\"vulnerabilities\":308,\"severityInfo\":[{\"severity\":\"S3\",\"vulnInstanceCount\":128,\"count\":128,\"severitylevel\":3},{\"severity\":\"S4\",\"vulnInstanceCount\":170,\"count\":170,\"severitylevel\":4},{\"severity\":\"S5\",\"vulnInstanceCount\":10,\"count\":10,\"severitylevel\":5}]},{\"environment\":\"Non Production\",\"vulnerabilities\":0,\"severityInfo\":[{\"severity\":\"S3\",\"vulnInstanceCount\":0,\"count\":0,\"severitylevel\":3},{\"severity\":\"S4\",\"vulnInstanceCount\":0,\"count\":0,\"severitylevel\":4},{\"severity\":\"S5\",\"vulnInstanceCount\":0,\"count\":0,\"severitylevel\":5}]}]}]},\"message\":\"success\"}";
        when(HttpUtil.get(anyString(),anyString())).thenReturn(vulnResponse);
        AssetGroupUtil.fetchVulnDistribution("", "","");
    }

    @Test
    public void testFetchComplianceInfo() throws Exception {
        String complResponse = "{\"data\":{\"distribution\":{\"tagging\":59,\"security\":89,\"costOptimization\":67,\"governance\":82,\"overall\":74}},\"message\":\"success\"}";
        when(HttpUtil.get(anyString(),anyString())).thenReturn(complResponse);
        List<Map<String, Object>> complianceInfo = AssetGroupUtil.fetchComplianceInfo("", "", Arrays.asList("infra"),"");
        assertThat(complianceInfo.size(), is(1));
        assertThat(complianceInfo.get(0).get("domain").toString(), is("infra"));
        assertThat(complianceInfo.get(0).get("overall").toString(), is("74"));
    }
    
    @Test(expected=Exception.class)
    public void testFetchComplianceInfoException() throws Exception {
        String complResponse = "{\"data1\":{\"distribution\":{\"tagging\":59,\"security\":89,\"costOptimization\":67,\"governance\":82,\"overall\":74}},\"message\":\"success\"}";
        when(HttpUtil.get(anyString(),anyString())).thenReturn(complResponse);
        AssetGroupUtil.fetchComplianceInfo("", "", Arrays.asList("infra"),"");
    }

    @Test
    public void testFetchRuleComplianceInfo() throws Exception {
        String ruleComplianceJson = "{\"data\":{\"response\":[{\"severity\":\"low\",\"name\":\"EBS snapshots should be tagged with mandatory tags \",\"compliance_percent\":50,\"lastScan\":\"2018-08-01T18:00:50.263Z\",\"ruleCategory\":\"tagging\",\"resourcetType\":\"snapshot\",\"ruleId\":\"PacMan_TaggingRule_version-1_SnapshotTaggingRule_snapshot\",\"assetsScanned\":55085,\"passed\":27619,\"failed\":27466,\"contribution_percent\":100},{\"severity\":\"low\",\"name\":\"Security groups should be tagged with mandatory tags\",\"compliance_percent\":18,\"lastScan\":\"2018-08-01T23:00:47.392Z\",\"ruleCategory\":\"tagging\",\"resourcetType\":\"sg\",\"ruleId\":\"PacMan_TaggingRule_version-1_SgTaggingRule_sg\",\"assetsScanned\":7129,\"passed\":1297,\"failed\":5832,\"contribution_percent\":100},{\"severity\":\"high\",\"name\":\"Amazon EBS volumes should not be underutilized \",\"compliance_percent\":84,\"lastScan\":\"2018-08-01T18:04:23.044Z\",\"ruleCategory\":\"costOptimization\",\"resourcetType\":\"volume\",\"ruleId\":\"PacMan_Underutilized-Amazon-EBS-Volumes_version-1_Underutilized-EBS-Volumes_volume\",\"assetsScanned\":35938,\"passed\":30191,\"failed\":5747,\"contribution_percent\":100},{\"severity\":\"low\",\"name\":\"EBS volumes should be tagged with mandatory tags \",\"compliance_percent\":84,\"lastScan\":\"2018-08-01T23:00:55.626Z\",\"ruleCategory\":\"tagging\",\"resourcetType\":\"volume\",\"ruleId\":\"PacMan_TaggingRule_version-1_VolumeTaggingRule_volume\",\"assetsScanned\":35938,\"passed\":30227,\"failed\":5711,\"contribution_percent\":100},{\"severity\":\"low\",\"name\":\"Any Ec2 instance should not have S3 vulnerability \",\"compliance_percent\":30,\"lastScan\":\"2018-08-01T18:00:26.570Z\",\"ruleCategory\":\"security\",\"resourcetType\":\"ec2\",\"ruleId\":\"PacMan_Ec2WithSeverityVulnerability_version-1_Ec2WithS3Vulnerability_ec2\",\"assetsScanned\":7192,\"passed\":2172,\"failed\":5020,\"contribution_percent\":100}]}}";
        when(HttpUtil.post(anyString(),anyString(),anyString(),anyString())).thenReturn(ruleComplianceJson);
        List<Map<String, Object>> complianceInfo = AssetGroupUtil.fetchRuleComplianceInfo("", "", Arrays.asList("infra"),"");
        assertThat(complianceInfo.size(), is(5));
        assertThat(complianceInfo.get(0).get("domain").toString(), is("infra"));
        assertThat(complianceInfo.get(0).get("ruleId").toString(), is("PacMan_TaggingRule_version-1_SnapshotTaggingRule_snapshot"));
    }
    
    @Test(expected=Exception.class)
    public void testFetchRuleComplianceInfoException() throws Exception {
        String ruleComplianceJson = "{\"data1\":{\"response\":[{\"severity\":\"low\",\"name\":\"EBS snapshots should be tagged with mandatory tags \",\"compliance_percent\":50,\"lastScan\":\"2018-08-01T18:00:50.263Z\",\"ruleCategory\":\"tagging\",\"resourcetType\":\"snapshot\",\"ruleId\":\"PacMan_TaggingRule_version-1_SnapshotTaggingRule_snapshot\",\"assetsScanned\":55085,\"passed\":27619,\"failed\":27466,\"contribution_percent\":100},{\"severity\":\"low\",\"name\":\"Security groups should be tagged with mandatory tags\",\"compliance_percent\":18,\"lastScan\":\"2018-08-01T23:00:47.392Z\",\"ruleCategory\":\"tagging\",\"resourcetType\":\"sg\",\"ruleId\":\"PacMan_TaggingRule_version-1_SgTaggingRule_sg\",\"assetsScanned\":7129,\"passed\":1297,\"failed\":5832,\"contribution_percent\":100},{\"severity\":\"high\",\"name\":\"Amazon EBS volumes should not be underutilized \",\"compliance_percent\":84,\"lastScan\":\"2018-08-01T18:04:23.044Z\",\"ruleCategory\":\"costOptimization\",\"resourcetType\":\"volume\",\"ruleId\":\"PacMan_Underutilized-Amazon-EBS-Volumes_version-1_Underutilized-EBS-Volumes_volume\",\"assetsScanned\":35938,\"passed\":30191,\"failed\":5747,\"contribution_percent\":100},{\"severity\":\"low\",\"name\":\"EBS volumes should be tagged with mandatory tags \",\"compliance_percent\":84,\"lastScan\":\"2018-08-01T23:00:55.626Z\",\"ruleCategory\":\"tagging\",\"resourcetType\":\"volume\",\"ruleId\":\"PacMan_TaggingRule_version-1_VolumeTaggingRule_volume\",\"assetsScanned\":35938,\"passed\":30227,\"failed\":5711,\"contribution_percent\":100},{\"severity\":\"low\",\"name\":\"Any Ec2 instance should not have S3 vulnerability \",\"compliance_percent\":30,\"lastScan\":\"2018-08-01T18:00:26.570Z\",\"ruleCategory\":\"security\",\"resourcetType\":\"ec2\",\"ruleId\":\"PacMan_Ec2WithSeverityVulnerability_version-1_Ec2WithS3Vulnerability_ec2\",\"assetsScanned\":7192,\"passed\":2172,\"failed\":5020,\"contribution_percent\":100}]}}";
        when(HttpUtil.post(anyString(),anyString(),anyString(),anyString())).thenReturn(ruleComplianceJson);
        AssetGroupUtil.fetchRuleComplianceInfo("", "", Arrays.asList("infra"),"");
    }
    
    @Test
    public void testFetchVulnSummary() throws Exception {
        String vulnSummaryJson = "{\"data\":{\"output\":{\"hosts\":7192,\"vulnerabilities\":132285,\"totalVulnerableAssets\":5815}},\"message\":\"success\"}";
        when(HttpUtil.get(anyString(),anyString())).thenReturn(vulnSummaryJson);
        Map<String, Object> vulnSummary = AssetGroupUtil.fetchVulnSummary("", "","");
        assertThat(vulnSummary.get("total"), is(7192l));
    }
    
    @Test(expected=Exception.class)
    public void testFetchVulnSummaryException() throws Exception {
        String vulnSummaryJson = "{\"data1\":{\"output\":{\"hosts\":7192,\"vulnerabilities\":132285,\"totalVulnerableAssets\":5815}},\"message\":\"success\"}";
        when(HttpUtil.get(anyString(),anyString())).thenReturn(vulnSummaryJson);
        AssetGroupUtil.fetchVulnSummary("", "","");
        
    }
    @Test
    public void testFetchTaggingSummary() throws Exception {
        String tagSummaryJson = "{\"data\":{\"output\":{\"assets\":124704,\"untagged\":49384,\"tagged\":75320,\"compliance\":60}},\"message\":\"success\"}";
        when(HttpUtil.get(anyString(),anyString())).thenReturn(tagSummaryJson);
        Map<String, Object> tagSummary = AssetGroupUtil.fetchTaggingSummary("", "","");
        assertThat(tagSummary.get("total"), is(124704l));
    }
    
    @Test(expected=Exception.class)
    public void testFetchTaggingSummaryException() throws Exception {
        String tagSummaryJson = "{\"data1\":{\"output\":{\"assets\":124704,\"untagged\":49384,\"tagged\":75320,\"compliance\":60}},\"message\":\"success\"}";
        when(HttpUtil.get(anyString(),anyString())).thenReturn(tagSummaryJson);
        AssetGroupUtil.fetchTaggingSummary("", "","");
        
    }
    
    @Test
    public void testFetchCertSummary() throws Exception {
        String certSummaryJson = "{\"data\":{\"output\":{\"certificates\":1320,\"certificates_expiring\":0}},\"message\":\"success\"}";
        when(HttpUtil.get(anyString(),anyString())).thenReturn(certSummaryJson);
        Map<String, Object> certSummary = AssetGroupUtil.fetchCertSummary("", "","");
        assertThat(certSummary.get("total"), is(1320l));
    }
 
    @Test(expected=Exception.class)
    public void testFetchCertSummaryException() throws Exception {
        String certSummaryJson = "{\"data1\":{\"output\":{\"certificates\":1320,\"certificates_expiring\":0}},\"message\":\"success\"}";
        when(HttpUtil.get(anyString(),anyString())).thenReturn(certSummaryJson);
        AssetGroupUtil.fetchCertSummary("", "","");
       
    }
    
    @Test
    public void testFetchIssuesInfo() throws Exception {
        String issueDistrJson = "{\"data\":{\"distribution\":{\"total_issues\":113688,\"ruleCategory_percentage\":{\"tagging\":57,\"security\":25,\"costOptimization\":7,\"governance\":11},\"distribution_by_severity\":{\"high\":27918,\"critical\":718,\"low\":79342,\"medium\":5710},\"distribution_ruleCategory\":{\"tagging\":64914,\"security\":29327,\"costOptimization\":8962,\"governance\":10485}}},\"message\":\"success\"}";
        when(HttpUtil.get(anyString(),anyString())).thenReturn(issueDistrJson);
        List<Map<String, Object>> issueDistribution = AssetGroupUtil.fetchIssuesInfo("", "",Arrays.asList("infra"),"");
        assertThat(issueDistribution.get(0).get("domain"), is("infra"));
        assertThat(issueDistribution.get(0).get("total"), is(113688l));
    }
    
    @Test(expected=Exception.class)
    public void testFetchIssuesInfoException() throws Exception {
        String issueDistrJson = "{\"data1\":{\"distribution\":{\"total_issues\":113688,\"ruleCategory_percentage\":{\"tagging\":57,\"security\":25,\"costOptimization\":7,\"governance\":11},\"distribution_by_severity\":{\"high\":27918,\"critical\":718,\"low\":79342,\"medium\":5710},\"distribution_ruleCategory\":{\"tagging\":64914,\"security\":29327,\"costOptimization\":8962,\"governance\":10485}}},\"message\":\"success\"}";
        when(HttpUtil.get(anyString(),anyString())).thenReturn(issueDistrJson);
        AssetGroupUtil.fetchIssuesInfo("", "",Arrays.asList("infra"),"");
        
    }

}