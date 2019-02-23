package com.rnkrsoft.platform.compiler.swift;

import com.rnkrsoft.platform.compiler.CompileContext;
import com.rnkrsoft.platform.compiler.InterfacePlatformCompiler;
import com.rnkrsoft.platform.compiler.InterfaceScanner;
import com.rnkrsoft.platform.generator.DeviceType;
import com.rnkrsoft.platform.generator.InterfacePlatformGenerator;
import org.junit.Test;

import javax.web.doc.DocScanner;


/**
 * Created by rnkrsoft.com on 2019/2/10.
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
        ctx.setTargetPackage("com.rnkrsoft.demo");
        ctx.setServiceName("service");
        ctx.setDomainsName("domains");
        ctx.setPackZip(true);
        ctx.setShortName(true);
        compiler.compile(ctx);
        System.out.println(ctx.getFileFormats());
    }

    @Test
    public void testCompile1() throws Exception {
        String[] serviceClasses = new String[]{
                "com.rnkrsoft.platform.service.DemoService",
        };
        InterfacePlatformGenerator.generate(DeviceType.Android, "com.rnkrsoft.demo", "./target", "com.rnkrsoft.platform.service", serviceClasses);

    }
}