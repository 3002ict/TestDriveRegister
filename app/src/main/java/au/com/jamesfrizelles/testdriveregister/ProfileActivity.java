package au.com.jamesfrizelles.testdriveregister;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import au.com.jamesfrizelles.testdriveregister.models.Drive;
import au.com.jamesfrizelles.testdriveregister.models.User;

public class ProfileActivity extends BaseActivity {
    private Context context;
    private String TAG;
    private boolean doubleBackToExitPressedOnce;
    private TextView userNameTextView;
    private User user;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //initialize values
        context = ProfileActivity.this;
        TAG = "ProfileActivity";
        doubleBackToExitPressedOnce = false;
        userNameTextView = (TextView) findViewById(R.id.userNameTextView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //receive user data from LoginActivity
        final Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        //set user info to Recycler View
        userNameTextView.setText(user.name);
        final ArrayList<String> textList = new ArrayList<String>();
        textList.add(user.email);
        textList.add(user.phone);
        final ArrayList<String> imageList = new ArrayList<String>();
        imageList.add("email");
        imageList.add("phone");
        (new Thread(new Runnable() {
            @Override
            public void run() {
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                CustomRecyclerAdapter recyclerAdapter = new CustomRecyclerAdapter(context, textList, imageList);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(recyclerAdapter);
            }
        })).start();

        //disable refresh
        mSwipeRefreshLayout.setEnabled(false);

        //Toolbar setting
        String toolBarTitle = "PROFILE";
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(toolBarTitle);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DriverDetailsActivity.class);
                intent.putExtra("user", user);
                startActivityForResult(intent, 100);
            }
        });

        //Firebase authentication check
        initFirebaseAuth();
        addAuthStateListener(context);

        if(user.resume != null ){
            final String driveKey;
            final Drive drive;
            Iterator it = user.resume.entrySet().iterator();
            it.hasNext();
            Map.Entry pair = (Map.Entry)it.next();
            driveKey = pair.getKey().toString();

            Object obj = pair.getValue();
            if (obj instanceof HashMap) {
                final  HashMap driveHashMap = (HashMap) obj;
                final String status = driveHashMap.get("status").toString();


                new AlertDialog.Builder(context)
                        .setTitle("Resume Test Drive")
                        .setMessage("You have uncompleted test drive. Do you want to continue?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // OK button pressed
                                Log.d(TAG, status);
                                if(status.equals("started") && driveHashMap.containsKey("start_drive")) {
                                    Intent intentDrive = new Intent(context, DriveActivity.class);
                                    intentDrive.putExtra("user", user);
                                    intentDrive.putExtra("startTime", driveHashMap.get("start_drive").toString());
                                    intentDrive.putExtra("key", driveKey);
                                    startActivity(intentDrive);
                                }else if(status.equals("inProgress") && driveHashMap.containsKey("start_drive") && driveHashMap.containsKey("finish_drive") ) {
                                    Intent intentReview = new Intent(context, ReviewActivity.class);
                                    intentReview.putExtra("user", user);
                                    intentReview.putExtra("startTime", driveHashMap.get("start_drive").toString());
                                    intentReview.putExtra("key", driveKey);
                                    intentReview.putExtra("endTime", driveHashMap.get("finish_drive").toString());
                                    startActivity(intentReview);
                                    finish();
                                }else{

                                        Toast.makeText(context, "Invalid data.",
                                                Toast.LENGTH_SHORT).show();

                                    //Remove uncompleted data
                                    Map<String, Object> driveUpdates = new HashMap<>();
                                    driveUpdates.put("/users/" + getUid() + "/resume", null);
                                }


                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Cancel button pressed
                                //Remove uncompleted data
                                Map<String, Object> driveUpdates = new HashMap<>();
                                driveUpdates.put("/users/" + getUid() + "/resume", null);
                                mDatabase.updateChildren(driveUpdates);
                            }
                        })
                        .show();
            }

            it.remove(); // avoids a ConcurrentModificationException


        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            switch (resultCode){
                case RESULT_OK:
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
