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
import java.util.Timer;
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
    public void onCreate(){
        System.out.println("Cr********************************");
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAcceleromete = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        PowerManager manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "s");// CPU保存运行

    }
    @Override
    public void onDestroy(){
        System.out.println("ds********************************");
        InsertTask dft = new InsertTask(true);
        new Thread(dft).start();
        mWakeLock.release();

        mSensorManager.unregisterListener (this);

        super.onDestroy();

    }

    private  SensorManager mSensorManager = null;
    private  Sensor mAcceleromete = null;
    private Handler mHandler;
    private Date startTime;
    private Timer timer = new Timer();
    public void setmHandler(Handler mHandler){
        this.mHandler = mHandler;
    }
    @Override
    public int onStartCommand(Intent intent,
                               int flags,
                               int startId){
        System.out.println("onStartCommand********************");
        if( intent.getBooleanExtra(getString(R.string.ser_switch),false)){
            startTime = new  Date(System.currentTimeMillis());
            mWakeLock.acquire();
            mSensorManager.registerListener(this, mAcceleromete,
                    SensorManager.SENSOR_DELAY_NORMAL);

            ScheduledExecutorService service = Executors.newScheduledThreadPool(10);

            long initialDelay1 = 1;
            long period1 = 1;
            // 从现在开始1秒钟之后，每隔1秒钟执行一次job1
            service.scheduleAtFixedRate(
                    new InsertTask(false), initialDelay1,
                    period1, TimeUnit.MINUTES);



//
//            long delay1 = 1 * 1000;
//            long period1 = 1000;
//            // 从现在开始 1 秒钟之后，每隔 1 秒钟执行一次 job1
//            timer.schedule(new InsertTask(), delay1, period1);

        }
        return START_STICKY;
    }
    @Override
    public ComponentName startService(Intent service) {
        System.out.println("startService********************");
        return super.startService(service);
    }

    @Override
    public boolean stopService(Intent name) {
        System.out.println("stopService********************");
        mSensorManager.unregisterListener (this);

        return super.stopService(name);
    }

    private int[] oSC= {0,0,0,0,0};

    private int i= 0;
    private final char[] xyz = {'x','y','z'};
    @Override
    public void onSensorChanged(SensorEvent event){
//        System.out.println("onSensorChanged***********" + mHandler);
        for(i = 0;i<event.values.length;i++){

            if(event.values[i] > 13 ){
//                System.out.println("onSensorChanged***********" + i + xyz[i] + event.values[i] );
                oSCCntUp(0);
                sendMsg(0);
            }else
            if(event.values[i] > 12 ){
//                System.out.println("onSensorChanged***********" + i + xyz[i] + event.values[i] );
                oSCCntUp(1);
                sendMsg(1);
            }else
            if(event.values[i] > 10.5 ){
//                System.out.println("onSensorChanged***********" + i + xyz[i] + event.values[i] );
                oSCCntUp(2);
                sendMsg(2);
            }else
            if(event.values[i] > 10 ){
//                System.out.println("onSensorChanged***********" + i + xyz[i] + event.values[i] );
                oSCCntUp(3);
//                sendMsg(3);
            }
        }

    }
    private synchronized void oSCCntUp(int idx){
        if(idx < 3) oSC[oSC.length - 1]++;
        oSC[idx]++;
    }

    private int oAC= 0;
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//        System.out.println("onAccuracyChanged***********");
        oAC++;
    }

    public void clearCntStep(){
        for(int i=0;i<oSC.length;i++){
            oSC[i] = 0;
            sendMsg(i);
        }
    }
    private void sendMsg(int idx){
        if(mHandler != null ) {
//            System.out.println("sendMsg***********" + mHandler);
            Message msg = new Message();

            msg.arg1 = oSC[oSC.length - 1];
            msg.what = idx;

            mHandler.sendMessage(msg);
        }
    }
    public static final String TAG = "CountService";
    private class InsertTask extends TimerTask implements Runnable{

        private Date  endTime;
        private Boolean isEndCnt = false;
//        private int[] cnts;
        public InsertTask(boolean isShutDown) {
//            this.startTime = startTime;
//            this.cnts = cnts;
            endTime = new  Date(System.currentTimeMillis());
            if (isShutDown){
                isEndCnt = true;
            }else if(startTime.getDate()!=endTime.getDate()){
                isEndCnt = true;
            }
        }
        @Override
        public void run(){
            TraceLogDBManager traceLogDBManager = TraceLogDBManager.GetInstance();
            long res = 0;
            try {
                res = traceLogDBManager.dealData(startTime,endTime,oSC);
            } catch (ParseException e) {
            }
            if (isEndCnt){
                clearCntStep();
                startTime = new  Date(System.currentTimeMillis());
            }

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
