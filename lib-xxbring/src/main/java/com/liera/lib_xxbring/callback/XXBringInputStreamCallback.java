package com.liera.lib_xxbring.callback;

import android.os.Handler;
import com.liera.lib_xxbring.request.IXXBringRequest;
import java.io.InputStream;

/**
 * 响应为InputStream
 */
public interface XXBringInputStreamCallback extends XXBringBaseCallback {

    void inputStreamResponseSuccess(IXXBringRequest req, InputStream input, Handler mHandler);

}
