package com.rnkrsoft.platform.generator;

import com.rnkrsoft.platform.compiler.CompileContext;
import com.rnkrsoft.platform.compiler.InterfacePlatformCompiler;
import com.rnkrsoft.platform.compiler.InterfacePlatformCompilerFactory;
import com.rnkrsoft.platform.compiler.InterfaceScanner;

import javax.web.doc.DocScanner;
import java.io.OutputStream;
import java.util.Arrays;
@Deprecated
public abstract class InterfacePlatformGenerator {
    private InterfacePlatformGenerator() {
    }

    @Deprecated
    public static void generate(DeviceType deviceType, String outputPackage, String outputPath, String basePackage, String... serviceClassNames) throws Exception {
        generate(null, deviceType, outputPackage, outputPath, basePackage, serviceClassNames);
    }
    public static void generate(String copyright, DeviceType deviceType, String outputPackage, String outputPath, String basePackage, String... serviceClassNames) throws Exception {
        DocScanner docScanner = InterfaceScanner.scan(basePackage);
        String type = deviceType.getCode();
        if (deviceType == DeviceType.iOS){
            type = "Swift";
        }
        InterfacePlatformCompiler compiler = InterfacePlatformCompilerFactory.getInstance(type);
        CompileContext ctx = new CompileContext();
        ctx.setCopyright(copyright);
        ctx.setDocScanner(docScanner);
        ctx.setOutputPath(outputPath);
        ctx.setOutputFileName(type);
        ctx.setTargetPackage(outputPackage);
        ctx.setServiceName(deviceType == DeviceType.iOS ? "service" :"facades");
        ctx.setDomainsName("domains");
        ctx.setPackZip(true);
        ctx.setShortName(deviceType == DeviceType.iOS);
        ctx.getIncludeServices().addAll(Arrays.asList(serviceClassNames));
        compiler.compile(ctx);
    }

    @Deprecated
    public static void generate(DeviceType deviceType, String outputPackage, OutputStream os, String basePackage, String... serviceClassNames) throws Exception {

    }
}
