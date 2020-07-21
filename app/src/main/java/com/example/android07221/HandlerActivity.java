package com.example.android07221;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HandlerActivity extends AppCompatActivity {
    private TextView txtdisplay;
    private Button btnstart;

    //대화상자
    ProgressDialog progessDialog;
    //대화상자가 표시 중 인지 여부를 저장할 변수
    boolean isQuit;
    //대화상자의 값으로 사용할 변수
    int value;

    //핸들러 객체 생성
    Handler handler = new Handler(
            Looper.getMainLooper()){
        //핸들러에게 메시지를 보내면 이 메소드가 호출되서
        //메인 스레드에게 작업을 처리해달고 요청합니다.
        //이 메소드에서 데이터 출력을 합니다.
        /*
        @Override
        public void handleMessage(Message msg){
            //전송된 데이터를 가져오기
            int data = msg.what;
            //텍스트 뷰에 출력
            txtdisplay.setText(data + "");
        }
        */


    };


    Handler progressHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            value++;
            txtdisplay.setText(value + "");
            try{
                Thread.sleep(100);
            }catch(Exception e){}
            if(value < 100 && isQuit == false){
                progessDialog.setProgress(value);
                progressHandler.sendEmptyMessage(0);
            }else{
                progessDialog.dismiss();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler);

        txtdisplay = (TextView)findViewById(R.id.txtdisplay);
        btnstart = (Button)findViewById(R.id.btnstart);

        //버튼의 클릭 이벤트 처리
        btnstart.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        value=0;
                        //대화상자 만들기
                        progessDialog = new ProgressDialog(
                                HandlerActivity.this);
                        progessDialog.setProgressStyle(
                                ProgressDialog.STYLE_HORIZONTAL
                        );
                        progessDialog.show();
                        isQuit = false;
                        progressHandler.sendEmptyMessage(0);
                    }
                });


        //스레드 생성
        Thread th = new Thread(){
            //스레드로 수행할 메소드
            int val = 0;
            public void run(){
                for(int i=0; i<20; i=i+1){
                    try{
                        //별도의 스레드에서 직접 출력하는 것은 안됨
                        //txtdisplay.setText(i + "");

                        //메시지 객체 생성
                        Message message = new Message();
                        //메시지에 데이터를 저장
                        message.what = i;
                        //핸들러에게 메시지 전송
                        //handler.sendMessage(message);

                        //다른 작업이 없을 때 처리하도록 전송
                        long curtime = System.currentTimeMillis();
                        handler.postAtTime(new Runnable() {
                            @Override
                            public void run() {
                                txtdisplay.setText(val++ +"");
                            }
                        }, curtime + 30000);

                        Thread.sleep(1000);
                    }catch(Exception e){}
                }
            }
        };
        th.start();

    }
}