package com.github.sdarioo.testgen.generator.source;

import com.github.sdarioo.testgen.util.StringUtil;

public class MethodTemplate 
{
    public final String _template;
    
    public MethodTemplate(String template)
    {
        _template = template;
    }
    
    public MethodTemplate(String[] templateLines)
    {
        this(StringUtil.join(templateLines, "\n"));
    }
    
    public String toString()
    {
        return _template;
    }
    
    public MethodTemplate withName(String name)
    {
        return with(NAME_VARIABLE, name);
    }
    
    public MethodTemplate with(String variable, String value)
    {
        String newTemplate = _template.replace(variable, value);
        return new MethodTemplate(newTemplate);
    }
    
    @Override
    public int hashCode() 
    {
        return _template.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (!(obj instanceof MethodTemplate)) {
            return false;
        }
        MethodTemplate other = (MethodTemplate)obj;
        return _template.equals(other._template);
    }

    public static final String NAME_VARIABLE = "${name}";
    public static final String TYPE_VARIABLE = "${type}";
}
