package com.rnkrsoft.platform.domains;

import lombok.Data;

import javax.web.doc.annotation.ApidocElement;
import java.io.Serializable;

/**
 * Created by rnkrsoft.com on 2018/7/2.
 */
@Data
public class DeleteRequest implements Serializable {
    @ApidocElement(value = "流水号", required = true)
    String serialNo;
}
