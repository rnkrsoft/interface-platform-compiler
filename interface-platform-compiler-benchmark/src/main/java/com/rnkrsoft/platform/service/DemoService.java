package com.rnkrsoft.platform.service;

import com.rnkrsoft.platform.domains.*;

import javax.web.doc.annotation.ApidocInterface;
import javax.web.doc.annotation.ApidocService;

/**
 * Created by rnkrsoft.com on 2018/7/2.
 * com_rnkrsoft_platform_service_DemoService
 */
@ApidocService(value = "演示服务", channel = "user_app")
public interface DemoService {
    @ApidocInterface(value = "新增", name = "101", version = "1")
    AddResponse add(AddRequest request);
    @ApidocInterface(value = "删除", name = "102")
    DeleteResponse delete(DeleteRequest request);
    @ApidocInterface(value = "查询", name = "103")
    QueryResponse query(QueryRequest request);
}
