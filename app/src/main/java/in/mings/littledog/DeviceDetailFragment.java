package in.mings.littledog;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import in.mings.littledog.bt.BluetoothLeService;
import in.mings.littledog.bt.IBluetoothLe;
import in.mings.littledog.bt.Device;


/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceDetailFragment extends Fragment {
    public static final String SERIAL_PORT_UUID = "0000dfb1-0000-1000-8000-00805f9b34fb";
    public static final String COMMAND_UUID = "0000dfb2-0000-1000-8000-00805f9b34fb";
    public static final String MODEL_NUMBER_STRING_UUID = "00002a24-0000-1000-8000-00805f9b34fb";

    private Device mDevice;
    private BluetoothLeService mBluetoothService;

    @InjectView(R.id.tb_buzzer)
    Switch mTbBuzzer;
    @InjectView(R.id.tb_color)
    Switch mTbColor;
    @InjectView(R.id.tv_address)
    TextView mTvAddress;
    @InjectView(R.id.tv_name)
    TextView mTvName;
    @InjectView(R.id.tb_connection)
    Switch mTbConnection;

    void connect(boolean connect) {
        BleActivity activity = (BleActivity) getActivity();
        if (activity != null) {
            BluetoothLeService leService = activity.getBluetoothLeService();
            if (leService != null) {
                if (connect) {
                    leService.connect(mDevice);
                } else {
                    leService.colse(mDevice);
                }
            }
        }
    }


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
        if (activity instanceof BleActivity) {
            mBluetoothService = ((BleActivity) activity).getBluetoothLeService();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mBluetoothService = null;
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
        if (mBluetoothService != null) {
            mBluetoothService.connect(mDevice);
        }
        mTbBuzzer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BleActivity activity = (BleActivity) getActivity();
                if (activity != null) {
                    BluetoothLeService leService = activity.getBluetoothLeService();
                    if (leService != null) {
                        if (!isChecked) {
                            leService.stopRingtone();
                        }

                    }
                }
            }
        });

        mTbConnection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                connect(isChecked);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean checked = mBluetoothService != null && mDevice != null && mBluetoothService.getConnectedState(mDevice) == IBluetoothLe.STATE_CONNECTED;
        mTbConnection.setChecked(checked);
    }


    public static class DummyActivity extends BleActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_device_detail);
        }

        @Override
        public void onBleServiceConnected(BleService service) {

        }
    }

}
