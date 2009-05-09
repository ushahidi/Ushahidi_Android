package org.addhen.ushahidi;

import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class ListIncidentAdapter extends BaseAdapter {
	
private Context iContext;
	
	private List<ListIncidentText> iItems = new ArrayList<ListIncidentText>();
	
	public ListIncidentAdapter( Context context ) {
		iContext = context;
	}
	
	public void addItem( ListIncidentText it ){
		iItems.add(it);
	}
	
	public void removeItems() {
		iItems.clear();
	}
	
	public void setListItems( List<ListIncidentText> lit ){
		iItems = lit;
	}
	
	public int getCount() {
		return iItems.size();
	}
	
	public Object getItem( int position ){
		return iItems.get( position );
	}
	
	public boolean areAllItemsSelectable(){
		return false;
	}
	
	public boolean isSelectable( int position ){
		return iItems.get( position ).isSelectable();
	}
	
	public long getItemId( int position ) {
		return position;
	}
	
	public View getView( int position, View convertView, ViewGroup parent ){
		ListIncidentTextView iTv;
		if( convertView == null ){
			iTv = new ListIncidentTextView ( iContext, iItems.get( position ));
		
		} else {
			
			iTv = ( ListIncidentTextView  ) convertView;
			
			iTv.setThumbnail(iItems.get(position).getThumbnail());
			
			iTv.setTitle( iItems.get( position).getTitle() );
			
			iTv.setDate( iItems.get( position ).getDate() );
			
			iTv.setStatus( iItems.get( position ).getStatus() );
			
			iTv.setId( iItems.get( position ).getId() );
		
		}
		return iTv;
	}
	
}
