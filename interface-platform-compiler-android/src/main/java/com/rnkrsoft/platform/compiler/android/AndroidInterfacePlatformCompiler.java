package com.rnkrsoft.platform.compiler.android;

import com.rnkrsoft.io.buffer.ByteBuf;
import com.rnkrsoft.platform.compiler.CompileContext;
import com.rnkrsoft.platform.compiler.InterfaceFileFormat;
import com.rnkrsoft.platform.compiler.InterfacePlatformCompiler;
import com.rnkrsoft.platform.compiler.format.ZipInterfaceFormat;
import com.rnkrsoft.platform.protocol.AsyncHandler;
import com.rnkrsoft.utils.DateUtils;
import com.rnkrsoft.utils.StringUtils;
import org.apache.commons.io.IOUtils;

import javax.web.doc.*;
import javax.web.doc.annotation.ApidocElement;
import javax.web.doc.annotation.ApidocInterface;
import javax.web.doc.annotation.ApidocService;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by rnkrsoft.com on 2019/2/2.
 */
public class AndroidInterfacePlatformCompiler implements InterfacePlatformCompiler {

    @Override
    public String getType() {
        return "Android";
    }

    public void compile(CompileContext context) throws IOException {
        DocScanner docScanner = context.getDocScanner();
        try {
            generate(context, docScanner.listService());
            if (context.isPackZip()) {
                OutputStream os = null;
                try {
                    File file = new File(context.getOutputPath(), context.getOutputFileName() + "-" + DateUtils.getTimestamp() + ".zip");
                    if (!file.getParentFile().exists()){
                        file.getParentFile().mkdirs();
                    }
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
        } catch (FileNotFoundException e) {
            ;
        }

    }

    void generate(CompileContext context, List<ServiceInfo> services) throws FileNotFoundException {
        for (ServiceInfo serviceInfo : services) {
            if (!context.getIncludeServices().isEmpty() && !context.getIncludeServices().contains(serviceInfo.getServiceClassName())) {
                continue;
            }
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
        buf.putUTF8("package ", context.getServicePackage(), ";\n");
        buf.putUTF8("\n");
        buf.putUTF8("import ", ApidocService.class.getName(), ";", "\n");
        buf.putUTF8("import ", ApidocInterface.class.getName(), ";", "\n");
        buf.putUTF8("import ", AsyncHandler.class.getName(), ";", "\n");
        buf.putUTF8("import ", "android.os.AsyncTask", ";", "\n");
        buf.putUTF8("\n");
        buf.putUTF8("import ", context.getDomainsPackage(), ".*;\n");
        buf.putUTF8("\n");
        buf.putUTF8("/**\n");
        buf.putUTF8(" * ", context.getCopyright(), " \n");
        buf.putUTF8(" */\n");
        buf.putUTF8("@ApidocService(value = \"", serviceInfo.getDesc(), "\", channel = \"", serviceInfo.getChannel(), "\" )\n");
        buf.putUTF8("public interface ", serviceInfo.getServiceClass().getSimpleName(), " {\n");
        context.increaseDeep();
        for (InterfaceInfo interfaceInfo : serviceInfo.getInterfaces()) {
            buf.putUTF8(context.indent_n(), "@ApidocInterface(value = \"", interfaceInfo.getDesc(), "\", name = \"", interfaceInfo.getName(), "\", version = \"", interfaceInfo.getVersion(), "\", usage = \"", interfaceInfo.getUsage(), "\")\n");
            buf.putUTF8(context.indent_n(), "AsyncTask ", interfaceInfo.getMethodName(), "(", interfaceInfo.getRequestClass().getSimpleName(), " request, AsyncHandler<", interfaceInfo.getResponseClass().getSimpleName(), "> asyncHandler);\n");
            buf.putUTF8("\n");
        }
        context.decreaseDeep();
        buf.putUTF8("}\n");
        InterfaceFileFormat fileFormat = new InterfaceFileFormat();
        fileFormat.setFilePath(context.getServiceFilePath());
        fileFormat.setPackagePath(context.getServicePackage());
        fileFormat.setCode(buf.asString("UTF-8"));
        fileFormat.setFileSuffix("java");
        fileFormat.setFileName(serviceInfo.getServiceClass().getSimpleName());
        context.addInterfaceFile(fileFormat);
    }

    void generateRequestClass(CompileContext context, InterfaceInfo interfaceInfo) throws FileNotFoundException {
        context.increaseDeep();
        ByteBuf buf = ByteBuf.allocate(1024).autoExpand(true);
        buf.putUTF8("package ", context.getDomainsPackage(), ";\n");
        buf.putUTF8("\n");
        buf.putUTF8("import javax.web.doc.annotation.ApidocElement;", "\n");
        buf.putUTF8("import java.util.List;", "\n");
        buf.putUTF8("import java.util.ArrayList;", "\n");
        if (interfaceInfo.isPageable()) {
            buf.putUTF8("import ", AbstractRequestPage.class.getName(), ";", "\n");
        } else {
            buf.putUTF8("import ", Serializable.class.getName(), ";", "\n");
        }
        buf.putUTF8("\n");
        buf.putUTF8("import ", context.getDomainsPackage(), ".*;", "\n");
        buf.putUTF8("\n");
        buf.putUTF8("/**\n");
        buf.putUTF8(" * ", context.getCopyright(), " \n");
        buf.putUTF8(" */\n");
        buf.putUTF8("public class ", interfaceInfo.getRequestClass().getSimpleName(), (interfaceInfo.isPageable() ? " extends AbstractRequestPage" : " implements Serializable"), " {\n");
        List<ElementInfo> columns = new ArrayList<ElementInfo>();
        columns.addAll(interfaceInfo.getRequest().getElements());
        for (ElementInfo column : columns) {
            if (column.isValue()) {
                ValueElementInfo valueElementInfo = column.as(ValueElementInfo.class);
                if (valueElementInfo.isEnum()) {
                    buf.putUTF8(context.indent_n(), "/**", "\n");
                    for (String key : valueElementInfo.getEnums().keySet()) {
                        String desc = valueElementInfo.getEnums().get(key);
                        buf.putUTF8(context.indent_n(), " * ", key, " ", desc, "\n");
                    }
                    buf.putUTF8(context.indent_n(), " */", "\n");
                }
                buf.putUTF8(context.indent_n(), "@ApidocElement(value = \"" + valueElementInfo.getDesc() + "\", required = " + valueElementInfo.isRequired() + ", minLen = " + valueElementInfo.getMinLen(), ", maxLen = " + valueElementInfo.getMaxLen(), ")\n");
                if (valueElementInfo.isMultiple()) {
                    buf.putUTF8(context.indent_n(), "final List<", valueElementInfo.getJavaClass().getSimpleName(), "> ", valueElementInfo.getName(), " = new ArrayList();\n");
                } else {
                    buf.putUTF8(context.indent_n(), valueElementInfo.getJavaClass().getSimpleName(), " ", valueElementInfo.getName(), ";\n");
                }
                buf.putUTF8("\n");
            } else if (column.isBean()) {
                BeanElementInfo beanElementInfo = column.as(BeanElementInfo.class);
                buf.putUTF8(context.indent_n(), "@ApidocElement(value = \"" + beanElementInfo.getDesc() + "\")", "\n");
                buf.putUTF8(context.indent_n(), beanElementInfo.getJavaClass().getSimpleName(), " ", beanElementInfo.getName(), ";\n");
                buf.putUTF8("\n");
            } else if (column.isForm()) {
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                buf.putUTF8(context.indent_n(), "@ApidocElement(value = \"" + formElementInfo.getDesc() + "\", required = " + formElementInfo.isRequired() + ")", "\n");
                buf.putUTF8(context.indent_n(), "final ", formElementInfo.getJavaClass().getSimpleName(), "<", formElementInfo.getBeanClass().getSimpleName(), ">", " ", formElementInfo.getName(), " = new ArrayList();\n");
                buf.putUTF8("\n");
            }
        }

        for (ElementInfo column : columns) {
            if (column.isValue()) {
                ValueElementInfo valueElementInfo = column.as(ValueElementInfo.class);
                if (valueElementInfo.isMultiple()) {
                    buf.putUTF8(context.indent_n(), "public ", "List<", valueElementInfo.getJavaClass().getSimpleName(), "> ", "get", StringUtils.firstCharToUpper(valueElementInfo.getName()), "() {\n");
                    buf.putUTF8(context.indent_n(), context.indent_1(), "return this.", valueElementInfo.getName(), ";\n");
                    buf.putUTF8(context.indent_n(), "}\n");
                    buf.putUTF8("\n");
                } else {
                    buf.putUTF8(context.indent_n(), "public ", valueElementInfo.getJavaClass().getSimpleName(), " ", "get", StringUtils.firstCharToUpper(valueElementInfo.getName()), "() {\n");
                    buf.putUTF8(context.indent_n(), context.indent_1(), "return this.", valueElementInfo.getName(), ";\n");
                    buf.putUTF8(context.indent_n(), "}\n");
                    buf.putUTF8("\n");
                    buf.putUTF8(context.indent_n(), "public ", "void ", "set", StringUtils.firstCharToUpper(valueElementInfo.getName()), "(", valueElementInfo.getJavaClass().getSimpleName(), " ", valueElementInfo.getName(), ") {\n");
                    buf.putUTF8(context.indent_n(), context.indent_1(), "this.", valueElementInfo.getName(), " = ", valueElementInfo.getName(), ";\n");
                    buf.putUTF8(context.indent_n(), "}\n");
                    buf.putUTF8("\n");
                }
            } else if (column.isBean()) {
                BeanElementInfo beanElementInfo = column.as(BeanElementInfo.class);
                buf.putUTF8(context.indent_n(), "public ", beanElementInfo.getJavaClass().getSimpleName(), " ", "get", StringUtils.firstCharToUpper(beanElementInfo.getName()), "() {\n");
                buf.putUTF8(context.indent_n(), context.indent_1(), "return this.", beanElementInfo.getName(), ";\n");
                buf.putUTF8(context.indent_n(), "}\n");
                buf.putUTF8("\n");
                buf.putUTF8(context.indent_n(), "public ", "void ", "set", StringUtils.firstCharToUpper(beanElementInfo.getName()), "(", beanElementInfo.getJavaClass().getSimpleName(), " ", beanElementInfo.getName(), ") {\n");
                buf.putUTF8(context.indent_n(), context.indent_1(), "this.", beanElementInfo.getName(), " = ", beanElementInfo.getName(), ";\n");
                buf.putUTF8(context.indent_n(), "}\n");
                buf.putUTF8("\n");
            } else if (column.isForm()) {
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                buf.putUTF8(context.indent_n(), "public ", formElementInfo.getJavaClass().getSimpleName(), "<", formElementInfo.getBeanClass().getSimpleName(), ">", " ", "get", StringUtils.firstCharToUpper(formElementInfo.getName()), "() {\n");
                buf.putUTF8(context.indent_n(), context.indent_1(), "return this.", formElementInfo.getName(), ";\n");
                buf.putUTF8(context.indent_n(), "}\n");
                buf.putUTF8("\n");
            }
        }
        context.decreaseDeep();
        buf.putUTF8(context.indent_n(), "}\n");
        InterfaceFileFormat fileFormat = new InterfaceFileFormat();
        fileFormat.setFilePath(context.getDomainsFilePath());
        fileFormat.setPackagePath(context.getDomainsPackage(false));
        fileFormat.setCode(buf.asString("UTF-8"));
        fileFormat.setFileSuffix("java");
        fileFormat.setFileName(interfaceInfo.getRequestClass().getSimpleName());
        context.addInterfaceFile(fileFormat);
        for (ElementInfo elementInfo : columns) {
            if (elementInfo.isBean()) {
                generateValueObjectClass(context, elementInfo.as(BeanElementInfo.class), false);
            }
            if (elementInfo.isForm()) {
                generateFormObjectClass(context, elementInfo.as(FormElementInfo.class), false);
            }
        }
    }

    void generateValueObjectClass(CompileContext context, BeanElementInfo elementInfo, boolean response) throws FileNotFoundException {
        ByteBuf buf = ByteBuf.allocate(1024).autoExpand(true);
        buf.putUTF8("package ", context.getDomainsPackage(), ";\n");
        buf.putUTF8("\n");
        buf.putUTF8("import ", ApidocElement.class.getName(), ";", "\n");
        buf.putUTF8("\n");
        buf.putUTF8("import ", Serializable.class.getName(), ";", "\n");
        buf.putUTF8("import ", List.class.getName(), ";", "\n");
        buf.putUTF8("import ", ArrayList.class.getName(), ";", "\n");
        buf.putUTF8("\n");
        buf.putUTF8("import ", context.getDomainsPackage(), ".*;", "\n");
        buf.putUTF8("\n");
        buf.putUTF8("/**\n");
        buf.putUTF8(" * ", context.getCopyright(), " \n");
        buf.putUTF8(" */\n");
        buf.putUTF8("public class ", elementInfo.getJavaClass().getSimpleName(), " implements Serializable", " {\n");
        context.increaseDeep();
        for (ElementInfo column : elementInfo.getElements()) {
            if (column.isValue()) {
                ValueElementInfo valueElementInfo = column.as(ValueElementInfo.class);
                if (valueElementInfo.isEnum()) {
                    buf.putUTF8(context.indent_n(), "/**", "\n");
                    for (String key : valueElementInfo.getEnums().keySet()) {
                        String desc = valueElementInfo.getEnums().get(key);
                        buf.putUTF8(context.indent_n(), " * ", key, " ", desc, "\n");
                    }
                    buf.putUTF8(context.indent_n(), " */", "\n");
                }
                buf.putUTF8(context.indent_n(), "@ApidocElement(value = \"" + valueElementInfo.getDesc() + "\", required = " + valueElementInfo.isRequired() + ", minLen = " + valueElementInfo.getMinLen(), ", maxLen = " + valueElementInfo.getMaxLen(), ")\n");
                if (valueElementInfo.isMultiple()) {
                    buf.putUTF8(context.indent_n(), "final List<", valueElementInfo.getJavaClass().getSimpleName(), "> ", valueElementInfo.getName(), " = new ArrayList();\n");
                } else {
                    buf.putUTF8(context.indent_n(), valueElementInfo.getJavaClass().getSimpleName(), " ", valueElementInfo.getName(), ";\n");
                }
                buf.putUTF8("\n");
            } else if (column.isBean()) {
                BeanElementInfo beanElementInfo = column.as(BeanElementInfo.class);
                buf.putUTF8(context.indent_n(), "@ApidocElement(value = \"" + beanElementInfo.getDesc() + "\")", "\n");
                buf.putUTF8(context.indent_n(), beanElementInfo.getJavaClass().getSimpleName(), " ", beanElementInfo.getName(), ";\n");
                buf.putUTF8("\n");
            } else if (column.isForm()) {
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                buf.putUTF8(context.indent_n(), "@ApidocElement(value = \"" + formElementInfo.getDesc() + "\", required = " + formElementInfo.isRequired() + ")", "\n");
                buf.putUTF8(context.indent_n(), "final ", formElementInfo.getJavaClass().getSimpleName(), "<", formElementInfo.getBeanClass().getSimpleName(), ">", " ", formElementInfo.getName(), " = new ArrayList();\n");
                buf.putUTF8("\n");
            }
        }

        for (ElementInfo column : elementInfo.getElements()) {
            if (column.isValue()) {
                ValueElementInfo valueElementInfo = column.as(ValueElementInfo.class);
                if (valueElementInfo.isMultiple()) {
                    buf.putUTF8(context.indent_n(), "public ", "List<", valueElementInfo.getJavaClass().getSimpleName(), "> ", "get", StringUtils.firstCharToUpper(valueElementInfo.getName()), "() {\n");
                    buf.putUTF8(context.indent_n(), context.indent_1(), "return this.", valueElementInfo.getName(), ";\n");
                    buf.putUTF8(context.indent_n(), "}\n");
                    buf.putUTF8("\n");
                } else {
                    buf.putUTF8(context.indent_n(), "public ", valueElementInfo.getJavaClass().getSimpleName(), " ", "get", StringUtils.firstCharToUpper(valueElementInfo.getName()), "() {\n");
                    buf.putUTF8(context.indent_n(), context.indent_1(), "return this.", valueElementInfo.getName(), ";\n");
                    buf.putUTF8(context.indent_n(), "}\n");
                    buf.putUTF8("\n");
                    buf.putUTF8(context.indent_n(), "public ", "void ", "set", StringUtils.firstCharToUpper(valueElementInfo.getName()), "(", valueElementInfo.getJavaClass().getSimpleName(), " ", valueElementInfo.getName(), ") {\n");
                    buf.putUTF8(context.indent_n(), context.indent_1(), "this.", valueElementInfo.getName(), " = ", valueElementInfo.getName(), ";\n");
                    buf.putUTF8(context.indent_n(), "}\n");
                    buf.putUTF8("\n");
                }
            } else if (column.isBean()) {
                BeanElementInfo beanElementInfo = column.as(BeanElementInfo.class);
                buf.putUTF8(context.indent_n(), "public ", beanElementInfo.getJavaClass().getSimpleName(), " ", "get", StringUtils.firstCharToUpper(beanElementInfo.getName()), "() {\n");
                buf.putUTF8(context.indent_n(), context.indent_1(), "return this.", beanElementInfo.getName(), ";\n");
                buf.putUTF8(context.indent_n(), "}\n");
                buf.putUTF8("\n");
                buf.putUTF8(context.indent_n(), "public ", "void ", "set", StringUtils.firstCharToUpper(beanElementInfo.getName()), "(", beanElementInfo.getJavaClass().getSimpleName(), " ", beanElementInfo.getName(), ") {\n");
                buf.putUTF8(context.indent_n(), context.indent_1(), "this.", beanElementInfo.getName(), " = ", beanElementInfo.getName(), ";\n");
                buf.putUTF8(context.indent_n(), "}\n");
                buf.putUTF8("\n");
            } else if (column.isForm()) {
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                buf.putUTF8(context.indent_n(), "public ", formElementInfo.getJavaClass().getSimpleName(), "<", formElementInfo.getBeanClass().getSimpleName(), ">", " ", "get", StringUtils.firstCharToUpper(formElementInfo.getName()), "() {\n");
                buf.putUTF8(context.indent_n(), context.indent_1(), "return this.", formElementInfo.getName(), ";\n");
                buf.putUTF8(context.indent_n(), "}\n");
                buf.putUTF8("\n");
            }
        }
        context.decreaseDeep();
        buf.putUTF8("}\n");
        InterfaceFileFormat fileFormat = new InterfaceFileFormat();
        fileFormat.setFilePath(context.getDomainsFilePath());
        fileFormat.setPackagePath(context.getDomainsPackage(false));
        fileFormat.setCode(buf.asString("UTF-8"));
        fileFormat.setFileSuffix("java");
        fileFormat.setFileName(elementInfo.getJavaClass().getSimpleName());
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
        buf.putUTF8("package ", context.getDomainsPackage(), ";\n");
        buf.putUTF8("\n");
        buf.putUTF8("import ", ApidocElement.class.getName(), ";", "\n");
        buf.putUTF8("\n");
        buf.putUTF8("import ", Serializable.class.getName(), ";", "\n");
        buf.putUTF8("import ", List.class.getName(), ";", "\n");
        buf.putUTF8("import ", ArrayList.class.getName(), ";", "\n");
        buf.putUTF8("\n");
        buf.putUTF8("import ", context.getDomainsPackage(), ".*;", "\n");
        buf.putUTF8("\n");
        buf.putUTF8("/**\n");
        buf.putUTF8(" * ", context.getCopyright(), " \n");
        buf.putUTF8(" */\n");
        buf.putUTF8("public class ", elementInfo.getBeanClass().getSimpleName(), " implements Serializable", " {\n");
        context.increaseDeep();
        for (ElementInfo column : elementInfo.getElements()) {
            if (column.isValue()) {
                ValueElementInfo valueElementInfo = column.as(ValueElementInfo.class);
                if (valueElementInfo.isEnum()) {
                    buf.putUTF8(context.indent_n(), "/**", "\n");
                    for (String key : valueElementInfo.getEnums().keySet()) {
                        String desc = valueElementInfo.getEnums().get(key);
                        buf.putUTF8(context.indent_n(), " * ", key, " ", desc, "\n");
                    }
                    buf.putUTF8(context.indent_n(), " */", "\n");
                }
                buf.putUTF8(context.indent_n(), "@ApidocElement(value = \"" + valueElementInfo.getDesc() + "\", required = " + valueElementInfo.isRequired() + ", minLen = " + valueElementInfo.getMinLen(), ", maxLen = " + valueElementInfo.getMaxLen(), ")\n");
                if (valueElementInfo.isMultiple()) {
                    buf.putUTF8(context.indent_n(), "final List<", valueElementInfo.getJavaClass().getSimpleName(), "> ", valueElementInfo.getName(), " = new ArrayList();\n");
                } else {
                    buf.putUTF8(context.indent_n(), valueElementInfo.getJavaClass().getSimpleName(), " ", valueElementInfo.getName(), ";\n");
                }
                buf.putUTF8("\n");
            } else if (column.isBean()) {
                BeanElementInfo beanElementInfo = column.as(BeanElementInfo.class);
                buf.putUTF8(context.indent_n(), "@ApidocElement(value = \"" + beanElementInfo.getDesc() + "\")", "\n");
                buf.putUTF8(context.indent_n(), beanElementInfo.getJavaClass().getSimpleName(), " ", beanElementInfo.getName(), ";\n");
                buf.putUTF8("\n");
            } else if (column.isForm()) {
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                buf.putUTF8(context.indent_n(), "@ApidocElement(value = \"" + formElementInfo.getDesc() + "\" , required = " + formElementInfo.isRequired() + ")", "\n");
                buf.putUTF8(context.indent_n(), "final ", formElementInfo.getJavaClass().getSimpleName(), "<", formElementInfo.getBeanClass().getSimpleName(), ">", " ", formElementInfo.getName(), " = new ArrayList();\n");
                buf.putUTF8("\n");
            }
        }

        for (ElementInfo column : elementInfo.getElements()) {
            if (column.isValue()) {
                ValueElementInfo valueElementInfo = column.as(ValueElementInfo.class);
                if (valueElementInfo.isMultiple()) {
                    buf.putUTF8(context.indent_n(), "public ", "List<", valueElementInfo.getJavaClass().getSimpleName(), "> ", "get", StringUtils.firstCharToUpper(valueElementInfo.getName()), "() {\n");
                    buf.putUTF8(context.indent_n(), context.indent_1(), "return this.", valueElementInfo.getName(), ";\n");
                    buf.putUTF8(context.indent_n(), "}\n");
                    buf.putUTF8("\n");
                } else {
                    buf.putUTF8(context.indent_n(), "public ", valueElementInfo.getJavaClass().getSimpleName(), " ", "get", StringUtils.firstCharToUpper(valueElementInfo.getName()), "() {\n");
                    buf.putUTF8(context.indent_n(), context.indent_1(), "return this.", valueElementInfo.getName(), ";\n");
                    buf.putUTF8(context.indent_n(), "}\n");
                    buf.putUTF8("\n");
                    buf.putUTF8(context.indent_n(), "public ", "void ", "set", StringUtils.firstCharToUpper(valueElementInfo.getName()), "(", valueElementInfo.getJavaClass().getSimpleName(), " ", valueElementInfo.getName(), ") {\n");
                    buf.putUTF8(context.indent_n(), context.indent_1(), "this.", valueElementInfo.getName(), " = ", valueElementInfo.getName(), ";\n");
                    buf.putUTF8(context.indent_n(), "}\n");
                    buf.putUTF8("\n");
                }
            } else if (column.isBean()) {
                BeanElementInfo beanElementInfo = column.as(BeanElementInfo.class);
                buf.putUTF8(context.indent_n(), "public ", beanElementInfo.getJavaClass().getSimpleName(), " ", "get", StringUtils.firstCharToUpper(beanElementInfo.getName()), "() {\n");
                buf.putUTF8(context.indent_n(), context.indent_1(), "return this.", beanElementInfo.getName(), ";\n");
                buf.putUTF8(context.indent_n(), "}\n");
                buf.putUTF8("\n");
                buf.putUTF8(context.indent_n(), "public ", "void ", "set", StringUtils.firstCharToUpper(beanElementInfo.getName()), "(", beanElementInfo.getJavaClass().getSimpleName(), " ", beanElementInfo.getName(), ") {\n");
                buf.putUTF8(context.indent_n(), context.indent_1(), "this.", beanElementInfo.getName(), " = ", beanElementInfo.getName(), ";\n");
                buf.putUTF8(context.indent_n(), "}\n");
                buf.putUTF8("\n");
            } else if (column.isForm()) {
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                buf.putUTF8(context.indent_n(), "public ", formElementInfo.getJavaClass().getSimpleName(), "<", formElementInfo.getBeanClass().getSimpleName(), ">", " ", "get", StringUtils.firstCharToUpper(formElementInfo.getName()), "() {\n");
                buf.putUTF8(context.indent_n(), context.indent_1(), "return this.", formElementInfo.getName(), ";\n");
                buf.putUTF8(context.indent_n(), "}\n");
                buf.putUTF8("\n");
            }
        }
        context.decreaseDeep();
        buf.putUTF8("}\n");
        InterfaceFileFormat fileFormat = new InterfaceFileFormat();
        fileFormat.setFilePath(context.getDomainsFilePath());
        fileFormat.setPackagePath(context.getDomainsPackage(false));
        fileFormat.setCode(buf.asString("UTF-8"));
        fileFormat.setFileSuffix("java");
        fileFormat.setFileName(elementInfo.getBeanClass().getSimpleName());
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
        buf.putUTF8("package ", context.getDomainsPackage(), ";\n");
        buf.putUTF8("\n");
        buf.putUTF8("import ", ApidocElement.class.getName(), ";", "\n");
        if (interfaceInfo.isPageable()) {
            buf.putUTF8("import ", AbstractResponsePage.class.getName(), ";", "\n");
        } else {
            buf.putUTF8("import ", AbstractResponse.class.getName(), ";", "\n");
        }
        buf.putUTF8("\n");
        buf.putUTF8("import ", Serializable.class.getName(), ";", "\n");
        buf.putUTF8("import ", List.class.getName(), ";", "\n");
        buf.putUTF8("import ", ArrayList.class.getName(), ";", "\n");
        buf.putUTF8("\n");
        buf.putUTF8("import ", context.getDomainsPackage(), ".*;", "\n");
        buf.putUTF8("\n");
        buf.putUTF8("/**\n");
        buf.putUTF8(" * ", context.getCopyright(), " \n");
        buf.putUTF8(" */\n");
        Class recordsJavaClass = null;
        List<ElementInfo> columns = new ArrayList<ElementInfo>();
        columns.addAll(interfaceInfo.getResponse().getElements());
        if (interfaceInfo.getResponse().isPageable()){
            for (ElementInfo column : interfaceInfo.getResponse().getAllElements()) {
                if (column.isForm()) {
                    FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                    if (formElementInfo.getName().equals("records")) {
                        recordsJavaClass = formElementInfo.getBeanClass();
                    }
                }
            }
        }
        buf.putUTF8("public class ", interfaceInfo.getResponseClass().getSimpleName(), (interfaceInfo.isPageable() ? " extends " + AbstractResponsePage.class.getSimpleName() + "<" + recordsJavaClass.getSimpleName() + ">" : " extends AbstractResponse"), " {\n");
        context.increaseDeep();
        for (ElementInfo column : columns) {
            if (column.isValue()) {
                ValueElementInfo valueElementInfo = column.as(ValueElementInfo.class);
                if (valueElementInfo.isEnum()) {
                    buf.putUTF8(context.indent_n(), "/**", "\n");
                    for (String key : valueElementInfo.getEnums().keySet()) {
                        String desc = valueElementInfo.getEnums().get(key);
                        buf.putUTF8(context.indent_n(), " * ", key, " ", desc, "\n");
                    }
                    buf.putUTF8(context.indent_n(), " */", "\n");
                }
                buf.putUTF8(context.indent_n(), "@ApidocElement(value = \"" + valueElementInfo.getDesc() + "\", required = " + valueElementInfo.isRequired() + ", minLen = " + valueElementInfo.getMinLen(), ", maxLen = " + valueElementInfo.getMaxLen(), ")\n");
                if (valueElementInfo.isMultiple()) {
                    buf.putUTF8(context.indent_n(), "final List<", valueElementInfo.getJavaClass().getSimpleName(), "> ", valueElementInfo.getName(), " = new ArrayList();\n");
                } else {
                    buf.putUTF8(context.indent_n(), valueElementInfo.getJavaClass().getSimpleName(), " ", valueElementInfo.getName(), ";\n");
                }
                buf.putUTF8("\n");
            } else if (column.isBean()) {
                BeanElementInfo beanElementInfo = column.as(BeanElementInfo.class);
                buf.putUTF8(context.indent_n(), "@ApidocElement(value = \"" + beanElementInfo.getDesc() + "\")\n");
                buf.putUTF8(context.indent_n(), beanElementInfo.getJavaClass().getSimpleName(), " ", beanElementInfo.getName(), ";\n");
                buf.putUTF8("\n");
            } else if (column.isForm()) {
                if (interfaceInfo.isPageable() && column.getName().equals("records")) {
                    continue;
                }
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                buf.putUTF8(context.indent_n(), "@ApidocElement(value = \"" + formElementInfo.getDesc() + "\", required = " + formElementInfo.isRequired() + ")", "\n");
                buf.putUTF8(context.indent_n(), "final ", formElementInfo.getJavaClass().getSimpleName(), "<", formElementInfo.getBeanClass().getSimpleName(), ">", " ", formElementInfo.getName(), " = new ArrayList();\n");
                buf.putUTF8("\n");
            }
        }

        for (ElementInfo column : columns) {
            if (column.isValue()) {
                ValueElementInfo valueElementInfo = column.as(ValueElementInfo.class);
                if (valueElementInfo.isMultiple()) {
                    buf.putUTF8(context.indent_n(), "public ", "List<", valueElementInfo.getJavaClass().getSimpleName(), "> ", "get", StringUtils.firstCharToUpper(valueElementInfo.getName()), "() {\n");
                    buf.putUTF8(context.indent_n(), context.indent_1(), "return this.", valueElementInfo.getName(), ";\n");
                    buf.putUTF8(context.indent_n(), "}\n");
                    buf.putUTF8("\n");
                } else {
                    buf.putUTF8(context.indent_n(), "public ", valueElementInfo.getJavaClass().getSimpleName(), " ", "get", StringUtils.firstCharToUpper(valueElementInfo.getName()), "() {\n");
                    buf.putUTF8(context.indent_n(), context.indent_1(), "return this.", valueElementInfo.getName(), ";\n");
                    buf.putUTF8(context.indent_n(), "}\n");
                    buf.putUTF8("\n");
                    buf.putUTF8(context.indent_n(), "public ", "void ", "set", StringUtils.firstCharToUpper(valueElementInfo.getName()), "(", valueElementInfo.getJavaClass().getSimpleName(), " ", valueElementInfo.getName(), ") {\n");
                    buf.putUTF8(context.indent_n(), context.indent_1(), "this.", valueElementInfo.getName(), " = ", valueElementInfo.getName(), ";\n");
                    buf.putUTF8(context.indent_n(), "}\n");
                    buf.putUTF8("\n");
                }
            } else if (column.isBean()) {
                BeanElementInfo beanElementInfo = column.as(BeanElementInfo.class);
                buf.putUTF8(context.indent_n(), "public ", beanElementInfo.getJavaClass().getSimpleName(), " ", "get", StringUtils.firstCharToUpper(beanElementInfo.getName()), "() {\n");
                buf.putUTF8(context.indent_n(), context.indent_1(), "return this.", beanElementInfo.getName(), ";\n");
                buf.putUTF8(context.indent_n(), "}\n");
                buf.putUTF8("\n");
                buf.putUTF8(context.indent_n(), "public ", "void ", "set", StringUtils.firstCharToUpper(beanElementInfo.getName()), "(", beanElementInfo.getJavaClass().getSimpleName(), " ", beanElementInfo.getName(), ") {\n");
                buf.putUTF8(context.indent_n(), context.indent_1(), "this.", beanElementInfo.getName(), " = ", beanElementInfo.getName(), ";\n");
                buf.putUTF8(context.indent_n(), "}\n");
                buf.putUTF8("\n");
            } else if (column.isForm()) {
                if (interfaceInfo.isPageable() && column.getName().equals("records")) {
                    continue;
                }
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                buf.putUTF8(context.indent_n(), "public ", formElementInfo.getJavaClass().getSimpleName(), "<", formElementInfo.getBeanClass().getSimpleName(), ">", " ", "get", StringUtils.firstCharToUpper(formElementInfo.getName()), "() {\n");
                buf.putUTF8(context.indent_n(), context.indent_1(), "return this.", formElementInfo.getName(), ";\n");
                buf.putUTF8(context.indent_n(), "}\n");
                buf.putUTF8("\n");
            }
        }
        context.decreaseDeep();
        buf.putUTF8("}\n");
        InterfaceFileFormat fileFormat = new InterfaceFileFormat();
        fileFormat.setFilePath(context.getDomainsFilePath());
        fileFormat.setPackagePath(context.getDomainsPackage());
        fileFormat.setCode(buf.asString("UTF-8"));
        fileFormat.setFileSuffix("java");
        fileFormat.setFileName(interfaceInfo.getResponseClass().getSimpleName());
        context.addInterfaceFile(fileFormat);

        Set<Class> classes = new HashSet<Class>();
        for (ElementInfo column : interfaceInfo.getResponse().getAllElements()) {
            if (column.isBean()) {
                BeanElementInfo beanElementInfo = column.as(BeanElementInfo.class);
                if (classes.contains(beanElementInfo.getJavaClass())) {
                    continue;
                }
                classes.add(beanElementInfo.getJavaClass());
                generateValueObjectClass(context, beanElementInfo, true);
            } else if (column.isForm()) {
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                if (classes.contains(formElementInfo.getBeanClass())) {
                    continue;
                }
                classes.add(formElementInfo.getBeanClass());
                generateFormObjectClass(context, formElementInfo, true);
            }
        }

    }
}
