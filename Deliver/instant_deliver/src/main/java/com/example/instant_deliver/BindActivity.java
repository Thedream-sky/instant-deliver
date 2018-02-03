package com.example.instant_deliver;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instant_deliver.beans.Users;
import com.example.instant_deliver.identifyView.Topbar;
import com.example.instant_deliver.tools.getConnState;
import com.example.instant_deliver.tools.topStatusTool;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class BindActivity extends Activity implements View.OnClickListener {
    private static final int PHOTO_REQUEST_CAREMA = 111;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 222;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 333;// 结果
    private static final int DIALOG_CREATE= 444; //创建上传头像提示弹窗
    //保存截屏uri
    private Uri imgUri;
    //popwindow弹窗
    private PopupWindow popupWindow;
    //导航栏
    private Topbar topbar;
    //电话
    private TextView bindPhone;
    //用户名
    private TextView bindUsername;
    //签名
    private TextView binbSingnature;
    //头像
    private ImageView head;
    //临时文件
    private File tempFile;
    private static String PHOTO_FILE_NAME = null;
    //进度条
    private ProgressDialog progressDialog;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                //获取用户信息
                getinfo();
            }
            if (msg.what == 2) {
                //显示popwindow
                showpop();
            }
            //获取地址
            if (msg.obj != null) {
                //上传头像
                uplodehead();
            }
            if(msg.what == DIALOG_CREATE){
                //创建提示框
                createProgressbar();
            }
        }
    };

    //上传头像
    private void uplodehead() {
        final Users users = BmobUser.getCurrentUser(Users.class);
        //如果已经存在头像，就先删除原来的头像
        if (users.getHeadurl() != null) {
            BmobFile file1 = new BmobFile();
            file1.setUrl(users.getHeadurl());
            file1.delete(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Log.i("del", "原有图像删除成功");
                    } else {
                        Log.i("del", "原有图像删除失败" + e.toString());
                    }
                }
            });
        }
        //读取用户头像
        File appDir = new File(Environment.getExternalStorageDirectory(), "instant_deliver");
        //当文件夹存在
        if (appDir.exists()) {
            String fileName = PHOTO_FILE_NAME;
            File file = new File(appDir, fileName);
            if (file.exists()) {
                final BmobFile bmobFile = new BmobFile(file);
                bmobFile.uploadblock(new UploadFileListener() {
                    @Override
                    public void onStart() {
                        //创建progressbar
                        handler.sendEmptyMessage(DIALOG_CREATE);
                    }

                    @Override
                    public void onProgress(Integer value) {
                        //progressDialog.setProgress(value);
                    }

                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            progressDialog.dismiss();
                            getImage();
                            Toast.makeText(BindActivity.this, "头像上传成功", Toast.LENGTH_SHORT).show();
                            Users user = new Users();
                            //成功保存图像地址
                            user.setHeadurl(bmobFile.getFileUrl());
                            user.update(users.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {

                                }
                            });
                        } else {
                            Toast.makeText(BindActivity.this, "头像上传失败" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    public static Uri geturi(Intent intent,Context context) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=")
                        .append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[] { MediaStore.Images.ImageColumns._ID },
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                }
                if (index == 0) {
                } else {
                    Uri uri_temp = Uri
                            .parse("content://media/external/images/media/"
                                    + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //接受返回值
        if (requestCode == 1 && resultCode == 2) {
            //刷新页面
            //获取信息
            getinfo();
        }
        /**
         * 图片选择返回结果
         */
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                //Log.i("kk",uri.toString());
                uri = geturi(data,BindActivity.this);
                crop(uri);
            }

        } else if (requestCode == PHOTO_REQUEST_CAREMA) {
            // 从相机返回的数据
            if (hasSdcard()) {
                //当安卓版本低于6.0时
                if(Build.VERSION.SDK_INT < 24){
                    Log.i("uri",Uri.fromFile(tempFile).toString());
                    crop(Uri.fromFile(tempFile));
                }else {
                    Uri outuri = FileProvider.getUriForFile(this,"com.example.instant_deliver.fileprovider", tempFile);
                    Log.i("uri",outuri.toString());
                    crop(outuri);
                    Log.i("hahha",outuri.toString());
                }
            } else {
                Toast.makeText(BindActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == PHOTO_REQUEST_CUT) {
            //返回传值
            Intent intent = new Intent();
            Users user = BmobUser.getCurrentUser(Users.class);
            //传对象
            intent.putExtra("user", user);
            setResult(4, intent);

            Uri uri = null;
           /**
            * 调用系统剪切库会出现各种问题，使用第三方解决此问题
            *  // 从剪切图片返回的数据
            // 适配不同机型返回uri
            if (data != null) {
                //有的机型无法直接获取图片地址
                uri = data.getData();
                if(uri == null){
                    uri = imgUri;
                }
            }else {
                uri = imgUri;
            }*/
            if(data != null){
                uri = UCrop.getOutput(data);
                if(uri == null){
                    uri = imgUri;
                }
                /**
                 *Bitmap bitmap = data.getParcelableExtra("data");
                 *在onActivityResult中通过data.getParcelableExtra("data")来获取数据容易出错， 剪切输出的值不能太大
                 * 建议根据uri来获取bitmip
                 */
                Bitmap myBitmap = null;
                myBitmap =getBitmapFromUri(uri);
                if (myBitmap != null) {
                    //判断网络状态
                    if (getConnState.isConn(BindActivity.this)) {
                        //保存头像
                        saveImage(myBitmap);
                        //上传图像
                        Message message = new Message();
                        message.obj = myBitmap;
                        handler.sendMessageDelayed(message, 500);
                    } else {
                        Toast.makeText(BindActivity.this, "当前网络不可用", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            try {
                if(tempFile != null){
                    // 将临时文件删除
                    tempFile.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind);
        //解决沉浸式状态栏问题
        topStatusTool.applyKitKatTranslucency(this,R.color.deepskyblue);
        //初始化
        init();
        //异步获取数据请求
        handler.sendEmptyMessage(1);
    }

    //点击返回键返回数据
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if( keyCode==KeyEvent.KEYCODE_BACK ){
            //返回传值
            Intent intent = new Intent();
            Users user = BmobUser.getCurrentUser(Users.class);
            //传对象
            intent.putExtra("user", user);
            setResult(4, intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    //初始化控件
    private void init() {
        //导航栏
        topbar = (Topbar) findViewById(R.id.bind_topbar);
        bindPhone = (TextView) findViewById(R.id.bind_phone);
        bindUsername = (TextView) findViewById(R.id.bind_user);
        binbSingnature = (TextView) findViewById(R.id.bind_singnature);
        head = (ImageView) findViewById(R.id.bind_headImg);


        //设置监听
        bindUsername.setOnClickListener(this);
        binbSingnature.setOnClickListener(this);
        bindPhone.setOnClickListener(this);
        head.setOnClickListener(this);

        topbar.setOnTopbarClickListener(new Topbar.topbarClickListener() {
            @Override
            public void leftclick() {
                //返回传值
                Intent intent = new Intent();
                Users user = BmobUser.getCurrentUser(Users.class);
                //传对象
                intent.putExtra("user", user);
                setResult(4, intent);
                finish();
            }

            @Override
            public void rightclick() {

            }
        });
    }

    //获取用户信息
    private void getinfo() {
        Users user = BmobUser.getCurrentUser(Users.class);

        //图片名（不同用户的有不同的id,自然头像图片就不一样）
        PHOTO_FILE_NAME = user.getObjectId() + ".jpg";
        //获取头像
        getImage();

        if (user.getSignature() != null) {
            binbSingnature.setText(user.getSignature());
        } else {
            //同一默认
            binbSingnature.setText("便捷生活就来校园速递吧！");
        }

        if (user.getMobilePhoneNumber() == null || user.getMobilePhoneNumber().equals("")) {
            bindPhone.setText("手机未绑定");
            bindPhone.setTextColor(Color.parseColor("#00BFFF"));
        } else {
            String phone = user.getMobilePhoneNumber();
            //拼接为保密号码
            phone = phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4, phone.length());
            bindPhone.setText(phone);
            bindPhone.setTextColor(Color.parseColor("#696969"));
        }
        bindUsername.setText(user.getUsername());
    }

    //显示popwindows弹出框
    private void showpop() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.update_head_popwindow, null);
        Button imagestore = (Button) contentView.findViewById(R.id.imagestore);
        Button tookphoto = (Button) contentView.findViewById(R.id.tookphoto);

        //PopupWindow出现之后，默认的是所有的操作都无效的，除了HOME键
        popupWindow = new PopupWindow(contentView);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        View rootview = LayoutInflater.from(BindActivity.this).inflate(R.layout.activity_bind, null);

        //这样，显示的时候，popupWindow获取啦焦点，后面的内容为非活动。
        popupWindow.setFocusable(true);
        //显示方式,这个必须在最后面，要不所有之后的属性设置都没有用
        popupWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);

        //从相册中选取图片
        imagestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从像册中获取
                gallery();
                //弹出框消失
                popupWindow.dismiss();
            }
        });

        //拍照
        tookphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拍照获取图片
                camera();
                //弹出框消失
                popupWindow.dismiss();
            }
        });
    }


    //从相册获取
    public void gallery() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        /** 安卓7.0以及以上给予读写权限 */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }


    //从相机获取
    public void camera() {
        // 激活相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        Uri uri =null;
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME);
            //在安卓7.0后不允许直接或许本地数据，需要用provide去调用
            if(Build.VERSION.SDK_INT < 24){
                // 从文件中创建uri
                uri = Uri.fromFile(tempFile);
            }else {
                uri = FileProvider.getUriForFile(this,"com.example.instant_deliver.fileprovider", tempFile);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        /** 安卓7.0以及以上给予读写权限 */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }


    /**
     * 剪切图片
     */
    private void crop(Uri uri) {
       /* Log.i("mm","剪切"+uri.toString());
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        *//** 安卓7.0以及以上给予读写权限 *//*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image*//*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        //路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        *//**返回值为空的时候会用到*//*
        imgUri = uri;
        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        //设置为返回数据
        intent.putExtra("return-data", false);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);*/
        /**返回值为空的时候会用到*/
        imgUri = uri;
        //裁剪后保存到文件中
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "instant_deliver"));
        UCrop uCrop = UCrop.of(uri, destinationUri);
        UCrop.Options options = new UCrop.Options();
        //设置裁剪图片可操作的手势
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        //设置隐藏底部容器，默认显示
        // options.setHideBottomControls(true);
        //设置toolbar颜色
        options.setToolbarColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        //设置状态栏颜色
        options.setStatusBarColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        //是否能调整裁剪框
