// AUTO-GENERATED
package com.github.sdarioo.testgen.generator.impl;

import com.github.sdarioo.testgen.generator.impl.GeneratorApp;
import java.lang.String;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(JUnitParamsRunner.class)
public class GeneratorAppTest
{
    @Test
    @Parameters(method = "testMain_Parameters")
    public void testMain(String[] args) throws Exception {
        GeneratorApp.main(args);
    }

    @Test
    @Parameters(method = "testConcat_Parameters")
    public void testConcat(GeneratorApp.StringList list, String expected) throws Exception {
        String result = GeneratorApp.concat(list);
        Assert.assertEquals(expected, result);
    }

    @Test
    @Parameters(method = "testSort_Parameters")
    public void testSort(List list, List expected) throws Exception {
        List result = GeneratorApp.sort(list);
        Assert.assertEquals(expected, result);
    }

    @Test
    @Parameters(method = "testCountGeneric_Parameters")
    public void testCountGeneric(List list, int expected) throws Exception {
        int result = GeneratorApp.countGeneric(list);
        Assert.assertEquals(expected, result);
    }

    @Test
    @Parameters(method = "testCount_Parameters")
    public void testCount(List<GeneratorApp.Pair<String>> list, String expected) throws Exception {
        String result = GeneratorApp.count(list);
        Assert.assertEquals(expected, result);
    }

    private Object[] testMain_Parameters() throws Exception {
        return new Object[] {
            new Object[]{ new String[]{} },
        };
    }

    private Object[] testConcat_Parameters() throws Exception {
        return new Object[] {
            new Object[]{ newStringList(Arrays.<String>asList("x", "y", "z")), "" },
        };
    }

    private Object[] testSort_Parameters() throws Exception {
        return new Object[] {
            new Object[]{ Arrays.asList("c", "b", "a"), Arrays.asList("a", "b", "c") },
        };
    }

    private Object[] testCountGeneric_Parameters() throws Exception {
        return new Object[] {
            new Object[]{ Arrays.asList(newPair("a", "b"), newPair("x", "y")), 0 },
        };
    }

    private Object[] testCount_Parameters() throws Exception {
        return new Object[] {
            new Object[]{ Arrays.<GeneratorApp.Pair<String>>asList(newPair2("a", "b"), newPair2("x", "y")), "2" },
        };
    }

    private static GeneratorApp.StringList newStringList(List<String> list) {
        GeneratorApp.StringList result = new GeneratorApp.StringList(list);
        return result;
    }

    private static GeneratorApp.Pair newPair(String x, String y) {
        GeneratorApp.Pair result = new GeneratorApp.Pair(x, y);
        return result;
    }

    private static GeneratorApp.Pair<String> newPair2(String x, String y) {
        GeneratorApp.Pair<String> result = new GeneratorApp.Pair<String>(x, y);
        return result;
    }

}
