package practices.my.countstep.DBManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import practices.my.countstep.common.db.ContextSaver;


public class TraceLogDBManager extends SQLiteOpenHelper {
    final private static String mDbName="TraceLogDB";
    final private static int mDbVersion=1;
    private static TraceLogDBManager mInstance=null;
    
    final private static String mCreateSqlForNoteClass="create table if not exists NoteClass(classId integer primary key asc autoincrement,className NVARCHAR(100),rowTime timestamp default (datetime('now', 'localtime')))";
    final private static String mCreateSqlForUserPhoto="create table if not exists UserPhoto(photoId integer primary key asc autoincrement,photoName VARCHAR(200),userPt VARCHAR(200),title VARCHAR(255),classId integer,content NVARCHAR(250),tag NVARCHAR(200),remark text,status integer default 0,rowTime timestamp default (datetime('now', 'localtime')))";
    final private static String[] mInsertSqlForNoteClass={"insert into NoteClass(className) values('默认分类[私有]');","insert into NoteClass(className) values('读书笔记[私有]');","insert into NoteClass(className) values('电子资料[公开]');"};
    private TraceLogDBManager(Context context, CursorFactory factory) {
        super(context, mDbName, factory, mDbVersion);
    }

    public static TraceLogDBManager GetInstance()
    {
        if(mInstance==null){
            mInstance = new TraceLogDBManager(ContextSaver.getContext(), null);
        }
        return mInstance;
    }

    private static final String INTEGER_TYPE = " INTEGER";
    private static final String LONG_TYPE = " LONG";
    private static final String BOOLEAN_TYPE = " BOOLEAN";
    private static final String DATETIME_TYPE = " DATETIME";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TraceLogDB.Entry.TABLE_NAME + " (" +
                    TraceLogDB.Entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TraceLogDB.Entry.COLUMN_NAME_CNT13 + INTEGER_TYPE + COMMA_SEP +
                    TraceLogDB.Entry.COLUMN_NAME_CNT12 + INTEGER_TYPE + COMMA_SEP +
                    TraceLogDB.Entry.COLUMN_NAME_CNT11 + INTEGER_TYPE + COMMA_SEP +
                    TraceLogDB.Entry.COLUMN_NAME_CNT10 + INTEGER_TYPE + COMMA_SEP +
                    TraceLogDB.Entry.COLUMN_NAME_START + LONG_TYPE + COMMA_SEP +
                    TraceLogDB.Entry.COLUMN_NAME_END + LONG_TYPE + COMMA_SEP +
                    TraceLogDB.Entry.COLUMN_NAME_INSERT_DATE + LONG_TYPE + COMMA_SEP +
                    TraceLogDB.Entry.COLUMN_NAME_DEL + BOOLEAN_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TraceLogDB.Entry.TABLE_NAME;
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_ENTRIES);


//        add dummy data !!!!!!
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        Calendar rightNow = Calendar.getInstance();
//        rightNow.setTime(new Date(System.currentTimeMillis()));
//
//
//        int[] cnts = {0, 0, 0, 0};
//        for (int i = 0; i < 10; i++) {
//
//
////            rightNow.add(Calendar.YEAR,-1);//日期减1年
////            rightNow.add(Calendar.MONTH,3);//日期加3个月
//            rightNow.add(Calendar.DAY_OF_YEAR, i);//日期加10天
//            Date st = rightNow.getTime();
//
//            cnts[0] = 10 + i;
//            cnts[1] = 50 + i;
//            cnts[2] = 100 + i;
//            cnts[3] = 100 + i;
//
//            try {
//                insertData(st, st, cnts, db);
//            } catch (ParseException e) {
//            }


//        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    private Cursor ExecSQLForCursor(String sql, String[] selectionArgs){
        SQLiteDatabase db =getWritableDatabase();
