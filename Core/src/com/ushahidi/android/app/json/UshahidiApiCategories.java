package com.ushahidi.android.app.json;

import java.util.ArrayList;
import java.util.List;

import com.ushahidi.android.app.entities.CategoryEntity;

public class UshahidiApiCategories {
	private static class Payload extends UshahidiApiResponse.Payload {
		private static class _Category {
			private CategoryEntity category;
		}

		private List<_Category> categories;
	}

	private Payload payload;

	public List<CategoryEntity> getCategories() {
		ArrayList<CategoryEntity> ret = new ArrayList<CategoryEntity>();
		for (Payload._Category c : payload.categories) {
			// perhaps there is a better way to get around the '#' issue?
			CategoryEntity cat = c.category;
			cat.setCategoryColor(cat.getCategoryColor());
			ret.add(cat);
		}
		return ret;
	}

}
