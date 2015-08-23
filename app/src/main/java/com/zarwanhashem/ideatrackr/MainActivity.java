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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.plus.Plus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Main page; contains list of existing ideas
 */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    public static final String IDEA_TITLE_KEY = "Title";
    public static final String IDEA_DETAILS_KEY = "Details";
    public static final String IDEA_EDIT_KEY = "Edit";
    public static final String IDEA_ID_KEY = "ID";
    public static final String IDEA_DELETE_KEY = "Delete";
    public static final String SIGNED_IN_KEY = "SignedIn";
    private final String IDEAS_KEY = "Ideas";
    private final String IDEA_DATA_KEY = "IdeaData";
    private static final int RC_SIGN_IN = 0;

    private static List<Button> ideas = new ArrayList<Button>();
    private static List<List<String>> ideaData = new ArrayList<List<String>>(); //each index is made up of a list; first index of inner list is title, second is details
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

        //Load from sharedPref if restoring session
        if (ideas.size() == 0 && sharedPref.contains(IDEAS_KEY)) loadFromSharedPreferences();
        if (sharedPref.contains(SIGNED_IN_KEY)) signedIn = sharedPref.getBoolean(SIGNED_IN_KEY, false);

        updateIdeas(getIntent());
        updateSharedPreferences();


        IdeaAdapter listAdapter = new IdeaAdapter(myContext, R.layout.idea_button, ideas, ideaData);
        ListView ideasListView = (ListView) findViewById(R.id.ideasListView);
        ideasListView.setAdapter(listAdapter);

        View signInBttnView = findViewById(R.id.sign_in_button);
        signInBttnView.setOnClickListener(this);

        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addScope(new Scope(Scopes.PLUS_LOGIN))
                .build();

        updateButtonVisibility();
    }

    private void loadFromSharedPreferences() {
        for (int i = 0; i < sharedPref.getInt(IDEAS_KEY, 0); i++) {
            ideas.add(new Button(myContext));
        }
        String dataValue = sharedPref.getString(IDEA_DATA_KEY, null);
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();
        ideaData = gson.fromJson(dataValue, ArrayList.class);
    }

    public void onSignOutButtonClicked(View v) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();

            signedIn = false;
            updateSignedIn();

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
            findViewById(R.id.back_up_ideas_button).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_button) {
            onSignInClicked();
        }
    }

    private void onSignInClicked() {

        if (mGoogleApiClient == null) {
            return;
        }
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();

        // Show a message to the user that we are signing in.
    }

    public void onBackupIdeasButtonClicked(View v) {
        if (mGoogleApiClient.isConnected()) {
            Drive.DriveApi.newDriveContents(mGoogleApiClient)
                    .setResultCallback(driveContentsCallback);
        }
    }

    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new
            ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.w("MyTag", "Error while trying to create new file contents");
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
                                writer.write("Hello World!");
                                writer.close();
                            } catch (IOException e) {
                                Log.e("MyTag", e.getMessage());
                            }

                            Calendar date = Calendar.getInstance();
                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle("Ideas: " + date.getTime())
                                    .setMimeType("text/plain")
                                    .setStarred(true).build();

                            // create a file on root folder
                            Drive.DriveApi.getRootFolder(mGoogleApiClient)
                                    .createFile(mGoogleApiClient, changeSet, driveContents)
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
                        Log.w("MyTag", "Error while trying to create the file");
                        return;
                    }
                    Log.d("MyTag", "Created a file with content: " + result.getDriveFile().getDriveId());
                }
            };

    public void onNewIdeaButtonClicked(View v) {
        updateSignedIn();

        Intent intent = new Intent(v.getContext(), NewIdeaPageActivity.class);
        startActivity(intent);
    }

    private void updateIdeas(Intent intent) {
        if (intent != null && intent.hasExtra(IDEA_EDIT_KEY)) {

            if (intent.getBooleanExtra(IDEA_EDIT_KEY, false)) {
                int id = intent.getIntExtra(IDEA_ID_KEY, -1);

                //Delete an idea
                if (intent.getBooleanExtra(IDEA_DELETE_KEY, false)) {
                    ideaData.remove(id);
                    ideas.remove(id);

                //Edit data of an existing idea
                } else {
                    ideaData.get(id).set(0, intent.getStringExtra(IDEA_TITLE_KEY));
                    ideaData.get(id).set(1, intent.getStringExtra(IDEA_DETAILS_KEY));
                }

            //Add a new idea
            } else {
                List<String> data = new ArrayList<String>();
                data.add(intent.getStringExtra(IDEA_TITLE_KEY));
                data.add(intent.getStringExtra(IDEA_DETAILS_KEY));
                ideaData.add(data);
                ideas.add(new Button(myContext));
            }
        }
    }

    public void updateSignedIn() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(SIGNED_IN_KEY, signedIn);
        editor.apply();
    }

    private void updateButtonVisibility() {
        if (mGoogleApiClient != null && signedIn) {
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
            findViewById(R.id.back_up_ideas_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        } else {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
            findViewById(R.id.back_up_ideas_button).setVisibility(View.GONE);
        }
    }

    private void updateSharedPreferences() {
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(ideaData);
        editor.putString(IDEA_DATA_KEY, json);
        editor.putInt(IDEAS_KEY, ideas.size());
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
        updateSignedIn();

        // Show the signed-in UI
        findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_up_ideas_button).setVisibility(View.VISIBLE);
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

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
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
            }
        } else {
            // Show the signed-out UI
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }
}