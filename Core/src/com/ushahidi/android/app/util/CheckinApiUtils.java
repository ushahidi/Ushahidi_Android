/**
 ** Copyright (c) 2010 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 **
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.
 **
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 **
 **/

package com.ushahidi.android.app.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.entities.Checkin;
import com.ushahidi.android.app.entities.MediaEntity;
import com.ushahidi.android.app.entities.UserEntity;

/**
 * Handle processing of the JSON string as returned from the HTTP request. Main
 * deals with reports related HTTP request.
 * 
 * @author eyedol
 */
public class CheckinApiUtils {

	private JSONObject jsonObject;

	private boolean processingResult;

	public CheckinApiUtils(String jsonString) {
		processingResult = true;

		try {
			jsonObject = new JSONObject(jsonString);
		} catch (JSONException e) {
			new Util().log("JSONException", e);
			processingResult = false;
		}
	}

	private JSONObject getCheckinsObject() {
		try {
			return jsonObject.getJSONObject("payload");
		} catch (JSONException e) {
			return new JSONObject();
		}
	}

	private JSONArray getCheckinsArray() {
		try {
			return getCheckinsObject().getJSONArray("checkins");
		} catch (JSONException e) {
			return new JSONArray();
		}
	}

	private JSONArray getCheckinsUsersArray() {
		try {
			return getCheckinsObject().getJSONArray("users");
		} catch (JSONException e) {
			return new JSONArray();
		}
	}

	public ArrayList<UserEntity> getCheckinsUsersList() {
		if (processingResult) {
			ArrayList<UserEntity> checkinsUsersList = new ArrayList<UserEntity>();
			JSONArray checkinsUsersArray = getCheckinsUsersArray();
			if (checkinsUsersArray != null) {
				for (int index = 0; index < checkinsUsersArray.length(); index++) {
					UserEntity users = new UserEntity();

					try {
						users.setUserId(checkinsUsersArray.getJSONObject(index)
								.getInt("id"));
						users.setUsername(checkinsUsersArray.getJSONObject(
								index).getString("name"));
						users.setColor(checkinsUsersArray.getJSONObject(index)
								.getString("color"));
					} catch (JSONException e) {

						processingResult = false;
						return null;
					}

					checkinsUsersList.add(users);
				}

				return checkinsUsersList;
			}
		}
		return null;
	}

	public List<Checkin> getCheckinsList(Context context) {
		new Util().log("Save report");
		if (processingResult) {
			List<Checkin> checkinList = new ArrayList<Checkin>();
			ArrayList<UserEntity> checkinsUsersList = new ArrayList<UserEntity>();
			JSONArray checkinsArray = getCheckinsArray();
			int id = 0;
			if (checkinsArray != null && checkinsArray.length() > 0) {
				for (int i = 0; i < checkinsArray.length(); i++) {
					Checkin currentCheckin = new Checkin();
					try {
						id = checkinsArray.getJSONObject(i).getInt("id");
						currentCheckin.setCheckinId(id);
						currentCheckin.setLocationName(checkinsArray
								.getJSONObject(i).getString("loc"));
						currentCheckin.setLocationLatitude(checkinsArray
								.getJSONObject(i).getString("lat"));
						currentCheckin.setLocationLongitude(checkinsArray
								.getJSONObject(i).getString("lon"));
						currentCheckin.setDate(checkinsArray.getJSONObject(i)
								.getString("date"));
						currentCheckin.setMessage(checkinsArray
								.getJSONObject(i).getString("msg"));
						if (checkinsArray.getJSONObject(i).isNull("user")) {
							currentCheckin.setUsername(checkinsArray
									.getJSONObject(i).getString("name"));
						} else {
							currentCheckin.setUserId(checkinsArray
									.getJSONObject(i).getJSONObject("user")
									.getInt("id"));
							//add users
							UserEntity users = new UserEntity();
							users.setUserId(checkinsArray.getJSONObject(i)
									.getJSONObject("user").getInt("id"));
							users.setUsername(checkinsArray.getJSONObject(i)
									.getJSONObject("user").getString("name"));
							users.setColor(checkinsArray.getJSONObject(i)
									.getJSONObject("user").getString("color"));
							checkinsUsersList.add(users);
							saveUsers(checkinsUsersList);
						}

						// retrieve media.
						if (!checkinsArray.getJSONObject(i).isNull("media")) {
							JSONArray mediaArr = checkinsArray.getJSONObject(i)
									.getJSONArray("media");
							for (int w = 0; w < mediaArr.length(); w++) {
								try {
									if (!mediaArr.getJSONObject(w).isNull("id")) {

										// look out for images
										if (mediaArr.getJSONObject(w).getInt(
												"type") == 1
												&& (!mediaArr.getJSONObject(w)
														.isNull("link"))) {

											final String fileName = Util
													.getDateTime() + ".jpg";
											// save images to file
											saveMedia(mediaArr.getJSONObject(w)
													.getInt("id"), (int) id,
													mediaArr.getJSONObject(w)
															.getInt("type"),
													fileName);
											if (mediaArr.getJSONObject(w)
													.getString("link")
													.startsWith("http")) {
												saveImages(mediaArr
														.getJSONObject(w)
														.getString("link"),
														fileName, context);
											} else {
												final String link = Preferences.domain
														+ "/media/uploads/"
														+ mediaArr
																.getJSONObject(
																		w)
																.getString(
																		"link");

												saveImages(link, fileName,
														context);
											}

										} else {
											// save media in database
											saveMedia(mediaArr.getJSONObject(w)
													.getInt("id"), (int) id,
													mediaArr.getJSONObject(w)
															.getInt("type"),
													mediaArr.getJSONObject(w)
															.getString("link"));
										}
									}
								} catch (JSONException exc) {
									new Util().log("JSONException", exc);
								}
							}
						}
					} catch (JSONException e) {
						new Util().log("JSONException", e);
						processingResult = false;
						return null;
					}
					checkinList.add(currentCheckin);
				}
				return checkinList;
			}

		}
		return null;
	}

	// Save checkins into database
	public boolean saveCheckins(Context context) {
		List<Checkin> checkins = getCheckinsList(context);

		if (checkins != null) {
			return Database.mCheckin.addCheckins(checkins);
		}

		return false;
	}

	public boolean saveUsers() {
		List<UserEntity> users = getCheckinsUsersList();
		if (users != null && users.size() > 0) {
			return Database.mUserDao.addUser(users);
		}
		return false;
	}
	
	public boolean saveUsers(List<UserEntity> users) {
		if (users != null && users.size() > 0) {
			return Database.mUserDao.addUser(users);
		}
		return false;
	}

	private void saveMedia(int mediaId, int checkinId, int type, String link) {
		new Util().log("downloading... " + link + " CheckinId: " + checkinId);
		MediaEntity media = new MediaEntity();
		media.setMediaId(mediaId);
		media.setCheckinId(checkinId);
		media.setType(type);
		media.setLink(link);
		List<MediaEntity> sMedia = new ArrayList<MediaEntity>();
		sMedia.add(media);

		// save new data
		Database.mMediaDao.addMedia(sMedia);
	}

	private void saveImages(String linkUrl, String fileName, Context context) {
		new Util().log("Save Images: " + linkUrl + " FileName: " + fileName);
		if (!TextUtils.isEmpty(linkUrl)) {
			ImageManager.downloadImage(linkUrl, fileName, context);
		}
	}

}
