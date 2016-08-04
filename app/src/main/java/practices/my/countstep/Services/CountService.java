package practices.my.countstep.Services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import practices.my.countstep.DBManager.TraceLogDBManager;
import practices.my.countstep.R;

/**
 * Created by ysy20_000 on 2016/7/9.
 */
@SuppressLint("NewApi")
class TriggerListener extends TriggerEventListener {
    public void onTrigger(TriggerEvent event) {
        // Do Work.
//2016
        // As it is a one shot sensor, it will be canceled automatically.
        // SensorManager.requestTriggerSensor(this, mSigMotion); needs to
        // be called again, if needed.
    }
}

public class CountService extends Service implements SensorEventListener {


    //    private
    private final CountServiceBinder countServiceBinder = new CountServiceBinder();

    public class CountServiceBinder extends Binder {
        public CountService getService() {
            return CountService.this;
        }

    }

//    private final SensorManager mSensorManager;

    //    CountService(){
//        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
//
//        mSigMotion = mSensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
//
//    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return countServiceBinder;
    }
//    private final Sensor mSigMotion;
//    private final TriggerEventListener mListener = new TriggerEventListener();

    private PowerManager.WakeLock mWakeLock;

    @Override
    public void onCreate() {
//        System.out.println("Cr********************************");
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAcceleromete = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        PowerManager manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "s");// CPU保存运行

    }

    @Override
    public void onDestroy() {
//        System.out.println("ds********************************");
        mSensorManager.unregisterListener(this);
        schService.shutdown();
        new Thread(dft).start();
        while (!dft.isEndSave()) {
        }
        if (dft.isEndSave()) sendMsg(-1);


        mWakeLock.release();
        super.onDestroy();

    }

    private SensorManager mSensorManager = null;
    private Sensor mAcceleromete = null;
    private Handler mHandler;

    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    private ScheduledExecutorService schService = Executors.newScheduledThreadPool(10);
    private InsertTask dft = new InsertTask();

    @Override
    public int onStartCommand(Intent intent,
                              int flags,
                              int startId) {
//        System.out.println("onStartCommand********************");
        if (intent.getBooleanExtra(getString(R.string.ser_switch), false)) {
            mWakeLock.acquire();
            mSensorManager.registerListener(this, mAcceleromete,
                    SensorManager.SENSOR_DELAY_NORMAL);

            // 从现在开始1秒钟之后，每隔1秒钟执行一次job1
            long initialDelay1 = 1;
            long period1 = 1;
            schService.scheduleAtFixedRate(
                    dft,
                    initialDelay1,
                    period1, TimeUnit.MINUTES);
        }
        return START_STICKY;
    }

    @Override
    public ComponentName startService(Intent service) {
//        System.out.println("startService********************");
        return super.startService(service);
    }

    @Override
    public boolean stopService(Intent name) {
//        System.out.println("stopService********************");
        mSensorManager.unregisterListener(this);

        return super.stopService(name);
    }

    private int[] oSC = {0, 0, 0, 0, 0};
    private final char[] xyz = {'x', 'y', 'z'};

    @Override
    public void onSensorChanged(SensorEvent event) {
//        System.out.println("onSensorChanged***********" + mHandler);
        for (int i = 0; i < event.values.length; i++) {
            float f = event.values[i];
            if (f > 13) {
//                System.out.println("onSensorChanged***********" + i + xyz[i] + event.values[i] );
                oSCCntUp(0);
                sendMsg(0);
            } else if (f > 12) {
//                System.out.println("onSensorChanged***********" + i + xyz[i] + event.values[i] );
                oSCCntUp(1);
                sendMsg(1);
            } else if (f > 11) {
//                System.out.println("onSensorChanged***********" + i + xyz[i] + event.values[i] );
                oSCCntUp(2);
                sendMsg(2);
            } else if (f > 10) {
//                System.out.println("onSensorChanged***********" + i + xyz[i] + event.values[i] );
                oSCCntUp(3);
//                sendMsg(3);
            }
        }

    }

    private void oSCCntUp(int idx) {
        synchronized (oSC) {
            if (idx < 3) oSC[oSC.length - 1]++;
            oSC[idx]++;
        }
    }

    private int oAC = 0;

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//        System.out.println("onAccuracyChanged***********");
        oAC++;
    }

    public void clearCntStep() {
        synchronized (oSC) {
            for (int i = 0; i < oSC.length - 1; i++) {
                oSC[i] = 0;

            }
        }
    }

    private void sendMsg(int idx) {
        if (mHandler != null) {
//            System.out.println("sendMsg***********" + mHandler);
            Message msg = new Message();
            msg.arg1 = oSC[oSC.length - 1];
            msg.what = idx;
            mHandler.sendMessage(msg);
        }
    }

    public static final String TAG = "CountService";

    private class InsertTask extends TimerTask implements Runnable {
        private Date startTime;

        public InsertTask() {
            startTime = new Date(System.currentTimeMillis());
        }

        public boolean isEndSave() {
            return endSave;
        }

        private volatile boolean endSave = false;

        @Override
        public void run() {
            endSave = (false);
            if ((oSC[0] + oSC[1] + oSC[2]) < 1) {
                endSave = (true);
                return;
            }
            Date endTime = new Date(System.currentTimeMillis());
            TraceLogDBManager traceLogDBManager = TraceLogDBManager.GetInstance();
            try {
                long res = traceLogDBManager.dealData(startTime, endTime, oSC);
            } catch (ParseException e) {
            }
            clearCntStep();
            startTime.setTime(endTime.getTime());

            endSave = (true);
        }

//        protected Boolean doInBackground(Integer... params) {
//
//            TraceLogDBManager traceLogDBManager = TraceLogDBManager.GetInstance();
//            long res = 0;
//            try {
//                res = traceLogDBManager.insertData(startTime,endTime,oSC);
//            } catch (ParseException e) {
//            }
////            System.out.println("InsertTask***********" + res);
////            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
////            int all = cnts[0]+cnts[1]+cnts[2];
//////            Log.i(TAG, dateFormat.format(startTime) + "--" + Integer.toString(all) + "--" + dateFormat.format(endTime));
////
////            Log.i(TAG, dateFormat.format(cr.getInt(3))+ ":"
////                    + Integer.toString(all)  );
//            clearCntStep();
//            startTime = new  Date(System.currentTimeMillis());
//            return true;
//        }
//
//        protected void onProgressUpdate(Integer... progress) {
////            setProgressPercent(progress[0]);
//        }
//
//        protected void onPostExecute(Long result) {
////
////
////            Toast.makeText(getApplicationContext(), "onPostExecute",
////                    Toast.LENGTH_SHORT).show();
//        }
    }

}
