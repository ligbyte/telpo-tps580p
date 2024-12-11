package com.stkj.cashier.greendao.tool;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.stkj.cashier.greendao.DaoMaster;


public class MyOpenHelper extends DaoMaster.OpenHelper {
    public MyOpenHelper(Context context, String name) {
        super(context, name);
    }

    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //切记不要调用super.onUpgrade(db,oldVersion,newVersion)
        if (oldVersion < newVersion) {
            db.beginTransaction();

//            // 如果不存在，则添加
//            Cursor cursor2 = db.rawQuery("select count(*) from sqlite_master where name='AUDIO_ENTITY' and sql like '%TAOBAO_GOODS_NAME%'", null);
//            if(cursor2.moveToFirst()){
//                int count = cursor2.getInt(0);
//                if(count <= 0){
//                    db.execSQL("alter table AUDIO_ENTITY add TAOBAO_GOODS_NAME TEXT");
//                }
//            }
//
//            MigrationHelper.migrate(db, MusicEntityDao.class);
//            // 如果不存在，则添加
//            Cursor cursor1 = db.rawQuery("select count(*) from sqlite_master where name='AUDIO_ENTITY' and sql like '%MEITUAN_GOODS_NAME%'", null);
//            if(cursor1.moveToFirst()){
//                int count = cursor1.getInt(0);
//                if(count <= 0){
//                    db.execSQL("alter table AUDIO_ENTITY add SHIPINHAOG_GOODS_NAME TEXT");
//                    db.execSQL("alter table AUDIO_ENTITY add MEITUAN_GOODS_NAME TEXT");
//                    db.execSQL("alter table AUDIO_ENTITY add XIAOHONGSHU_GOODS_NAME TEXT");
//                    db.execSQL("alter table AUDIO_ENTITY add KUAISHOU_GOODS_NAME TEXT");
//                }
//            }
//
//
//
//            MigrationHelper.migrate(db, AccountEntityDao.class);
//            // 如果不存在，则添加
//            Cursor cursor = db.rawQuery("select count(*) from sqlite_master where name='AUDIO_ENTITY' and sql like '%WEIGHT%'", null);
//            if(cursor.moveToFirst()){
//                int count = cursor.getInt(0);
//                if(count <= 0){
//                    db.execSQL("alter table AUDIO_ENTITY add WEIGHT INTEGER default 1");
//                    db.execSQL("update AUDIO_ENTITY set WEIGHT=1 where WEIGHT is null or WEIGHT=0");
//                }
//            }
//
//            // 如果不存在，则添加
//            Cursor cursorGoodsID = db.rawQuery("select count(*) from sqlite_master where name='AUDIO_ENTITY' and sql like '%GOODS_ID%'", null);
//            if(cursorGoodsID.moveToFirst()){
//                int count = cursorGoodsID.getInt(0);
//                if(count <= 0){
//                    db.execSQL("alter table AUDIO_ENTITY add GOODS_ID TEXT");
//                }
//            }
//
//            // 如果不存在，则添加
//            Cursor cursorGoodsName = db.rawQuery("select count(*) from sqlite_master where name='AUDIO_ENTITY' and sql like '%GOODS_NAME%'", null);
//            if(cursorGoodsName.moveToFirst()){
//                int count = cursorGoodsName.getInt(0);
//                if(count <= 0){
//                    db.execSQL("alter table AUDIO_ENTITY add GOODS_NAME TEXT");
//                }
//            }
//
            // 如果不存在，则添加
            Cursor cursorAudioSort = db.rawQuery("select count(*) from sqlite_master where name='COMPANY_MEMBERDB_ENTITY' and sql like '%ACCOUNT_TYPE%'", null);
            if(cursorAudioSort.moveToFirst()){
                int count = cursorAudioSort.getInt(0);
                if(count <= 0){
                    db.execSQL("alter table COMPANY_MEMBERDB_ENTITY add ACCOUNT_TYPE TEXT");
                    db.execSQL("alter table COMPANY_MEMBERDB_ENTITY add CALL_BACK INTEGER default -1");
                }
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        }
    }
}
