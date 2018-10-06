/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package firebase.core;

import android.os.AsyncTask;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.io.TiFileFactory;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Void;
import java.util.HashMap;

@Kroll.module(name = "TitaniumFirebaseCore", id = "firebase.core")
public class TitaniumFirebaseCoreModule extends KrollModule
{
	private static final String LCAT = "TitaniumFirebaseCoreModule";
	private static final boolean DBG = TiConfig.LOGD;

	public TitaniumFirebaseCoreModule()
	{
		super();
	}

	// Public APIs

	@Kroll.method
	public void configure(@Kroll.argument(optional = true) KrollDict param)
	{
		if (param != null) {
			String apiKey = "";
			String databaseURL = "";
			String projectID = "";
			String storageBucket = "";
			String applicationID = "";
			String GCMSenderID = "";
			FirebaseOptions.Builder options = new FirebaseOptions.Builder();

			if (param.containsKey("file")) {
				// open file and parse it
				try {
					JSONObject json = new JSONObject(loadJSONFromAsset(param.getString("file")));
					JSONObject projectInfo = json.getJSONObject("project_info");
					String packageName = TiApplication.getAppCurrentActivity().getPackageName();

					if (projectInfo.has("storage_bucket")) {
						storageBucket = projectInfo.getString("storage_bucket");
					}
					if (projectInfo.has("firebase_url")) {
						databaseURL = projectInfo.getString("firebase_url");
					}
					if (projectInfo.has("project_number")) {
						GCMSenderID = projectInfo.getString("project_number");
					}
					if (projectInfo.has("project_id")) {
						projectID = projectInfo.getString("project_id");
					}
					if (json.has("client")) {
						JSONArray clients = json.getJSONArray("client");
						for (int i = 0, len = clients.length(); i < len; i++) {
							JSONObject client = clients.getJSONObject(i);
							JSONObject clientInfo = client.getJSONObject("client_info");
							String pName = clientInfo.getJSONObject("android_client_info").getString("package_name");
							if (pName.equals(packageName)) {
								applicationID = client.getJSONObject("client_info").getString("mobilesdk_app_id");
								apiKey = client.getJSONArray("api_key").getJSONObject(0).getString("current_key");
							}
						}
					}
				} catch (JSONException e) {
					Log.e(LCAT, "Error parsing file");
				}
			} else {
				// use parameters
				if (param.containsKey("APIKey")) {
					apiKey = param.getString("APIKey");
				}
				if (param.containsKey("databaseURL")) {
					databaseURL = param.getString("databaseURL");
				}
				if (param.containsKey("projectID")) {
					projectID = param.getString("projectID");
				}
				if (param.containsKey("storageBucket")) {
					storageBucket = param.getString("storageBucket");
				}
				if (param.containsKey("applicationID")) {
					applicationID = param.getString("applicationID");
				}
				if (param.containsKey("GCMSenderID")) {
					GCMSenderID = param.getString("GCMSenderID");
				}
			}

			options.setApiKey(apiKey);
			options.setDatabaseUrl(databaseURL);
			options.setProjectId(projectID);
			options.setStorageBucket(storageBucket);
			options.setApplicationId(applicationID);
			options.setGcmSenderId(GCMSenderID);

			try {
				FirebaseApp.initializeApp(getActivity().getApplicationContext(), options.build());
			} catch (IllegalStateException e) {
				Log.w(LCAT, "There was a problem initializing FirebaseApp or it was initialized a second time.");
			}
		} else {
			try {
				FirebaseApp.initializeApp(getActivity().getApplicationContext());
			} catch (IllegalStateException e) {
				Log.w(LCAT, "There was a problem initializing FirebaseApp or it was initialized a second time.");
			}
		}
	}

	@Kroll.method
	public void deleteInstanceId(final KrollFunction callback)
	{
		Log.d(LCAT, "deleting instanceId ... ");
		new AsyncTask<Void, Void, IOException>() {
			protected IOException doInBackground(Void ... v) {
				try {
					FirebaseInstanceId.getInstance().deleteInstanceId();
					Log.d(LCAT, "instanceId deleted");
					return null;
				} catch (IOException e) {
						e.printStackTrace();
						return e;
				}
			}
			protected void onPostExecute(IOException error) {
				if(callback != null) {
					HashMap args = new HashMap<>();
					args.put("success", error == null);
					if(error != null){
						args.put("error", error.getLocalizedMessage());
					}
					callback.call(getKrollObject(), args);
				}
			}
		}.execute();
	}

	@Kroll.method
	public void deleteToken(final String authorizedEntity, final String scope, final KrollFunction callback)
	{
		Log.d(LCAT, "deleting token ... ");
		new AsyncTask<Void, Void, IOException>() {
			protected IOException doInBackground(Void ... v) {
				try {
					FirebaseInstanceId.getInstance().deleteToken(authorizedEntity, scope);
					Log.d(LCAT, "token deleted");
					return null;
				} catch (IOException e) {
						e.printStackTrace();
						return e;
				}
			}
			protected void onPostExecute(IOException error) {
				if(callback != null) {
					HashMap args = new HashMap<>();
					args.put("success", error == null);
					if(error != null){
						args.put("error", error.getLocalizedMessage());
					}
					callback.call(getKrollObject(), args);
				}
			}
		}.execute();
	}

	public String loadJSONFromAsset(String filename)
	{
		String json = null;

		try {
			String url = this.resolveUrl(null, filename);
			Log.d(LCAT, "JSON Path: " + url);

			InputStream inStream = TiFileFactory.createTitaniumFile(new String[] { url }, false).getInputStream();
			byte[] buffer = new byte[inStream.available()];
			inStream.read(buffer);
			inStream.close();
			json = new String(buffer, "UTF-8");
		} catch (IOException ex) {
			Log.e(LCAT, "Error reading file");
			return "";
		}
		return json;
	}
}
