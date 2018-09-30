package com.carbyke.carbyke;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chaos.view.PinView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.serhatsurguvec.continuablecirclecountdownview.ContinuableCircleCountDownView;

import java.util.Objects;
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
    private TextView statusText, wrong_number_tv;
    private RelativeLayout otpRelativeLayout;
    private SpinKitView spinKitView;
    private static final String countryCode = "+91";
    private ContinuableCircleCountDownView continuableCircleCountDownView;
    private static final int OTP_TIME = 60000;
    private FirebaseAuth firebaseAuth;

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
        firebaseAuth = FirebaseAuth.getInstance();
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
        wrong_number_tv = view.findViewById(R.id.pl_wrong_number_tv);
        statusText = view.findViewById(R.id.pl_otp_text);
        otpRelativeLayout = view.findViewById(R.id.pl_otpRelativeLayout);
        spinKitView = view.findViewById(R.id.pl_spin_kit);
        continuableCircleCountDownView = view.findViewById(R.id.pl_circleCountDownView);

        sendButton.setEnabled(false);
        verifyButton.setEnabled(false);
        resendButton.setEnabled(false);

        sendButton.setOnClickListener(this);
        resendButton.setOnClickListener(this);
        verifyButton.setOnClickListener(this);
        wrong_number_tv.setOnClickListener(this);
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

                setUpVerificationCallbacksSendCode();

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        Objects.requireNonNull(getActivity()),               // Activity (for callback binding)
                        verificationCallbacks);
        // ic

    }


//    to avoid abuse
    //when send button is used
    private void setUpVerificationCallbacksSendCode() {

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
                            new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                                    .title("Invalid credential")
                                    .titleColor(getResources().getColor(R.color.black))
                                    .content(e.getLocalizedMessage())
                                    .contentColorRes(R.color.black)
                                    .positiveText("Okay")
                                    .positiveColorRes(R.color.green)
                                    .backgroundColor(getResources().getColor(R.color.white))
                                    .icon(getResources().getDrawable(R.drawable.ic_warning))
                                    .show();
                            spinKitView.setVisibility(View.GONE);

                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // SMS quota exceeded
                            // Log.d(TAG, "SMS Quota exceeded.");
                            Toast.makeText(getActivity(), "Too many attempts, try after some time", Toast.LENGTH_SHORT).show();
                            spinKitView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {

                        spinKitView.setVisibility(View.GONE);
//                        disable edit text on code sent
                        phone_number_et.setEnabled(false);
                        wrong_number_tv.setVisibility(View.VISIBLE);
                        phone_number_et.setTextColor(Objects.requireNonNull(getActivity()).getResources().getColor(R.color.gray));

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


                        phoneVerificationId = verificationId;
                        resendToken = token;

                        continuableCircleCountDownView.setVisibility(View.VISIBLE);
                        continuableCircleCountDownView.setTimer(OTP_TIME);
                        continuableCircleCountDownView.setListener(new ContinuableCircleCountDownView.OnCountDownCompletedListener() {
                            @Override
                            public void onTick(long passedMillis) {
                                Log.w(TAG, "Tick." + passedMillis);

                            }

                            @Override
                            public void onCompleted() {
                                Log.w(TAG, "Completed.");
                                resendButton.setEnabled(true);
                            }
                        });
                        continuableCircleCountDownView.start();


                        // verifyButton.setEnabled(true);
                        sendButton.setEnabled(false);
                        //  resendButton.setEnabled(true);

                    }
                };
    }


