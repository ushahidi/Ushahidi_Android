package com.ushahidi.android.app.database;

import java.util.List;

import com.ushahidi.android.app.entities.CustomFormMetaEntity;

/**
 * 
 * @author markov00
 *
 */
public interface ICustomFormMetaDao {

    //fetch all custom forms
    public List<CustomFormMetaEntity> fetchAllCustomFormMetas();
    
    
    public List<CustomFormMetaEntity> fetchCustomFormMetaByFormId(int formId);
    
    //delete CustomForms
    public boolean deleteAllCustomFormMetas();
    
    //delete CustomForms by form id
    public boolean deleteCustomFormMetas(int formId);
    
    //add custom form
    public boolean addCustomFormMeta(CustomFormMetaEntity customForm);
    
    //add CustomForms
    public boolean addCustomFormMetas(List<CustomFormMetaEntity> customForms);
    
}
