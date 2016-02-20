package example.naoki.ble_myo.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;

import java.util.ArrayList;
import java.util.List;

import example.naoki.ble_myo.R;
import example.naoki.ble_myo.callback.IGestureDetectModel;
import example.naoki.ble_myo.constant.Constant;
import example.naoki.ble_myo.fragment.Myo1Fragment;
import example.naoki.ble_myo.fragment.Myo2Fragment;
import example.naoki.ble_myo.model.GestureDetectMethod;
import example.naoki.ble_myo.model.GestureDetectModel;
import example.naoki.ble_myo.model.GestureDetectModelManager;
import example.naoki.ble_myo.model.GestureDetectSendResultAction;
import example.naoki.ble_myo.model.GestureSaveMethod;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
        mHandler = new Handler();
        gestureText = (TextView) findViewById(R.id.gestureTextView);

        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
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
}

