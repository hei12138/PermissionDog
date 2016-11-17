package com.example.hei123.permissiondog;

import android.Manifest;
import android.app.Activity;
import android.app.LauncherActivity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.hei123.permissiondog.PermissionDog.PermissionDog;

public class Launcher extends Activity {

    private PermissionDog permissionDog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        Button btn_request = (Button) findViewById(R.id.btn_request);
        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionDog = new PermissionDog(new PermissionDog.PermissionDogAction() {
                    @Override
                    public void multi(String[] granteds, String[] denieds, String[] nevershows) {
                        Log.e("tag","多个回调");
                    }
                });
                //开始申请权限
                //permissionDog.requestSinglePermissions(Launcher.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
                permissionDog.requestMultiPermissions(Launcher.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CALL_PHONE});

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionDog.notifyPermissionsChanged(this,requestCode,permissions,grantResults);
    }
}
