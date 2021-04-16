package com.liera.lib_xxbring.callback;

import android.os.Handler;
import com.liera.lib_xxbring.request.IXXBringRequest;

/**
 * 响应为byteArray
 */
public interface XXBringByteArrayCallback extends XXBringBaseCallback {

    void byteArrayResponseSuccess(IXXBringRequest req, byte[] byteArray, Handler mHandler);

}
