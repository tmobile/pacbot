package com.tmobile.cso.pacman.qualys.dto;

import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;


/**
 * The Class Vuln.
 */
public class Vuln {

    /** The qid. */
    private String qid;
    
    /** The vulntype. */
    private String vulntype;
    
    /** The severitylevel. */
    private byte severitylevel;
    
    /** The title. */
    private String title;
    
    /** The category. */
    private String category;
    
    /** The lastservicemodificationdatetime. */
    private XMLGregorianCalendar lastservicemodificationdatetime;
    
    /** The publisheddatetime. */
    private XMLGregorianCalendar publisheddatetime;
    
    /** The bugtraqlist. */
    private KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.BUGTRAQLIST bugtraqlist;
    
    /** The patchable. */
    private String patchable;
    
    /** The softwarelist. */
    private KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.SOFTWARELIST softwarelist;
    
    /** The vendorreferencelist. */
    private KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.VENDORREFERENCELIST vendorreferencelist;
    
    /** The cvelist. */
    private KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.CVELIST cvelist;
    
    /** The diagnosis. */
    private String diagnosis;
    
    /** The diagnosiscomment. */
    private String diagnosiscomment;
    
    /** The consequence. */
    private String consequence;
    
    /** The consequencecomment. */
    private String consequencecomment;
    
    /** The solution. */
    private String solution;
    
    /** The solutioncomment. */
    private String solutioncomment;
    
    /** The compliancelist. */
    private KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.COMPLIANCELIST compliancelist;
    
    /** The correlation. */
    private KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.CORRELATION correlation;
    
    /** The cvss. */
    private KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.CVSS cvss;
    
    /** The cvssv 3. */
    private KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.CVSSV3 cvssv3;
    
    /** The pciflag. */
    private byte pciflag;
    
    /** The pcireasons. */
    private KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.PCIREASONS pcireasons;
    
    /** The supportedmodules. */
    private String supportedmodules;
    
    /** The discovery. */
    private KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.DISCOVERY discovery;
    
    /** The isdisabled. */
    private String isdisabled;
    
    /** The load date. */
    private Date _loadDate;
    
    /** The latest. */
    private boolean latest;
    
    /** The classification. */
    private String classification;

    /**
     * Gets the qid.
     *
     * @return the qid
     */
    public String getQid() {
        return qid;
    }

    /**
     * Sets the qid.
     *
     * @param qid the new qid
     */
    public void setQid(String qid) {
        this.qid = qid;
    }

    /**
     * Gets the vulntype.
     *
     * @return the vulntype
     */
    public String getVulntype() {
        return vulntype;
    }

    /**
     * Sets the vulntype.
     *
     * @param vulntype the new vulntype
     */
    public void setVulntype(String vulntype) {
        this.vulntype = vulntype;
    }

    /**
     * Gets the severitylevel.
     *
     * @return the severitylevel
     */
    public byte getSeveritylevel() {
        return severitylevel;
    }

