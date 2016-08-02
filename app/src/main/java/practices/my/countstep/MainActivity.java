package practices.my.countstep;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
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
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import practices.my.countstep.DBManager.TraceLogDBManager;
import practices.my.countstep.Services.CountService;
import practices.my.countstep.common.db.ContextSaver;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    boolean mBind = false;
    private Intent serviceItent;
    private CountService countService;
    //    private TextView[] tv;
    private TextView tv0,tvInScl;
    private Switch sw;
    //    private ScrollView mScrollView;
    private int nowCnt = 0;
    private Handler mHandler;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextSaver.setContext(this);

//        System.out.println("app Create******************************");
        serviceItent = new Intent(this, CountService.class);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

//        tv = new TextView[4];
//        tv[0] = (TextView) findViewById(R.id.textView);
//        tv[1] = (TextView) findViewById(R.id.textView2);
//        tv[2] = (TextView) findViewById(R.id.textView3);
//        tv[3] = (TextView) findViewById(R.id.textView4);
//        changeTxt(0,"Hello");

        initLandAndPort();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                nowCnt = msg.arg1;
//                    changeTxt(msg.what,   Integer.toString( msg.arg1));
                changeTxt(nowCnt);
            }
        };

// Wraps Android's native log framework.
//        LogWrapper logWrapper = new LogWrapper();
//        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
//        Log.setLogNode(logWrapper);
//
//        // Filter strips out everything except the message text.
//        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
//        logWrapper.setNext(msgFilter);
//
//        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.log_fragment);
//        msgFilter.setNext(logFragment.getLogView());
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


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

