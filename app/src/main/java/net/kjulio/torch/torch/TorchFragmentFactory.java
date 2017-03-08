package net.kjulio.torch.torch;

import android.content.Context;

import net.kjulio.torch.torch.camera1.Camera1TorchFragment;
import net.kjulio.torch.torch.mock.MockTorchFragment;
import net.kjulio.torch.utils.Utils;

public class TorchFragmentFactory {

    private final Context context;

    public TorchFragmentFactory(Context context) {
        this.context = context;
    }

    public TorchFragment create() {
        return create(false);
    }

    public TorchFragment create(boolean mock) {
        if (mock || !Utils.deviceHasCamera(context)) {
            return new MockTorchFragment();
        } else {
            // TODO: Uncomment when support for different camera APIs is ready.
            // if (Build.VERSION.SDK_INT < 21) {
            //     return new Camera1TorchFragment();
            // } else if (Build.VERSION.SDK_INT < 23) {
            //     return new Camera2TorchFragment();
            // } else {
            //     return new FlashlightTorchFragment();
            // }
            return new Camera1TorchFragment();
        }
    }

}
