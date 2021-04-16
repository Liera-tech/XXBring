package com.liera.lib_xxbring.callback;

import android.os.Handler;

import com.liera.lib_xxbring.request.IXXBringRequest;

public interface XXBringBaseCallback {

    void responseFail(IXXBringRequest req, int respCode, Exception e, Handler mHandler);

}
