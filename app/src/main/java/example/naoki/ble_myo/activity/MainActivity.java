package example.naoki.ble_myo.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;
import java.util.Set;

import example.naoki.ble_myo.R;
import example.naoki.ble_myo.callback.IGestureDetectModel;
import example.naoki.ble_myo.constant.Constant;
import example.naoki.ble_myo.fragment.Myo1Fragment;
import example.naoki.ble_myo.fragment.Myo2Fragment;
import example.naoki.ble_myo.model.GestureDetectMethod;
import example.naoki.ble_myo.model.GestureDetectModel;
import example.naoki.ble_myo.model.GestureDetectModelManager;
import example.naoki.ble_myo.model.GestureDetectSendResultAction;
import example.naoki.ble_myo.model.GestureSaveModel;
import example.naoki.ble_myo.model.NopModel;

public class MainActivity extends ActionBarActivity {
   private Handler mHandler;
   private static final String TAG = "BLE_Myo";

   private BluetoothAdapter mBluetoothAdapter;
   private TextView gestureText;

   private String deviceNamesString;
   private List<String> deviceNames;

   private GestureSaveModel saveModel;
   private GestureDetectModel detectModel;
   private GestureDetectMethod detectMethod;

   private Myo1Fragment myo1Fragment;
   private Myo2Fragment myo2Fragment;

   private void replaceFragment(int containerResId, Fragment fragment) {
      FragmentManager fragmentManager = getSupportFragmentManager();
      FragmentTransaction transaction = fragmentManager.beginTransaction();
      transaction.replace(containerResId, fragment);
      transaction.commit();
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main_layout);
      mHandler = new Handler();

      gestureText = (TextView) findViewById(R.id.gestureTextView);

      startNopModel();

      BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
      mBluetoothAdapter = mBluetoothManager.getAdapter();

      bindingIntent();
   }

   private void bindingIntent() {
      Intent intent = getIntent();
      deviceNamesString = intent.getStringExtra(ListActivity.TAG);
      if (deviceNamesString != null && !deviceNamesString.isEmpty()) {
         deviceNames = new Gson().fromJson(deviceNamesString, List.class);

         // Ensures Bluetooth is available on the device and it is enabled. If not,
         // displays a dialog requesting user permission to enable Bluetooth.
         if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {//neu bluetooth chua mo thi mở
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Constant.REQUEST_ENABLE_BT);
         } else {
            /* // Scanning Time out by Handler.
            // The device scanning needs high energy.
            mHandler.postDelayed(new Runnable() {
               @Override
               public void run() {
                  mBluetoothAdapter.stopLeScan(MainActivity.this);
               }
            }, SCAN_PERIOD);
            mBluetoothAdapter.startLeScan(this);*/

            if (deviceNames != null && deviceNames.size() > 0) {
               if (deviceNames.get(0) != null && !deviceNames.get(0).isEmpty()) {
                  myo1Fragment = Myo1Fragment.getInstance(deviceNames.get(0), mBluetoothAdapter);
                  replaceFragment(R.id.ll1, myo1Fragment);
               }

               if (deviceNames.get(1) != null && !deviceNames.get(1).isEmpty()) {
                  myo2Fragment = Myo2Fragment.getInstance(deviceNames.get(1), mBluetoothAdapter);
                  replaceFragment(R.id.ll2, myo2Fragment);
               }

            }
         }
      }
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      menu.add(0, Constant.MENU_LIST, 0, "Find Myo");
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
      //noinspection SimplifiableIfStatement
      switch (id) {
         case Constant.MENU_LIST:
//                Log.d("Menu","Select Menu A");
            Intent intent = new Intent(this, ListActivity.class);
            startActivityForResult(intent, 199);
            return true;

      }
      return false;
   }

   /*public void onClickSave(View v) {
      if (saveMethod.getSaveState() == GestureSaveMethod.SaveState.Ready ||
            saveMethod.getSaveState() == GestureSaveMethod.SaveState.Have_Saved) {
         saveModel = new GestureSaveModel(saveMethod);
         startSaveModel();
      } else if (saveMethod.getSaveState() == GestureSaveMethod.SaveState.Not_Saved) {
         startSaveModel();
      }
      saveMethod.setState(GestureSaveMethod.SaveState.Now_Saving);
      gestureText.setText("Saving ; " + (saveMethod.getGestureCounter() + 1));
   }

   public void onClickDetect(View v) {
      if (saveMethod.getSaveState() == GestureSaveMethod.SaveState.Have_Saved) {
         gestureText.setText("Let's Go !!");
         detectMethod = new GestureDetectMethod(saveMethod.getCompareDataList());
         detectModel = new GestureDetectModel(detectMethod);
         startDetectModel();
      }
   }*/

   public void startSaveModel() {
      IGestureDetectModel model = saveModel;
      model.setAction(new GestureDetectSendResultAction(this));
      GestureDetectModelManager.setCurrentModel(model);
   }

   public void startDetectModel() {
      IGestureDetectModel model = detectModel;
      model.setAction(new GestureDetectSendResultAction(this));
      GestureDetectModelManager.setCurrentModel(model);
   }

   public void startNopModel() {
      GestureDetectModelManager.setCurrentModel(new NopModel());
   }

   public void setGestureText(final String message) {
      mHandler.post(new Runnable() {
         @Override
         public void run() {
            gestureText.setText(message);
         }
      });
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (requestCode == 199) {
         deviceNamesString = data.getStringExtra(ListActivity.TAG);
         deviceNames = new Gson().fromJson(deviceNamesString, List.class);

         if (deviceNames != null && deviceNames.size() > 0) {
            if (deviceNames.get(0) != null && !deviceNames.get(0).isEmpty()) {
               myo1Fragment = Myo1Fragment.getInstance(deviceNames.get(0), mBluetoothAdapter);
               replaceFragment(R.id.ll1, myo1Fragment);
               myo1Fragment.onActivityResult(requestCode, resultCode, data);
            }

            if (deviceNames.get(1) != null && !deviceNames.get(1).isEmpty()) {
               myo2Fragment = Myo2Fragment.getInstance(deviceNames.get(1), mBluetoothAdapter);
               replaceFragment(R.id.ll2, myo2Fragment);
               myo2Fragment.onActivityResult(requestCode, resultCode, data);
            }

         }
      }
   }
}

