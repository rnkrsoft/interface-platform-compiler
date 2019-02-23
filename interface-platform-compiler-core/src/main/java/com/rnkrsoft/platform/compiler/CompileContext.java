package com.rnkrsoft.platform.compiler;

import com.rnkrsoft.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;

import javax.web.doc.DocScanner;
import javax.web.doc.InterfaceInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rnkrsoft.com on 2019/2/2.
 * 编译上下文
 */
public class CompileContext {
    /**
     * 版权申明
     */
    @Getter
    String copyright = "copyright rnkrsoft.com";
    /**
     * 输出文件路径
     */
    @Getter
    @Setter
    String outputPath = "";
    /**
     * 输出文件名
     */
    @Getter
    @Setter
    String outputFileName = "gen";
    /**
     * 目标包路径
     */
    @Setter
    String targetPackage = "com.rnkrsoft.platform";
    /**
     * 接口信息对象
     */
    @Getter
    @Setter
    InterfaceInfo interfaceInfo;

    int valueObjectNestingDeep = 0;
    /**
     * 文档扫描器
     */
    @Getter
    @Setter
    DocScanner docScanner;
    /**
     * 是否打包成Zip包
     */
    @Getter
    @Setter
    boolean packZip = true;
    /**
     * 是否缩写
     */
    @Getter
    @Setter
    boolean shortName = false;
    /**
     * domain包路径
     */
    @Getter
    @Setter
    String domainsName = "domain";
    /**
     * 服务类包路径
     */
    @Getter
    @Setter
    String serviceName = "service";

    public void setCopyright(String copyright) {
        if (copyright == null){
            return;
        }
        this.copyright = copyright;
    }

    @Getter
    final List<String> includeServices = new ArrayList<String>();
    public String getDomainsPackage() {
        return getDomainsPackage(true);
    }
    public String getDomainsPackage(boolean dot) {
        String name = (targetPackage.endsWith(".") ? targetPackage: targetPackage + ".") + domainsName;
        if (isShortName()) {
            return StringUtils.shortPackageName(name, 3, true, dot ? "." : "");
        }else {
            return name;
        }
    }
    public String getServicePackage() {
        return getServicePackage(true);
    }
    public String getServicePackage(boolean dot) {
        String name = (targetPackage.endsWith(".") ? targetPackage: targetPackage + ".") + serviceName;
        if (isShortName()) {
            return StringUtils.shortPackageName(name, 3, true, dot ? "." : "");
        }else {
            return name;
        }
    }

    /**
     * domain文件路径
     */
    public String getDomainsFilePath() {
        return getDomainsPackage(true).replaceAll("\\.", "/");
    }

    public String getServiceFilePath() {
        return getServicePackage(true).replaceAll("\\.", "/");
    }

    /**
     * 所有的接口文件格式
     */
    @Getter
    private final List<InterfaceFileFormat> fileFormats = new ArrayList();

    public String getTargetPackage() {
        if (targetPackage == null){
            targetPackage = "";
        }
        if (targetPackage.startsWith(".")){
            targetPackage = targetPackage.substring(1);
        }
        if (targetPackage.endsWith(".")){
            targetPackage = targetPackage.substring(0, targetPackage.length() - 1);
        }
        return targetPackage;
    }

    /**
     * 增加一个接口文件
     *
     * @param fileFormat 文件格式
     */
    public void addInterfaceFile(InterfaceFileFormat fileFormat) {
        this.fileFormats.add(fileFormat);
    }

    public CompileContext increaseDeep() {
        valueObjectNestingDeep++;
        if (valueObjectNestingDeep > 10) {
            throw new UnsupportedOperationException("不支持的文件嵌套深度" + valueObjectNestingDeep);
        }
        return this;
    }

    public CompileContext decreaseDeep() {
        valueObjectNestingDeep--;
        return this;
    }

    public String indent_1() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("    ");
        return buffer.toString();
    }

    public String indent_n() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < valueObjectNestingDeep; i++) {
            buffer.append("    ");
        }
        return buffer.toString();
    }
}
