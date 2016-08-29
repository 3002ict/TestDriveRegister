package au.com.jamesfrizelles.testdriveregister;

import android.content.Context;
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

import au.com.jamesfrizelles.testdriveregister.models.User;

public class ProfileActivity extends BaseActivity {
    private Context context;
    private String TAG;
    private boolean doubleBackToExitPressedOnce;
    private TextView userNameTextView;
    private User user;
    private SwipeRefreshLayout mSwipeRefreshLayout;


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

        //receive user data from LoginActivity
        Intent intent = getIntent();
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
                startActivityForResult(intent, 100);
            }
        });

        //Firebase authentication check
        initFirebaseAuth();
        addAuthStateListener(context);


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
