package com.apps.seikoshadow.ganglanddealer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


public class end_game extends AppCompatActivity {
    int currentCash;
    int currentBank;
    int currentDebt;
    int finalScore;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_game);

        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.fullScreenUnitId));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitialAd.show();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();

                Button submitBtn = (Button) findViewById(R.id.submitScore);
                Button quitBtn = (Button) findViewById(R.id.quitBtn);

                submitBtn.setVisibility(View.VISIBLE);
                quitBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                Button submitBtn = (Button) findViewById(R.id.submitScore);
                Button quitBtn = (Button) findViewById(R.id.quitBtn);

                submitBtn.setVisibility(View.VISIBLE);
                quitBtn.setVisibility(View.VISIBLE);
            }
        });
        mInterstitialAd.loadAd(adRequest);

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
        } else if (finalScore < 10000) {
            endGameDescTxt.setText(getString(R.string.subTenK));
        } else if (finalScore < 50000) {
            endGameDescTxt.setText(getString(R.string.subFiftyK));
        } else if (finalScore < 100000) {
            endGameDescTxt.setText(getString(R.string.subHundredK));
        } else if (finalScore < 500000) {
            endGameDescTxt.setText(getString(R.string.subFiveHundredK));
        } else if (finalScore > 500000) {
            endGameDescTxt.setText(getString(R.string.endGameLast));
        }

        scoreTxt.setText(String.valueOf(finalScore));
    }

    public void quitGame(View view) {

        Intent quit = new Intent(this, Menu.class);
        startActivity(quit);
    }

    public void submitScore(View view) {

        Intent submitScore = new Intent(this, Menu.class);

        Bundle extras = new Bundle();
        extras.putInt("score", finalScore);

        submitScore.putExtras(extras);
        submitScore.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(submitScore);
        finish();
    }

}
