package com.rnkrsoft.platform.compiler;

import lombok.Data;

import javax.web.doc.DocScanner;
import javax.web.doc.InterfaceInfo;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rnkrsoft.com on 2019/2/2.
 */
@Data
public class CompileContext {
    /**
     * 版权申明
     */
    String copyright = "copyright rnkrsoft.com";
    /**
     * 输出文件路径
     */
    String outputPath = "";
    /**
     * 输出文件名
     */
    String outputFileName = "gen";
    /**
     * 目标包路径
     */
    String targetPackage = "com.rnkrsoft.platform";
    /**
     * 接口信息对象
     */
    InterfaceInfo interfaceInfo;

    int valueObjectNestingDeep = 0;
    /**
     * 文档扫描器
     */
    DocScanner docScanner;
    /**
     * 是否打包成Zip包
     */
    boolean packZip = true;
    /**
     * 保存的包路径
     */
    String basePackage = "com.rnkrsoft.platform";
    /**
     * 文件保存路径
     */
    String baseFilePath = "";
    /**
     * domain包路径
     */
    String domainsPackage = "com.rnkrsoft.platform.domain";
    /**
     * 服务类包路径
     */
    String servicePackage = "com.rnkrsoft.platform.service";

    /**
     * domain文件路径
     */
    public String getDomainsFilePath() {
        return domainsPackage.replaceAll("\\.", "/");
    }

    public String getServiceFilePath() {
        return servicePackage.replaceAll("\\.", "/");
    }

    /**
     * 所有的接口文件格式
     */
    private final List<InterfaceFileFormat> fileFormats = new ArrayList();

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
        if (valueObjectNestingDeep > 4) {
            throw new UnsupportedOperationException("不支持的文件嵌套深度" + valueObjectNestingDeep);
        }
        return this;
    }

    public CompileContext decreaseDeep() {
        valueObjectNestingDeep--;
        return this;
    }
}
