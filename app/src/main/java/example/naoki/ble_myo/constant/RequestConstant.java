package example.naoki.ble_myo.constant;

/**
 * Created by QuyPH on 2/22/2016.
 */
public class RequestConstant {
   public static final String URL_BASE_SERVER = "http://116.106.193.48:8080";

   public static final String PARAM_L_EMG_JSON = "lEmgJson";
   public static final String PARAM_R_EMG_JSON = "rEmgJson";

   public static final String URL_REQUEST_EMG_DATA = URL_BASE_SERVER + "/MYO-war/TranslateServlet";
}
