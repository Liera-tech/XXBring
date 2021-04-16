package com.liera.lib_xxbring.callback;

import android.os.Handler;

import com.liera.lib_xxbring.request.IXXBringRequest;
import com.liera.lib_xxbring.response.IXXBringResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 响应为json
 */
public interface XXBringJsonArrayCallback extends XXBringBaseCallback {

    void jsonResponseArraySuccess(IXXBringRequest req, ArrayList<? extends IXXBringResponse> resp, Handler mHandler);
}
