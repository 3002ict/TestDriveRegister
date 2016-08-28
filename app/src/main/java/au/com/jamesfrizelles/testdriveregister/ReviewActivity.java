package au.com.jamesfrizelles.testdriveregister;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class ReviewActivity extends AppCompatActivity {
    private Context context;
    private String TAG;
    private TextView startTimeTextView;
    private TextView endTimeTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Review");
        setSupportActionBar(toolbar);

        //initialize values
        context = ReviewActivity.this;
        TAG = "ReviewActivity";

    }

}
