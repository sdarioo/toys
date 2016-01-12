/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.*;

public class BeanParamTest 
{
    @Test
    public void testBean()
    {
        Bean b = new Bean("hello", 666); //$NON-NLS-1$
        
        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.readerFor(Bean.class);
        ObjectWriter writer = mapper.writerFor(Bean.class);
        
        try {
            writer.writeValue(System.out, b);
        } catch (JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    
    private static class Bean
    {
        String name;
        int age;
        
        Bean(String name, int age)
        {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
