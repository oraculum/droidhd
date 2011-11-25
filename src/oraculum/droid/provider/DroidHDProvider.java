package oraculum.droid.provider;

import java.util.ArrayList;
import java.util.Arrays;

import oraculum.droid.provider.DroidHDContract.Clientes;
import oraculum.droid.provider.DroidHDContract.Clientes.SearchSuggest;
import oraculum.droid.provider.DroidHDContract.ClientesColumns;
import oraculum.droid.provider.DroidHDDatabase.Tables;
import oraculum.droid.util.SelectionBuilder;
import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class DroidHDProvider extends ContentProvider {
	private static final String TAG = "DroidHDProvider";
    private static final boolean LOGV = Log.isLoggable(TAG, Log.VERBOSE);

    private DroidHDDatabase mOpenHelper;

    private static UriMatcher sUriMatcher = buildUriMatcher();
    
    private static final int CLIENTES = 100;
    private static final int CLIENTES_ID = 101;
    private static final int CLIENTES_SEARCH = 102;

    private static final int SEARCH_SUGGEST = 800;


    /**
     * Build and return a {@link UriMatcher} that catches all {@link Uri}
     * variations supported by this {@link ContentProvider}.
     */
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DroidHDContract.CONTENT_AUTHORITY;
        
        matcher.addURI(authority, "clientes", CLIENTES);
        matcher.addURI(authority, "clientes/search/*", CLIENTES_SEARCH);
        matcher.addURI(authority, "clientes/*", CLIENTES_ID);
        matcher.addURI(authority, "search_suggest_query", SEARCH_SUGGEST);

        return matcher;
    }
    
    @Override
    public boolean onCreate() {
        final Context context = getContext();
        mOpenHelper = new DroidHDDatabase(context);
        return true;
    }
    
    
    /** {@inheritDoc} */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CLIENTES:
                return Clientes.CONTENT_TYPE;
            case CLIENTES_ID:
                return Clientes.CONTENT_ITEM_TYPE;
            case CLIENTES_SEARCH:
            	return Clientes.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
    
    
    /** {@inheritDoc} */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        if (LOGV) Log.v(TAG, "query(uri=" + uri + ", proj=" + Arrays.toString(projection) + ")");
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        sUriMatcher = buildUriMatcher();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            default: {
                // Most cases are handled with simple SelectionBuilder
                final SelectionBuilder builder = buildExpandedSelection(uri, match);
                return builder.where(selection, selectionArgs).query(db, projection, sortOrder);
            }
            case SEARCH_SUGGEST: {
                final SelectionBuilder builder = new SelectionBuilder();

                // Adjust incoming query to become SQL text match
                selectionArgs[0] = selectionArgs[0] + "%";
                builder.table(Tables.SEARCH_SUGGEST);
                builder.where(selection, selectionArgs);
                builder.map(SearchManager.SUGGEST_COLUMN_QUERY,
                        SearchManager.SUGGEST_COLUMN_TEXT_1);

                projection = new String[] { BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1,
                        SearchManager.SUGGEST_COLUMN_QUERY };

                final String limit = uri.getQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT);
                return builder.query(db, projection, null, null, SearchSuggest.DEFAULT_SORT, limit);
            }
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (LOGV) Log.v(TAG, "insert(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CLIENTES: {
                db.insertOrThrow(Tables.CLIENTES, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Clientes.buildClienteUri(values.getAsString(Clientes.cliente_id));
            }
            case CLIENTES_ID: {
            	db.insertOrThrow(Tables.CLIENTES, null, values);
            	getContext().getContentResolver().notifyChange(uri, null);
            	return Clientes.buildClienteUri(values.getAsString(Clientes.cliente_id));
            }
            case SEARCH_SUGGEST: {
                db.insertOrThrow(Tables.SEARCH_SUGGEST, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return SearchSuggest.CONTENT_URI;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (LOGV) Log.v(TAG, "update(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).update(db, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return retVal;
    }

    /** {@inheritDoc} */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (LOGV) Log.v(TAG, "delete(uri=" + uri + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).delete(db);
        getContext().getContentResolver().notifyChange(uri, null);
        return retVal;
    }
    
    /**
     * Apply the given set of {@link ContentProviderOperation}, executing inside
     * a {@link SQLiteDatabase} transaction. All changes will be rolled back if
     * any single one fails.
     */
    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Build a simple {@link SelectionBuilder} to match the requested
     * {@link Uri}. This is usually enough to support {@link #insert},
     * {@link #update}, and {@link #delete} operations.
     */
    private SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CLIENTES: {
                return builder.table(Tables.CLIENTES);
            }
            case CLIENTES_ID: {
	        	final String clienteId = Clientes.getClienteId(uri);
                return builder.table(Tables.CLIENTES)
                        .where(Clientes.cliente_id + "=?", clienteId);
	        }
            case SEARCH_SUGGEST: {
                return builder.table(Tables.SEARCH_SUGGEST);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    /**
     * Build an advanced {@link SelectionBuilder} to match the requested
     * {@link Uri}. This is usually only used by {@link #query}, since it
     * performs table joins useful for {@link Cursor} data.
     */
    private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
        final SelectionBuilder builder = new SelectionBuilder();
        switch (match) {
            case CLIENTES: {
                return builder.table(Tables.CLIENTES);
            }
            case CLIENTES_ID: {
	        	final String clienteId = Clientes.getClienteId(uri);
                return builder.table(Tables.CLIENTES)
                        .where(Clientes.cliente_id + "=?", clienteId);
	        }
            case CLIENTES_SEARCH: {
            	final String query = Clientes.getSearchQuery(uri);
                return builder.table(Tables.CLIENTES)
                        .where(ClientesColumns.cliente_nome + " LIKE '" + query + "%'");
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }
}