//when re-send button is used
    private void setUpVerificationCallbacksReSendCode() {

        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(
                            PhoneAuthCredential credential) {

                        // signoutButton.setEnabled(true);
                        // statusText.setText("Signed In");
                        resendButton.setEnabled(false);
                        phone_number_et.setEnabled(false);
                        verifyButton.setEnabled(false);
                        wrong_number_tv.setVisibility(View.GONE);
                        codeText.setText("");

                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                                    .title("Invalid credential")
                                    .titleColor(getResources().getColor(R.color.black))
                                    .content(e.getLocalizedMessage())
                                    .contentColorRes(R.color.black)
                                    .positiveText("Okay")
                                    .positiveColorRes(R.color.green)
                                    .backgroundColor(getResources().getColor(R.color.white))
                                    .icon(getResources().getDrawable(R.drawable.ic_warning))
                                    .show();
                            spinKitView.setVisibility(View.GONE);

                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // SMS quota exceeded
                            // Log.d(TAG, "SMS Quota exceeded.");
                            Toast.makeText(getActivity(), "Too many attempts, try after some time", Toast.LENGTH_SHORT).show();
                            spinKitView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {

                        spinKitView.setVisibility(View.GONE);

                        String phoneNumber = phone_number_et.getText().toString();


                        phoneNumber = countryCode+phoneNumber;

                        statusText.setVisibility(View.VISIBLE);

                        continuableCircleCountDownView.cancel();
                        resendButton.setEnabled(false);
                        statusText.setText("New OTP has been re-sent to "+phoneNumber+". " +
                                "App will automatically detect the OTP if not please enter manually and verify.");

                        phoneVerificationId = verificationId;
                        resendToken = token;

                        continuableCircleCountDownView.setVisibility(View.VISIBLE);
                        continuableCircleCountDownView.setTimer(OTP_TIME);
                        continuableCircleCountDownView.setListener(new ContinuableCircleCountDownView.OnCountDownCompletedListener() {
                            @Override
                            public void onTick(long passedMillis) {
                                Log.w(TAG, "Tick." + passedMillis);

                            }

                            @Override
                            public void onCompleted() {
                                Log.w(TAG, "Completed.");
                                resendButton.setEnabled(true);
                            }
                        });
                        continuableCircleCountDownView.start();


                        // verifyButton.setEnabled(true);
                        sendButton.setEnabled(false);
                        //  resendButton.setEnabled(true);

                    }
                };
    }






    public void verifyCode() {
        //ic
                String code = codeText.getText().toString();
                PhoneAuthCredential credential =
                        PhoneAuthProvider.getCredential(phoneVerificationId, code);
                signInWithPhoneAuthCredential(credential);


    }

//    after verification, login with firebase with phone auth
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // signoutButton.setEnabled(true);
                            codeText.setText("");
                            //  statusText.setText("Signed In");
                            resendButton.setEnabled(false);
                            verifyButton.setEnabled(false);
                            //FirebaseUser user = task.getResult().getUser();
                            String user_uid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                            String phone_number = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber();

                            Toast.makeText(getActivity(), "Congratulations! Login Successful.", Toast.LENGTH_SHORT).show();


                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                //Toast.makeText(PhoneLogin.this, "Error: "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                                        .title("Error")
                                        .titleColor(getResources().getColor(R.color.black))
                                        .content(task.getException().getLocalizedMessage())
                                        .contentColorRes(R.color.black)
                                        .positiveText("Okay")
                                        .positiveColorRes(R.color.green)
                                        .backgroundColor(getResources().getColor(R.color.white))
                                        .icon(getResources().getDrawable(R.drawable.ic_warning))
                                        .show();
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    public void resendCode() {
        //ic
                String phoneNumber = phone_number_et.getText().toString();

                spinKitView.setVisibility(View.VISIBLE);

                phoneNumber = countryCode+phoneNumber;
                setUpVerificationCallbacksReSendCode();

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
//                wrong number
            case R.id.pl_wrong_number_tv:
                wrong_number_tv.setVisibility(View.GONE);
                phone_number_et.setText(null);
                phone_number_et.setEnabled(true);
                resendButton.setEnabled(false);
                sendButton.setEnabled(true);
                statusText.setVisibility(View.GONE);
                codeText.setText(null);
                otpRelativeLayout.setVisibility(View.GONE);
                phone_number_et.setTextColor(Color.BLACK);
                continuableCircleCountDownView.cancel();
                continuableCircleCountDownView.setVisibility(View.GONE);
                break;

        }

    }
    //    onclick


//    end
}
