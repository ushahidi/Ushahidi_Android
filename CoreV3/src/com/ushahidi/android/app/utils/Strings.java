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

package com.ushahidi.android.app.utils;

import android.widget.EditText;

import java.util.StringTokenizer;

/**
 * Strings
 *
 * Helper class for String
 */
public class Strings {

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.trim().length() == 0;
    }

    public static boolean isNullOrEmpty(EditText editText) {
        return editText != null && isNullOrEmpty(editText.getText().toString());
    }

    public static boolean anyWordStartsWith(String text, String...words) {
        if (!isNullOrEmpty(text)) {
            StringTokenizer tokenizer = new StringTokenizer(text, ",");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                for (String word : words) {
                    if (word != null && word.toLowerCase().startsWith(token.trim().toLowerCase())) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }

    public static boolean areNullOrEmpty(String...words) {
        for (String word : words) {
            if (!isNullOrEmpty(word)) {
                return false;
            }
        }
        return true;
    }
}
