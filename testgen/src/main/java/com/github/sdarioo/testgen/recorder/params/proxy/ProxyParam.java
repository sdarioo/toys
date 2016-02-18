package com.github.sdarioo.testgen.recorder.params.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import com.github.sdarioo.testgen.generator.MethodBuilder;
import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.recorder.Call;
import com.github.sdarioo.testgen.recorder.IParameter;
import com.github.sdarioo.testgen.recorder.params.AbstractParam;
import com.github.sdarioo.testgen.util.TypeUtil;

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
        return (handler != null) ? handler.getType() : super.getRecordedType();
    }
    
    @Override
    public boolean isSupported(Type targetType, Collection<String> errors) 
    {
        RecordingInvocationHandler handler = getHandler();
        if (handler == null) {
            errors.add("Unexpected proxy InvocationHandler."); //$NON-NLS-1$
            return false;
        }
        if (!super.isAssignable(handler.getType(), targetType, errors)) {
            return false;
        }
        
        List<Call> calls = handler.getCalls();
        boolean bSupported = true;
        for (Call call : calls) {
            bSupported &= call.isSupported(errors);
        }
        return bSupported;
    }

    @Override
    public String toSouceCode(Type targetType, TestSuiteBuilder builder) 
    {
        builder.addImport("org.mockito.Mockito"); //$NON-NLS-1$
        
        String factoryMethodName = getFactoryMethodName(builder);
        String factoryMethodTemplate = getFactoryMethodTemplate(builder);
        TestMethod factoryMethod = builder.addHelperMethod(factoryMethodTemplate, factoryMethodName);
        return fmt("{0}()", factoryMethod.getName()); //$NON-NLS-1$
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
    private String getFactoryMethodTemplate(TestSuiteBuilder builder)
    {
        RecordingInvocationHandler handler = getHandler();
        Class<?> rawType = handler.getType();
        
        MethodBuilder methodBuilder = new MethodBuilder(builder);
        methodBuilder.modifier(Modifier.PRIVATE | Modifier.STATIC);
        methodBuilder.returnType(rawType);
        methodBuilder.name("###");
        
        methodBuilder.statement(fmt("{0} mock = Mockito.mock({0}.class)", TypeUtil.getName(rawType, builder)));
        
        
        for (Call call : handler.getCalls()) {
            Method method = call.getMethod();
            List<IParameter> args = call.args();
            Class<?>[] argTypes = method.getParameterTypes();
            
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.size(); i++) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(args.get(i).toSouceCode(argTypes[i], builder));
            }
            String result = call.getResult().toSouceCode(method.getReturnType(), builder);
            String when = fmt("Mockito.when(mock.{0}({1})).thenReturn({2})", method.getName(), sb.toString(), result);
            methodBuilder.statement(when);
        }
        
        methodBuilder.statement(fmt("return mock"));
        
        String template = methodBuilder.build();
        template = template.replace("{", "'{'");
        template = template.replace("}", "'}'");
        template = template.replace("###", "{0}");
        return template;
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
