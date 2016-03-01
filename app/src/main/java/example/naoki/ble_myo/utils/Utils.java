package example.naoki.ble_myo.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by QuyPH on 3/1/2016.
 */
public class Utils {
   public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
   private static final String PARAMETER_SEPARATOR = "&";
   private static final String NAME_VALUE_SEPARATOR = "=";

   private static Utils instance;

   public static Utils getInstance() {
      if (instance == null) {
         instance = new Utils();
      }

      return instance;
   }

   public static String formatParams(
         Map<String, String> parameters,
         String encoding) {
      final StringBuilder result = new StringBuilder();

      try {
         Set paramsSet = parameters.entrySet();
         Map.Entry<String, String> entryParam = null;
         for (Object parameter : paramsSet) {
            entryParam = (Map.Entry<String, String>) parameter;

            String encodedName = URLEncoder.encode(entryParam.getKey(), encoding);
            String value = entryParam.getValue();
            String encodedValue = value != null ? URLEncoder.encode(value, encoding) : "";
            if (result.length() > 0)
               result.append(PARAMETER_SEPARATOR);
            result.append(encodedName);
            result.append(NAME_VALUE_SEPARATOR);
            result.append(encodedValue);
         }
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
      }
      return result.toString();
   }
}
