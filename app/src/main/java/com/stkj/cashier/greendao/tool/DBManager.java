package com.stkj.cashier.greendao.tool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.stkj.cashier.App;
import com.stkj.cashier.greendao.DaoMaster;
import com.stkj.cashier.greendao.DaoSession;


public class DBManager {
    private final static String dbName = "company_member";
    private static DBManager mInstance;
    private MyOpenHelper openHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    private DBManager(Context context) {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        openHelper = new MyOpenHelper(context, dbName, null);

        db = openHelper.getWritableDatabase();
        //在初始化greenDao的地方加上这一行
        db.disableWriteAheadLogging();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    /**
     * 获取单例引用
     *
     * @return
     */
    public static DBManager getInstance() {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager(App.applicationContext);
                }
            }
        }
        return mInstance;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }
}