package au.com.jamesfrizelles.testdriveregister;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import au.com.jamesfrizelles.testdriveregister.models.Drive;

public class StartDriveActivity extends BaseActivity {
    private Context context;
    private String TAG;
    private Drive drive;
    private TextView drivernameTextView;
    private TextView licenceTextView;
    private TextView phoneTextView;
    private TextView emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_drive);


        //initialize values
        context = StartDriveActivity.this;
        TAG = "StartDriveActivity";
        licenceTextView = (TextView) findViewById(R.id.licenceTextView);
        drivernameTextView = (TextView) findViewById(R.id.drivernameTextView);
        phoneTextView = (TextView) findViewById(R.id.phoneTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);


        //toolbar settings
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        String toolBarTitle = "Start Drive";
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

        //set drive values for each text view
        licenceTextView.setText(drive.licence);
        drivernameTextView.setText(drive.drivername);
        phoneTextView.setText(drive.phone);
        emailTextView.setText(drive.email);

        //firebase auth check
        initFirebaseAuth();
        addAuthStateListener(context);

    }

    public void onClickStartDrive(View view){
        LayoutInflater factory = LayoutInflater.from(context);
        final View v = factory.inflate(R.layout.dialog_start_drive, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(context, DriveActivity.class);
                startActivity(intent);

                //finish this activity
                setResult(RESULT_OK);
                finish();
            }
        });
        builder.setView(v);
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.show();

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

    @Override
    protected void onStart() {
        super.onStart();
        addAuthStateListener(context);
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeAuthStateListener();
    }
}
