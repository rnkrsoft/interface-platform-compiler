package com.rnkrsoft.platform.compiler.swift;

import com.rnkrsoft.platform.compiler.CompileContext;
import com.rnkrsoft.platform.compiler.InterfacePlatformCompiler;
import com.rnkrsoft.platform.compiler.InterfaceScanner;
import com.rnkrsoft.platform.compiler.java.JavaInterfacePlatformCompiler;
import com.rnkrsoft.utils.StringUtils;
import org.junit.Test;

import javax.web.doc.DocScanner;

import static org.junit.Assert.*;

/**
 * Created by wing4j on 2019/2/10.
 */
public class SwiftInterfacePlatformCompilerTest {

    @Test
    public void testCompile() throws Exception {
        DocScanner docScanner = InterfaceScanner.scan("com.rnkrsoft.platform.service");
        InterfacePlatformCompiler compiler = new SwiftInterfacePlatformCompiler();
        CompileContext ctx = new CompileContext();
        ctx.setDocScanner(docScanner);
        ctx.setOutputPath("./target");
        ctx.setOutputFileName("swift");
        ctx.setTargetPackage(StringUtils.shortPackageName("com.rnkrsoft.demo", 3, true, ""));
        ctx.setServiceName("service");
        ctx.setDomainsName("domains");
//        ctx.setBasePackage("com.rnkrsoft");
//        ctx.setBaseFilePath("");
        ctx.setPackZip(true);
        compiler.compile(ctx);
        System.out.println(ctx.getFileFormats());
    }
}