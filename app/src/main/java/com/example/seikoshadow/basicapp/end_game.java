package com.example.seikoshadow.basicapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class end_game extends AppCompatActivity {
    int currentCash;
    int currentBank;
    int currentDebt;
    int finalScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        Intent endGame = getIntent();
        Bundle extras = endGame.getExtras();

        currentCash = extras.getInt("cash");
        currentBank = extras.getInt("bank");
        currentDebt = extras.getInt("debt");

        finalScore = currentCash + currentBank - (currentDebt * 2);

        TextView endGameDescTxt = (TextView) findViewById(R.id.endGameTxt);
        TextView scoreTxt = (TextView) findViewById(R.id.scoreValTxt);

        if (finalScore < 0) {
            endGameDescTxt.setText(getString(R.string.inDebt));
            scoreTxt.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        }

        scoreTxt.setText(String.valueOf(finalScore));

    }

    public void quitGame() {
        Intent quit = new Intent(this, Menu.class);
        startActivity(quit);
    }
}
