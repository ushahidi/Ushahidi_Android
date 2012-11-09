package com.ushahidi.android.app.json;

import java.util.List;

public class UshahidiApiVersion {

	private static class Payload extends UshahidiApiResponse.Payload {
		private static class Version {
			private String version;
			private String database;
		}

		private List<Version> version;
		private int checkins;
		private String email;
		private String sms;
		private List<String> plugins;
	}

	private Payload payload;

	public String getDomain() {
		return payload.domain;
	}

	public String getSms() {
		return payload.sms;
	}

}
