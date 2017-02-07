package com.example.ygl.networktest;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by YGL on 2017/2/1.
 */

public class SavePicture {
    public static void savePicture(String picturepath,Bitmap bitmap){
        File picpath=new File(picturepath);
        if (!picpath.exists()) {//不存在此路径则建立此路径
            picpath.mkdirs();
        }
        Date now = new Date(); // 将日期加入文件名称
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy_MM_dd_HH_mm_ss_");
        int a;//加入随机尾
        Random ra = new Random();
        a = ra.nextInt(10000);
        String filename = picturepath+dateFormat.format(now) + a + ".jpg";

        File picture=new File(filename);
        FileOutputStream out;
        try{
            out=new FileOutputStream(picture);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
            //out.flush();不等待缓冲区满就输出
            out.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
