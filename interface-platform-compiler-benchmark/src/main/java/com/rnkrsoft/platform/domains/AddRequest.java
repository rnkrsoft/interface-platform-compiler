package com.rnkrsoft.platform.domains;

import com.rnkrsoft.platform.enums.SexEnums;
import com.rnkrsoft.platform.enums.SexEnums2;
import com.rnkrsoft.platform.protocol.TokenAble;
import lombok.Data;

import javax.web.doc.annotation.ApidocElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rnkrsoft.com on 2018/7/2.
 */
@Data
public class AddRequest implements Serializable, TokenAble{
    @ApidocElement(value = "姓名", required = true, minLen = 10, maxLen = 20)
    String name;
    @ApidocElement(value = "年龄", required = false, minLen = 5)
    Integer age;
    @ApidocElement(value = "大小", required = true, maxLen = 5)
    int size;

    @ApidocElement(value = "开关")
    Boolean enable;

    @ApidocElement(value = "测试开饭馆")
    boolean enable1;

    @ApidocElement(value = "xxxx")
    long age1;

    @ApidocElement(value = "性别", enumClass = SexEnums.class, defaults = "1")
    String sex;

    @ApidocElement(value = "性别1", enumClass = SexEnums2.class, defaults = "2")
    int sex1;

    @ApidocElement(value = "性别2", enumClass = SexEnums2.class, defaults = "2")
    final List<Integer> sex2 = new ArrayList();


    @ApidocElement(value = "列表")
    List<RecordVO> list1;

    @ApidocElement(value = "测试")
    RecordVO recordV11;

    @ApidocElement(value = "绘画令牌")
    String token;
}
