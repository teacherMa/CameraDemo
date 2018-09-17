package com.baidu.mabenteng.camera2demo.customview;

import android.view.SurfaceView;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;

/**
 * @author mabenteng
 * @version v10.10.0
 * Created by AndroidStudio
 * @since 2018/9/17
 */

public class WaterMarkPreview extends SurfaceView {
    public WaterMarkPreview(Context context) {
        this(context, null);
    }

    public WaterMarkPreview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaterMarkPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        setZOrderOnTop(true);
    }
}
