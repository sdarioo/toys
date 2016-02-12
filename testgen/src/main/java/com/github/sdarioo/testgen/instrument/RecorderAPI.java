package com.github.sdarioo.testgen.instrument;

import java.lang.reflect.Method;
import java.util.LinkedList;

import org.objectweb.asm.Type;

import com.github.sdarioo.testgen.logging.Logger;
import com.github.sdarioo.testgen.recorder.Call;
import com.github.sdarioo.testgen.recorder.Recorder;

public final class RecorderAPI
{
	private static ThreadLocal<ThreadLocalRecorder> threadLocalRecorders =
			new ThreadLocal<ThreadLocalRecorder>() {
		
		protected ThreadLocalRecorder initialValue() {
			String thread = Thread.currentThread().getName();
			Logger.info("New ThreadLocalRecorder [" + thread + ']'); //$NON-NLS-1$
			return new ThreadLocalRecorder();
		};
	};
	
	
	private RecorderAPI() {}
	
	public static void methodBegin(Method method, Object target, Object[] args)
	{
		threadLocalRecorders.get().methodBegin(method, target, args);
	}
	
	public static void methodEnd()
	{
		threadLocalRecorders.get().methodEnd(true, null, null);
	}
	
	public static void methodEndWithResult(Object object)
	{
		threadLocalRecorders.get().methodEnd(false, object, null);
	}
	
	public static void methodEndWithException(Throwable throwable)
	{
		threadLocalRecorders.get().methodEnd(false, null, throwable);
	}
	
    public static java.lang.reflect.Method getMethod(Class<?> owner, String name, String descriptor)
    {
        Method[] declaredMethods = owner.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (name.equals(method.getName())) {
                String desc = org.objectweb.asm.commons.Method.getMethod(method).getDescriptor();
                if (descriptor.equals(desc)) {
                    return method;
                }
            }
        }
        return null;
    }
	
    public static Object proxy(Class<?> argumentClass, Object actualValue)
    {
        if (actualValue == null) {
            return actualValue;
        }
        
        if ((argumentClass == null) || !argumentClass.isInterface()) {
            return actualValue;
        }
        
        System.err.println("NEW PROXY: " + argumentClass.getName());
        return actualValue;
    }
    
	private static class ThreadLocalRecorder
	{
		private final LinkedList<Call> _callQueue = new LinkedList<Call>();
		
		void methodBegin(Method method, Object target, Object[] args)
		{
			Call call = Call.newCall(method, target, args);
			_callQueue.addFirst(call);
		}
		
		void methodEnd(boolean isVoid, Object result, Throwable thr)
		{
			if (!_callQueue.isEmpty()) {
				Call call = _callQueue.removeFirst();
				if (isVoid) {
					call.end();
				} else if (thr != null) {
					call.endWithException(thr);
				} else {
					call.endWithResult(result);
				}
				Recorder.getDefault().record(call);
			}
		}
	}
	
	static final String TYPE_NAME = Type.getType(RecorderAPI.class).getInternalName();
}
