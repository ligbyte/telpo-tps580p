package com.stkj.cashier.greendao.biz;

import android.text.TextUtils;
import android.text.format.DateUtils;

import com.stkj.cashier.util.util.LogUtils;
import com.stkj.cashier.util.util.SPUtils;

import com.stkj.cashier.util.util.TimeUtils;
import com.google.gson.Gson;
import com.stkj.cashier.App;
import com.stkj.cashier.bean.db.CompanyMemberdbEntity;
import com.stkj.cashier.greendao.CompanyMemberdbEntityDao;
import com.stkj.cashier.greendao.tool.DBManager;

import org.greenrobot.greendao.async.AsyncSession;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;


import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mcv.facepass.FacePassException;

/**
 * 关键词业务类
 */
public class CompanyMemberBiz {

    public interface CompanyMemberListListener{
        void render(List<CompanyMemberdbEntity> beans);
    }

    /**
     * 音频库Activity获取账号列表

     * @param listener
     */
    public static void getCompanyMemberList(CompanyMemberListListener listener){
        AsyncSession asyncSession = DBManager.getInstance().getDaoSession().startAsyncSession();
        asyncSession.setListenerMainThread(operation -> {
            if(operation.isCompletedSucessfully() && listener != null){
                List<CompanyMemberdbEntity> entity = (List<CompanyMemberdbEntity>) operation.getResult();
                if(entity != null) {
                    listener.render(entity);
                } else {
                    listener.render(new ArrayList<>());
                }
            } else if (listener != null) {
                listener.render(new ArrayList<>());
            }
        });
        CompanyMemberdbEntityDao dao = DBManager.getInstance().getDaoSession().getCompanyMemberdbEntityDao();
        QueryBuilder<CompanyMemberdbEntity> queryBuilder = dao.queryBuilder();

        Query<CompanyMemberdbEntity> query = queryBuilder
//                .where(CompanyMemberdbEntityDao.Properties.IsDelete.eq(false),
//                AccountEntityDao.Properties.CreateUser.eq(SPUtils.getInstance().getString(SPKeys.PHONE_KEY)),
//                AccountEntityDao.Properties.PlatformType.eq(type),AccountEntityDao.Properties.DouyinType.eq(douyinType)
//                )
                .build();
        asyncSession.queryList(query);
    }
    /**
     * 音频库Activity获取账号列表

     * @param listener
     */
    public static void getCompanyMemberList(String AccountType,String dep,String keyword, CompanyMemberListListener listener){
        AsyncSession asyncSession = DBManager.getInstance().getDaoSession().startAsyncSession();
        asyncSession.setListenerMainThread(operation -> {
            if(operation.isCompletedSucessfully() && listener != null){
                List<CompanyMemberdbEntity> entity = (List<CompanyMemberdbEntity>) operation.getResult();
                if(entity != null) {
                    listener.render(entity);
                } else {
                    listener.render(new ArrayList<>());
                }
            } else if (listener != null) {
                listener.render(new ArrayList<>());
            }
        });
        CompanyMemberdbEntityDao dao = DBManager.getInstance().getDaoSession().getCompanyMemberdbEntityDao();
        QueryBuilder<CompanyMemberdbEntity> queryBuilder = dao.queryBuilder();
//        List<WhereCondition> whereConditions1 = new ArrayList<>();
//        List<WhereCondition> whereConditions2 = new ArrayList<>();
//        if (!AccountType.equals("全部人员")){
//            whereConditions1.add(CompanyMemberdbEntityDao.Properties.AccountType.eq(AccountType));
//            whereConditions2.add(CompanyMemberdbEntityDao.Properties.AccountType.eq(AccountType));
//        }
//        if (!dep.equals("全部部门")){
//            whereConditions1.add(CompanyMemberdbEntityDao.Properties.AccountType.eq(dep));
//            whereConditions2.add(CompanyMemberdbEntityDao.Properties.AccountType.eq(dep));
//        }
//        whereConditions1.add(CompanyMemberdbEntityDao.Properties.CardNumber.like(keyword));
//        queryBuilder.or(whereConditions1,whereConditions2);
//        whereConditions2.add(CompanyMemberdbEntityDao.Properties.FullName.like(keyword));
        Query<CompanyMemberdbEntity> query;
        if (!TextUtils.isEmpty(keyword)) {
            keyword = "%"+ keyword +"%";
            if (AccountType.equals("全部人员") && dep.equals("全部部门")) {
                query = queryBuilder
                        .where(queryBuilder.or(CompanyMemberdbEntityDao.Properties.FullName.like(keyword)
//                                ,CompanyMemberdbEntityDao.Properties.CardNumber.like(keyword)
                                ,CompanyMemberdbEntityDao.Properties.Phone.like(keyword)))
                        .build();
            } else if (AccountType.equals("全部人员") && !dep.equals("全部部门")) {
                query = queryBuilder
                        .where(CompanyMemberdbEntityDao.Properties.DepNameType.eq(dep)
                                , queryBuilder.or(CompanyMemberdbEntityDao.Properties.FullName.like(keyword)
//                                        ,CompanyMemberdbEntityDao.Properties.CardNumber.like(keyword)
                                        ,CompanyMemberdbEntityDao.Properties.Phone.like(keyword)))
                        .build();
            } else if (dep.equals("全部部门")) {
                query = queryBuilder
                        .where(CompanyMemberdbEntityDao.Properties.AccountType.eq(AccountType)
                                , queryBuilder.or(CompanyMemberdbEntityDao.Properties.FullName.like(keyword)
//                                        ,CompanyMemberdbEntityDao.Properties.CardNumber.like(keyword)
                                        ,CompanyMemberdbEntityDao.Properties.Phone.like(keyword)))
                        .build();
            } else {
                query = queryBuilder
                        .where(CompanyMemberdbEntityDao.Properties.AccountType.eq(AccountType),
                                CompanyMemberdbEntityDao.Properties.DepNameType.eq(dep)
                                , queryBuilder.or(CompanyMemberdbEntityDao.Properties.FullName.like(keyword)
//                                        ,CompanyMemberdbEntityDao.Properties.CardNumber.like(keyword)
                                        ,CompanyMemberdbEntityDao.Properties.Phone.like(keyword)))
                        .build();
            }
        }else {
            if (AccountType.equals("全部人员") && dep.equals("全部部门")) {
                query = queryBuilder
                        .build();
            } else if (AccountType.equals("全部人员") && !dep.equals("全部部门")) {
                query = queryBuilder
                        .where(CompanyMemberdbEntityDao.Properties.DepNameType.eq(dep))
                        .build();
            } else if (dep.equals("全部部门")) {
                query = queryBuilder
                        .where(CompanyMemberdbEntityDao.Properties.AccountType.eq(AccountType))
                        .build();
            } else {
                query = queryBuilder
                        .where(CompanyMemberdbEntityDao.Properties.AccountType.eq(AccountType),
                                CompanyMemberdbEntityDao.Properties.DepNameType.eq(dep))
                        .build();
            }
        }
        asyncSession.queryList(query);
    }




