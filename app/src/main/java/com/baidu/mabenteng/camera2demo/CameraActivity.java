package com.baidu.mabenteng.camera2demo;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.baidu.mabenteng.camera2demo.utils.App;

import java.io.File;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    public static String PATH = "";

    static {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            if (null != App.getContext().getExternalFilesDir(null)) {
                PATH = App.getContext().getExternalFilesDir(null).getPath();
            }
        } else {
            PATH = App.getContext().getFilesDir().getPath();
        }
    }

    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;

    public static Intent getJumpIntent(Context context) {
        return new Intent(context, CameraActivity.class);
    }

    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        setSystemNotificationBarInvisible();

        SurfaceView surfaceView = findViewById(R.id.camera_view);
        surfaceView.getHolder().addCallback(this);

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecord();
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;
        startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void setSystemNotificationBarInvisible() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(lp);
    }

    private void startPreview() {
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    private void startRecord() {
        if (null == mMediaRecorder) {
            mMediaRecorder = new MediaRecorder();
        }

        mMediaRecorder.reset();

        mCamera.stopPreview();
        mCamera.unlock();

        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);


        CamcorderProfile profile = CamcorderProfile
                .get(CamcorderProfile.QUALITY_LOW);

        mMediaRecorder.setVideoEncodingBitRate(profile.videoBitRate);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

        mMediaRecorder.setVideoSize(WIDTH, HEIGHT);
        mMediaRecorder.setVideoEncodingBitRate(1024 * 1024);
        mMediaRecorder.setOrientationHint(90);

        File outputFile = ensureFile();
        if (null == outputFile) {
            return;
        }

        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

        mMediaRecorder.setOutputFile(outputFile.getAbsolutePath());

        mMediaRecorder.setMaxDuration(0);

        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaRecorder.start();

    }

    private File ensureFile() {
        String path = PATH;
        path = path + "/.camera1.mp4";
        File file = new File(PATH);
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
}
