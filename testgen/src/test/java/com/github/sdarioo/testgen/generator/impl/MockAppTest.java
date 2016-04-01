// AUTO-GENERATED
package com.github.sdarioo.testgen.generator.impl;

import com.github.sdarioo.testgen.generator.impl.MockApp;
import java.util.List;
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
    public void testFoo(MockApp.IFile file, MockApp.IContext context, int expected) throws Exception {
        int result = MockApp.foo(file, context);
        Assert.assertEquals(expected, result);
    }

    @SuppressWarnings("unused")
    private static Object[] testFoo_Parameters() throws Exception {
        return new Object[] {
            new Object[]{ newIFileMock("text", null), newIContextMock(newIFileMock("text", null), 666), 666 }
        };
    }

    private static MockApp.IFile newIFileMock(String read, List<? extends List> lines) {
        MockApp.IFile mock = Mockito.mock(MockApp.IFile.class);
        Mockito.when(mock.read()).thenReturn(read);
        Mockito.doReturn(lines).when(mock).getLines();
        return mock;
    }

    private static MockApp.IContext newIContextMock(MockApp.IFile arg0, int length) {
        MockApp.IContext mock = Mockito.mock(MockApp.IContext.class);
        Mockito.when(mock.getLength(arg0)).thenReturn(length);
        return mock;
    }

}
