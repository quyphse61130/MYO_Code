package example.naoki.ble_myo.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Set;

import example.naoki.ble_myo.R;
import example.naoki.ble_myo.callback.MyoGattCallback;
import example.naoki.ble_myo.constant.Constant;
import example.naoki.ble_myo.model.MyoModel;

/**
 * Created by QuyPH on 2/17/2016.
 */
public class Myo1Fragment extends MyoBaseFragment {
    private static Myo1Fragment instance;

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Set<BluetoothDevice> pairedDevices
                    = mBluetoothAdapter.getBondedDevices();
            for (BluetoothDevice bluetoothDevice : pairedDevices) {
                if (myoName.equals(bluetoothDevice.getName())) {
                    mBluetoothAdapter.stopLeScan(this);
                    // Trying to connect GATT

                    mMyoCallback = new MyoGattCallback(mHandler, statusText);
                    mBluetoothGatt = device.connectGatt(getActivity(), false, mMyoCallback);
                    mMyoCallback.setBluetoothGatt(mBluetoothGatt);
                }
            }
        }
    };

    public Myo1Fragment(String myoName, BluetoothAdapter mBluetoothAdapter) {
        super(myoName, mBluetoothAdapter);
        mHandler = new Handler();
    }

    public static Myo1Fragment getInstance(String myoName, BluetoothAdapter mBluetoothAdapter) {
        if (instance == null) {
            instance = new Myo1Fragment(myoName, mBluetoothAdapter);
        }
        return instance;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_myo1;
    }

    @Override
    protected void initView(View view) {
        statusText = (TextView) view.findViewById(R.id.emgDataTextView);
        btnVibrate = (Button) view.findViewById(R.id.bVibrate);
    }

    @Override
    protected void bindingData(View view) {
        btnVibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickVibration(v);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothAdapter.stopLeScan(leScanCallback);
            }
        }, Constant.SCAN_PERIOD);
        mBluetoothAdapter.startLeScan(leScanCallback);
    }
}
