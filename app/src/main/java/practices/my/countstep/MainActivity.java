package practices.my.countstep;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Intent serviceItent;
    private CountService countService;

    private TextView[] tv;
    private Switch sw;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        System.out.println("app Create******************************");
        serviceItent = new Intent(this,CountService.class);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        tv = new TextView[4];
        tv[0] = (TextView) findViewById(R.id.textView);
        tv[1] = (TextView) findViewById(R.id.textView2);
        tv[2] = (TextView) findViewById(R.id.textView3);
        tv[3] = (TextView) findViewById(R.id.textView4);
        changeTxt(0,"Hello");
        sw = (Switch) findViewById(R.id.switch1);
        sw.setOnTouchListener(new SwitchTouchListener());
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
//                System.out.println("handleMessage***********");
                if( msg.arg1 == 0 ){
                    if(msg.what == 0){
                        changeTxt(msg.what,   "Hello");
                    }else{
                        changeTxt(msg.what,   "");
                    }
                }else{
                    changeTxt(msg.what,   Integer.toString( msg.arg1));
                }

            }
        };

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
//        System.out.println("app Start******************************");

    }

    private Handler mHandler;
    @Override
    public void onResume(){
        super.onResume();
//        System.out.println("app Resume******************************");
        sw.setChecked(mBound);


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
                        sw.setChecked(mBound);
//                        System.out.println("sw--------------" + sw.isChecked());
                    }
                }
            }
            return true;
        }
    }
    boolean mBound = false;
    private void toggleBindService(){
        if (mBound) {
            unbindService(serviceConnection);
            serviceItent.putExtra( getString(R.string.ser_switch),false);
            stopService(serviceItent);
            mBound = false;
        }else{
            serviceItent.putExtra( getString(R.string.ser_switch),true);
            bindService(serviceItent, serviceConnection, Context.BIND_AUTO_CREATE);
            mBound = true;
        }
    }
    @Override

    protected void onDestroy(){
        super.onDestroy();
// PREVENT leaked ServiceConnection
        if (mBound) {
            unbindService(serviceConnection);
            serviceItent.putExtra( getString(R.string.ser_switch),false);
            stopService(serviceItent);
        }
    }
    @Override
    public void onBackPressed(){
        finish();
    }

}
