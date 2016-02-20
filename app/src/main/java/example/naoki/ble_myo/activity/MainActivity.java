package example.naoki.ble_myo.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;

import java.util.ArrayList;
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

    private List<Myo> mKnownMyos = new ArrayList<Myo>();
    private MyoAdapter mAdapter;

    private Hub hub;
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
            Log.w(TAG, "Attached to " + myo.getMacAddress() + ", now known as Myo " + identifyMyo(myo) + ".");
            hub.attachByMacAddress("CD:57:3F:83:D5:FD");
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
        bindingMyoHub();

    }

    private void bindingMyoHub() {
        // First, we initialize the Hub singleton.
        hub = Hub.getInstance();
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

                if (deviceNames.size() > 1 && deviceNames.get(1) != null && !deviceNames.get(1).isEmpty()) {
                    myo2Fragment = Myo2Fragment.getInstance(deviceNames.get(1), mBluetoothAdapter);
                    replaceFragment(R.id.ll2, myo2Fragment);
                    myo2Fragment.onActivityResult(requestCode, resultCode, data);
                }

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

    private int identifyMyo(Myo myo) {
        return mKnownMyos.indexOf(myo) + 1;
    }

    public class MyoAdapter extends ArrayAdapter<String> {

        public MyoAdapter(Context context, int count) {
            super(context, android.R.layout.simple_list_item_1);
            // Initialize adapter with items for each expected Myo.
            for (int i = 0; i < count; i++) {
                add("Waiting!");
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
}

