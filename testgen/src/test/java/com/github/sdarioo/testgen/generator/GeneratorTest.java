package com.github.sdarioo.testgen.generator;

import org.junit.Assert;
import java.util.Properties;
import com.github.sdarioo.testgen.generator.Generator;
import junitparams.JUnitParamsRunner;
import java.lang.String;
import org.junit.runner.RunWith;
import org.junit.Test;
import junitparams.Parameters;


@RunWith(JUnitParamsRunner.class)
public class GeneratorTest
{
    private Object[] testConcat_Parameters() {
        return new Object[] {
            new Object[]{ props(pair("key-7", "value-7")), "key-7=value-7" },
            new Object[]{ props(pair("key-3", "value-3")), "key-3=value-3" },
            new Object[]{ props(pair("key-2", "value-2")), "key-2=value-2" },
            new Object[]{ props(pair("key-6", "value-6")), "key-6=value-6" },
            new Object[]{ props(pair("key-5", "value-5")), "key-5=value-5" },
            new Object[]{ props(pair("key-8", "value-8")), "key-8=value-8" },
            new Object[]{ props(pair("key-1", "value-1")), "key-1=value-1" },
            new Object[]{ props(pair("key-0", "value-0")), "key-0=value-0" },
            new Object[]{ props(pair("key-9", "value-9")), "key-9=value-9" },
            new Object[]{ props(pair("key-4", "value-4")), "key-4=value-4" }
        };
    }

    @Test
    @Parameters(method = "testConcat_Parameters")
    public void testConcat(Properties arg0, String expected) throws Exception {
        String result=Generator.concat(arg0);
        Assert.assertEquals(expected, result);
    }

    private static java.util.Properties props(String[]... pairs) {
        java.util.Properties p = new java.util.Properties();
        for (String[] pair : pairs) {
            p.setProperty(pair[0], pair[1]);
        }
        return p;
    }

    private static String[] pair(String key, String value) {
        return new String[] { key, value};
    }

}
