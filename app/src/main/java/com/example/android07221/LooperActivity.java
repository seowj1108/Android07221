package com.example.android07221;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.Random;

public class LooperActivity extends AppCompatActivity {
    //뷰 객체
    private ListView list_odd, list_even;
    //ListView는 MVC 패턴을 구현하기 위해서
    //List 와 ListAdapter를 이용해서 출력

    //ListView에 출력할 데이터 변수
    ArrayList<String> oddDatas, evenDatas;
    //List 와 ListView를 연결해 줄 컨트롤러 변수
    ArrayAdapter<String> oddAdapter, evenAdapter;

    //핸들러 변수
    Handler handler;

    //화면을 갱신하는 스레드
    class OneThread extends Thread{
        //핸들러를 인스턴스 변수로 만든 이유는 다른 스레드에서
        //이 핸들러에게 메시지를 전달하기 위해서
        Handler oneHandler;
        public void run(){
            Looper.prepare();
            oneHandler = new Handler(
                    Looper.getMainLooper()){
                public void handleMessage(Message msg){
                    //안드로이드에서 예외처리 없이 대기
                    SystemClock.sleep(1000);
                    //anonymous class에서 사용하기 위해서
                    //final 변수로 변환
                    final int data = msg.arg1;
                    if(msg.what == 0){
                        handler.post(new Runnable(){
                            public void run(){
                                evenDatas.add("even:" + data);
                                //리스트 뷰를 재출력
                                evenAdapter.notifyDataSetChanged();
                            }
                        });
                    }else{
                        handler.post(new Runnable(){
                            public void run(){
                                oddDatas.add("odd:" + data);
                                //리스트 뷰를 재출력
                                oddAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            };
            Looper.loop();
        }
    }

    //위의 스레드에 대한 변수 생성
    OneThread oneThread;

    //데이터를 생성해주는 스레드
    //0.1초마다 랜덤한 정수를 생성해서 OneThread의 oneHandler에게
    //메시지를 전송하는 스레드
    class TwoThread extends Thread{

        public void run(){
            Random random = new Random();
            for(int i=0; i<10; i=i+1){
                int data = random.nextInt();
                SystemClock.sleep(100);

                //UI갱신을 위해서 핸들러에게 메시지를 전성
                Message message = new Message();
                if(data % 2 == 0){
                    //what은 구분하기 위해서 주로 사용
                    message.what = 0;
                }else{
                    message.what = 1;
                }
                //arg는 주로 데이터를 전달할 목적으로 사용
                message.arg1 = data;
                message.arg2 = i;
                //핸들러에게 메시지를 전송
                oneThread.oneHandler.sendMessage(message);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_looper);

        //뷰를 찾아오기
        list_odd = (ListView)findViewById(R.id.list_odd);
        list_even = (ListView)findViewById(R.id.list_even);

        //뷰에 연결할 데이터를 생성 - Model
        oddDatas = new ArrayList<>();
        evenDatas = new ArrayList<>();

        //뷰와 모델 연결
        oddAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                oddDatas);
        evenAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                evenDatas);
        list_odd.setAdapter(oddAdapter);
        list_even.setAdapter(evenAdapter);

        //핸들러와 스레드 객체를 생성하고 시작
        handler = new Handler();
        oneThread = new OneThread();
        TwoThread twoThread = new TwoThread();
        oneThread.start();
        twoThread.start();
    }
}