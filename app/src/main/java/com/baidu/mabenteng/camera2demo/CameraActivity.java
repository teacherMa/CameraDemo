package com.baidu.mabenteng.camera2demo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mabenteng.camera2demo.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("deprecation")
public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private static final String TAG = "mabenteng_" + CameraActivity.class.getSimpleName();

    private boolean mIsRecord = false;

    public static Intent getJumpIntent(Context context) {
        return new Intent(context, CameraActivity.class);
    }

    private SurfaceView mSurfaceView;
    private Camera mCamera;
    private int mCameraID;
    private MediaRecorder mMediaRecorder;
    private int mOrientation = 0;

    // 相机类型，默认为后置相机
    private int mCameraKind = Camera.CameraInfo.CAMERA_FACING_BACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        if (!checkCameraFeature(CameraActivity.this)) {
            return;
        }

        setSystemNotificationBarInvisible();

        findCamera();

        mCamera = openCamera();

        final FrameLayout frameLayout = findViewById(R.id.camera_view);
        frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (frameLayout.findViewWithTag("tag") == null) {
                    Camera.Parameters parameters = mCamera.getParameters();

                    Camera.Size previewSize = parameters.getSupportedPreviewSizes().get(0);
                    float rate = (previewSize.height * 1.0f) / (previewSize.width * 1.0f);
                    int width = frameLayout.getWidth();
                    int height = (int) (width / rate);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
                    mSurfaceView.setLayoutParams(layoutParams);
                    mSurfaceView.setTag("tag");
                    frameLayout.addView(mSurfaceView);
                }
            }
        });

        mSurfaceView = new SurfaceView(CameraActivity.this);

        mSurfaceView.getHolder().addCallback(this);

        final TextView start = findViewById(R.id.start_record);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsRecord) {
                    stopRecord();
                    start.setText("START");
                } else {
                    startRecord();
                    start.setText("STOP");
                }
            }
        });

        findViewById(R.id.capture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capture();
            }
        });

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startPreview();
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCamera.stopPreview();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPreview();
    }

    private boolean checkCameraFeature(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private void setSystemNotificationBarInvisible() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(lp);
    }

    private void startPreview() {
        if (null == mCamera) {
            return;
        }

        deployCamera();

        try {
            mCamera.setPreviewDisplay(mSurfaceView.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCamera.startPreview();
    }

    private void findCamera() {
        int count = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < count; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == mCameraKind) {
                mCameraID = i;
                break;
            }
        }
    }

    private Camera openCamera() {
        try {
            return Camera.open(mCameraID);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 部署相机
     */
    private void deployCamera() {
        adjustPreviewOrientation();
        setParameters();
    }

    /**
     * 调整预览角度
     */
    private void adjustPreviewOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraID, info);
        int rotation = CameraActivity.this.getWindowManager().getDefaultDisplay().getRotation();
        int degrees;
        int result;
        // actually,our activity should forbid screen rotate, degrees always equals 0
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                degrees = 0;
        }
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        mOrientation = result;
        mCamera.setDisplayOrientation(result);
    }

    /**
     * 设置相关参数
     */
    private void setParameters() {
        Camera.Parameters parameters = mCamera.getParameters();

        // 预览帧率
        List<int[]> supportedPreviewFpsRange = parameters.getSupportedPreviewFpsRange();
        int[] fpsRange = supportedPreviewFpsRange.get(supportedPreviewFpsRange.size() - 1);
        parameters.setPreviewFpsRange(fpsRange[0], fpsRange[1]);

        // 缩略图质量
        parameters.setJpegThumbnailQuality(100);

        // 对焦模式
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

        // 保存的照片尺寸
        Camera.Size pictureSize = parameters.getSupportedPictureSizes().get(0);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);

        // 拍出来的照片的角度
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraID, info);
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            parameters.setRotation(info.orientation);
        } else {
            parameters.setRotation(mOrientation);
        }

        int format = ImageFormat.JPEG;
        int bitPerPixel = ImageFormat.getBitsPerPixel(format);
        List<Integer> formats = parameters.getSupportedPictureFormats();
        for (int f : formats) {
            if(ImageFormat.getBitsPerPixel(f) > bitPerPixel) {
                format = f;
                bitPerPixel = ImageFormat.getBitsPerPixel(f);
            }
        }
        parameters.setPictureFormat(format);

        mCamera.setParameters(parameters);
    }

    private void startRecord() {
        if (null == mMediaRecorder) {
            mMediaRecorder = new MediaRecorder();
        }

        mMediaRecorder.reset();

        mCamera.unlock();

        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);

        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        mMediaRecorder.setOrientationHint(90);

        File outputFile = FileUtils.ensureVideoFile();
        if (null == outputFile) {
            return;
        }

        mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());

        mMediaRecorder.setOutputFile(outputFile.getAbsolutePath());

        mMediaRecorder.setMaxDuration(0);

        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            stopRecord();
            mIsRecord = false;
            return;
        }
        mIsRecord = true;
        Toast.makeText(CameraActivity.this, "开始录制", Toast.LENGTH_LONG).show();
    }

    private void capture() {
        mCamera.takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {
                Log.e(TAG, "onShutter: I play a shutter sound");
            }
        }, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                if (FileUtils.isEmpty(data)) {
                    return;
                }
                File outputFile = FileUtils.getFile(FileUtils.sCameraPhotoPath, "raw_photo", FileUtils.sCameraPhotoSuffix, true);
                FileUtils.saveFile(outputFile, data);
            }
        }, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                if (FileUtils.isEmpty(data)) {
                    return;
                }
                File outputFile = FileUtils.getFile(FileUtils.sCameraPhotoPath, "post_photo", FileUtils.sCameraPhotoSuffix, true);
                FileUtils.saveFile(outputFile, data);
            }
        }, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                if (FileUtils.isEmpty(data)) {
                    return;
                }
                File outputFile = FileUtils.getFile(FileUtils.sCameraPhotoPath, "photo", FileUtils.sCameraPhotoSuffix, true);
                if (FileUtils.saveFile(outputFile, data)) {
                    mCamera.startPreview();
                } else {
                    mCamera.release();
                }
            }
        });
    }

    private void stopRecord() {
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mIsRecord = false;
    }
}
