package com.rnkrsoft.domains;

import javax.web.doc.annotation.ApidocElement;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import com.rnkrsoft.domains.*;

/**
 * Created by interface platform generator
 */
public class Record2VO implements Serializable {
    @ApidocElement(value = "大小", required = true, minLen = 0, maxLen = 255)
    int size;

    @ApidocElement(value = "Record3VO")
    Record3VO record3VO;

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Record3VO getRecord3VO() {
        return this.record3VO;
    }

    public void setRecord3VO(Record3VO record3VO){
        this.record3VO = record3VO;
    }

}
