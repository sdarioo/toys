package com.examples.demo.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.ttddyy.dsproxy.listener.AbstractQueryLoggingListener;

public class SQLExecutionListener extends AbstractQueryLoggingListener {

	private volatile boolean isCollecting;
	private final List<String> messages = new ArrayList<String>();
	
	private static final SQLExecutionListener DEFAULT = new SQLExecutionListener();
	
	private SQLExecutionListener() {}
	
	public static SQLExecutionListener getDefault() {
		return DEFAULT;
	}
	
	@Override
	protected void writeLog(String message) {
		if (isCollecting) {
			messages.add(message);
		}
	}
	
	public void startCollecting() {
		isCollecting = true;
	}
	
	public void stopCollecting() {
		isCollecting = false;
	}

	public List<String> getMessages() {
		return Collections.unmodifiableList(messages);
	}
	
	public void logAndReset() {
		List<String> copy = new ArrayList<>(messages);
		messages.clear();
		copy.forEach(System.err::println);
	}
}
