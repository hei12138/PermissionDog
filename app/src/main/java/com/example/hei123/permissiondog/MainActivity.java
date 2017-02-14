package com.example.hei123.permissiondog;


import android.Manifest;
import android.app.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.hei123.permissiondog.PermissionDog.PermissionDog;

public class MainActivity extends Activity {

    String[] multiPermission = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE};
    String singlePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final int REQUEST_PERMISSION_SETTING = 0x001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View btn_single = findViewById(R.id.btn_single);
        View btn_multi = findViewById(R.id.btn_multi);
        btn_single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSinglePermission();
            }
        });
        btn_multi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestMultiPermission();
            }
        });
    }

    /**
     * 请求单个权限
     */
    private void requestSinglePermission() {
        PermissionDog.getInstance().SetAction(new PermissionDog.PermissionDogAction() {
            @Override
            public void never() {
                super.never();
                //已经勾选了不再提醒
                Toast.makeText(MainActivity.this, "已经拒绝权限，请前往设置开启", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
            }

            @Override
            public void denied() {
                super.denied();
                //未获取到权限
                Toast.makeText(MainActivity.this, "未获取到了单个权限", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void grant() {
                super.grant();
                //获取到了权限
                Toast.makeText(MainActivity.this, "获取到了单个权限", Toast.LENGTH_SHORT).show();

            }
        });
        PermissionDog.getInstance().requestSinglePermissions(MainActivity.this, singlePermission);
    }

    /**
     * 请求多个权限
     */
    private void requestMultiPermission() {
        PermissionDog.getInstance().SetAction(new PermissionDog.PermissionDogAction() {
            @Override
            public void allallow() {
                super.allallow();
                //在此设置权限全部被允许后的操作
                Toast.makeText(MainActivity.this, "多个权限全部被允许", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void multi(String[] granteds, String[] denieds, String[] nevershows) {
                if (nevershows.length != 0) {
                    new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme).setTitle("Permission")
                            .setMessage("PermissionDog需要摄像头和拨打电话的权限")
                            .setPositiveButton("前往设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //有权限被勾选了不再提醒
                            Toast.makeText(MainActivity.this, "有权限被勾选了不再提醒", Toast.LENGTH_SHORT).show();
                        }
                    }).create().show();
                } else {
                    if (denieds.length != 0) {
                        new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme).setTitle("Permission")
                                .setMessage("PermissionDog需要读写空间和拨打电话的权限")
                                .setPositiveButton("获取", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        PermissionDog.getInstance().requestMultiPermissions(MainActivity.this, multiPermission);
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "未获取到必要的权限", Toast.LENGTH_SHORT).show();
                            }
                        }).create().show();
                    } else {
                        //在此设置权限全部被允许后的操作
                        Toast.makeText(MainActivity.this, "多个权限全部被允许", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        PermissionDog.getInstance().requestMultiPermissions(MainActivity.this, multiPermission);

    }


    //重写权限申请的回调
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionDog.getInstance().notifyPermissionsChanged(MainActivity.this, requestCode, permissions, grantResults);
    }
}
