package com.github.sdarioo.testgen;

import com.github.sdarioo.testgen.App;
import com.github.sdarioo.testgen.App.Person;
import java.lang.Boolean;
import java.lang.Byte;
import java.lang.Double;
import java.lang.Short;
import java.lang.String;
import java.util.Properties;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(JUnitParamsRunner.class)
public class AppTest
{
    @Test
    @Parameters(method = "testIsAdult_Parameters")
    public void testIsAdult(Person[] p, boolean expected) throws Exception {
        boolean result=App.isAdult(p);
        Assert.assertEquals(expected, result);
    }

    @Test
    @Parameters(method = "testConcat_Parameters")
    public void testConcat(Properties props, String expected) throws Exception {
        String result=App.concat(props);
        Assert.assertEquals(expected, result);
    }

    @Test
    @Parameters(method = "testAdd_Parameters")
    public void testAdd(Short x, Byte y, Boolean b, Double expected) throws Exception {
        Double result=App.add(x,y,b);
        Assert.assertEquals(expected, result);
    }

    @Test
    @Parameters(method = "testTrim_Parameters")
    public void testTrim(String text, String expected) throws Exception {
        String result=App.trim(text);
        Assert.assertEquals(expected, result);
    }

    @Test
    @Parameters(method = "testMain_Parameters")
    public void testMain(String[] args) throws Exception {
        App.main(args);
    }


    private Object[] testIsAdult_Parameters() {
        return new Object[] {
            new Object[]{ new Person[]{newPerson(16, null)}, true },
            new Object[]{ new Person[]{newPerson(17, null)}, true },
            new Object[]{ new Person[]{newPerson(13, null)}, true },
            new Object[]{ new Person[]{newPerson(18, null)}, true },
            new Object[]{ new Person[]{newPerson(11, null)}, true },
            new Object[]{ new Person[]{newPerson(10, null)}, true },
            new Object[]{ new Person[]{newPerson(19, null)}, true },
            new Object[]{ new Person[]{newPerson(12, null)}, true },
            new Object[]{ new Person[]{newPerson(14, null)}, true },
            new Object[]{ new Person[]{newPerson(15, null)}, true }
        };
    }


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


    private Object[] testAdd_Parameters() {
        return new Object[] {
            new Object[]{ (short)9, (byte)9, false, 18.0d },
            new Object[]{ (short)8, (byte)8, false, 16.0d },
            new Object[]{ (short)6, (byte)6, false, 12.0d },
            new Object[]{ (short)3, (byte)3, false, 6.0d },
            new Object[]{ (short)2, (byte)2, false, 4.0d },
            new Object[]{ (short)1, (byte)1, false, 2.0d },
            new Object[]{ (short)5, (byte)5, false, 10.0d },
            new Object[]{ (short)4, (byte)4, false, 8.0d },
            new Object[]{ (short)7, (byte)7, false, 14.0d },
            new Object[]{ (short)0, (byte)0, false, 0.0d }
        };
    }


    private Object[] testTrim_Parameters() {
        return new Object[] {
            new Object[]{ "\nline\n", "line" }
        };
    }


    private Object[] testMain_Parameters() {
        return new Object[] {
            new Object[]{ new String[]{} }
        };
    }

    private static Person newPerson(int age, String name) {
        Person result = new Person();
        result.setAge(age);
        result.setName(name);
        return result;
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
