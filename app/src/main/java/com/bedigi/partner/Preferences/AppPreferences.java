package com.bedigi.partner.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppPreferences {
    private static final String APP_SHARED_PREFS = "urbanclap-partner";
    private SharedPreferences appSharedPrefs;
    private Editor prefsEditor;

    public AppPreferences(Context context) {
        this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    public void RemoveAllSharedPreference() {
        prefsEditor.clear();
        prefsEditor.commit();
    }

    public String getName() {
        return appSharedPrefs.getString("name", " ");
    }

    public void setName(String text) {
        prefsEditor.putString("name", text);
        prefsEditor.commit();
    }

    public String getPhone() {
        return appSharedPrefs.getString("phone", " ");
    }

    public void setPhone(String text) {
        prefsEditor.putString("phone", text);
        prefsEditor.commit();
    }

    public String getId() {
        return appSharedPrefs.getString("id", "");
    }

    public void setId(String text) {
        prefsEditor.putString("id", text);
        prefsEditor.commit();
    }

    public String getOrgId() {
        return appSharedPrefs.getString("org_id", " ");
    }

    public void setOrgId(String text) {
        prefsEditor.putString("org_id", text);
        prefsEditor.commit();
    }

    public String getEmail() {
        return appSharedPrefs.getString("email", " ");
    }

    public void setEmail(String text) {
        prefsEditor.putString("email", text);
        prefsEditor.commit();
    }

    public String getImage() {
        return appSharedPrefs.getString("image", " ");
    }

    public void setImage(String text) {
        prefsEditor.putString("image", text);
        prefsEditor.commit();
    }

    public String getReferral() {
        return appSharedPrefs.getString("referral", "");
    }

    public void setReferral(String text) {
        prefsEditor.putString("referral", text);
        prefsEditor.commit();
    }

}