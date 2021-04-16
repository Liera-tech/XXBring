package com.liera.lib_xxbring.callback;

import android.os.Handler;

import com.liera.lib_xxbring.request.IXXBringRequest;
import com.liera.lib_xxbring.response.IXXBringResponse;

public interface XXBringJsonObjectCallback extends XXBringBaseCallback {

    void jsonResponseObjectSuccess(IXXBringRequest req, IXXBringResponse resp, Handler mHandler);
}
