package com.github.sdarioo.testgen;

import com.github.sdarioo.testgen.App;
import org.junit.Assert;
import junitparams.JUnitParamsRunner;
import com.github.sdarioo.testgen.App.Person;
import org.junit.runner.RunWith;
import org.junit.Test;
import junitparams.Parameters;


@RunWith(JUnitParamsRunner.class)
public class AppTest
{

    private Object[] testIsAdult_Parameters() {
        return new Object[] {
            new Object[]{ newPerson(15, null), false },
            new Object[]{ newPerson(11, null), false },
            new Object[]{ newPerson(19, null), true },
            new Object[]{ newPerson(10, null), false },
            new Object[]{ newPerson(12, null), false },
            new Object[]{ newPerson(18, null), true },
            new Object[]{ newPerson(14, null), false },
            new Object[]{ newPerson(16, null), false },
            new Object[]{ newPerson(13, null), false },
            new Object[]{ newPerson(17, null), false }
        };
    }

    @Test
    @Parameters(method = "testIsAdult_Parameters")
    public void testIsAdult(App.Person arg0, boolean expected) throws Exception {
        boolean result=App.isAdult(arg0);
        Assert.assertEquals(expected, result);
    }

    private static App.Person newPerson(int age, java.lang.String name) {
        App.Person result = new App.Person();
        result.setAge(age);
        result.setName(name);
        return result;
    }

}
