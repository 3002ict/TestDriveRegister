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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.api.model.StringList;

import java.io.Serializable;

import au.com.jamesfrizelles.testdriveregister.models.Drive;
import au.com.jamesfrizelles.testdriveregister.models.User;

public class DriverDetailsActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {
    private Context context;
    private String TAG;
    private EditText licenceEditText;
    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText emailEditText;
    private EditText regoEditText;
    private EditText streetEditText;
    private EditText suburbEditText;
    private EditText carModelEditText;
    private EditText postcodeEditText;
    private Spinner stateSpinner;
    private Spinner carMakeSpinner;
    private String state;
    private String carMake;
    private User user;

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
        regoEditText = (EditText) findViewById(R.id.regoEditText);
        streetEditText = (EditText) findViewById(R.id.streetEditText);
        suburbEditText = (EditText) findViewById(R.id.suburbEditText);
        carModelEditText = (EditText) findViewById(R.id.carModelEditText);
        postcodeEditText = (EditText) findViewById(R.id.postcodeEditText);
        stateSpinner = (Spinner) findViewById(R.id.stateSpinner);
        carMakeSpinner = (Spinner) findViewById(R.id.carMakeSpinner);
        state = "QLD";
        carMake = "Audi";

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        //set adapter to spinner
        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(context, R.array.states_array, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);
        stateSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> carMakeAdapter = ArrayAdapter.createFromResource(context, R.array.makes_array, android.R.layout.simple_spinner_item);
        carMakeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carMakeSpinner.setAdapter(carMakeAdapter);
        carMakeSpinner.setOnItemSelectedListener(this);


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
        String rego = regoEditText.getText().toString();
        String street = streetEditText.getText().toString();
        String suburb = suburbEditText.getText().toString();
        String postcode = postcodeEditText.getText().toString();
        String carModel = carModelEditText.getText().toString();

        if (!validateForm()) {
            return;
        }

        String address = street + ", " + suburb + ", " + state + " " + postcode;
        Drive drive = new Drive(drivername, licence, phone, email, getUid(), user.name, rego, address, carMake, carModel, null, null, "started");
        Intent intent = new Intent(context, AgreementActivity.class);
        intent.putExtra("drive", drive);
        intent.putExtra("user", user);
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
            requestFocus(emailEditText);
            valid = false;
        }else if(!isValidEmail(email)){
            emailEditText.setError("Email is not valid.");
            requestFocus(emailEditText);
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        String phone = phoneEditText.getText().toString();
        if (TextUtils.isEmpty(phone)){
            phoneEditText.setError("Required");
            requestFocus(phoneEditText);
            valid = false;
        }else{
            phoneEditText.setError(null);
        }

        String licence = licenceEditText.getText().toString();
        if (TextUtils.isEmpty(licence)) {
            licenceEditText.setError("Required.");
            requestFocus(licenceEditText);
            valid = false;
        } else {
            licenceEditText.setError(null);
        }

        String name = nameEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Required.");
            requestFocus(nameEditText);
            valid = false;
        } else {
            nameEditText.setError(null);
        }

        String rego = regoEditText.getText().toString();
        if (TextUtils.isEmpty(rego)) {
            regoEditText.setError("Required.");
            requestFocus(regoEditText);
            valid = false;
        } else {
            regoEditText.setError(null);
        }

        String street = streetEditText.getText().toString();
        if (TextUtils.isEmpty(street)) {
            streetEditText.setError("Required.");
            requestFocus(streetEditText);
            valid = false;
        } else {
            streetEditText.setError(null);
        }

        String city = suburbEditText.getText().toString();
        if (TextUtils.isEmpty(city)) {
            suburbEditText.setError("Required.");
            requestFocus(suburbEditText);
            valid = false;
        } else {
            suburbEditText.setError(null);
        }

        String carModel = carModelEditText.getText().toString();
        if (TextUtils.isEmpty(carModel)) {
            carModelEditText.setError("Required.");
            requestFocus(carModelEditText);
            valid = false;
        } else {
            carModelEditText.setError(null);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()){
            case R.id.stateSpinner:
                state = (String) adapterView.getItemAtPosition(i);
                break;
            case R.id.carMakeSpinner:
                carMake = (String) adapterView.getItemAtPosition(i);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
