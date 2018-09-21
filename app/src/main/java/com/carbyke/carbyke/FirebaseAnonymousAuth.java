package com.carbyke.carbyke;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.Context.MODE_PRIVATE;

public class FirebaseAnonymousAuth {
    SharedPreferences sharedPreferencesAnonymousAuth;
    private static final String PREF_AU = "pref_au";
    private static final String STATUS = "status";
    private FirebaseAuth mAuth;
    private Context context;

    FirebaseAnonymousAuth(Context context){
        this.context = context;
        sharedPreferencesAnonymousAuth = context.getSharedPreferences(PREF_AU, MODE_PRIVATE);
    }

    public void Auth() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("r000", "signInAnonymously:success");
                            SharedPreferences.Editor editor = sharedPreferencesAnonymousAuth.edit();
                            editor.putString(STATUS, "success");
                            editor.apply();;
                            //FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("r000", "signInAnonymously:failure", task.getException());

                        }
                    }
                });

    }
}
