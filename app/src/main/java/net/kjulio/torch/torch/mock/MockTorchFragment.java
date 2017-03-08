package net.kjulio.torch.torch.mock;

import net.kjulio.torch.R;
import net.kjulio.torch.torch.TorchFragment;
import net.kjulio.torch.utils.Utils;

import timber.log.Timber;

public class MockTorchFragment extends TorchFragment {

    @Override
    public void on() {
        showWarningMessage();
        Timber.d("Torch on");
    }

    @Override
    public void off() {
        showWarningMessage();
        Timber.d("Torch off");
    }

    private void showWarningMessage() {
        Utils.snackbar(getActivity().findViewById(R.id.coordinator), getString(R.string.warning_no_flash));
    }

}
