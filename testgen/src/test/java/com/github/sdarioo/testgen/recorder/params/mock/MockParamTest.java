package com.github.sdarioo.testgen.recorder.params.mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.TestMethod;

public class MockParamTest
{
    
    @SuppressWarnings("nls")
    @Test
    public void testNestedMock()
    {
        IServiceProvider provider = new ServiceProvider();
        assertTrue(ProxyFactory.canProxy(IServiceProvider.class, provider));
        IServiceProvider proxy = (IServiceProvider)ProxyFactory.newProxy(IServiceProvider.class, provider);
        
        assertEquals(2, proxy.getService("A").length("xxx"));
        assertEquals(4, proxy.getService("B").length("xxx"));
        
        TestSuiteBuilder builder = new TestSuiteBuilder();
        
        MockParam param = new MockParam(proxy);
        String src = param.toSouceCode(IServiceProvider.class, builder);
        assertEquals("newIServiceProviderMock()", src);
        
        verifyHelperMethod(builder, 0, new String[] {
                "private static MockParamTest.IService newIServiceMock(String arg0, int length) {",
                "    MockParamTest.IService mock = Mockito.mock(MockParamTest.IService.class);",
                "    Mockito.when(mock.length(arg0)).thenReturn(length);",
                "    return mock;",
                "}"
        });
        
        verifyHelperMethod(builder, 1, new String[] {
                "private static MockParamTest.IServiceProvider newIServiceProviderMock() {",
                "    MockParamTest.IServiceProvider mock = Mockito.mock(MockParamTest.IServiceProvider.class);",
                "    Mockito.when(mock.getService(\"A\")).thenReturn(newIServiceMock(\"xxx\", 2));",
                "    Mockito.when(mock.getService(\"B\")).thenReturn(newIServiceMock(\"xxx\", 4));",
                "    return mock;",
                "}"
        });
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testNetstedCollectionOfMocks()
    {
        IServiceProvider provider = new ServiceProvider();
        assertTrue(ProxyFactory.canProxy(IServiceProvider.class, provider));
        IServiceProvider proxy = (IServiceProvider)ProxyFactory.newProxy(IServiceProvider.class, provider);
        List<IService> services = proxy.getServices();
        for (IService service : services) {
            service.length("xxx"); //$NON-NLS-1$
        }
        
        TestSuiteBuilder builder = new TestSuiteBuilder();
        MockParam param = new MockParam(proxy);
        
        boolean isSupported = param.isSupported(IServiceProvider.class, new HashSet<String>());
        assertTrue(isSupported);
        
        String src = param.toSouceCode(IServiceProvider.class, builder);
        
        assertEquals("newIServiceProviderMock(Arrays.<MockParamTest.IService>asList(newIServiceMock(\"xxx\", 2), newIServiceMock(\"xxx\", 4)))", src);
        
        verifyHelperMethod(builder, 0, new String[] {
                "private static MockParamTest.IServiceProvider newIServiceProviderMock(List<MockParamTest.IService> services) {",
                "    MockParamTest.IServiceProvider mock = Mockito.mock(MockParamTest.IServiceProvider.class);",
                "    Mockito.when(mock.getServices()).thenReturn(services);",
                "    return mock;",
                "}"
        });
        
        verifyHelperMethod(builder, 1, new String[] {
                "private static MockParamTest.IService newIServiceMock(String arg0, int length) {",
                "    MockParamTest.IService mock = Mockito.mock(MockParamTest.IService.class);",
                "    Mockito.when(mock.length(arg0)).thenReturn(length);",
                "    return mock;",
                "}"
        });
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testWildcardMockCollection()
    {
        IServiceProvider provider = new ServiceProvider();
        IServiceProvider proxy = (IServiceProvider)ProxyFactory.newProxy(IServiceProvider.class, provider);
        List<? extends IService> services = proxy.getServicesExt();
        
        TestSuiteBuilder builder = new TestSuiteBuilder();
        MockParam param = new MockParam(proxy);
        
        boolean isSupported = param.isSupported(IServiceProvider.class, new HashSet<String>());
        assertTrue(isSupported);
        
        String src = param.toSouceCode(IServiceProvider.class, builder);
        assertEquals("newIServiceProviderMock(Arrays.asList(newIServiceMock(), newIServiceMock()))", src);
        
        verifyHelperMethod(builder, 0, new String[] {
                "private static MockParamTest.IServiceProvider newIServiceProviderMock(List<? extends MockParamTest.IService> servicesExt) {",
                "    MockParamTest.IServiceProvider mock = Mockito.mock(MockParamTest.IServiceProvider.class);",
                "    Mockito.when(mock.getServicesExt()).thenReturn(servicesExt);",
                "    return mock;",
                "}"
        });
    }
    
    
    private static void verifyHelperMethod(TestSuiteBuilder builder, int index, String[] expectedLines)
    {
        List<TestMethod> methods = builder.getHelperMethods();
        assertTrue(methods.size() > index);
        String src = methods.get(index).toSourceCode();
        
        String[] lines = src.split("\\n"); //$NON-NLS-1$
        assertEquals(lines.length, expectedLines.length);
        for (int i = 0; i < lines.length; i++) {
            assertEquals(lines[i], expectedLines[i]);
        }
    }
    
    //-----------------------------------------------------------
    

    public static interface IServiceProvider
    {
        IService getService(String name);
        List<IService> getServices();
        List<? extends IService> getServicesExt();
    }
    public static interface IService
    {
        int length(String str);
    }
    
    private static class ServiceProvider
        implements IServiceProvider
    {
        @Override
        public IService getService(String name) {
            if ("A".equals(name)) 
                return new ServiceA();
            return new ServiceB();
        }

        @Override
        public List<IService> getServices() {
            return Arrays.asList(new ServiceA(), new ServiceB());
        }
        @Override
        public List<? extends IService> getServicesExt() {
            return getServices();
        }
    }
    
    private static class ServiceA implements IService
    {
        @Override
        public int length(String str) {
            return str.length() - 1;
        }
    }
    private static class ServiceB implements IService
    {
        @Override
        public int length(String str) {
            return str.length() + 1;
        }
    }
}
