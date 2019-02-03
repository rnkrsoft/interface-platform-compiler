package com.rnkrsoft.platform.domains;

import lombok.Data;

import javax.web.doc.annotation.ApidocElement;
import java.io.Serializable;

/**
 * Created by rnkrsoft.com on 2018/7/11.
 */
@Data
public class Record2VO implements Serializable{
    @ApidocElement(value = "大小", required = true)
    int size;

    @ApidocElement("Record3VO")
    Record3VO record3VO;
}
