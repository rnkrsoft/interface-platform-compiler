package com.rnkrsoft.domains;

import javax.web.doc.annotation.ApidocElement;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

import com.rnkrsoft.domains.*;

/**
 * Created by interface platform generator
 */
public class AddRequest implements Serializable {
    @ApidocElement(value = "姓名", required = true, minLen = 10, maxLen = 20)
    String name;

    @ApidocElement(value = "年龄", required = false, minLen = 5, maxLen = 255)
    Integer age;

    @ApidocElement(value = "大小", required = true, minLen = 0, maxLen = 5)
    int size;

    @ApidocElement(value = "开关", required = true, minLen = 0, maxLen = 255)
    Boolean enable;

    @ApidocElement(value = "测试开饭馆", required = true, minLen = 0, maxLen = 255)
    boolean enable1;

    @ApidocElement(value = "xxxx", required = true, minLen = 0, maxLen = 255)
    long age1;

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

    /**
     * 1 男人
     * 2 女人
     */
    @ApidocElement(value = "性别2", required = true, minLen = 0, maxLen = 255)
    final List<Integer> sex2 = new ArrayList();

    @ApidocElement(value = "列表", required = true)
    final List<RecordVO> list1 = new ArrayList();

    @ApidocElement(value = "测试")
    RecordVO recordV11;

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

    public Boolean getEnable() {
        return this.enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public boolean getEnable1() {
        return this.enable1;
    }

    public void setEnable1(boolean enable1) {
        this.enable1 = enable1;
    }

    public long getAge1() {
        return this.age1;
    }

    public void setAge1(long age1) {
        this.age1 = age1;
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

    public List<Integer> getSex2() {
        return this.sex2;
    }

    public List<RecordVO> getList1() {
        return this.list1;
    }

    public RecordVO getRecordV11() {
        return this.recordV11;
    }

    public void setRecordV11(RecordVO recordV11){
        this.recordV11 = recordV11;
    }

}