package com.rnkrsoft.domains;

import javax.web.doc.annotation.ApidocElement;
import java.util.List;
import java.util.ArrayList;
import javax.web.doc.AbstractRequestPage;

import com.rnkrsoft.domains.*;

/**
 * Created by interface platform generator
 */
public class QueryRequest extends AbstractRequestPage {
    @ApidocElement(value = "姓名", required = true, minLen = 0, maxLen = 255)
    String name;

    @ApidocElement(value = "年龄", required = false, minLen = 0, maxLen = 255)
    Integer age;

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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return this.age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

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