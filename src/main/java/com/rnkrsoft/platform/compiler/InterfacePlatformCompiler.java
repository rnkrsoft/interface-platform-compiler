package com.rnkrsoft.platform.compiler;

import java.io.IOException;

/**
 * Created by rnkrsoft.com on 2019/2/2.
 */
public interface InterfacePlatformCompiler {
    /**
     * 进行接口编译
     * @param ctx 上下文
     */
    void compile(CompileContext ctx) throws IOException;
}
