package com.rnkrsoft.platform.enums;

import com.rnkrsoft.interfaces.EnumIntegerCode;

/**
 * Created by rnkrsoft.com on 2018/10/10.
 */
public enum SexEnums2 implements EnumIntegerCode {
    MAN(1, "男人"),
    WOMAN(2, "女人");
    int code;
    String desc;

    SexEnums2(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
