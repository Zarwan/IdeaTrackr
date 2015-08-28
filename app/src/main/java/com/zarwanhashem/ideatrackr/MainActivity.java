package com.zarwanhashem.ideatrackr;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.plus.Plus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Main page; contains the list of existing ideas, and Google related tasks
 */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    //Shared preferences keys (consistent throughout all classes)
    public static final String IDEA_EDIT_KEY = "Edit";
    public static final String IDEA_ID_KEY = "ID";
    public static final String IDEA_DELETE_KEY = "Delete";
    public static final String CURR_IDEA_KEY = "CurrentIdea";
    private final String SIGNED_IN_KEY = "SignedIn";
    private final String NUM_IDEAS_KEY = "NumberOfIdeas";
    private final String IDEAS_KEY = "Ideas";

    private static final int RC_SIGN_IN = 0;

    private static List<Button> ideaButtons = new ArrayList<Button>(); //The buttons used to access ideas
    private static List<Idea> ideas = new ArrayList<Idea>(); //Stores created ideas
    private static SharedPreferences sharedPref;
    private static boolean signedIn = false;
    private Context myContext;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIsResolving = false;
    private boolean mShouldResolve = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myContext = getApplicationContext();

        sharedPref = myContext.getSharedPreferences("sharedPref", 0);

        //Load data from sharedPreferences
        if (ideaButtons.size() == 0 && sharedPref.contains(IDEAS_KEY)) loadIdeasFromSharedPreferences();
        if (sharedPref.contains(SIGNED_IN_KEY)) signedIn = sharedPref.getBoolean(SIGNED_IN_KEY, false);

        updateIdeas(getIntent());
        updateSharedPreferences(); //save the updated ideas into sharedPreferences

        //Output the list of ideas on the main page
        IdeaAdapter listAdapter = new IdeaAdapter(myContext, R.layout.idea_button, ideaButtons, ideas);
        ListView ideasListView = (ListView) findViewById(R.id.ideasListView);
        ideasListView.setAdapter(listAdapter);

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addScope(new Scope(Scopes.PLUS_LOGIN))
                .build();

        updateGoogleButtonVisibility();
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    private void loadIdeasFromSharedPreferences() {
        for (int i = 0; i < sharedPref.getInt(NUM_IDEAS_KEY, 0); i++) {
            ideaButtons.add(new Button(myContext));
        }

        String jsonIdeas = sharedPref.getString(IDEAS_KEY, null);
        Type typeOfListOfIdea = new TypeToken<ArrayList<Idea>>(){}.getType();
        ideas = new Gson().fromJson(jsonIdeas, typeOfListOfIdea);

        //If no ideas are found TypeToken gives us a null array instead of an empty one
        if (ideas == null) {
            ideas = new ArrayList<Idea>();
        }
    }

    public void onSignOutButtonClicked(View v) {
        if (getmGoogleApiClient() != null && getmGoogleApiClient().isConnected()) {
            Plus.AccountApi.clearDefaultAccount(getmGoogleApiClient());
            getmGoogleApiClient().disconnect();

            signedIn = false;
            saveSignedIn();
            updateGoogleButtonVisibility();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_button) {
            onSignInClicked();
        }
    }

    private void onSignInClicked() {
        if (getmGoogleApiClient() == null) {
            return;
        }
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        getmGoogleApiClient().connect();
    }

    public void onBackupIdeasButtonClicked(View v) {
        if (getmGoogleApiClient().isConnected()) {
            Drive.DriveApi.newDriveContents(getmGoogleApiClient())
                    .setResultCallback(driveContentsCallback);
        }
    }

    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new
            ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        return;
                    }
                    final DriveContents driveContents = result.getDriveContents();

                    // Perform I/O off the UI thread.
                    new Thread() {
                        @Override
                        public void run() {

                            // write content to DriveContents
                            OutputStream outputStream = driveContents.getOutputStream();
                            Writer writer = new OutputStreamWriter(outputStream);

                            try {
                                //Set the file contents

                                if (ideas.isEmpty()) {
                                    writer.write("You have no ideas!");

                                } else {
                                    for (Idea idea : ideas) {
                                        writer.write(idea.getTitle() + ": " + idea.getDetails() + "\n");
                                    }
                                }
                                writer.close();
                            } catch (IOException e) {
                                Log.e("DriveError", e.getMessage());
                            }

                            //Set the file properties (title, file type)

                            Calendar date = Calendar.getInstance();
                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle("Ideas: " + date.getTime())
                                    .setMimeType("text/plain")
                                    .setStarred(true).build();

                            // create a file on root folder
                            Drive.DriveApi.getRootFolder(getmGoogleApiClient())
                                    .createFile(getmGoogleApiClient(), changeSet, driveContents)
                                    .setResultCallback(fileCallback);
                        }
                    }.start();
                }
            };

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Toast.makeText(myContext, "Unable to create backup", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Toast.makeText(myContext, "Backup created successfully", Toast.LENGTH_LONG).show();
                }
            };

    public void onNewIdeaButtonClicked(View v) {
        saveSignedIn();
        Intent intent = new Intent(v.getContext(), NewIdeaPageActivity.class);
        startActivity(intent);
    }

    /**
     * Updates the stored ideas with changes made in other activities (new/edited ideas)
     * @param intent - The intent passed to MainActivity (call getIntent() in onCreate)
     */
    private void updateIdeas(Intent intent) {
        if (intent != null && intent.hasExtra(IDEA_EDIT_KEY)) {

            if (intent.getBooleanExtra(IDEA_EDIT_KEY, false)) {
                int id = intent.getIntExtra(IDEA_ID_KEY, -1);

                //Delete an idea
                if (intent.getBooleanExtra(IDEA_DELETE_KEY, false)) {
                    ideas.remove(id);
                    ideaButtons.remove(id);

                //Edit data of an existing idea
                } else {
                    Idea currIdea = new Gson().fromJson(intent.getStringExtra(CURR_IDEA_KEY), Idea.class);
                    ideas.get(id).setTitle(currIdea.getTitle());
                    ideas.get(id).setDetails(currIdea.getDetails());
                }

            //Add a new idea
            } else {
                Idea currIdea = new Gson().fromJson(intent.getStringExtra(CURR_IDEA_KEY), Idea.class);
                ideas.add(currIdea);
                ideaButtons.add(new Button(myContext));
            }
        }
    }

    /**
     * Updates the signed in variable in sharedPreferences
     */
    public void saveSignedIn() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(SIGNED_IN_KEY, signedIn);
        editor.apply();
    }

    /**
     * Updates the Google related buttons depending on whether the user is signed in or not
     * Google related buttons are: sign-in, sign-out, and backup ideas into Drive
     */
    private void updateGoogleButtonVisibility() {
        if (getmGoogleApiClient() != null && signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
            findViewById(R.id.back_up_ideas_button).setVisibility(View.VISIBLE);

        } else {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
            findViewById(R.id.back_up_ideas_button).setVisibility(View.GONE);
        }
    }

    /**
     * Saves the current ideas and signed in status into sharedPreferences
     */
    private void updateSharedPreferences() {
        SharedPreferences.Editor editor = sharedPref.edit();
        String json = new Gson().toJson(ideas);
        editor.putString(IDEAS_KEY, json);
        editor.putInt(NUM_IDEAS_KEY, ideaButtons.size());
        editor.putBoolean(SIGNED_IN_KEY, signedIn);
        editor.apply();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        mShouldResolve = false;
        signedIn = true;
        saveSignedIn();
        updateGoogleButtonVisibility();
        Toast.makeText(myContext, "Google account login successful", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    mIsResolving = false;
                    getmGoogleApiClient().connect();
                }
            } else {
                Toast.makeText(myContext, "Google login failed", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            getmGoogleApiClient().connect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getmGoogleApiClient() != null) getmGoogleApiClient().connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (getmGoogleApiClient() != null) getmGoogleApiClient().disconnect();
    }
}