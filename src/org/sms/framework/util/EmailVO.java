package org.sms.framework.util;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class EmailVO {

	private String subject;
	private String msg;
	private String[] toAddresses;
	private String[] ccAddresses;
	private String[] bccAddresses;
	private String fromAddress;

	public EmailVO(String[] toAddr, String[] ccAddr, String[] bccAddr,
			String fromAddr, String msg) {

		if (ArrayUtils.isEmpty(toAddr) && ArrayUtils.isEmpty(ccAddr)
				&& ArrayUtils.isEmpty(bccAddr)) {
			throw new IllegalArgumentException(
					"ERR201: No valid recipient address provided");
		}

		if (StringUtils.isEmpty(fromAddr)) {
			throw new IllegalArgumentException(
					"ERR202: No valid sender address provided");

		}
		if (StringUtils.isEmpty(msg)) {
			throw new IllegalArgumentException(
					"ERR203 : No message has been provided");
		}

		this.setToAddresses(toAddr);
		this.setFromAddress(fromAddr);
		this.setCcAddresses(ccAddr);
		this.setBccAddresses(bccAddr);
		this.setMsg(msg);

	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String[] getToAddresses() {
		return toAddresses;
	}

	public void setToAddresses(String[] toAddresses) {
		this.toAddresses = toAddresses;
	}

	public String[] getCcAddresses() {
		return ccAddresses;
	}

	public void setCcAddresses(String[] ccAddresses) {
		this.ccAddresses = ccAddresses;
	}

	public String[] getBccAddresses() {
		return bccAddresses;
	}

	public void setBccAddresses(String[] bccAddresses) {
		this.bccAddresses = bccAddresses;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
}
