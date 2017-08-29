package com.example.seikoshadow.basicapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

// TODO change all calls to update text health using setHealth()

// TODO complete descriptions of drugs in strings file - Done
// todo Make pay debt button change depending on city - Done
// TODO ability to pay debt - Done
// todo ability to move to certain city - passing currentCityNum value, need method to change this - Done
// todo set up day count - Done
// todo set up end game - Done
// todo display buy value of drugs when selling - done
// todo display drugs if holding but don't allow sell and remove value - done
// todo after click buy in buy/sell then click out it will keep last value - clear value of alreadyBoughtOrSold
// todo make same drugs display after load - done
// todo events
// todo weapons and armour - Done
//  todo got count of visible drugs, still need to be able to pick from just visible though as there's a bug where it can pick drugs which arent visible - Done
// todo code up hospital properly
// todo set all dialogs to use customDialog style

public class GameView extends AppCompatActivity {
    public static final String SAVE_FILE = "saveFile";
    // Weapons
    public int currentWeapon = 1; // hands = 1, pistol = 2, rifle = 3, smg = 4, assRifle = 5
    public boolean[] enemyAliveOrDead = {true, true, true}; // true = alive
    int currentCashVal = 5000;
    int currentDebtVal = 5000;
    int currentBankVal = 0;
    int currentDayVal = 1;
    int maxHealth = 100;
    int currentHealthVal = 100;
    int totalDaysVal = 30;
    int interestRate = 5;
    int maxDrugs = 100;
    int firstRun = 1;
    int runChance = 50; // percentage chance to run
    int eventChance = 70; // percentage chance of event 40
    int hospitalCost = 100; // Cost per point of health

    int selectedRow = 0;
    int maxDrugsPurchasableNum = 0;
    int inputDrugQuantity = 0;
    int inputLoanQuantity = 0;
    int costOfDrugs = 0;
    int totalItemsHeld = 0;
    int totalCurrentDrugHeldVal = 0;
    CharSequence confirmButtonTxt = "";
    CharSequence buyOrSellVal = "";
    int currentDrugVal = 0;
    int debtInterest = 0;
    int currentCityNum = 0;
    String[] cities;
    int[] drugQuantities;
    int[] drugValues;
    int[] drugVisible;
    String depositOrWithdraw = "";
    // All lists containing average purchase value of each drug
    List<Integer> drugPurchases1 = new ArrayList<>();
    List<Integer> drugPurchases2 = new ArrayList<>();
    List<Integer> drugPurchases3 = new ArrayList<>();
    List<Integer> drugPurchases4 = new ArrayList<>();
    List<Integer> drugPurchases5 = new ArrayList<>();
    List<Integer> drugPurchases6 = new ArrayList<>();
    List<Integer> drugPurchases7 = new ArrayList<>();
    List<Integer> drugPurchases8 = new ArrayList<>();
    List<Integer> drugPurchases9 = new ArrayList<>();
    List<Integer> drugPurchases10 = new ArrayList<>();
    List<Integer> drugPurchases11 = new ArrayList<>();
    int[] weaponDmg = {30, 50, 60, 70, 80};
    int[] weaponAccuracy = {60, 70, 80, 90, 100};
    int[] weaponCost = {0, 1000, 5000, 10000, 20000};
    int selectedWpnVal = 0;
    // Enemies
    String[] enemyNames = {"Policeman", "Rival Drug Dealer", "Armed Officer"};
    int[] enemiesHealth = {100, 150, 300};
    int[] enemiesAttack = {20, 30, 40};
    int[] enemiesAccuracy = {60, 70, 80};
    int selectedEnemyId = 0;
    String[] bodyParts;

    int drugAverage1 = 0;
    int drugAverage2 = 0;
    int drugAverage3 = 0;
    int drugAverage4 = 0;
    int drugAverage5 = 0;
    int drugAverage6 = 0;
    int drugAverage7 = 0;
    int drugAverage8 = 0;
    int drugAverage9 = 0;
    int drugAverage10 = 0;
    int drugAverage11 = 0;

    boolean alreadyBoughtOrSold = false;

    Random rn = new Random();


    int obfuscate = -1; // just used to make sure values rarely land on memorable number
    // Current drug costs
    //  Name             Range   base
    int drug1Val =       rn.nextInt(660) + 1100 + obfuscate;     // Acid
    int drug2Val =       rn.nextInt(12000) + 20000 + obfuscate;     // Cocaine
    int drug3Val =       rn.nextInt(550) + 800 + obfuscate;     // hash
    int drug4Val =       rn.nextInt(5500) + 8000 + obfuscate;     // heroin
    int drug5Val =       rn.nextInt(1020) + 1400 + obfuscate;     // ludes
    int drug6Val =       rn.nextInt(150) + 200 + obfuscate;      // opium
    int drug7Val =       rn.nextInt(1300) + 2000 + obfuscate;   // pcp
    int drug8Val =       rn.nextInt(50) + 50 + obfuscate;        // Poppers
    int drug9Val =       rn.nextInt(2000) + 2500 + obfuscate;      // shrooms
    int drug10Val =      rn.nextInt(3400) + 4500 + obfuscate;   // speed
    int drug11Val =      rn.nextInt(350) + 456 + obfuscate;    // weed

