package com.apps.seikoshadow.ganglanddealer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by Shaun on 10/09/2017.
 */

public class boomOrBustDialog extends AlertDialog {
    Context mContext;
    private int boomOrBust;
    private int currentDrugVal;
    private int boomOrBustValue;
    private int newDrugValue;
    private int restoreEvent;
    private boomOrBustDialog dialog;

    protected boomOrBustDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    // Getters
    public int getCurrentDrugVal() {
        return currentDrugVal;
    }

    // Setters
    public void setCurrentDrugVal(int currentDrugVal) {
        this.currentDrugVal = currentDrugVal;
    }

    public int getBoomOrBustValue() {
        return boomOrBustValue;
    }

    public void setBoomOrBustValue(int boomOrBustValue) {
        this.boomOrBustValue = boomOrBustValue;
    }

    public int getNewDrugValue() {
        return newDrugValue;
    }

    public void setNewDrugValue(int newDrugValue) {
        this.newDrugValue = newDrugValue;
    }

    public int getRestoreEvent() {
        return restoreEvent;
    }

    public void setNewRestoreEvent(int restoreEvent) {
        this.restoreEvent = restoreEvent;
    }

    public void handleDialog(int drugCount, TableLayout drugTable) {
        try {
            View dialogView = View.inflate(getContext(), R.layout.events, null);
            Context context = getContext();

            dialog = new boomOrBustDialog(mContext);
            dialog.setTitle(mContext.getResources().getString(R.string.event));
            dialog.setContentView(R.layout.drug_dialog);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setView(dialogView);

            dialog.show();
            setDividerStyle(dialog);

            if (boomOrBust == 0)
                boomOrBust = randomInt(1, 2);

            // ========== Drugs worth more ===========

            TextView descriptionTwo = (TextView) dialogView.findViewById(R.id.descTwo);
            Button firstBtn = (Button) dialogView.findViewById(R.id.button);
            Button secondBtn = (Button) dialogView.findViewById(R.id.button2);
            TextView dialogDescription = (TextView) dialogView.findViewById(R.id.descOne);
            TextView drugName;
            TextView drugValueTxt;
            TableRow drugRow;

            int x = 0;
            int whichDrug = 0;
            int priceChangePercentage = randomInt(50, 80);

            descriptionTwo.setVisibility(View.GONE);
            firstBtn.setVisibility(View.GONE);
            secondBtn.setText(context.getString(R.string.cancel));

            // define whichDrug
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
            drugName.setTypeface(null, Typeface.BOLD);
            drugValueTxt.setTypeface(null, Typeface.BOLD);

            // if drug value doesn't equal ---
            if (!drugValueTxt.getText().toString().equals("---")) {
                currentDrugVal = Integer.parseInt(drugValueTxt.getText().toString());
            }

            if (boomOrBustValue == 0)
                boomOrBustValue = (currentDrugVal / 100) * priceChangePercentage;

            if (boomOrBust == 1) {
                dialog.setTitle(getContext().getString(R.string.drugBoom));
                newDrugValue = currentDrugVal + boomOrBustValue;
                drugName.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
                drugValueTxt.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
                dialogDescription.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
                dialogDescription.setText(context.getString(R.string.drugBoomDesc, drugName.getText().toString()));
            } else if (boomOrBust == 2) {
                dialog.setTitle(context.getString(R.string.drugBust));
                newDrugValue = currentDrugVal - boomOrBustValue;
                drugName.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
                drugValueTxt.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
                dialogDescription.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
                dialogDescription.setText(context.getString(R.string.drugBustDesc, drugName.getText().toString(), drugName.getText().toString()));
            }

            drugValueTxt.setText(String.valueOf(newDrugValue));
            restoreEvent = 2;

            secondBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            dialog.dismiss();
            Log.e("myapp", "failed to load event in time " + e.getMessage());
        }
    }

    private int randomInt(int min, int max) {
        Random randomNum = new Random();
        return randomNum.nextInt((max - min) + 1) + min;
    }

    private void setDividerStyle(Dialog dialog) {
        // Divider Styling
        int titleDividerId = getContext().getResources().getIdentifier("titleDivider", "id", "android");
        View titleDivider = dialog.findViewById(titleDividerId);
        if (titleDivider != null)
            titleDivider.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
    }
}
