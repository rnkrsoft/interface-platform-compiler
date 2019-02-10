package com.rnkrsoft.platform.demo;

import com.rnkrsoft.io.buffer.ByteBuf;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rnkrsoft.com on 2019/2/3.
 */
public class DemoTest {
    @Test
    public void test1() {
        String source = "Hello {{arg}}!";
        Template tmpl = Mustache.compiler().compile(source);
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("arg", "world");
        String s = tmpl.execute(context); // returns "Hello world!"
        System.out.println(s);
    }

    @Test
    public void test2() throws IOException {
        ByteBuf byteBuf = ByteBuf.allocate(1024).autoExpand(true);
        byteBuf.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("templates/android/service.java"));
        Template tmpl = Mustache.compiler().compile(byteBuf.getString("UTF-8", byteBuf.readableLength()));
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("copyright", "版权所有");
        context.put("domainPackage", "com.rnkrsoft.platform.demo.domain");
        context.put("servicePackage", "com.rnkrsoft.platform.demo.service");
        context.put("author", "rnkrsoft.com");
        context.put("serviceDesc", "测试服务");
        context.put("serviceName", "DemoService");
        context.put("channelName", "test-channel");
        context.put("android", "true");
        List<Map> interfaces = new ArrayList<Map>();
        {
            Map<String, Object> interface1 = new HashMap<String, Object>();
            interface1.put("interfaceDesc", "测试接口1");
            interface1.put("txNo", "001");
            interface1.put("interfaceVersion", "1");
            interface1.put("interfaceUsage", "测试");
            interface1.put("interfaceName", "hello");
            interface1.put("interfaceRequest", "Request1");
            interface1.put("interfaceResponse", "Response1");
            interfaces.add(interface1);
        }
        {
            Map<String, Object> interface1 = new HashMap<String, Object>();
            interface1.put("interfaceDesc", "测试接口2");
            interface1.put("txNo", "002");
            interface1.put("interfaceVersion", "1");
            interface1.put("interfaceUsage", "测试");
            interface1.put("interfaceName", "hello2");
            interface1.put("interfaceRequest", "Request2");
            interface1.put("interfaceResponse", "Response2");
            interfaces.add(interface1);
        }
        context.put("interfaces", interfaces);
        String s = tmpl.execute(context); // returns "Hello world!"
        System.out.println(s);
    }
}
