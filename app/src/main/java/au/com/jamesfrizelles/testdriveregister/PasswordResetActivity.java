package au.com.jamesfrizelles.testdriveregister;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends BaseActivity {
    private Context context;
    private String TAG;
    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        //initialize values
        context = PasswordResetActivity.this;
        TAG = "PasswordResetActivity";
        emailEditText = (EditText) findViewById(R.id.passwordResetEmailEditText);
    }

    public void onClickCancelPasswordReset(View view){
        finish();
    }

    public void onClickSendEmail(View view){
        String email = emailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Required.");
            return;
        } else {
            emailEditText.setError(null);
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();

        showProgressDialog();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, R.string.email_sent,
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(context, R.string.email_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }
}
