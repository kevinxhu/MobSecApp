package com.android.mobsec;

import com.android.mobsec.policyElem.Elements;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.LiveFolders;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

/**
 * Provides access to a database of notes. Each note has a title, the note
 * itself, a creation date and a modified data.
 */
public class PolElemProvider extends ContentProvider {

    private static final String TAG = "PolElemProvider";

    private static final String DATABASE_NAME = "mob_sec.db";
    private static final int DATABASE_VERSION = 2;
    private static final String ELEMENTS_TABLE_NAME = "elements";

    private static HashMap<String, String> sNotesProjectionMap;
    private static HashMap<String, String> sLiveFolderProjectionMap;

    private static final int NOTES = 1;
    private static final int NOTE_ID = 2;
    private static final int LIVE_FOLDER_NOTES = 3;

    private static final UriMatcher sUriMatcher;

    /**
     * This class helps open, create, and upgrade the database file.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + ELEMENTS_TABLE_NAME + " ("
                    + Elements._ID + " INTEGER PRIMARY KEY,"
                    + Elements.NAME + " TEXT,"
                    + Elements.IPADDR + " TEXT,"
                    + Elements.CREATED_DATE + " INTEGER,"
                    + Elements.MODIFIED_DATE + " INTEGER"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(ELEMENTS_TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
        case NOTES:
            qb.setProjectionMap(sNotesProjectionMap);
            break;

        case NOTE_ID:
            qb.setProjectionMap(sNotesProjectionMap);
            qb.appendWhere(Elements._ID + "=" + uri.getPathSegments().get(1));
            break;

        case LIVE_FOLDER_NOTES:
            qb.setProjectionMap(sLiveFolderProjectionMap);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = policyElem.Elements.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
        case NOTES:
        case LIVE_FOLDER_NOTES:
            return Elements.CONTENT_TYPE;

        case NOTE_ID:
            return Elements.CONTENT_ITEM_TYPE;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != NOTES) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        Long now = Long.valueOf(System.currentTimeMillis());

        // Make sure that the fields are all set
        if (values.containsKey(policyElem.Elements.CREATED_DATE) == false) {
            values.put(policyElem.Elements.CREATED_DATE, now);
        }

        if (values.containsKey(policyElem.Elements.MODIFIED_DATE) == false) {
            values.put(policyElem.Elements.MODIFIED_DATE, now);
        }

        if (values.containsKey(policyElem.Elements.NAME) == false) {
            Resources r = Resources.getSystem();
            values.put(policyElem.Elements.NAME, r.getString(android.R.string.untitled));
        }

        if (values.containsKey(policyElem.Elements.IPADDR) == false) {
            values.put(policyElem.Elements.IPADDR, "");
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(ELEMENTS_TABLE_NAME, Elements.NAME, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(policyElem.Elements.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case NOTES:
            count = db.delete(ELEMENTS_TABLE_NAME, where, whereArgs);
            break;

        case NOTE_ID:
            String noteId = uri.getPathSegments().get(1);
            count = db.delete(ELEMENTS_TABLE_NAME, Elements._ID + "=" + noteId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case NOTES:
            count = db.update(ELEMENTS_TABLE_NAME, values, where, whereArgs);
            break;

        case NOTE_ID:
            String noteId = uri.getPathSegments().get(1);
            count = db.update(ELEMENTS_TABLE_NAME, values, Elements._ID + "=" + noteId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(policyElem.AUTHORITY, "elements", NOTES);
        sUriMatcher.addURI(policyElem.AUTHORITY, "elements/#", NOTE_ID);
        sUriMatcher.addURI(policyElem.AUTHORITY, "live_folders/elements", LIVE_FOLDER_NOTES);

        sNotesProjectionMap = new HashMap<String, String>();
        sNotesProjectionMap.put(Elements._ID, Elements._ID);
        sNotesProjectionMap.put(Elements.NAME, Elements.NAME);
        sNotesProjectionMap.put(Elements.IPADDR, Elements.IPADDR);
        sNotesProjectionMap.put(Elements.CREATED_DATE, Elements.CREATED_DATE);
        sNotesProjectionMap.put(Elements.MODIFIED_DATE, Elements.MODIFIED_DATE);

        // Support for Live Folders.
        sLiveFolderProjectionMap = new HashMap<String, String>();
        sLiveFolderProjectionMap.put(LiveFolders._ID, Elements._ID + " AS " +
                LiveFolders._ID);
        sLiveFolderProjectionMap.put(LiveFolders.NAME, Elements.NAME + " AS " +
                LiveFolders.NAME);
        // Add more columns here for more robust Live Folders.
    }
}
