package com.example.tubes.Callback;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GetUserDataCallBack extends AsyncTask<String, String, ArrayList<HashMap<String, String>>> {
    //Global global = new Global();
    private ProgressDialog pDialog;
    Context context;
    int success;
    String message;
    private JSONParser jsonParser = new JSONParser();
    JSONArray jsonArray = null;
    ArrayList<HashMap<String, String>> arrayListRet;

    public GetUserDataCallBack(Context m_context) {
        this.context= m_context;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }


    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
        try {
            // Building Parameters
            List<NameValuePair> paramsed = new ArrayList<NameValuePair>();

            Log.d("username : ",params[0]);

            paramsed.add(new BasicNameValuePair("username", params[0])); //username


            arrayListRet = new ArrayList<>();

            //call http
            JSONObject json = jsonParser.makeHttpRequest("http://dev.projectlab.co.id/mit/1317016/get_user_data.php",
                    "POST", paramsed);
            System.out.println("json 2 = "+json.toString());

            success = json.getInt("success");

            message = json.getString("message");


            if (success == 1) {
                jsonArray = json.getJSONArray("data");

                HashMap<String, String> map = new HashMap<String, String>();

                map.put("success", String.valueOf(success));
                map.put("message", message);

                arrayListRet.add(map);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject c = jsonArray.getJSONObject(i);
                    HashMap<String, String> map1 = new HashMap<String, String>();

                    map1.put("success", String.valueOf(success));
                    map1.put("nama", String.valueOf(c.getString("nama")));
                    map1.put("email", String.valueOf(c.getString("email")));
                    map1.put("no_hp", String.valueOf(c.getString("no_hp")));
                    map1.put("username", String.valueOf(c.getString("username")));
                    map1.put("tgl_lahir", String.valueOf(c.getString("tgl_lahir")));
                    map1.put("alamat", String.valueOf(c.getString("alamat")));
                    map1.put("pekerjaan", String.valueOf(c.getString("pekerjaan")));
                    map1.put("image_url", String.valueOf(c.getString("image_url")));
                    arrayListRet.add(map1);
                }
            } else {
                //Unsuccessfully picking book. Another driver has taken the order.
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("success", String.valueOf(success));
                map.put("message", message);
                Log.d("messageError : ",message);

                arrayListRet.clear();
                arrayListRet.add(map);

            }
        } catch (Exception error) {
            Log.d("Error Http",error.getMessage());
            HashMap<String, String> map = new HashMap<String, String>();

            map.put("success", "-1");
            map.put("message", "Internal Error : Hubungi Customer Service");

            arrayListRet.clear();
            arrayListRet.add(map);
        }

        System.out.println("arrayListRet = "+arrayListRet);
        return arrayListRet;
    }


    /**
     * After completing sys_navdrawer_background task Dismiss the progress dialog
     * **/
    protected void onPostExecute(ArrayList<HashMap<String, String>> file_url) {
    }
}

