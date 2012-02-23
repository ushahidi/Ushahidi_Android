package com.ushahidi.android.app.database;

/**
 * Interface that must be implemented by persisted objects
 * 
 * @author eyedol
 *
 */
public interface DbEntity {
    
    public Long getDbId();
    
    public void setDbId(Long id);
    
}
