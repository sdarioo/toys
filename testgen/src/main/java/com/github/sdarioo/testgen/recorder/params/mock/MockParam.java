package com.github.sdarioo.testgen.recorder.params.mock;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.recorder.params.AbstractParam;

public class MockParam
    extends AbstractParam
{
    private final Object _proxy;
    
    public MockParam(Object proxy)
    {
        super(proxy.getClass());
        _proxy = proxy;
    }

    @Override
    public Class<?> getRecordedType() 
    {
        RecordingInvocationHandler handler = getHandler();
        return (handler != null) ? handler.getInterface() : super.getRecordedType();
    }
    
    @Override
    public boolean isSupported(Type targetType, Collection<String> errors) 
    {
        RecordingInvocationHandler handler = getHandler();
        if (handler == null) {
            errors.add("Unexpected proxy InvocationHandler."); //$NON-NLS-1$
            return false;
        }
        if (targetType == null) {
            errors.add("Mock prameter target type is unknown."); //$NON-NLS-1$
            return false;
        }
        
        Set<String> subErrors = new HashSet<>();
        
        if (!super.isAssignable(handler.getInterface(), targetType, subErrors)) {
            errors.add(createErrorMessage(subErrors));
            return false;
        }
        if (!handler.isSupported(subErrors)) {
            errors.add(createErrorMessage(subErrors));
            return false;
        }
        return true;
    }

    @Override
    public String toSouceCode(Type targetType, TestSuiteBuilder builder) 
    {
        builder.addImport("org.mockito.Mockito"); //$NON-NLS-1$
        
        String factoryMethodName = getFactoryMethodName(builder);
        return MockParamHelper.toSouceCode(getHandler(), factoryMethodName, builder);
    }
    
    public RecordingInvocationHandler getHandler()
    {
        InvocationHandler handler = Proxy.getInvocationHandler(_proxy);
        if (!(handler instanceof RecordingInvocationHandler)) {
            return null;
        }
        return (RecordingInvocationHandler)handler;
    }
    
    @SuppressWarnings("nls")
    private String getFactoryMethodName(TestSuiteBuilder builder)
    {
        String objectClass = builder.getTypeName(getRecordedType());
        int index = objectClass.lastIndexOf('.');
        if (index > 0) {
            objectClass = objectClass.substring(index + 1);
        }
        return "new" + objectClass + "Mock";
    }
    
    @SuppressWarnings("nls")
    private String createErrorMessage(Set<String> subErrors)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Problems while recording mock invocations for %s%n", getHandler().getInterface().getName()));
        for (String msg : subErrors) {
            sb.append(String.format("  - %s%n", msg));
        }
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MockParam other = (MockParam)obj;
        RecordingInvocationHandler myHandler = getHandler();
        RecordingInvocationHandler otherHandler = other.getHandler();
        if ((myHandler == null) || (otherHandler == null)) {
            return false;
        }
        return myHandler.getCalls().equals(otherHandler.getCalls());
    }
    
    @Override
    public int hashCode() 
    {
        RecordingInvocationHandler handler = getHandler();
        if (handler == null) {
            return 0;
        }
        return handler.getCalls().hashCode();
    }

}
