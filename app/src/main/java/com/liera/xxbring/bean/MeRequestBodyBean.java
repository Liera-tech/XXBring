package com.liera.xxbring.bean;

import com.liera.lib_xxbring.bean.XXBringRequestBody;

public class MeRequestBodyBean extends XXBringRequestBody {

    private String name;

    private String pwd;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public String toString() {
        return "MeRequestBodyBean{" +
                "name='" + name + '\'' +
                ", pwd='" + pwd + '\'' +
                '}';
    }
}
