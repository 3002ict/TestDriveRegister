package au.com.jamesfrizelles.testdriveregister;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import au.com.jamesfrizelles.testdriveregister.models.Drive;
import au.com.jamesfrizelles.testdriveregister.models.User;

public class AgreementActivity extends BaseActivity {
    private Context context;
    private String TAG;
    private WebView webView;
    private CheckBox checkBox;
    private boolean checked;
    private Button continueButton;
    private EditText licenceEditText;
    private Drive drive;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);

        //initialize values
        context = AgreementActivity.this;
        TAG = "AgreementActivity";
        webView = (WebView) findViewById(R.id.webView);

        checkBox = (CheckBox) findViewById(R.id.checkBox);
        continueButton = (Button) findViewById(R.id.continueButton);


        //button settings
        continueButton.setVisibility(View.GONE);

        //web view settings

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);

        webView.loadUrl("https://testdriveregister-4ef26.firebaseapp.com/agreement");

        //check box settings
        checked = false;
        checkBox.setChecked(false);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( ((CheckBox)view).isChecked() ) {
                    checked = true;
                    continueButton.setVisibility(View.VISIBLE);
                }else{
                    checked = false;
                    continueButton.setVisibility(View.GONE);
                }
            }
        });

        //toolbar settings
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        String toolBarTitle = "Agreement";
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

        //get drive object
        Intent intent = getIntent();
        drive = (Drive) intent.getSerializableExtra("drive");
        user = (User) intent.getSerializableExtra("user");

        //firebase auth check
        initFirebaseAuth();
        addAuthStateListener(context);
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

    public void onClickDecline(View view){
        finish();
    }

    public void onClickContinue(View view){
        Intent intent = new Intent(context, StartDriveActivity.class);
        intent.putExtra("drive", drive);
        intent.putExtra("user", user);
        startActivityForResult(intent, 300);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 300){
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
    protected void onStop() {
        super.onStop();
        removeAuthStateListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        addAuthStateListener(context);
    }
}
