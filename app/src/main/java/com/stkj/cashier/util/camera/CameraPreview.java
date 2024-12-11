package com.stkj.cashier.util.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.stkj.cashier.util.util.LogUtils;

/**
 * Created by linyue on 16/1/2.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private Camera camera = null;

    private CameraPreviewListener listener;

    private float scaleW = 1;

    private float scaleH = 1;

    private float aspectRatio = 0f;

    public CameraPreview(Context context) {
        super(context);
        init();
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    void setCamera(Camera camera) {
        this.camera = camera;
        restartPreview(getHolder());
    }

    private void restartPreview(SurfaceHolder holder) {
        if (camera != null) {
            if (holder.getSurface() == null) {
                return;
            }

            try {
                camera.stopPreview();
            } catch (Exception e) {
            }

            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
//                camera.startFaceDetection();
                if (listener != null) {
                    listener.onStartPreview();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setScale(float scaleW, float scaleH) {
        this.scaleW = scaleW;
        this.scaleH = scaleH;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        restartPreview(holder);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        restartPreview(holder);
    }

    public void setListener(CameraPreviewListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        //setMeasuredDimension((int) (width * scaleW), (int) (height * scaleH));
        if (aspectRatio == 0f) {
            setMeasuredDimension(width, height);
        } else {
            // Performs center-crop transformation of the camera frames
            int newWidth;
            int newHeight;
            float actualRatio = (width > height) ? aspectRatio : 1f / aspectRatio;
            if (width < height * actualRatio) {
                newHeight = height;
                newWidth = Math.round(height * actualRatio);
            } else {
                newWidth = width;
                newHeight = Math.round(width / actualRatio);
            }

            Log.d("TAG", "Measured dimensions set: " + newWidth + " x " + newHeight);
            setMeasuredDimension(newWidth, newHeight);
            if (autoFitSurfaceListener != null) {
                autoFitSurfaceListener.onMeasuredDimension(newWidth, newHeight);
            }
        }

    }
    public void setAspectRatio(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Size cannot be negative");
        }
        LogUtils.e( "setAspectRatio width: " + width + " height " + height);
        aspectRatio = (float) width / height;
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }
    public interface CameraPreviewListener {
        public void onStartPreview();
    }

    private OnAutoFitSurfaceListener autoFitSurfaceListener;

    public void setAutoFitSurfaceListener(OnAutoFitSurfaceListener autoFitSurfaceListener) {
        this.autoFitSurfaceListener = autoFitSurfaceListener;
    }

    public interface OnAutoFitSurfaceListener {
        void onMeasuredDimension(int width, int height);
    }
}