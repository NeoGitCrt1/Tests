package practices.my.countstep;

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
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;

/**
 * Created by ysy20_000 on 2016/7/9.
 */
class TriggerListener extends TriggerEventListener {
    public void onTrigger(TriggerEvent event) {
        // Do Work.

        // As it is a one shot sensor, it will be canceled automatically.
        // SensorManager.requestTriggerSensor(this, mSigMotion); needs to
        // be called again, if needed.
    }
}

public class CountService extends Service implements SensorEventListener {


//    private
    private final CountServiceBinder countServiceBinder = new CountServiceBinder();
    public class CountServiceBinder extends Binder {
        CountService getService() {
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
        mWakeLock.release();

        mSensorManager.unregisterListener (this);
        clearCntStep();
        super.onDestroy();

    }

    private  SensorManager mSensorManager = null;
    private  Sensor mAcceleromete = null;
    private Handler mHandler;
    public void setmHandler(Handler mHandler){
        this.mHandler = mHandler;
    }
    @Override
    public int onStartCommand(Intent intent,
                               int flags,
                               int startId){
        System.out.println("onStartCommand********************");
        if( intent.getBooleanExtra(getString(R.string.ser_switch),false)){
            mWakeLock.acquire();
            mSensorManager.registerListener(this, mAcceleromete,
                    SensorManager.SENSOR_DELAY_NORMAL);
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
        DownloadFilesTask dft = new DownloadFilesTask();
        dft.execute(oSC[0],oSC[1],oSC[2],oSC[3]);
        return super.stopService(name);
    }

    private int[] oSC= {0,0,0,0};
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
            if(event.values[i] > 11 ){
//                System.out.println("onSensorChanged***********" + i + xyz[i] + event.values[i] );
                oSCCntUp(2);
                sendMsg(2);
            }else
            if(event.values[i] > 10 ){
//                System.out.println("onSensorChanged***********" + i + xyz[i] + event.values[i] );
                oSCCntUp(3);
                sendMsg(3);
            }
        }

    }
    private synchronized void oSCCntUp(int idx){
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

            msg.arg1 = oSC[idx];
            msg.what = idx;

            mHandler.sendMessage(msg);
        }
    }

    private class DownloadFilesTask extends AsyncTask<Integer ,Integer,Integer> {

        protected Integer doInBackground(Integer... cnts) {
            int count = cnts.length;
            long totalSize = 0;
            TaceLogHelper tlh = new TaceLogHelper();
            return 1;
        }

        protected void onProgressUpdate(Integer... progress) {
//            setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Long result) {
//            showDialog("Downloaded " + result + " bytes");
        }
    }

}
