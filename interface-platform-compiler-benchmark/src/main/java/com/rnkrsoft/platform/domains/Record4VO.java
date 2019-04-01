package com.rnkrsoft.platform.domains;

import lombok.Data;

import javax.web.doc.annotation.ApidocElement;
import java.io.Serializable;

/**
 * Created by woate on 2019/4/1.
 */
@Data
public class Record4VO implements Serializable{
    @ApidocElement(value = "姓名", required = true, defaults = "")
    String name;
    @ApidocElement(value = "年龄", required = false)
    Integer age;
    @ApidocElement(value = "大小", required = true)
    int size;
}