    /**
     * 新增
     * @param entity 音频对象
     * @return ID
     */
    public static long addCompanyMember(CompanyMemberdbEntity entity){
        if(entity == null){
            return 0;
        }
        return DBManager.getInstance().getDaoSession().getCompanyMemberdbEntityDao().insertOrReplace(entity);
    }
    /**
     * 新增
     * @param entities
     */
    public static void addCompanyMembers(List<CompanyMemberdbEntity> entities){
        if(entities == null || entities.size() <= 0){
            return;
        }
        for (int i = 0; i < entities.size(); i++) {
            CompanyMemberdbEntity unique = DBManager.getInstance().getDaoSession().getCompanyMemberdbEntityDao().queryBuilder()
                    .where(CompanyMemberdbEntityDao.Properties.UniqueNumber.eq(entities.get(i).getUniqueNumber()))
                    .unique();
            if (unique == null) {
                if (entities.get(i).getCardState() !=64) {
                    String date = TimeUtils.millis2String(System.currentTimeMillis());
                    entities.get(i).setOpeningDate(date);
                    DBManager.getInstance().getDaoSession().getCompanyMemberdbEntityDao().insert(entities.get(i));
                }
            }else {
                if(App.instance.getMFacePassHandler()!=null&&unique.getFaceToken()!=null){
                    try {
                        boolean b = App.instance.getMFacePassHandler().deleteFace(unique.getFaceToken().getBytes(StandardCharsets.ISO_8859_1));
                        LogUtils.e("删除地库人脸token成功",unique.getFullName()+"=="+b);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.e("删除地库人脸token失败",unique.getFullName()+e.getMessage());
                    }
                }

                if (entities.get(i).getCardState() !=64) {
                    entities.get(i).setId(unique.getId());
                    String date = TimeUtils.millis2String(System.currentTimeMillis());
                    entities.get(i).setOpeningDate(date);
                   // DBManager.getInstance().getDaoSession().getCompanyMemberdbEntityDao().update(entities.get(i));
                    DBManager.getInstance().getDaoSession().getCompanyMemberdbEntityDao().delete(unique);
                    DBManager.getInstance().getDaoSession().getCompanyMemberdbEntityDao().insert(entities.get(i));
                }else {
                    DBManager.getInstance().getDaoSession().getCompanyMemberdbEntityDao().delete(unique);
                }


            }

        }

    }
//    public static CompanyMemberdbEntity getCompanyMember(String id){
//        return DBManager.getInstance().getDaoSession().getCompanyMemberdbEntityDao().queryBuilder()
////                .where(AccountEntityDao.Properties.IsDelete.eq(false),
////                        AccountEntityDao.Properties.ID.eq(id),
////                        AccountEntityDao.Properties.PlatformType.eq(platformType),
////                        AccountEntityDao.Properties.CreateUser.eq(SPUtils.getInstance().getString(SPKeys.PHONE_KEY)))
//                .unique();
//    }
    public static CompanyMemberdbEntity getCompanyMember(String faceToken){
        return DBManager.getInstance().getDaoSession().getCompanyMemberdbEntityDao().queryBuilder()
                .where(CompanyMemberdbEntityDao.Properties.FaceToken.eq(faceToken) )
                .unique();
    }
    public static CompanyMemberdbEntity getCompanyMemberByCard(String card){
        return DBManager.getInstance().getDaoSession().getCompanyMemberdbEntityDao().queryBuilder()
                .where(CompanyMemberdbEntityDao.Properties.CardNumber.eq(card) )
                .unique();
    }
    public static CompanyMemberdbEntity getCompanyMemberByPhone(String phone){
        return DBManager.getInstance().getDaoSession().getCompanyMemberdbEntityDao().queryBuilder()
                .where(CompanyMemberdbEntityDao.Properties.Phone.eq(phone) )
                .unique();
    }
    public static CompanyMemberdbEntity getCompanyMemberByCode(String code){
        final CompanyMemberdbEntity[] companyMemberdbEntity = {new CompanyMemberdbEntity()};
        getCompanyMemberList(new CompanyMemberListListener() {
            @Override
            public void render(List<CompanyMemberdbEntity> beans) {
                for (int i = 0; i < beans.size(); i++) {
                    String sub = beans.get(i).getPhone().substring(beans.get(i).getPhone().length()-6);
                    if (sub.equals(code)){
                        companyMemberdbEntity[0] = beans.get(i);
                    }
                }
            }
        });
        return companyMemberdbEntity[0];
    }
    /**
     * 修改
     * @param entity
     */
    public static void updateCompanyMember(CompanyMemberdbEntity entity){
        if(entity == null){
            return;
        }
//        entity.setUpdateTime(DateUtils.dateToString(new Date(),"yyyy-MM-dd HH:mm:ss"));
//        entity.setUpdateUser(SPUtils.getInstance().getString(SPKeys.PHONE_KEY));
        DBManager.getInstance().getDaoSession().getCompanyMemberdbEntityDao().update(entity);
    }

