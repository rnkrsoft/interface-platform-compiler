package com.rnkrsoft.platform.compiler;

import lombok.Data;

import javax.web.doc.InterfaceInfo;

/**
 * Created by rnkrsoft.com on 2019/2/2.
 */
@Data
public class CompileContext {
    /**
     * ����ļ�·��
     */
    String outputPath;
    /**
     * Ŀ���·��
     */
    String targetPackage;
    /**
     * �ӿ���Ϣ����
     */
    InterfaceInfo interfaceInfo;
}
