package com.rnkrsoft.domains;

import javax.web.doc.annotation.ApidocElement;
import javax.web.doc.AbstractResponse;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import com.rnkrsoft.domains.*;

/**
 * Created by interface platform generator
 */
public class AddResponse extends AbstractResponse {
    @ApidocElement(value = "流水号", required = true, minLen = 0, maxLen = 255)
    String serialNo;

    @ApidocElement(value = "记录" , required = true)
    final List<Record1VO> records = new ArrayList();

    public String getSerialNo() {
        return this.serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public List<Record1VO> getRecords() {
        return this.records;
    }

}