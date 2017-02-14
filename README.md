# PermissionDog
权限狗 权限申请
最近在一家公司实习，项目中需要用到适配安卓6.0以上的系统，我本来是想用其他人已经写好的权限申请框架来实现的，但是发现跟我的需求有点小区别，所以就自己写了一个

  这个权限申请的帮助类很小，只有一个java文件，复制到你的项目中就可以直接使用

没想到什么好名字 既然有权限，那就叫权限狗吧
#使用截图
![image](https://github.com/hei12138/PermissionDog/blob/master/app/src/main/java/com/example/hei123/permissiondog/ScreenShot/failed_to_get_multi_permission.jpg)
#特性

1：支持单个和多个权限的申请

2：支持三个回调：允许，拒绝，勾中不再显示框并拒绝

3：多个回调时返回允许，拒绝，勾中不再显示框并拒绝的权限列表
#使用

1.在你需要申请权限的Activity中重写onRequestPermissionsResult

 @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionDog.notifyPermissionsChanged(this, requestCode, permissions, grantResults);
    }
2.在你需要申请权限的地方初始去添加一下代码

permissionDog = new PermissionDog(new PermissionDog.PermissionDogAction() {

                    @Override
                    public void denied() {
                        //单个权限时被拒绝
                    }

                    @Override
                    public void grant() {
                        super.grant();
                        //单个权限时被允许
                    }

                    @Override
                    public void never() {
                        super.never();
                        //单个权限被拒绝并勾中不再显示框
                    }

                    @Override
                    public void multi(String[] granteds, String[] denieds, String[] nevershows) {
                        Log.e("tag", "多个回调");
                    }
                });
                //开始申请权限
　　　　　　　　　　//申请单个权限
                //permissionDog.requestSinglePermissions(Launcher.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
　　　　　　　　　　//申请多个权限
                permissionDog.requestMultiPermissions(Launcher.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CALL_PHONE});
　　3.运行

#解释：

申请单个权限时不需要重写multi方法，

申请多个权限时不需要重写上面的三个方法

最后：在我的github上有权限狗
