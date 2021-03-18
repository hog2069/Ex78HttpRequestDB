package com.hog2020.ex78httprequestdb;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class BoardActivity extends AppCompatActivity {

    ArrayList<Item> items = new ArrayList<>();
    RecyclerView recyclerView;
    BoardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        recyclerView=findViewById(R.id.recycler);
        adapter= new BoardAdapter(this,items);
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //리사이클러 뷰가 보여주는 데이터를 읽어오기 서버에서
        loadData();
    }

    //서버에서 데이터를 읽어오는 기능메소드
    void loadData(){

        //서버에서 DB 값을 echo 시켜주는 php 문서 실행
        new Thread(){
            @Override
            public void run() {
//                String serverUrl="http://hog2069.dothome.co.kr/Android/loadDB.php";
                String serverUrl="http://hog2069.dothome.co.kr/Android/loadDBtojson.php";

                try {
                    URL url = new URL(serverUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    connection.setUseCaches(false);

                    InputStream is = connection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader reader = new BufferedReader(isr);

                    final StringBuffer buffer = new StringBuffer();
                    String line =reader.readLine();
                    while(line!=null){
                        buffer.append(line+"\n");
                        line=reader.readLine();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(BoardActivity.this).setMessage(buffer.toString()).create().show();
                        }
                    });

                    //Android Java 에 이미 JSON 문자열을 분석해주는 클래스들이 존재함
                    //xmlParser 보다 훨씬 간단하게 코딩이 가능함 - 다음시간에 JSON 분석 수업 실시

                    //서버에서 echo 된 문자열데이터에서 '&' 문자를 기준으로 문자열들을 분리함
                    String[] rows=buffer.toString().split("&");//분리된 문자열들을 배열로

                    //기존 리사이크뷰의 항목들을 모두 삭제

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            items.clear();
                            adapter.notifyDataSetChanged();
                        }
                    });

                    for(String row : rows){
                        //한글 데이터만에서 각 컬름(칸) 값들을 분리
                        String[] datas=row.split("@");
                        if(datas.length!=4)continue;

                        int no=Integer.parseInt(datas[0]);
                        String name= datas[1];
                        String msg=datas[2];
                        String date=datas[3];


                        //리사이클러뷰 에 직접 값들을 넣는 것이 아니라
                        //리사이클러뷰 가 보여주는 값들을 가진 ArrayList 에
                        //항목을 추가하고 갱신
                        Item item= new Item(no,name,msg,date);
                        items.add(0,item);

                        //리사이클러뷰 갱신
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyItemInserted(0);
                            }
                        });
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}