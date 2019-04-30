package com.tmobile.cso.pacman.inventory.vo;

import java.util.Date;

public class IAMCertificateVH {

	/** The serverCertificateName. */
	String serverCertificateName;

	/** The arn. */
	String arn;

	/** The expiryDate. */
	Date expiryDate;

	public String getServerCertificateName() {
		return serverCertificateName;
	}

	public void setServerCertificateName(String serverCertificateName) {
		this.serverCertificateName = serverCertificateName;
	}

	public String getArn() {
		return arn;
	}

	public void setArn(String arn) {
		this.arn = arn;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}



}
