package in.mings.littledog;

import android.app.Activity;
import android.app.Fragment;
import android.app.Service;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import in.mings.littledog.bt.BluetoothLeService;
import in.mings.mingle.utils.Logger;


public class MainActivity extends Activity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private BluetoothLeService bluetoothLeService;
    private boolean mBound;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.d(TAG, "Service connected");
            mBound = true;
            BluetoothLeService.BluetoothLeBinder binder = (BluetoothLeService.BluetoothLeBinder) service;
            bluetoothLeService = binder.getService();
            bluetoothLeService.startLeScan();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.d(TAG, "Service onServiceDisconnected");
            bluetoothLeService = null;
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    private void bindLeService() {
        Intent intent = new Intent(this, BluetoothLeService.class);
        bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE);
    }

    protected void onStart() {
        super.onStart();
        bindLeService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }


}
