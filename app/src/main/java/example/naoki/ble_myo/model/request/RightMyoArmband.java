package example.naoki.ble_myo.model.request;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import example.naoki.ble_myo.model.myo.EmgData;

/**
 * Created by QuyPH on 2/22/2016.
 */
public class RightMyoArmband implements Serializable {
   @SerializedName("right")
   private List<EmgData> right;

   public RightMyoArmband() {
   }

   public RightMyoArmband(List<EmgData> right) {
      this.right = right;
   }

   public List<EmgData> getRight() {
      return right;
   }

   public void setRight(List<EmgData> right) {
      this.right = right;
   }

   @Override
   public String toString() {
      return "LeftMyoArmband{" +
            "right=" + right.size() +
            '}';
   }
}
