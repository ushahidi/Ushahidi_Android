package com.ushahidi.android.app.json;

import java.util.ArrayList;
import java.util.List;

import com.ushahidi.android.app.entities.Category;

public class UshahidiApiCategories {
	private static class Payload extends UshahidiApiResponse.Payload {
		private static class _Category {
			private Category category;
		}

		private List<_Category> categories;
	}

	private Payload payload;

	public List<Category> getCategories() {
		ArrayList<Category> ret = new ArrayList<Category>();
		for (Payload._Category c : payload.categories) {
			// perhaps there is a better way to get around the '#' issue?
			Category cat = c.category;
			cat.setCategoryColor(cat.getCategoryColor());
			ret.add(cat);
		}
		return ret;
	}

}
