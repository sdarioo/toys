package com.github.sdarioo.testgen.recorder.params;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.SerializationUtils;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.ResourceFile;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.recorder.IParameter;

public class SerializableParam
    implements IParameter
{
    private final Class<?> _clazz;
    private final byte[] _bytes;
    
    public SerializableParam(Serializable value)
    {
        _clazz = value.getClass();
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
        
        String resName = ClassUtils.getShortCanonicalName(_clazz);
        
        ResourceFile resFile = builder.addResource(_bytes, resName);
        TestMethod deserialize = builder.addHelperMethod(template, "deserialize"); //$NON-NLS-1$
        
        return deserialize.getName() + "(\"" + resFile.getFileName() + "\")"; //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    private String getFactoryMethodTemplate(TestSuiteBuilder builder)
    {
        String template = builder.getTemplatesCache().get(_clazz);
        if (template == null) {
            String typeName = builder.getTypeName(_clazz);
            template = DESERIALIZE_TEMPLATE_TEMPLATE.replace("<TYPE>", typeName); //$NON-NLS-1$
            builder.getTemplatesCache().put(_clazz, template);
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
        return _clazz.equals(other._clazz) && Arrays.equals(_bytes, other._bytes);
    }
    
    @Override
    public int hashCode() 
    {
        return _clazz.hashCode() + 31 * Arrays.hashCode(_bytes);
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
