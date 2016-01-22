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
    
    /**
     * @return non null if all fields can be set using constructor + setter + accessible field
     */
    public Bean build()
    {
        if (_constructors.isEmpty()) {
            _constructors.add(Constructor.DEFAULT);
        }
        
        Bean result = null;
        for (Constructor constructor : _constructors) {
            
            Set<Field> fieldsToSet = new HashSet<Field>(_fields);
            // Constructor
            fieldsToSet.removeAll(constructor.setters);
            
            // Setters + accessible fields
            List<Field> fieldsWithSetter = new ArrayList<Field>();
            for (Field field : fieldsToSet) {
                Method setter = _setters.get(field);
                if (setter != null) {
                    fieldsWithSetter.add(field);
                } else if (!field.isPrivate()) {
                    fieldsWithSetter.add(field);
                }
            }
            fieldsToSet.removeAll(fieldsWithSetter);
            
            if (fieldsToSet.isEmpty()) {
                result = new Bean(_fields, constructor, _getters, _setters);
                break;
            }
        }
        return result;
    }
    
    private static Method chooseMethod(String fieldName, Method... getters)
    {
        // Choose best matching method for given field name
        for (int i = 0; i < fieldName.length(); i++) {
            String suffix = fieldName.substring(i).toLowerCase();
            if (suffix.isEmpty()) {
                break;
            }
            for (Method method : getters) {
                String methodName = method.getName().toLowerCase();
                if (methodName.endsWith(suffix)) {
                    return method;
                }
            }
        }
        return getters[0];
    }
    
}
