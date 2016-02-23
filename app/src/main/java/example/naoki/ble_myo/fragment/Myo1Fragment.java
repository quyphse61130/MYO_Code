package example.naoki.ble_myo.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
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
import example.naoki.ble_myo.model.requestobject.RightMyoArmband;

/**
 * Created by QuyPH on 2/17/2016.
 */
public class Myo1Fragment extends MyoBaseFragment {
   private static Myo1Fragment instance;
   private RightMyoArmband mRightMyoArmband;
   private List<EmgData> rEmgDataList;

   private Gson gson;

   private Myo1FragmentEndEventCallback myo1FragmentEndEventCallback;

   //private EmgData mE
   public Myo1Fragment(Myo myo, BluetoothAdapter mBluetoothAdapter) {
      super(myo, mBluetoothAdapter);
      mHandler = new Handler();
      rEmgDataList = new ArrayList<>();
   }

   public static Myo1Fragment getInstance(Myo myo, BluetoothAdapter mBluetoothAdapter, Myo1FragmentEndEventCallback myo1FragmentEndEventCallback) {
      if (instance == null) {
         instance = new Myo1Fragment(myo, mBluetoothAdapter);
      }

      instance.gson = new Gson();
      instance.myo1FragmentEndEventCallback = myo1FragmentEndEventCallback;
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

   private MyoGattCallback.IdentifyEmgDataCallback identifyEmgDataCallback = new MyoGattCallback.IdentifyEmgDataCallback() {
      @Override
      public void onReceiveText(int[] emgDatas, byte[] emgDataBytes) {
         EmgCharacteristicData emgCharacteristicData = new EmgCharacteristicData(emgDataBytes);
         EmgData emgData = emgCharacteristicData.getEmg8Data_abs();
         rEmgDataList.add(emgData);

         if (emgData.getSumEmgData() < Constant.DEFAULT_END_EVENT) {
            mRightMyoArmband = new RightMyoArmband(rEmgDataList);
            myo1FragmentEndEventCallback.onEndEvent(mRightMyoArmband);
            rEmgDataList.clear();
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

   public interface Myo1FragmentEndEventCallback {
        void onEndEvent(RightMyoArmband mRightMyoArmband);
   }
}
