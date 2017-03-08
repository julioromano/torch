package net.kjulio.torch.torch.camera2;

import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import net.kjulio.torch.databinding.FragmentTorchCamera2Binding;
import net.kjulio.torch.torch.TorchFragment;

import java.io.IOException;

import timber.log.Timber;

public class Camera2TorchFragment extends TorchFragment {

    private final MySurfaceHolderCallback mySurfaceHolderCallback = new MySurfaceHolderCallback();
    private final Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {}
    };

    private Camera camera;
    private SurfaceHolder surfaceHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentTorchCamera2Binding binding = FragmentTorchCamera2Binding.inflate(inflater, container, false);

        // Init Camera resources
        surfaceHolder = binding.surfaceView.getHolder();
        surfaceHolder.addCallback(mySurfaceHolderCallback);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        return binding.getRoot();
    }

    @Override
    public void on() {
        switchOn();
    }

    @Override
    public void off() {
        switchOff();
    }

    private void switchOn() {
        try {
            camera = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            Timber.e(e, "Can't open camera, maybe it's in use or not available.");
        }

        // If we don't recall this here then camera won't work after
        // resuming from sleep
        mySurfaceHolderCallback.surfaceCreated(surfaceHolder);

        Camera.Parameters parameters = camera.getParameters();
        // Hacked torch switch on logic for devices which do not support FLASH_MODE_TOCH (e.g. Galaxy Ace)
        if (!parameters.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_TORCH)) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            camera.setParameters(parameters);
            camera.autoFocus(autoFocusCallback);
            camera.startPreview();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameters);
        } else {
            // Standard logic for device with support for FLASH_MODE_TORCH
            // Some phones don't preserve parameters state after stopPreview(),
            // we gotta set them each time here!
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameters);
            camera.startPreview();
        }
    }

    private void switchOff() {
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            // Some phones don't turn off flash upon stopPreview(), we gotta do
            // it manually here!
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameters);
            camera.stopPreview();
            // Release camera resource
            camera.release();
            camera = null;
        }
    }

    class MySurfaceHolderCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // HACK: since we want to open the camera handle only when actually turning on
            // the light (to avoid occupying the camera handle when the light is off) we
            // need to surround this try/catch block with an "if guard" to avoid calling
            // setPreviewDisplay() with a still empty camera handle (which would crash)
            if (camera != null) {
                try {
                    camera.setPreviewDisplay(surfaceHolder);
                } catch (IOException e) {
                    Timber.e(e, "Error setting camera preview.");
                }
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {}
    }
}
