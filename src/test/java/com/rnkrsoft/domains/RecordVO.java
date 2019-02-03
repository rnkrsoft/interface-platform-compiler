package com.rnkrsoft.domains;

import javax.web.doc.annotation.ApidocElement;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import com.rnkrsoft.domains.*;

/**
 * Created by interface platform generator
 */
public class RecordVO implements Serializable {
    @ApidocElement(value = "姓名", required = true, minLen = 0, maxLen = 255)
    String name;

    @ApidocElement(value = "年龄", required = false, minLen = 0, maxLen = 255)
    Integer age;

    @ApidocElement(value = "大小", required = true, minLen = 0, maxLen = 255)
    int size;

    @ApidocElement(value = "记录")
    Record1VO record1VO;

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

    public Record1VO getRecord1VO() {
        return this.record1VO;
    }

    public void setRecord1VO(Record1VO record1VO){
        this.record1VO = record1VO;
    }

}
