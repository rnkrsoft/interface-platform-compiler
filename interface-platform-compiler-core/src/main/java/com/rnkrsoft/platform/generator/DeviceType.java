package com.rnkrsoft.platform.generator;

import com.rnkrsoft.interfaces.EnumStringCode;

@Deprecated
public enum DeviceType implements EnumStringCode {
    @Deprecated
    iOS("iOS", "苹果手机"),
    @Deprecated
    Android("Android", "安卓手机"),
    @Deprecated
    Java("Java", "Java"),
    @Deprecated
    JavaScript("JavaScript", "JavaScript");

    String code;
    String desc;

    private DeviceType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }
}