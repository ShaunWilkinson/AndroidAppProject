package com.apps.seikoshadow.ganglanddealer;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.google.firebase.analytics.FirebaseAnalytics;



public class Menu extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static int RC_SIGN_IN = 9001;
    boolean mExplicitSignOut = false;
    boolean mInSignInFlow = false; // set to true when you're in the middle of the
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;
    // sign in flow, to know you should not attempt
    // to connect in onStart()
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Create the Google Api Client with access to the Play Games services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                // add other APIs and scopes here as needed
                .build();


        setContentView(R.layout.main_menu);

        MobileAds.initialize(this, "ca-app-pub-9371752022465917~3558234858");

        setupStyle();

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

    }

    public void setupStyle() {
        TextView title = (TextView) findViewById(R.id.titleTxt);
        Button newBtn = (Button) findViewById(R.id.newBtn);
        Button loadBtn = (Button) findViewById(R.id.loadBtn);
        Button leaderBtn = (Button) findViewById(R.id.leaderBoardBtn);
        Button changeBtn = (Button) findViewById(R.id.changeLogBtn);

        try {
            Typeface titleTypeface = Typeface.createFromAsset(getAssets(), "fonts/28 Days Later.ttf");
            title.setTypeface(titleTypeface);
            newBtn.setTypeface(titleTypeface);
            loadBtn.setTypeface(titleTypeface);
            leaderBtn.setTypeface(titleTypeface);
            changeBtn.setTypeface(titleTypeface);
        } catch (Exception e) {
            Log.e("myapp", "Couldn't load title font");
        }
        title.setBackground(ContextCompat.getDrawable(this, R.drawable.menu_title_gradient));

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button) {
            // start the asynchronous sign in flow
            mSignInClicked = true;
            mGoogleApiClient.connect();
        } else if (view.getId() == R.id.sign_out_button) {
            // sign out.
            mSignInClicked = false;
            mExplicitSignOut = true;
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                Games.signOut(mGoogleApiClient);
                mGoogleApiClient.disconnect();
            }

            // show sign-in button, hide the sign-out button
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mInSignInFlow && !mExplicitSignOut) {
            // auto sign in
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // show sign-out button, hide the sign-in button
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);

        // (your code here: update UI, enable functionality that depends on sign in, etc)
        handleSubmitScore();
    }

    public void handleSubmitScore() {
        // If user selected submitScore
        if (getIntent() != null && getIntent().getExtras() != null) {
            // make sure it was submit Score and not something else
            if (getIntent().getExtras().containsKey("score")) {

                mGoogleApiClient.connect();

                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    // Call a Play Games services API method, for example:
                    int score = getIntent().getExtras().getInt("score");
                    Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.leaderboard_best_dealers_in_the_world), score);
                    Toast.makeText(getApplicationContext(), getString(R.string.submittedScore), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.needToSignIn), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // already resolving
            return;
        }

        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, R.string.signin_other_error)) {
                mResolvingConnectionFailure = false;
            }
        }

        // Put code here to display the sign-in button
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in
                // failed. The R.string.signin_failure should reference an error
                // string in your strings.xml file that tells the user they
                // could not be signed in, such as "Unable to sign in."
                BaseGameUtils.showActivityResultError(this,
                        requestCode, resultCode, R.string.signin_failure);
            }
        }
    }

    // Call when the sign-in button is clicked
    private void signInClicked() {
        mSignInClicked = true;
        mGoogleApiClient.connect();
    }

    // Call when the sign-out button is clicked
    private void signOutclicked() {
        mSignInClicked = false;
        Games.signOut(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        mGoogleApiClient.connect();
    }


    // Go to main game screen
    public void newGame(View v) {
        Intent intent = new Intent(this, GameView.class);

        startActivity(intent);
    }

    public void loadGame(View v) {
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

    public void showChangeLog(View v) {
        Intent intent = new Intent(this, change_log.class);

        startActivity(intent);
    }

    public void showLeaderboard(View v) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startActivityForResult(
                    Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                            getString(R.string.leaderboard_best_dealers_in_the_world)), 0);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.needToSignIn), Toast.LENGTH_LONG).show();
        }
    }
}