    /**
     * 修改
     * @param entities
     */
    public static void updateCompanyMembers(List<CompanyMemberdbEntity> entities){
        if(entities == null || entities.size() <= 0){
            return;
        }
        DBManager.getInstance().getDaoSession().getCompanyMemberdbEntityDao().updateInTx(entities);
    }

    /**
     * 删除
     * @param entity
     */
    public static void deleteCompanyMember(CompanyMemberdbEntity entity){
        if(entity == null){
            return;
        }
//        entity.setUpdateTime(DateUtils.dateToString(new Date(),"yyyy-MM-dd HH:mm:ss"));
//        entity.setUpdateUser(SPUtils.getInstance().getString(SPKeys.PHONE_KEY));
//        entity.setIsDelete(true);
        DBManager.getInstance().getDaoSession().getCompanyMemberdbEntityDao().updateInTx(entity);
    }

    /**
     * 删除
     * @param entities
     */
    public static void deleteCompanyMembers(List<CompanyMemberdbEntity> entities){
        if(entities != null && entities.size() > 0) {
            for (int i = 0; i < entities.size(); i++) {
                deleteCompanyMember(entities.get(i));
            }
        }
    }

    public static void getCompanyMember2(){
        QueryBuilder<CompanyMemberdbEntity> companyMemberdbEntityQueryBuilder = DBManager.getInstance().getDaoSession().getCompanyMemberdbEntityDao().queryBuilder();

        List<CompanyMemberdbEntity> list = companyMemberdbEntityQueryBuilder.list();

        LogUtils.e("本地数据库人脸", list.size());

        for (int i = 0; i < list.size(); i++) {
            CompanyMemberdbEntity companyMemberdbEntity = list.get(i);
             LogUtils.e("showConfirmDialog"+companyMemberdbEntity.getFullName()+"/"+companyMemberdbEntity.getUserNumber()+"/"+companyMemberdbEntity.getFaceToken());
        }


    }
    /**
     * 插入数据
     * @param audioEntity
     */
    public static void restoreCompanyMember(CompanyMemberdbEntity audioEntity){
        DBManager.getInstance().getDaoSession().getCompanyMemberdbEntityDao().insert(audioEntity);
    }
    /**
     * 删除所有数据
     */
    public static void deleteRelationAll(){
        DBManager.getInstance().getDaoSession().getCompanyMemberdbEntityDao().queryBuilder()
                .buildDelete().executeDeleteWithoutDetachingEntities();
//        DBManager.getInstance().getDaoSession().getAudioLibraryRelationEntityDao().deleteAll();
    }
}
