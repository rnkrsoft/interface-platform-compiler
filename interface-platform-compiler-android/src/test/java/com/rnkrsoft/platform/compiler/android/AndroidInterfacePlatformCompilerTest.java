package com.rnkrsoft.platform.compiler.android;

import com.rnkrsoft.platform.compiler.CompileContext;
import com.rnkrsoft.platform.compiler.InterfacePlatformCompiler;
import com.rnkrsoft.platform.compiler.InterfaceScanner;
import com.rnkrsoft.platform.generator.DeviceType;
import com.rnkrsoft.platform.generator.InterfacePlatformGenerator;
import org.junit.Test;

import javax.web.doc.DocScanner;

import static org.junit.Assert.*;

/**
 * Created by rnkrsoft.com on 2019/2/3.
 */
public class AndroidInterfacePlatformCompilerTest {

    @Test
    public void testCompile() throws Exception {
        DocScanner docScanner = InterfaceScanner.scan("com.rnkrsoft.platform.service");
        InterfacePlatformCompiler compiler = new AndroidInterfacePlatformCompiler();
        CompileContext ctx = new CompileContext();
        ctx.setDocScanner(docScanner);
        ctx.setOutputPath("./target");
        ctx.setOutputFileName("android");
        ctx.setTargetPackage("com.rnkrsoft.demo");
        ctx.setServiceName("facades");
        ctx.setDomainsName("domains");
        ctx.setPackZip(true);
        compiler.compile(ctx);
        System.out.println(ctx.getFileFormats());
    }
    @Test
    public void testCompile1() throws Exception {
        String[] serviceClasses = new String[]{};
        InterfacePlatformGenerator.generate(DeviceType.Android, "com.rnkrsoft.demo", "./target", "com.rnkrsoft.platform.service", serviceClasses);

    }
}