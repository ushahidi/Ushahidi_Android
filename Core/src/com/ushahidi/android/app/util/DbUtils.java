
package com.ushahidi.android.app.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.ushahidi.android.app.database.DbEntity;

public class DbUtils {

    /** Column name for the ID column */
    public static final String COLUMN_ID = "_id";

    public static final int DATABASE_VERSION = 14;

    public static final String DATABASE_NAME = "ushahidi_db";

    /**
     * SQL keywords. These should not be used for non-standard use, e.g. column
     * names
     */
    private static final String[] SQL_KEYWORDS = {
            "from", "group", "select"
    };

    /** @return <code>true</code> if the supplied string is a SQL keyword. */
    private static boolean isSqlRestricted(String string) {
        for (String keyword : SQL_KEYWORDS) {
            if (keyword.equalsIgnoreCase(string)) {
                return true;
            }
        }
        return false;
    }

    public static String getCreateStatement(Class<? extends DbEntity> entityClass) {
        if (entityClass.isPrimitive() || entityClass.isEnum()) {
            throw new IllegalArgumentException(
                    "Cannot store primitive type or enum in separate table: " + entityClass);
        }

        StringBuilder bob = new StringBuilder();
        for (Field intransientField : getFields(entityClass)) {
            bob.append(", ");
            String columnName = getColumnName(intransientField);
            bob.append(columnName);
            bob.append(" ");
            bob.append(getColumnType(intransientField.getType()));
            if (columnName.equalsIgnoreCase(COLUMN_ID)) {
                bob.append(" PRIMARY KEY AUTOINCREMENT");
            }
        }

        return "CREATE TABLE " + getTableName(entityClass) + " (" + bob.substring(2) + ");";
    }

    public static String getCreateTable(String tableName) {

        if (tableName.isEmpty()) {
            throw new IllegalArgumentException("Cannot create an empty table ");
        }

        return "CREATE TABLE " + tableName;

    }

    static String getColumnType(Class<?> clazz) {
        if (clazz.equals(String.class)) {
            return "TEXT";
        }
        if (clazz.equals(int.class) || clazz.equals(Integer.class) || clazz.equals(long.class)
                || clazz.equals(Long.class) || clazz.isEnum()) {
            return "INTEGER";
        }
        throw new IllegalArgumentException("Unsupported column type: " + clazz);
    }

    public static String getColumnName(Field field) {
        String columnName = field.getName();
        if (isSqlRestricted(columnName)) {
            columnName = "_" + columnName;
        }
        return columnName;
    }

    /**
     * @return all fields of the supplied class which are not
     *         <code>transient</code>
     */
    public static List<Field> getFields(Class<? extends DbEntity> entityClass) {
        List<Field> fields = new LinkedList<Field>();

        for (Field declared : entityClass.getDeclaredFields()) {
            if (!Modifier.isTransient(declared.getModifiers())) {
                declared.setAccessible(true);
                fields.add(declared);
            }
        }

        return fields;
    }

    public static String getTableName(Class<? extends DbEntity> entityClass) {

        return entityClass.getSimpleName();
    }

    // @SuppressWarnings("unchecked")
    // static <T extends DbEntity> Class<T> getTableClass(String tableName) {
    // try {
    // return (Class<T>) Class.forName(tableName.replace('_', '.'));
    // } catch (ClassNotFoundException ex) {
    // throw new IllegalArgumentException("Could not get class for table: " +
    // tableName, ex);
    // }
    // }

