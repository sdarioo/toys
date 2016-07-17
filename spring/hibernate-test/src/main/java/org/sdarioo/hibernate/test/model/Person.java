/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package org.sdarioo.hibernate.test.model;

import java.text.MessageFormat;

import javax.persistence.*;


@Entity
@Table(name="PERSONS")
public class Person 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="ID")
    private int id;
    
    @Column(name="NAME")
    private String name;
    
    public Person()
    {
        this("<new-person>"); //$NON-NLS-1$
    }
    public Person(String name)
    {
        this.name = name;
    }
    
    public int getId() 
    {
        return id;
    }
    public void setId(int id) 
    {
        this.id = id;
    }
    public String getName() 
    {
        return name;
    }
    public void setName(String name) 
    {
        this.name = name;
    }
    
    @Override
    public String toString() 
    {
        return MessageFormat.format("Person[id={0}, name={1}]", id, name); //$NON-NLS-1$
    }
}
