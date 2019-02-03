package com.rnkrsoft.platform.domains;

import com.rnkrsoft.platform.enums.SexEnums;
import com.rnkrsoft.platform.enums.SexEnums2;
import lombok.Data;

import javax.web.doc.AbstractRequestPage;
import javax.web.doc.annotation.ApidocElement;
import java.io.Serializable;

/**
 * Created by rnkrsoft.com on 2018/7/2.
 */
@Data
public class QueryRequest extends AbstractRequestPage implements Serializable {
    @ApidocElement(value = "姓名", required = true)
    String name;
    @ApidocElement(value = "年龄", required = false)
    Integer age;
    @ApidocElement(value = "大小", required = true)
    int size;

    @ApidocElement(value = "性别", enumClass = SexEnums.class, defaults = "1")
    String sex;

    @ApidocElement(value = "性别1", enumClass = SexEnums2.class, defaults = "2")
    int sex1;
}
