package org.addhen.ushahidi.data;

public class AddSmsData {
	private String addSmsUsername;
	private String addSmsPassword;
	private String addSmsMessageFrom;
	private String addSmsMessageDescription;
	private String addSmsMessageDate;
	
	public AddSmsData() {
		addSmsUsername = "";
		addSmsPassword = "";
		addSmsMessageFrom = "";
		addSmsMessageDescription = "";
		addSmsMessageDate = "";
	}
	
	public String getSmsUsername() {
		return this.addSmsUsername;
	}
	
	public void setSmsUsername( String smsUsername) {
		this.addSmsUsername = smsUsername;
	}
	
	public String getSmsPassword() {
		return addSmsPassword;
	}
	
	public void setSmsPassword( String smsPassword ) {
		this.addSmsPassword =  smsPassword;
	}
	
	public String getSmsMessageFrom() {
		return this.addSmsMessageFrom;
	}
	
	public void setSmsMessageFrom( String smsMessageFrom ) {
		this.addSmsMessageFrom = smsMessageFrom;
	}
	
	public String getSmsMessageDescription() {
		return this.addSmsMessageDescription;
	}
	
	public void setSmsMessageDescription( String smsMessageDescription ) {
		this.addSmsMessageDescription = smsMessageDescription;
	}
	
	public String getSmsMessageDate() {
		return this.addSmsMessageDate;
	}
	
	public void setSmsMessageDate( String smsMessageDate ) {
		this.addSmsMessageDate = smsMessageDate;
	}

}
