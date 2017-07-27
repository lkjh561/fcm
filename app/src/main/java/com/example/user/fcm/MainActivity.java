package com.example.user.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final int NOTIFICATION_ID =1 ;
    TextView showtoken;
    Button loginbtn,registerbtn;
    EditText accoundtext,passwordtext;
   // login login;
    private SharedPreferences settings;
    private static final String data = "DATA";
    private static final String token = "TOKEN";
    private static final String MY_ACTION="org.hualang.broadcast.action.MY_ACTION";
    private int i = 1;




    Notification n;
    NotificationManager nm;
    Notification notification;
    PendingIntent pendingIntent;
    NotificationManager notificationManger;
    MyFirebaseMessagingService myFirebaseMessagingService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showtoken = (TextView)findViewById(R.id.textView3);
        registerbtn=(Button)findViewById(R.id.register);
        loginbtn=(Button)findViewById(R.id.login) ;
        accoundtext = (EditText) findViewById(R.id.accound);
        passwordtext=(EditText)findViewById(R.id.password);

        settings = getSharedPreferences("DATA",0);

        myFirebaseMessagingService = null;
        Intent it= new Intent();
        it.setClass(this, MyFirebaseMessagingService.class);
        startService(it);

        notificationManger = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
       Intent intent= new Intent();
        intent.setClass(this, MainActivity.class);

        pendingIntent = PendingIntent.getActivity(this,NOTIFICATION_ID,intent,0);


        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register(accoundtext.getText().toString(),passwordtext.getText().toString());
                Log.e(accoundtext.getText().toString(),passwordtext.getText().toString());
            }
        });
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             login(accoundtext.getText().toString(),passwordtext.getText().toString());
                Log.e(accoundtext.getText().toString(),passwordtext.getText().toString());
                notification = new Notification.Builder(MainActivity.this)
                        .setSmallIcon(android.R.drawable.sym_def_app_icon)
                        .setContentTitle(settings.getString("Title",""))
                        .setContentText(settings.getString("Message",""))
                        .setContentIntent(pendingIntent)
                        .build(); // available from API level 11 and onwards
                notificationManger.notify(0, notification);

                //Log.e("Message",MyFirebaseMessagingService.Message);

            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                }catch (Exception e){

                }finally{
                    if(settings.getString(token,"").equals("")){
                        saveData(MyInstanceIDService.refreshedToken);
                        Log.e("saveData",MyInstanceIDService.refreshedToken);
                    }else{
                        // readData();

                        Log.e("readData",settings.getString(token ,""));

                    }
                }
            }
        }).start();

        showtoken.setText(settings.getString(token, ""));
    }
    /*public void readData(){
           settings = getSharedPreferences(data,0);

           showtoken.setText(settings.getString(token, ""));

       }*/
    public void saveData(String setToken){
        settings.edit()
                .putString(token, setToken)
                .commit();


    }
    private void login(final String username, final String password){
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();

                String token1 = settings.getString(token, "");


                String postParameter = "username=" + username + "&password=" + password + "&registrationid=" + token1 +"&language=ch";
                Log.e("string=",postParameter);
                URL url = null;

                try {
                    url = new URL("http://120.105.161.201/fcm/login.php?" + postParameter);
                    HttpURLConnection urlConnection = (HttpURLConnection) url
                            .openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Accept",
                            "application/json");
                    urlConnection.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded");// setting your headers its a json in my case set your appropriate header

                    urlConnection.setDoOutput(true);
                    urlConnection.connect();// setting your connection

                    StringBuffer buffer = new StringBuffer();
                    InputStream is = urlConnection.getInputStream();
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(is));
                    String line = null;
                    while ((line = br.readLine()) != null)
                        buffer.append(line);
                    // reading your response

                    is.close();
                    urlConnection.disconnect();// close your connection

                    String res = buffer.toString();
                    Log.e("res",res);
                    JSONObject jObject  = new JSONObject(res);
                    //     JSONObject data = jObject.getJSONObject("data"); // get data object
                    String ret = jObject.getString("RetCode"); // get the name from data.

                    Log.e("ret",ret);
                    Log.e("res",res);

                    if(ret.equals("0")){
                        //登入成功
                        String token = jObject.getString("Token");
                        Log.e("token",token);
                    }

                    else if(ret.equals("1")){
                        //用戶不存在
                        String RetMsg = jObject.getString("RetMsg");
                        Log.e("RetMsg",RetMsg);
                    }
                    else if(ret.equals("2")){
                        //密碼不匹配
                        String RetMsg = jObject.getString("RetMsg");
                        Log.e("RetMsg",RetMsg);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };


        thread.start();

    }
    private void register(final String username, final String password){
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                String postParameter = "username=" + username + "&password=" + password;
                Log.e("string=",postParameter);
                URL url = null;

                try {
                    url = new URL("http://120.105.161.201/fcm/user_register.php?"+postParameter);
                    HttpURLConnection urlConnection = (HttpURLConnection) url
                            .openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Accept",
                            "application/json");
                    urlConnection.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded");// setting your headers its a json in my case set your appropriate header

                    urlConnection.setDoOutput(true);
                    urlConnection.connect();// setting your connection

                    StringBuffer buffer = new StringBuffer();
                    InputStream is = urlConnection.getInputStream();
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(is));
                    String line = null;
                    while ((line = br.readLine()) != null)
                        buffer.append(line);
                    // reading your response

                    is.close();
                    urlConnection.disconnect();// close your connection
                    String res = buffer.toString();
                    JSONObject jObject  = new JSONObject(res);
                    String ret = jObject.getString("RetCode");
                    Log.e("ret",ret);
                    Log.e("res:",res);

                    if(ret.equals("0")){
                        ///註冊成功
                    }


                    else if(ret.equals("1")){
                        ///註冊失敗
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        thread.start();
    }
  /*  private void removematch(final String mac){
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                String postParameter = "token=" + token +"&mac=" + mac;
                Log.e("string=",postParameter);
                URL url = null;

                try {
                    url = new URL("http://120.105.161.201/jpushex/removematch.php?" + postParameter);
                    HttpURLConnection urlConnection = (HttpURLConnection) url
                            .openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Accept",
                            "application/json");
                    urlConnection.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded");// setting your headers its a json in my case set your appropriate header

                    urlConnection.setDoOutput(true);
                    urlConnection.connect();// setting your connection

                    Log.e("response:",urlConnection.getResponseCode()+"");
                    StringBuffer buffer = new StringBuffer();
                    InputStream is = urlConnection.getInputStream();

                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(is));
                    String line = null;
                    while ((line = br.readLine()) != null)
                        buffer.append(line);
                    // reading your response

                    is.close();
                    urlConnection.disconnect();// close your connection
                    String res = buffer.toString();
                    String ret = res;
                    Log.e("buffer:",ret);
                    //          String address = buffer.toString().substring(2);

                    if(ret.equals("0")){
                        //配對成功
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = "解除成功:"/* + address*/;
                      /*  mHandler.sendMessage(msg);

                    }

                    else if(ret.equals("1")){
                        //token不正確
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = "token不正確";
                        mHandler.sendMessage(msg);
                    }
                    else if(ret.equals("2")){
                        //tag已和別的手機配對
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = "tag沒在資料庫中";
                        mHandler.sendMessage(msg);
                    }
                    else if(ret.equals("3")){
                        //tag已和別的手機配對
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = "原本就沒配對";
                        mHandler.sendMessage(msg);

                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };


        thread.start();

    }*/
    /* public String sendPost() {
        HttpPost httpRequest = new HttpPost(uriAPI);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("data", settings.getString(token, "")));
        try {
            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient()
                    .execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                Log.e("log_http","200");
                strResult = EntityUtils.toString(httpResponse.getEntity());
                get = true;
                return strResult;
            }else
                Log.e("log_http","error");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/

}

