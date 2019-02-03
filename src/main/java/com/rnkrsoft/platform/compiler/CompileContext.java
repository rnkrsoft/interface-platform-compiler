package com.rnkrsoft.platform.compiler;

import lombok.Data;

import javax.web.doc.InterfaceInfo;

/**
 * Created by rnkrsoft.com on 2019/2/2.
 */
@Data
public class CompileContext {
    /**
     * 输出文件路径
     */
    String outputPath;
    /**
     * 目标包路径
     */
    String targetPackage;
    /**
     * 接口信息对象
     */
    InterfaceInfo interfaceInfo;
}
