package com.rnkrsoft.platform.domains;

import lombok.Data;

import javax.web.doc.annotation.ApidocElement;
import java.io.Serializable;

/**
 * Created by rnkrsoft.com on 2018/7/2.
 */
@Data
public class RecordVO implements Serializable{
    @ApidocElement(value = "姓名", required = true, defaults = "")
    String name;
    @ApidocElement(value = "年龄", required = false)
    Integer age;
    @ApidocElement(value = "大小", required = true)
    int size;

    @ApidocElement(value = "记录")
    Record1VO record1VO;
}
