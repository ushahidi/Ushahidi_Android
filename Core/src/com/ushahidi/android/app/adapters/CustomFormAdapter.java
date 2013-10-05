package com.ushahidi.android.app.adapters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.entities.CustomFormMetaEntity;
import com.ushahidi.android.app.entities.ReportCustomFormEntity;
import com.ushahidi.android.app.util.Util;
import com.ushahidi.java.sdk.api.CustomFormMeta;

public class CustomFormAdapter {

	
	public static View createTextView(Context context, ReportCustomFormEntity rcf){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout view = (LinearLayout)inflater.inflate(R.layout.reportcustomform_textview, null);
		new Util().log("Creating text view with "+view+" of childs:"+view.getChildCount());
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setText(rcf.getFieldName());
		TextView value = (TextView) view.findViewById(R.id.value);
		value.setText(rcf.getValue());
		return view;
	}

	public static View createView(Context context, CustomFormMetaEntity cf) {

		View view = null;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		switch (cf.getType()) {

		case CustomFormMeta.TYPE_CHECKBOX:
		case CustomFormMeta.TYPE_DATE:
		case CustomFormMeta.TYPE_DROPDOWN:
			view = inflater.inflate(R.layout.customform_dropdown, null);
			Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
			spinner.setId(cf.getFieldId());
			String[] values = getCleanedValues(cf.getDefaultValues());
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,  R.layout.customform_spinner_item,values);

			adapter.setDropDownViewResource(R.layout.customform_spinner_dropdown_item);
			spinner.setAdapter(adapter);
			break;
		case CustomFormMeta.TYPE_PASSWORD:
			view = inflater.inflate(R.layout.customform_edittext, null);
			EditText etPassword = (EditText) view.findViewById(R.id.edittext);
			etPassword.setId(cf.getFieldId());
			etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			etPassword.setId(cf.getFieldId());
			break;
		case CustomFormMeta.TYPE_RADIO:
			
		case CustomFormMeta.TYPE_TEXTAREA:
		case CustomFormMeta.TYPE_TEXT:
			view = inflater.inflate(R.layout.customform_edittext, null);
			EditText et = (EditText) view.findViewById(R.id.edittext);
			et.setId(cf.getFieldId());
			break;
		}

		TextView tw = (TextView) view.findViewById(R.id.title);
		tw.setText(cf.getName());

		return view;
	}
	public static String[] getCleanedValues(String defaultValues){
		String[] values = defaultValues.split(",");
		for(int i = 0; i < values.length; i++){
			values[i] = values[i].replace(" ", "");
		}
		return values;
	}
	
	public static Map<Integer,String> getValuesFromLayout(List<CustomFormMetaEntity> customFormsDefs, Activity context){
		HashMap<Integer,String> map = new HashMap<Integer,String>();
		
		for(CustomFormMetaEntity cfme : customFormsDefs){
			
			switch (cfme.getType()) {

			case CustomFormMeta.TYPE_CHECKBOX:
			case CustomFormMeta.TYPE_DATE:
			case CustomFormMeta.TYPE_DROPDOWN:
				Spinner spinner = (Spinner) context.findViewById(cfme.getFieldId());
				map.put(cfme.getFieldId(),spinner.getSelectedItem().toString());
				break;	
			case CustomFormMeta.TYPE_RADIO:
				break;
			case CustomFormMeta.TYPE_PASSWORD:
			case CustomFormMeta.TYPE_TEXTAREA:
			case CustomFormMeta.TYPE_TEXT:
				EditText view = (EditText)context.findViewById(cfme.getFieldId());
				map.put(cfme.getFieldId(),view.getText().toString());
				break;
			}
		}
		
		
		return map;
	}
	
	public static Map<String,String> convertEntityToMap(List<ReportCustomFormEntity> list){
		Map<String,String> map = new HashMap<String,String>();
		for(ReportCustomFormEntity rcfe : list){
			map.put(String.valueOf(rcfe.getFieldId()),rcfe.getValue());
		}
		return map;
	}

}
