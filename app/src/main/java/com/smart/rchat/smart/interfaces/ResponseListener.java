package com.smart.rchat.smart.interfaces;

import com.smart.rchat.smart.util.RchatError;

import org.json.JSONObject;

/**
 * Created by nishant on 05.02.17.
 */

public interface ResponseListener  {

    void onSuccess(JSONObject jsonObject);

    void onError(Exception error);

}
