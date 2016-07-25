package practices.my.countstep;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import practices.my.countstep.DBManager.TraceLogDBManager;
import practices.my.countstep.logger.Log;
import practices.my.countstep.logger.LogFragment;
import practices.my.countstep.logger.LogWrapper;
import practices.my.countstep.logger.MessageOnlyLogFilter;

public class MainActivity extends AppCompatActivity {

    private Intent serviceItent;
    private CountService countService;

    private TextView[] tv;
    private TextView tv0;
    private Switch sw;
    private ScrollView mScrollView;

    private static MainActivity  instance;
    public static Context getContext()
    {
        return instance;
    }

    public MainActivity() {
        instance = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        System.out.println("app Create******************************");
        serviceItent = new Intent(this,CountService.class);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

//        tv = new TextView[4];
//        tv[0] = (TextView) findViewById(R.id.textView);
//        tv[1] = (TextView) findViewById(R.id.textView2);
//        tv[2] = (TextView) findViewById(R.id.textView3);
//        tv[3] = (TextView) findViewById(R.id.textView4);
//        changeTxt(0,"Hello");

        tv0 = (TextView) findViewById(R.id.textView0);
        sw = (Switch) findViewById(R.id.switch1);
        sw.setOnTouchListener(new SwitchTouchListener());
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
//                System.out.println("handleMessage***********");
                if( msg.arg1 == 0 ){
                    changeTxt(   "Hello");
                    Log.i("Clear","");
                    showTask dft = new showTask();
                    dft.execute();
//                    if(msg.what == 0){
//                        changeTxt(msg.what,   "Hello");
//                    }else{
//                        changeTxt(msg.what,   "");
//                    }
                }else{

//                    changeTxt(msg.what,   Integer.toString( msg.arg1));
                    changeTxt(  Integer.toString( msg.arg1));
                }

            }
        };

// Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());

//        bt =(Button) findViewById(R.id.button);
//        bt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                cnt++;
//////                str.append(cnt);
////                changeTxt(Integer.toString(cnt));
//                countService.clearCntStep();
//            }
//        });

//        bt2 =(Button) findViewById(R.id.button2);
//        bt2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                stopService(serviceItent);
//            }
//        });


        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        showTask dft = new showTask();
        dft.execute();
//        System.out.println("app Start******************************");

    }

    private Handler mHandler;
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onResume(){
        super.onResume();

//        System.out.println("app Resume******************************");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            sw.setChecked(mBind);
        }


    }
    @Override
    public void onPause(){

        super.onPause();
//        System.out.println("app Pause******************************");
        mHandler.removeMessages(0);
        mHandler.removeMessages(1);
        mHandler.removeMessages(2);
        mHandler.removeMessages(3);
    }
    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeMessages(0);
        mHandler.removeMessages(1);
        mHandler.removeMessages(2);
        mHandler.removeMessages(3);
//        System.out.println("app stop******************************");

    }
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            countService = ((CountService.CountServiceBinder)service).getService();
            countService.setmHandler(mHandler);
            startService(serviceItent);
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            countService = null;
        }

    };
    private void changeTxt(int idx, String str){
        tv[idx].setText(str);
    }
    private void changeTxt( String str){
        tv0.setText(str);
    }

    class SwitchTouchListener implements View.OnTouchListener{
        private static final int MAX_CLICK_DURATION = 200;
        private long startClickTime;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    startClickTime = Calendar.getInstance().getTimeInMillis();
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                    if(clickDuration < MAX_CLICK_DURATION) {
                        Switch sw = (Switch)v;
//                        sw.toggle();
//                        System.out.println("sw++++++++++++++" + sw.isChecked());

                        toggleBindService();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            sw.setChecked(mBind);
                        }
//                        System.out.println("sw--------------" + sw.isChecked());
                    }
                }
            }
            return true;
        }
    }
    boolean mBind = false;
    private void toggleBindService(){
        if (mBind) {
            unbindService(serviceConnection);
            serviceItent.putExtra( getString(R.string.ser_switch),false);
            stopService(serviceItent);
            mBind = false;
        }else{
            serviceItent.putExtra( getString(R.string.ser_switch),true);
            bindService(serviceItent, serviceConnection, Context.BIND_AUTO_CREATE);
            mBind = true;
        }
    }
    @Override

    protected void onDestroy(){
        super.onDestroy();
// PREVENT leaked ServiceConnection
        if (mBind) {
            unbindService(serviceConnection);
            serviceItent.putExtra( getString(R.string.ser_switch),false);
            stopService(serviceItent);
        }
    }
    @Override
    public void onBackPressed(){
        finish();
    }

    public static final String TAG = "MainActivity";
    private class showTask extends AsyncTask<Integer, Integer, Boolean> {

        public showTask() {
        }

        protected Boolean doInBackground(Integer... params) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            TraceLogDBManager traceLogDBManager = TraceLogDBManager.GetInstance();
            Cursor cr = traceLogDBManager.getData(-1,"asc");
            int all = 0;
            if(cr!=null) {
                while (cr.moveToNext()) {
                    all = cr.getInt(0) + cr.getInt(1) + cr.getInt(2);
//                    Log.i(TAG, dateFormat.format(cr.getInt(5)) + "--" + Integer.toString(all) + "--" + dateFormat.format(cr.getInt(6)))
//                    System.out.println("++++"+cr.getInt(3));
                    Log.i(TAG, dateFormat.format(cr.getLong(3))+ ":"
                            + Integer.toString(all)  );
                }
            }else{
                return  false;

            }
            return true;
        }

        protected void onProgressUpdate(Integer... progress) {
//            setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Long result) {
//            showDialog("Downloaded " + result + " bytes");
        }
    }
}
