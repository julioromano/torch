package net.kjulio.torch.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import net.kjulio.torch.R;
import net.kjulio.torch.app.App;
import net.kjulio.torch.databinding.ActivityMainBinding;
import net.kjulio.torch.torch.TorchFragment;
import net.kjulio.torch.torch.TorchFragmentFactory;
import net.kjulio.torch.ui.settings.SettingsActivity;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    public final ObservableBoolean torchButton = new ObservableBoolean();

    private static final int PERMISSIONS_REQUEST_CAMERA = 243;

    private TorchFragment torchFragment;
    private SharedPreferences sharedPreferences;
    private boolean lastInvocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        FragmentManager fm = getSupportFragmentManager();
        if (savedInstanceState == null) {
            torchFragment = new TorchFragmentFactory(this).create();
            fm.beginTransaction()
                    .replace(R.id.frameLayout, torchFragment, TorchFragment.TORCH_FRAGMENT_TAG)
                    .commit();
        } else {
            torchFragment = (TorchFragment) fm.findFragmentByTag(TorchFragment.TORCH_FRAGMENT_TAG);
        }

        // Sets button state according to preferences (or true if no preference found)
        String s = sharedPreferences.getString("pref_startup_state", "last");
        switch (s) {
            case "last":
                torchButton.set(sharedPreferences.getBoolean("mTorchButton", true));
                break;
            case "off":
                torchButton.set(false);
                break;
            case "on":
                torchButton.set(true);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Enable notification
        ((App) getApplication()).setNotify(true);

        // Cancels existing notification, if any
        ((App) getApplication()).clearNotif();

        // Set correct display stays on state
        if (sharedPreferences.getBoolean("pref_nosleep", true)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        if (torchButton.get()) {
            torch(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Save preferences
        sharedPreferences.edit()
                .putBoolean("mTorchButton", torchButton.get())
                .apply();

        // Raise notification if pref_background is true and torch is actually on
        if (sharedPreferences.getBoolean("pref_background", true) &&
                sharedPreferences.getBoolean("mTorchButton", true)) {
            ((App) getApplication()).sendNotif();
        } else {
            torch(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancels existing notification, if any
        ((App) getApplication()).clearNotif();
        torch(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                // Disable notification as we're staying in the app
                ((App) getApplication()).setNotify(false);
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClickTorchButton() {
        torch(torchButton.get());
    }

    private void torch(boolean enable) {
        lastInvocation = enable;
        if (hasCameraPermission()) {
            if (enable) {
                torchFragment.on();
            } else {
                torchFragment.off();
            }
        } else {
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                PERMISSIONS_REQUEST_CAMERA);
    }

    private boolean hasCameraPermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED);
    }

    private void onCameraPermissionGranted() {
        torch(lastInvocation);
    }

    private void onCameraPermissionDenied() {
        requestCameraPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Timber.d("Permission granted");
                    onCameraPermissionGranted();
                } else {
                    Timber.e("Permission denied");
                    onCameraPermissionDenied();
                }
            }
        }
    }
}
