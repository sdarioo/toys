package com.github.sdarioo.testgen.recorder.params.proxy;

public class ProxyParamTest
{
    
    public void testMockPram()
    {
    
    }
    
    
    public static int getLength(IServiceProvider provider, String name, String str)
    {
        IService service = provider.getService(name);
        return service.length(str);
    }
    
    public static interface IServiceProvider
    {
        IService getService(String name);
    }
    private static interface IService
    {
        int length(String str);
    }
}
