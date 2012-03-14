package com.ushahidi.android.app.entities;

/**
 * Interface that must be implemented by persisted objects
 * 
 * @author eyedol
 *
 */
public interface IDbEntity {
    
    public int getDbId();
    
    public void setDbId(int id);
    
}