    /**
     * Sets the severitylevel.
     *
     * @param severitylevel the new severitylevel
     */
    public void setSeveritylevel(byte severitylevel) {
        this.severitylevel = severitylevel;
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the category.
     *
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category.
     *
     * @param category the new category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the lastservicemodificationdatetime.
     *
     * @return the lastservicemodificationdatetime
     */
    public XMLGregorianCalendar getLastservicemodificationdatetime() {
        return lastservicemodificationdatetime;
    }

    /**
     * Sets the lastservicemodificationdatetime.
     *
     * @param lastservicemodificationdatetime the new lastservicemodificationdatetime
     */
    public void setLastservicemodificationdatetime(XMLGregorianCalendar lastservicemodificationdatetime) {
        this.lastservicemodificationdatetime = lastservicemodificationdatetime;
    }

    /**
     * Gets the publisheddatetime.
     *
     * @return the publisheddatetime
     */
    public XMLGregorianCalendar getPublisheddatetime() {
        return publisheddatetime;
    }

    /**
     * Sets the publisheddatetime.
     *
     * @param publisheddatetime the new publisheddatetime
     */
    public void setPublisheddatetime(XMLGregorianCalendar publisheddatetime) {
        this.publisheddatetime = publisheddatetime;
    }

    /**
     * Gets the bugtraqlist.
     *
     * @return the bugtraqlist
     */
    public KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.BUGTRAQLIST getBugtraqlist() {
        return bugtraqlist;
    }

    /**
     * Sets the bugtraqlist.
     *
     * @param bugtraqlist the new bugtraqlist
     */
    public void setBugtraqlist(KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.BUGTRAQLIST bugtraqlist) {
        this.bugtraqlist = bugtraqlist;
    }

    /**
     * Gets the patchable.
     *
     * @return the patchable
     */
    public String getPatchable() {
        return patchable;
    }

    /**
     * Sets the patchable.
     *
     * @param patchable the new patchable
     */
    public void setPatchable(String patchable) {
        this.patchable = patchable;
    }

    /**
     * Gets the softwarelist.
     *
     * @return the softwarelist
     */
    public KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.SOFTWARELIST getSoftwarelist() {
        return softwarelist;
    }

    /**
     * Sets the softwarelist.
     *
     * @param softwarelist the new softwarelist
     */
    public void setSoftwarelist(KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.SOFTWARELIST softwarelist) {
        this.softwarelist = softwarelist;
    }

    /**
     * Gets the vendorreferencelist.
     *
     * @return the vendorreferencelist
     */
    public KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.VENDORREFERENCELIST getVendorreferencelist() {
        return vendorreferencelist;
    }

    /**
     * Sets the vendorreferencelist.
     *
     * @param vendorreferencelist the new vendorreferencelist
     */
    public void setVendorreferencelist(
            KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.VENDORREFERENCELIST vendorreferencelist) {
        this.vendorreferencelist = vendorreferencelist;
    }

    /**
     * Gets the cvelist.
     *
     * @return the cvelist
     */
    public KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.CVELIST getCvelist() {
        return cvelist;
    }

    /**
     * Sets the cvelist.
     *
     * @param cvelist the new cvelist
     */
    public void setCvelist(KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.CVELIST cvelist) {
        this.cvelist = cvelist;
    }

    /**
     * Gets the diagnosis.
     *
     * @return the diagnosis
     */
    public String getDiagnosis() {
        return diagnosis;
    }

    /**
     * Sets the diagnosis.
     *
     * @param diagnosis the new diagnosis
     */
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    /**
     * Gets the diagnosiscomment.
     *
     * @return the diagnosiscomment
     */
    public String getDiagnosiscomment() {
        return diagnosiscomment;
    }

    /**
     * Sets the diagnosiscomment.
     *
     * @param diagnosiscomment the new diagnosiscomment
     */
    public void setDiagnosiscomment(String diagnosiscomment) {
        this.diagnosiscomment = diagnosiscomment;
    }

    /**
     * Gets the consequence.
     *
     * @return the consequence
     */
    public String getConsequence() {
        return consequence;
    }

    /**
     * Sets the consequence.
     *
     * @param consequence the new consequence
     */
    public void setConsequence(String consequence) {
        this.consequence = consequence;
    }

    /**
     * Gets the consequencecomment.
     *
     * @return the consequencecomment
     */
    public String getConsequencecomment() {
        return consequencecomment;
    }

    /**
     * Sets the consequencecomment.
     *
     * @param consequencecomment the new consequencecomment
     */
    public void setConsequencecomment(String consequencecomment) {
        this.consequencecomment = consequencecomment;
    }

    /**
     * Gets the solution.
     *
     * @return the solution
     */
    public String getSolution() {
        return solution;
    }

    /**
     * Sets the solution.
     *
     * @param solution the new solution
     */
    public void setSolution(String solution) {
        this.solution = solution;
    }

    /**
     * Gets the solutioncomment.
     *
     * @return the solutioncomment
     */
    public String getSolutioncomment() {
        return solutioncomment;
    }

    /**
     * Sets the solutioncomment.
     *
     * @param solutioncomment the new solutioncomment
     */
    public void setSolutioncomment(String solutioncomment) {
        this.solutioncomment = solutioncomment;
    }

    /**
     * Gets the compliancelist.
     *
     * @return the compliancelist
     */
    public KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.COMPLIANCELIST getCompliancelist() {
        return compliancelist;
    }

    /**
     * Sets the compliancelist.
     *
     * @param compliancelist the new compliancelist
     */
    public void setCompliancelist(KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.COMPLIANCELIST compliancelist) {
        this.compliancelist = compliancelist;
    }

    /**
     * Gets the correlation.
     *
     * @return the correlation
     */
    public KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.CORRELATION getCorrelation() {
        return correlation;
    }

    /**
     * Sets the correlation.
     *
     * @param correlation the new correlation
     */
    public void setCorrelation(KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.CORRELATION correlation) {
        this.correlation = correlation;
    }

    /**
     * Gets the cvss.
     *
     * @return the cvss
     */
    public KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.CVSS getCvss() {
        return cvss;
    }

    /**
     * Sets the cvss.
     *
     * @param cvss the new cvss
     */
    public void setCvss(KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.CVSS cvss) {
        this.cvss = cvss;
    }

    /**
     * Gets the cvssv 3.
     *
     * @return the cvssv 3
     */
    public KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.CVSSV3 getCvssv3() {
        return cvssv3;
    }

    /**
     * Sets the cvssv 3.
     *
     * @param cvssv3 the new cvssv 3
     */
    public void setCvssv3(KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.CVSSV3 cvssv3) {
        this.cvssv3 = cvssv3;
    }

    /**
     * Gets the pciflag.
     *
     * @return the pciflag
     */
    public byte getPciflag() {
        return pciflag;
    }

    /**
     * Sets the pciflag.
     *
     * @param pciflag the new pciflag
     */
    public void setPciflag(byte pciflag) {
        this.pciflag = pciflag;
    }

    /**
     * Gets the pcireasons.
     *
     * @return the pcireasons
     */
    public KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.PCIREASONS getPcireasons() {
        return pcireasons;
    }

    /**
     * Sets the pcireasons.
     *
     * @param pcireasons the new pcireasons
     */
    public void setPcireasons(KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.PCIREASONS pcireasons) {
        this.pcireasons = pcireasons;
    }

    /**
     * Gets the supportedmodules.
     *
     * @return the supportedmodules
     */
    public String getSupportedmodules() {
        return supportedmodules;
    }

    /**
     * Sets the supportedmodules.
     *
     * @param supportedmodules the new supportedmodules
     */
    public void setSupportedmodules(String supportedmodules) {
        this.supportedmodules = supportedmodules;
    }

    /**
     * Gets the discovery.
     *
     * @return the discovery
     */
    public KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.DISCOVERY getDiscovery() {
        return discovery;
    }

    /**
     * Sets the discovery.
     *
     * @param discovery the new discovery
     */
    public void setDiscovery(KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST.VULN.DISCOVERY discovery) {
        this.discovery = discovery;
    }

    /**
     * Gets the isdisabled.
     *
     * @return the isdisabled
     */
    public String getIsdisabled() {
        return isdisabled;
    }

    /**
     * Sets the isdisabled.
     *
     * @param isdisabled the new isdisabled
     */
    public void setIsdisabled(String isdisabled) {
        this.isdisabled = isdisabled;
    }

    /**
     * Gets the load date.
     *
     * @return the load date
     */
    public Date get_loadDate() {
        return _loadDate;
    }

    /**
     * Sets the load date.
     *
     * @param date the new load date
     */
    public void set_loadDate(Date date) {
        this._loadDate = date;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Vuln [qid=" + qid + ", vulntype=" + vulntype + ", severitylevel=" + severitylevel + ", title=" + title
                + ", category=" + category + ", lastservicemodificationdatetime=" + lastservicemodificationdatetime
                + ", publisheddatetime=" + publisheddatetime + ", bugtraqlist=" + bugtraqlist + ", patchable="
                + patchable + ", softwarelist=" + softwarelist + ", vendorreferencelist=" + vendorreferencelist
                + ", cvelist=" + cvelist + ", diagnosis=" + diagnosis + ", diagnosiscomment=" + diagnosiscomment
                + ", consequence=" + consequence + ", consequencecomment=" + consequencecomment + ", solution="
                + solution + ", solutioncomment=" + solutioncomment + ", compliancelist=" + compliancelist
                + ", correlation=" + correlation + ", cvss=" + cvss + ", cvssv3=" + cvssv3 + ", pciflag=" + pciflag
                + ", pcireasons=" + pcireasons + ", supportedmodules=" + supportedmodules + ", discovery=" + discovery
                + ", isdisabled=" + isdisabled + "]";
    }

    /**
     * Checks if is latest.
     *
     * @return true, if is latest
     */
    public boolean isLatest() {
        return latest;
    }

    /**
     * Sets the latest.
     *
     * @param latest the new latest
     */
    public void setLatest(boolean latest) {
        this.latest = latest;
    }

    /**
     * Gets the classification.
     *
     * @return the classification
     */
    public String getClassification() {
        return classification;
    }

    /**
     * Sets the classification.
     *
     * @param classification the new classification
     */
    public void setClassification(String classification) {
        this.classification = classification;
    }
}
