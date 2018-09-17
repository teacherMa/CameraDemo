package com.baidu.mabenteng.camera2demo;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.baidu.mabenteng.camera2demo.customview.Camera2Preview;
import com.baidu.mabenteng.camera2demo.customview.WaterMarkPreview;
import com.baidu.mabenteng.camera2demo.utils.App;

public class Camera2Activity extends AppCompatActivity{

    private Camera2Preview mPreview;
    private WaterMarkPreview mWaterMarkPreview;

    public static Intent getJumpIntent(Context context) {
        return new Intent(context, Camera2Activity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        initCameraView();
    }

    private void initCameraView() {
        // Create our Preview view and set it as the content of our activity.
        mPreview = new Camera2Preview(this);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        mPreview.setAspectRatio(preview.getWidth(), preview.getHeight());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPreview != null)
            mPreview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) mPreview.onPause();
    }

    public void switchCamera(View view) {
        mPreview.switchCamera();
    }

    public void takePicture(View view) {
        mPreview.takePicture();
    }

    public void toggleVideo(View view) {
        if (mPreview.toggleVideo()) {
            ((Button) view).setText("停止录制视频");
        } else {
            ((Button) view).setText("开始录制视频");
        }
    }

    public void toggleWaterMark(View view) {
        if (mWaterMarkPreview == null) {
            mWaterMarkPreview = (WaterMarkPreview) findViewById(R.id.camera_watermark_preview);
            mPreview.setWaterMarkPreview(mWaterMarkPreview);
        }
        if (mPreview.toggleWaterMark()) {
            mWaterMarkPreview.setVisibility(View.VISIBLE);
        } else {
            mWaterMarkPreview.setVisibility(View.GONE);
        }
    }

}
