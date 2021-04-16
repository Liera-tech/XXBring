package com.liera.xxbring.response;

public class MePostUploadResponse extends BaseResponse<MePostUploadResponse.SuccessBean> {

    public static class SuccessBean{
        String data;

        public String getData() {
            return data;
        }

        @Override
        public String toString() {
            return "SuccessBean{" +
                    "data='" + data + '\'' +
                    '}';
        }
    }
}
