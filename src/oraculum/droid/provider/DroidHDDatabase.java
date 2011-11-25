package oraculum.droid.provider;

import oraculum.droid.provider.DroidHDContract.ClientesColumns;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DroidHDDatabase extends SQLiteOpenHelper {
	private static final String TAG = "DroidHDDatabase";

    private static final String DATABASE_NAME = "droidhd.db";

    // NOTE: carefully update onUpgrade() when bumping database versions to make
    // sure user data is saved.

    private static final int VER_LAUNCH = 6;

    private static final int DATABASE_VERSION = VER_LAUNCH;
    
    interface Tables {
        String CLIENTES = "clientes";
        
        String SEARCH_SUGGEST = "search_suggest";
    }
    
    public DroidHDDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.CLIENTES + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ClientesColumns.cliente_id + " TEXT NOT NULL,"
                + ClientesColumns.cliente_nome + " TEXT NOT NULL,"
                + ClientesColumns.cliente_tel + " TEXT,"
                + ClientesColumns.cliente_tel2 + " TEXT,"
                + ClientesColumns.cliente_email + " TEXT,"
                + ClientesColumns.cliente_end + " TEXT,"
                + ClientesColumns.cliente_num + " TEXT,"
                + ClientesColumns.cliente_comp + " TEXT,"
                + ClientesColumns.cliente_bairro + " TEXT,"
                + ClientesColumns.cliente_cidade + " TEXT,"
                + ClientesColumns.cliente_estado + " TEXT,"
                + ClientesColumns.cliente_cep + " TEXT,"
                + "UNIQUE (" + ClientesColumns.cliente_id + ") ON CONFLICT REPLACE)");
    }
    
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);
        
        int version = oldVersion;
        
        Log.d(TAG, "after upgrade logic, at version " + version);
        if (version != DATABASE_VERSION) {
            Log.w(TAG, "Destroying old data during upgrade");
            
            db.execSQL("DROP TABLE IF EXISTS " + Tables.CLIENTES);
            
            onCreate(db);
        }
    }
}
