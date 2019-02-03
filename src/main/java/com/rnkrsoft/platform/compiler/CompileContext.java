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
     * ��Ȩ����
     */
    String copyright = "copyright rnkrsoft.com";
    /**
     * ����ļ�·��
     */
    String outputPath = "";
    /**
     * ����ļ���
     */
    String outputFileName = "gen";
    /**
     * Ŀ���·��
     */
    String targetPackage = "com.rnkrsoft.platform";
    /**
     * �ӿ���Ϣ����
     */
    InterfaceInfo interfaceInfo;

    int valueObjectNestingDeep = 0;
    /**
     * �ĵ�ɨ����
     */
    DocScanner docScanner;
    /**
     * �Ƿ�����Zip��
     */
    boolean packZip = true;
    /**
     * ����İ�·��
     */
    String basePackage = "com.rnkrsoft.platform";
    /**
     * domain��·��
     */
    String domainsName = "domain";
    /**
     * �������·��
     */
    String serviceName = "service";
    public String getDomainsPackage() {
        return basePackage + "." + domainsName;
    }

    public String getServicePackage() {
        return basePackage + "." + serviceName;
    }

    /**
     * domain�ļ�·��
     */
    public String getDomainsFilePath() {
        return getDomainsPackage().replaceAll("\\.", "/");
    }

    public String getServiceFilePath() {
        return getServicePackage().replaceAll("\\.", "/");
    }

    /**
     * ���еĽӿ��ļ���ʽ
     */
    private final List<InterfaceFileFormat> fileFormats = new ArrayList();

    /**
     * ����һ���ӿ��ļ�
     *
     * @param fileFormat �ļ���ʽ
     */
    public void addInterfaceFile(InterfaceFileFormat fileFormat) {
        this.fileFormats.add(fileFormat);
    }

    public CompileContext increaseDeep() {
        valueObjectNestingDeep++;
        if (valueObjectNestingDeep > 4) {
            throw new UnsupportedOperationException("��֧�ֵ��ļ�Ƕ�����" + valueObjectNestingDeep);
        }
        return this;
    }

    public CompileContext decreaseDeep() {
        valueObjectNestingDeep--;
        return this;
    }
}
