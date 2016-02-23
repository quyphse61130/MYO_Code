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

    public void requestEmgData(RequestApiListener requestApiListener, String lEmgJson, String rEmgJson) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.PARAM_L_EMG_JSON, lEmgJson);
        params.put(RequestConstant.PARAM_R_EMG_JSON, rEmgJson);
        new RequestTask(requestApiListener, params, null).execute(RequestConstant.URL_REQUEST_EMG_DATA, Constant.GET_METHOD);
    }

}

