package com.rnkrsoft.platform.compiler.android;

import com.rnkrsoft.platform.compiler.CompileContext;
import com.rnkrsoft.platform.compiler.InterfacePlatformCompiler;
import com.rnkrsoft.platform.compiler.InterfaceScanner;
import org.junit.Test;

import javax.web.doc.DocScanner;

import static org.junit.Assert.*;

/**
 * Created by wing4j on 2019/2/3.
 */
public class AndroidInterfacePlatformCompilerTest {

    @Test
    public void testCompile() throws Exception {
        DocScanner docScanner = InterfaceScanner.scan("com.rnkrsoft.platform.service");
        InterfacePlatformCompiler compiler = new AndroidInterfacePlatformCompiler();
        CompileContext ctx = new CompileContext();
        ctx.setDocScanner(docScanner);
        ctx.setOutputPath("./target");
        ctx.setOutputFileName("demo");
//        ctx.setBasePackage("com.rnkrsoft.platform");
        ctx.setServiceName("service");
        ctx.setDomainsName("domains");
//        ctx.setBasePackage("com.rnkrsoft");
//        ctx.setBaseFilePath("");
        ctx.setPackZip(true);
        compiler.compile(ctx);
        System.out.println(ctx.getFileFormats());
    }
}