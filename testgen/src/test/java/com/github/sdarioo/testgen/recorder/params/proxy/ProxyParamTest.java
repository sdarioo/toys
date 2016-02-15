package com.github.sdarioo.testgen.recorder.params.proxy;

import org.mockito.Mockito;

public class ProxyParamTest
{
    public void testMockito()
    {
        IProvider p = Mockito.mock(IProvider.class);
        Mockito.when(p.doSth("ala ma kota")).thenReturn("dome value");
    }
    
    public static interface IProvider
    {
        String doSth(String s);
    }
}
