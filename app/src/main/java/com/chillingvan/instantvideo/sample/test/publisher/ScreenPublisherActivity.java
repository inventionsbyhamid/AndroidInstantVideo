package com.chillingvan.instantvideo.sample.test.publisher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.glview.texture.GLMultiTexProducerView;
import com.chillingvan.canvasgl.glview.texture.GLTexture;
import com.chillingvan.canvasgl.textureFilter.BasicTextureFilter;
import com.chillingvan.canvasgl.textureFilter.HueFilter;
import com.chillingvan.canvasgl.textureFilter.TextureFilter;
import com.chillingvan.instantvideo.sample.R;
import com.chillingvan.instantvideo.sample.test.publisher.data.TextData;
import com.chillingvan.instantvideo.sample.test.publisher.views.TextBitmapView;
import com.chillingvan.lib.encoder.video.H264Encoder;

import java.util.List;


public class ScreenPublisherActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ScreenPublisher";

    private static final String STATE_RESULT_CODE = "result_code";
    private static final String STATE_RESULT_DATA = "result_data";

    private static final int REQUEST_MEDIA_PROJECTION = 1;

    private int mScreenDensity;

    private int mResultCode;
    private Intent mResultData;

    private Surface mSurface;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionManager mMediaProjectionManager;
    private Button mButtonToggle;
    private DisplayTextureView displayTextureView;
    private TextureFilter textureFilterLT;
    private TextureFilter textureFilterRT;
    SurfaceTexture surfaceTexture;
    TextBitmapView textBitmapView;
    int width, height;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_publisher);

        mButtonToggle = (Button) findViewById(R.id.toggle);
        displayTextureView = findViewById(R.id.surface);
        mButtonToggle.setOnClickListener(this);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        textBitmapView = new TextBitmapView(new TextData(2, "Hello", "CENTER", 40, Color.WHITE, 0, 200, false, false));
        displayTextureView.setOnDrawListener(new H264Encoder.OnDrawListener() {
            @Override
            public void onGLDraw(ICanvasGL canvasGL, List<GLTexture> producedTextures, List<GLTexture> consumedTextures) {
                if (textureFilterLT == null) {
                    textureFilterLT = new BasicTextureFilter();
                }
                if (textureFilterRT == null) {
                    textureFilterRT = new HueFilter(180);
                }
                GLTexture texture = producedTextures.get(0);
                RawTexture outsideTexture = texture.getRawTexture();
                SurfaceTexture outsideSurfaceTexture = texture.getSurfaceTexture();

                int width = outsideTexture.getWidth();
                int height = outsideTexture.getHeight();
                canvasGL.drawBitmap(textBitmapView.getBitmap(), 0,0, textBitmapView.getBitmap().getWidth(), textBitmapView.getBitmap().getHeight());
                canvasGL.drawSurfaceTexture(outsideTexture, outsideSurfaceTexture, 0, 0, width / 2, height / 2, textureFilterLT);

            }
        });

        displayTextureView.setSurfaceTextureCreatedListener(new GLMultiTexProducerView.SurfaceTextureCreatedListener() {
            @Override
            public void onCreated(List<GLTexture> producedTextureList) {
                GLTexture texture = producedTextureList.get(0);
                surfaceTexture = texture.getSurfaceTexture();
                RawTexture outsideTexture = texture.getRawTexture();
                width = outsideTexture.getWidth();
                height = outsideTexture.getHeight();
                surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                    @Override
                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                        displayTextureView.requestRender();
                    }
                });
                mSurface = new Surface(surfaceTexture);
                if(mVirtualDisplay != null) {
                    mVirtualDisplay.setSurface(mSurface);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggle:
                if (mVirtualDisplay == null) {
                    startScreenCapture();
                } else {
                    stopScreenCapture();
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tearDownMediaProjection();
    }

    private void setUpMediaProjection() {
        mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, mResultData);
    }

    private void tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, R.string.user_cancelled, Toast.LENGTH_SHORT).show();
                return;
            }
            Log.i(TAG, "Starting screen capture");
            mResultCode = resultCode;
            mResultData = data;
            setUpMediaProjection();
            setUpVirtualDisplay();
        }
    }

    private void startScreenCapture() {
        if (mSurface == null) {
            return;
        }
        if (mMediaProjection != null) {
            setUpVirtualDisplay();
        } else if (mResultCode != 0 && mResultData != null) {
            setUpMediaProjection();
            setUpVirtualDisplay();
        } else {
            Log.i(TAG, "Requesting confirmation");
            // This initiates a prompt dialog for the user to confirm screen projection.
            startActivityForResult(
                    mMediaProjectionManager.createScreenCaptureIntent(),
                    REQUEST_MEDIA_PROJECTION);
        }
    }

    private void setUpVirtualDisplay() {
        Log.d(TAG, "Setting up a VirtualDisplay: " +
                displayTextureView.getWidth() + "x" + displayTextureView.getHeight() +
                " (" + mScreenDensity + ")");
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture",
                width, height, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mSurface, null, null);
        mButtonToggle.setText(R.string.stop);
    }

    private void stopScreenCapture() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        mVirtualDisplay = null;
        mButtonToggle.setText(R.string.start);
    }
}
