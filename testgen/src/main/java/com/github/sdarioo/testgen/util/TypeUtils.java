/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.util;

import java.lang.reflect.*;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

/**
 * Inspired on:
 * org.apache.commons.lang3.reflect.TypeUtils
 */
public class TypeUtils 
{
    
    public static boolean containsTypeVariables(Type type)
    {
        return org.apache.commons.lang3.reflect.TypeUtils.containsTypeVariables(type);
    }
    
    public static Type parameterize(Class<?> raw)
    {
        TypeVariable<?>[] typeParameters = raw.getTypeParameters();
        if (typeParameters.length > 0) {
            return org.apache.commons.lang3.reflect.TypeUtils.parameterize(raw, typeParameters);
        }
        return raw;
    }
    
    public static String getNameWithBounds(TypeVariable<?> type, TestSuiteBuilder builder)
    {
        StringBuilder buf = new StringBuilder(type.getName());
        Type[] bounds = type.getBounds();
        if (bounds.length > 0 && !(bounds.length == 1 && Object.class.equals(bounds[0]))) {
            buf.append(" extends "); //$NON-NLS-1$
            appendAllTo(buf, " & ", builder, type.getBounds()); //$NON-NLS-1$
        }
        return buf.toString();
    }
    
    public static String getName(Type type, TestSuiteBuilder builder) 
    {
        Validate.notNull(type);
        
        if (type instanceof Class<?>) {
            return builder.getTypeName((Class<?>) type);
        }
        if (type instanceof ParameterizedType) {
            return parameterizedTypeToString((ParameterizedType) type, builder);
        }
        if (type instanceof WildcardType) {
            return wildcardTypeToString((WildcardType) type, builder);
        }
        if (type instanceof TypeVariable<?>) {
            return typeVariableToString((TypeVariable<?>) type, builder);
        }
        if (type instanceof GenericArrayType) {
            return genericArrayTypeToString((GenericArrayType) type, builder);
        }
        throw new IllegalArgumentException(ObjectUtils.identityToString(type));
    }
    
    private static String parameterizedTypeToString(ParameterizedType type, TestSuiteBuilder builder) 
    {
        StringBuilder buf = new StringBuilder();

        Type useOwner = type.getOwnerType();
        Class<?> raw = (Class<?>)type.getRawType();
        Type[] typeArguments = type.getActualTypeArguments();
        if (useOwner == null) {
            buf.append(getName(raw, builder));
        } else {
            if (useOwner instanceof Class<?>) {
                buf.append(getName(((Class<?>) useOwner), builder));
            } else {
                buf.append(useOwner.toString());
            }
            buf.append('.').append(raw.getSimpleName());
        }
        appendAllTo(buf.append('<'), ", ", builder, typeArguments).append('>'); //$NON-NLS-1$
        return buf.toString();
    }
    
    private static String typeVariableToString(TypeVariable<?> type, TestSuiteBuilder builder) 
    {
        return type.getName();
    }
    
    @SuppressWarnings("nls")
    private static String wildcardTypeToString(WildcardType type, TestSuiteBuilder builder) 
    {
        StringBuilder buf = new StringBuilder().append('?');
        Type[] lowerBounds = type.getLowerBounds();
        Type[] upperBounds = type.getUpperBounds();
        if (lowerBounds.length > 1 || lowerBounds.length == 1 && lowerBounds[0] != null) {
            appendAllTo(buf.append(" super "), " & ", builder, lowerBounds);
        } else if (upperBounds.length > 1 || upperBounds.length == 1 && !Object.class.equals(upperBounds[0])) {
            appendAllTo(buf.append(" extends "), " & ", builder, upperBounds);
        }
        return buf.toString();
    }

    private static String genericArrayTypeToString(GenericArrayType type, TestSuiteBuilder builder) 
    {
        return String.format("%s[]", getName(type.getGenericComponentType(), builder)); //$NON-NLS-1$
    }
    
    private static StringBuilder appendAllTo(StringBuilder buf, 
            String sep, TestSuiteBuilder builder, Type... types) 
    {
        Validate.notEmpty(Validate.noNullElements(types));
        if (types.length > 0) {
            buf.append(getName(types[0], builder));
            for (int i = 1; i < types.length; i++) {
                buf.append(sep).append(getName(types[i], builder));
            }
        }
        return buf;
    }
}
