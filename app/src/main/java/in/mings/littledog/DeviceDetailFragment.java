package in.mings.littledog;



import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import butterknife.ButterKnife;
import butterknife.InjectView;
import in.mings.littledog.bt.BluetoothLeService;
import in.mings.littledog.bt.IBluetoothLe;
import in.mings.littledog.db.Device;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class DeviceDetailFragment extends Fragment {
    private Device mDevice;
    private BluetoothLeService mBluetoothService;

    @InjectView(R.id.tb_buzzer)
    ToggleButton mTbBuzzer;
    @InjectView(R.id.tb_color)
    ToggleButton mTbColor;
    @InjectView(R.id.tv_address)
    TextView mTvAddress;
    @InjectView(R.id.tv_name)
    TextView mTvName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_device_detail, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof BleActivity) {
            mBluetoothService = ((BleActivity) activity).getBluttoothLeService();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mBluetoothService =null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDevice = getActivity().getIntent().getParcelableExtra(IBluetoothLe.EXTRA_DEVICE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTvAddress.setText(mDevice.address);
        mTvName.setText(mDevice.name);
        if(mBluetoothService != null) {
            mBluetoothService.connect(mDevice.address);
        }
        mTbBuzzer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            }
        });
    }

    public static class DummyActivity extends BleActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_device_detail);
        }
    }

}
