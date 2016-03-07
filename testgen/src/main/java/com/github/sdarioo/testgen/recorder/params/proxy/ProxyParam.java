package com.github.sdarioo.testgen.recorder.params.proxy;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.recorder.Call;
import com.github.sdarioo.testgen.recorder.IParameter;
import com.github.sdarioo.testgen.recorder.params.AbstractParam;
import com.github.sdarioo.testgen.util.StringUtil;

public class ProxyParam
    extends AbstractParam
{
    private final Object _proxy;
    
    public ProxyParam(Object proxy)
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
        if (!super.isAssignable(handler.getInterface(), targetType, errors)) {
            return false;
        }
        return handler.isSupported(errors);
    }

    @Override
    public String toSouceCode(Type targetType, TestSuiteBuilder builder) 
    {
        builder.addImport("org.mockito.Mockito"); //$NON-NLS-1$
        
        String factoryMethodName = getFactoryMethodName(builder);
        String factoryMethodTemplate = ProxyParamSourceCode.getFactoryMethodTemplate(getHandler(), builder);
        TestMethod factoryMethod = builder.addHelperMethod(factoryMethodTemplate, factoryMethodName);
        
        List<String> args = new ArrayList<String>();
        for (Call call : getHandler().getCalls()) {
            Method method = call.getMethod();
            if (ProxyParamSourceCode.isFactoryMethodWithArgs(getHandler())) {
                Class<?>[] types = method.getParameterTypes();
                List<IParameter> values = call.args();
                for (int i = 0; i < types.length; i++) {
                    args.add(values.get(i).toSouceCode(types[i], builder));
                }
                args.add(call.getResult().toSouceCode(method.getReturnType(), builder));    
            }
        }
        return fmt("{0}({1})", factoryMethod.getName(), StringUtil.join(args, ", ")); //$NON-NLS-1$ //$NON-NLS-2$
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
        ProxyParam other = (ProxyParam)obj;
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
