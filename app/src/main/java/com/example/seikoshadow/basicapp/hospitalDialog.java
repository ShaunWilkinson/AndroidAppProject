package com.example.seikoshadow.basicapp;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Shaun on 27/08/2017.
 */

public class hospitalDialog extends AlertDialog implements View.OnClickListener {
    Context mContext;
    private int currentHealthVal;
    private int maxHealth;

    protected hospitalDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public int getCurrentHealthVal() {
        return currentHealthVal;
    }

    public void setCurrentHealthVal(int currentHealthVal) {
        this.currentHealthVal = currentHealthVal;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void handleDialog() {
        hospitalDialog dialog = new hospitalDialog(mContext);
        dialog.setTitle(mContext.getResources().getString(R.string.hospitalTitle));
        dialog.setContentView(R.layout.drug_dialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        // set proper dialog theme

        TextView description = (TextView) dialog.findViewById(R.id.dialogDescription);
        EditText input = (EditText) dialog.findViewById(R.id.dialogEditText);

        description.setText(mContext.getResources().getString(R.string.hospitalDescription));

        input.setText(String.valueOf(maxHealth - currentHealthVal));

        dialog.show();
    }

    // Handle button clicks
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buyBtn:
                break;
        }
    }
}
