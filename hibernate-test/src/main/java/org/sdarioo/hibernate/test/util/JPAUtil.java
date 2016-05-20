/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package org.sdarioo.hibernate.test.util;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil 
{
    private static EntityManagerFactory emFactory;
    
    private JPAUtil() {}
    
    static {
        emFactory = Persistence.createEntityManagerFactory("org.sdarioo.hibernate_test.model"); //$NON-NLS-1$
    }
    
    public static EntityManagerFactory getEMFactory()
    {
        return emFactory;
    }
}
