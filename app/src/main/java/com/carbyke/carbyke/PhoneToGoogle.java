package com.carbyke.carbyke;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import mehdi.sakout.fancybuttons.FancyButton;

public class PhoneToGoogle extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String NAME = "name";
    private static final String USER_PROFILES = "user_profiles";
    private View view;
    private TextView signed_in_message_tv, skip_tv;
    private FancyButton google_sign_in_fb;

    private FirebaseAuth mAuth;

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "GoogleActivity";

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(USER_PROFILES);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phoneto_google);
        signed_in_message_tv = findViewById(R.id.tg_signed_in_message_tv);
        google_sign_in_fb = findViewById(R.id.tg_google_fb);
        skip_tv = findViewById(R.id.tg_skip_tv);

        mAuth = FirebaseAuth.getInstance();

        onclick();
    }
    public void onStart(){
        super.onStart();
        setMessage();
        initiateGoogleSignInServices();
    }


    private void initiateGoogleSignInServices(){
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(PhoneToGoogle.this)
                .enableAutoManage(PhoneToGoogle.this/* FragmentActivity */, this /* OnConnectionFailedListener */)
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
                Toast.makeText(this, ""+account.getDisplayName()+" "+account.getPhotoUrl(), Toast.LENGTH_SHORT).show();
            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                Toast.makeText(PhoneToGoogle.this, "It seems like you have cancelled Google Authentication!", Toast.LENGTH_LONG).show();
                // rotateLoading.stop();
                // updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);


        Objects.requireNonNull(mAuth.getCurrentUser()).linkWithCredential(credential)
                //Objects.requireNonNull(mAuth.getCurrentUser()).linkWithCredential(credential)
                .addOnCompleteListener(PhoneToGoogle.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //  updateUI(user);
                            saveUserProfileDataInFirebaseDataBase(Objects.requireNonNull(task.getResult()).getUser());
                            Toast.makeText(PhoneToGoogle.this, "linked", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            showSignInFailedMessage((Objects.requireNonNull(task.getException())).getLocalizedMessage());
                        }
                    }
                });
    }
    // [END auth_with_google]

    //    saving data at firebase database
    private void saveUserProfileDataInFirebaseDataBase(final FirebaseUser user){
        mAuth = FirebaseAuth.getInstance();
        String name = Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName();
        Toast.makeText(this, "s "+name, Toast.LENGTH_SHORT).show();

        databaseReference.child(user.getUid()).child(NAME).setValue(name);
        //PhoneToGoogle.this.finish();
    }
//    saving data at firebase database


    private void showSignInFailedMessage(String errorMessage){
        try{
            new MaterialDialog.Builder(PhoneToGoogle.this)
                    .title("Sign in failed")
                    .content(Objects.requireNonNull(errorMessage))
                    .contentColorRes(R.color.black)
                    .titleColor(getResources().getColor(R.color.googleRed))
                    .titleColor(getResources().getColor(R.color.black))
                    .positiveText("Okay")
                    .positiveColorRes(R.color.black)
                    .backgroundColor(getResources().getColor(R.color.white))
                    .icon(getResources().getDrawable(R.drawable.ic_warning))
                    .show();
        }
        catch (NullPointerException e) { e.printStackTrace(); }

    }



    private void onclick() {
        google_sign_in_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //            if user register earlier but did not complete phone registration
                if (GoogleSignIn.getLastSignedInAccount(PhoneToGoogle.this) != null){
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                    mGoogleApiClient.clearDefaultAccountAndReconnect();
                }
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        skip_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneToGoogle.this.finish();
            }
        });
    }


    private void setMessage() {

        String phone_number = null;
        try {
            phone_number = mAuth.getCurrentUser().getPhoneNumber();
        } catch (NullPointerException e) {
            Toast.makeText(PhoneToGoogle.this, "exception", Toast.LENGTH_SHORT).show();
        }
        signed_in_message_tv.setText(String.format("You're signed-in with phone number %s. Link your google account so that next time you can sign in with email or phone number.", phone_number));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        try{
            new MaterialDialog.Builder(PhoneToGoogle.this)
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
