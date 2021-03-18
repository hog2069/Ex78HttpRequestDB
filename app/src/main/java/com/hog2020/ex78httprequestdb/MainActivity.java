package com.hog2020.ex78httprequestdb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText etName,etMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName=findViewById(R.id.et_name);
        etMsg=findViewById(R.id.et_message);
    }

    public void clickBtn(View view) {
        new Thread(){
            @Override
            public void run() {
                String name= etName.getText().toString();
                String msg= etMsg.getText().toString();

                String severUrl="http://hog2069.dothome.co.kr/Android/insertDB.php";

                try {
                    URL url = new URL(severUrl);

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);

                    //보낼 데이터
                    String data= "name="+name+"&msg="+msg;
                    OutputStream os = connection.getOutputStream();
                    OutputStreamWriter writer = new OutputStreamWriter(os);
                    writer.write(data,0,data.length());
                    writer.flush();
                    writer.close();

                    //insertDB.php 로 부터 DB 저장 결과를 echo 로받어 SnackBar 로받기
                    InputStream is = connection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader reader = new BufferedReader(isr);
                    final StringBuffer buffer = new StringBuffer();

                    while(true){
                        String line = reader.readLine();
                        if(line==null)break;

                        buffer.append(line+"\n");
                    }

                    //스낵바나 토스트도 화면에 보여지는 UI Thread 에서 작업
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Snackbar.make(etName,buffer.toString(),Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, buffer.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void clickload(View view) {
        //DB 데이터를 읽어서 보여주는 화면으로 이동
        Intent intent = new Intent(this,BoardActivity.class);
        startActivity(intent);

    }
}