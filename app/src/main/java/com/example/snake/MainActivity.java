package com.example.snake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private final List<snakepoints> snakePointsList  = new ArrayList<>();

    private SurfaceView surfaceView;
    private TextView scoreTV;


    private SurfaceHolder surfaceHolder;

    // snake moving position. Values must be right, left, top, bottom.
    // By default snake move to right
    private String movingPosition = "right";

    private int score = 0;

    private static final int pointSize = 28;
    private static final int defaultTalePoints = 3;
    private static final int snakeColor = Color.YELLOW;
    private static final int snakeMovingSpeed = 800;


    private int positionX, positionY;
    private Timer timer;

//    public MainActivity(int getPositionY) {
//        this.positionY = getPositionY;
//    }
    private Canvas canvas = null;
    private Paint pointColor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getting surfaceview and score TextView from xml file

        surfaceView = findViewById(R. id. surfaceView);
        scoreTV = findViewById(R.id.scoreTV);

        //  getting ImageButtons from xml file

        final AppCompatImageButton topBtn = findViewById(R.id.topBtn);
        final AppCompatImageButton leftBtn = findViewById(R.id.leftBtn);
        final AppCompatImageButton rightBtn = findViewById(R.id.rightBtn);
        final AppCompatImageButton bottomBtn = findViewById(R.id.bottomBtn);

        // adding callback to surfaceview

        surfaceView.getHolder().addCallback(this);

        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                // chek if previous moving position in not bottom.
                if(!movingPosition.equals("bottom")){
                    movingPosition = "top;";
                }

            }
        });

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!movingPosition.equals("right")){
                    movingPosition = "left";
                }

            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (movingPosition.equals("left")){
                    movingPosition = "right";

                }

            }
        });
        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(movingPosition.equals("top")){
                    movingPosition = "bottom";
                }

            }
        });
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

        this.surfaceHolder = surfaceHolder;

        init();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    private void init(){

        snakepoints.clear();

        scoreTV.setText("0");

        score = 0;

        movingPosition = "right";

        int startPositionX = (pointSize) * defaultTalePoints;

        for(int i = 0; i < defaultTalePoints; i++){

            snakepoints snakePoints = new snakepoints(startPositionX, pointSize);
            snakePointsList.add(snakePoints);

            startPositionX = startPositionX - (pointSize * 2 );


        }

        addPoints();

        moveSnake();
    }

    private  void addPoints() {
        int surfaceWidth = surfaceView.getWidth() - (pointSize * 2);
        int surfaceHeight = surfaceView.getHeight() - (pointSize * 2);

        int randomXPosition = new Random(0).nextInt(surfaceWidth / pointSize);
        int randomYPosition = new Random(0).nextInt(surfaceHeight / pointSize);

        if ((randomXPosition % 2) != 0) {
            randomXPosition = randomXPosition++;

            if ((randomYPosition % 2) != 0) {
                randomYPosition = randomYPosition++;
            }
            positionX = (pointSize * randomXPosition) + pointSize;
        }

    }

    private void moveSnake(){

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                int headPositionX = snakePointsList.get(0).getPositionX();
                int headPositionY = snakePointsList.get(0).getPositionY();

                if (headPositionX == positionX && positionY == headPositionY) {

                    growSnake();
                    addPoints();
                }
                switch (movingPosition) {
                    case "right":
                        snakePointsList.get(0).setPositionX(headPositionX + (pointSize * 2));
                        snakePointsList.get(0).setPositionY(headPositionY);
                        break;
                    case "left":
                        snakePointsList.get(0).setPositionX(headPositionX - (pointSize * 2));
                        snakePointsList.get(0).setPositionY(headPositionY);
                        break;

                    case "top":
                        snakePointsList.get(0).setPositionX(headPositionX);
                        snakePointsList.get(0).setPositionY(headPositionY - (pointSize * 2));
                        break;

                    case "bottom":
                        snakePointsList.get(0).setPositionX(headPositionX);
                        snakePointsList.get(0).setPositionY(headPositionY + (pointSize * 2));
                        break;
                }
                    if (chekGameOver(headPositionX, headPositionY)){
                        timer.purge();
                        timer.cancel();

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Your Score = "+score);
                        builder.setTitle("Game Over");
                        builder.setCancelable(false);
                        snakePointsList.get(0).setPositionX(0);
                        snakePointsList.get(0).setPositionY(0);
                        builder.setPositiveButton("Start Again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                init();
                            }
                        });
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                builder.show();
                            }
                        });

                    }
                    else{
                        canvas = surfaceHolder.lockCanvas();
                        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
                        canvas.drawCircle(snakePointsList.get(0).getPositionX(), snakePointsList.get(0).getPositionY(),pointSize, createPointColor());
                        canvas.drawCircle(positionX, positionY, pointSize,  createPointColor());
                        for(int i = 1; i < snakePointsList.size(); i++){
                            int getTempPositionX = snakePointsList.get(i).getPositionX();
                            int getTempPositionY = snakePointsList.get(i).getPositionY();

                            snakePointsList.get(i).setPositionX();
                            snakePointsList.get(i).setPositionY();
                            canvas.drawCircle(snakePointsList.get(i).getPositionX(),snakePointsList.get(i).getPositionY(), pointSize, createPointColor());

                            headPositionX = getTempPositionX;
                            headPositionY = getTempPositionY;

                        }
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }

        }, 1000 - snakeMovingSpeed, 1000 - snakeMovingSpeed);
    }
    private void growSnake(){
        snakepoints snakePoints = new snakepoints(0,0);
        snakePointsList.add(snakePoints);
        score++ ;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreTV.setText(String.valueOf(score));

            }
        });


    }
    private boolean chekGameOver(int headPositionX, int headPositionY ){
        boolean gameOver = false;

        if (snakePointsList.get(0).getPositionX()<0 ||
        snakePointsList.get(0).getPositionY()<0 ||
                    snakePointsList.get(0).getPositionX() >= surfaceView.getWidth() ||
                    snakePointsList.get(0).getPositionY() >= surfaceView.getHeight()){
            gameOver = true;
        }
        else{
            for(int i = 1; i < snakePointsList.size(); i++){
                if(headPositionX == snakePointsList.get(i).getPositionX() &&
                            headPositionY == snakePointsList.get(i).getPositionY()){
                    gameOver = true;
                    break;
    
                }
            }
        }
        return gameOver;

    }
    private Paint createPointColor(){

        if(pointColor == null){

            pointColor = new Paint();
            pointColor.setColor(snakeColor);
            pointColor.setStyle(Paint.Style.FILL);
            pointColor.setAntiAlias(true);

        }

        return pointColor;
    }
}