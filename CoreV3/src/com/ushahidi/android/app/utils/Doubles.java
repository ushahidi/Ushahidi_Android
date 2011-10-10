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

import android.text.Editable;
import android.widget.EditText;

/**
 * Doubles
 *
 * Helper class for Double
 */
public class Doubles {

    public static boolean isDouble(String text) {
        try {
            Double.parseDouble(text);
            return true;
        }
        catch(NumberFormatException nfe) {
            return false;
        }
    }

    public static boolean isDouble(Editable editable) {
        return editable != null && isDouble(editable.toString());

    }

    public static boolean isDouble(EditText editText) {
        return editText != null && isDouble(editText.getText().toString());
    }
}
