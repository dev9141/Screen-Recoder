package com.example.screenrecorder;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SPVariables {

    public static void setInt(String name, int value, Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        sp.edit().putInt(name, value).apply();
    }

    public static int getInt(String name, Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(name, 0);
    }

    public static void setLong(String name, long value, Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        sp.edit().putLong(name, value).apply();
    }

    public static long getLong(String name, Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getLong(name, 0L);
    }

    public static void setStringSet(String key, Set<String> set, Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        sp.edit().putStringSet(key, set).apply();
    }

    public static JSONArray getEditedTemplates(Context c) {
        JSONArray arr = new JSONArray();
        Set<String> editedTemplates = SPVariables.getStringSet(Keys.editedtemplates, c);
        for (String editedTemplate : editedTemplates
                ) {
            try {
                String editedTemplateJSON = SPVariables.getString(editedTemplate, c);
                JSONObject obj = new JSONObject(editedTemplateJSON);
                arr.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arr;
    }

    public static Set<String> getStringSet(String key, Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getStringSet(key, new HashSet<String>());
    }

    public static String getString(String name, Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getString(name, "");
    }

    public static Set<String> getEditedTemplateIds(Context c) {
        Set<String> arr = new HashSet<>();
        Set<String> editedTemplates = SPVariables.getStringSet(Keys.editedtemplates, c);
        for (String editedTemplate : editedTemplates
                ) {
            try {
                String editedTemplateJSON = SPVariables.getString(editedTemplate, c);
                JSONObject obj = new JSONObject(editedTemplateJSON);
                arr.add(obj.getString("idOfTemplate"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arr;
    }

    public static HashMap<String, String> getEditedTemplateKeys(Context c) {
        Set<String> editedTemplates = SPVariables.getStringSet(Keys.editedtemplates, c);
        HashMap<String, String> map = new HashMap<>();
        for (String editedTemplate : editedTemplates
                ) {
            try {
                String editedTemplateJSON = SPVariables.getString(editedTemplate, c);
                map.put(editedTemplate, editedTemplateJSON);
            } catch (Exception ee) {
            }
        }
        return map;
    }

    public static JSONObject getCurrentEditedTemplateAsJSON(Context c, String idOfTemplate, String idOfDocument, String idOfFile) {
        String idOfClient = SPVariables.getString(Keys.currentClientID, c);
        String userid = SPVariables.getString(Keys.usertableid, c);
        Set<String> editedTemplates = SPVariables.getStringSet(Keys.editedtemplates, c);
        JSONObject obj = new JSONObject();
        for (String editedTemplate : editedTemplates
                ) {
            try {
                String currentkey = idOfClient + "#" + idOfTemplate + "#" + idOfDocument + "#" + idOfFile + "#" + userid;
                if (currentkey.equals(editedTemplate)) {
                    String editedTemplateJSON = SPVariables.getString(editedTemplate, c);
                    obj = new JSONObject(editedTemplateJSON);
                }
            } catch (Exception ee) {
            }
        }
        return obj;
    }

    public static boolean isTemplateDownloadedByAnyUser(String gridrowid, Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        Map<String, ?> map = sp.getAll();
        for (Map.Entry<String, ?> entry : map.entrySet()
                ) {
            if (entry.getKey().startsWith("downloadedtemplate#") && entry.getKey().endsWith("#" + gridrowid)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTemplateDownloadedByParticularUser(String gridrowid, String userid, Context c) {
        String ans = SPVariables.getString("downloadedtemplate#" + userid + "#" + gridrowid, c);
        if (ans != null && ans.trim().length() > 0) {
            return true;
        }
        return false;
    }

    public static void addToPendingImageJSON(Context c, String compMainId, String dataMainId, String idOfGroup, String idOfTail,
                                             String idOfClient, String fileName, String Image, String thread) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("compMainId", compMainId);
            obj.put("dataMainId", dataMainId);
            obj.put("idOfGroup", idOfGroup);
            obj.put("idOfTail", idOfTail);
            obj.put("idOfClient", idOfClient);
            obj.put("fileName", fileName);
            obj.put("thread", thread);
            String jsonarr = SPVariables.getString(Keys.strJSONPendingImages, c);
            JSONArray arr = null;
            if (jsonarr.equals("")) {
                arr = new JSONArray();
            } else {
                arr = new JSONArray(jsonarr);
            }
            // check if already exists
            boolean existing = false;
            for (int i = 0; i < arr.length(); i++) {
                JSONObject objexisting = arr.getJSONObject(i);
                if (objexisting.get("fileName").equals(obj.get("fileName"))) {
                    existing = true;
                    break;
                }
            }
            if (!existing) {
                arr.put(obj);
            }
            SPVariables.setString(Keys.strJSONPendingImages, arr.toString(), c);
            int size = getPendingImageJSONCount(c);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void setString(String name, String value, Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        sp.edit().putString(name, value).apply();
    }

    public static int getPendingImageJSONCount(Context c) {
        String jsonarr = SPVariables.getString(Keys.strJSONPendingImages, c);
        JSONArray arr = null;
        if (jsonarr.equals("")) {
            arr = new JSONArray();
        } else {
            try {
                arr = new JSONArray(jsonarr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arr.length();
    }

    public static void removeFromPendingImageJSON(String filename, Context c) {
        try {
            String jsonarr = SPVariables.getString(Keys.strJSONPendingImages, c);
            JSONArray arr = null;
            JSONArray arrnew = new JSONArray("[]");
            if (jsonarr.equals("")) {
                arr = new JSONArray();
            } else {
                arr = new JSONArray(jsonarr);
            }
            // check if already exists
            boolean existing = false;
            for (int i = 0; i < arr.length(); i++) {
                JSONObject objexisting = arr.getJSONObject(i);
                if (objexisting.get("fileName").equals(filename)) {
                    existing = true;
                } else {
                    arrnew.put(objexisting);
                }
            }
            SPVariables.setString(Keys.strJSONPendingImages, arrnew.toString(), c);
            int size = getPendingImageJSONCount(c);
        } catch (Exception ee) {
        }
    }

    public static String getJSONPendingImageArray(Context c) {
        String jsonarr = SPVariables.getString(Keys.strJSONPendingImages, c);
        return jsonarr;
    }

    public static boolean isImagePendingforGridrow(String gridrowID, Context c) {
        try {
            String jsonarr = SPVariables.getString(Keys.strJSONPendingImages, c);
            JSONArray arr = null;
            JSONArray arrnew = new JSONArray("[]");
            if (jsonarr.equals("")) {
                arr = new JSONArray();
            } else {
                arr = new JSONArray(jsonarr);
            }
            for (int i = 0; i < arr.length(); i++) {
                JSONObject objexisting = arr.getJSONObject(i);
                if (objexisting.get("dataMainId").equals(gridrowID)) {
                    return true;
                }
            }
            return false;
        } catch (Exception ee) {
            return false;
        }
    }

    public static class Keys {

        public static final String strJSONPendingImages = "strJSONPendingImages";
        public static final String strToken = "strToken";
        public static final String strTokenForeground = "strTokenForeground";
        public static final String strEncryptionKey = "strEncryptionKey";
        public static final String strEncryptionKeyForeground = "strEncryptionKeyForeground";
        public static String strClientKey = "strClientKey";
        public static String strClientKeyForeground = "strClientKeyForeground";
        public static String netMode = "netMode";
        public static String un = "un";
        public static String pw = "pw";
        public static String username = "username";
        public static String usertableid = "usertableid";
        public static String noofclients = "noofclients";
        public static String remun = "remun";
        public static String rempass = "rempass";
        public static String remflag = "remflag";
        public static String currentdownloadname = "currentdownloadname";
        public static String currentdownloadpercent = "currentdownloadpercent";
        public static String currentdownloadid = "currentdownloadid";
        public static String currentaircraftid = "currentaircraftid";
        public static String currentcompmainid = "currentcompmainid";
        public static String currentClientID = "currentClientID";
        public static String currentClientName = "currentClientName";
        //public static String CompMainID="CompMainID";
        public static String lockedCount = "lockedcount";
        public static String lockedtime = "lockedtime";
        public static String LastSyncStartTime = "LastSyncStartTime";
        public static String LastSyncEndTime = "LastSyncEndTime";
        public static String lastSyncDateTime = "lastSyncDateTime";
        public static String agreement = "agreement";
        public static String TemplateJSON = "TemplateJSON";
        public static String editedtemplates = "editedtemplates";
        public static String currentdownloadstatus = "currentdownloadstatus"; // PLAY, PAUSE, STOP
        public static String basedownloadpercentage = "basedownloadpercentage";
        public static String IsImageEdited = "IsImageEdited";
        public static String LastUploadTime = "LastUploadTime";
        public static String DBVersion = "DBVersion";
    }
}
