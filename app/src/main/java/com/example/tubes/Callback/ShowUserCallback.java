package com.example.tubes.Callback;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowUserCallback extends AsyncTask<String, String, ArrayList<HashMap<String, String>>> {
    //Global global = new Global();
    Context context;
    int success;
    String message;
    private JSONParser jsonParser = new JSONParser();
    JSONArray jsonArray = null;
    ArrayList<HashMap<String, String>> arrayListRet;

    public ShowUserCallback(Context m_context) {
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

        Log.d("limitBottom : ",params[0]);
        Log.d("limitTop : ",params[0]);

        paramsed.add(new BasicNameValuePair("limit_bottom", params[0]));
        paramsed.add(new BasicNameValuePair("limit_top", params[1]));
        paramsed.add(new BasicNameValuePair("title", params[2]));


        arrayListRet = new ArrayList<>();

        try {
            //call http
            JSONObject json = jsonParser.makeHttpRequest("http://dev.projectlab.co.id/mit/1317016/view_mst_user.php",
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
                    map1.put("username", String.valueOf(c.getString("username")));
                    map1.put("name", String.valueOf(c.getString("name")));
                    arrayListRet.add(map1);
                }
            } else {
                //Unsuccessfully picking book. Another driver has taken the order.
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("success", String.valueOf(success));
                map.put("message", message);

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

