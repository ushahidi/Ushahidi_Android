package com.ushahidi.android.app.database;

import java.util.List;

import com.ushahidi.android.app.entities.CustomFormEntity;

/**
 * 
 * @author markov00
 *
 */
public interface ICustomFormDao {

    //fetch all custom forms
    public List<CustomFormEntity> fetchAllCustomForms();
    
    
    public List<CustomFormEntity> fetchCustomFormByFormId(int formId);
    
    //delete CustomForms
    public boolean deleteAllCustomForms();
    
    //delete CustomForms by form id
    public boolean deleteCustomForms(int formId);
    
    //add custom form
    public boolean addCustomForm(CustomFormEntity customForm);
    
    //add CustomForms
    public boolean addCustomForms(List<CustomFormEntity> customForm);
    
}
