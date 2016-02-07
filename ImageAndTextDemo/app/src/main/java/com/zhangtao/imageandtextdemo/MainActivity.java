package com.zhangtao.imageandtextdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {
    private EditText ImageAndTextArea;


    public static final int SELECT_PHOTO=1;
    public static final int CROP_PHOTO=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageAndTextArea= (EditText) findViewById(R.id.edit_ImageAndText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_actionbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.iamge:
                getImage();
                break;
        }
        return true;
    }

    public void getImage(){
        Intent SysImageintent=new Intent();
     //   SysImageintent.addCategory(Intent.CATEGORY_OPENABLE);
        SysImageintent.setAction(Intent.ACTION_GET_CONTENT);
        SysImageintent.setType("image/*");
        SysImageintent.putExtra("scale", true);
        SysImageintent.putExtra("crop",true);
        startActivityForResult(SysImageintent, CROP_PHOTO);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri imageUri;
        if (data!=null) {
            imageUri = data.getData();

            switch (requestCode) {
                case SELECT_PHOTO:

                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra("crop", true);
                    // aspectX aspectY 是宽高的比例
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);

                    // outputX,outputY 是剪裁图片的宽高
                    intent.putExtra("outputX", 150);
                    intent.putExtra("outputY", 150);
                    intent.putExtra("return-data", true);
                    startActivityForResult(intent, CROP_PHOTO);
                    break;
                case CROP_PHOTO:
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        insertPic(bitmap, imageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;

            }
        }else{
            return;

        }
    }

    public void insertPic(Bitmap bitmap,Uri imageUri){
// 根据Bitmap对象创建ImageSpan对象
        ImageSpan imageSpan = new ImageSpan(this, bitmap);
        // 创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
        String tempUrl = "<img src=\"" + imageUri + "\" />";
        SpannableString spannableString = new SpannableString(tempUrl);
        // 用ImageSpan对象替换你指定的字符串
        spannableString.setSpan(imageSpan, 0, tempUrl.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 将选择的图片追加到EditText中光标所在位置
        int index = ImageAndTextArea.getSelectionStart(); // 获取光标所在位置
        Editable edit_text = ImageAndTextArea.getEditableText();
        if (index < 0 || index >= edit_text.length()) {
            edit_text.append(spannableString);
        } else {
            edit_text.insert(index, spannableString);
        }
        System.out.println("插入的图片：" + spannableString.toString());
    }
}
