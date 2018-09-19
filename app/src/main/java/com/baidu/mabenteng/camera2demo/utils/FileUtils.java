package com.baidu.mabenteng.camera2demo.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author mabenteng
 * @version v10.10.0
 * Created by AndroidStudio
 * @since 2018/9/17
 */

public class FileUtils {
    private static String sRoot = "";

    static {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            File file = App.getContext().getExternalFilesDir(null);
            if (null != file) {
                sRoot = file.getPath();
            }
        } else {
            sRoot = App.getContext().getFilesDir().getPath();
        }
    }

    public static final String sCameraPhotoPath = "CameraPhoto";
    public static final String sCameraPhotoSuffix = ".jpg";

    public static final String sCameraVideoPath = "CameraVideo";
    public static final String sCameraVideoSuffix = ".mp4";

    public static final String sCamera2PhotoPath = "Camera2Photo";
    public static final String sCamera2PhotoSuffix = ".jpg";

    public static final String sCamera2VideoPath = "Camera2Video";
    public static final String sCamera2VideoSuffix = ".mp4";

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File ensureVideoFile() {
        String path = sRoot;
        String time = String.valueOf(System.currentTimeMillis());
        path = path + "/" + time + "camera1.mp4";
        File file = new File(path);
        if (!file.exists()) {
            file.getParentFile().mkdir();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return file;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File getFile(String path, String name, String suffix, boolean addTime) {
        String time = "";
        if (addTime) {
            time = String.valueOf(System.currentTimeMillis());
        }
        if (TextUtils.isEmpty(path)) {
            path = "default";
        }
        path = sRoot.concat("/").concat(path).concat("/").concat(time).concat(name).concat(suffix);
        File file = new File(path);
        if (!file.exists()) {
            file.getParentFile().mkdir();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return file;
    }

    public static boolean isEmpty(byte[] data) {
        return data == null || data.length == 0;
    }

    public static boolean saveFile(File outputFile, byte[] data) {
        if (null != outputFile && null != data && data.length != 0) {
            try {
                FileOutputStream outputStream = new FileOutputStream(outputFile);
                outputStream.write(data);
                outputStream.flush();
                outputStream.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

}
