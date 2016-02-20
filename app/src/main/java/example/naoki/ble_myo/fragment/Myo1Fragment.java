package example.naoki.ble_myo.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.thalmic.myo.Myo;

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

    public Myo1Fragment(Myo myo, BluetoothAdapter mBluetoothAdapter) {
        super(myo, mBluetoothAdapter);
        mHandler = new Handler();
    }

    public static Myo1Fragment getInstance(Myo myo, BluetoothAdapter mBluetoothAdapter) {
        if (instance == null) {
            instance = new Myo1Fragment(myo, mBluetoothAdapter);
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
        btnEMG = (Button) view.findViewById(R.id.bEMG);
    }

    @Override
    protected void bindingData(View view) {
        btnVibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickVibration(v);
            }
        });

        btnEMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickEMG(v);
            }
        });

        for (BluetoothDevice bluetoothDevice : pairedDevices) {
            if (myo.getMacAddress().equals(bluetoothDevice.getAddress())) {

                mMyoCallback = new MyoGattCallback(mHandler, statusText);
                mBluetoothGatt = bluetoothDevice.connectGatt(getActivity(), false, mMyoCallback);
                mMyoCallback.setBluetoothGatt(mBluetoothGatt);
            }
        }
    }
}
