package com.rnkrsoft.domains;

import javax.web.doc.annotation.ApidocElement;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

import com.rnkrsoft.domains.*;

/**
 * Created by interface platform generator
 */
public class DeleteRequest implements Serializable {
    @ApidocElement(value = "流水号", required = true, minLen = 0, maxLen = 255)
    String serialNo;

    public String getSerialNo() {
        return this.serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

}