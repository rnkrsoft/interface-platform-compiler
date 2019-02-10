package com.rnkrsoft.platform.demo;

import com.rnkrsoft.io.buffer.ByteBuf;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rnkrsoft.com on 2019/2/3.
 */
public class Demo1Test {
    @Test
    public void test1() throws IOException {
        ByteBuf byteBuf = ByteBuf.allocate(1024).autoExpand(true);
        byteBuf.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("templates/android/request.java"));
        Template tmpl = Mustache.compiler().compile(byteBuf.getString("UTF-8", byteBuf.readableLength()));
        Map<String, Object> context = new HashMap<String, Object>();
        System.out.println(tmpl.execute(context));
    }
}
