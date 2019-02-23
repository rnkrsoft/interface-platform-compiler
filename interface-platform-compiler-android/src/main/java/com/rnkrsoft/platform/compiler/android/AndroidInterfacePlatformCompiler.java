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
                    File file = new File(context.getOutputPath(), context.getOutputFileName() + "-"+ DateUtils.getTimestamp() + ".zip");
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
            if (!context.getIncludeServices().isEmpty() && !context.getIncludeServices().contains(serviceInfo.getServiceClassName())){
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
        buf.put("UTF-8", "package ", context.getServicePackage(), ";\n");
        buf.put("UTF-8", "\n");
        buf.put("UTF-8", "import ", ApidocService.class.getName(), ";", "\n");
        buf.put("UTF-8", "import ", ApidocInterface.class.getName(), ";", "\n");
        buf.put("UTF-8", "import ", AsyncHandler.class.getName(), ";", "\n");
        buf.put("UTF-8", "import ", "android.os.AsyncTask", ";", "\n");
        buf.put("UTF-8", "\n");
        buf.put("UTF-8", "import ", context.getDomainsPackage(), ".*;\n");
        buf.put("UTF-8", "\n");
        buf.put("UTF-8", "/**\n");
        buf.put("UTF-8", " * ", context.getCopyright(), " \n");
        buf.put("UTF-8", " */\n");
        buf.put("UTF-8", "@ApidocService(value = \"", serviceInfo.getDesc(), "\", channel = \"", serviceInfo.getChannel(), "\" )\n");
        buf.put("UTF-8", "public interface ", serviceInfo.getServiceClass().getSimpleName(), " {\n");
        context.increaseDeep();
        for (InterfaceInfo interfaceInfo : serviceInfo.getInterfaces()) {
            buf.put("UTF-8", context.indent_n(), "@ApidocInterface(value = \"", interfaceInfo.getDesc(), "\", name = \"", interfaceInfo.getName(), "\", version = \"", interfaceInfo.getVersion(), "\", usage = \"", interfaceInfo.getUsage(), "\")\n");
            buf.put("UTF-8", context.indent_n(), "AsyncTask ", interfaceInfo.getMethodName(), "(", interfaceInfo.getRequestClass().getSimpleName(), " request, AsyncHandler<", interfaceInfo.getResponseClass().getSimpleName(), "> asyncHandler);\n");
            buf.put("UTF-8", "\n");
        }
        context.decreaseDeep();
        buf.put("UTF-8", "}\n");
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
        buf.put("UTF-8", "package ", context.getDomainsPackage(), ";\n");
        buf.put("UTF-8", "\n");
        buf.put("UTF-8", "import javax.web.doc.annotation.ApidocElement;", "\n");
        buf.put("UTF-8", "import java.util.List;", "\n");
        buf.put("UTF-8", "import java.util.ArrayList;", "\n");
        if (interfaceInfo.isPageable()) {
            buf.put("UTF-8", "import ", AbstractRequestPage.class.getName(), ";", "\n");
        } else {
            buf.put("UTF-8", "import ", Serializable.class.getName(), ";", "\n");
        }
        buf.put("UTF-8", "\n");
        buf.put("UTF-8", "import ", context.getDomainsPackage(), ".*;", "\n");
        buf.put("UTF-8", "\n");
        buf.put("UTF-8", "/**\n");
        buf.put("UTF-8", " * ", context.getCopyright(), " \n");
        buf.put("UTF-8", " */\n");
        buf.put("UTF-8", "public class ", interfaceInfo.getRequestClass().getSimpleName(), (interfaceInfo.isPageable() ? " extends AbstractRequestPage" : " implements Serializable"), " {\n");
        for (ElementInfo column : interfaceInfo.getRequest().getElements()) {
            if (column.isValue()) {
                ValueElementInfo valueElementInfo = column.as(ValueElementInfo.class);
                if (valueElementInfo.isEnum()) {
                    buf.put("UTF-8", context.indent_n(), "/**", "\n");
                    for (String key : valueElementInfo.getEnums().keySet()) {
                        String desc = valueElementInfo.getEnums().get(key);
                        buf.put("UTF-8", context.indent_n(), " * ", key, " ", desc, "\n");
                    }
                    buf.put("UTF-8", context.indent_n(), " */", "\n");
                }
                buf.put("UTF-8", context.indent_n(), "@ApidocElement(value = \"" + valueElementInfo.getDesc() + "\", required = " + valueElementInfo.isRequired() + ", minLen = " + valueElementInfo.getMinLen(), ", maxLen = " + valueElementInfo.getMaxLen(), ")\n");
                if (valueElementInfo.isMultiple()) {
                    buf.put("UTF-8", context.indent_n(), "final List<", valueElementInfo.getJavaClass().getSimpleName(), "> ", valueElementInfo.getName(), " = new ArrayList();\n");
                } else {
                    buf.put("UTF-8", context.indent_n(), valueElementInfo.getJavaClass().getSimpleName(), " ", valueElementInfo.getName(), ";\n");
                }
                buf.put("UTF-8", "\n");
            } else if (column.isBean()) {
                BeanElementInfo beanElementInfo = column.as(BeanElementInfo.class);
                buf.put("UTF-8", context.indent_n(), "@ApidocElement(value = \"" + beanElementInfo.getDesc() + "\")", "\n");
                buf.put("UTF-8", context.indent_n(), beanElementInfo.getJavaClass().getSimpleName(), " ", beanElementInfo.getName(), ";\n");
                buf.put("UTF-8", "\n");
            } else if (column.isForm()) {
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                buf.put("UTF-8", context.indent_n(), "@ApidocElement(value = \"" + formElementInfo.getDesc() + "\", required = " + formElementInfo.isRequired() + ")", "\n");
                buf.put("UTF-8", context.indent_n(), "final ", formElementInfo.getJavaClass().getSimpleName(), "<", formElementInfo.getBeanClass().getSimpleName(), ">", " ", formElementInfo.getName(), " = new ArrayList();\n");
                buf.put("UTF-8", "\n");
            }
        }

        for (ElementInfo column : interfaceInfo.getRequest().getElements()) {
            if (column.isValue()) {
                ValueElementInfo valueElementInfo = column.as(ValueElementInfo.class);
                if (valueElementInfo.isMultiple()) {
                    buf.put("UTF-8", context.indent_n(), "public ", "List<", valueElementInfo.getJavaClass().getSimpleName(), "> ", "get", StringUtils.firstCharToUpper(valueElementInfo.getName()), "() {\n");
                    buf.put("UTF-8", context.indent_n(), context.indent_1(), "return this.", valueElementInfo.getName(), ";\n");
                    buf.put("UTF-8", context.indent_n(), "}\n");
                    buf.put("UTF-8", "\n");
                } else {
                    buf.put("UTF-8", context.indent_n(), "public ", valueElementInfo.getJavaClass().getSimpleName(), " ", "get", StringUtils.firstCharToUpper(valueElementInfo.getName()), "() {\n");
                    buf.put("UTF-8", context.indent_n(), context.indent_1(), "return this.", valueElementInfo.getName(), ";\n");
                    buf.put("UTF-8", context.indent_n(), "}\n");
                    buf.put("UTF-8", "\n");
                    buf.put("UTF-8", context.indent_n(), "public ", "void ", "set", StringUtils.firstCharToUpper(valueElementInfo.getName()), "(", valueElementInfo.getJavaClass().getSimpleName(), " ", valueElementInfo.getName(), ") {\n");
                    buf.put("UTF-8", context.indent_n(), context.indent_1(), "this.", valueElementInfo.getName(), " = ", valueElementInfo.getName(), ";\n");
                    buf.put("UTF-8", context.indent_n(), "}\n");
                    buf.put("UTF-8", "\n");
                }
            } else if (column.isBean()) {
                BeanElementInfo beanElementInfo = column.as(BeanElementInfo.class);
                buf.put("UTF-8", context.indent_n(), "public ", beanElementInfo.getJavaClass().getSimpleName(), " ", "get", StringUtils.firstCharToUpper(beanElementInfo.getName()), "() {\n");
                buf.put("UTF-8", context.indent_n(), context.indent_1(), "return this.", beanElementInfo.getName(), ";\n");
                buf.put("UTF-8", context.indent_n(), "}\n");
                buf.put("UTF-8", "\n");
                buf.put("UTF-8", context.indent_n(), "public ", "void ", "set", StringUtils.firstCharToUpper(beanElementInfo.getName()), "(", beanElementInfo.getJavaClass().getSimpleName(), " ", beanElementInfo.getName(), ") {\n");
                buf.put("UTF-8", context.indent_n(), context.indent_1(), "this.", beanElementInfo.getName(), " = ", beanElementInfo.getName(), ";\n");
                buf.put("UTF-8", context.indent_n(), "}\n");
                buf.put("UTF-8", "\n");
            } else if (column.isForm()) {
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                buf.put("UTF-8", context.indent_n(), "public ", formElementInfo.getJavaClass().getSimpleName(), "<", formElementInfo.getBeanClass().getSimpleName(), ">", " ", "get", StringUtils.firstCharToUpper(formElementInfo.getName()), "() {\n");
                buf.put("UTF-8", context.indent_n(), context.indent_1(), "return this.", formElementInfo.getName(), ";\n");
                buf.put("UTF-8", context.indent_n(), "}\n");
                buf.put("UTF-8", "\n");
            }
        }
        Set<Class> classes = new HashSet<Class>();
        for (ElementInfo column : interfaceInfo.getRequest().getElements()) {
            if (column.isBean()) {
                BeanElementInfo beanElementInfo = column.as(BeanElementInfo.class);
                if (classes.contains(beanElementInfo.getJavaClass())) {
                    continue;
                }
                classes.add(beanElementInfo.getJavaClass());
                generateValueObjectClass(context, buf, beanElementInfo);
            } else if (column.isForm()) {
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                if (classes.contains(formElementInfo.getBeanClass())) {
                    continue;
                }
                classes.add(formElementInfo.getBeanClass());
                generateFormObjectClass(context, buf, formElementInfo);
            }
        }
        buf.put("UTF-8", "}\n");
        InterfaceFileFormat fileFormat = new InterfaceFileFormat();
        fileFormat.setFilePath(context.getDomainsFilePath());
        fileFormat.setPackagePath(context.getDomainsPackage());
        fileFormat.setCode(buf.getString("UTF-8", buf.readableLength()));
        fileFormat.setFileSuffix("java");
        fileFormat.setFileName(interfaceInfo.getRequestClass().getSimpleName());
        context.addInterfaceFile(fileFormat);
        context.decreaseDeep();
    }

    void generateValueObjectClass(CompileContext context, ByteBuf buf, BeanElementInfo elementInfo) throws FileNotFoundException {
        buf.put("UTF-8", "\n");
        buf.put("UTF-8", context.indent_n(), "public static class ", elementInfo.getJavaClass().getSimpleName(), " implements Serializable", " {\n");
        context.increaseDeep();
        for (ElementInfo column : elementInfo.getElements()) {
            if (column.isValue()) {
                ValueElementInfo valueElementInfo = column.as(ValueElementInfo.class);
                if (valueElementInfo.isEnum()) {
                    buf.put("UTF-8", context.indent_n(), "/**", "\n");
                    for (String key : valueElementInfo.getEnums().keySet()) {
                        String desc = valueElementInfo.getEnums().get(key);
                        buf.put("UTF-8", context.indent_n(), " * ", key, " ", desc, "\n");
                    }
                    buf.put("UTF-8", context.indent_n(), " */", "\n");
                }
                buf.put("UTF-8", context.indent_n(), "@ApidocElement(value = \"" + valueElementInfo.getDesc() + "\", required = " + valueElementInfo.isRequired() + ", minLen = " + valueElementInfo.getMinLen(), ", maxLen = " + valueElementInfo.getMaxLen(), ")\n");
                if (valueElementInfo.isMultiple()) {
                    buf.put("UTF-8", context.indent_n(), "final List<", valueElementInfo.getJavaClass().getSimpleName(), "> ", valueElementInfo.getName(), " = new ArrayList();\n");
                } else {
                    buf.put("UTF-8", context.indent_n(), valueElementInfo.getJavaClass().getSimpleName(), " ", valueElementInfo.getName(), ";\n");
                }
                buf.put("UTF-8", "\n");
            } else if (column.isBean()) {
                BeanElementInfo beanElementInfo = column.as(BeanElementInfo.class);
                buf.put("UTF-8", context.indent_n(), "@ApidocElement(value = \"" + beanElementInfo.getDesc() + "\")", "\n");
                buf.put("UTF-8", context.indent_n(), beanElementInfo.getJavaClass().getSimpleName(), " ", beanElementInfo.getName(), ";\n");
                buf.put("UTF-8", "\n");
            } else if (column.isForm()) {
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                buf.put("UTF-8", context.indent_n(), "@ApidocElement(value = \"" + formElementInfo.getDesc() + "\", required = " + formElementInfo.isRequired() + ")", "\n");
                buf.put("UTF-8", context.indent_n(), "final ", formElementInfo.getJavaClass().getSimpleName(), "<", formElementInfo.getBeanClass().getSimpleName(), ">", " ", formElementInfo.getName(), " = new ArrayList();\n");
                buf.put("UTF-8", "\n");
            }
        }

        for (ElementInfo column : elementInfo.getElements()) {
            if (column.isValue()) {
                ValueElementInfo valueElementInfo = column.as(ValueElementInfo.class);
                if (valueElementInfo.isMultiple()) {
                    buf.put("UTF-8", context.indent_n(), "public ", "List<", valueElementInfo.getJavaClass().getSimpleName(), "> ", "get", StringUtils.firstCharToUpper(valueElementInfo.getName()), "() {\n");
                    buf.put("UTF-8", context.indent_n(), context.indent_1(), "return this.", valueElementInfo.getName(), ";\n");
                    buf.put("UTF-8", context.indent_n(), "}\n");
                    buf.put("UTF-8", "\n");
                } else {
                    buf.put("UTF-8", context.indent_n(), "public ", valueElementInfo.getJavaClass().getSimpleName(), " ", "get", StringUtils.firstCharToUpper(valueElementInfo.getName()), "() {\n");
                    buf.put("UTF-8", context.indent_n(), context.indent_1(), "return this.", valueElementInfo.getName(), ";\n");
                    buf.put("UTF-8", context.indent_n(), "}\n");
                    buf.put("UTF-8", "\n");
                    buf.put("UTF-8", context.indent_n(), "public ", "void ", "set", StringUtils.firstCharToUpper(valueElementInfo.getName()), "(", valueElementInfo.getJavaClass().getSimpleName(), " ", valueElementInfo.getName(), ") {\n");
                    buf.put("UTF-8", context.indent_n(), context.indent_1(), "this.", valueElementInfo.getName(), " = ", valueElementInfo.getName(), ";\n");
                    buf.put("UTF-8", context.indent_n(), "}\n");
                    buf.put("UTF-8", "\n");
                }
            } else if (column.isBean()) {
                BeanElementInfo beanElementInfo = column.as(BeanElementInfo.class);
                buf.put("UTF-8", context.indent_n(), "public ", beanElementInfo.getJavaClass().getSimpleName(), " ", "get", StringUtils.firstCharToUpper(beanElementInfo.getName()), "() {\n");
                buf.put("UTF-8", context.indent_n(), context.indent_1(), "return this.", beanElementInfo.getName(), ";\n");
                buf.put("UTF-8", context.indent_n(), "}\n");
                buf.put("UTF-8", "\n");
                buf.put("UTF-8", context.indent_n(), "public ", "void ", "set", StringUtils.firstCharToUpper(beanElementInfo.getName()), "(", beanElementInfo.getJavaClass().getSimpleName(), " ", beanElementInfo.getName(), ") {\n");
                buf.put("UTF-8", context.indent_n(), context.indent_1(), "this.", beanElementInfo.getName(), " = ", beanElementInfo.getName(), ";\n");
                buf.put("UTF-8", context.indent_n(), "}\n");
                buf.put("UTF-8", "\n");
            } else if (column.isForm()) {
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                buf.put("UTF-8", context.indent_n(), "public ", formElementInfo.getJavaClass().getSimpleName(), "<", formElementInfo.getBeanClass().getSimpleName(), ">", " ", "get", StringUtils.firstCharToUpper(formElementInfo.getName()), "() {\n");
                buf.put("UTF-8", context.indent_n(), context.indent_1(), "return this.", formElementInfo.getName(), ";\n");
                buf.put("UTF-8", context.indent_n(), "}\n");
                buf.put("UTF-8", "\n");
            }
        }
        Set<Class> classes = new HashSet<Class>();
        for (ElementInfo column : elementInfo.getElements()) {
            if (column.isBean()) {
                BeanElementInfo beanElementInfo = column.as(BeanElementInfo.class);
                if (classes.contains(beanElementInfo.getJavaClass())) {
                    continue;
                }
                classes.add(beanElementInfo.getJavaClass());
                generateValueObjectClass(context, buf, beanElementInfo);
            } else if (column.isForm()) {
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                if (classes.contains(formElementInfo.getBeanClass())) {
                    continue;
                }
                classes.add(formElementInfo.getBeanClass());
                generateFormObjectClass(context, buf, formElementInfo);
            }
        }
        context.decreaseDeep();
        buf.put("UTF-8", context.indent_n(), "}\n");
    }


    void generateFormObjectClass(CompileContext context, ByteBuf buf, FormElementInfo elementInfo) throws FileNotFoundException {
        buf.put("UTF-8", "\n");
        buf.put("UTF-8", context.indent_n(), "public static class ", elementInfo.getBeanClass().getSimpleName(), " implements Serializable", " {\n");
        context.increaseDeep();
        for (ElementInfo column : elementInfo.getElements()) {
            if (column.isValue()) {
                ValueElementInfo valueElementInfo = column.as(ValueElementInfo.class);
                if (valueElementInfo.isEnum()) {
                    buf.put("UTF-8", context.indent_n(), "/**", "\n");
                    for (String key : valueElementInfo.getEnums().keySet()) {
                        String desc = valueElementInfo.getEnums().get(key);
                        buf.put("UTF-8", context.indent_n(), " * ", key, " ", desc, "\n");
                    }
                    buf.put("UTF-8", context.indent_n(), " */", "\n");
                }
                buf.put("UTF-8", context.indent_n(), "@ApidocElement(value = \"" + valueElementInfo.getDesc() + "\", required = " + valueElementInfo.isRequired() + ", minLen = " + valueElementInfo.getMinLen(), ", maxLen = " + valueElementInfo.getMaxLen(), ")\n");
                if (valueElementInfo.isMultiple()) {
                    buf.put("UTF-8", context.indent_n(), "final List<", valueElementInfo.getJavaClass().getSimpleName(), "> ", valueElementInfo.getName(), " = new ArrayList();\n");
                } else {
                    buf.put("UTF-8", context.indent_n(), valueElementInfo.getJavaClass().getSimpleName(), " ", valueElementInfo.getName(), ";\n");
                }
                buf.put("UTF-8", "\n");
            } else if (column.isBean()) {
                BeanElementInfo beanElementInfo = column.as(BeanElementInfo.class);
                buf.put("UTF-8", context.indent_n(), "@ApidocElement(value = \"" + beanElementInfo.getDesc() + "\")", "\n");
                buf.put("UTF-8", context.indent_n(), beanElementInfo.getJavaClass().getSimpleName(), " ", beanElementInfo.getName(), ";\n");
                buf.put("UTF-8", "\n");
            } else if (column.isForm()) {
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                buf.put("UTF-8", context.indent_n(), "@ApidocElement(value = \"" + formElementInfo.getDesc() + "\" , required = " + formElementInfo.isRequired() + ")", "\n");
                buf.put("UTF-8", context.indent_n(), "final ", formElementInfo.getJavaClass().getSimpleName(), "<", formElementInfo.getBeanClass().getSimpleName(), ">", " ", formElementInfo.getName(), " = new ArrayList();\n");
                buf.put("UTF-8", "\n");
            }
        }

        for (ElementInfo column : elementInfo.getElements()) {
            if (column.isValue()) {
                ValueElementInfo valueElementInfo = column.as(ValueElementInfo.class);
                if (valueElementInfo.isMultiple()) {
                    buf.put("UTF-8", context.indent_n(), "public ", "List<", valueElementInfo.getJavaClass().getSimpleName(), "> ", "get", StringUtils.firstCharToUpper(valueElementInfo.getName()), "() {\n");
                    buf.put("UTF-8", context.indent_n(), context.indent_1(), "return this.", valueElementInfo.getName(), ";\n");
                    buf.put("UTF-8", context.indent_n(), "}\n");
                    buf.put("UTF-8", "\n");
                } else {
                    buf.put("UTF-8", context.indent_n(), "public ", valueElementInfo.getJavaClass().getSimpleName(), " ", "get", StringUtils.firstCharToUpper(valueElementInfo.getName()), "() {\n");
                    buf.put("UTF-8", context.indent_n(), context.indent_1(), "return this.", valueElementInfo.getName(), ";\n");
                    buf.put("UTF-8", context.indent_n(), "}\n");
                    buf.put("UTF-8", "\n");
                    buf.put("UTF-8", context.indent_n(), "public ", "void ", "set", StringUtils.firstCharToUpper(valueElementInfo.getName()), "(", valueElementInfo.getJavaClass().getSimpleName(), " ", valueElementInfo.getName(), ") {\n");
                    buf.put("UTF-8", context.indent_n(), context.indent_1(), "this.", valueElementInfo.getName(), " = ", valueElementInfo.getName(), ";\n");
                    buf.put("UTF-8", context.indent_n(), "}\n");
                    buf.put("UTF-8", "\n");
                }
            } else if (column.isBean()) {
                BeanElementInfo beanElementInfo = column.as(BeanElementInfo.class);
                buf.put("UTF-8", context.indent_n(), "public ", beanElementInfo.getJavaClass().getSimpleName(), " ", "get", StringUtils.firstCharToUpper(beanElementInfo.getName()), "() {\n");
                buf.put("UTF-8", context.indent_n(), context.indent_1(), "return this.", beanElementInfo.getName(), ";\n");
                buf.put("UTF-8", context.indent_n(), "}\n");
                buf.put("UTF-8", "\n");
                buf.put("UTF-8", context.indent_n(), "public ", "void ", "set", StringUtils.firstCharToUpper(beanElementInfo.getName()), "(", beanElementInfo.getJavaClass().getSimpleName(), " ", beanElementInfo.getName(), ") {\n");
                buf.put("UTF-8", context.indent_n(), context.indent_1(), "this.", beanElementInfo.getName(), " = ", beanElementInfo.getName(), ";\n");
                buf.put("UTF-8", context.indent_n(), "}\n");
                buf.put("UTF-8", "\n");
            } else if (column.isForm()) {
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                buf.put("UTF-8", context.indent_n(), "public ", formElementInfo.getJavaClass().getSimpleName(), "<", formElementInfo.getBeanClass().getSimpleName(), ">", " ", "get", StringUtils.firstCharToUpper(formElementInfo.getName()), "() {\n");
                buf.put("UTF-8", context.indent_n(), context.indent_1(), "return this.", formElementInfo.getName(), ";\n");
                buf.put("UTF-8", context.indent_n(), "}\n");
                buf.put("UTF-8", "\n");
            }
        }
        Set<Class> classes = new HashSet<Class>();
        for (ElementInfo column : elementInfo.getElements()) {
            if (column.isBean()) {
                BeanElementInfo beanElementInfo = column.as(BeanElementInfo.class);
                if (classes.contains(beanElementInfo.getJavaClass())) {
                    continue;
                }
                classes.add(beanElementInfo.getJavaClass());
                generateValueObjectClass(context, buf, beanElementInfo);
            } else if (column.isForm()) {
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                if (classes.contains(formElementInfo.getBeanClass())) {
                    continue;
                }
                classes.add(formElementInfo.getBeanClass());
                generateFormObjectClass(context, buf, formElementInfo);
            }
        }
        context.decreaseDeep();
        buf.put("UTF-8", context.indent_n(), "}\n");
    }

    void generateResponseClass(CompileContext context, InterfaceInfo interfaceInfo) throws FileNotFoundException {
        ByteBuf buf = ByteBuf.allocate(1024).autoExpand(true);
        buf.put("UTF-8", "package ", context.getDomainsPackage(), ";\n");
        buf.put("UTF-8", "\n");
        buf.put("UTF-8", "import ", ApidocElement.class.getName(), ";", "\n");
        if (interfaceInfo.isPageable()) {
            buf.put("UTF-8", "import ", AbstractResponsePage.class.getName(), ";", "\n");
        } else {
            buf.put("UTF-8", "import ", AbstractResponse.class.getName(), ";", "\n");
        }
        buf.put("UTF-8", "\n");
        buf.put("UTF-8", "import ", Serializable.class.getName(), ";", "\n");
        buf.put("UTF-8", "import ", List.class.getName(), ";", "\n");
        buf.put("UTF-8", "import ", ArrayList.class.getName(), ";", "\n");
        buf.put("UTF-8", "\n");
        buf.put("UTF-8", "import ", context.getDomainsPackage(), ".*;", "\n");
        buf.put("UTF-8", "\n");
        buf.put("UTF-8", "/**\n");
        buf.put("UTF-8", " * ", context.getCopyright(), " \n");
        buf.put("UTF-8", " */\n");
        Class recordsJavaClass = null;
        for (ElementInfo column : interfaceInfo.getResponse().getAllElements()) {
            if (column.isForm()) {
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                if (interfaceInfo.isPageable() && formElementInfo.getName().equals("records")) {
                    recordsJavaClass = formElementInfo.getBeanClass();
                }
            }
        }
        buf.put("UTF-8", "public class ", interfaceInfo.getResponseClass().getSimpleName(), (interfaceInfo.isPageable() ? " extends " + AbstractResponsePage.class.getSimpleName() + "<" + interfaceInfo.getResponseClass().getSimpleName() + "." + recordsJavaClass.getSimpleName() + ">" : " extends AbstractResponse"), " {\n");
        context.increaseDeep();
        for (ElementInfo column : interfaceInfo.getResponse().getElements()) {
            if (column.isValue()) {
                ValueElementInfo valueElementInfo = column.as(ValueElementInfo.class);
                if (valueElementInfo.isEnum()) {
                    buf.put("UTF-8", context.indent_n(), "/**", "\n");
                    for (String key : valueElementInfo.getEnums().keySet()) {
                        String desc = valueElementInfo.getEnums().get(key);
                        buf.put("UTF-8", context.indent_n(), " * ", key, " ", desc, "\n");
                    }
                    buf.put("UTF-8", context.indent_n(), " */", "\n");
                }
                buf.put("UTF-8", context.indent_n(), "@ApidocElement(value = \"" + valueElementInfo.getDesc() + "\", required = " + valueElementInfo.isRequired() + ", minLen = " + valueElementInfo.getMinLen(), ", maxLen = " + valueElementInfo.getMaxLen(), ")\n");
                if (valueElementInfo.isMultiple()) {
                    buf.put("UTF-8", context.indent_n(), "final List<", valueElementInfo.getJavaClass().getSimpleName(), "> ", valueElementInfo.getName(), " = new ArrayList();\n");
                } else {
                    buf.put("UTF-8", context.indent_n(), valueElementInfo.getJavaClass().getSimpleName(), " ", valueElementInfo.getName(), ";\n");
                }
                buf.put("UTF-8", "\n");
            } else if (column.isBean()) {
                BeanElementInfo beanElementInfo = column.as(BeanElementInfo.class);
                buf.put("UTF-8", context.indent_n(), "@ApidocElement(value = \"" + beanElementInfo.getDesc() + "\")\n");
                buf.put("UTF-8", context.indent_n(), beanElementInfo.getJavaClass().getSimpleName(), " ", beanElementInfo.getName(), ";\n");
                buf.put("UTF-8", "\n");
            } else if (column.isForm()) {
                if (interfaceInfo.isPageable() && column.getName().equals("records")) {
                    continue;
                }
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                buf.put("UTF-8", context.indent_n(), "@ApidocElement(value = \"" + formElementInfo.getDesc() + "\", required = " + formElementInfo.isRequired() + ")", "\n");
                buf.put("UTF-8", context.indent_n(), "final ", formElementInfo.getJavaClass().getSimpleName(), "<", formElementInfo.getBeanClass().getSimpleName(), ">", " ", formElementInfo.getName(), " = new ArrayList();\n");
                buf.put("UTF-8", "\n");
            }
        }

        for (ElementInfo column : interfaceInfo.getResponse().getElements()) {
            if (column.isValue()) {
                ValueElementInfo valueElementInfo = column.as(ValueElementInfo.class);
                if (valueElementInfo.isMultiple()) {
                    buf.put("UTF-8", context.indent_n(), "public ", "List<", valueElementInfo.getJavaClass().getSimpleName(), "> ", "get", StringUtils.firstCharToUpper(valueElementInfo.getName()), "() {\n");
                    buf.put("UTF-8", context.indent_n(), context.indent_1(), "return this.", valueElementInfo.getName(), ";\n");
                    buf.put("UTF-8", context.indent_n(), "}\n");
                    buf.put("UTF-8", "\n");
                } else {
                    buf.put("UTF-8", context.indent_n(), "public ", valueElementInfo.getJavaClass().getSimpleName(), " ", "get", StringUtils.firstCharToUpper(valueElementInfo.getName()), "() {\n");
                    buf.put("UTF-8", context.indent_n(), context.indent_1(), "return this.", valueElementInfo.getName(), ";\n");
                    buf.put("UTF-8", context.indent_n(), "}\n");
                    buf.put("UTF-8", "\n");
                    buf.put("UTF-8", context.indent_n(), "public ", "void ", "set", StringUtils.firstCharToUpper(valueElementInfo.getName()), "(", valueElementInfo.getJavaClass().getSimpleName(), " ", valueElementInfo.getName(), ") {\n");
                    buf.put("UTF-8", context.indent_n(), context.indent_1(), "this.", valueElementInfo.getName(), " = ", valueElementInfo.getName(), ";\n");
                    buf.put("UTF-8", context.indent_n(), "}\n");
                    buf.put("UTF-8", "\n");
                }
            } else if (column.isBean()) {
                BeanElementInfo beanElementInfo = column.as(BeanElementInfo.class);
                buf.put("UTF-8", context.indent_n(), "public ", beanElementInfo.getJavaClass().getSimpleName(), " ", "get", StringUtils.firstCharToUpper(beanElementInfo.getName()), "() {\n");
                buf.put("UTF-8", context.indent_n(), context.indent_1(), "return this.", beanElementInfo.getName(), ";\n");
                buf.put("UTF-8", context.indent_n(), "}\n");
                buf.put("UTF-8", "\n");
                buf.put("UTF-8", context.indent_n(), "public ", "void ", "set", StringUtils.firstCharToUpper(beanElementInfo.getName()), "(", beanElementInfo.getJavaClass().getSimpleName(), " ", beanElementInfo.getName(), ") {\n");
                buf.put("UTF-8", context.indent_n(), context.indent_1(), "this.", beanElementInfo.getName(), " = ", beanElementInfo.getName(), ";\n");
                buf.put("UTF-8", context.indent_n(), "}\n");
                buf.put("UTF-8", "\n");
            } else if (column.isForm()) {
                if (interfaceInfo.isPageable() && column.getName().equals("records")) {
                    continue;
                }
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                buf.put("UTF-8", context.indent_n(), "public ", formElementInfo.getJavaClass().getSimpleName(), "<", formElementInfo.getBeanClass().getSimpleName(), ">", " ", "get", StringUtils.firstCharToUpper(formElementInfo.getName()), "() {\n");
                buf.put("UTF-8", context.indent_n(), context.indent_1(), "return this.", formElementInfo.getName(), ";\n");
                buf.put("UTF-8", context.indent_n(), "}\n");
                buf.put("UTF-8", "\n");
            }
        }
        Set<Class> classes = new HashSet<Class>();
        for (ElementInfo column : interfaceInfo.getResponse().getElements()) {
            if (column.isBean()) {
                BeanElementInfo beanElementInfo = column.as(BeanElementInfo.class);
                if (classes.contains(beanElementInfo.getJavaClass())) {
                    continue;
                }
                classes.add(beanElementInfo.getJavaClass());
                generateValueObjectClass(context, buf, beanElementInfo);
            } else if (column.isForm()) {
                FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                if (classes.contains(formElementInfo.getBeanClass())) {
                    continue;
                }
                classes.add(formElementInfo.getBeanClass());
                generateFormObjectClass(context, buf, formElementInfo);
            }
        }
        if (interfaceInfo.isPageable()) {
            List<ElementInfo> columns = interfaceInfo.getResponse().getAllElements();
            for (ElementInfo column : columns) {
                if (column.isBean()) {

                } else if (column.isForm()) {
                    FormElementInfo formElementInfo = column.as(FormElementInfo.class);
                    if (classes.contains(formElementInfo.getBeanClass())) {
                        continue;
                    }
                    classes.add(formElementInfo.getBeanClass());
                    generateFormObjectClass(context, buf, formElementInfo);
                }
            }
        }
        context.decreaseDeep();
        buf.put("UTF-8", "}\n");
        InterfaceFileFormat fileFormat = new InterfaceFileFormat();
        fileFormat.setFilePath(context.getDomainsFilePath());
        fileFormat.setPackagePath(context.getDomainsPackage());
        fileFormat.setCode(buf.asString("UTF-8"));
        fileFormat.setFileSuffix("java");
        fileFormat.setFileName(interfaceInfo.getResponseClass().getSimpleName());
        context.addInterfaceFile(fileFormat);
    }
}
