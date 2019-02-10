package com.rnkrsoft.platform.compiler;

import com.rnkrsoft.platform.compiler.swift.SwiftInterfacePlatformCompiler;

/**
 * Created by rnkrsoft.com on 2019/2/10.
 */
public class InterfacePlatformCompilerFactory {
    public static InterfacePlatformCompiler getInstance(String deviceType){
        InterfacePlatformCompiler instance = new SwiftInterfacePlatformCompiler();
        return instance;
    }
}
