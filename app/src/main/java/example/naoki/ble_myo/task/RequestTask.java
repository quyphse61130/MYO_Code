package example.naoki.ble_myo.task;

import android.os.AsyncTask;

import java.util.Map;

import vn.wisky.pos.constant.Constant;
import vn.wisky.pos.listener.RequestApiListener;
import vn.wisky.pos.manager.NetworkManager;
import vn.wisky.pos.model.response.Response;
import vn.wisky.pos.utils.Utils;

/**
 * Created by PhatNT
 * on 14/09/2015.
 */
public class RequestTask extends AsyncTask<String, Void, Response> {
    private RequestApiListener requestListener;
    private Map<String, String> params;
    private String jsonDataContent;

    public RequestTask(RequestApiListener requestListener, Map<String, String> params, String jsonDataContent) {
        this.requestListener = requestListener;
        this.params = params;
        this.jsonDataContent = jsonDataContent;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        requestListener.onPrepareRequest();
    }

    @Override
    protected Response doInBackground(String... params) {
        String url = params[0];
        String method = params[1];
        NetworkManager executor = new NetworkManager();
        if (Constant.GET_METHOD.equalsIgnoreCase(method)) {
            return executor.executeGetRequest(url, this.params, jsonDataContent);
        } else {
            return executor.executePostRequest(url, this.params, jsonDataContent);
        }
    }

    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);
        requestListener.onRequestDone(response);
    }

    @Override
    protected void onCancelled() {
        Utils.showDebugLog("onCancelled", "onCancelled 1");
        super.onCancelled();
        requestListener = new RequestApiListener() {
            @Override
            public void onPrepareRequest() {

            }

            @Override
            public void onRequestDone(Response response) {

            }
        };
    }

    @Override
    protected void onCancelled(Response response) {
        Utils.showDebugLog("onCancelled", "onCancelled 2");
        super.onCancelled(response);
        requestListener = new RequestApiListener() {
            @Override
            public void onPrepareRequest() {

            }

            @Override
            public void onRequestDone(Response response) {

            }
        };
    }
}
