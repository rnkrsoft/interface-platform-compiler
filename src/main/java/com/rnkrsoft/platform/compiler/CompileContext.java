package com.rnkrsoft.platform.compiler;

import lombok.Data;

import javax.web.doc.DocScanner;
import javax.web.doc.InterfaceInfo;
import java.util.ArrayList;
import java.util.List;

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
     * ����ļ���
     */
    String outputFileName;
    /**
     * Ŀ���·��
     */
    String targetPackage;
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
     * �ļ�����·��
     */
    String baseFilePath;
    /**
     * �Ƿ�����Zip��
     */
    boolean packZip;
    /**
     * ����İ�·��
     */
    String basePackage;
    /**
     * domain��·��
     */
    String domainsPackage;
    /**
     * ������·��
     */
    String constantsPackage;
    /**
     * �������·��
     */
    String servicePackage;
    /**
     * domain�ļ�·��
     */
    String domainsFilePath;
    /**
     * �����ļ�·��
     */
    String serviceFilePath;
    /**
     * ���еĽӿ��ļ���ʽ
     */
    private final List<InterfaceFileFormat> fileFormats = new ArrayList();

    /**
     * ����һ���ӿ��ļ�
     * @param fileFormat �ļ���ʽ
     */
    public void addInterfaceFile(InterfaceFileFormat fileFormat){
        this.fileFormats.add(fileFormat);
    }

    public CompileContext increaseDeep(){
        valueObjectNestingDeep++;
        if (valueObjectNestingDeep > 4){
            throw new UnsupportedOperationException("��֧�ֵ��ļ�Ƕ�����" + valueObjectNestingDeep);
        }
        return this;
    }

    public CompileContext decreaseDeep(){
        valueObjectNestingDeep--;
        return this;
    }
}
