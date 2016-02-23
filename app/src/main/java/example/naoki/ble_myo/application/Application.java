package example.naoki.ble_myo.application;

/**
 * Created by QuyPH on 2/22/2016.
 */
public class Application extends android.app.Application {
   private static Application application;

   public static Application getInstance() {
      return application;
   }

   @Override
   public void onCreate() {
      super.onCreate();
      application = this;
   }
}
