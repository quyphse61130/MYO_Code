package example.naoki.ble_myo.model;

import example.naoki.ble_myo.callback.IGestureDetectAction;
import example.naoki.ble_myo.callback.IGestureDetectModel;

/**
 * Created by naoki on 15/04/16.
 */
public class NopModel implements IGestureDetectModel {
    @Override
    public void event(long time, byte[] data) {
    }

    @Override
    public void setAction(IGestureDetectAction action) {
    }


    @Override
    public void action() {

    }
}
