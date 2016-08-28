package au.com.jamesfrizelles.testdriveregister;

import android.content.Context;
import android.content.Intent;
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

public class LoginActivity extends BaseActivity {
    private Context context;
    private String TAG;
    private EditText emailEditText;
    private EditText passwordEditText;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = LoginActivity.this;
        TAG = "LoginActivity";

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        emailEditText.setSelected(false);
        passwordEditText.setSelected(false);

        //get firebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void signin(){
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
                }else{
                    Log.i(TAG, "signInWithEmail:succeed", task.getException());
                    Toast.makeText(context, R.string.sign_in_succeeded,
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
                hideProgressDialog();
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
        signin();
    }
}