//        Log.i("ExecSQLForCursor",sql);
        return db.rawQuery(sql, selectionArgs);
    }
    private void ExecSQL(String sql){
        try{
            SQLiteDatabase db =getWritableDatabase();
            ExecSQL(sql,db);
        }catch(Exception e){
            Log.e("ExecSQL Exception",e.getMessage());
            e.printStackTrace();
        }
    }
    private void ExecSQL(String sql,SQLiteDatabase db ){
        try{
            db.execSQL(sql);
            Log.i("ExecSQL",sql);
        }catch(Exception e){
            Log.e("ExecSQL Exception",e.getMessage());
            e.printStackTrace();
        }
    }

    private void setContent(ContentValues cv, Date startTime, Date endTime, int[] cnts) {
        cv.put(TraceLogDB.Entry.COLUMN_NAME_START, startTime.getTime());
        cv.put(TraceLogDB.Entry.COLUMN_NAME_END, endTime.getTime());
        cv.put(TraceLogDB.Entry.COLUMN_NAME_CNT13, cnts[0]);
        cv.put(TraceLogDB.Entry.COLUMN_NAME_CNT12, cnts[1]);
        cv.put(TraceLogDB.Entry.COLUMN_NAME_CNT11, cnts[2]);
        cv.put(TraceLogDB.Entry.COLUMN_NAME_CNT10, cnts[3]);
    }
    //添加
    public long insertData(Date startTime, Date endTime, int[] cnts, SQLiteDatabase db) throws ParseException {

        ContentValues cv = new ContentValues();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        cv.put(TraceLogDB.Entry.COLUMN_NAME_INSERT_DATE,  dateFormat.parse(dateFormat.format(startTime)).getTime());
        cv.put(TraceLogDB.Entry.COLUMN_NAME_DEL, false);
        setContent(cv, startTime, endTime, cnts);
        return db.insert(TraceLogDB.Entry.TABLE_NAME, null, cv);
    }

    public long insertData(Date startTime, Date endTime, int[] cnts) {

        SQLiteDatabase db = getWritableDatabase();
        try {
            return insertData(startTime, endTime, cnts, db);
        } catch (ParseException e) {
            return -1;
        }
    }
    //添加
    public long updateData(Date startTime, Date endTime, int[] cnts) {
        SQLiteDatabase db =getWritableDatabase();
        ContentValues cv = new ContentValues();
        setContent(cv, startTime, endTime, cnts);
        String[] args = {Long.toString(startTime.getTime())};
        return db.update(TraceLogDB.Entry.TABLE_NAME, cv, TraceLogDB.Entry.COLUMN_NAME_START + "=?",args);
    }
    public long dealData(Date startTime ,Date endTime, int[] cnts) throws ParseException {

        long res = 0;

        if(getData(startTime).moveToNext()){
            res = updateData(startTime,endTime,cnts);
        }else{
            res = insertData(startTime,endTime,cnts);
        }

        return res;
    }
    //查询
    public Cursor getData(Date startTime){
        Cursor cur = null;
//        try{

            String sql = "select " + TraceLogDB.Entry.COLUMN_NAME_CNT13
                    + " from "
                    + TraceLogDB.Entry.TABLE_NAME
                    + " where " + TraceLogDB.Entry.COLUMN_NAME_START
                    + "= ?" ;
            String[] args = {Long.toString(startTime.getTime())};
            cur = ExecSQLForCursor(sql,args);
//        }catch (Exception e) {
//            cur = null;
////            Log.e("SearchPhoto Exception",e.getMessage());
////            e.printStackTrace();
//        }
        return cur;
    }

    private StringBuilder outputBuilder = new StringBuilder();
    private final String sql = "select sum(" + TraceLogDB.Entry.COLUMN_NAME_CNT13 + ") , sum(" +
            TraceLogDB.Entry.COLUMN_NAME_CNT12 + "),sum(" +
            TraceLogDB.Entry.COLUMN_NAME_CNT11 + "),"
            + TraceLogDB.Entry.COLUMN_NAME_INSERT_DATE
            + " from "
            + TraceLogDB.Entry.TABLE_NAME
            + " group by " + TraceLogDB.Entry.COLUMN_NAME_INSERT_DATE;
    private final String sqlment = "select " + TraceLogDB.Entry.COLUMN_NAME_CNT13 + "," +
            TraceLogDB.Entry.COLUMN_NAME_CNT12 + "," +
            TraceLogDB.Entry.COLUMN_NAME_CNT11 + ","
            + TraceLogDB.Entry.COLUMN_NAME_START + ","
            + TraceLogDB.Entry.COLUMN_NAME_END
            + " from "
            + TraceLogDB.Entry.TABLE_NAME
            + " where "
            + TraceLogDB.Entry.COLUMN_NAME_INSERT_DATE
            + " = ?";
    public Cursor getData(int row,String sort){
        Cursor cur = null;
        try{
            String ord = (sort==null|| sort.toLowerCase().startsWith("a"))?" asc":" desc";

            outputBuilder.append(sql);
            String[] args = {String.valueOf(row)};
            if(row>0){
                outputBuilder.append(" limit ?");
            } else if (row < 0) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                args[0] = String.valueOf(dateFormat.parse(dateFormat.format(System.currentTimeMillis())).getTime());
            } else {
                args = null;
            }

            if (row < 0) {
                cur = ExecSQLForCursor(sqlment, args);
            } else {
                cur = ExecSQLForCursor(outputBuilder.toString(), args);
            }
        }catch (Exception e) {
            cur = null;
        }
        outputBuilder = new StringBuilder();
        return cur;
    }
    //删除照片信息
    public int DeleteUserPhoto(int photoId){
        SQLiteDatabase db =getWritableDatabase();
        String[] args = {String.valueOf(photoId)};
        return db.delete(TraceLogDB.Entry.TABLE_NAME, "photoId=?", args);
    }
}