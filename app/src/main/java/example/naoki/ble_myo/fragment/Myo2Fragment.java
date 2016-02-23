package example.naoki.ble_myo.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.thalmic.myo.Myo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import example.naoki.ble_myo.R;
import example.naoki.ble_myo.callback.MyoGattCallback;
import example.naoki.ble_myo.constant.Constant;
import example.naoki.ble_myo.model.EmgCharacteristicData;
import example.naoki.ble_myo.model.EmgData;
import example.naoki.ble_myo.model.MyoModel;
import example.naoki.ble_myo.model.requestobject.LeftMyoArmband;
import example.naoki.ble_myo.model.requestobject.RightMyoArmband;


/**
 * Created by QuyPH on 2/17/2016.
 */
public class Myo2Fragment extends MyoBaseFragment {
   private static Myo2Fragment instance;
   private LeftMyoArmband mLeftMyoArmband;
   private List<EmgData> lEmgDataList;

   private Myo2FragmentEndEventCallback myo2FragmentEndEventCallback;

   public Myo2Fragment(Myo myo, BluetoothAdapter mBluetoothAdapter) {
      super(myo, mBluetoothAdapter);
      mHandler = new Handler();
      lEmgDataList = new ArrayList<>();
   }

   public static Myo2Fragment getInstance(Myo myo, BluetoothAdapter mBluetoothAdapter, Myo2FragmentEndEventCallback myo2FragmentEndEventCallback) {
      if (instance == null) {
         instance = new Myo2Fragment(myo, mBluetoothAdapter);
      }
      instance.myo2FragmentEndEventCallback = myo2FragmentEndEventCallback;
      return instance;
   }


   @Override
   protected int getLayoutId() {
      return R.layout.fragment_myo2;
   }

   @Override
   protected void initView(View view) {
      statusText = (TextView) view.findViewById(R.id.emgDataTextView);
      btnVibrate = (Button) view.findViewById(R.id.bVibrate);
      btnEMG = (Button) view.findViewById(R.id.bEMG);

   }

   private MyoGattCallback.IdentifyEmgDataCallback identifyEmgDataCallback = new MyoGattCallback.IdentifyEmgDataCallback() {
      @Override
      public void onReceiveText(int[] emgDatas, byte[] emgDataBytes) {
         EmgCharacteristicData emgCharacteristicData = new EmgCharacteristicData(emgDataBytes);
         EmgData emgData = emgCharacteristicData.getEmg8Data_abs();
         lEmgDataList.add(emgData);

         if (emgData.getSumEmgData() < Constant.DEFAULT_END_EVENT) {
            mLeftMyoArmband = new LeftMyoArmband(lEmgDataList);
            myo2FragmentEndEventCallback.onEndEvent(mLeftMyoArmband);
            lEmgDataList.clear();
         }
      }
   };

   @Override
   protected void bindingData(View view) {
      for (BluetoothDevice bluetoothDevice : pairedDevices) {
         if (myo.getMacAddress().equals(bluetoothDevice.getAddress())) {

            mMyoCallback = new MyoGattCallback(mHandler, statusText, identifyEmgDataCallback);
            mBluetoothGatt = bluetoothDevice.connectGatt(getActivity(), false, mMyoCallback);
            mMyoCallback.setBluetoothGatt(mBluetoothGatt);
         }
      }

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

   }

   public interface Myo2FragmentEndEventCallback {
      void onEndEvent(LeftMyoArmband mLeftMyoArmband);
   }
}
