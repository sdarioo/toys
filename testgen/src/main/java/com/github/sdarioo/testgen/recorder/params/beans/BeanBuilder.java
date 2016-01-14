/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params.beans;

import java.util.*;

import org.objectweb.asm.commons.Method;

import com.github.sdarioo.testgen.logging.Logger;

public class BeanBuilder 
{
    private final List<Field> _fields = new ArrayList<Field>(); 
    private final List<Constructor> _constructors = new ArrayList<Constructor>();
    
    private final Map<Field, Method> _getters = new HashMap<Field, Method>();
    private final Map<Field, Method> _setters = new HashMap<Field, Method>();
    
    
    public void addField(Field field)
    {
        _fields.add(field);
    }
    
    public void addGetter(Field field, Method method)
    {
        Method getter = _getters.get(field);
        if (getter != null) {
            getter = chooseMethod(field.name, getter, method);
        } else {
            getter = method;
        }
        _getters.put(field, getter);
    }
    
    public void addSetter(Field field, Method method)
    {
        Method setter = _setters.get(field);
        if (setter != null) {
            setter = chooseMethod(field.name, setter, method);
        } else {
            setter = method;
        }
        _setters.put(field, setter);
    }
    
    public void addConstructor(Method method, List<Field> setFields)
    {
        Constructor constructor = new Constructor(method, setFields);
        _constructors.add(constructor);
    }
    
    public Field getField(String name, String desc)
    {
        for (Field field : _fields) {
            if (field.name.equals(name) && field.desc.equals(desc)) {
                return field;
            }
        }
        Logger.warn("Cannot find field: " + name + ' ' + desc); //$NON-NLS-1$
        return null;
    }
    
    public Bean newBean()
    {
        if (_constructors.isEmpty()) {
            _constructors.add(Constructor.DEFAULT);
        }
        
        Bean result = null;
        for (Constructor constructor : _constructors) {
            Set<Field> fields = new HashSet<Field>(_fields);
            fields.removeAll(constructor.setters);
            
            Map<Field, Method> setters = new HashMap<Field, Method>();
            for (Field field : fields) {
                Method setter = _setters.get(field);
                if (setter != null) {
                    setters.put(field, setter);
                }
            }
            if (fields.size() == setters.size()) {
                result = new Bean(_fields, constructor, _getters, setters);
                break;
            }
        }
        if (result == null) {
            result = Bean.UNSUPPORTED;
        }
        return result;
    }
    
    private static Method chooseMethod(String fieldName, Method... getters)
    {
        // TODO - better matching strategy
        for (Method method : getters) {
            if (method.getName().toLowerCase().endsWith(fieldName.toLowerCase())) {
                return method;
            }
        }
        return getters[0];
    }
    
}
