package com.carbyke.carbyke;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.serhatsurguvec.continuablecirclecountdownview.ContinuableCircleCountDownView;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import mehdi.sakout.fancybuttons.FancyButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhoneLogin extends Fragment implements View.OnClickListener{

    private View view;

    private static final String TAG = "PhoneAuth";

    private EditText phone_number_et;
    private PinView codeText;
    private FancyButton verifyButton;
    private FancyButton sendButton;
    private FancyButton resendButton;
    private Button signoutButton;
    private TextView statusText;
    private RelativeLayout otpRelativeLayout;
    private SpinKitView spinKitView;
    private static final String countryCode = "+91";
    private int otp_ticker;
    private ContinuableCircleCountDownView continuableCircleCountDownView;

    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;


    public PhoneLogin() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_phone_login, container, false);
        fetchIDs();
        TextWatcher();
        return view;
    }

//    text watcher
    private void TextWatcher() {
        phone_number_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 10){
                    sendButton.setEnabled(true);
                }
                else sendButton.setEnabled(false);
                // TODO Auto-generated method stub
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



        codeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 6){
                    verifyButton.setEnabled(true);
                }
                else verifyButton.setEnabled(false);
                // TODO Auto-generated method stub
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



    }
//    text watcher

    //    getting ids
    private void fetchIDs() {
        phone_number_et = view.findViewById(R.id.phoneText);
        codeText = view.findViewById(R.id.codeText);
        verifyButton = view.findViewById(R.id.verifyButton);
        sendButton = view.findViewById(R.id.sendButton);
        resendButton = view.findViewById(R.id.resendButton);
       // signoutButton = view.findViewById(R.id.signoutButton);
        statusText = view.findViewById(R.id.pl_otp_text);
        otpRelativeLayout = view.findViewById(R.id.pl_otpRelativeLayout);
        spinKitView = view.findViewById(R.id.pl_spin_kit);
        continuableCircleCountDownView = view.findViewById(R.id.pl_circleCountDownView);

        sendButton.setEnabled(false);
        verifyButton.setEnabled(false);
        resendButton.setEnabled(false);

//        signoutButton.setEnabled(false);
       // statusText.setText("Signed Out");
        sendButton.setOnClickListener(this);
        resendButton.setOnClickListener(this);
        verifyButton.setOnClickListener(this);
    }

    public void onStart(){
        super.onStart();
    }

    public void sendCode() {
        // ic
                String phoneNumber = phone_number_et.getText().toString();

                if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 10){
                    Toast.makeText(getActivity(), "incorrect Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                spinKitView.setVisibility(View.VISIBLE);
                phoneNumber = countryCode+phoneNumber;

                setUpVerificationCallbacks();

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        Objects.requireNonNull(getActivity()),               // Activity (for callback binding)
                        verificationCallbacks);
        // ic


    }

    private void setUpVerificationCallbacks() {

        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(
                            PhoneAuthCredential credential) {

                       // signoutButton.setEnabled(true);
                        // statusText.setText("Signed In");
                        resendButton.setEnabled(false);
                        verifyButton.setEnabled(false);
                        codeText.setText("");

                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            //  Log.d(TAG, "Invalid credential: "
                            //         + e.getLocalizedMessage());
//                            new SweetAlertDialog(PhoneLogin.this, SweetAlertDialog.ERROR_TYPE)
//                                    .setTitleText("Invalid credential")
//                                    .setContentText(e.getLocalizedMessage())
//                                    .show();
                            spinKitView.setVisibility(View.GONE);

                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // SMS quota exceeded
                            Log.d(TAG, "SMS Quota exceeded.");
                            Toast.makeText(getActivity(), "SMS Quota exceeded.", Toast.LENGTH_SHORT).show();
                            spinKitView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {

                        spinKitView.setVisibility(View.GONE);
                        otpRelativeLayout.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.SlideInUp)
                                .duration(700)
                                .repeat(0)
                                .playOn(otpRelativeLayout);

                        String phoneNumber = phone_number_et.getText().toString();


                        phoneNumber = countryCode+phoneNumber;

                        statusText.setVisibility(View.VISIBLE);
                        statusText.setText("OTP has been sent to "+phoneNumber+". " +
                                "App will automatically detect the OTP if not please enter manually and verify.");

//                        SuperActivityToast.create(PhoneLogin.this, new Style())
//                                .setProgressBarColor(Color.BLACK)
//                                .setText("  OTP Sent to "+phoneNumber)
//                                .setDuration(Style.DURATION_LONG)
//                                .setIconResource(R.drawable.ic_info)
//                                .setFrame(Style.FRAME_STANDARD)
//                                .setColor(getResources().getColor(R.color.black90))
//                                .setAnimations(Style.ANIMATIONS_POP).show();

                        phoneVerificationId = verificationId;
                        resendToken = token;

                        continuableCircleCountDownView.setVisibility(View.VISIBLE);
                        continuableCircleCountDownView.setTimer(10000);
                        continuableCircleCountDownView.setListener(new ContinuableCircleCountDownView.OnCountDownCompletedListener() {
                            @Override
                            public void onTick(long passedMillis) {
                                Log.w(TAG, "Tick." + passedMillis);

                            }

                            @Override
                            public void onCompleted() {
                                Log.w(TAG, "Completed.");
                                resendButton.setEnabled(true);
                                continuableCircleCountDownView.setVisibility(View.GONE);
                            }
                        });
                        continuableCircleCountDownView.start();

//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                resendButton.setEnabled(true);
//                            }
//                        },5000);




                       // verifyButton.setEnabled(true);
                        sendButton.setEnabled(false);
                      //  resendButton.setEnabled(true);

                    }
                };
    }

    public void verifyCode() {

        //ic
                String code = codeText.getText().toString();

                if (TextUtils.isEmpty(code) || code.length() < 6){
//                    new SweetAlertDialog(PhoneLogin.this, SweetAlertDialog.ERROR_TYPE)
//                            .setTitleText("Oops...")
//                            .setContentText("enter full code!")
//                            .show();
                    return;
                }

                PhoneAuthCredential credential =
                        PhoneAuthProvider.getCredential(phoneVerificationId, code);
                signInWithPhoneAuthCredential(credential);


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        fbAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // signoutButton.setEnabled(true);
//                            codeText.setText("");
//                            //  statusText.setText("Signed In");
//                            resendButton.setEnabled(false);
//                            verifyButton.setEnabled(false);
//                            //FirebaseUser user = task.getResult().getUser();
//                            String user_uid = Objects.requireNonNull(fbAuth.getCurrentUser()).getUid();
//                            String phone_number = Objects.requireNonNull(fbAuth.getCurrentUser()).getPhoneNumber();
//
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                            editor.putString(FIRST_SCREEN, PROFILE_SETUP);
//                            editor.putString(MY_UID, user_uid);
//                            editor.apply();
//
//
//
//                            databaseReference.child(user_uid).child("uid").setValue(user_uid);
//                            databaseReference.child(user_uid).child("phone_number").setValue(phone_number);
//                            DatabaseReference secret = FirebaseDatabase.getInstance().getReference();
//                            secret.child("registered_contacts").child(phone_number).setValue(user_uid);
//
//                            startActivity(new Intent(PhoneLogin.this, ProfileSetup.class));
//                            PhoneLogin.this.finish();
//
//
//                        } else {
//                            if (task.getException() instanceof
//                                    FirebaseAuthInvalidCredentialsException) {
//                                //Toast.makeText(PhoneLogin.this, "Error: "+task.getException().toString(), Toast.LENGTH_SHORT).show();
//                                new SweetAlertDialog(PhoneLogin.this, SweetAlertDialog.ERROR_TYPE)
//                                        .setTitleText("Error")
//                                        .setContentText(task.getException().getLocalizedMessage())
//                                        .show();
//                                // The verification code entered was invalid
//                            }
//                        }
//                    }
//                });
    }

    public void resendCode() {

        //ic
                String phoneNumber = phone_number_et.getText().toString();

                if (TextUtils.isEmpty(countryCode)){
//                    new SweetAlertDialog(PhoneLogin.this, SweetAlertDialog.ERROR_TYPE)
//                            .setTitleText("Oops...")
//                            .setContentText("Choose your Country!")
//                            .show();
                    return;
                }

                if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 10){
//                    new SweetAlertDialog(PhoneLogin.this, SweetAlertDialog.ERROR_TYPE)
//                            .setTitleText("Oops...")
//                            .setContentText("Incorrect Mobile Number! ")
//                            .show();
                    return;
                }

                spinKitView.setVisibility(View.VISIBLE);
                phoneNumber = countryCode+phoneNumber;
                setUpVerificationCallbacks();

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,
                        60,
                        TimeUnit.SECONDS,
                        Objects.requireNonNull(getActivity()),
                        verificationCallbacks,
                        resendToken);




    }

//    onclick
    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
//            send code button
            case R.id.sendButton:
                sendCode();
                break;
//            verify code
            case R.id.verifyButton:
                verifyCode();
                break;
//            resend code
            case R.id.resendButton:
                resendCode();
                break;

        }

    }
    //    onclick


//    end
}
