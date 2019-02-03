package com.rnkrsoft.facades;

import javax.web.doc.annotation.ApidocService;
import javax.web.doc.annotation.ApidocInterface;
import com.rnkrsoft.platform.protocol.AsyncHandler;
import android.os.AsyncTask;

import com.rnkrsoft.domains.*;

/**
 * Created by interface platform generator 
 */
@ApidocService(value = "演示服务", channel = "user_app" )
public interface DemoService {
    @ApidocInterface(value = "新增", name = "101", version = "1", usage = "")
    AsyncTask add(AddRequest request, AsyncHandler<AddResponse> asyncHandler);

    @ApidocInterface(value = "删除", name = "102", version = "1.0.0", usage = "")
    AsyncTask delete(DeleteRequest request, AsyncHandler<DeleteResponse> asyncHandler);

    @ApidocInterface(value = "查询", name = "103", version = "1.0.0", usage = "")
    AsyncTask query(QueryRequest request, AsyncHandler<QueryResponse> asyncHandler);

}