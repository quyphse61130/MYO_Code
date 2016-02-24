package example.naoki.ble_myo.listener;


import example.naoki.ble_myo.model.response.Response;

/**
 * Author: phatnt199
 * Created on 10/7/15.
 */
public interface RequestApiListener extends RequestListener {
    void onRequestDone(Response response);
}
