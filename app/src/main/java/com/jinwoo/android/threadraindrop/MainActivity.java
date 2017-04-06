package com.jinwoo.android.threadraindrop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    FrameLayout layout;
    Stage stage;
    Button btnStart, btnPause, btnStop;

    int deviceWidth, deviceHeight;

    MakeRain rain;
    RedrawThread redrawthread;

    boolean redrawFlag = true;

    boolean pauseFlag = true;
    boolean stopFlag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics  matrix = getResources().getDisplayMetrics();
        deviceWidth = matrix.widthPixels;
        deviceHeight = matrix.heightPixels;


        layout = (FrameLayout)findViewById(R.id.layout);
        btnStart = (Button)findViewById(R.id.btnStart);
        btnPause = (Button)findViewById(R.id.btnPause);
        btnStop = (Button)findViewById(R.id.btnStop);

        btnStart.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        stage = new Stage(this);
        layout.addView(stage);

    }

    /*
            버튼 클릭
     */
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.btnStart:
                    pauseFlag = false;
                    redrawFlag = false;

                    redrawthread = new RedrawThread(stage);
                    redrawthread.start();
                    rain = new MakeRain(stage);
                    rain.start();
                break;
            case R.id.btnPause:
                pauseFlag = true;
                redrawFlag = true;

                break;
            case R.id.btnStop:

                stopFlag = true;
                pauseFlag = true;
                redrawFlag = true;

                stage.removeAll();
                rain.interrupt();
                redrawthread.interrupt();
                stage.postInvalidate();

                break;
        }
    }


    class RedrawThread extends Thread{
        Stage stage;
        public RedrawThread(Stage stage){
            this.stage = stage;
        }
        @Override
        public void run(){
            while(!redrawFlag){
                Log.i("FIFA", "========================================RedrawThread");
                stage.postInvalidate();
                try {
                    Thread.sleep(50);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    class MakeRain extends Thread{

        Stage stage;

        public MakeRain(Stage stage){
            this.stage = stage;
        }
        @Override
        public void run() {
            while(!pauseFlag){
                Log.i("FIFA", "======================================== MakeRain");
                stopFlag = false;
                pauseFlag = false;
                new Raindrop(stage);
                try {
                    Thread.sleep(50); //0.05초당 하나 빗방울 생성
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Raindrop extends Thread{
        int x;
        int y;
        int radius;
        int speed;
        Stage stage;

        public Raindrop(Stage stage){
            Random random = new Random();
            x = random.nextInt(deviceWidth); // x좌표 : 0부터 1023
            y = 0;
            radius = random.nextInt(30)+5; // 크기 : 5부터 34
            speed = random.nextInt(10)+1;

            this.stage = stage;
            stage.addRaindrop(this);
        }

        @Override
        public void run(){
            while(!stopFlag && y <= deviceHeight){
                if(!pauseFlag) {
                    Log.i("FIFA", "========================================RainDrop");
                    y = y + speed;
                    try {
                        Thread.sleep(10); // 0.05초당 y축 위치 변경
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
            if(y > deviceHeight ){
                stage.removeRaindrop(this);
            }

        }
   }


    class Stage extends View{

        Paint rainColor;
        List<Raindrop> raindrops;

        public Stage(Context context){
            super(context);
            raindrops = new CopyOnWriteArrayList<>();
            rainColor = new Paint();
            rainColor.setColor(Color.BLUE);

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            //Log.i("Rain Size","==================================" + raindrops.size());
            for(Raindrop raindrop : raindrops){
                canvas.drawCircle(raindrop.x, raindrop.y, raindrop.radius, rainColor);
            }
        }

        public void addRaindrop(Raindrop raindrop){
            raindrops.add(raindrop);
            raindrop.start();

        }

        public void removeRaindrop(Raindrop raindrop){
            raindrops.remove(raindrop);
            raindrop.interrupt();
        }

        public void removeAll(){
            for(Raindrop rainItem : raindrops){
                raindrops.remove(rainItem);
                rainItem.interrupt();
            }
        }

    }

    @Override
    protected void onDestroy() {

        redrawFlag = true;
        stopFlag = true;
        pauseFlag = true;

        stage.removeAll();
        rain.interrupt();
        redrawthread.interrupt();
        stage.postInvalidate();

        super.onDestroy();
    }

}
