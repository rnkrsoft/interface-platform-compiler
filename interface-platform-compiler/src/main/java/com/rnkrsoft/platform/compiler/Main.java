package com.rnkrsoft.platform.compiler;

import com.rnkrsoft.utils.StringUtils;

import javax.web.doc.DocScanner;
import java.io.IOException;

/**
 * Created by rnkrsoft.com on 2019/2/10.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        DocScanner docScanner = InterfaceScanner.scan("com.rnkrsoft.platform.service");
        InterfacePlatformCompiler compiler = InterfacePlatformCompilerFactory.getInstance("swift");
        CompileContext ctx = new CompileContext();
        ctx.setDocScanner(docScanner);
        ctx.setOutputPath("./target");
        ctx.setOutputFileName("swift");
        ctx.setTargetPackage(StringUtils.shortPackageName("com.rnkrsoft.demo", 3, true, ""));
        ctx.setServiceName("service");
        ctx.setDomainsName("domains");
        ctx.setPackZip(true);
        compiler.compile(ctx);
    }
}
