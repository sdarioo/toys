// AUTO-GENERATED
package com.github.sdarioo.testgen.generator.impl;

import com.github.sdarioo.testgen.generator.impl.MockApp;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(JUnitParamsRunner.class)
public class MockAppTest
{
    @Test
    @Parameters(method = "testFoo_Parameters")
    public void testFoo(MockApp.IFile file, MockApp.IContext context, int expected) throws Exception
    {
        int result = MockApp.foo(file, context);
        Assert.assertEquals(expected, result);
    }

    @SuppressWarnings("unused")
    private static Object[] testFoo_Parameters() throws Exception 
    {
        return new Object[] {
            new Object[]{ getIFileMock(), newIContextMock(getIFileMock(), 666), 666 }
        };
    }

    private static MockApp.IFile IFILE;
    private static MockApp.IFile getIFileMock() 
    {
        if (IFILE == null) {
            IFILE = Mockito.mock(MockApp.IFile.class);
            Mockito.when(IFILE.read()).thenReturn("text");
            Mockito.doReturn(null).when(IFILE).getLines();
        }
        return IFILE;
    }

    private static MockApp.IContext newIContextMock(MockApp.IFile arg0, int length) 
    {
        MockApp.IContext mock = Mockito.mock(MockApp.IContext.class);
        Mockito.when(mock.getLength(arg0)).thenReturn(length);
        return mock;
    }

}
