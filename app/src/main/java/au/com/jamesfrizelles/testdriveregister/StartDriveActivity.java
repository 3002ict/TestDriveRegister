package au.com.jamesfrizelles.testdriveregister;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import au.com.jamesfrizelles.testdriveregister.models.Drive;

public class StartDriveActivity extends AppCompatActivity {
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

    }

    public void onClickStartDrive(View view){
        Intent intent = new Intent(context, DriveActivity.class);
        startActivity(intent);
        //finish AgreementActivity
        finishActivity(2);

        //finish this activity
        finish();
    }

}
