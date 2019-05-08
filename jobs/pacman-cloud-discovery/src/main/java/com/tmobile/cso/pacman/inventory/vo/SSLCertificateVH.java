package com.tmobile.cso.pacman.inventory.vo;

import java.util.Date;
import java.util.List;

public class SSLCertificateVH {
<<<<<<< HEAD
	
	/** The domainName. */
	String domainName;
	
	/** The certificateARN. */
	String certificateARN;
	
	/** The expiryDate. */
	Date expiryDate;
	
=======

	/** The domainName. */
	String domainName;

	/** The certificateARN. */
	String certificateARN;

	/** The expiryDate. */
	Date expiryDate;

>>>>>>> cfdbfd0614b3defe9f0a27cf7508b392546c050d
	List<String> issuerDetails;

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getCertificateARN() {
		return certificateARN;
	}

	public void setCertificateARN(String certificateARN) {
		this.certificateARN = certificateARN;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public List<String> getIssuerDetails() {
		return issuerDetails;
	}

	public void setIssuerDetails(List<String> issuerDetails) {
		this.issuerDetails = issuerDetails;
	}

}
