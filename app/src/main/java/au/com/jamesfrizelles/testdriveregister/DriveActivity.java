package au.com.jamesfrizelles.testdriveregister;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.os.Handler;

public class DriveActivity extends AppCompatActivity {
    private Context context;
    private String TAG;
    private Handler handler;
    private Date date;
    private TextView startTimeTextView;
    private TextView currentTimeTextView;
    private DriveTimer dtimer;
    private ScheduledFuture future;
    private ScheduledExecutorService exec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);

        //initialize values
        context = DriveActivity.this;
        TAG = "DriveActivity";
        date = new Date(System.currentTimeMillis());
        startTimeTextView = (TextView) findViewById(R.id.startTimeTextView);
        currentTimeTextView = (TextView) findViewById(R.id.currentTimeTextView);
        handler = new Handler(getMainLooper());
        dtimer = new DriveTimer();
        exec = Executors.newSingleThreadScheduledExecutor();

        //set timer
        future = exec.scheduleAtFixedRate(dtimer, 0, 1000, TimeUnit.MILLISECONDS);


        //show start time
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        startTimeTextView.setText(df.format(date));


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Test Drive Register");
        setSupportActionBar(toolbar);

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"Timer schedule stopped.");
        future.cancel(true);
    }

    private class TimerThread extends Thread{
        public void run(){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Date currentDate = new Date(System.currentTimeMillis());
                    long millis = currentDate.getTime() - date.getTime();

                    String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));

                    currentTimeTextView.setText(hms);
                }
            });
        }
    }

    private class DriveTimer implements Runnable {
        public void run(){
            TimerThread thread = new TimerThread();
            thread.start();
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.e(TAG,"Timer schedule stopped.");
        future.cancel(true);
    }
    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        future = exec.scheduleAtFixedRate(dtimer, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public void onClickFinishDrive(View view){
        new AlertDialog.Builder(context)
                .setMessage(getResources().getString(R.string.alertFinishDriveMessage))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // OK button pressed
                        Intent intent = new Intent(context, ReviewActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
