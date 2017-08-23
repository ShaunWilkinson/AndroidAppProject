package com.example.seikoshadow.basicapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        // Create new button
        Button newBtn = (Button) findViewById(R.id.newBtn);
        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            // if button clicked
            public void onClick(View v) {
                // run function
                newGame();
            }
        });

        Button loadBtn = (Button) findViewById(R.id.loadBtn);
        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadGame();
            }
        });
    }

    // Go to main game screen
    private void newGame() {
        Intent intent = new Intent(this, GameView.class);

        startActivity(intent);
    }

    private void loadGame() {
        // Start new intent
        Intent load = new Intent(this, GameView.class);

        int loadGame = 1;

        // Add values to pass when moving
        Bundle extras = new Bundle();
        extras.putInt("loadGame", loadGame);

        load.putExtras(extras);

        // Start the main activity
        startActivity(load);
    }


}
