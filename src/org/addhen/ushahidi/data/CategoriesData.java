
package org.addhen.ushahidi.data;

public class CategoriesData {
	
	int categoryId = 0;
	String categoryTitle = "";
	String categoryDescription = "";
	String categoryColor = "";
	
	public String getCategoryTitle() {
		return categoryTitle;
	}

	public void setCategoryTitle(String title ) {
		this.categoryTitle = title;
	}
	
	public String getCategoryDescription() {
		return categoryDescription;
	}
	
	public void setCategoryDescription( String description ) {
		this.categoryDescription = description;
	}
	
	public int getCategoryId() {
		return categoryId;
	}
	
	public void setCategoryId( int id ) {
		this.categoryId = id;
	}
	
	public String getCategoryColor() {
		return categoryColor;
	}
	
	public void setCategoryColor( String color ) {
		this.categoryColor = color;
	}
}
