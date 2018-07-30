package com.example.aaronbuehne.swipepadrecorder;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Instructions extends AppCompatActivity {

    public static String pattern1 = "01258763";
    public static String pattern2 = "631574";
    Button practice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        practice = findViewById(R.id.practice_btn);
        if (!ParticipantInfo.firstTime) {
            ImageView pattern = findViewById(R.id.patternImage);
            pattern.setImageResource(R.drawable.pattern2);
        }
    }

    public void goSwipe(View view) {
        ParticipantInfo.practice = false;
        Intent intent = new Intent(this, SwipeScreen.class);
        startActivity(intent);
    }

    public void practice(View view) {
        ParticipantInfo.practice = true;
        Intent intent = new Intent(this, SwipeScreen.class);
        startActivity(intent);
    }
}
