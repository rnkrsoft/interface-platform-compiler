package com.rnkrsoft.platform.compiler.format;

import com.rnkrsoft.io.buffer.ByteBuf;
import lombok.Data;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 压缩
 */
@Data
public class ZipInterfaceFormat {
    String zipPath;
    String zipFileName;
    OutputStream os;
    final Map<String, String> files = new HashMap();

    public ZipInterfaceFormat(String zipPath, String zipFileName) {
        this.zipPath = zipPath;
        this.zipFileName = zipFileName;
    }


    public ZipInterfaceFormat(OutputStream os) {
        this.os = os;
    }

    /**
     * 增加文件
     *
     * @param fileName
     * @param data
     * @return
     */
    public ZipInterfaceFormat add(String fileName, String data) {
        files.put(fileName, data);
        return this;
    }

    /**
     * 保存文件
     *
     * @throws IOException
     */
    public void save() throws IOException {
        OutputStream os = null;
        if (zipPath != null && zipFileName != null) {
            os = new FileOutputStream(new File(zipPath, zipFileName));
        } else if (this.os != null) {
            os = this.os;
        }
        CheckedOutputStream cos = new CheckedOutputStream(os, new CRC32());
        ZipOutputStream zos = new ZipOutputStream(cos, Charset.forName("UTF-8"));
        for (String fileName : files.keySet()) {
            String data = files.get(fileName);
            ByteBuf byteBuf = ByteBuf.allocate(data.getBytes("UTF-8")).autoExpand(true);
            ZipEntry entry = new ZipEntry(fileName);
            zos.putNextEntry(entry);
            byteBuf.write(zos);
            zos.closeEntry();
        }
        zos.setComment("Interface Platform Client,create by Interface-Platform-Generator! ");
        zos.finish();
        zos.close();
    }
}