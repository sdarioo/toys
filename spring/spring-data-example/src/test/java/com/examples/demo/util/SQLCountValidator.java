package com.examples.demo.util;

import org.junit.Assert;

import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;

/**
 * SQL statements count validator.
 */
public class SQLCountValidator {
	 
    private SQLCountValidator() {
    }
 
    /**
     * Reset the statement recorder
     */
    public static void reset() {
        QueryCountHolder.clear();
    }
 
    /**
     * Assert select statement count
     * @param expectedSelectCount expected select statement count
     */
    public static void assertSelectCount(int expectedSelectCount) {
        QueryCount queryCount = QueryCountHolder.getGrandTotal();
        int recordedSelectCount = queryCount.getSelect();
        
        Assert.assertEquals(expectedSelectCount, recordedSelectCount);
    }
 
    /**
     * Assert insert statement count
     * @param expectedInsertCount expected insert statement count
     */
    public static void assertInsertCount(int expectedInsertCount) {
        QueryCount queryCount = QueryCountHolder.getGrandTotal();
        int recordedInsertCount = queryCount.getInsert();
        
        Assert.assertEquals(expectedInsertCount, recordedInsertCount);
    }
 
    /**
     * Assert update statement count
     * @param expectedUpdateCount expected update statement count
     */
    public static void assertUpdateCount(int expectedUpdateCount) {
        QueryCount queryCount = QueryCountHolder.getGrandTotal();
        int recordedUpdateCount = queryCount.getUpdate();
        
        Assert.assertEquals(expectedUpdateCount, recordedUpdateCount);
    }
 
    /**
     * Assert delete statement count
     * @param expectedDeleteCount expected delete statement count
     */
    public static void assertDeleteCount(int expectedDeleteCount) {
        QueryCount queryCount = QueryCountHolder.getGrandTotal();
        int recordedDeleteCount = queryCount.getDelete();
        
        Assert.assertEquals(expectedDeleteCount, recordedDeleteCount);
    }
}
