package com.rnkrsoft.platform.compiler;

import lombok.Data;
import lombok.ToString;

/**
 * Created by rnkrsoft.com on 2018/7/11.
 * 接口文件文件格式
 */
@Data
@ToString
public class InterfaceFileFormat {
    /**
     * 包路径
     */
    String packagePath;
    /**
     * 文件路径
     */
    String filePath;
    /**
     * 文件名
     */
    String fileName;
    /**
     * 文件后缀
     */
    String fileSuffix;
    /**
     * 代码
     */
    String code;
}
