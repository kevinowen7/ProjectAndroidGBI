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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RegisterUserCallback extends AsyncTask<String, String, ArrayList<HashMap<String, String>>> {
    //Global global = new Global();
    private ProgressDialog pDialog;
    Context context;
    int success;
    String message;
    private JSONParser jsonParser = new JSONParser();
    JSONArray jsonArray = null;
    ArrayList<HashMap<String, String>> arrayListRet;

    public RegisterUserCallback(Context m_context) {
        this.context= m_context;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }


    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(String... params) {

        // Building Parameters
        List<NameValuePair> paramsed = new ArrayList<NameValuePair>();

        Log.d("nama : ",params[0]);
        Log.d("email : ",params[1]);
        Log.d("no_hp : ",params[2]);
        Log.d("username : ",params[3]);
        Log.d("pass : ",params[4]);
        Log.d("tgl_lahir : ",params[5]);
        Log.d("alamat : ",params[6]);
        Log.d("pekerjaan : ",params[7]);

        paramsed.add(new BasicNameValuePair("nama", params[0])); //nama
        paramsed.add(new BasicNameValuePair("email", params[1])); //email
        paramsed.add(new BasicNameValuePair("no_hp", params[2])); //no_hp
        paramsed.add(new BasicNameValuePair("username", params[3])); //username
        paramsed.add(new BasicNameValuePair("password", params[4])); //password
        paramsed.add(new BasicNameValuePair("tgl_lahir", params[5])); //tgl_lahir
        paramsed.add(new BasicNameValuePair("alamat", params[6])); //alamat
        paramsed.add(new BasicNameValuePair("pekerjaan", params[7])); //pekerjaan


        arrayListRet = new ArrayList<>();

        try {
            //call http
            JSONObject json = jsonParser.makeHttpRequest("http://dev.projectlab.co.id/mit/1317016/mst_register.php",
                    "POST", paramsed);
            System.out.println("json 2 = "+json.toString());
            //Log.d("CEKIDBOOK_MSG",json.toString());

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
                    map1.put("payload", String.valueOf(c));
                    arrayListRet.add(map1);
                }
            } else {
                //Unsuccessfully picking book. Another driver has taken the order.
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("success", String.valueOf(success));
                map.put("message", message);

                arrayListRet.add(map);

            }
        } catch (Exception error) {
            Log.d("Error Http",error.getMessage());
            HashMap<String, String> map = new HashMap<String, String>();

            map.put("success", "-1");
            map.put("message", "Internal Error : Hubungi Customer Service");


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

