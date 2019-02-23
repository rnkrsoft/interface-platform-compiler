package com.rnkrsoft.platform.compiler;


import com.rnkrsoft.logtrace4j.ErrorContext;
import com.rnkrsoft.logtrace4j.ErrorContextFactory;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created by rnkrsoft.com on 2019/2/10.
 */
public class InterfacePlatformCompilerFactory {
    public static InterfacePlatformCompiler getInstance(String type) {
        InterfacePlatformCompiler compiler = null;
        ServiceLoader<InterfacePlatformCompiler> serviceLoader = ServiceLoader.load(InterfacePlatformCompiler.class);
        Iterator<InterfacePlatformCompiler> compilerIterator = serviceLoader.iterator();
        while (compiler == null && compilerIterator.hasNext()) {
            InterfacePlatformCompiler compiler1 = compilerIterator.next();
            if (type != null && !type.isEmpty()) {
                if (type.equalsIgnoreCase(compiler1.getType())) {
                    compiler = compiler1;
                }
            } else {
                compiler = compiler1;
            }
        }
        if (compiler == null) {
            ErrorContext errorContext = ErrorContextFactory.instance().reset();
            errorContext.message("未发现支持'{}'的编译器", type)
                    .solution("在META-INF/services/{}文件中定义实现类", InterfacePlatformCompiler.class.getName());
            Iterator<InterfacePlatformCompiler> it = serviceLoader.iterator();
            int i = 0;
            while (it.hasNext()) {
                i++;
                InterfacePlatformCompiler compiler1 = it.next();
                errorContext.extra("found" + i, "实现[{}] {}", i, compiler1.getClass().getName());
            }
            throw errorContext.runtimeException();
        }
        return compiler;
    }
}
