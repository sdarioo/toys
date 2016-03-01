package com.github.sdarioo.testgen.recorder.params.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

import com.github.sdarioo.testgen.generator.MethodBuilder;
import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.recorder.Call;
import com.github.sdarioo.testgen.recorder.IParameter;
import com.github.sdarioo.testgen.recorder.ArgNamesCache;
import com.github.sdarioo.testgen.recorder.params.AbstractParam;
import com.github.sdarioo.testgen.util.StringUtil;
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
        String factoryMethodTemplate = getFactoryMethodTemplate(builder);
        TestMethod factoryMethod = builder.addHelperMethod(factoryMethodTemplate, factoryMethodName);
        
        List<String> args = new ArrayList<String>();
        for (Call call : getHandler().getCalls()) {
            Method method = call.getMethod();
            List<IParameter> argValues = call.args();
            Class<?>[] argTypes = method.getParameterTypes();
            
            for (int i = 0; i < argTypes.length; i++) {
                args.add(argValues.get(i).toSouceCode(argTypes[i], builder));
            }
            args.add(call.getResult().toSouceCode(method.getReturnType(), builder));
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
    private String getFactoryMethodTemplate(TestSuiteBuilder builder)
    {
        RecordingInvocationHandler handler = getHandler();
        List<Call> calls = handler.getCalls();
        Class<?> rawType = handler.getInterface();
        
        List<Type> argTypes = new ArrayList<Type>();
        List<String> argNames = new ArrayList<String>();
        getArgTypesAndNames(calls, argTypes, argNames);
        
        MethodBuilder methodBuilder = new MethodBuilder(builder);
        methodBuilder.modifier(Modifier.PRIVATE | Modifier.STATIC).
            returnType(rawType).
            name("###").
            args(argTypes.toArray(new Type[0]), argNames.toArray(new String[0])).
            statement(fmt("{0} mock = Mockito.mock({0}.class)", TypeUtil.getName(rawType, builder)));
                
        int argIndex = 0;
        for (Call call : calls) {
            Method method = call.getMethod();
            int argCount = method.getParameterTypes().length;
            String args = StringUtil.join(subList(argNames, argIndex, argCount), ", ");
            argIndex += argCount;
            
            String when = fmt("Mockito.when(mock.{0}({1})).thenReturn({2})", method.getName(), args, argNames.get(argIndex));
            methodBuilder.statement(when);
            argIndex++;
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
    
    private static void getArgTypesAndNames(List<Call> calls, List<Type> typesHolder, List<String> namesHolder)
    {
        int uniqueIdx = 0;
        // TODO - check if all calls has different method
        // TODO - if params generic method also should be generic
        for (Call call : calls) {
            Method method = call.getMethod();
            Type[] types = method.getParameterTypes();
            String[] names = ArgNamesCache.getArgNames(method, true);
            
            typesHolder.addAll(Arrays.asList(types));
            namesHolder.addAll(Arrays.asList(names));
            
            Type returnType = method.getGenericReturnType();
            String returnArgName = methodNameToArgName(method.getName());
            
            typesHolder.add(returnType);
            namesHolder.add(returnArgName);
        }
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
    
    private static String methodNameToArgName(String methodName)
    {
        if (methodName.startsWith("get")) { //$NON-NLS-1$
            methodName = methodName.substring(3);
        }
        if (Character.isUpperCase(methodName.charAt(0))) {
            methodName = StringUtils.uncapitalize(methodName);
        }
        return methodName;
    }
    
    private static <T> T last(List<T> list)
    {
        return list.get(list.size() - 1);
    }
    
    private static <T> List<T> subList(List<T> list, int index, int length)
    {
        List<T> result = new ArrayList<T>();
        for (int i = index; i < (index + length); i++) {
            result.add(list.get(i));
        }
        return result;
    }

}
