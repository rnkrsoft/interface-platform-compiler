package com.rnkrsoft.platform.compiler;

import com.rnkrsoft.io.buffer.ByteBuf;
import com.rnkrsoft.message.MessageFormatter;

import javax.web.doc.DocScanner;
import javax.web.doc.DocScannerFactory;
import javax.web.doc.InterfaceInfo;
import javax.web.doc.ServiceInfo;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by rnkrsoft.com on 2019/7/14.
 * 接口平台SQL生成器
 */
public class InterfacePlatformSQLGenerator {
    /**
     * 生成SQL语句
     * @param basePackages
     * @throws IOException
     */
    public static void generate(File fileName, String... basePackages) throws IOException {
        ByteBuf byteBuf = ByteBuf.allocate(1024).autoExpand(true);
        DocScanner docScanner = DocScannerFactory.newInstance();
        docScanner.addScanPackage(basePackages);
        docScanner.init(true);
        List<ServiceInfo> services = docScanner.listService();
        for (ServiceInfo serviceInfo : services) {
            for (InterfaceInfo interfaceInfo : serviceInfo.getInterfaces()){
                byteBuf.putUTF8(generateInterfaceSQL(interfaceInfo)).putUTF8(";\n");
            }
        }
        int i = byteBuf.write(fileName.getCanonicalPath());
    }

    static String generateInterfaceSQL(InterfaceInfo interfaceInfo) {
        String format = "insert  into tb_interface_definition" +
                //0,1,2
                "(serial_no," +
                //3
                "tx_no," +
                //4
                "version," +
                //5
                "channel," +
                //6
                "interface_desc," +
                //7
                "interface_type," +
                //8
                "interface_direction," +
                //9
                "gateway_url," +
                //10
                "http_timeout_second," +
                //11
                "service_class_name," +
                //12
                "method_name," +
                //13
                "encrypt_algorithm," +
                //14
                "decrypt_algorithm," +
                //15
                "sign_algorithm," +
                //16
                "verify_algorithm," +
                //17
                "use_token_as_password," +
                //18
                "password," +
                //19
                "first_sign_second_encrypt," +
                //20
                "first_verify_second_decrypt," +
                //21
                "validate_token," +
                //22
                "idempotent_redo," +
                //23
                "write_message," +
                //24
                "write_mode," +
                //25
                "key_vector," +
                //26
                "create_date," +
                //27
                "last_update_date) " +
                "values ('{}:{}:{}', " +
                //3
                "'{}'," +
                //4
                "'{}'," +
                //5
                "'{}'," +
                //6
                "'{}'," +
                //7
                "'{}'," +
                //8
                "'{}'," +
                //9
                "'{}'," +
                //10
                "'{}'," +
                //11
                "'{}'," +
                //12
                "'{}'," +
                //13
                "'{}'," +
                //14
                "'{}'," +
                //15
                "'{}'," +
                //16
                "'{}'," +
                //17
                "{}," +
                //18
                "'{}'," +
                //19
                "{}," +
                //20
                "{}," +
                //21
                "{}," +
                //22
                "{}," +
                //23
                "{}," +
                //24
                "'{}'," +
                //25
                "'{}', " +
                //26
                "{}, " +
                //27
                "{})";
        String[] args = new String[28];
        args[0] = interfaceInfo.getServiceInfo().getChannel();
        args[1] = interfaceInfo.getName();
        args[2] = interfaceInfo.getVersion();
        args[3] = interfaceInfo.getName();
        args[4] = interfaceInfo.getVersion();
        args[5] = interfaceInfo.getServiceInfo().getChannel();
        args[6] = interfaceInfo.getDesc();
        args[7] = "SYNC";
        args[8] = "INNER";
        args[9] = "";
        args[10] = "20";
        args[11] = interfaceInfo.getServiceInfo().getServiceClassName();
        args[12] = interfaceInfo.getName();
        args[13] = "AES";
        args[14] = "AES";
        args[15] = "SHA512";
        args[16] = "SHA512";
        args[17] = "0";
        args[18] = "1234567890654321";
        args[19] = "1";
        args[20] = "1";
        args[21] = "0";
        args[22] = "0";
        args[23] = "1";
        args[24] = "SYNC";
        args[25] = "1234567890654321";
        args[26] = "current_timestamp()";
        args[27] = "current_timestamp()";
        return MessageFormatter.format(format, args);
    }
}
