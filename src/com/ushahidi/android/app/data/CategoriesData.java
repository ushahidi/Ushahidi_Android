/** 
 ** Copyright (c) 2010 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 ** 
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.	
 **	
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 ** 
 **/

package com.ushahidi.android.app.data;

public class CategoriesData {

    private int categoryId = 0;

    private String categoryTitle = "";

    private String categoryDescription = "";

    private String categoryColor = "";

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String title) {
        this.categoryTitle = title;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String description) {
        this.categoryDescription = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int id) {
        this.categoryId = id;
    }

    public String getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(String color) {
        this.categoryColor = color;
    }
}
