package com.github.sdarioo.testgen.recorder.params;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.SerializationUtils;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.MethodTemplate;
import com.github.sdarioo.testgen.generator.source.ResourceFile;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.util.TypeUtil;

public class SerializableParam
    extends AbstractParam
{
    private final byte[] _bytes;
    
    public SerializableParam(Serializable value)
    {
        super(value.getClass());
        _bytes = SerializationUtils.serialize(value);
    }

    @Override
    public boolean isSupported(Type targetType, Collection<String> errors) 
    {
        return true;
    }
    
    @Override
    public String toSouceCode(Type targetType, TestSuiteBuilder builder) 
    {
        builder.addImport("java.io.*"); //$NON-NLS-1$
        MethodTemplate template = getFactoryMethodTemplate(targetType, builder);
        
        String resName = ClassUtils.getShortCanonicalName(getRecordedType());
        
        ResourceFile resFile = builder.addResource(_bytes, resName);
        TestMethod deserialize = builder.addHelperMethod(template, "deserialize"); //$NON-NLS-1$
        
        return deserialize.getName() + "(\"" + resFile.getFileName() + "\")"; //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    private MethodTemplate getFactoryMethodTemplate(Type targetType, TestSuiteBuilder builder)
    {
        String returnType = TypeUtil.getName(targetType, builder);
        
        MethodTemplate template = builder.getTemplatesCache().get(returnType);
        if (template == null) {
            template = DESERIALIZE_TEMPLATE.with(MethodTemplate.TYPE_VARIABLE, returnType); //$NON-NLS-1$
            builder.getTemplatesCache().put(returnType, template);
        }
        return template;
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
        SerializableParam other = (SerializableParam)obj;
        return getRecordedType().equals(other.getRecordedType()) && Arrays.equals(_bytes, other._bytes);
    }
    
    @Override
    public int hashCode() 
    {
        return getRecordedType().hashCode() + 31 * Arrays.hashCode(_bytes);
    }
    
    @SuppressWarnings("nls")
    private static final MethodTemplate DESERIALIZE_TEMPLATE = new MethodTemplate(new String[] {
    "private ${type} ${name}(String res) throws Exception {",
    "    InputStream in = getClass().getResourceAsStream(res);",
    "    try {",
    "        ObjectInputStream stream = new ObjectInputStream(in);",
    "        return (${type})stream.readObject();",
    "    } finally {",
    "        in.close();",
    "    }",
    "}" });
}
