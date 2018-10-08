package com.carbyke.carbyke;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class PhoneToGoogle extends Fragment {

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

    public PhoneToGoogle() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_phone_to_google, container, false);
        fetchIDs();
        onclick();
        return view;
    }

    public void onStart(){
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
        mGoogleApiClient = new GoogleApiClient.Builder(Objects.requireNonNull(getActivity()))
                .enableAutoManage(getActivity()/* FragmentActivity */, (GoogleApiClient.OnConnectionFailedListener) getActivity() /* OnConnectionFailedListener */)
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
                Toast.makeText(getActivity(), "It seems like you have cancelled Google Authentication!", Toast.LENGTH_LONG).show();
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

        mAuth.signInWithCredential(credential)
                //Objects.requireNonNull(mAuth.getCurrentUser()).linkWithCredential(credential)
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //  updateUI(user);
                            saveUserProfileDataInFirebaseDataBase(task.getResult().getUser());
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

    //    saving data at firebase database
    private void saveUserProfileDataInFirebaseDataBase(final FirebaseUser user){
        databaseReference.child(NAME).setValue(user.getDisplayName());
        Objects.requireNonNull(getActivity()).finish();
    }
//    saving data at firebase database


    private void showSignInFailedMessage(String errorMessage){
        try{
            new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
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

            }
        });

        skip_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).finish();
            }
        });
    }

    private void fetchIDs() {
        signed_in_message_tv = view.findViewById(R.id.tg_signed_in_message_tv);
        google_sign_in_fb = view.findViewById(R.id.tg_google_fb);
        skip_tv = view.findViewById(R.id.tg_skip_tv);

        setMessage();
    }

    private void setMessage() {
        mAuth = FirebaseAuth.getInstance();
        String phone_number = Objects.requireNonNull(mAuth.getCurrentUser()).getPhoneNumber();
        signed_in_message_tv.setText(String.format("You're signed-in as %s. Link your mail account so that next time you can sign in with email or phone number.", phone_number));
    }

//    end
}
