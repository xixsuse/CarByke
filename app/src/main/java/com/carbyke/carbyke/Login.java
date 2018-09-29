package com.carbyke.carbyke;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
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
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import mehdi.sakout.fancybuttons.FancyButton;

public class Login extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private static final String TAG = "GoogleActivity";
    private FancyButton google_fb;
    private ImageButton cancel_ib;
    private SpinKitView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fetchIDs();
    }

//    getting ids
    private void fetchIDs() {
        google_fb = findViewById(R.id.al_google_fb);
        cancel_ib = findViewById(R.id.al_cancel_ib);

        google_fb.setOnClickListener(Login.this);
        cancel_ib.setOnClickListener(Login.this);
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
                Toast.makeText(this, "Success "+ Objects.requireNonNull(account).getDisplayName(), Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_login, new PhoneLogin()).addToBackStack("phoneLogin").commit();
                // firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                Toast.makeText(Login.this, "It seems like you have cancelled Google Authentication!", Toast.LENGTH_LONG).show();
               // rotateLoading.stop();
                // updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        // showProgressDialog();
       // rotateLoading.start();
       // gifImageView.setVisibility(View.VISIBLE);
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //  updateUI(user);
                            Toast.makeText(Login.this, "Success", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            showSignInFailedMessage((Objects.requireNonNull(task.getException())).getLocalizedMessage());
                        }

                        // [START_EXCLUDE]
                     //   rotateLoading.stop();

                        // hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]


    private void showSignInFailedMessage(String errorMessage){
        try{
            new MaterialDialog.Builder(Login.this)
                    .title("Sign in failed")
                    .content(Objects.requireNonNull(errorMessage))
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
//            if user register earlier but did not complete phone registration
            if (GoogleSignIn.getLastSignedInAccount(Login.this) != null){
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                //mGoogleApiClient.clearDefaultAccountAndReconnect();
            }
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
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


//    end
}
