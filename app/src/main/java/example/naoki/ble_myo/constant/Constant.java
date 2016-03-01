package example.naoki.ble_myo.constant;

/**
 * Created by QuyPH on 2/18/2016.
 */
public class Constant {
   public static final String GET_METHOD = "GET";
   public static final String POST_METHOD = "POST";

   public static final int MENU_LIST = 0;
   public static final int MENU_BYE = 1;
   /**
    * Device Scanning Time (ms)
    */
   public static final long SCAN_PERIOD = 5000;

   /**
    * Intent code for requesting Bluetooth enable
    */
   public static final int REQUEST_ENABLE_BT = 1;

   public static final double DEFAULT_END_EVENT = 25;
   public static final int DEFAULT_HZ_BREAK_EVENT = 3;
   public static final int DEFAULT_HZ_START_EVENT = 0;

   public static final int REST_CONDITION = 12;

   public static final String KNOWN_MYO_LIST = "known_myo_list";
}
