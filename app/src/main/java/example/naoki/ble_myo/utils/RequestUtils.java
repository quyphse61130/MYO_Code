package example.naoki.ble_myo.utils;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import vn.wisky.pos.constant.Constant;
import vn.wisky.pos.constant.RequestConstant;
import vn.wisky.pos.listener.RequestApiListener;
import vn.wisky.pos.task.RequestTask;

/**
 * Created by PhatNT
 * on 07/09/2015.
 */
public class RequestUtils {

    private static RequestUtils instance;

    public static RequestUtils getInstance() {
        if (instance == null) {
            instance = new RequestUtils();
        }
        return instance;
    }

    public void doLogin(RequestApiListener requestApiListener, String loginJsonContent) {
        Map<String, String> params = new HashMap<>();
        new RequestTask(requestApiListener, params, loginJsonContent).execute(RequestConstant.URL_SERVER_LOGIN, Constant.POST_METHOD);
    }

    @NonNull
    private Map<String, String> setGraphParams(String storeId, String sStartDate, String sEndDate, String token) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.PARAM_STORE_ID, storeId);
        params.put(RequestConstant.PARAM_START_TIME, sStartDate);
        params.put(RequestConstant.PARAM_END_TIME, sEndDate);
        params.put(RequestConstant.PARAM_ACCESS_TOKEN, token);
        return params;
    }

    public void getOverViewData(RequestApiListener requestApiListener, String storeId, String sStartDate, String sEndDate, String token) {
        Map<String, String> params = setGraphParams(storeId, sStartDate, sEndDate, token);
        new RequestTask(requestApiListener, params, null).execute(RequestConstant.URL_SERVER_GET_OVERVIEW_DATA, Constant.GET_METHOD);
    }

    public void getRevenueDayInMonthData(RequestApiListener requestApiListener, String storeId, String sStartDate, String sEndDate, String token) {
        Map<String, String> params = setGraphParams(storeId, sStartDate, sEndDate, token);
        new RequestTask(requestApiListener, params, null).execute(RequestConstant.URL_SERVER_GET_REVENUE_DAY_DATA, Constant.GET_METHOD);
    }

    public void getRevenueMonthData(RequestApiListener requestApiListener, String storeId, String sStartDate, String sEndDate, String token) {
        Map<String, String> params = setGraphParams(storeId, sStartDate, sEndDate, token);
        new RequestTask(requestApiListener, params, null).execute(RequestConstant.URL_SERVER_GET_REVENUE_MONTH_DATA, Constant.GET_METHOD);
    }

    public void getRevenueDayInWeekData(RequestApiListener requestApiListener, String storeId, String sStartDate, String sEndDate, String token) {
        Map<String, String> params = setGraphParams(storeId, sStartDate, sEndDate, token);
        new RequestTask(requestApiListener, params, null).execute(RequestConstant.URL_SERVER_GET_REVENUE_DAY_IN_WEEK_DATA, Constant.GET_METHOD);
    }

    public void getRevenueHourData(RequestApiListener requestApiListener, String storeId, String sStartDate, String sEndDate, String token) {
        Map<String, String> params = setGraphParams(storeId, sStartDate, sEndDate, token);
        new RequestTask(requestApiListener, params, null).execute(RequestConstant.URL_SERVER_GET_REVENUE_HOUR_DATA, Constant.GET_METHOD);
    }

    public void getCategoryProductData(RequestApiListener requestApiListener, String storeId, String sStartDate, String sEndDate, String token) {
        Map<String, String> params = setGraphParams(storeId, sStartDate, sEndDate, token);
        new RequestTask(requestApiListener, params, null).execute(RequestConstant.URL_SERVER_GET_CATEGORY_PRODUCT_DATA, Constant.GET_METHOD);
    }

    public void getCategoryDepartmentData(RequestApiListener requestApiListener, String storeId, String sStartDate, String sEndDate, String categoryId, String token) {
        Map<String, String> params = setGraphParams(storeId, sStartDate, sEndDate, token);
        params.put(RequestConstant.PARAM_CATEGORY_ID, categoryId);
        new RequestTask(requestApiListener, params, null).execute(RequestConstant.URL_SERVER_GET_CATEGORY_DATA, Constant.GET_METHOD);
    }

    public void getShiftData(RequestApiListener requestApiListener, String storeId, String sStartDate, String sEndDate, String token) {
        Map<String, String> params = setGraphParams(storeId, sStartDate, sEndDate, token);
        new RequestTask(requestApiListener, params, null).execute(RequestConstant.URL_SERVER_GET_SHIFT_DATA, Constant.GET_METHOD);
    }

    public void getProducts(RequestApiListener requestApiListener, String storeId, String sStartDate, String sEndDate, String token) {
        Map<String, String> params = setGraphParams(storeId, sStartDate, sEndDate, token);
        new RequestTask(requestApiListener, params, null).execute(RequestConstant.URL_SERVER_GET_PRODUCTS, Constant.GET_METHOD);
    }

    public void getLoadDateProductData(RequestApiListener requestApiListener, String storeId, String sStartDate, String sEndDate, String productId, String token) {
        Map<String, String> params = setGraphParams(storeId, sStartDate, sEndDate, token);
        params.put(RequestConstant.PARAM_PRODUCT_ID, productId);
        new RequestTask(requestApiListener, params, null).execute(RequestConstant.URL_SERVER_LOAD_DATE_DETAIL_PRODUCT, Constant.GET_METHOD);
    }

    public void getLoadMonthProductData(RequestApiListener requestApiListener, String storeId, String sStartDate, String sEndDate, String productId, String token) {
        Map<String, String> params = setGraphParams(storeId, sStartDate, sEndDate, token);
        params.put(RequestConstant.PARAM_PRODUCT_ID, productId);
        new RequestTask(requestApiListener, params, null).execute(RequestConstant.URL_SERVER_LOAD_MONTH_DETAIL_PRODUCT, Constant.GET_METHOD);
    }

    public void getSystemRevenueReport(RequestApiListener requestApiListener, String sStartDate, String sEndDate, String token) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.PARAM_START_TIME, sStartDate);
        params.put(RequestConstant.PARAM_END_TIME, sEndDate);
        params.put(RequestConstant.PARAM_ACCESS_TOKEN, token);
        new RequestTask(requestApiListener, params, null).execute(RequestConstant.URL_SERVER_SYSTEM_REVENUE_REPORT, Constant.GET_METHOD);
    }
}

