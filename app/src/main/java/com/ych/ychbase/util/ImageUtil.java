package com.ych.ychbase.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.blankj.utilcode.util.ImageUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ImageUtil {
    public static String bitmapCompress(String path, int quality) {

        Bitmap photoBitmap = ImageUtils.getBitmap(new File(path));

        //创建路径
        String compressPath = Environment.getExternalStorageDirectory()
                .getPath() + "/CompressPic";
        //获取外部储存目录
        File file = new File(path);
        //创建新目录, 创建此抽象路径名指定的目录，包括创建必需但不存在的父目录。
        file.mkdirs();
        //以当前时间重新命名文件
        long i = System.currentTimeMillis();
        //生成新的文件
        file = new File(file.toString() + "/" + i + ".jpeg");
        Log.e("fileNew", file.getPath());
        //创建输出流
        OutputStream out = null;
        try {
            out = new FileOutputStream(file.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //压缩文件，返回结果，参数分别是压缩的格式，压缩质量的百分比，输出流
        photoBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);

        return file.getPath();

    }
}
