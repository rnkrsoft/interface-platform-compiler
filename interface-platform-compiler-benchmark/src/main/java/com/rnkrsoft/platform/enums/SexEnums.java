package com.rnkrsoft.platform.enums;

import com.rnkrsoft.interfaces.EnumStringCode;

/**
 * Created by rnkrsoft.com on 2018/10/10.
 */
public enum SexEnums implements EnumStringCode {
    MAN("MAN1", "男人"),
    WOMAN("WOMAN2", "女人");
    String code;
    String desc;

    SexEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
