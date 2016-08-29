package au.com.jamesfrizelles.testdriveregister;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

import au.com.jamesfrizelles.testdriveregister.models.Drive;

public class DriverDetailsActivity extends BaseActivity {
    private Context context;
    private String TAG;
    private EditText licenceEditText;
    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_details);

        //initialize values
        context = DriverDetailsActivity.this;
        TAG = "DriverDetailsActivity";
        licenceEditText = (EditText) findViewById(R.id.licenceEditText);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);



        //toolbar settings
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        String toolBarTitle = "Driver Details";
        toolbar.setTitle(toolBarTitle);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        //Firebase authentication check
        initFirebaseAuth();
        addAuthStateListener(context);

    }

    public void onClickNext(View view){
        String drivername = nameEditText.getText().toString();
        String licence = licenceEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String email = emailEditText.getText().toString();

        if (!validateForm()) {
            return;
        }

        Drive drive = new Drive(drivername, licence, phone, email, getUid(), "Jame");
        Intent intent = new Intent(context, AgreementActivity.class);
        intent.putExtra("drive", drive);
        startActivityForResult(intent, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivity");
        if(requestCode == 200){
            switch (resultCode){
                case RESULT_OK:
                    setResult(RESULT_OK);
                    finish();
                    break;
                case RESULT_CANCELED:
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.menu:
                break;
            case R.id.menu_logout:
                signOut();
                break;
        }

        return super.onOptionsItemSelected(item);
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

        String phone = phoneEditText.getText().toString();
        if (TextUtils.isEmpty(phone)){
            phoneEditText.setError("Required");
            valid = false;
        }else{
            phoneEditText.setError(null);
        }

        String licence = licenceEditText.getText().toString();
        if (TextUtils.isEmpty(licence)) {
            licenceEditText.setError("Required.");
            valid = false;
        } else {
            licenceEditText.setError(null);
        }

        String name = nameEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Required.");
            valid = false;
        } else {
            nameEditText.setError(null);
        }

        return valid;
    }

    @Override
    public void onStart() {
        super.onStart();
        addAuthStateListener(context);
    }

    @Override
    public void onStop() {
        super.onStop();
       removeAuthStateListener();
    }

}
