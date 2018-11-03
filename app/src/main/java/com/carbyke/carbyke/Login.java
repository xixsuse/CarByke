package com.carbyke.carbyke;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import mehdi.sakout.fancybuttons.FancyButton;

public class Login extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private static final String TAG = "GoogleActivity";
    private FancyButton google_fb, phone_fb;
    private ImageButton cancel_ib;
    private SpinKitView sv;
    private SharedPreferences sharedPreferencesLoginMode;
    private final static String MODE = "mode";
    private final static String LOGIN_MODE = "login_mode";

    private final static String USER_PROFILES = "user_profiles";
    private final static String PROFILE = "profile";
    private final static String EMAIL = "email";
    private final static String PHONE_NUMBER = "phone_number";
    private final String NAME = "name";
    private final String GENDER = "gender";
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(USER_PROFILES);
    private DatabaseReference databaseReferenceUID;

    private SharedPreferences sharedPreferencesLogin;
    private final static String LOGIN = "login";
    private final static String LOGGED_IN_OR_NOT = "logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fetchIDs();

        initialiseFirebaseAuth();
        initialiseSharedPrefs();

    }

//    initializing firebase auth
    private void initialiseFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance();
    }
    //    initializing firebase auth


//    initialising shared preferences
    private void initialiseSharedPrefs(){
        sharedPreferencesLoginMode = getSharedPreferences(MODE, MODE_PRIVATE);
    }
    //    initialising shared preferences

