package com.example.hei123.permissiondog.PermissionDog;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;
import java.security.Permissions;
import java.util.ArrayList;
import java.util.jar.Manifest;

/**
 * lishencai
 * connect to hei12138@outlook.com
 * Created by hei123 on 11/17/2016.
 * CopyRight @hei123
 * 修改或复制请保留此版权信息
 */

public class PermissionDog {

    /**
     * 被拒绝的权限数组
     */
    private ArrayList<String> Denieds;
    /**
     * 勾选了不再展示的数组
     */
    private ArrayList<String> NeverShow;
    /**
     * 已获取权限的数组
     */
    private ArrayList<String> Granteds;
    private WeakReference<PermissionDogAction> mAction;
    private static PermissionDog mPermissionDog;

    /**
     * 构造函数直接传Action
     */
    private PermissionDog() {
    }


    public static PermissionDog getInstance() {
        if (mPermissionDog == null) {
            mPermissionDog = new PermissionDog();
        }
        return mPermissionDog;
    }


    public void SetAction(PermissionDogAction action){
        this.mAction=new WeakReference<PermissionDogAction>(action);
    }

    /**
     * 申请单个权限
     *
     * @param activity
     * @param permission 权限
     */
    public void requestSinglePermissions(Activity activity, String permission) {
        String[] permissions = new String[]{permission};
        int isGrant = activity.checkCallingOrSelfPermission(permission);
        if (isGrant == PackageManager.PERMISSION_GRANTED) {
            PermissionDogAction permissionDogAction = mAction.get();
            if (permissionDogAction != null)
                permissionDogAction.grant();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            activity.requestPermissions(permissions, 0);
    }

    public void requestSinglePermissions(Fragment fragment,String permission){
        String[] permissions = new String[]{permission};
        int isGrant = fragment.getActivity().checkCallingOrSelfPermission(permission);
        if (isGrant == PackageManager.PERMISSION_GRANTED) {
            PermissionDogAction permissionDogAction = mAction.get();
            if (permissionDogAction != null)
                permissionDogAction.grant();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            fragment.requestPermissions(permissions, 0);
    }


    /**
     * 申请多个权限
     *
     * @param activity
     * @param permissions 权限数组
     */
    public void requestMultiPermissions(Activity activity, @NonNull String[] permissions) {
        ArrayList<String> deniedPermission = new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                int isGrant = activity.checkCallingOrSelfPermission(permission);
                if (isGrant == PackageManager.PERMISSION_DENIED) {
                    //没有获得权限
                    deniedPermission.add(permission);
                }
            }
            if (deniedPermission.size() > 0) {
                activity.requestPermissions(deniedPermission.toArray(new String[deniedPermission.size()]), 1);
            }else{
                PermissionDogAction permissionDogAction = mAction.get();
                if (permissionDogAction != null)
                    permissionDogAction.allallow();
            }
        }else{
            PermissionDogAction permissionDogAction = mAction.get();
            if (permissionDogAction != null)
                permissionDogAction.allallow();
        }
    }



    /**
     * 通知权限更改
     *
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
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        if (!activity.shouldShowRequestPermissionRationale(permissions[0])) {
                            PermissionDogAction permissionDogAction = mAction.get();
                            //自定义的对话框了
                            if (permissionDogAction != null)
                                permissionDogAction.never();
                        } else {
                            //没有勾选对话框
                            PermissionDogAction permissionDogAction = mAction.get();
                            if (permissionDogAction != null)
                                permissionDogAction.denied();
                        }
                } else {
                    //允许权限
                    PermissionDogAction permissionDogAction = mAction.get();
                    if (permissionDogAction != null)
                        permissionDogAction.grant();
                }
                break;
            case 1:
                NeverShow = new ArrayList<>();
                Granteds = new ArrayList<>();
                Denieds = new ArrayList<>();
                //多个权限申请时，每个权限都会有一个对话框，并带有勾选框
                for (int i = 0; i < grantResults.length; i++) {
                    //遍历所有结果
                    if (grantResults[i] == -1) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
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
                PermissionDogAction permissionDogAction = mAction.get();
                if (permissionDogAction != null)
                    permissionDogAction.multi(Granteds.toArray(new String[Granteds.size()]),
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
        public void grant() {
        }

        /**
         * 拒绝权限
         */
        public void denied() {
        }

        /**
         * 勾选了不再显示框
         */
        public void never() {
        }

        /**
         * 多个请求时的回调
         *
         * @param granteds   获得权限的数组
         * @param denieds    未获得权限的数组
         * @param nevershows 勾选了nevershowagain
         */
        public void multi(String[] granteds, String[] denieds, String[] nevershows) {
        }

        /**
         * 申请多个权限时，所有权限都被允许
         */
        public void allallow() {

        }
    }
}
