package example.naoki.ble_myo.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.thalmic.myo.Myo;

import java.util.ArrayList;
import java.util.List;

import example.naoki.ble_myo.R;
import example.naoki.ble_myo.callback.MyoGattCallback;
import example.naoki.ble_myo.constant.Constant;
import example.naoki.ble_myo.model.myo.EmgCharacteristicData;
import example.naoki.ble_myo.model.myo.EmgData;

/**
 * Created by QuyPH on 2/17/2016.
 */
public class TrainingMyo2Fragment extends MyoBaseFragment {
   private static final String LOG_TAG = TrainingMyo2Fragment.class.getSimpleName();
   private static TrainingMyo2Fragment instance;
   private List<EmgData> emgDataList;

   //private EmgData mE
   public TrainingMyo2Fragment(Myo myo, BluetoothAdapter mBluetoothAdapter) {
      super(myo, mBluetoothAdapter);
      mHandler = new Handler();
      emgDataList = new ArrayList<>();
   }

   public static TrainingMyo2Fragment getInstance(Myo myo,
                                                  BluetoothAdapter mBluetoothAdapter) {
      if (instance == null) {
         instance = new TrainingMyo2Fragment(myo, mBluetoothAdapter);
      }

      Log.d(LOG_TAG, "getInstance: " + myo.getName() + " | " + myo.getMacAddress() + " | " + myo.getArm());
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
      btnStopEmg = (Button) view.findViewById(R.id.bStopEmg);
   }

   private MyoGattCallback.IdentifyEmgDataCallback identifyEmgDataCallback = new MyoGattCallback.IdentifyEmgDataCallback() {
      @Override
      public void onReceiveText(final byte[] emgDataBytes) {
         if (numberOfHz == Constant.DEFAULT_HZ_BREAK_EVENT) {
            EmgCharacteristicData emgCharacteristicData = new EmgCharacteristicData(emgDataBytes);
            EmgData emgData = emgCharacteristicData.getEmg8Data_abs();
            emgDataList.add(emgData);
            Log.w(LOG_TAG, "Size hand 2: " + emgDataList.size() + " | " + emgData.toString());
            numberOfHz = Constant.DEFAULT_HZ_START_EVENT;
         } else {
            numberOfHz++;
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
            onClickEMG();
         }
      });

      btnStopEmg.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            onClickNoEMG();
         }
      });
   }

   public List<EmgData> getEmgDataList() {
      return emgDataList;
   }

   public void setEmgDataList(List<EmgData> emgDataList) {
      this.emgDataList = emgDataList;
   }
}
