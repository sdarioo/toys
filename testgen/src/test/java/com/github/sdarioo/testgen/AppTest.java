package com.github.sdarioo.testgen;

import java.util.Properties;
import java.lang.Double;
import java.lang.Short;
import org.junit.runner.RunWith;
import com.github.sdarioo.testgen.App;
import java.lang.Boolean;
import org.junit.Test;
import junitparams.JUnitParamsRunner;
import java.lang.String;
import org.junit.Assert;
import java.lang.Byte;
import junitparams.Parameters;


@RunWith(JUnitParamsRunner.class)
public class AppTest
{

    private Object[] testMain_Parameters() {
        return new Object[] {
            new Object[]{ new String[]{} }
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
            new Object[]{ (short)0, (byte)0, false, 0.0d },
            new Object[]{ (short)9, (byte)9, false, 18.0d },
            new Object[]{ (short)1, (byte)1, false, 2.0d },
            new Object[]{ (short)8, (byte)8, false, 16.0d },
            new Object[]{ (short)3, (byte)3, false, 6.0d },
            new Object[]{ (short)6, (byte)6, false, 12.0d },
            new Object[]{ (short)2, (byte)2, false, 4.0d },
            new Object[]{ (short)7, (byte)7, false, 14.0d },
            new Object[]{ (short)4, (byte)4, false, 8.0d },
            new Object[]{ (short)5, (byte)5, false, 10.0d }
        };
    }

    @Test
    @Parameters(method = "testAdd_Parameters")
    public void testAdd(Short arg0, Byte arg1, Boolean arg2, Double expected) throws Exception {
        Double result=App.add(arg0,arg1,arg2);
        Assert.assertEquals(expected, result);
    }


    private Object[] testTrim_Parameters() {
        return new Object[] {
            new Object[]{ "\nline\n", "line" }
        };
    }

    @Test
    @Parameters(method = "testTrim_Parameters")
    public void testTrim(String arg0, String expected) throws Exception {
        String result=App.trim(arg0);
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
