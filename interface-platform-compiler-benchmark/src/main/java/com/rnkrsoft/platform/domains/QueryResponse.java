package com.rnkrsoft.platform.domains;

import lombok.Data;

import javax.web.doc.AbstractResponsePage;
import java.io.Serializable;

/**
 * Created by rnkrsoft.com on 2018/7/2.
 */
@Data
public class QueryResponse extends AbstractResponsePage<RecordVO> implements Serializable {
}