//    getting ids
    private void fetchIDs() {
        google_fb = findViewById(R.id.al_google_fb);
        phone_fb = findViewById(R.id.al_phone_fb);
        cancel_ib = findViewById(R.id.al_cancel_ib);

        google_fb.setOnClickListener(Login.this);
        cancel_ib.setOnClickListener(Login.this);
        phone_fb.setOnClickListener(Login.this);
    }

    protected void onStart(){
        super.onStart();
        initiateGoogleSignInServices();
    }

    private void initiateGoogleSignInServices(){
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        String[] SCOPES = {Scopes.PROFILE};

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(Login.this)
                .enableAutoManage(Login.this/* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(Objects.requireNonNull(account));
            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                Toast.makeText(Login.this, "It seems like you have cancelled Google Authentication!", Toast.LENGTH_LONG).show();
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            saveUserProfileDataInFirebaseDataBase(Objects.requireNonNull(task.getResult()).getUser());
                        } else {
                            // If sign in fails, display a message to the user.
                            showSignInFailedMessage((Objects.requireNonNull(task.getException())).getLocalizedMessage());
                        }
                    }
                });


    }
    // [END auth_with_google]


    //  error message if login failed
    private void showSignInFailedMessage(String localizedMessage) {
        try{
            new MaterialDialog.Builder(Login.this)
                    .title("Sign in failed")
                    .content(Objects.requireNonNull(localizedMessage))
                    .contentColorRes(R.color.black)
                    .titleColor(getResources().getColor(R.color.googleRed))
                    .titleColor(getResources().getColor(R.color.black))
                    .positiveText("Try Again")
                    .positiveColorRes(R.color.black)
                    .backgroundColor(getResources().getColor(R.color.white))
                    .icon(getResources().getDrawable(R.drawable.ic_warning))
                    .show();
        }
        catch (NullPointerException e) { e.printStackTrace(); }
    }
    //  error message if login failed


    //    saving data at firebase database                                               //     saving data
    private void saveUserProfileDataInFirebaseDataBase(final FirebaseUser user){
        databaseReferenceUID = databaseReference.child(user.getUid()).child(PROFILE);

        databaseReferenceUID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // if name already saved then
                if (!dataSnapshot.hasChild(NAME)){
                    databaseReferenceUID.child(NAME).setValue(user.getDisplayName());
                }
                if (!dataSnapshot.hasChild(GENDER)){
                    databaseReferenceUID.child(GENDER).setValue("male");
                }
                databaseReferenceUID.child(EMAIL).setValue(user.getEmail()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        checkIfUserDetailsAreSaved(user);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showSignInFailedMessage(e.getLocalizedMessage());
                    }
                });
//        user is signed in so, need to save in prefs when phone linked
//                sharedPreferencesLogin = getSharedPreferences(LOGIN, MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferencesLogin.edit();
//                editor.putString(LOGGED_IN_OR_NOT, "true");
//                editor.apply();
                MySharedPrefs mySharedPrefs = new MySharedPrefs(Login.this);
                mySharedPrefs.setLoginPref("true");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                   showSignInFailedMessage(databaseError.getMessage());
            }
        });




    }
//    saving data at firebase database

    // if phone number is not linked then proceed to link number otherwise close login activity
    private void checkIfUserDetailsAreSaved(FirebaseUser user) {

        String phone_number = user.getPhoneNumber();
        if (TextUtils.isEmpty(phone_number)){
//            SharedPreferences.Editor editor = sharedPreferencesLoginMode.edit();
//            editor.putString(LOGIN_MODE, "google");
//            editor.apply();
            MySharedPrefs mySharedPrefs = new MySharedPrefs(Login.this);
            mySharedPrefs.setLoginMode("google");

            startActivity(new Intent(Login.this, PhoneLogin.class));
            Login.this.finish();
        }
        else {
            // save in shared pref that user is logged in and then exit this activity if second time log in, other wise prompt for phone auth link
            Login.this.finish();
        }

//        databaseReferenceUID.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (!dataSnapshot.hasChild(PHONE_NUMBER)){
//                            SharedPreferences.Editor editor = sharedPreferencesLoginMode.edit();
//                            editor.putString(LOGIN_MODE, "google");
//                            editor.apply();
//                            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_login, new PhoneLogin()).addToBackStack("phoneLogin").commit();
//                        }
//                        else {
//                            // save in shared pref that user is logged in and then exit this activity if second time log in, other wise prompt for phone auth link
//                            Login.this.finish();
//                        }
//                        sharedPreferencesLogin = getSharedPreferences(LOGIN, MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPreferencesLogin.edit();
//                        editor.putString(LOGGED_IN_OR_NOT, "true");
//                        editor.apply();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
    }
//  checkIfUserDetailsAreSaved



    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.stopAutoManage(Login.this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

//        google login
        if (id == R.id.al_google_fb){
            // sign out if any previous user logged in
            mAuth.signOut();
//            if user register earlier but did not complete phone registration
            if (GoogleSignIn.getLastSignedInAccount(Login.this) != null){
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                //mGoogleApiClient.clearDefaultAccountAndReconnect();
            }
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }


        //        phone login
        if (id == R.id.al_phone_fb){
//            SharedPreferences.Editor editor = sharedPreferencesLoginMode.edit();
//            editor.putString(LOGIN_MODE, "phone");
//            editor.apply();
            MySharedPrefs mySharedPrefs = new MySharedPrefs(Login.this);
            mySharedPrefs.setLoginMode("phone");
            // sign out if any previous user logged in
            mAuth.signOut();
//            listen for response from phone login activity to close login activity
            Intent intent = new Intent(Login.this,PhoneLogin.class);
            intent.putExtra("finisher", new ResultReceiver(null) {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    Login.this.finish();
                }
            });
            startActivityForResult(intent,1);
        }


//        cancel button
        else if (id == R.id.al_cancel_ib){
            Login.this.finish();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        try{
            new MaterialDialog.Builder(Login.this)
                    .title("Connection Failed")
                    .content(""+connectionResult)
                    .contentColorRes(R.color.black)
                    .titleColor(getResources().getColor(R.color.googleRed))
                    .titleColor(getResources().getColor(R.color.black))
                    .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS)
                    .positiveText("Okay")
                    .positiveColorRes(R.color.black)
                    .backgroundColor(getResources().getColor(R.color.white))
                    .icon(getResources().getDrawable(R.drawable.ic_cancel))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        }
                    })
                    .show();
        }
        catch (NullPointerException e) { e.printStackTrace(); }
    }


    public void onBackPressed() {
                super.onBackPressed();

            }


//    end
}
