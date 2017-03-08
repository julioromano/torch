package net.kjulio.torch.torch;

import android.support.v4.app.Fragment;

public abstract class TorchFragment extends Fragment {

    public static final String TORCH_FRAGMENT_TAG = "torchFragmentTag";

    public abstract void on();

    public abstract void off();

}
