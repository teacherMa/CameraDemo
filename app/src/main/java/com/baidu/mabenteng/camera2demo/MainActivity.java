package com.baidu.mabenteng.camera2demo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.goto_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    startActivity(CameraActivity.getJumpIntent(MainActivity.this));
                } else {
                    requestPermission(1);
                }
            }
        });
        findViewById(R.id.goto_camera2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    startActivity(Camera2Activity.getJumpIntent(MainActivity.this));
                } else {
                    requestPermission(2);
                }
            }
        });
    }

    private boolean checkPermission() {
        return MainActivity.this.checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
                && MainActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                && MainActivity.this.checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(int requestCode) {
        MainActivity.this.requestPermissions(
                new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length <= 0) {
            return;
        }
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (1 == requestCode) {
            startActivity(CameraActivity.getJumpIntent(MainActivity.this));
        } else if (2 == requestCode) {
            startActivity(Camera2Activity.getJumpIntent(MainActivity.this));
        }
    }
}
