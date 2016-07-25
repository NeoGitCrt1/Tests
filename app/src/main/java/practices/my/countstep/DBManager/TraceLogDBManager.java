package practices.my.countstep.DBManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import practices.my.countstep.MainActivity;

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
            mInstance = new TraceLogDBManager(MainActivity.getContext(),null);
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    private Cursor ExecSQLForCursor(String sql, String[] selectionArgs){
        SQLiteDatabase db =getWritableDatabase();
        Log.i("ExecSQLForCursor",sql);
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
    //添加照片信息
    public long insertData(Date startTime ,Date endTime, int[] cnts) throws ParseException {

        SQLiteDatabase db =getWritableDatabase();
        ContentValues cv = new ContentValues();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        cv.put(TraceLogDB.Entry.COLUMN_NAME_INSERT_DATE,  dateFormat.parse(dateFormat.format(startTime)).getTime());
        cv.put(TraceLogDB.Entry.COLUMN_NAME_START, startTime.getTime());
        cv.put(TraceLogDB.Entry.COLUMN_NAME_END,endTime.getTime());
        cv.put(TraceLogDB.Entry.COLUMN_NAME_CNT13, cnts[0]);
        cv.put(TraceLogDB.Entry.COLUMN_NAME_CNT12, cnts[1]);
        cv.put(TraceLogDB.Entry.COLUMN_NAME_CNT11, cnts[2]);
        cv.put(TraceLogDB.Entry.COLUMN_NAME_CNT10, cnts[3]);
        cv.put(TraceLogDB.Entry.COLUMN_NAME_DEL, false);

        return db.insert(TraceLogDB.Entry.TABLE_NAME, null, cv);
    }
    //查询
    public Cursor getData(int row,String sort){
        Cursor cur = null;
        try{
            String ord = (sort==null|| sort.toLowerCase().startsWith("a"))?" asc":" desc";
            String sql = "select sum(" + TraceLogDB.Entry.COLUMN_NAME_CNT13 + ") , sum(" +
                    TraceLogDB.Entry.COLUMN_NAME_CNT12 + "),sum(" +
                    TraceLogDB.Entry.COLUMN_NAME_CNT11 + "),"
                    +  TraceLogDB.Entry.COLUMN_NAME_INSERT_DATE
                    + " from "
                    + TraceLogDB.Entry.TABLE_NAME
                    + " group by " + TraceLogDB.Entry.COLUMN_NAME_INSERT_DATE;
            String[] args = {String.valueOf(row)};
            if(row>0){
                sql +=" limit ?";
            }else{
                args=null;
            }
            cur = ExecSQLForCursor(sql,args);
        }catch (Exception e) {
            cur = null;
//            Log.e("SearchPhoto Exception",e.getMessage());
            e.printStackTrace();
        }
        return cur;
    }
    //修改照片信息
    public int UpdateUserPhoto(int photoId,int classId,String title,String content, String tag){
        SQLiteDatabase db =getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("classId", classId);
        cv.put("title", title);
        cv.put("content", content);
        cv.put("tag", tag);
        String[] args = {String.valueOf(photoId)};
        return db.update(TraceLogDB.Entry.TABLE_NAME, cv, "photoId=?",args);
    }
    //删除照片信息
    public int DeleteUserPhoto(int photoId){
        SQLiteDatabase db =getWritableDatabase();
        String[] args = {String.valueOf(photoId)};
        return db.delete(TraceLogDB.Entry.TABLE_NAME, "photoId=?", args);
    }
}