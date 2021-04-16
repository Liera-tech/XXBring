package com.liera.lib_xxbring.callback;

import android.os.Handler;

import com.liera.lib_xxbring.request.IXXBringRequest;

/**
 * 响应为text
 */
public interface XXBringTextCallback extends XXBringBaseCallback {

    void textResponseSuccess(IXXBringRequest req, String text, Handler mHandler);

}
