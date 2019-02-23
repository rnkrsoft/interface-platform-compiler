package com.rnkrsoft.platform.domains;

import com.rnkrsoft.platform.enums.SexEnums;
import com.rnkrsoft.platform.enums.SexEnums2;
import lombok.Data;

import javax.web.doc.annotation.ApidocElement;
import java.io.Serializable;

/**
 * Created by rnkrsoft.com on 2018/7/11.
 */
@Data
public class Record3VO implements Serializable{
    @ApidocElement(value = "大小", required = true)
    int size;


    @ApidocElement(value = "性别", enumClass = SexEnums.class, defaults = "1")
    String sex;

    @ApidocElement(value = "性别1", enumClass = SexEnums2.class, defaults = "2")
    int sex1;
}
