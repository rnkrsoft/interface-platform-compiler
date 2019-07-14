package com.rnkrsoft.platform.compiler;

import com.rnkrsoft.interfaces.EnumStringCode;

public enum DeviceType implements EnumStringCode {
    Swift("swift", "苹果手机"),
    Android("Android", "安卓手机"),
    AndroidInner("AndroidInner", "安卓手机内部类"),
    Java("Java", "Java"),
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