package example.naoki.ble_myo.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.thalmic.myo.Hub;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import example.naoki.ble_myo.R;

public class ListActivity extends ActionBarActivity implements BluetoothAdapter.LeScanCallback {
   public static final int MENU_SCAN = 0;
   public static final int LIST_DEVICE_MAX = 5;
   private static final String LOG_TAG = ListActivity.class.getSimpleName();

   public static String TAG = "BluetoothList";

   /**
    * Device Scanning Time (ms)
    */
   private static final long SCAN_PERIOD = 5000;

   /**
    * Intent code for requesting Bluetooth enable
    */
   private static final int REQUEST_ENABLE_BT = 1;

   private Handler mHandler;
   private BluetoothAdapter mBluetoothAdapter;
   private BluetoothGatt mBluetoothGatt;
   private ArrayList<String> deviceNames = new ArrayList<>();
   private String myoName = null;
   private List<String> listMyoName;
   private ArrayAdapter<String> adapter;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_list);

      BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
      mBluetoothAdapter = mBluetoothManager.getAdapter();
      mHandler = new Handler();

      ListView lv = (ListView) findViewById(R.id.listView1);


      adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_expandable_list_item_1, deviceNames);

      lv.setAdapter(adapter);
      listMyoName = new ArrayList<String>();
      lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView listView = (ListView) parent;
            String item = (String) listView.getItemAtPosition(position);
            myoName = item;

            if (!listMyoName.contains(myoName)) {
               listMyoName.add(myoName);
            }
            Log.d(LOG_TAG, "Total connected myo: " + listMyoName.size());
         }
      });
      IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
      registerReceiver(mPairReceiver, intent);
   }

   public void onClickStart(View view) {
      String listConnectedMyo = new Gson().toJson(listMyoName);
      Intent returnIntent = getIntent();
      returnIntent.putExtra(ListActivity.TAG, listConnectedMyo);
      setResult(Activity.RESULT_OK, returnIntent);
      finish();
   }


   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      //getMenuInflater().inflate(R.menu.menu_list, menu);
      menu.add(0, MENU_SCAN, 0, "Scan");

      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();

      //noinspection SimplifiableIfStatement
      if (id == MENU_SCAN) {
         scanDevice();
         return true;
      }

      return super.onOptionsItemSelected(item);
   }

   public void onClickScan(View v) {
      scanDevice();
   }

   @Override
   public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
      // Device Log
      ParcelUuid[] uuids = device.getUuids();
      String uuid = "";
      if (uuids != null) {
         for (ParcelUuid puuid : uuids) {
            uuid += puuid.toString() + " ";
         }
      }

      String msg = "name=" + device.getName() + ", bondStatus="
            + device.getBondState() + ", address="
            + device.getAddress() + ", type" + device.getType()
            + ", uuids=" + uuid;
      Log.d("BLEActivity", msg);

      if (device.getName() != null && !deviceNames.contains(device.getName())) {
         deviceNames.add(device.getName());
      }
   }

   public void scanDevice() {
      // Ensures Bluetooth is available on the device and it is enabled. If not,
      // displays a dialog requesting user permission to enable Bluetooth.
      if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
         Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
         startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
      } else {
         deviceNames.clear();
         // Scanning Time out by Handler.
         // The device scanning needs high energy.
         mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
               mBluetoothAdapter.stopLeScan(ListActivity.this);

               adapter.notifyDataSetChanged();
               Toast.makeText(getApplicationContext(), "Stop Device Scan", Toast.LENGTH_SHORT).show();

            }
         }, SCAN_PERIOD);
         mBluetoothAdapter.startLeScan(ListActivity.this);
      }
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
      unregisterReceiver(mPairReceiver);
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
         scanDevice();
      }
   }


   private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
      public void onReceive(Context context, Intent intent) {
         String action = intent.getAction();
         if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
            final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
            final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

            if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
               Toast.makeText(ListActivity.this, "Paired", Toast.LENGTH_SHORT).show();

            } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {
               Toast.makeText(ListActivity.this, "Unpaired", Toast.LENGTH_SHORT).show();
            }
         }
      }
   };

   @Override
   public void onBackPressed() {
      super.onBackPressed();
      String listConnectedMyo = new Gson().toJson(listMyoName);
      Intent returnIntent = getIntent();
      returnIntent.putExtra(ListActivity.TAG, listConnectedMyo);
      setResult(Activity.RESULT_OK, returnIntent);
      finish();
   }
}
