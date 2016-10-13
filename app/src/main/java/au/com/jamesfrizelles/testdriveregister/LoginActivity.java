package au.com.jamesfrizelles.testdriveregister;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import au.com.jamesfrizelles.testdriveregister.models.User;

public class LoginActivity extends BaseActivity {
    private Context context;
    private String TAG;
    private EditText emailEditText;
    private EditText passwordEditText;
    private FirebaseAuth firebaseAuth;
    private boolean doubleBackToExitPressedOnce;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initialize values
        context = LoginActivity.this;
        TAG = "LoginActivity";
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        emailEditText.setSelected(false);
        passwordEditText.setSelected(false);
        doubleBackToExitPressedOnce = false;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //get firebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void signIn(){
        String email = emailEditText.getText().toString(),
                password = passwordEditText.getText().toString();

        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        OnCompleteListener<AuthResult> signInOnCompleteListener = new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithEmail:failed", task.getException());
                    Toast.makeText(context, R.string.auth_failed,
                            Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }else{
                    Log.i(TAG, "signInWithEmail:succeed", task.getException());
                    //get user data from Firebase database
                    final String userId = getUid();
                    mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // Get user value
                                    User user = dataSnapshot.getValue(User.class);
                                    if(user.enabled){
                                        Toast.makeText(context, R.string.sign_in_succeeded,
                                                Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(context, ProfileActivity.class);
                                        intent.putExtra("user", user);
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        Toast.makeText(context, R.string.auth_failed,
                                                Toast.LENGTH_SHORT).show();
                                    }

                                    hideProgressDialog();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                    Toast.makeText(context, R.string.database_error,
                                            Toast.LENGTH_SHORT).show();
                                    hideProgressDialog();
                                }
                            });

                    hideProgressDialog();
                }
            }
        };

        //Sign in with email and password
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, signInOnCompleteListener);
    }

    //Form validation
    private boolean validateForm() {
        boolean valid = true;

        String email = emailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Required.");
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Required.");
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        return valid;
    }

    public void onClickSignIn(View view){
        signIn();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);

    }

    public void onClickResetPassword(View view){
        Intent intent = new Intent(context, PasswordResetActivity.class);
        startActivity(intent);
    }
}
