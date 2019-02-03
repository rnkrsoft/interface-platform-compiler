package com.rnkrsoft.domains;

import javax.web.doc.annotation.ApidocElement;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import com.rnkrsoft.domains.*;

/**
 * Created by interface platform generator
 */
public class Record3VO implements Serializable {
    @ApidocElement(value = "大小", required = true, minLen = 0, maxLen = 255)
    int size;

    /**
     * MAN1 男人
     * WOMAN2 女人
     */
    @ApidocElement(value = "性别", required = true, minLen = 0, maxLen = 255)
    String sex;

    /**
     * 1 男人
     * 2 女人
     */
    @ApidocElement(value = "性别1", required = true, minLen = 0, maxLen = 255)
    int sex1;

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getSex1() {
        return this.sex1;
    }

    public void setSex1(int sex1) {
        this.sex1 = sex1;
    }

}
