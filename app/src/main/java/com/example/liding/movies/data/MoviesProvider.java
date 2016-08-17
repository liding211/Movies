package com.example.liding.movies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MoviesProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper dbHelper;

    static final int FAVORITE = 100;

    //favorite._id = ?
    private static final String movieSelection =
        MoviesContractor.MovieEntry.TABLE_NAME + "." + MoviesContractor.MovieEntry._ID + " = ? ";

    static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = MoviesContractor.CONTENT_AUTHORITY;
        matcher.addURI(authority, MoviesContractor.PATH_FAVORITE, FAVORITE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.
     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case FAVORITE:
                return MoviesContractor.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "favorite"
            case FAVORITE: {
                retCursor = dbHelper.getReadableDatabase().query(
                    MoviesContractor.MovieEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAVORITE:
                long id = db.insert(MoviesContractor.MovieEntry.TABLE_NAME, null, values);
                if ( id > 0 )
                    returnUri = MoviesContractor.MovieEntry.buildFavoriteUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Student: Start by getting a writable database
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String targetTable;
        switch (sUriMatcher.match(uri)){
            case FAVORITE:
                targetTable = MoviesContractor.MovieEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException();
        }

        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";

        int rowsDeleted = db.delete(targetTable, selection, selectionArgs);

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        return 0;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        dbHelper.close();
        super.shutdown();
    }
}