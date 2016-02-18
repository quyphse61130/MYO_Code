package example.naoki.ble_myo.model;

import java.io.Serializable;

/**
 * Created by TanPhat on 18/02/2016.
 */
public class MyoModel implements Serializable {
    private String name;
    private String macAddress;

    public MyoModel(String name, String macAddress) {
        this.name = name;
        this.macAddress = macAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    @Override
    public String toString() {
        return "MyoModel{" +
                "name='" + name + '\'' +
                ", macAddress='" + macAddress + '\'' +
                '}';
    }
}