//        options.setFreeStyleCropEnabled(true);
        uCrop.withOptions(options);
        uCrop.start(this,PHOTO_REQUEST_CUT);
    }


    // 判断sdcard是否被挂载
    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }


    //根据地址获取mitmap
    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            // 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            Log.e("[Android]", e.getMessage());
            Log.e("[Android]", "目录为：" + uri);
            e.printStackTrace();
            return null;
        }
    }

    //保存图片到本地
    public void saveImage(Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "instant_deliver");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = PHOTO_FILE_NAME;
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读取本地头像图片
    public void getImage() {
        //读取用户头像
        File appDir = new File(Environment.getExternalStorageDirectory(), "instant_deliver");
        //当文件夹存在
        if (appDir.exists()) {
            String fileName = PHOTO_FILE_NAME;
            File file = new File(appDir, fileName);
            if (file.exists()) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    //读取本地图片并显示
                    head.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    //创建progressbar
    private void createProgressbar() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("头像上传中");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }


    //对各控件的监听
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bind_user:
                //跳转到用户名更新页面
                Intent intent = new Intent(this, UpdateuserActivity.class);
                intent.putExtra("name",bindUsername.getText().toString());
                startActivityForResult(intent, 1);
                ;
                break;
            case R.id.bind_singnature:
                //跳转到签名更新页面
                Intent intent1 = new Intent(this, UpdateSingnatureActivity.class);
                intent1.putExtra("singnature",binbSingnature.getText().toString());
                startActivityForResult(intent1, 1);
                break;
            case R.id.bind_phone:
                Users user = BmobUser.getCurrentUser(Users.class);
                if (user.getMobilePhoneNumber() == null || user.getMobilePhoneNumber().equals("")) {
                    //跳转到号码绑定的页面
                    Intent intent2 = new Intent(this, bindphoneActivity.class);
                    startActivityForResult(intent2, 1);
                } else {
                    //跳转到号码解绑的页面
                    Intent intent3 = new Intent(this, UnbindActivity.class);
                    startActivityForResult(intent3, 1);
                }
                ;
                break;
            case R.id.bind_headImg:
                //异步处理
                handler.sendEmptyMessage(2);
                break;
        }
    }
}
