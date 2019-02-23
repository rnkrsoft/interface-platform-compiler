package com.rnkrsoft.platform.domains;

import lombok.Data;

import javax.web.doc.AbstractResponse;
import javax.web.doc.annotation.ApidocElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rnkrsoft.com on 2018/7/2.
 */
@Data
public class AddResponse extends AbstractResponse implements Serializable {
    @ApidocElement(value = "流水号", required = true)
    String serialNo;
    @ApidocElement(value = "记录", required = true)
    final List<Record1VO> records = new ArrayList();
}
