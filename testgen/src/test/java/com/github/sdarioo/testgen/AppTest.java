package com.github.sdarioo.testgen;

import java.util.Properties;
import org.junit.runner.RunWith;
import com.github.sdarioo.testgen.App;
import org.junit.Test;
import junitparams.JUnitParamsRunner;
import java.lang.String;
import org.junit.Assert;
import junitparams.Parameters;


@RunWith(JUnitParamsRunner.class)
public class AppTest
{
    private Object[] testMain_Parameters() {
        return new Object[] {
            new Object[]{ null }
        };
    }

    @Test
    @Parameters(method = "testMain_Parameters")
    public void testMain(String[] arg0) throws Exception {
        App.main(arg0);
    }

    private Object[] testConcat_Parameters() {
        return new Object[] {
            new Object[]{ props(pair("key-3", "value-3")), "key-3=value-3" },
            new Object[]{ props(pair("key-9", "value-9")), "key-9=value-9" },
            new Object[]{ props(pair("key-2", "value-2")), "key-2=value-2" },
            new Object[]{ props(pair("key-5", "value-5")), "key-5=value-5" },
            new Object[]{ props(pair("key-7", "value-7")), "key-7=value-7" },
            new Object[]{ props(pair("key-8", "value-8")), "key-8=value-8" },
            new Object[]{ props(pair("key-1", "value-1")), "key-1=value-1" },
            new Object[]{ props(pair("key-6", "value-6")), "key-6=value-6" },
            new Object[]{ props(pair("key-0", "value-0")), "key-0=value-0" },
            new Object[]{ props(pair("key-4", "value-4")), "key-4=value-4" }
        };
    }

    @Test
    @Parameters(method = "testConcat_Parameters")
    public void testConcat(Properties arg0, String expected) throws Exception {
        String result=App.concat(arg0);
        Assert.assertEquals(expected, result);
    }

    private Object[] testAdd_Parameters() {
        return new Object[] {
            new Object[]{ 9, 1, 10 },
            new Object[]{ 8, 2, 10 },
            new Object[]{ 7, 3, 10 },
            new Object[]{ 6, 4, 10 },
            new Object[]{ 0, 10, 10 },
            new Object[]{ 5, 5, 10 },
            new Object[]{ 4, 6, 10 },
            new Object[]{ 1, 9, 10 },
            new Object[]{ 3, 7, 10 },
            new Object[]{ 2, 8, 10 }
        };
    }

    @Test
    @Parameters(method = "testAdd_Parameters")
    public void testAdd(int arg0, int arg1, int expected) throws Exception {
        int result=App.add(arg0,arg1);
        Assert.assertEquals(expected, result);
    }

    private static String[] pair(String key, String value) {
        return new String[] { key, value};
    }

    private static java.util.Properties props(String[]... pairs) {
        java.util.Properties p = new java.util.Properties();
        for (String[] pair : pairs) {
            p.setProperty(pair[0], pair[1]);
        }
        return p;
    }

}
