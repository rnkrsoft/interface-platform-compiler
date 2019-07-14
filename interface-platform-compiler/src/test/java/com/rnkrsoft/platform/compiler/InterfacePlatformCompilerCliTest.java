package com.rnkrsoft.platform.compiler;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by woate on 2019/7/14.
 */
public class InterfacePlatformCompilerCliTest {

    @Test
    public void testCompile1() throws Exception {
        String[] serviceClasses = new String[]{};
        InterfacePlatformCompilerCli.generate(DeviceType.Android, "com.rnkrsoft.demo", "./target", "com.rnkrsoft.platform.service", serviceClasses);
    }


    @Test
    public void testCompile2() throws Exception {
        String[] serviceClasses = new String[]{
                "com.rnkrsoft.platform.service.DemoService",
        };
        InterfacePlatformCompilerCli.generate(DeviceType.Android, "com.rnkrsoft.demo", "./target", "com.rnkrsoft.platform.service", serviceClasses);

    }
}