    public static Object getContentValue(DbEntity entity, Field field) {
        try {
            return field.get(entity);
        } catch (IllegalAccessException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    static void addContentValue(ContentValues values, DbEntity entity, Field field,
            boolean insertNulls) {
        try {
            Class<?> type = field.getType();
            String key = getColumnName(field);
            if (type.equals(int.class)) {
                values.put(key, field.getInt(entity));
            } else if (type.equals(Integer.class)) {
                Object value = field.get(entity);
                if (insertNulls || value != null) {
                    values.put(key, (Integer)value);
                }
            } else if (type.equals(long.class)) {
                values.put(key, field.getLong(entity));
            } else if (type.equals(Long.class)) {
                Object value = field.get(entity);
                if (insertNulls || value != null) {
                    values.put(key, (Long)value);
                }
            } else if (type.equals(String.class)) {
                Object value = field.get(entity);
                if (insertNulls || value != null) {
                    values.put(key, (String)value);
                }
            } else if (type.isEnum()) {
                Object value = field.get(entity);
                if (value != null) {
                    values.put(key, ((Enum<?>)value).ordinal());
                } else if (insertNulls) {
                    values.put(key, (Integer)null);
                }
            } else if (type.equals(DbEntity.class)) {
                Object value = field.get(entity);
                if (value != null) {
                    values.put(key, ((DbEntity)value).getDbId());
                } else if (insertNulls) {
                    values.put(key, (Integer)null);
                }
            } else
                throw new IllegalArgumentException("Unable to add content value for class");
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Converts a {@link Cursor} into a {@link List}, and closes the
     * {@link Cursor}
     * 
     * @param cursor
     * @return
     */
    public static <T extends DbEntity> List<T> asList(Class<T> entityClass, Cursor cursor) {
        if (cursor == null) {
            return Collections.emptyList();
        } else {
            ArrayList<T> results = new ArrayList<T>(cursor.getCount());

            // Get all rows from the cursor, and convert them into POJOs
            List<Field> fields = getFields(entityClass);
            while (cursor.moveToNext()) {
                T instance;
                try {
                    instance = entityClass.newInstance();
                } catch (Exception ex) {
                    throw new IllegalArgumentException("Unable to create instance of "
                            + entityClass);
                }

                // set field values
                for (Field field : fields) {
                    setFieldValue(instance, field, cursor);
                }

                results.add(instance);
            }

            cursor.close();
            return results;
        }
    }

    static <T extends DbEntity> void setFieldValue(T instance, Field field, Cursor cursor) {
        Class<?> type = field.getType();
        Object value;
        if (type.equals(int.class) || type.equals(Integer.class)) {
            value = cursor.getInt(cursor.getColumnIndex(getColumnName(field)));
        } else if (type.equals(long.class) || type.equals(Long.class)) {
            value = cursor.getLong(cursor.getColumnIndex(getColumnName(field)));
        } else if (type.equals(String.class)) {
            value = cursor.getString(cursor.getColumnIndex(getColumnName(field)));
        } else if (type.isEnum()) {
            value = type.getEnumConstants()[cursor.getInt(cursor
                    .getColumnIndex(getColumnName(field)))];
        } else
            throw new IllegalArgumentException("Unsupported column type: " + type);
        try {
            field.set(instance, value);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Could not set field " + field.getName() + " of "
                    + instance.getClass());
        }
    }

    public static String getWhereClause(Uri uri, String selection) {
        List<String> pathSegments = uri.getPathSegments();
        if (pathSegments.size() > 1) {
            if (selection.length() > 0)
                selection += " AND ";
            selection += "_id=" + pathSegments.get(1);
        }
        return null;
    }

    public static String getTableName(Uri uri) {
        return uri.getPathSegments().get(0);
    }

    public static String getUri(String baseUri, Class<? extends DbEntity> entityClass) {
        return baseUri + '/' + DbUtils.getTableName(entityClass);
    }

    public static ContentValues getValues(DbEntity entity) {
        return getValues(entity, true);
    }

    public static ContentValues getNonNullValues(DbEntity entity) {
        return getValues(entity, false);
    }

    static ContentValues getValues(DbEntity entity, boolean insertNulls) {
        List<Field> fields = DbUtils.getFields(entity.getClass());
        ContentValues values = new ContentValues(fields.size());
        for (Field field : fields) {
            DbUtils.addContentValue(values, entity, field, insertNulls);
        }
        return values;
    }

    /**
     * Converts an object value into a {@link String} value suitable for using
     * as a <code>selectionArg</code> in a SQL WHERE statement.
     * 
     * @param value
     * @return
     */
    public static String getValueAsSqlString(Object value) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "I don't expect you can use NULL in this way... should be a special method for that.");
        }

        if (value instanceof String) {
            return (String)value;
        } else if (value instanceof Integer) {
            return Integer.toString((Integer)value);
        } else if (value instanceof Long) {
            return Integer.toString((Integer)value);
        } else if (value.getClass().isEnum()) {
            return Integer.toString(((Enum<?>)value).ordinal());
        } else
            throw new IllegalArgumentException("Unsupported value type for where statement: "
                    + value.getClass().getName());
    }

    /**
     * Credits http://goo.gl/7kOpU
     * 
     * @param db
     * @param tableName
     * @return
     */
    public static List<String> getColumns(SQLiteDatabase db, String tableName) {
        List<String> ar = null;
        Cursor c = null;

        try {
            c = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null);

            if (c != null) {
                ar = new ArrayList<String>(Arrays.asList(c.getColumnNames()));
            }

        } catch (Exception e) {
            Log.v(tableName, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
        }
        return ar;
    }

    public static String join(List<String> list, String delim) {
        StringBuilder buf = new StringBuilder();
        int num = list.size();
        for (int i = 0; i < num; i++) {
            if (i != 0)
                buf.append(delim);
            buf.append((String)list.get(i));
        }
        return buf.toString();
    }

}
