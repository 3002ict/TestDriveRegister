package au.com.jamesfrizelles.testdriveregister;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ReviewActivity extends BaseActivity {
    private Context context;
    private String TAG;
    private TextView startTimeTextView;
    private TextView endTimeTextView;
    private String startTime;
    private String endTime;
    private boolean doubleBackToExitPressedOnce;
    private String key;
    private RatingBar ratingBar;
    private Float ratingVal;
    private EditText commentsEditText;
    private DatabaseReference mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //toolbar settings
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Review");
        setSupportActionBar(toolbar);

        //initialize values
        context = ReviewActivity.this;
        TAG = "ReviewActivity";
        startTimeTextView = (TextView) findViewById(R.id.startTimeTextView);
        endTimeTextView = (TextView) findViewById(R.id.endTimeTextView);
        doubleBackToExitPressedOnce = false;
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingVal = 0f;
        commentsEditText = (EditText) findViewById(R.id.commnetsEditText);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //set rating bar listener
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                ratingVal = v;
            }
        });

        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        startTime = intent.getStringExtra("startTime");
        endTime = intent.getStringExtra("endTime");
        startTimeTextView.setText(startTime);
        endTimeTextView.setText(endTime);

        //firebase Auth check
        initFirebaseAuth();
        addAuthStateListener(context);

    }

    public void onClickSubmitReview(View view){
        showProgressDialog();
        String comments = "";
        comments += commentsEditText.getText().toString();
        Map<String, Object> driveUpdates = new HashMap<>();
        driveUpdates.put("/drives/" + key + "/rate", Float.toString(ratingVal));
        driveUpdates.put("/drives/" + key + "/comments", comments);
        driveUpdates.put("/drives/" + key + "/status", "pending");
        mDatabase.updateChildren(driveUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){
                    hideProgressDialog();
                    Toast.makeText(context, R.string.submission_failed,
                            Toast.LENGTH_SHORT).show();
                }else {
                    hideProgressDialog();
                    sendEmail();
                    LayoutInflater factory = LayoutInflater.from(context);
                    final View v = factory.inflate(R.layout.dialog_confirmation, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            signOut();
                        }
                    });
                    builder.setView(v);
                    AlertDialog dialog = builder.create();
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                    dialog.show();
                }
            }
        });
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

    private void sendEmail(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://test-drive-mailer.herokuapp.com?key=" + key);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    String str = InputStreamToString(con.getInputStream());
                    Log.d(TAG, "HTTP:" + str);
                } catch(Exception ex) {
                    System.out.println(ex);
                }
            }
        }).start();

    }

    // InputStream -> String
    static String InputStreamToString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

}
