package com.rnkrsoft.platform.compiler;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by woate on 2019/2/23.
 */
public class CompileContextTest {

    @Test
    public void testGetTargetPackage() throws Exception {
        CompileContext compileContext = new CompileContext();
        compileContext.setTargetPackage(".com.demo.");
        System.out.println(compileContext.getTargetPackage());
    }
}