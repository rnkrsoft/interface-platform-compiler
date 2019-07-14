package com.rnkrsoft.platform.compiler;

import javax.web.doc.DocScanner;
import java.util.Arrays;

/**
 * 接口平台编译器命令行
 */
public abstract class InterfacePlatformCompilerCli {
    private InterfacePlatformCompilerCli() {
    }

    public static void generate(DeviceType deviceType, String outputPackage, String outputPath, String basePackage, String... serviceClassNames) throws Exception {
        generate(null, deviceType, outputPackage, outputPath, basePackage, serviceClassNames);
    }

    public static void generate(String copyright, DeviceType deviceType, String outputPackage, String outputPath, String basePackage, String... serviceClassNames) throws Exception {
        DocScanner docScanner = InterfaceScanner.scan(basePackage);
        String type = deviceType.getCode();
        InterfacePlatformCompiler compiler = InterfacePlatformCompilerFactory.getInstance(type);
        CompileContext ctx = new CompileContext();
        ctx.setCopyright(copyright);
        ctx.setDocScanner(docScanner);
        ctx.setOutputPath(outputPath);
        ctx.setOutputFileName(type);
        ctx.setTargetPackage(outputPackage);
        ctx.setServiceName(deviceType == DeviceType.Swift ? "service" : "facades");
        ctx.setDomainsName("domains");
        ctx.setPackZip(true);
        ctx.setShortName(deviceType == DeviceType.Swift);
        ctx.getIncludeServices().addAll(Arrays.asList(serviceClassNames));
        compiler.compile(ctx);
    }
}
