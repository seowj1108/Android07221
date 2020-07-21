package com.example.android07221;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android07221.R;

public class AsyncActivity extends AppCompatActivity {
    private TextView state;
    private ProgressBar progress;
    private Button threadstart, threadcancel;

    //프로그래스의 값을 저장할 변수
    int value ;

    //비동기적으로 실행하는 클래스를 생성
    //첫번째 제너릭은 백그라운드 작업을 위한 doInBackground()의 매개변수 자료형
    //두번째 제너릭은 doInBackground에서 중간 중간에 호출하는
    //publishProgress 함수의 매개변수 자료형
    //세번째 제너릭은 doInBackground 의 리턴 타입으로 onPostExecute()
    //메소드의 매개변수 자료형
    class BackgroundTask
            extends AsyncTask<Integer, Integer, Integer> {
        //맨 처음 한 번만 호출되는 메소드
        @Override
        public void onPreExecute(){
            //초기화 작업 수행
            value = 0;
            progress.setProgress(value);
        }

        //백그라운드에서 작업하는 메소드
        @Override
        //execute 메소드를 호출할 때 대입하는 값이 values에 대입
        public Integer doInBackground(Integer ... values){
            //아직 종료되지 않았다면
            while(isCancelled() == false){
                //value의 값을 1씩 증가 - 100이 되면 중지
                //100이 안되면 publishProgress를
                //호출해서 UI를 갱신합니다.
                value = value + 1;
                if(value >= 100){
                    break;
                }else{
                    //이 메소드를 호출하면
                    //onProgressUpdate가 호출됩니다.
                    publishProgress(value);
                }
                try{
                    Thread.sleep(1000);
                }catch(Exception e){}
            }
            return value;
        }

        @Override
        //doInBackground에서 publishProgress를 호출하면
        //호출되는 메소드 - 주기적인 UI 갱신
        public void onProgressUpdate(Integer ... values){
            //프로그래스 바 설정
            progress.setProgress(values[0]);
            state.setText("값:" + values[0]);
        }

        @Override
        //doInBackground 수행이 정상 종료되었을 때 호출되는 메소드
        //매개변수는 doInBackground의 리턴 값
        public void onPostExecute(Integer result){
            progress.setProgress(0);
            state.setText("스레드 정상 종료");
        }

        @Override
        //정상 종료가 되지 않고 강제 종료 되었을 때 호출되는 메소드
        public void onCancelled(){
            progress.setProgress(0);
            state.setText("스레드 강제 종료");
        }
    }

    //AsyncTask 변수
    private BackgroundTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async);

        state = (TextView)findViewById(R.id.state);
        progress = (ProgressBar)findViewById(R.id.progress);
        threadstart = (Button)findViewById(R.id.threadstart);
        threadstart.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view){
                //AsyncTask 객체를 생성하고 시작
                //BackgroundTask 의 onPreExecute() -->
                //doInBackground(100)으로 호출
                task = new BackgroundTask();
                task.execute(100);
            }
        });
        threadcancel = (Button)findViewById(R.id.threadcancel);
        threadcancel.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view){
                //AsyncTask 중지
                task.cancel(true);
            }
        });
    }
}