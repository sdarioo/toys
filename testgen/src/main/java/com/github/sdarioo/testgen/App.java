/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen;

import java.util.Properties;

import com.github.sdarioo.testgen.generator.Generator;
import com.github.sdarioo.testgen.recorder.Call;
import com.github.sdarioo.testgen.recorder.Recorder;
import com.github.sdarioo.testgen.recorder.Call.MethodRef;
import com.github.sdarioo.testgen.recorder.params.ParamsFactory;

public class App 
{
    public static void main(String[] args) 
    {
        //Generator.registerShutdownHook();
        
        for (int i = 0; i < 10; i++) {
            Properties p = new Properties();
            p.setProperty("key-"+i, "value-"+i);
            
            concat(p);
            add(i, 10 - i);
        }
        
        Generator.generateTestsSafe(Recorder.getDefault());
    }
    
    public static int add(int x, int y)
    {
        Call call = Call.newCall(new MethodRef() {});
        call.args().add(ParamsFactory.newValue(x));
        call.args().add(ParamsFactory.newValue(y));
        
        int result = 0;
        try {
            result = x + y;
            return result;
        } finally {
            call.setResult(ParamsFactory.newValue(result));
            Recorder.getDefault().record(call);
        }
    }
    
    public static String concat(Properties props)
    {
        Call call = Call.newCall(new MethodRef() {});
        call.args().add(ParamsFactory.newValue(props));
        
        String ret = null;
        try {
            StringBuilder sb = new StringBuilder();
            for (Object key : props.keySet()) {
                if (sb.length() > 0) {
                    sb.append(';');
                }
                String sKey = (String)key;
                String sValue = props.getProperty(sKey);
                sb.append(sKey).append('=').append(sValue);
            }
            ret = sb.toString();
            return ret;
        } finally {
            call.setResult(ParamsFactory.newValue(ret));
            Recorder.getDefault().record(call);
        }
        
    }
}
