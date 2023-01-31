package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.fragments.HomeFragment;
import com.example.myapplication.R;

import android.media.MediaPlayer;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class WheelActivity extends AppCompatActivity {
private ImageView imageView;
private TextView textView;
private Button btn;
    private String Indian;
    private String Italian;
    private String American;
    private String Kebab;
    private String Sushi;
    private String Chinese;
    //final String [] sectors = {"Chinese", "Sushi", "Indian", "Italian", "American", "Kebab"};
    final String [] sectors = {"Indian", "Italian", "American", "Kebab", "Chinese", "Sushi"};


    final int[] sectorDegrees = new int[sectors.length];

    // rnd index
    int randomSectorIndex =0 ;
    boolean spinning = false;
    //gen random index
    Random  random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel);

        imageView = findViewById(R.id.imageViewWheel);
        btn = findViewById(R.id.buttonSpin);

        generateSectorDegrees();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!spinning){
                    spin();
                    spinning = true;
                    //gen degrees
                }
            }
        });




        ImageView backBtn = findViewById(R.id.imageViewBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



    }

    private void spin() {
        //get random index
      randomSectorIndex = random.nextInt(sectors.length);
        int randomDegree = generateRandomDegreeSpin();

        //spin animation

        RotateAnimation rotateAnimation = new RotateAnimation(0, randomDegree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5F, RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        //time for spin
        rotateAnimation.setDuration(3600); // 4 secs
        rotateAnimation.setFillAfter(true);

        //interpolator
        rotateAnimation.setInterpolator(new DecelerateInterpolator()); // high

        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                String randomChoice = sectors[sectors.length - (randomSectorIndex +1)];
                String choice = "Your random Cusine is "+ " " + randomChoice;
                toast(choice);
                MediaPlayer mediaplayer = MediaPlayer.create(WheelActivity.this, R.raw.winner);//You Can Put Your File Name Instead Of abc
                mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaPlayer.reset();
                        mediaPlayer.release();
                    }
                });
                mediaplayer.start();


                // spinning end
                spinning =false;

            }

            @Override
            public void onAnimationRepeat(Animation animation) {


            }
        });

        imageView.startAnimation(rotateAnimation);
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private int generateRandomDegreeSpin() {
     return (360*sectors.length) + sectorDegrees[randomSectorIndex];
    }

    private void generateSectorDegrees() {
        int sectorDegree = 360/sectors.length;
        for (int i =0; i<sectors.length; i++){
            sectorDegrees[i] = (i+1) *sectorDegree;

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.review_menu, menu);
        return true;
    }

}