# PermissionDog
权限狗 权限申请
最近在一家公司实习，项目中需要用到适配安卓6.0以上的系统，我本来是想用其他人已经写好的权限申请框架来实现的，但是发现跟我的需求有点小区别，所以就自己写了一个

  这个权限申请的帮助类很小，只有一个java文件，复制到你的项目中就可以直接使用

没想到什么好名字 既然有权限，那就叫权限狗吧
#使用截图
![image](https://github.com/hei12138/PermissionDog/blob/master/app/src/main/java/com/example/hei123/permissiondog/ScreenShot/request_single_permission.jpg)
<br>请求单个权限<br>
![image](https://github.com/hei12138/PermissionDog/blob/master/app/src/main/java/com/example/hei123/permissiondog/ScreenShot/request_multi_permission.jpg)
<br>请求多个权限<br>
![image](https://github.com/hei12138/PermissionDog/blob/master/app/src/main/java/com/example/hei123/permissiondog/ScreenShot/failed_to_get_multi_permission.jpg)
<br>请求多个权限失败<br>
![image](https://github.com/hei12138/PermissionDog/blob/master/app/src/main/java/com/example/hei123/permissiondog/ScreenShot/never_get_permission.jpg)
<br>请求多个权限时有权限勾选了不再提醒<br>
##特性

1：支持单个和多个权限的申请

2：单个支持三个回调：允许，拒绝，勾中不再显示框并拒绝

3：多个权限回调时返回允许，拒绝，勾中不再显示框并拒绝的权限列表
##使用

###1.在你需要申请权限的Activity中重写onRequestPermissionsResult
```java
  @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionDog.getInstance().notifyPermissionsChanged(MainActivity.this, requestCode, permissions, grantResults);
    }
  ```
    
###2.在你需要申请权限的地方初始去添加一下代码

####请求单个权限
```java
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
```
####请求多个权限  
```java
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
```
###3.运行

##解释：

申请单个权限时不需要重写multi方法，

申请多个权限时不需要重写上面的三个方法
