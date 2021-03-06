package example.naoki.ble_myo.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;

import java.util.ArrayList;
import java.util.List;

import example.naoki.ble_myo.R;
import example.naoki.ble_myo.constant.Constant;
import example.naoki.ble_myo.fragment.Myo1Fragment;
import example.naoki.ble_myo.fragment.Myo2Fragment;
import example.naoki.ble_myo.listener.RequestApiListener;
import example.naoki.ble_myo.model.myo.EmgData;
import example.naoki.ble_myo.model.request.MyoSignal;
import example.naoki.ble_myo.model.response.Response;
import example.naoki.ble_myo.model.request.LeftMyoArmband;
import example.naoki.ble_myo.model.request.RightMyoArmband;
import example.naoki.ble_myo.utils.RequestUtils;

/**
 * Created by TanPhat
 * on 18/02/2016.
 */
public class MultipleMyosActivity extends FragmentActivity {
   private static final String TAG = "MultipleMyosActivity";
   private Myo1Fragment myo1Fragment;
   private Myo2Fragment myo2Fragment;

   private BluetoothAdapter mBluetoothAdapter;
   private Gson gson;

   public static int restConditionLeftHand = 0;
   public static int restConditionRightHand = 0;

   private RightMyoArmband mRightMyoArmband;//cai nay cung la 1 list right emgData
   private List<EmgData> rEmgDataList;

   private LeftMyoArmband mLeftMyoArmband;
   private List<EmgData> lEmgDataList;

   private TextView stringResult;
   public static boolean isCallingAPI = false;

   public static boolean isClickDetect = false;
   private Myo1Fragment.Myo1FragmentEndEventCallback myo1FragmentEndEventCallback = new Myo1Fragment.Myo1FragmentEndEventCallback() {
      @Override
      public void onEndEvent(List<EmgData> rEmgDataList) {
         MultipleMyosActivity.this.rEmgDataList.addAll(rEmgDataList);
         restConditionRightHand++;

         if (restConditionRightHand >= Constant.REST_CONDITION) {//sao ;lai co 12
            mRightMyoArmband = new RightMyoArmband(MultipleMyosActivity.this.rEmgDataList);
            sendRequestArmbandApi();
         }
      }
   };

   private Myo2Fragment.Myo2FragmentEndEventCallback myo2FragmentEndEventCallback = new Myo2Fragment.Myo2FragmentEndEventCallback() {
      @Override
      public void onEndEvent(List<EmgData> lEmgDataList) {
         MultipleMyosActivity.this.lEmgDataList.addAll(lEmgDataList);
         restConditionLeftHand++;

         if (restConditionLeftHand >= Constant.REST_CONDITION) {
            mLeftMyoArmband = new LeftMyoArmband(MultipleMyosActivity.this.lEmgDataList);
            sendRequestArmbandApi();
         }
      }
   };

   private RequestApiListener requestApiListener = new RequestApiListener() {
      @Override
      public void onRequestDone(Response response) {
         Log.w(TAG, response.toString());
         myo1Fragment.getEmgDataList().clear();
         myo2Fragment.getEmgDataList().clear();
         mLeftMyoArmband = null;
         mRightMyoArmband = null;
         stringResult.setText(response.getStringData());
         isCallingAPI = false;
      }

      @Override
      public void onPrepareRequest() {
         myo1Fragment.onClickNoEMG();
         myo2Fragment.onClickNoEMG();
         isClickDetect = false;
         isCallingAPI = true;
      }
   };

   // We store each Myo object that we attach to in this list, so that we can keep track of the order we've seen
   // each Myo and give it a unique short identifier (see onAttach() and identifyMyo() below).
   private ArrayList<Myo> mKnownMyos = new ArrayList<>();
   private MyoAdapter mAdapter;

   private DeviceListener mListener = new AbstractDeviceListener() {
      // Every time the SDK successfully attaches to a Myo armband, this function will be called.
      //
      // You can rely on the following rules:
      //  - onAttach() will only be called once for each Myo device
      //  - no other events will occur involving a given Myo device before onAttach() is called with it
      //
      // If you need to do some kind of per-Myo preparation before handling events, you can safely do it in onAttach().
      @Override
      public void onAttach(Myo myo, long timestamp) {
         // The object for a Myo is unique - in other words, it's safe to compare two Myo references to
         // see if they're referring to the same Myo.
         // Add the Myo object to our list of known Myo devices. This list is used to implement identifyMyo() below so
         // that we can give each Myo a nice short identifier.
         mKnownMyos.add(myo);
         // Now that we've added it to our list, get our short ID for it and print it out.
         Log.i(TAG, "Attached to " + myo.getMacAddress() + ", now known as Myo " + identifyMyo(myo) + ".");
      }

      @Override
      public void onConnect(Myo myo, long timestamp) {
         mAdapter.setMessage(myo, "Myo " + identifyMyo(myo) + " has connected.");
      }

      @Override
      public void onDisconnect(Myo myo, long timestamp) {
         mAdapter.setMessage(myo, "Myo " + identifyMyo(myo) + " has disconnected.");
      }

      @Override
      public void onPose(Myo myo, long timestamp, Pose pose) {
         mAdapter.setMessage(myo, "Myo " + identifyMyo(myo) + " switched to pose " + pose.toString() + ".");
      }
   };

