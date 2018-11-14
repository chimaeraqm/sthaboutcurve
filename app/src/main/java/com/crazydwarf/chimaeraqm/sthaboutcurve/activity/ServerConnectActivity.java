package com.crazydwarf.chimaeraqm.sthaboutcurve.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServerConnectActivity extends AppCompatActivity
{
    private static final String url = "https://wx.dwarfworkshop.com/qm/db_connect.php";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String request_name = "test2";
        String request_func = "SELECT_ALL";
        //String request_func = "INSERT";
        sendRequest(request_func,request_name);
    }

    void sendRequest(final String request_func, String request_name) {
        Map map = new HashMap();
        map.put("func_name", request_func);
        map.put("username", request_name);

        JSONObject jsonObject = new JSONObject(map);
        final String jsonString = jsonObject.toString();
        RequestBody requestBody = RequestBody.create(null,jsonString);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(ServerConnectActivity.this, "fail to connect to sever", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                try{
                    JSONObject jsonObject1 = new JSONObject(res);
                    String username = jsonObject1.getString("username");
                    String password = jsonObject1.getString("password");
                    String info = jsonObject1.getString("info");
                    Toast.makeText(ServerConnectActivity.this, username, Toast.LENGTH_SHORT).show();
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
