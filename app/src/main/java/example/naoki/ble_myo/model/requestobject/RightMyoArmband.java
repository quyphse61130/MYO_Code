package example.naoki.ble_myo.model.requestobject;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import example.naoki.ble_myo.model.EmgData;

/**
 * Created by QuyPH on 2/22/2016.
 */
public class RightMyoArmband implements Serializable {
   @SerializedName("left")
   private List<EmgData> left;

   public RightMyoArmband(List<EmgData> left) {
      this.left = left;
   }

   public List<EmgData> getLeft() {
      return left;
   }

   public void setLeft(List<EmgData> left) {
      this.left = left;
   }

   @Override
   public String toString() {
      return "LeftMyoArmband{" +
            "left=" + left.size() +
            '}';
   }
}