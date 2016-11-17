package com.example.hei123.permissiondog.PermissionDog;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by hei123 on 11/17/2016.
 * CopyRight @hei123
 */

public class PermissionDog {

    /**
     * 被拒绝的权限数组
     */
    private ArrayList<String> Denieds=new ArrayList<>();
    /**
     * 勾选了不再展示的数组
     */
    private ArrayList<String> NeverShow=new ArrayList<>();
    /**
     * 已获取权限的数组
     */
    private ArrayList<String> Granteds=new ArrayList<>();
    private PermissionDogAction mAction;

    /**
     * 构造函数直接传Action
     */
    public PermissionDog(PermissionDogAction action) {
        mAction = action;
    }

    /**
     * 申请单个权限
     *
     * @param activity
     * @param permission 权限
     */
    public void requestSinglePermissions(Activity activity, String permission) {
        String[] permissions = new String[]{permission};
        activity.requestPermissions(permissions, 0);
    }

    /**
     * 申请多个权限
     *
     * @param activity
     * @param permission 权限数组
     */
    public void requestMultiPermissions(Activity activity, @NonNull String[] permission) {
        activity.requestPermissions(permission, 1);
    }


    /**
     * 通知权限更改
     * @param activity
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void notifyPermissionsChanged(Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        requestPermissionForResult(activity, requestCode, permissions, grantResults);
    }


    /**
     * 处理返回结果
     *
     * @param activity
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    private void requestPermissionForResult(@NonNull Activity activity, int requestCode, @Nullable String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                //单个权限
                if (grantResults[0] == -1) {
                    if (!activity.shouldShowRequestPermissionRationale(permissions[0])) {
                        //自定义的对话框了
                        if(mAction!=null)
                            mAction.grant();
                    } else {
                        //没有勾选对话框
                        if(mAction!=null)
                            mAction.denied();
                    }
                } else {
                    //允许权限
                    if(mAction!=null)
                        mAction.never();
                }
                break;
            case 1:
                //多个权限申请时，每个权限都会有一个对话框，并带有勾选框
                for (int i=0;i<grantResults.length;i++){
                    //遍历所有结果
                    if (grantResults[i] == -1) {
                        if (!activity.shouldShowRequestPermissionRationale(permissions[i])) {
                            //自定义的对话框了
                            NeverShow.add(permissions[i]);
                        } else {
                            //没有勾选对话框
                            Denieds.add(permissions[i]);
                        }
                    } else {
                        //允许权限
                        Granteds.add(permissions[i]);
                    }
                }
                if (mAction!=null)
                    mAction.multi(Granteds.toArray(new String[Granteds.size()]),
                            Denieds.toArray(new String[Denieds.size()]),
                            NeverShow.toArray(new String[NeverShow.size()]));

                break;
        }

    }

    /**
     * 权限更改后的回调接口
     */
    public static abstract class PermissionDogAction {


        /**
         * 允许权限
         */
        public void grant(){}

        /**
         * 拒绝权限
         */
        public void denied(){}

        /**
         * 勾选了不再显示框
         */
        public void never(){}

        /**
         * 多个请求时的回调
         * @param granteds 获得权限的数组
         * @param denieds 未获得权限的数组
         * @param nevershows 勾选了nevershowagain
         */
        public void multi(String[] granteds, String[] denieds, String[] nevershows){}

    }
}
