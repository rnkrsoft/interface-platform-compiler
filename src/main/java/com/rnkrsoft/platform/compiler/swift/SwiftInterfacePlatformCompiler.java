package com.rnkrsoft.platform.compiler.swift;

import com.rnkrsoft.io.buffer.ByteBuf;
import com.rnkrsoft.platform.compiler.CompileContext;
import com.rnkrsoft.platform.compiler.InterfaceFileFormat;
import com.rnkrsoft.platform.compiler.InterfacePlatformCompiler;
import com.rnkrsoft.platform.format.ZipInterfaceFormat;
import com.rnkrsoft.utils.StringUtils;
import org.apache.commons.io.IOUtils;

import javax.web.doc.*;
import java.io.*;
import java.util.List;
import java.util.Set;

/**
 * Created by rnkrsoft.com on 2019/2/2.
 */
public class SwiftInterfacePlatformCompiler implements InterfacePlatformCompiler {
    public void compile(CompileContext context) throws IOException {
        DocScanner docScanner = context.getDocScanner();
        try {
            generate(context, docScanner.listService());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (context.isPackZip()) {
            OutputStream os = null;
            try {
                File file = new File(context.getOutputPath(), context.getOutputFileName() + ".zip");
                if (file.exists()) {
                    file.delete();
                }
                os = new FileOutputStream(file);
                ZipInterfaceFormat zipInterfaceFormat = new ZipInterfaceFormat(os);
                for (InterfaceFileFormat format : context.getFileFormats()) {
                    zipInterfaceFormat.add((format.getFilePath().startsWith("/") ? format.getFilePath().substring(1) : format.getFilePath()) + "/" + format.getFileName() + "." + format.getFileSuffix(), format.getCode());
                }
                zipInterfaceFormat.save();
            } finally {
                IOUtils.closeQuietly(os);
            }
        }
    }

    void generate(CompileContext context, List<ServiceInfo> services) throws FileNotFoundException {
        for (ServiceInfo serviceInfo : services) {
            generateServiceClass(context, serviceInfo);
            Set<InterfaceInfo> interfaces = serviceInfo.getInterfaces();
            for (InterfaceInfo interfaceInfo : interfaces) {
                generateRequestClass(context, interfaceInfo);
                generateResponseClass(context, interfaceInfo);
            }
        }
    }

    void generateServiceClass(CompileContext context, ServiceInfo serviceInfo) throws FileNotFoundException {
        ByteBuf buf = ByteBuf.allocate(1024).autoExpand(true);
        String className = serviceInfo.getServiceClass().getName();
        int lastDotIdx = className.lastIndexOf(".");
        String packageName = className.substring(0, lastDotIdx);
        int lastDotIdx1 = packageName.lastIndexOf(".");
        String targetPackage = packageName.substring(0, lastDotIdx1);
        if (context.getTargetPackage() == null) {
            context.setTargetPackage(targetPackage);
        }
        buf.put("UTF-8", "import Foundation", "\n");
        buf.put("UTF-8", "import InterfacePlatformClientSDK", "\n");
        buf.put("UTF-8", "\n");
        buf.put("UTF-8", "/**", "\n");
        buf.put("UTF-8", " *  Created by interface platform generator", "\n");
        buf.put("UTF-8", " *  ", serviceInfo.getDesc(), "\n");
        buf.put("UTF-8", " */", "\n");
        buf.put("UTF-8", "class ", context.getServicePackage(), serviceInfo.getServiceClass().getSimpleName(), " {", "\n");
        for (InterfaceInfo interfaceInfo : serviceInfo.getInterfaces()) {
            buf.put("UTF-8", "    ", "///", interfaceInfo.getDesc(), " ", interfaceInfo.getUsage(), "\n");
            buf.put("UTF-8", "    ", "let ", interfaceInfo.getMethodName(),
                    " = ApiModel<", context.getDomainsPackage(), interfaceInfo.getRequestClass().getSimpleName(), ", ", context.getDomainsPackage(), interfaceInfo.getResponseClass().getSimpleName(), ">",
                    "(",
                    " channel : \"", serviceInfo.getChannel(), "\",",
                    " name : \"", interfaceInfo.getName(), "\",",
                    " version : \"", interfaceInfo.getVersion(), "\",",
                    " value : \"", interfaceInfo.getDesc(), "\")",
                    "\n");
            buf.put("UTF-8", "\n");
        }
        buf.put("UTF-8", "}");
        InterfaceFileFormat fileFormat = new InterfaceFileFormat();
        fileFormat.setFilePath(context.getServiceFilePath());
        fileFormat.setPackagePath(context.getServicePackage());
        fileFormat.setCode(buf.asString("UTF-8"));
        fileFormat.setFileSuffix("swift");
        fileFormat.setFileName(serviceInfo.getServiceClass().getSimpleName());
        context.addInterfaceFile(fileFormat);
    }
    void generateRequestClass(CompileContext context, InterfaceInfo interfaceInfo) throws FileNotFoundException {
        ByteBuf buf = ByteBuf.allocate(1024).autoExpand(true);
        buf.put("UTF-8", "import Foundation", "\n");
        buf.put("UTF-8", "import InterfacePlatformClientSDK", "\n");
        buf.put("UTF-8", "\n");
        buf.put("UTF-8", "/**", "\n");
        buf.put("UTF-8", " *  Created by interface platform generator", "\n");
        buf.put("UTF-8", " *  ", interfaceInfo.getDesc(), "\n");
        buf.put("UTF-8", " */", "\n");
        buf.put("UTF-8", "struct ", context.getDomainsPackage() + interfaceInfo.getRequestClass().getSimpleName(), " : JSONable {", "\n");
        for (ElementInfo column : interfaceInfo.getRequest().getAllElements()) {

            if (column.isValue()) {
                ValueElementInfo valueElementInfo = column.as(ValueElementInfo.class);
                buf.put("UTF-8", "    ///", valueElementInfo.isRequired() ? "必输 ": "", valueElementInfo.getDesc(), " ", StringUtils.safeToString(valueElementInfo.getUsage(), "") , " ","\n");
                if (valueElementInfo.isEnum()){
                    buf.put("UTF-8", "    ///", valueElementInfo.getEnums().toString(),"\n");
                }
                if (valueElementInfo.getJavaClass() == Integer.TYPE || valueElementInfo.getJavaClass() == Integer.class) {
                    if (valueElementInfo.isMultiple()) {
                        buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": [Int]?", "\n");
                    } else {
                        buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": Int", valueElementInfo.isRequired() ? " = 0" : "?", "\n");
                    }
                } else if (valueElementInfo.getJavaClass() == String.class) {
                    if (valueElementInfo.isMultiple()) {
                        buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ":  [String]?", "\n");
                    } else {
                        buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": String", valueElementInfo.isRequired() ? " = \"\"" : "?", "\n");
                    }
                } else if (valueElementInfo.getJavaClass() == Boolean.TYPE || valueElementInfo.getJavaClass() == Boolean.class) {
                    buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": Bool", valueElementInfo.isRequired() ? (valueElementInfo.getDefaults().isEmpty() ? " = true" : "=" + valueElementInfo.getDefaults().get(0)) : "?", "\n");
                } else if (valueElementInfo.getJavaClass() == Integer.TYPE || valueElementInfo.getJavaClass() == Integer.class) {
                    buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": Int", valueElementInfo.isRequired() ? (valueElementInfo.getDefaults().isEmpty() ? " = 0" : "=" + valueElementInfo.getDefaults().get(0)) : "?", "\n");
                } else if (valueElementInfo.getJavaClass() == Long.TYPE || valueElementInfo.getJavaClass() == Long.class) {
                    buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": Int64", valueElementInfo.isRequired() ? (valueElementInfo.getDefaults().isEmpty() ? " = 0" : "=" + valueElementInfo.getDefaults().get(0)) : "?", "\n");
                } else {
                    buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": ", (valueElementInfo.getDefaults().isEmpty() ? " " : "=" + valueElementInfo.getDefaults().get(0)), "?", "\n");
                }
            } else if (column.isBean()) {
                BeanElementInfo beanElementInfo = column.as(BeanElementInfo.class);
                buf.put("UTF-8", "    ///", beanElementInfo.getDesc(), " ", beanElementInfo.getUsage() ,"\n");
                buf.put("UTF-8", "    ", "var ", beanElementInfo.getName(), ": ", context.getDomainsPackage(), beanElementInfo.getJavaClass().getSimpleName(), " ?", "\n");
            } else if (column.isForm()) {
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                buf.put("UTF-8", "    ///", formElementInfo.getDesc(), " ", formElementInfo.getUsage() ,"\n");
                buf.put("UTF-8", "    ", "var ", column.getName(), ": [", context.getDomainsPackage(), formElementInfo.getBeanClass().getSimpleName(), "]?", "\n");
            }
            buf.put("UTF-8", "\n");
        }
        buf.put("UTF-8", "}");
        InterfaceFileFormat fileFormat = new InterfaceFileFormat();
        fileFormat.setFilePath(context.getDomainsFilePath());
        fileFormat.setPackagePath(context.getDomainsPackage());
        fileFormat.setCode(buf.asString("UTF-8"));
        fileFormat.setFileSuffix("swift");
        fileFormat.setFileName(context.getDomainsPackage() + interfaceInfo.getRequestClass().getSimpleName());
        context.addInterfaceFile(fileFormat);
        for (ElementInfo elementInfo : interfaceInfo.getRequest().getAllElements()) {
            if (elementInfo.isBean()) {
                generateValueObjectClass(context, elementInfo.as(BeanElementInfo.class), false);
            }
            if (elementInfo.isForm()) {
                generateFormObjectClass(context, elementInfo.as(FormElementInfo.class), false);
            }
        }
    }

    void generateConstantsClass(CompileContext context, ValueElementInfo column) {

    }

    void generateValueObjectClass(CompileContext context, BeanElementInfo elementInfo, boolean response) throws FileNotFoundException {
        ByteBuf buf = ByteBuf.allocate(1024).autoExpand(true);
        buf.put("UTF-8", "import Foundation", "\n");
        buf.put("UTF-8", "import InterfacePlatformClientSDK", "\n");
        buf.put("UTF-8", "\n");
        buf.put("UTF-8", "/**", "\n");
        buf.put("UTF-8", " *  Created by interface platform generator", "\n");
        buf.put("UTF-8", " *  ", elementInfo.getDesc(), "\n");
        buf.put("UTF-8", " */", "\n");
        buf.put("UTF-8", "class ", context.getDomainsPackage() + elementInfo.getJavaClass().getSimpleName(), " : NSObject, JSONable {", "\n");
        for (ElementInfo column : elementInfo.getElements()) {
            if (column.isValue()) {
                ValueElementInfo valueElementInfo = column.as(ValueElementInfo.class);
                buf.put("UTF-8", "    ///", valueElementInfo.isRequired() ? "必输 ": "", valueElementInfo.getDesc(), " ", StringUtils.safeToString(valueElementInfo.getUsage(), "") , " ","\n");
                if (valueElementInfo.isEnum()){
                    buf.put("UTF-8", "    ///", valueElementInfo.getEnums().toString(),"\n");
                }
                if (valueElementInfo.getJavaClass() == Integer.TYPE || valueElementInfo.getJavaClass() == Integer.class) {
                    if (valueElementInfo.isMultiple()) {
                        buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": [Int]?", "\n");
                    } else {
                        buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": Int", valueElementInfo.isRequired() ? " = 0" : "?", "\n");
                    }
                } else if (valueElementInfo.getJavaClass() == String.class) {
                    if (valueElementInfo.isMultiple()) {
                        buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ":  [String]?", "\n");
                    } else {
                        buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": String", valueElementInfo.isRequired() ? " = \"\"" : "?", "\n");
                    }
                } else if (valueElementInfo.getJavaClass() == Boolean.TYPE || valueElementInfo.getJavaClass() == Boolean.class) {
                    buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": Bool", valueElementInfo.isRequired() ? (valueElementInfo.getDefaults().isEmpty() ? " = true" : "=" + valueElementInfo.getDefaults().get(0)) : "?", "\n");
                } else if (valueElementInfo.getJavaClass() == Integer.TYPE || valueElementInfo.getJavaClass() == Integer.class) {
                    buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": Int", valueElementInfo.isRequired() ? (valueElementInfo.getDefaults().isEmpty() ? " = 0" : "=" + valueElementInfo.getDefaults().get(0)) : "?", "\n");
                } else if (valueElementInfo.getJavaClass() == Long.TYPE || valueElementInfo.getJavaClass() == Long.class) {
                    buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": Int64", valueElementInfo.isRequired() ? (valueElementInfo.getDefaults().isEmpty() ? " = 0" : "=" + valueElementInfo.getDefaults().get(0)) : "?", "\n");
                } else {
                    buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": ", (valueElementInfo.getDefaults().isEmpty() ? " " : "=" + valueElementInfo.getDefaults().get(0)), "?", "\n");
                }
            } else if (column.isBean()) {
                BeanElementInfo beanElementInfo = column.as(BeanElementInfo.class);
                buf.put("UTF-8", "    ///", beanElementInfo.getDesc(), " ", beanElementInfo.getUsage() ,"\n");
                buf.put("UTF-8", "    ", "var ", beanElementInfo.getName(), ": ", context.getDomainsPackage(), beanElementInfo.getJavaClass().getSimpleName(), " ?", "\n");
            } else if (column.isForm()) {
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                buf.put("UTF-8", "    ///", formElementInfo.getDesc(), " ", formElementInfo.getUsage() ,"\n");
                buf.put("UTF-8", "    ", "var ", column.getName(), ": [", context.getDomainsPackage(), formElementInfo.getBeanClass().getSimpleName(), "]?", "\n");
            }
            buf.put("UTF-8", "\n");
        }
        if (response) {
            buf.put("UTF-8", "    required override init() {", "\n");
            buf.put("UTF-8", "    }", "\n");
            buf.put("UTF-8", "\n");
        }
        buf.put("UTF-8", " }");
        InterfaceFileFormat fileFormat = new InterfaceFileFormat();
        fileFormat.setFilePath(context.getDomainsFilePath());
        fileFormat.setPackagePath(context.getDomainsPackage());
        fileFormat.setCode(buf.asString("UTF-8"));
        fileFormat.setFileSuffix("swift");
        fileFormat.setFileName(context.getDomainsPackage() + elementInfo.getJavaClass().getSimpleName());
        context.addInterfaceFile(fileFormat);
        for (ElementInfo elementInfo1 : elementInfo.getElements()) {
            if (elementInfo1.isBean()) {
                generateValueObjectClass(context, elementInfo1.as(BeanElementInfo.class), response);
            }
            if (elementInfo1.isForm()) {
                generateFormObjectClass(context, elementInfo1.as(FormElementInfo.class), response);
            }
        }
    }

    void generateFormObjectClass(CompileContext context, FormElementInfo elementInfo, boolean response) throws FileNotFoundException {
        ByteBuf buf = ByteBuf.allocate(1024).autoExpand(true);
        buf.put("UTF-8", "import Foundation", "\n");
        buf.put("UTF-8", "import InterfacePlatformClientSDK", "\n");
        buf.put("UTF-8", "\n");
        buf.put("UTF-8", "/**", "\n");
        buf.put("UTF-8", " *  Created by interface platform generator", "\n");
        buf.put("UTF-8", " *  ", elementInfo.getDesc(), "\n");
        buf.put("UTF-8", " */", "\n");
        buf.put("UTF-8", "class ", context.getDomainsPackage() + elementInfo.getBeanClass().getSimpleName(), " : NSObject, JSONable {", "\n");
        for (ElementInfo column : elementInfo.getElements()) {
            if (column.isValue()) {
                ValueElementInfo valueElementInfo = column.as(ValueElementInfo.class);
                buf.put("UTF-8", "    ///", valueElementInfo.isRequired() ? "必输 ": "", valueElementInfo.getDesc(), " ", StringUtils.safeToString(valueElementInfo.getUsage(), "") , " ","\n");
                if (valueElementInfo.isEnum()){
                    buf.put("UTF-8", "    ///", valueElementInfo.getEnums().toString(),"\n");
                }
                if (valueElementInfo.getJavaClass() == Integer.TYPE || valueElementInfo.getJavaClass() == Integer.class) {
                    if (valueElementInfo.isMultiple()) {
                        buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": [Int]?", "\n");
                    } else {
                        buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": Int", valueElementInfo.isRequired() ? " = 0" : " ?", "\n");
                    }
                } else if (valueElementInfo.getJavaClass() == String.class) {
                    if (valueElementInfo.isMultiple()) {
                        buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ":  [String]?", "\n");
                    } else {
                        buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": String", valueElementInfo.isRequired() ? " = \"\"" : "?", "\n");
                    }
                } else if (valueElementInfo.getJavaClass() == Boolean.TYPE || valueElementInfo.getJavaClass() == Boolean.class) {
                    buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": Bool", valueElementInfo.isRequired() ? (valueElementInfo.getDefaults().isEmpty() ? " = true" : "=" + valueElementInfo.getDefaults().get(0)) : "?", "\n");
                } else if (valueElementInfo.getJavaClass() == Integer.TYPE || valueElementInfo.getJavaClass() == Integer.class) {
                    buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": Int", valueElementInfo.isRequired() ? (valueElementInfo.getDefaults().isEmpty() ? " = 0" : "=" + valueElementInfo.getDefaults().get(0)) : "?", "\n");
                } else if (valueElementInfo.getJavaClass() == Long.TYPE || valueElementInfo.getJavaClass() == Long.class) {
                    buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": Int64", valueElementInfo.isRequired() ? (valueElementInfo.getDefaults().isEmpty() ? " = 0" : "=" + valueElementInfo.getDefaults().get(0)) : "?", "\n");
                } else {
                    buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": ", (valueElementInfo.getDefaults().isEmpty() ? " " : "=" + valueElementInfo.getDefaults().get(0)), "?", "\n");
                }
            } else if (column.isBean()) {
                BeanElementInfo beanElementInfo = column.as(BeanElementInfo.class);
                buf.put("UTF-8", "    ///", beanElementInfo.getDesc(), " ", beanElementInfo.getUsage() ,"\n");
                buf.put("UTF-8", "    ", "var ", beanElementInfo.getName(), ": ", context.getDomainsPackage(), beanElementInfo.getJavaClass().getSimpleName(), " ?", "\n");
            } else if (column.isForm()) {
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                buf.put("UTF-8", "    ///", formElementInfo.getDesc(), " ", formElementInfo.getUsage() ,"\n");
                buf.put("UTF-8", "    ", "var ", column.getName(), ": [", context.getDomainsPackage(), formElementInfo.getBeanClass().getSimpleName(), "]?", "\n");
            }
            buf.put("UTF-8", "\n");
        }
        if (response) {
            buf.put("UTF-8", "    required override init() {", "\n");
            buf.put("UTF-8", "    }", "\n");
            buf.put("UTF-8", "\n");
        }
        buf.put("UTF-8", " }");
        InterfaceFileFormat fileFormat = new InterfaceFileFormat();
        fileFormat.setFilePath(context.getDomainsFilePath());
        fileFormat.setPackagePath(context.getDomainsPackage());
        fileFormat.setCode(buf.asString("UTF-8"));
        fileFormat.setFileSuffix("swift");
        fileFormat.setFileName(context.getDomainsPackage() + elementInfo.getBeanClass().getSimpleName());
        context.addInterfaceFile(fileFormat);
        for (ElementInfo elementInfo1 : elementInfo.getElements()) {
            if (elementInfo1.isBean()) {
                generateValueObjectClass(context, elementInfo1.as(BeanElementInfo.class), response);
            }
            if (elementInfo1.isForm()) {
                generateFormObjectClass(context, elementInfo1.as(FormElementInfo.class), response);
            }
        }
    }

    void generateResponseClass(CompileContext context, InterfaceInfo interfaceInfo) throws FileNotFoundException {
        ByteBuf buf = ByteBuf.allocate(1024).autoExpand(true);
        buf.put("UTF-8", "import Foundation", "\n");
        buf.put("UTF-8", "import InterfacePlatformClientSDK", "\n");
        buf.put("UTF-8", "\n");
        buf.put("UTF-8", "/**", "\n");
        buf.put("UTF-8", " *  Created by interface platform generator", "\n");
        buf.put("UTF-8", " *  ", interfaceInfo.getDesc(), "\n");
        buf.put("UTF-8", " */", "\n");
        buf.put("UTF-8", "class ", context.getDomainsPackage() + interfaceInfo.getResponseClass().getSimpleName(), " : AbstractResponse {", "\n");
        for (ElementInfo column : interfaceInfo.getResponse().getAllElements()) {
            if (column.isValue()) {
                ValueElementInfo valueElementInfo = column.as(ValueElementInfo.class);
                buf.put("UTF-8", "    ///", valueElementInfo.isRequired() ? "必输 ": "", valueElementInfo.getDesc(), " ", StringUtils.safeToString(valueElementInfo.getUsage(), "") , " ","\n");
                if (valueElementInfo.isEnum()){
                    buf.put("UTF-8", "    ///", valueElementInfo.getEnums().toString(),"\n");
                }
                if (valueElementInfo.getJavaClass() == Integer.TYPE || valueElementInfo.getJavaClass() == Integer.class) {
                    if (valueElementInfo.isMultiple()) {
                        buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": [Int]?", "\n");
                    } else {
                        buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": Int", valueElementInfo.isRequired() ? " = 0" : " ?", "\n");
                    }
                } else if (valueElementInfo.getJavaClass() == String.class) {
                    if (valueElementInfo.isMultiple()) {
                        buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ":  [String]?", "\n");
                    } else {
                        buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": String", valueElementInfo.isRequired() ? " = \"\"" : "?", "\n");
                    }
                } else if (valueElementInfo.getJavaClass() == Boolean.TYPE || valueElementInfo.getJavaClass() == Boolean.class) {
                    buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": Bool", valueElementInfo.isRequired() ? (valueElementInfo.getDefaults().isEmpty() ? " = true" : "=" + valueElementInfo.getDefaults().get(0)) : "?", "\n");
                } else if (valueElementInfo.getJavaClass() == Integer.TYPE || valueElementInfo.getJavaClass() == Integer.class) {
                    buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": Int", valueElementInfo.isRequired() ? (valueElementInfo.getDefaults().isEmpty() ? " = 0" : "=" + valueElementInfo.getDefaults().get(0)) : "?", "\n");
                } else if (valueElementInfo.getJavaClass() == Long.TYPE || valueElementInfo.getJavaClass() == Long.class) {
                    buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": Int64", valueElementInfo.isRequired() ? (valueElementInfo.getDefaults().isEmpty() ? " = 0" : "=" + valueElementInfo.getDefaults().get(0)) : "?", "\n");
                } else {
                    buf.put("UTF-8", "    ", "var ", valueElementInfo.getName(), ": ", (valueElementInfo.getDefaults().isEmpty() ? " " : "=" + valueElementInfo.getDefaults().get(0)), "?", "\n");
                }
            } else if (column.isBean()) {
                BeanElementInfo beanElementInfo = column.as(BeanElementInfo.class);
                buf.put("UTF-8", "    ///", beanElementInfo.getDesc(), " ", beanElementInfo.getUsage() ,"\n");
                buf.put("UTF-8", "    ", "var ", beanElementInfo.getName(), ": ", context.getDomainsPackage(), beanElementInfo.getJavaClass().getSimpleName(), " ?", "\n");
            } else if (column.isForm()) {
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                buf.put("UTF-8", "    ///", formElementInfo.getDesc(), " ", formElementInfo.getUsage() ,"\n");
                buf.put("UTF-8", "    ", "var ", column.getName(), ": [", context.getDomainsPackage(), formElementInfo.getBeanClass().getSimpleName(), "]?", "\n");
            }
            buf.put("UTF-8", "\n");
        }
        buf.put("UTF-8", "}");
        InterfaceFileFormat fileFormat = new InterfaceFileFormat();
        fileFormat.setFilePath(context.getDomainsFilePath());
        fileFormat.setPackagePath(context.getDomainsPackage());
        fileFormat.setCode(buf.asString("UTF-8"));
        fileFormat.setFileSuffix("swift");
        fileFormat.setFileName(context.getDomainsPackage() + interfaceInfo.getResponseClass().getSimpleName());
        context.addInterfaceFile(fileFormat);
        for (ElementInfo elementInfo : interfaceInfo.getResponse().getAllElements()) {
            if (elementInfo.isBean()) {
                generateValueObjectClass(context, elementInfo.as(BeanElementInfo.class), true);
            }
            if (elementInfo.isForm()) {
                generateFormObjectClass(context, elementInfo.as(FormElementInfo.class), true);
            }
        }
    }
}