    // ======== ON CREATE ===========
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_view);

        handleLoadOrMove();

        // If end game is reached
        if (currentDayVal >= totalDaysVal) {
            endGame();
        }

        setupAllVariables();
        varyDrugsVisible();

        // stop events if it's the first day
        if (currentDayVal != 1) {
            handleEvents();
        }
    }
    // ======= END ==================

    private void handleEvents() {
        int whichEventToRun = randomInt(1, 100); // randomInt(1, 10) - FIX

        // ======================================
        // ======= EVENTS =======================
        // =====================================

            /* 1 - 10     Mugged - Lose some cash
               11 - 90 -  Boom or Bust - Drug prices go up or down
               91 - 100 -  Battle

               Stopped by Cops - Attack or run
               OD - Health loss
               drugs worth more or less - 50% chance
               Mugged - lose drugs

            ======================================== */

        if (randomInt(1, 100) <= eventChance) {

            // ======== setup the alert ============
            final AlertDialog eventDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.event))
                    .create();

            View dialogView = View.inflate(this, R.layout.events, null);
            eventDialog.setView(dialogView);
            eventDialog.setCancelable(false);
            eventDialog.setCanceledOnTouchOutside(false);
            eventDialog.show();
            setDividerStyle(eventDialog);

            // Initialise dialog contents
            TextView currentCash = (TextView) findViewById(R.id.currentCashView);
            final TextView currentHealth = (TextView) findViewById(R.id.currentHealthValue);
            final TextView dialogDescription = (TextView) dialogView.findViewById(R.id.descOne);
            TextView dialogDescriptionTwo = (TextView) dialogView.findViewById(R.id.descTwo);
            final Button firstBtn = (Button) dialogView.findViewById(R.id.button);
            final Button secondBtn = (Button) dialogView.findViewById(R.id.button2);

            dialogDescriptionTwo.setVisibility(View.GONE);

            // ====== EVent Handler ================
            if (whichEventToRun >= 1 && whichEventToRun <= 5) {
                // ========== mug event =============
                eventDialog.setTitle(getString(R.string.mugged));

                int percentageLost = randomInt(0, 40);
                int stolenAmountNum = (currentCashVal / 100) * percentageLost;
                String stolenAmount = String.valueOf(stolenAmountNum);

                String action;
                int knockOutOrStabbed = randomInt(0, 1);

                if (knockOutOrStabbed == 1) {
                    action = getString(R.string.knockOut);
                    currentHealthVal = currentHealthVal - randomInt(1, 15);
                } else {
                    action = getString(R.string.stabbing);
                    currentHealthVal = currentHealthVal - randomInt(15, 30);
                }

                currentHealth.setText(String.valueOf(currentHealthVal));

                dialogDescription.setText(getString(R.string.mugDesc, stolenAmount, action));
                currentCashVal = currentCashVal - stolenAmountNum;
                currentCash.setText(String.valueOf(currentCashVal));

                firstBtn.setVisibility(View.GONE);
                secondBtn.setText(getString(R.string.cancel));

                secondBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        eventDialog.dismiss();
                    }
                });
                // ========== end mug ===============
            } else if (whichEventToRun >= 6 && whichEventToRun <= 85) {
                int boomOrBust = randomInt(0, 1);
                // ========== Drugs worth more ===========

                TableLayout drugTable = (TableLayout) findViewById(R.id.drugTable);
                TableRow drugRow;
                TextView drugName;
                TextView drugValueTxt;

                int drugCount = getCountOfVisibleDrugs();

                int x = 0;
                int whichDrug = 0;

                // while a drugs no chosen loop
                while (x == 0) {
                    // choose a random drug
                    whichDrug = randomInt(1, drugCount);
                    drugRow = (TableRow) drugTable.getChildAt(whichDrug);
                    drugValueTxt = (TextView) drugRow.getChildAt(2);

                    // if the drug is visible and the price isn't '---'
                    if (drugTable.getChildAt(whichDrug).getVisibility() == View.VISIBLE && !drugValueTxt.getText().equals("---")) {
                        // exit the loop
                        x = 1;
                    }
                }

                drugRow = (TableRow) drugTable.getChildAt(whichDrug);
                drugName = (TextView) drugRow.getChildAt(1);
                drugValueTxt = (TextView) drugRow.getChildAt(2);

                int priceChangePercentage = randomInt(30, 50);

                // if drug value doesn't equal ---
                if (!drugValueTxt.getText().toString().equals("---")) {
                    currentDrugVal = Integer.parseInt(drugValueTxt.getText().toString());
                }

                int valueToAdd = (currentDrugVal / 100) * priceChangePercentage;

                int newDrugValue;

                if (boomOrBust == 0) {
                    eventDialog.setTitle(getString(R.string.drugBoom));
                    newDrugValue = currentDrugVal + valueToAdd;
                } else {
                    eventDialog.setTitle(getString(R.string.drugBust));
                    newDrugValue = currentDrugVal - valueToAdd;
                }

                drugValueTxt.setText(String.valueOf(newDrugValue));

                if (boomOrBust == 0) {
                    drugName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGreen));
                    drugValueTxt.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGreen));
                    dialogDescription.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGreen));

                    drugName.setTypeface(null, Typeface.BOLD);
                    drugValueTxt.setTypeface(null, Typeface.BOLD);

                    dialogDescription.setText(getString(R.string.drugBoomDesc, drugName.getText().toString()));
                } else {
                    drugName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
                    drugValueTxt.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
                    dialogDescription.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));

                    drugName.setTypeface(null, Typeface.BOLD);
                    drugValueTxt.setTypeface(null, Typeface.BOLD);

                    dialogDescription.setText(getString(R.string.drugBustDesc, drugName.getText().toString(), drugName.getText().toString()));
                }

                firstBtn.setVisibility(View.GONE);
                secondBtn.setText(getString(R.string.cancel));

                secondBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        eventDialog.dismiss();
                    }
                });

            // Fight
            } else if (whichEventToRun >= 86 && whichEventToRun <= 100) {
                // Select easiest enemy who's alive
                if (enemyAliveOrDead[0]) {
                    selectedEnemyId = 0;
                } else if (enemyAliveOrDead[1]) {
                    selectedEnemyId = 1;
                } else if (enemyAliveOrDead[2]) {
                    selectedEnemyId = 2;
                } else {
                    return;
                }

                eventDialog.setTitle(getString(R.string.attackTitle));
                dialogDescription.setText(getString(R.string.attackStart, enemyNames[selectedEnemyId]));
                firstBtn.setText(getString(R.string.fight));
                secondBtn.setText(getString(R.string.run));

                firstBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        eventDialog.dismiss();
                        battleHandler(true);
                    }
                });

                secondBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // if run away success
                        if (runHandler(eventDialog) == 1) {
                            return;
                        } else {
                            eventDialog.dismiss();
                            battleHandler(true);
                    }
                    }
                });
            }
        }
    }

    public void battleHandler(boolean triedToRun) {
        //TODO implement run mechanic

        // Create the dialog
        final AlertDialog eventDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.attackTitle))
                .create();

        final View dialogView = View.inflate(this, R.layout.battle, null);
        eventDialog.setView(dialogView);
        eventDialog.show();
        eventDialog.setCanceledOnTouchOutside(false);
        eventDialog.setCancelable(false);
        setDividerStyle(eventDialog);

        // If player tries to run they miss an attack
        if (!triedToRun) {
            if(runHandler(eventDialog) == 1) {
                return;
            } else {
                playerAttack();
            }
        }

        // Initialise dialog contents
        TextView enemyHealthTxt = (TextView) dialogView.findViewById(R.id.currentEnemyHealthTxt);
        final TextView currentHealth = (TextView) dialogView.findViewById(R.id.currentHealthTxt);
        TextView enemyDialogDescription = (TextView) dialogView.findViewById(R.id.attackEnemyDescription);
        Button firstBtn = (Button) dialogView.findViewById(R.id.button);
        Button secondBtn = (Button) dialogView.findViewById(R.id.button2);

        // Set values
        firstBtn.setText(getString(R.string.fight));
        secondBtn.setText(getString(R.string.run));

        boolean playerDamaged = enemyAttack();

        setHealth();

        // Set values
        Log.d("myapp", String.valueOf(currentHealthVal));
        currentHealth.setText(String.valueOf(currentHealthVal));
        enemyHealthTxt.setText(String.valueOf(enemiesHealth[selectedEnemyId]));

        // Handle player or enemy death
        if(currentHealthVal == 0 && enemiesHealth[selectedEnemyId] == 0) {
            currentHealthVal = 0;
            enemiesHealth[selectedEnemyId] = 0;

            currentHealth.setText(String.valueOf(currentHealthVal));
            enemyHealthTxt.setText(String.valueOf(enemiesHealth[selectedEnemyId]));

            setBattleDescription(playerDamaged, enemyDialogDescription, 3);
            firstBtn.setVisibility(View.GONE);
            secondBtn.setText(getString(R.string.ok));

            secondBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    eventDialog.dismiss();
                    currentHealthVal = 100;
                    currentHealth.setText(String.valueOf(currentHealthVal));
                }
            });

        } else if (currentHealthVal <= 0) {
            currentHealthVal = 0;
            currentHealth.setText(String.valueOf(currentHealthVal));

            setBattleDescription(playerDamaged, enemyDialogDescription, 1);
            firstBtn.setVisibility(View.GONE);
            secondBtn.setText(getString(R.string.ok));

            setHealth();

            secondBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    eventDialog.dismiss();
                    currentHealthVal = 100;
                    currentHealth.setText(String.valueOf(currentHealthVal));

                    setHealth();
                }
            });

        } else if(enemiesHealth[selectedEnemyId] <= 0) {
            enemiesHealth[selectedEnemyId] = 0;

            enemyHealthTxt.setText(String.valueOf(enemiesHealth[selectedEnemyId]));

            setBattleDescription(playerDamaged, enemyDialogDescription, 2);
            firstBtn.setVisibility(View.GONE);
            secondBtn.setText(getString(R.string.ok));

            secondBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    eventDialog.dismiss();
                }
            });

        } else {
            setBattleDescription(playerDamaged, enemyDialogDescription, 0);
            firstBtn.setText(getString(R.string.fight));
            secondBtn.setText(getString(R.string.run));

            firstBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    eventDialog.dismiss();
                    battleHandler(true);
                }
            });
            secondBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    eventDialog.dismiss();
                    if (runHandler(eventDialog) == 1) {
                        return;
                    } else {
                        battleHandler(false);
                    }
                }
            });
        }
    }

    public int runHandler(Dialog eventDialog) {
        eventDialog.dismiss();

        setHealth();

        if (randomInt(1, 100) < runChance) {
            // succesfully ran away

            String[] runAwaySuccessMessages = getResources().getStringArray(R.array.runAwaySuccess);
            String chosenMsg = runAwaySuccessMessages[randomInt(0, runAwaySuccessMessages.length - 1)];

            Toast.makeText(getApplicationContext(), chosenMsg, Toast.LENGTH_LONG).show();
            return 1;
        } else {
            String[] runAwaySuccessMessages = getResources().getStringArray(R.array.runAwayFail);
            String chosenMsg = runAwaySuccessMessages[randomInt(0, runAwaySuccessMessages.length - 1)];

            Toast.makeText(getApplicationContext(), chosenMsg, Toast.LENGTH_LONG).show();
            return 0;
        }
    }

    public boolean enemyAttack() {
        boolean playerDamaged = false;

        if(randomInt(1, 100) < enemiesAccuracy[selectedEnemyId]) {
            currentHealthVal -= enemiesAttack[selectedEnemyId];
            playerDamaged = true;
        }

        return playerDamaged;
    }

    public boolean playerAttack() {
        boolean enemyDamaged = false;
        if(randomInt(1, 100) < weaponAccuracy[selectedWpnVal]) {
            enemiesHealth[selectedEnemyId] -= weaponDmg[selectedWpnVal];
            enemyDamaged = true;
        }

        return enemyDamaged;
    }

    public void setBattleDescription(boolean playerDamaged, TextView dialogDescription, int playerOrEnemyDead) {
        // playerOrEnemyDead
        // 0 Nobody dead
        // 1 Player Dead
        // 2 Enemy Dead
        // 3 Both Dead

        // Nobody dead
        if (playerOrEnemyDead == 0) {
            switch (enemyNames[selectedEnemyId]) {
                case "Policeman": {
                    if (playerDamaged) {
                        dialogDescription.setText(getString(R.string.policeDescription, String.valueOf(enemiesAttack[selectedEnemyId])));
                    } else {
                        dialogDescription.setText(getString(R.string.policeDescriptionMiss));//here
                    }
                    break;
                }
                case "Rival Drug Dealer": {
                    if (playerDamaged) {
                        dialogDescription.setText(getString(R.string.drugDealerDescription));
                    } else {
                        dialogDescription.setText(getString(R.string.drugDealerDescriptionMiss));
                    }
                    break;
                }
                case "Armed Officer": {

                    String bodyPart = bodyParts[randomInt(0, bodyParts.length - 1)];

                    if (playerDamaged) {
                        dialogDescription.setText(getString(R.string.officerDescription, bodyPart, String.valueOf(enemiesAttack[selectedEnemyId])));
                    } else {
                        dialogDescription.setText(getString(R.string.officerDescriptionMiss, bodyPart));
                    }
                    break;
                }
                case "default": {
                    dialogDescription.setText(getString(R.string.placeholder));
                    break;
                }
            }
            // Player Dead
        } else if (playerOrEnemyDead == 1) {

            String bodyPart = bodyParts[randomInt(0, bodyParts.length - 1)];

            switch (enemyNames[selectedEnemyId]) {
                case "Policeman": {
                    handleDeath(dialogDescription);
                    break;
                }
                case "Rival Drug Dealer": {
                    dialogDescription.setText(getString(R.string.drugDealerKilledPlayer));
                    handleDeath(dialogDescription);
                    break;
                }
                case "Armed Officer": {
                    dialogDescription.setText(getString(R.string.officerKilledPlayer, bodyPart));
                    handleDeath(dialogDescription);
                    break;
                }
                case "default": {
                    dialogDescription.setText(getString(R.string.placeholder));
                    handleDeath(dialogDescription);
                    break;
                }
            }
            // enemy dead
        } else if (playerOrEnemyDead == 2){
            switch (enemyNames[selectedEnemyId]) {
                case "Policeman": {
                    handleWin(1, dialogDescription);
                    break;
                }
                case "Rival Drug Dealer": {
                    handleWin(2, dialogDescription);
                    break;
                }
                case "Armed Officer": {
                    handleWin(3, dialogDescription);
                    break;
                }
                case "default": {
                    handleWin(999, dialogDescription);
                    break;
                }
            }
            // Everyone Dead
        } else if (playerOrEnemyDead == 3) {

            String bodyPart = bodyParts[randomInt(0, bodyParts.length - 1)];

            switch (enemyNames[selectedEnemyId]) {
                case "Policeman": {
                    dialogDescription.setText(getString(R.string.policeDead));
                    handleDraw(1, dialogDescription);
                    break;
                }
                case "Rival Drug Dealer": {
                    dialogDescription.setText(getString(R.string.drugDealerDead));
                    handleDraw(2, dialogDescription);
                    break;
                }
                case "Armed Officer": {
                    dialogDescription.setText(getString(R.string.officerDead, bodyPart));
                    handleDraw(3, dialogDescription);
                    break;
                }
                case "default": {
                    dialogDescription.setText(getString(R.string.placeholder));
                    handleDraw(999, dialogDescription);
                    break;
                }
            }
        }
    }

    public void handleDraw(int typeOfDraw, TextView dialogDescription) {
        // typeOfDraw
        // 1 = player and policeman dead
        // 2 = player and drug dealer dead
        // 3 = player and armed officer dead
        // 999 = undefined draw

        // TODO handle draw messages

        switch(typeOfDraw) {
            case 1: {

                break;
            }
            case 2: {

                break;
            }
            case 3: {

                break;
            }
            default: {
                break;
            }
        }
    }

    public void handleWin(int typeOfWin, TextView dialogDescription) {
        // TODO handle win - Done

        // typeOfWin
        // 1 = player kills policeman
        // 2 = player kills policeman drug dealer
        // 3 = player kills policeman armed officer
        // 999 = undefined win

        switch(typeOfWin) {
            case 1: {
                dialogDescription.setText(getString(R.string.policeDead));
                enemyAliveOrDead[selectedEnemyId] = false;
                break;
            }
            case 2: {
                dialogDescription.setText(getString(R.string.drugDealerDead));
                enemyAliveOrDead[selectedEnemyId] = false;
                break;
            }
            case 3: {
                dialogDescription.setText(getString(R.string.officerDead));
                enemyAliveOrDead[selectedEnemyId] = false;
                break;
            }
            default: {
                dialogDescription.setText(getString(R.string.placeholder));
                break;
            }
        }
    }

    public void handleDeath(TextView dialogDescription) {
        // TODO handle Death

        // typeOfDeath
        // 1 = policeman killed player
        // 2 = drug dealer killed player
        // 3 = armed officer killed player
        // 999 = undefined death

        currentCashVal = 0;
        currentDayVal += 3;

        TextView currentCash = (TextView) findViewById(R.id.currentCashView);
        TextView currentDay = (TextView) findViewById(R.id.currentDayView);

        if (currentCash != null) {
            currentCash.setText(String.valueOf(currentCashVal));
        }
        if (currentDay != null) {
            currentDay.setText(String.valueOf(currentDayVal));
        }

        dialogDescription.setText(getString(R.string.policeKilledPlayer));

        // === Get quantities of drugs ====
        TableLayout drugTable = (TableLayout) findViewById(R.id.drugTable);

        int drugTableRowCount = drugTable.getChildCount();
        drugQuantities = new int[drugTableRowCount];
        drugValues = new int[drugTableRowCount];

        //Get current quantities
        for (int i = 0; i < drugTableRowCount; i++) {
            View drugTableRow = drugTable.getChildAt(i);
            if (drugTableRow instanceof TableRow) {
                TableRow currentRow = (TableRow) drugTableRow;
                TextView quantity = (TextView) currentRow.getChildAt(0);
                drugQuantities[i] = Integer.parseInt(quantity.getText().toString());

                quantity.setText("0");
            }
        }
    }

    public int getCountOfVisibleDrugs() {
        TableLayout drugTable = (TableLayout) findViewById(R.id.drugTable);
        int childCount = drugTable.getChildCount();
        int count = 0;

        for (int i = 0; i < childCount; i++) {
            if (drugTable.getChildAt(i).getVisibility() == View.VISIBLE) {
                count++;
            }
        }

        return count;
    }

    public void handleLoadOrMove() {
        bodyParts = getResources().getStringArray(R.array.bodyParts);

        // If user selected load game
        if (getIntent() != null && getIntent().getExtras() != null) {
            // make sure it was load game and not a different intent
            if (getIntent().getExtras().getInt("loadGame") == 1) {
                loadGame();
            } else {
                // If intent was move then run setupMove
                setupMove();
            }
        }
    }

    public void endGame() {
        Button endGameBtn = (Button) findViewById(R.id.moveBtn);

        endGameBtn.setText(R.string.finish);

        endGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), end_game.class);
                Bundle extras = new Bundle();

                extras.putInt("cash", currentCashVal);
                extras.putInt("bank", currentBankVal);
                extras.putInt("debt", currentDebtVal);

                intent.putExtras(extras);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                finish();
            }
        });
    }

    public void saveGame() {
        // Setup sharedPrefs
        SharedPreferences.Editor savePref = getSharedPreferences(SAVE_FILE, MODE_PRIVATE).edit();

        // Store all important values
        savePref.putInt("currentCash", currentCashVal);
        savePref.putInt("currentDebt", currentDebtVal + debtInterest);
        savePref.putInt("currentBank", currentBankVal);
        savePref.putInt("currentItemsHeld", totalItemsHeld);
        savePref.putInt("selectedCity", currentCityNum);
        savePref.putInt("currentDayVal", currentDayVal);
        savePref.putInt("drugAverage1", drugAverage1);
        savePref.putInt("drugAverage2", drugAverage2);
        savePref.putInt("drugAverage3", drugAverage3);
        savePref.putInt("drugAverage4", drugAverage4);
        savePref.putInt("drugAverage5", drugAverage5);
        savePref.putInt("drugAverage6", drugAverage6);
        savePref.putInt("drugAverage7", drugAverage7);
        savePref.putInt("drugAverage8", drugAverage8);
        savePref.putInt("drugAverage9", drugAverage9);
        savePref.putInt("drugAverage10", drugAverage10);
        savePref.putInt("drugAverage11", drugAverage11);
        savePref.putInt("currentWeapon", currentWeapon);
        savePref.putInt("currentHealth", currentHealthVal);
        savePref.putString("enemiesHealth", Arrays.toString(enemiesHealth));
        savePref.putString("enemiesAliveOrDead", Arrays.toString(enemyAliveOrDead));

        // === Get quantities of drugs ====
        TableLayout drugTable = (TableLayout) findViewById(R.id.drugTable);
        int drugTableRowCount = drugTable.getChildCount();
        drugQuantities = new int[drugTableRowCount];
        drugValues = new int[drugTableRowCount];

        //Get current quantities
        for (int i = 0; i < drugTableRowCount; i++) {
            View drugTableRow = drugTable.getChildAt(i);
            if (drugTableRow instanceof TableRow) {
                TableRow currentRow = (TableRow) drugTableRow;
                TextView quantity = (TextView) currentRow.getChildAt(0);
                drugQuantities[i] = Integer.parseInt(quantity.getText().toString());

                TextView value = (TextView) currentRow.getChildAt(2);
                drugValues[i] = Integer.parseInt(value.getText().toString());
            }
        }

        savePref.putString("drugQuantities", Arrays.toString(drugQuantities));
        savePref.putString("drugValues", Arrays.toString(drugValues));
        savePref.putString("drugVisible", Arrays.toString(drugVisible));

        // commit changes
        savePref.apply();

        quitGame();
    }

    public void quitGame() {
        Intent quit = new Intent(this, Menu.class);
        quit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(quit);

        finish();
    }

    public void loadGame() {
            //TODO load game logic
        SharedPreferences savePref = getSharedPreferences(SAVE_FILE, MODE_PRIVATE);

        currentCashVal = savePref.getInt("currentCash", -1);
        currentDebtVal = savePref.getInt("currentDebt", -1);
        currentBankVal = savePref.getInt("currentBank", -1);
        totalItemsHeld = savePref.getInt("currentItemsHeld", -1);
        currentCityNum = savePref.getInt("selectedCity", -1);
        currentDayVal = savePref.getInt("currentDayVal", -1);
        drugAverage1 = savePref.getInt("drugAverage1", -1);
        drugAverage2 = savePref.getInt("drugAverage2", -1);
        drugAverage3 = savePref.getInt("drugAverage3", -1);
        drugAverage4 = savePref.getInt("drugAverage4", -1);
        drugAverage5 = savePref.getInt("drugAverage5", -1);
        drugAverage6 = savePref.getInt("drugAverage6", -1);
        drugAverage7 = savePref.getInt("drugAverage7", -1);
        drugAverage8 = savePref.getInt("drugAverage8", -1);
        drugAverage9 = savePref.getInt("drugAverage9", -1);
        drugAverage10 = savePref.getInt("drugAverage10", -1);
        drugAverage11 = savePref.getInt("drugAverage11", -1);
        currentWeapon = savePref.getInt("currentWeapon", -1);
        currentHealthVal = savePref.getInt("currentHealth", -1);

        // Process the drug quantities
        try {
            String[] drugQuantity = savePref.getString("drugQuantities", null).replace("[", "").replace("]", "").split(", ");
            String[] drugValue = savePref.getString("drugValues", null).replace("[", "").replace("]", "").split(", ");
            String[] drugVis = savePref.getString("drugVisible", null).replace("[", "").replace("]", "").split(", ");

            drugQuantities = new int[drugQuantity.length];
            drugValues = new int[drugQuantity.length];
            drugVisible = new int[drugQuantity.length];

            for (int i = 0; i < drugQuantity.length; i++) {
                drugQuantities[i] = Integer.parseInt(drugQuantity[i]);
                drugValues[i] = Integer.parseInt(drugValue[i]);
                drugVisible[i] = Integer.parseInt(drugVis[i]);
            }
        } catch (NullPointerException e) {
            Log.e("myapp", "failed to load drug information - NullPointerException");
        }

        // Process enemy data
        try {
            String[] enemiesHealth = savePref.getString("enemiesHealth", null).replace("[", "").replace("]", "").split(", ");
            String[] enemiesAliveOrDead = savePref.getString("enemiesAliveOrDead", null).replace("[", "").replace("]", "").split(", ");

            for (int i = 0; i < enemiesHealth.length; i++) {
                enemiesHealth[i] = enemiesHealth[i];
                enemyAliveOrDead[i] = Boolean.getBoolean(enemiesAliveOrDead[i]);
            }
        } catch (NullPointerException e) {
            Log.e("myapp", "failed to load enemy information - NullPointerException");
        }

    }

    // ===== HANDLE BACK BUTTON PRESS ======
    @Override
    public void onBackPressed() {
        // Create a new alert
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.buyOrSell))
                .create();

        // use drug_dialog layout for dialog contents
        final View saveOrQuitDial = View.inflate(this, R.layout.buy_or_sell_dialog, null);
        dialog.setView(saveOrQuitDial);

        LinearLayout saveOrQuitLayout = (LinearLayout) saveOrQuitDial.findViewById(R.id.buySellDialogLayout);
        final Button save = (Button) saveOrQuitDial.findViewById(R.id.buyOptionBtn);
        Button quit = (Button) saveOrQuitLayout.findViewById(R.id.sellOptionBtn);

        save.setText(getString(R.string.save));
        quit.setText(getString(R.string.quit));

        dialog.show();
        setDividerStyle(dialog);

        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveGame();
                dialog.dismiss();
            }
        });

        quit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                quitGame();

            }
        });
    }

    private void weaponDealerHandler() {
        if (currentCashVal == 0) {
            // If no money show error message
            Toast.makeText(getApplicationContext(), getString(R.string.noMoneyForGuns), Toast.LENGTH_LONG).show();
        } else {
            final View dialogView = View.inflate(this, R.layout.weapon_dealer, null);
            final AlertDialog weaponDealerDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.guns))
                    .setView(dialogView)
                    .create();
            weaponDealerDialog.show();
            setDividerStyle(weaponDealerDialog);

            final TextView costValTxt = (TextView) dialogView.findViewById(R.id.costValTxt);
            final TextView cashValTxt = (TextView) dialogView.findViewById(R.id.currentCashTxt);

            final TextView damageValTxt = (TextView) dialogView.findViewById(R.id.damageValTxt);
            final TextView accuracyValTxt = (TextView) dialogView.findViewById(R.id.accuracyValTxt);
            final TextView selectedWeapon = (TextView) dialogView.findViewById(R.id.selectedWeaponTxt);
            final TextView currentWeaponTxt = (TextView) dialogView.findViewById(R.id.currentWeaponTxt);

            LinearLayout pistolBtn = (LinearLayout) dialogView.findViewById(R.id.btn1);
            LinearLayout rifleBtn = (LinearLayout) dialogView.findViewById(R.id.btn2);
            LinearLayout smgBtn = (LinearLayout) dialogView.findViewById(R.id.btn3);
            LinearLayout assaultBtn = (LinearLayout) dialogView.findViewById(R.id.btn4);
            Button buyBtn = (Button) dialogView.findViewById(R.id.buyBtn);
            Button closeBtn = (Button) dialogView.findViewById(R.id.closeBtn);

            // set default values
            damageValTxt.setText(String.valueOf(weaponDmg[0]));
            accuracyValTxt.setText(String.valueOf(weaponAccuracy[0]));
            costValTxt.setText(getString(R.string.currencyQuantity, String.valueOf(weaponCost[0])));
            cashValTxt.setText(getString(R.string.currencyQuantity, String.valueOf(currentCashVal)));
            selectedWeapon.setText(getString(R.string.fists));

            wpnDealerGetCurrentWpn(dialogView);
            switch (currentWeapon) {
                case 1:
                    currentWeaponTxt.setText(getString(R.string.fists));
                    break;
                case 2:
                    currentWeaponTxt.setText(getString(R.string.pistol));
                    break;
                case 3:
                    currentWeaponTxt.setText(getString(R.string.hunting_rifle));
                    break;
                case 4:
                    currentWeaponTxt.setText(getString(R.string.smg));
                    break;
                case 5:
                    currentWeaponTxt.setText(getString(R.string.assault_rifle));
                    break;
            }

            pistolBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    damageValTxt.setText(String.valueOf(weaponDmg[1]));
                    accuracyValTxt.setText(String.valueOf(weaponAccuracy[1]));
                    costValTxt.setText(getString(R.string.currencyQuantity, String.valueOf(weaponCost[1])));
                    selectedWeapon.setText(getString(R.string.pistol));

                    wpnDealerGetCurrentWpn(dialogView);
                    wpnDealerGetSelectedWpn(dialogView);
                    wpnDealerCalcValueColors(dialogView);
                }
            });

            rifleBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    damageValTxt.setText(String.valueOf(weaponDmg[2]));
                    accuracyValTxt.setText(String.valueOf(weaponAccuracy[2]));
                    costValTxt.setText(getString(R.string.currencyQuantity, String.valueOf(weaponCost[2])));
                    selectedWeapon.setText(getString(R.string.hunting_rifle));

                    wpnDealerGetCurrentWpn(dialogView);
                    wpnDealerGetSelectedWpn(dialogView);
                    wpnDealerCalcValueColors(dialogView);
                }
            });


            smgBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    damageValTxt.setText(String.valueOf(weaponDmg[3]));
                    accuracyValTxt.setText(String.valueOf(weaponAccuracy[3]));
                    costValTxt.setText(getString(R.string.currencyQuantity, String.valueOf(weaponCost[3])));
                    selectedWeapon.setText(getString(R.string.smg));

                    wpnDealerGetCurrentWpn(dialogView);
                    wpnDealerGetSelectedWpn(dialogView);
                    wpnDealerCalcValueColors(dialogView);
                }
            });

            assaultBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    damageValTxt.setText(String.valueOf(weaponDmg[4]));
                    accuracyValTxt.setText(String.valueOf(weaponAccuracy[4]));
                    costValTxt.setText(getString(R.string.currencyQuantity, String.valueOf(weaponCost[4])));
                    selectedWeapon.setText(getString(R.string.assault_rifle));

                    wpnDealerGetCurrentWpn(dialogView);
                    wpnDealerGetSelectedWpn(dialogView);

                    wpnDealerGetCurrentWpn(dialogView);
                    wpnDealerGetSelectedWpn(dialogView);
                    wpnDealerCalcValueColors(dialogView);
                }
            });


            buyBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // HANDLE WHAT WEAPON WAS SELECTED
                    int selectedWeaponCost;

                    wpnDealerGetSelectedWpn(dialogView); // returns selectedWpnVal

                    selectedWeaponCost = weaponCost[selectedWpnVal-1];

                    // if selected weapon isn't current weapon
                    if (selectedWpnVal != currentWeapon) {
                        // if cost of weapon is more than user has
                        if (selectedWeaponCost > currentCashVal) {
                            Toast.makeText(getApplicationContext(), getString(R.string.noMoneyToBuyGuns), Toast.LENGTH_LONG).show();
                            // if weapon is affordable
                        } else {
                            currentCashVal = currentCashVal - selectedWeaponCost;
                            currentWeapon = selectedWpnVal;

                            ImageView weaponImg = (ImageView) findViewById(R.id.weapon);
                            TextView currentCash = (TextView) findViewById(R.id.currentCashView);

                            currentCash.setText(String.valueOf(currentCashVal));
                            cashValTxt.setText(getString(R.string.currencyQuantity, String.valueOf(currentCashVal)));

                            switch (selectedWpnVal) {
                                case 2:
                                    weaponImg.setBackgroundResource(R.drawable.gun_pistol);
                                    currentWeaponTxt.setText(getString(R.string.pistol));
                                    break;
                                case 3:
                                    weaponImg.setBackgroundResource(R.drawable.gun_rifle);
                                    currentWeaponTxt.setText(getString(R.string.hunting_rifle));
                                    break;
                                case 4:
                                    weaponImg.setBackgroundResource(R.drawable.gun_smg);
                                    currentWeaponTxt.setText(getString(R.string.smg));
                                    break;
                                case 5:
                                    weaponImg.setBackgroundResource(R.drawable.gun_assault_rifle);
                                    currentWeaponTxt.setText(getString(R.string.assault_rifle));
                                    break;
                            }

                            damageValTxt.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTxtWhite));
                            accuracyValTxt.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTxtWhite));
                        }
                    }
                }
            });

            closeBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    weaponDealerDialog.dismiss();
                }
            });
        }
    }


    // Used by weapon dealer to calculate what the held weapon is
    public int wpnDealerGetCurrentWpn(View dialogView) {
        TextView currentWeaponTxt = (TextView) dialogView.findViewById(R.id.currentWeaponTxt);
        String currentWpnTxt = currentWeaponTxt.getText().toString();

        if (currentWpnTxt.equals(getString(R.string.fists))) {
            currentWeapon = 1;
        } else if (currentWpnTxt.equals(getString(R.string.pistol))) {
            currentWeapon = 2;
        } else if (currentWpnTxt.equals(getString(R.string.hunting_rifle))) {
            currentWeapon = 3;
        } else if (currentWpnTxt.equals(getString(R.string.smg))) {
            currentWeapon = 4;
        } else if (currentWpnTxt.equals(getString(R.string.assault_rifle))) {
            currentWeapon = 5;
        }

        return currentWeapon;
    }

    // Used by weapon dealer to calculate what the selected weapon is
    public int wpnDealerGetSelectedWpn(View dialogView) {
        TextView selectedWeapon = (TextView) dialogView.findViewById(R.id.selectedWeaponTxt);
        String selectedWpmTxt = selectedWeapon.getText().toString();

        if (selectedWpmTxt.equals(getString(R.string.pistol))) {
            selectedWpnVal = 2;
        } else if (selectedWpmTxt.equals(getString(R.string.hunting_rifle))) {
            selectedWpnVal = 3;
        } else if (selectedWpmTxt.equals(getString(R.string.smg))) {
            selectedWpnVal = 4;
        } else if (selectedWpmTxt.equals(getString(R.string.assault_rifle))) {
            selectedWpnVal = 5;
        }

        return selectedWpnVal;
    }

    public void wpnDealerCalcValueColors(View dialogView) {
        final TextView damageValTxt = (TextView) dialogView.findViewById(R.id.damageValTxt);
        final TextView accuracyValTxt = (TextView) dialogView.findViewById(R.id.accuracyValTxt);

        if (currentWeapon != -1) {
            // Color damage text
            int currentWpnDmg = weaponDmg[currentWeapon-1];
            int selectedWpnDmg = weaponDmg[selectedWpnVal-1];

            if (currentWpnDmg > selectedWpnDmg) {
                damageValTxt.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            } else if (currentWpnDmg < selectedWpnDmg) {
                damageValTxt.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGreen));
            } else if (currentWpnDmg == selectedWpnDmg) {
                damageValTxt.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTxtWhite));
            }

            // Color accuracy text
            int currentWpnAcc = weaponAccuracy[currentWeapon-1];
            int selectedWpnAcc = weaponAccuracy[selectedWpnVal-1];
            if (currentWpnAcc > selectedWpnAcc) {
                accuracyValTxt.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            } else if (currentWpnAcc < selectedWpnAcc) {
                accuracyValTxt.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGreen));
            } else if (currentWpnAcc == selectedWpnAcc) {
                accuracyValTxt.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTxtWhite));
            }
        }
    }
    // ======= END OF WEAPON HANDLING ==========

    // -====== BANK HANDLING ===========

    private void bankHandler(View view) {
        final View bankView = view;

        // Handle no money and no bank
        if (currentCashVal == 0 && currentBankVal == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.noMoneyForBank), Toast.LENGTH_LONG).show();

            // Handle no money but money in bank
        } else if ((currentCashVal == 0 && currentBankVal > 0) || depositOrWithdraw.equals("withdraw")) {
            depositOrWithdraw = "";

            final TextView currentBankTxt = (TextView) findViewById(R.id.currentBankView);
            final TextView currentCashTxt = (TextView) findViewById(R.id.currentCashView);

            View dialogView = View.inflate(this, R.layout.drug_dialog, null);
            final AlertDialog withdrawMoneyDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.bank))
                    .setView(dialogView)
                    .create();

            withdrawMoneyDialog.show();
            setDividerStyle(withdrawMoneyDialog);

            // Initialise dialog contents
            TextView dialogDescription = (TextView) dialogView.findViewById(R.id.dialogDescription);
            final EditText dialogQuantity = (EditText) dialogView.findViewById(R.id.dialogEditText);
            Button dialogButton = (Button) dialogView.findViewById(R.id.buyBtn);

            withdrawMoneyDialog.setTitle(getString(R.string.bank));
            dialogDescription.setText(getString(R.string.withdrawMoneyBank));
            dialogQuantity.setText(String.valueOf(currentBankVal));
            dialogButton.setText(getString(R.string.withdrawBank));

            dialogButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int withdrawValue = Integer.parseInt(dialogQuantity.getText().toString());

                    if (withdrawValue > currentBankVal) {
                        withdrawValue = currentBankVal;
                    }

                    currentCashVal = currentCashVal + withdrawValue;
                    currentBankVal = currentBankVal - withdrawValue;

                    currentCashTxt.setText(String.valueOf(currentCashVal));
                    currentBankTxt.setText(String.valueOf(currentBankVal));

                    withdrawMoneyDialog.dismiss();
                }
            });
            // Handle money but no bank
        } else if ((currentCashVal > 0 && currentBankVal == 0) || depositOrWithdraw.equals("deposit")) {
            depositOrWithdraw = "";

            final TextView currentBankTxt = (TextView) findViewById(R.id.currentBankView);
            final TextView currentCashTxt = (TextView) findViewById(R.id.currentCashView);

            View dialogView = View.inflate(this, R.layout.drug_dialog, null);
            final AlertDialog depositMoneyDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.bank))
                    .setView(dialogView)
                    .create();

            depositMoneyDialog.show();
            setDividerStyle(depositMoneyDialog);

            // Initialise dialog contents
            TextView dialogDescription = (TextView) dialogView.findViewById(R.id.dialogDescription);
            final EditText dialogQuantity = (EditText) dialogView.findViewById(R.id.dialogEditText);
            Button dialogButton = (Button) dialogView.findViewById(R.id.buyBtn);

            dialogDescription.setText(getString(R.string.depositMoneyBank));
            dialogQuantity.setText(String.valueOf(currentCashVal));
            dialogButton.setText(getString(R.string.depositBank));

            dialogButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int depositValue = Integer.parseInt(dialogQuantity.getText().toString());

                    if (depositValue > currentCashVal) {
                        depositValue = currentCashVal;
                    }

                    currentCashVal = currentCashVal - depositValue;
                    currentBankVal = currentBankVal + depositValue;

                    currentCashTxt.setText(String.valueOf(currentCashVal));
                    currentBankTxt.setText(String.valueOf(currentBankVal));

                    depositMoneyDialog.dismiss();
                }
            });
        } else if (currentCashVal > 0 && currentBankVal > 0){
            final View buyOrSellDial = View.inflate(this, R.layout.buy_or_sell_dialog, null);
            final AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.bank))
                    .setView(buyOrSellDial)
                    .create();

            LinearLayout buyOrSellDialogLayout = (LinearLayout) buyOrSellDial.findViewById(R.id.buySellDialogLayout);
            final Button deposit = (Button) buyOrSellDial.findViewById(R.id.buyOptionBtn);
            Button withdraw = (Button) buyOrSellDialogLayout.findViewById(R.id.sellOptionBtn);

            deposit.setText(getString(R.string.depositBank));
            withdraw.setText(getString(R.string.withdrawBank));

            dialog.show();
            setDividerStyle(dialog);

            deposit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //TODO change dialog view to buy
                    depositOrWithdraw = "deposit";

                    dialog.dismiss();
                    activityBtn(bankView);

                }
            });

            withdraw.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    confirmButtonTxt = getString(R.string.sell);
                    depositOrWithdraw = "withdraw";

                    dialog.dismiss();
                    activityBtn(bankView);
                }
            });

        }
    }

    private void loanSharkHandler() {
        final TextView debtOwedTxt = (TextView) findViewById(R.id.currentDebtView);
        final TextView currentCashTxt = (TextView) findViewById(R.id.currentCashView);
        final Button actionBtn = (Button) findViewById(R.id.activityBtn);

        if (currentCashVal == 0 && currentDebtVal > 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.noMoneyForLoanShark), Toast.LENGTH_LONG).show();

            // If user doesn't have a loan
        } else if (currentDebtVal == 0) {
            View dialogView = View.inflate(this, R.layout.drug_dialog, null);
            final AlertDialog takeDebtDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.loanShark))
                    .setView(dialogView)
                    .create();

            takeDebtDialog.show();
            setDividerStyle(takeDebtDialog);

            // Initialise dialog contents
            TextView dialogDescription = (TextView) dialogView.findViewById(R.id.dialogDescription);
            final EditText dialogQuantity = (EditText) dialogView.findViewById(R.id.dialogEditText);
            Button dialogButton = (Button) dialogView.findViewById(R.id.buyBtn);

            dialogButton.setText(getString(R.string.loanSharkTakeBtn));
            dialogDescription.setText(getString(R.string.loanSharkDesc));

            // Handles what happens when the user clicks pay
            dialogButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // TODO maybe make maximum quantity of loan based on something? - days, previous loans, current cash

                    // make sure input isn't null
                    if (!TextUtils.isEmpty(dialogQuantity.getText())) {
                        // set the inputLoanQuantity value
                        inputLoanQuantity = Integer.parseInt(dialogQuantity.getText().toString());

                        // Update current debt
                        currentCashVal = currentCashVal + inputLoanQuantity;
                        currentDebtVal = inputLoanQuantity;

                        debtOwedTxt.setText(String.valueOf(currentDebtVal));
                        currentCashTxt.setText(String.valueOf(currentCashVal));

                        actionBtn.setText(R.string.loanSharkPayBtn);
                    }

                    takeDebtDialog.dismiss();
                }
            });

            // if user already has a loan
        } else {
            if (currentDebtVal > 0) {
                // Setup pay loan
                View dialogView = View.inflate(this, R.layout.drug_dialog, null);
                final AlertDialog payDebtDialog = new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.loanShark))
                        .setView(dialogView)
                        .create();
                payDebtDialog.show();
                setDividerStyle(payDebtDialog);

                // Initialise dialog contents
                TextView dialogDescription = (TextView) dialogView.findViewById(R.id.dialogDescription);
                final EditText dialogQuantity = (EditText) dialogView.findViewById(R.id.dialogEditText);
                final Button dialogButton = (Button) dialogView.findViewById(R.id.buyBtn);

                // Handle setting up the pay dialog
                dialogButton.setText(getString(R.string.loanSharkPayBtn));
                dialogDescription.setText(getString(R.string.loanSharkPayDesc));

                if (currentCashVal < currentDebtVal) {
                    dialogQuantity.setText(String.valueOf(currentCashVal));
                } else if (currentCashVal >= currentDebtVal) {
                    dialogQuantity.setText(String.valueOf(currentDebtVal));
                }

                // Handles what happens when the user clicks pay
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // make sure input isn't null
                        if (!TextUtils.isEmpty(dialogQuantity.getText())) {
                            // set the inputLoanQuantity value
                            inputLoanQuantity = Integer.parseInt(dialogQuantity.getText().toString());

                            // Loan bigger than cash
                            if (inputLoanQuantity > currentCashVal) {
                                // loan bigger than debt
                                if (inputLoanQuantity > currentDebtVal) {
                                    inputLoanQuantity = currentDebtVal;
                                } else {
                                    inputLoanQuantity = currentCashVal;
                                }
                            } else {
                                if (inputLoanQuantity > currentDebtVal) {
                                    inputLoanQuantity = currentDebtVal;
                                }
                            }

                            // Update current debt
                            currentCashVal = currentCashVal - inputLoanQuantity;
                            currentDebtVal = currentDebtVal - inputLoanQuantity;

                            debtOwedTxt.setText(String.valueOf(currentDebtVal));
                            currentCashTxt.setText(String.valueOf(currentCashVal));

                            if (currentDebtVal == 0) {
                                actionBtn.setText(R.string.loanSharkTakeBtn);
                            }
                        }

                        payDebtDialog.dismiss();
                    }
                });
            }
        }
    }

    // ======= HANDLE ACTIVITY BUTTON CLICK ============
    public void activityBtn(final View view) {
        final Button actionBtn = (Button) findViewById(R.id.activityBtn);
        String actionBtnTxt = actionBtn.getText().toString();

        // ========== HANDLE WEAPON DEALER ===========
        if (actionBtnTxt.equals(getString(R.string.guns))) {
            weaponDealerHandler();
        }
        // =========== HANDLE BANK =============
         else if (actionBtnTxt.equals(getString(R.string.bank))) {
            bankHandler(view);
        }
        // ========== HANDLE LOAN SHARK ==============
        else if (actionBtnTxt.equals(getString(R.string.loanSharkTakeBtn)) || actionBtnTxt.equals(getString(R.string.loanSharkPayBtn))) {
            loanSharkHandler();
        }
        // ========== HANDLE HOSPITAL ==========
        else if (actionBtnTxt.equals(getString(R.string.hospital))) {
            hospitalHandler();
        }
    }

    private void hospitalHandler() {
        if (currentHealthVal != 100) {

            hospitalDialog dialog = new hospitalDialog(this, R.style.Theme_AppTheme);
            dialog.handleDialog();

        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.noNeedOfHospital), Toast.LENGTH_LONG).show();
        }
    }

    public void setupMove() {
        // If player has moved pass values from last city
        if (getIntent() != null && getIntent().getExtras() != null) {
            Intent move = getIntent();
            Bundle extras = move.getExtras();

            firstRun = extras.getInt("firstRun");
            currentCashVal = extras.getInt("currentCash");
            currentDebtVal = extras.getInt("currentDebt") + debtInterest;
            currentBankVal = extras.getInt("currentBank");
            totalItemsHeld = extras.getInt("currentItemsHeld");
            currentCityNum = extras.getInt("selectedCity");
            currentDayVal = extras.getInt("currentDayVal");
            drugQuantities = extras.getIntArray("drugQuantities");
            currentWeapon = extras.getInt("currentWeapon", -1);
            currentHealthVal = extras.getInt("currentHealth", -1);
            enemiesHealth = extras.getIntArray("enemiesHealth");
            enemyAliveOrDead = extras.getBooleanArray("enemyAliveOrDead");

            if (extras.getInt("drugAverage1") != 0) {
                drugPurchases1.add(extras.getInt("drugAverage1"));
            }
            if (extras.getInt("drugAverage2") != 0) {
                drugPurchases2.add(extras.getInt("drugAverage2"));
            }
            if (extras.getInt("drugAverage3") != 0) {
                drugPurchases3.add(extras.getInt("drugAverage3"));
            }
            if (extras.getInt("drugAverage4") != 0) {
                drugPurchases4.add(extras.getInt("drugAverage4"));
            }
            if (extras.getInt("drugAverage5") != 0) {
                drugPurchases5.add(extras.getInt("drugAverage5"));
            }
            if (extras.getInt("drugAverage6") != 0) {
                drugPurchases6.add(extras.getInt("drugAverage6"));
            }
            if (extras.getInt("drugAverage7") != 0) {
                drugPurchases7.add(extras.getInt("drugAverage7"));
            }
            if (extras.getInt("drugAverage8") != 0) {
                drugPurchases8.add(extras.getInt("drugAverage8"));
            }
            if (extras.getInt("drugAverage9") != 0) {
                drugPurchases9.add(extras.getInt("drugAverage9"));
            }
            if (extras.getInt("drugAverage10") != 0) {
                drugPurchases10.add(extras.getInt("drugAverage10"));
            }
            if (extras.getInt("drugAverage11") != 0) {
                drugPurchases11.add(extras.getInt("drugAverage11"));
            }
        }
    }

    // On click of move button
    /*public void moveBtnClick(View view) {
        // TODO stop able to select current city

        //Create dialog for choosing location to move to
        AlertDialog.Builder moveDialog = new AlertDialog.Builder(this);
        AlertDialog dialog = moveDialog.create();
        View dialogView = View.inflate(this, R.layout.move, null);

        dialog.setView(dialogView);
        dialog.show();
    }*/

    // TESTING
    public void moveBtnClick(View view) {
        FragmentManager fm = getFragmentManager();
        CustomDialog dialogFragment = new CustomDialog();
        dialogFragment.show(fm, "Fragment");
    }

    // On click of city selection button
    public void citySelected(View view) {

        TextView currentCity = (TextView) findViewById(R.id.cityHeader);
        // Get which button was pressed in dialog
        Button sender = (Button) view;
        int selectedButton = sender.getId();
        String senderTxt = sender.getText().toString();
        String currentCityTxt = currentCity.getText().toString();

        if (senderTxt.equals(currentCityTxt)) {
            Toast.makeText(getApplicationContext(), getString(R.string.moveError), Toast.LENGTH_LONG).show();
            return;
        }

        // depending on text value of button
        switch(selectedButton) {
            case R.id.firstCityBtn:
                currentCityNum = 0;
                break;
            case R.id.secondCityBtn:
                currentCityNum = 1;
                break;
            case R.id.thirdCityBtn:
                currentCityNum = 2;
                break;
            case R.id.fourthCityBtn:
                currentCityNum = 3;
                break;
            case R.id.fifthCityBtn:
                currentCityNum = 4;
                break;
        }

        //Calculate the debt interest to add to debt value
        debtInterest = Math.round((currentDebtVal / 100.0f) * interestRate);

        if (!drugPurchases1.isEmpty()) {
            drugAverage1 = averageArray(drugPurchases1);
        }
        if (!drugPurchases2.isEmpty()) {
            drugAverage2 = averageArray(drugPurchases2);
        }
        if (!drugPurchases3.isEmpty()) {
            drugAverage3 = averageArray(drugPurchases3);
        }
        if (!drugPurchases4.isEmpty()) {
            drugAverage4 = averageArray(drugPurchases4);
        }
        if (!drugPurchases5.isEmpty()) {
            drugAverage5 = averageArray(drugPurchases5);
        }
        if (!drugPurchases6.isEmpty()) {
            drugAverage6 = averageArray(drugPurchases6);
        }
        if (!drugPurchases7.isEmpty()) {
            drugAverage7 = averageArray(drugPurchases7);
        }
        if (!drugPurchases8.isEmpty()) {
            drugAverage8 = averageArray(drugPurchases8);
        }
        if (!drugPurchases9.isEmpty()) {
            drugAverage9 = averageArray(drugPurchases9);
        }
        if (!drugPurchases10.isEmpty()) {
            drugAverage10 = averageArray(drugPurchases10);
        }
        if (!drugPurchases11.isEmpty()) {
            drugAverage11 = averageArray(drugPurchases11);
        }

        TableLayout drugTable = (TableLayout) findViewById(R.id.drugTable);
        int drugTableRowCount = drugTable.getChildCount();
        drugQuantities = new int[drugTableRowCount];

        //Get current quantities
        for (int i = 0; i < drugTableRowCount; i++) {
            View drugTableRow = drugTable.getChildAt(i);
            if (drugTableRow instanceof TableRow) {
                TableRow currentRow = (TableRow) drugTableRow;
                TextView quantity = (TextView) currentRow.getChildAt(0);
                drugQuantities[i] = Integer.parseInt(quantity.getText().toString());
            }
        }

        // Start new intent
        Intent move = new Intent(this, GameView.class);

        // Add values to pass when moving
        Bundle extras = new Bundle();
            extras.putInt("firstRun", firstRun);
            extras.putInt("currentCash", currentCashVal);
            extras.putInt("currentDebt", currentDebtVal + debtInterest);
            extras.putInt("currentBank", currentBankVal);
            extras.putInt("currentItemsHeld", totalItemsHeld);
            extras.putInt("selectedCity", currentCityNum);
            extras.putInt("currentDayVal", currentDayVal + 1);
            extras.putIntArray("drugQuantities", drugQuantities);
            extras.putInt("drugAverage1", drugAverage1);
            extras.putInt("drugAverage2", drugAverage2);
            extras.putInt("drugAverage3", drugAverage3);
            extras.putInt("drugAverage4", drugAverage4);
            extras.putInt("drugAverage5", drugAverage5);
            extras.putInt("drugAverage6", drugAverage6);
            extras.putInt("drugAverage7", drugAverage7);
            extras.putInt("drugAverage8", drugAverage8);
            extras.putInt("drugAverage9", drugAverage9);
            extras.putInt("drugAverage10", drugAverage10);
            extras.putInt("drugAverage11", drugAverage11);
            extras.putInt("currentWeapon", currentWeapon);
            extras.putInt("currentHealth", currentHealthVal);
            extras.putIntArray("enemiesHealth", enemiesHealth);
            extras.putBooleanArray("enemyAliveOrDead", enemyAliveOrDead);

        move.putExtras(extras);


        move.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Start the main activity
        startActivity(move);
        finish();
    }

    public void onDrugRowClick(final View view) {

        selectedRow = view.getId();

        TextView cashValue = (TextView) findViewById(R.id.currentCashView);
        TableRow currentTableRow = (TableRow) view;
        final TextView currentTableRowQuantity = (TextView) currentTableRow.getChildAt(0);
        TextView currentTableRowCost = (TextView) currentTableRow.getChildAt(2);

        // If current item isn't sellable in location then return message then exit
        if (currentTableRowCost.getText() == "---") {
            Toast.makeText(getApplicationContext(), getString(R.string.cantSell), Toast.LENGTH_LONG).show();
            return;
        }

        // use drug_dialog layout for dialog contents
        final View v = View.inflate(this, R.layout.drug_dialog, null);
        // Create a new alert
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(v)
                .create();

        currentCashVal = Integer.parseInt(cashValue.getText().toString());
        totalCurrentDrugHeldVal = Integer.parseInt(currentTableRowQuantity.getText().toString());

        // Define the linearlayout of the drug dialog
        LinearLayout drugDialogContents = (LinearLayout) v.findViewById(R.id.drugDialogContents);
        TextView currentDrugDescTxt = (TextView) drugDialogContents.findViewById(R.id.dialogDescription);
        TextView buyValueTxt = (TextView) drugDialogContents.findViewById(R.id.buyValueTxt);

        TextView currentTableRowValue = (TextView) currentTableRow.getChildAt(2);
        currentDrugVal = Integer.parseInt(currentTableRowValue.getText().toString());

        String dialogTxt;

        switch (selectedRow) {
            case R.id.tableRow1:
                dialog.setTitle(getString(R.string.drug1) + " - " + getString(R.string.currencyQuantity, String.valueOf(currentDrugVal)));
                currentDrugDescTxt.setText(getString(R.string.drug1Desc));
                if (!drugPurchases1.isEmpty()) {
                    dialogTxt = getString(R.string.boughtFor) + " " + String.valueOf(averageArray(drugPurchases1));
                    buyValueTxt.setText(dialogTxt);
                    buyValueTxt.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tableRow2:
                dialog.setTitle(getString(R.string.drug2) + " - " + getString(R.string.currencyQuantity, String.valueOf(currentDrugVal)));
                currentDrugDescTxt.setText(getString(R.string.drug2Desc));
                if (!drugPurchases2.isEmpty()) {
                    dialogTxt = getString(R.string.boughtFor) + " " + String.valueOf(averageArray(drugPurchases2));
                    buyValueTxt.setText(dialogTxt);
                    buyValueTxt.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tableRow3:
                dialog.setTitle(getString(R.string.drug3) + " - " + getString(R.string.currencyQuantity, String.valueOf(currentDrugVal)));
                currentDrugDescTxt.setText(getString(R.string.drug3Desc));
                if (!drugPurchases3.isEmpty()) {
                    dialogTxt = getString(R.string.boughtFor) + " " + String.valueOf(averageArray(drugPurchases3));
                    buyValueTxt.setText(dialogTxt);
                    buyValueTxt.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tableRow4:
                dialog.setTitle(getString(R.string.drug4) + " - " + getString(R.string.currencyQuantity, String.valueOf(currentDrugVal)));
                currentDrugDescTxt.setText(getString(R.string.drug4Desc));
                if (!drugPurchases4.isEmpty()) {
                    dialogTxt = getString(R.string.boughtFor) + " " + String.valueOf(averageArray(drugPurchases4));
                    buyValueTxt.setText(dialogTxt);
                    buyValueTxt.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tableRow5:
                dialog.setTitle(getString(R.string.drug5) + " - " + getString(R.string.currencyQuantity, String.valueOf(currentDrugVal)));
                currentDrugDescTxt.setText(getString(R.string.drug5Desc));
                if (!drugPurchases5.isEmpty()) {
                    dialogTxt = getString(R.string.boughtFor) + " " + String.valueOf(averageArray(drugPurchases5));
                    buyValueTxt.setText(dialogTxt);
                    buyValueTxt.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tableRow6:
                dialog.setTitle(getString(R.string.drug6) + " - " + getString(R.string.currencyQuantity, String.valueOf(currentDrugVal)));
                currentDrugDescTxt.setText(getString(R.string.drug6Desc));
                if (!drugPurchases6.isEmpty()) {
                    dialogTxt = getString(R.string.boughtFor) + " " + String.valueOf(averageArray(drugPurchases6));
                    buyValueTxt.setText(dialogTxt);
                    buyValueTxt.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tableRow7:
                dialog.setTitle(getString(R.string.drug7) + " - " + getString(R.string.currencyQuantity, String.valueOf(currentDrugVal)));
                currentDrugDescTxt.setText(getString(R.string.drug7Desc));
                if (!drugPurchases7.isEmpty()) {
                    dialogTxt = getString(R.string.boughtFor) + " " + String.valueOf(averageArray(drugPurchases7));
                    buyValueTxt.setText(dialogTxt);
                    buyValueTxt.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tableRow8:
                dialog.setTitle(getString(R.string.drug8) + " - " + getString(R.string.currencyQuantity, String.valueOf(currentDrugVal)));
                currentDrugDescTxt.setText(getString(R.string.drug8Desc));
                if (!drugPurchases8.isEmpty()) {
                    dialogTxt = getString(R.string.boughtFor) + " " + String.valueOf(averageArray(drugPurchases8));
                    buyValueTxt.setText(dialogTxt);
                    buyValueTxt.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tableRow9:
                dialog.setTitle(getString(R.string.drug9) + " - " + getString(R.string.currencyQuantity, String.valueOf(currentDrugVal)));
                currentDrugDescTxt.setText(getString(R.string.drug9Desc));
                if (!drugPurchases9.isEmpty()) {
                    dialogTxt = getString(R.string.boughtFor) + " " + String.valueOf(averageArray(drugPurchases9));
                    buyValueTxt.setText(dialogTxt);
                    buyValueTxt.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tableRow10:
                dialog.setTitle(getString(R.string.drug10) + " - " + getString(R.string.currencyQuantity, String.valueOf(currentDrugVal)));
                currentDrugDescTxt.setText(getString(R.string.drug10Desc));
                if (!drugPurchases10.isEmpty()) {
                    dialogTxt = getString(R.string.boughtFor) + " " + String.valueOf(averageArray(drugPurchases10));
                    buyValueTxt.setText(dialogTxt);
                    buyValueTxt.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tableRow11:
                dialog.setTitle(getString(R.string.drug11) + " - " + getString(R.string.currencyQuantity, String.valueOf(currentDrugVal)));
                currentDrugDescTxt.setText(getString(R.string.drug11Desc));
                if (!drugPurchases11.isEmpty()) {
                    dialogTxt = getString(R.string.boughtFor) + " " + String.valueOf(averageArray(drugPurchases11));
                    buyValueTxt.setText(dialogTxt);
                    buyValueTxt.setVisibility(View.VISIBLE);
                }
                break;
        }

        // Set default quantity of dialog field
        maxDrugsPurchasableNum = currentCashVal / currentDrugVal;

        // Define edittext then set value of maximum drugs
        final EditText buySellQuantity = (EditText) v.findViewById(R.id.dialogEditText);

        // If drugs held but no money
        if (totalCurrentDrugHeldVal > 0 && maxDrugsPurchasableNum == 0 || totalItemsHeld == maxDrugs && totalCurrentDrugHeldVal > 0) {
            buySellQuantity.setText(String.valueOf(totalCurrentDrugHeldVal));
            confirmButtonTxt = getString(R.string.sell);
        }
        // If no drugs held and can afford some
        if (totalCurrentDrugHeldVal == 0 && maxDrugsPurchasableNum > 0) {
            buySellQuantity.setText(String.valueOf(maxDrugsPurchasableNum));
            confirmButtonTxt = getString(R.string.buy);
        }
        // If holding drugs and have enough money to buy more
        if (totalCurrentDrugHeldVal > 0 && maxDrugsPurchasableNum > 0 && !alreadyBoughtOrSold) {
            // use drug_dialog layout for dialog contents
            final View buyOrSellDial = View.inflate(this, R.layout.buy_or_sell_dialog, null);
            LinearLayout buyOrSellDialogLayout = (LinearLayout) buyOrSellDial.findViewById(R.id.buySellDialogLayout);
            dialog.setView(buyOrSellDial);

            dialog.setTitle(getString(R.string.buyOrSell));
            final Button buy = (Button) buyOrSellDial.findViewById(R.id.buyOptionBtn);
            Button sell = (Button) buyOrSellDialogLayout.findViewById(R.id.sellOptionBtn);

            buy.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    confirmButtonTxt = getString(R.string.buy);
                    //TODO change dialog view to buy
                    buyOrSellVal = "buy";

                    alreadyBoughtOrSold = true;

                    dialog.dismiss();
                    onDrugRowClick(view);

                }
            });

            sell.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    confirmButtonTxt = getString(R.string.sell);
                    buyOrSellVal = "sell";

                    alreadyBoughtOrSold = true;

                    dialog.dismiss();
                    onDrugRowClick(view);
                }
            });
        } else if (totalCurrentDrugHeldVal > 0 && maxDrugsPurchasableNum > 0 && alreadyBoughtOrSold) {
            buySellQuantity.setText(String.valueOf(totalCurrentDrugHeldVal));
        }

        // If cannot afford any items and no drugs held
        if (maxDrugsPurchasableNum == 0 && totalCurrentDrugHeldVal == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.buyError), Toast.LENGTH_LONG).show();
            return;
        }

        dialog.show();
        setDividerStyle(dialog);

        // Confirm logic
        final Button buyButton = (Button) v.findViewById(R.id.buyBtn);

        if (confirmButtonTxt == getString(R.string.buy)) {
            buyButton.setText(R.string.buy);
        } else if (confirmButtonTxt == getString(R.string.sell)) {
            buyButton.setText(R.string.sell);
        }

        final TextView value = (TextView) v.findViewById(R.id.value);
        String quantity = buySellQuantity.getText().toString();
        if (!quantity.isEmpty()) {
            int totalValue = currentDrugVal * Integer.parseInt(buySellQuantity.getText().toString());
            value.setText(getString(R.string.value) + " " + String.valueOf(totalValue));
            value.setVisibility(View.VISIBLE);
        }

        // If value in quantity field changes
        buySellQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Do your stuff
                String input = buySellQuantity.getText().toString();

                if(!input.matches("")) {
                    inputDrugQuantity = Integer.parseInt(buySellQuantity.getText().toString());
                    int totalValue = currentDrugVal * inputDrugQuantity;

                    value.setText(getString(R.string.totalValue, getString(R.string.value), String.valueOf(totalValue)));
                } else {
                    value.setText(getString(R.string.totalValue, getString(R.string.value), "0"));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        buyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Define the linearlayout of the drug dialog

                alreadyBoughtOrSold = false;

                //LinearLayout drugDialogContents = (LinearLayout) v.findViewById(R.id.drugDialogContents);
                EditText buySellQuantity = (EditText) dialog.findViewById(R.id.dialogEditText);
                TextView currentCashTxt = (TextView) findViewById(R.id.currentCashView);
                TextView currentDrugsHeld = (TextView) findViewById(R.id.currentDrugQuantityView);

                // Make sure input isn't empty
                if(!TextUtils.isEmpty((buySellQuantity.getText()))) {
                    inputDrugQuantity = Integer.parseInt(buySellQuantity.getText().toString());

                    // =========== SELL Setup ============
                    if (confirmButtonTxt == getString(R.string.sell)) {
                        // If user enters more than they have then sell max available
                        if (inputDrugQuantity > totalCurrentDrugHeldVal) {
                            inputDrugQuantity = totalCurrentDrugHeldVal;
                        }

                        // Update cash
                        costOfDrugs = inputDrugQuantity * currentDrugVal;
                        currentCashVal = currentCashVal + costOfDrugs;

                        // update totaldrugs value
                        totalItemsHeld = totalItemsHeld - inputDrugQuantity;

                        // Update CASH text
                        currentCashTxt.setText(String.valueOf(currentCashVal));

                        // Update currentDrugsHeld text
                        currentDrugsHeld.setText(String.valueOf(totalItemsHeld));

                        totalCurrentDrugHeldVal = totalCurrentDrugHeldVal - inputDrugQuantity;

                        if (totalCurrentDrugHeldVal == 0) {
                            switch (selectedRow) {
                                case R.id.tableRow1: //here
                                    drugPurchases1.clear();
                                    break;
                                case R.id.tableRow2:
                                    drugPurchases2.clear();
                                    break;
                                case R.id.tableRow3:
                                    drugPurchases3.clear();
                                    break;
                                case R.id.tableRow4:
                                    drugPurchases4.clear();
                                    break;
                                case R.id.tableRow5:
                                    drugPurchases5.clear();
                                    break;
                                case R.id.tableRow6:
                                    drugPurchases6.clear();
                                    break;
                                case R.id.tableRow7:
                                    drugPurchases7.clear();
                                    break;
                                case R.id.tableRow8:
                                    drugPurchases8.clear();
                                    break;
                                case R.id.tableRow9:
                                    drugPurchases9.clear();
                                    break;
                                case R.id.tableRow10:
                                    drugPurchases10.clear();
                                    break;
                                case R.id.tableRow11:
                                    drugPurchases11.clear();
                                    break;
                            }
                        }

                        // ======== BUY Setup ===========
                    } else if (confirmButtonTxt == getString(R.string.buy)) {

                        // If user enters more than possible then just buy the max available
                        if (inputDrugQuantity > maxDrugsPurchasableNum) {
                            inputDrugQuantity = maxDrugsPurchasableNum;
                        }

                        // If buying would put over 100 drug limit
                        if (totalItemsHeld + inputDrugQuantity > maxDrugs) {
                            // Set buy amount to max possible
                            inputDrugQuantity = maxDrugs - totalItemsHeld;
                        }

                        costOfDrugs = inputDrugQuantity * currentDrugVal;

                        if (inputDrugQuantity <= maxDrugsPurchasableNum && inputDrugQuantity != 0) {
                            // minus from cash
                            currentCashVal = currentCashVal - costOfDrugs;
                            // update totaldrugs value
                            totalItemsHeld = totalItemsHeld + inputDrugQuantity;

                            // Update CASH text
                            currentCashTxt.setText(String.valueOf(currentCashVal));

                            // Update currentDrugsHeld text
                            currentDrugsHeld.setText(String.valueOf(totalItemsHeld));

                            totalCurrentDrugHeldVal = totalCurrentDrugHeldVal + inputDrugQuantity;

                            // todo buy value of drugs

                            // update drug purchase price arrays
                           switch (selectedRow) {
                                case R.id.tableRow1: //here
                                    drugPurchases1.add(currentDrugVal);
                                    break;
                                case R.id.tableRow2:
                                    drugPurchases2.add(currentDrugVal);
                                    break;
                                case R.id.tableRow3:
                                    drugPurchases3.add(currentDrugVal);
                                    break;
                                case R.id.tableRow4:
                                    drugPurchases4.add(currentDrugVal);
                                    break;
                                case R.id.tableRow5:
                                    drugPurchases5.add(currentDrugVal);
                                    break;
                                case R.id.tableRow6:
                                    drugPurchases6.add(currentDrugVal);
                                    break;
                                case R.id.tableRow7:
                                    drugPurchases7.add(currentDrugVal);
                                    break;
                                case R.id.tableRow8:
                                    drugPurchases8.add(currentDrugVal);
                                    break;
                                case R.id.tableRow9:
                                    drugPurchases9.add(currentDrugVal);
                                    break;
                                case R.id.tableRow10:
                                    drugPurchases10.add(currentDrugVal);
                                    break;
                                case R.id.tableRow11:
                                    drugPurchases11.add(currentDrugVal);
                                    break;
                            }
                        }
                    }

                    // Update quantity values
                    switch (selectedRow) {
                        case R.id.tableRow1:
                            TextView quantity1 = (TextView) findViewById(R.id.quantity1);
                            quantity1.setText(String.valueOf(totalCurrentDrugHeldVal));
                            break;
                        case R.id.tableRow2:
                            TextView quantity2 = (TextView) findViewById(R.id.quantity2);
                            quantity2.setText(String.valueOf(totalCurrentDrugHeldVal));
                            break;
                        case R.id.tableRow3:
                            TextView quantity3 = (TextView) findViewById(R.id.quantity3);
                            quantity3.setText(String.valueOf(totalCurrentDrugHeldVal));
                            break;
                        case R.id.tableRow4:
                            TextView quantity4 = (TextView) findViewById(R.id.quantity4);
                            quantity4.setText(String.valueOf(totalCurrentDrugHeldVal));
                            break;
                        case R.id.tableRow5:
                            TextView quantity5 = (TextView) findViewById(R.id.quantity5);
                            quantity5.setText(String.valueOf(totalCurrentDrugHeldVal));
                            break;
                        case R.id.tableRow6:
                            TextView quantity6 = (TextView) findViewById(R.id.quantity6);
                            quantity6.setText(String.valueOf(totalCurrentDrugHeldVal));
                            break;
                        case R.id.tableRow7:
                            TextView quantity7 = (TextView) findViewById(R.id.quantity7);
                            quantity7.setText(String.valueOf(totalCurrentDrugHeldVal));
                            break;
                        case R.id.tableRow8:
                            TextView quantity8 = (TextView) findViewById(R.id.quantity8);
                            quantity8.setText(String.valueOf(totalCurrentDrugHeldVal));
                            break;
                        case R.id.tableRow9:
                            TextView quantity9 = (TextView) findViewById(R.id.quantity9);
                            quantity9.setText(String.valueOf(totalCurrentDrugHeldVal));
                            break;
                        case R.id.tableRow10:
                            TextView quantity10 = (TextView) findViewById(R.id.quantity10);
                            quantity10.setText(String.valueOf(totalCurrentDrugHeldVal));
                            break;
                        case R.id.tableRow11:
                            TextView quantity11 = (TextView) findViewById(R.id.quantity11);
                            quantity11.setText(String.valueOf(totalCurrentDrugHeldVal));
                            break;
                    }
                }

                dialog.dismiss();
            }
        });
    }

    public void setHealth() {
        ImageView healthIcon = (ImageView) findViewById(R.id.imageView5);
        TextView currentHealthTxt = (TextView) findViewById(R.id.currentHealthValue);

        currentHealthTxt.setText(String.valueOf(currentHealthVal));

        if (currentHealthVal >= 50) {
            healthIcon.setBackgroundResource(R.drawable.hearticon1);
        } else {
            healthIcon.setBackgroundResource(R.drawable.hearticon2);
        }
    }

    public int averageArray(List<Integer> drugPurchaseArray) {
        int sum = 0;
        for (int i = 0; i < drugPurchaseArray.size(); i++) {
            sum += drugPurchaseArray.get(i);
        }

        return sum / drugPurchaseArray.size();
    }

    public int randomInt(int min, int max) {
        Random randomNum = new Random();
        return randomNum.nextInt((max - min) + 1) + min;
    }

    public void setupAllVariables() {
        // Create TextView variables
        TextView currentCity = (TextView) findViewById(R.id.cityHeader);
        TextView currentCashTxt = (TextView) findViewById(R.id.currentCashView);
        TextView currentDebtTxt = (TextView) findViewById(R.id.currentDebtView);
        TextView currentBankTxt = (TextView) findViewById(R.id.currentBankView);
        TextView currentDayTxt = (TextView) findViewById(R.id.currentDayView);
        TextView totalDaysTxt = (TextView) findViewById(R.id.totalDaysView);
        TextView cost1Txt = (TextView) findViewById(R.id.cost1);
        TextView cost2Txt = (TextView) findViewById(R.id.cost2);
        TextView cost3Txt = (TextView) findViewById(R.id.cost3);
        TextView cost4Txt = (TextView) findViewById(R.id.cost4);
        TextView cost5Txt = (TextView) findViewById(R.id.cost5);
        TextView cost6Txt = (TextView) findViewById(R.id.cost6);
        TextView cost7Txt = (TextView) findViewById(R.id.cost7);
        TextView cost8Txt = (TextView) findViewById(R.id.cost8);
        TextView cost9Txt = (TextView) findViewById(R.id.cost9);
        TextView cost10Txt = (TextView) findViewById(R.id.cost10);
        TextView cost11Txt = (TextView) findViewById(R.id.cost11);
        TextView totalItemsHel = (TextView) findViewById(R.id.currentDrugQuantityView);

        Button activityBtn = (Button) findViewById(R.id.activityBtn);

        ScrollView drugScrollView = (ScrollView) findViewById(R.id.scrollView);

        cities = getResources().getStringArray(R.array.cities);

        // Set display values to starting values
        currentCity.setText(String.valueOf(cities[currentCityNum]));
        currentCashTxt.setText(String.valueOf(currentCashVal));
        currentDebtTxt.setText(String.valueOf(currentDebtVal));
        currentBankTxt.setText(String.valueOf(currentBankVal));
        totalDaysTxt.setText(String.valueOf(totalDaysVal));
        currentDayTxt.setText(String.valueOf(currentDayVal));
        totalItemsHel.setText(String.valueOf(totalItemsHeld));

        if (drugValues == null) {
            // Set value of drugs
            cost1Txt.setText(String.valueOf(drug1Val));
            cost2Txt.setText(String.valueOf(drug2Val));
            cost3Txt.setText(String.valueOf(drug3Val));
            cost4Txt.setText(String.valueOf(drug4Val));
            cost5Txt.setText(String.valueOf(drug5Val));
            cost6Txt.setText(String.valueOf(drug6Val));
            cost7Txt.setText(String.valueOf(drug7Val));
            cost8Txt.setText(String.valueOf(drug8Val));
            cost9Txt.setText(String.valueOf(drug9Val));
            cost10Txt.setText(String.valueOf(drug10Val));
            cost11Txt.setText(String.valueOf(drug11Val));
        } else {
            cost1Txt.setText(String.valueOf(drugValues[0]));
            cost2Txt.setText(String.valueOf(drugValues[1]));
            cost3Txt.setText(String.valueOf(drugValues[2]));
            cost4Txt.setText(String.valueOf(drugValues[3]));
            cost5Txt.setText(String.valueOf(drugValues[4]));
            cost6Txt.setText(String.valueOf(drugValues[5]));
            cost7Txt.setText(String.valueOf(drugValues[6]));
            cost8Txt.setText(String.valueOf(drugValues[7]));
            cost9Txt.setText(String.valueOf(drugValues[8]));
            cost10Txt.setText(String.valueOf(drugValues[9]));
            cost11Txt.setText(String.valueOf(drugValues[10]));
        }

        switch(currentCityNum) {
            case 0: // North Tyneside
                // if no debt change button
                if (currentDebtVal == 0) {
                    activityBtn.setText(R.string.loanSharkTakeBtn);
                } else if (currentDebtVal > 0) {
                    activityBtn.setText(R.string.loanSharkPayBtn);
                }

                drugScrollView.setBackgroundResource(R.drawable.north_tyneside);
                break;
            case 1: // South Tyneside
                activityBtn.setText(R.string.guns);
                drugScrollView.setBackgroundResource(R.drawable.south_shields);
                break;
            case 2: // Sunderland
                activityBtn.setText(R.string.hospital);
                drugScrollView.setBackgroundResource(R.drawable.wearmouth_bridge__sunderland);
                break;
            case 3: // Gateshead
                // TODO make move button centered
                activityBtn.setVisibility(View.GONE);
                drugScrollView.setBackgroundResource(R.drawable.quasideback);
                break;
            case 4: // Newcastle Upon Tyne
                drugScrollView.setBackgroundResource(R.drawable.theatre_royal__newcastle_upon_tyne);
                activityBtn.setText(getString(R.string.bank));
                break;
        }

        // Update activity button text


        TableLayout drugTable = (TableLayout) findViewById(R.id.drugTable);

        // Loop through to set up current quantities once moved
        if (drugQuantities != null) {
            for (int i = 0; i < drugTable.getChildCount(); i++) {
                View view = drugTable.getChildAt(i);
                TableRow row = (TableRow) view;

                if (row != null) {
                    TextView currentQuantity = (TextView) row.getChildAt(0);
                    currentQuantity.setText(String.valueOf(drugQuantities[i]));
                }
            }
        }

        // HANDLE SETUP FOR WEAPONS AND ARMOUR
        TextView currentHealthValTxt = (TextView) findViewById(R.id.currentHealthValue);
        ImageView weaponImg = (ImageView) findViewById(R.id.weapon);

        setHealth();

        currentHealthValTxt.setText(String.valueOf(currentHealthVal));


        switch (currentWeapon) {
            case 2:
                weaponImg.setBackgroundResource(R.drawable.gun_pistol);
                break;
            case 3:
                weaponImg.setBackgroundResource(R.drawable.gun_rifle);
                break;
            case 4:
                weaponImg.setBackgroundResource(R.drawable.gun_smg);
                break;
            case 5:
                weaponImg.setBackgroundResource(R.drawable.gun_assault_rifle);
                break;
        }
    }

    public void setDividerStyle(Dialog dialog) {
        // Divider Styling
        int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
        View titleDivider = dialog.findViewById(titleDividerId);
        if (titleDivider != null)
            titleDivider.setBackgroundColor(getResources().getColor(R.color.colorAccent));
    }

    public void varyDrugsVisible() {
        // Get number of tableRows (= to numDrugs)
        TableLayout drugTable = (TableLayout) findViewById(R.id.drugTable);
        int totalNumDrugs = drugTable.getChildCount();

        if (drugVisible == null) {
            drugVisible = new int[totalNumDrugs];

            // for each drug
            for (int i = 0; i < totalNumDrugs; i++) {
                // get the relevant row
                View view = drugTable.getChildAt(i);
                TableRow row = (TableRow) view;
                TextView currentDrugCostTxt = (TextView) row.getChildAt(2);

                // random true / false
                int displayRow = randomInt(1, 10);
                // todo balance drug prices
                // if Random false
                if (displayRow > 7) {
                    // make sure
                    if (drugQuantities != null) {
                        if (drugQuantities[i] == 0) {
                            row.setVisibility(View.GONE);
                            drugVisible[i] = 0;
                        } else if (drugQuantities[i] > 0) {
                            currentDrugCostTxt.setText("---");
                        }
                    } else {
                        row.setVisibility(View.GONE);
                    }
                } else {
                    drugVisible[i] = 1;
                }
            }
        } else {
            for (int i = 0; i < totalNumDrugs; i++) {
                View view = drugTable.getChildAt(i);
                TableRow row = (TableRow) view;

                if (row != null) {
                    if (drugVisible[i] == 0) {
                        row.setVisibility(View.GONE);
                    } else {
                        row.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }
}