//        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
////            openChart();
////        }
////            Toast.makeText(getApplicationContext(), "切换为横屏", Toast.LENGTH_SHORT).show();
//        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//            assert fab != null;
////            fab.setVisibility(View.INVISIBLE);
//            fab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(mTrick < 0 ){
//                        mTrick = 0 ;
//                    }else{
//                        mTrick = -1;
//                    }
//                    Snackbar.make(view, "Replace with your own action" + mTrick, Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                }
//            });
////            Toast.makeText(getApplicationContext(), "切换为竖屏", Toast.LENGTH_SHORT).show();
//        }


    }

    private int mTrick = 0;
    private void initLandAndPort() {
        setContentView(R.layout.activity_main);
        tv0 = (TextView) findViewById(R.id.textView0);

//        mScrollView = (ScrollView) findViewById(R.id.scrollView);
//        tvInScl = (TextView) findViewById(R.id.textViewInScl);
//        tvInScl.setText("empty");
//        System.out.println("tvInScl.getHeight()");
        sw = (Switch) findViewById(R.id.switch1);
        sw.setOnTouchListener(new SwitchTouchListener());
        sw.setChecked(mBind);
        changeTxt(nowCnt);

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

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onResume() {
        super.onResume();
//        System.out.println("app Resume******************************");
        sw.setChecked(mBind);
        mTrick = 0;



    }

    @Override
    public void onPause() {
        mTrick = 0;
        super.onPause();
//        System.out.println("app Pause******************************");
//        mHandler.removeMessages(0);
//        mHandler.removeMessages(1);
//        mHandler.removeMessages(2);
//        mHandler.removeMessages(3);
    }

    @Override
    public void onStop() {
        super.onStop();
//        mHandler.removeMessages(0);
//        mHandler.removeMessages(1);
//        mHandler.removeMessages(2);
//        mHandler.removeMessages(3);
//        System.out.println("app stop******************************");

    }

//    private void changeTxt(int idx, String str) {
//        tv[idx].setText(str);
//    }

    private void changeTxt(int n) {
        if (n < 1) {
            tv0.setText("Hello");
            if (n < 0) {
                nowCnt = 0;
                new ShowTask().execute();
            }
        } else {
            tv0.setText(Integer.toString(n));
        }

    }

    private void toggleBindService() {
        if (mBind) {
            unbindService(serviceConnection);
            serviceItent.putExtra(getString(R.string.ser_switch), false);
            stopService(serviceItent);
        } else {
            serviceItent.putExtra(getString(R.string.ser_switch), true);
            bindService(serviceItent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
        mBind = !mBind;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            countService = ((CountService.CountServiceBinder) service).getService();
            countService.setmHandler(mHandler);
            startService(serviceItent);
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            countService = null;
        }

    };
    @Override

    protected void onDestroy() {
        super.onDestroy();
// PREVENT leaked ServiceConnection
        if (mBind) {
            unbindService(serviceConnection);
            serviceItent.putExtra(getString(R.string.ser_switch), false);
            stopService(serviceItent);
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }

    /**
     * The encapsulated graphical view.
     */
    private GraphicalView mView;
    /**
     * The chart to be drawn.
     */
    private XYSeriesRenderer incomeRenderer;
    private XYMultipleSeriesRenderer multiRenderer;
    private GraphicalView openChart(String[] dates, int[] cnts) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
//        int[] x = {1, 2, 3, 4, 5, 6, 7, 8};
//        int[] income = {2000, 2500, 2700, 3000, 2800, 3500, 3700, 3800};
//        int[] expense = {2200, 2700, 2900, 2800, 2600, 3000, 3300, 3400};

        // Creating an  XYSeries for Income
        XYSeries incomeSeries = new XYSeries("Income");
        // Creating an  XYSeries for Expense
//        XYSeries expenseSeries = new XYSeries("Expense");
        // Adding data to Income and Expense Series
        for (int i = 0; i < cnts.length; i++) {
            incomeSeries.add(i + 1, cnts[i]);
//            expenseSeries.add(x[i],expense[i]);
        }

        // Creating a dataset to hold each series
        // Adding Income Series to the dataset
        dataset.addSeries(incomeSeries);
        // Adding Expense Series to dataset
//        dataset.addSeries(expenseSeries);

        // Creating XYSeriesRenderer to customize incomeSeries
        if (incomeRenderer == null) {
            incomeRenderer = new XYSeriesRenderer();
            incomeRenderer.setColor(getResources().getColor(R.color.blue));
            incomeRenderer.setPointStyle(PointStyle.SQUARE);
            incomeRenderer.setFillPoints(true);
            incomeRenderer.setLineWidth(2);
            incomeRenderer.setChartValuesTextSize(20);
            incomeRenderer.setDisplayChartValues(true);

        }

        // Creating XYSeriesRenderer to customize expenseSeries
//        XYSeriesRenderer expenseRenderer = new XYSeriesRenderer();
//        expenseRenderer.setColor(Color.YELLOW);
//        expenseRenderer.setPointStyle(PointStyle.CIRCLE);
//        expenseRenderer.setFillPoints(true);
//        expenseRenderer.setLineWidth(2);
//        expenseRenderer.setDisplayChartValues(true);
        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        if (multiRenderer == null) {
            multiRenderer = new XYMultipleSeriesRenderer();
            multiRenderer.setXLabels(0);
//            multiRenderer.setChartTitle("Income vs Expense Chart");
//            multiRenderer.setXTitle("Year 2012");
//            multiRenderer.setYTitle("Amount in Dollars");
            multiRenderer.setZoomButtonsVisible(false);
            multiRenderer.setAntialiasing(true);
            multiRenderer.setShowGridX(true);
            multiRenderer.setShowGridY(false);
            multiRenderer.setShowLabels(true, false);
            multiRenderer.setShowLegend(false);
            multiRenderer.setEnableBlackBackground(false);
            multiRenderer.setLabelsTextSize(15);
//            multiRenderer.setXLabelsColor(Color.CYAN);
//            multiRenderer.setShowLabels(false);
//            multiRenderer.setBackgroundColor(Color.GRAY);
//            multiRenderer.setApplyBackgroundColor(true);
//            for (int i = 0; i < dates.length; i++) {
//                multiRenderer.addXTextLabel(i + 1, dates[i]);
//            }

            // Adding incomeRenderer and expenseRenderer to multipleRenderer
            // Note: The order of adding dataseries to dataset and renderers to multipleRenderer
            // should be same
            multiRenderer.addSeriesRenderer(incomeRenderer);
        }
        multiRenderer.clearXTextLabels();
        for (int i = 0; i < dates.length; i++) {
            multiRenderer.addXTextLabel(i + 1, dates[i]);
        }

//        multiRenderer.addSeriesRenderer(expenseRenderer);

//        // Creating an intent to plot line chart using dataset and multipleRenderer
//        Intent intent = ChartFactory.getLineChartIntent(getBaseContext(), dataset, multiRenderer);
//
//        // Start Activity
//        startActivity(intent);

//        mChart = (AbstractChart) intent.getExtras().getSerializable(ChartFactory.CHART);
//        mView = new GraphicalView(this, mChart);
        return ChartFactory.getLineChartView(this, dataset, multiRenderer);
//        String title = intent.getExtras().getString(ChartFactory.TITLE);
//        if (title == null) {
//            requestWindowFeature(Window.FEATURE_NO_TITLE);
//        } else if (title.length() > 0) {
//            setTitle(title);
//        }
//        isChartReady = true;

//        setContentView(mView);
    }

    private synchronized void showChart(String[] dates, int[] cnts) {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ViewGroup vg1 = (ViewGroup) findViewById(R.id.contenChartView);
            vg1.removeAllViews();
            vg1.addView(openChart(dates, cnts));
        }

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initLandAndPort();
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            new ShowTask(mTrick).execute();
            if (mTrick > 1) {
                mTrick = 0;
            }
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            assert fab != null;
            fab.setVisibility(View.GONE);
            mTrick++;
            if (mTrick > 1) {
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.setVisibility(View.GONE);
                        mTrick = mTrick < 0 ? 0 : -1;
                        Snackbar.make(view, "Replace with your own action" + mTrick, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }
        }

    }

    class SwitchTouchListener implements View.OnTouchListener {
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
                    if (clickDuration < MAX_CLICK_DURATION) {
                        Switch sw = (Switch) v;
//                        sw.toggle();
//                        System.out.println("sw++++++++++++++" + sw.isChecked());

                        toggleBindService();
                        sw.setChecked(mBind);
//                        System.out.println("sw--------------" + sw.isChecked());
                    }
                }
            }
            return true;
        }
    }

    private class ShowTask extends AsyncTask<Integer, Integer, Boolean> {
        final StringBuilder outputBuilder = new StringBuilder();
        String[] dates;
        int[] outCnts;
        int mRowLimit = 0;
        boolean isShowDtl = false;
        public ShowTask() {
        }

        public ShowTask(int n) {
            if (n < 0) {
                isShowDtl = true;
            }
        }

        protected Boolean doInBackground(Integer... params) {
            SimpleDateFormat dateFormat;
            if (isShowDtl) {
                mRowLimit = -1;
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            } else {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            }
            TraceLogDBManager traceLogDBManager = TraceLogDBManager.GetInstance();
            Cursor cr = traceLogDBManager.getData(mRowLimit, "asc");

            int all = 0;
            if (cr != null && cr.getCount() > 0) {

                dates = new String[cr.getCount()];
                outCnts = new int[cr.getCount()];
                int i = 0;
                if (!isShowDtl) {
                    while (cr.moveToNext()) {
                        all = cr.getInt(0) + cr.getInt(1) + cr.getInt(2);
//                    outputBuilder.append(dateFormat.format(cr.getLong(3))).append(":").append(Integer.toString(all)  ).append("\n");
//                    Log.i(TAG,outputBuilder.toString());
//                    Log.i(TAG, dateFormat.format(cr.getInt(5)) + "--" + Integer.toString(all) + "--" + dateFormat.format(cr.getInt(6)))
//                    System.out.println("++++"+outputBuilder.toString());

//                    Log.i(TAG, dateFormat.format(cr.getLong(3))+ ":"
//                            + Integer.toString(all)  );

                        dates[i] = dateFormat.format(cr.getLong(3));
                        outCnts[i] = all;
                        i++;

                    }
                } else {
                    while (cr.moveToNext()) {
                        all = cr.getInt(0) + cr.getInt(1) + cr.getInt(2);
//                    outputBuilder.append(dateFormat.format(cr.getLong(3))).append(":").append(Integer.toString(all)  ).append("\n");
//                    Log.i(TAG,outputBuilder.toString());
//                    Log.i(TAG, dateFormat.format(cr.getInt(5)) + "--" + Integer.toString(all) + "--" + dateFormat.format(cr.getInt(6)))
//                    System.out.println("++++"+outputBuilder.toString());

//                    Log.i(TAG, dateFormat.format(cr.getLong(3))+ ":"
//                            + Integer.toString(all)  );

                        dates[i] = dateFormat.format(cr.getLong(3)) + "\n" + dateFormat.format(cr.getLong(4));
                        outCnts[i] = all;
                        i++;

                    }

                }
            } else {
                return false;

            }
            return true;
        }

        protected void onProgressUpdate(Integer... progress) {
//            setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Boolean result) {
//            System.out.println("++++"+outputBuilder.toString());
//            tvInScl.setText(outputBuilder.toString());
//            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            if (dates != null && outCnts != null) {
                showChart(dates, outCnts);
            }

        }
    }
}
