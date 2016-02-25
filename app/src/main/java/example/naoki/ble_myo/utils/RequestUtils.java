package example.naoki.ble_myo.utils;

import java.util.HashMap;
import java.util.Map;

import example.naoki.ble_myo.constant.Constant;
import example.naoki.ble_myo.constant.RequestConstant;
import example.naoki.ble_myo.listener.RequestApiListener;
import example.naoki.ble_myo.task.RequestTask;

/**
 * Created by PhatNT
 * on 07/09/2015.
 */
public class RequestUtils {

    private static RequestUtils instance;

    public static RequestUtils getInstance() {
        if (instance == null) {
            instance = new RequestUtils();
        }
        return instance;
    }

    public void sendEmgData(RequestApiListener requestApiListener, String myoSignalContent) {
        Map<String, String> params = new HashMap<>();
        new RequestTask(requestApiListener, params, myoSignalContent).execute(RequestConstant.URL_REQUEST_EMG_DATA, Constant.POST_METHOD);
    }

}

