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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
import example.naoki.ble_myo.fragment.TrainingMyo1Fragment;
import example.naoki.ble_myo.fragment.TrainingMyo2Fragment;
import example.naoki.ble_myo.listener.RequestApiListener;
import example.naoki.ble_myo.model.myo.EmgData;
import example.naoki.ble_myo.model.response.Response;
import example.naoki.ble_myo.utils.RequestUtils;

/**
 * Created by QuyPH on 2/27/2016.
 */
public class TrainActivity extends FragmentActivity {

   private static final String TAG = "TrainActivity";
   private TrainingMyo1Fragment trainingMyo1Fragment;
   private TrainingMyo2Fragment trainingMyo2Fragment;

   private BluetoothAdapter mBluetoothAdapter;

   private List<EmgData> rEmgDataList;
   private List<EmgData> lEmgDataList;

   private EditText etMeaning;

   public static boolean isClickDetect = false;
   public static boolean isCallingAPI = false;

   public static boolean isClickSave = false;

   private RequestApiListener requestApiListener = new RequestApiListener() {
      @Override
      public void onRequestDone(Response response) {
         Log.w(TAG, response.getStringData());
         isCallingAPI = false;
      }

      @Override
      public void onPrepareRequest() {
         isClickDetect = false;
         isCallingAPI = true;
      }
   };

   // We store each Myo object that we attach to in this list, so that we can keep track of the order we've seen
   // each Myo and give it a unique short identifier (see onAttach() and identifyMyo() below).
   public static ArrayList<Myo> mKnownMyos = new ArrayList<>();
   private MyoAdapter mAdapter;

   private int currentConnectedMyo;


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
         currentConnectedMyo = mKnownMyos.size();
          if (currentConnectedMyo == 1) {
            trainingMyo1Fragment = TrainingMyo1Fragment.getInstance(myo, mBluetoothAdapter);
            replaceFragment(R.id.ll1, trainingMyo1Fragment);
         } else if (currentConnectedMyo == 2) {
            trainingMyo2Fragment = TrainingMyo2Fragment.getInstance(myo, mBluetoothAdapter);
            replaceFragment(R.id.ll2, trainingMyo2Fragment);
         }
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

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_train);
      currentConnectedMyo = 0;

      etMeaning = (EditText) findViewById(R.id.et_meaning);

      BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
      mBluetoothAdapter = mBluetoothManager.getAdapter();

      bindingMyoAmrband();
   }

   @Override
   protected void onResume() {
      super.onResume();
      if (mKnownMyos != null && !mKnownMyos.isEmpty()) {
         for (Myo myo : mKnownMyos) {
            mAdapter.setMessage(myo, "Myo " + identifyMyo(myo) + " has connected.");
            replaceMyo(myo);
         }
      }
   }

   private void replaceMyo(Myo myo) {
      if (myo.getMacAddress().equals(mKnownMyos.get(0).getMacAddress())) {
         trainingMyo1Fragment = TrainingMyo1Fragment.getInstance(myo, mBluetoothAdapter);
         replaceFragment(R.id.ll1, trainingMyo1Fragment);
      } else {
         trainingMyo2Fragment = TrainingMyo2Fragment.getInstance(myo, mBluetoothAdapter);
         replaceFragment(R.id.ll2, trainingMyo2Fragment);
      }
   }

   public void startTrainingGesture(View view) {
      isClickDetect = true;
      isClickSave = false;

      trainingMyo1Fragment.onClickEMG();
      trainingMyo2Fragment.onClickEMG();

      rEmgDataList = new ArrayList<>();
      trainingMyo1Fragment.setEmgDataList(rEmgDataList);

      lEmgDataList = new ArrayList<>();
      trainingMyo2Fragment.setEmgDataList(lEmgDataList);
   }

   public void detectGesture(View view) {
      Intent intent = new Intent(TrainActivity.this, MultipleMyosActivity.class);
      startActivity(intent);
   }

   public void saveGesture(View view) {
      trainingMyo1Fragment.onClickNoEMG();
      trainingMyo2Fragment.onClickNoEMG();

      rEmgDataList = trainingMyo1Fragment.getEmgDataList();
      lEmgDataList = trainingMyo2Fragment.getEmgDataList();

      sendTrainingData();
   }

   private void bindingMyoAmrband() {
      // First, we initialize the Hub singleton.
      Hub hub = Hub.getInstance();
      if (!hub.init(this)) {
         // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
         Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
         finish();
         return;
      }
      // Disable standard Myo locking policy. All poses will be delivered.
      hub.setLockingPolicy(Hub.LockingPolicy.NONE);
      final int attachingCount = 2;
      // Set the maximum number of simultaneously attached Myos to 2.
      hub.setMyoAttachAllowance(attachingCount);
      Log.i(TAG, "Attaching to " + attachingCount + " Myo armbands.");
      // attachToAdjacentMyos() attaches to Myo devices that are physically very near to the Bluetooth radio
      // until it has attached to the provided count.
      // DeviceListeners attached to the hub will receive onAttach() events once attaching has completed.
      hub.attachToAdjacentMyos(attachingCount);
      // Next, register for DeviceListener callbacks.
      hub.addListener(mListener);
      // Attach an adapter to the ListView for showing the state of each Myo.
      mAdapter = new MyoAdapter(this, attachingCount);
      ListView listView = (ListView) findViewById(R.id.list);
      listView.setAdapter(mAdapter);
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

   private void sendTrainingData() {

      if (rEmgDataList != null && lEmgDataList != null
            && !rEmgDataList.isEmpty() && !lEmgDataList.isEmpty()
            && !TextUtils.isEmpty(etMeaning.getText().toString())) {

         String leftData = lEmgDataList.get(lEmgDataList.size()-1).formatData();
         String rightData = rEmgDataList.get(rEmgDataList.size()-1).formatData();
         String meaning = etMeaning.getText().toString();
         RequestUtils.getInstance().sendEmgDataTrain(requestApiListener, leftData, rightData, meaning);
      }
   }
}
