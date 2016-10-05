package com.example.maikhar.cellularfingerprint;

/**
 * Created by Maikhar on 16-Aug-16.
 */
public class CustObj {
    private String name;
    private String val;

    public CustObj(String name, String val) {
        this.name = name;
        this.val = val;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}