   private void replaceMyo(Myo myo) {
      if (myo.getMacAddress().equals(mKnownMyos.get(0).getMacAddress())) {
         myo1Fragment = Myo1Fragment.getInstance(myo, mBluetoothAdapter, myo1FragmentEndEventCallback);
         replaceFragment(R.id.ll1, myo1Fragment);
      } else {
         myo2Fragment = Myo2Fragment.getInstance(myo, mBluetoothAdapter, myo2FragmentEndEventCallback);
         replaceFragment(R.id.ll2, myo2Fragment);
      }
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main_layout);
      this.gson = new Gson();
      stringResult = (TextView) findViewById(R.id.tv_stringresult);
      BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
      mBluetoothAdapter = mBluetoothManager.getAdapter();

      bindingMyoAmrband();
   }

   public void detectGesture(View view) {
      restConditionLeftHand = 0;
      restConditionRightHand = 0;

      isClickDetect = true;

      myo1Fragment.onClickEMG();
      myo2Fragment.onClickEMG();

      rEmgDataList = new ArrayList<>();
      lEmgDataList = new ArrayList<>();
   }

   private void bindingMyoAmrband() {
      mKnownMyos = TrainActivity.mKnownMyos;

      mAdapter = new MyoAdapter(this, 2);
      ListView listView = (ListView) findViewById(R.id.list);
      listView.setAdapter(mAdapter);

      if (mKnownMyos != null && !mKnownMyos.isEmpty()) {
         for (Myo myo : mKnownMyos) {
            mAdapter.setMessage(myo, "Myo " + identifyMyo(myo) + " has connected.");
            replaceMyo(myo);
         }
      }
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
      // We don't want any callbacks when the Activity is gone, so unregister the listener.
      Hub.getInstance().removeListener(mListener);
      // Shutdown the Hub. This will disconnect any Myo devices that are connected.
      Hub.getInstance().shutdown();
   }

   // This is a utility function implemented for this sample that maps a Myo to a unique ID starting at 1.
   // It does so by looking for the Myo object in mKnownMyos, which onAttach() adds each Myo into as it is attached.
   private int identifyMyo(Myo myo) {
      return mKnownMyos.indexOf(myo) + 1;
   }

   private class MyoAdapter extends ArrayAdapter<String> {
      public MyoAdapter(Context context, int count) {
         super(context, android.R.layout.simple_list_item_1);
         // Initialize adapter with items for each expected Myo.
         for (int i = 0; i < count; i++) {
            add("Waiting");
         }
      }

      public void setMessage(Myo myo, String message) {
         // identifyMyo returns IDs starting at 1, but the adapter indices start at 0.
         int index = identifyMyo(myo) - 1;
         // Replace the message.
         remove(getItem(index));
         insert(message, index);
      }
   }

   private void replaceFragment(int containerResId, Fragment fragment) {
      FragmentManager fragmentManager = getSupportFragmentManager();
      FragmentTransaction transaction = fragmentManager.beginTransaction();
      transaction.replace(containerResId, fragment);
      transaction.commit();
   }

   private void sendRequestArmbandApi() {

      if (!isCallingAPI && mLeftMyoArmband != null && mRightMyoArmband != null
            && !mLeftMyoArmband.getLeft().isEmpty() && !mRightMyoArmband.getRight().isEmpty()
            && restConditionLeftHand >= Constant.REST_CONDITION && restConditionRightHand >= Constant.REST_CONDITION) {//<12 thi sao
         int minDataSize = (mLeftMyoArmband.getLeft().size() <= mRightMyoArmband.getRight().size()) ? mLeftMyoArmband.getLeft().size() : mRightMyoArmband.getRight().size();
         for (int i = minDataSize; i < mLeftMyoArmband.getLeft().size(); i++) {
            mLeftMyoArmband.getLeft().remove(i);
         }

         for (int i = minDataSize; i < mRightMyoArmband.getRight().size(); i++) {
            mRightMyoArmband.getRight().remove(i);
         }

         Log.w(TAG, "myoSignalContent size left: " + mLeftMyoArmband.getLeft().size() + " | right: " + mRightMyoArmband.getRight().size());
         MyoSignal myoSignal = new MyoSignal(mLeftMyoArmband, mRightMyoArmband);
         String myoSignalContent = this.gson.toJson(myoSignal);

         if (!TextUtils.isEmpty(myoSignalContent)) {
            RequestUtils.getInstance().sendEmgData(requestApiListener, myoSignalContent);
         } else {
            Log.w(TAG, "myoSignalContent: " + myoSignalContent);
         }
      }
   }
}
