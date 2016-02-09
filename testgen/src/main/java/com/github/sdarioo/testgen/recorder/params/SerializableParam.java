package com.github.sdarioo.testgen.recorder.params;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.SerializationUtils;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.ResourceFile;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.util.TypeUtils;

public class SerializableParam
    extends AbstractParam
{
    private final byte[] _bytes;
    
    public SerializableParam(Serializable value)
    {
        this(value, null);
    }
    
    public SerializableParam(Serializable value, Type paramType)
    {
        super(value.getClass(), paramType);
        _bytes = SerializationUtils.serialize(value);
    }

    @Override
    public boolean isSupported(Collection<String> errors) 
    {
        return true;
    }
    
    @Override
    public String toSouceCode(TestSuiteBuilder builder) 
    {
        builder.addImport("java.io.*"); //$NON-NLS-1$
        String template = getFactoryMethodTemplate(builder);
        
        String resName = ClassUtils.getShortCanonicalName(getRecordedType());
        
        ResourceFile resFile = builder.addResource(_bytes, resName);
        TestMethod deserialize = builder.addHelperMethod(template, "deserialize"); //$NON-NLS-1$
        
        return deserialize.getName() + "(\"" + resFile.getFileName() + "\")"; //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    private String getFactoryMethodTemplate(TestSuiteBuilder builder)
    {
        Type genericType = getType();
        String returnType = (genericType != null && !TypeUtils.containsTypeVariables(genericType)) ?
                TypeUtils.getName(genericType, builder) :
                builder.getTypeName(getRecordedType());
        
        String template = builder.getTemplatesCache().get(returnType);
        if (template == null) {
            template = DESERIALIZE_TEMPLATE_TEMPLATE.replace("<TYPE>", returnType); //$NON-NLS-1$
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
    public static final String DESERIALIZE_TEMPLATE_TEMPLATE =
    "private <TYPE> {0}(String res) throws Exception '{'\n" +
    "    InputStream in = getClass().getResourceAsStream(res);\n" +
    "    try '{'\n" +
    "        ObjectInputStream stream = new ObjectInputStream(in);\n" +
    "        return (<TYPE>)stream.readObject();\n" +
    "    '}' finally '{'\n" +
    "        in.close();\n" +
    "    '}'\n" +
    "}";
}
