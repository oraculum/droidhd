package oraculum.droid.provider;

import java.util.List;

import oraculum.droid.util.ParserUtils;
import oraculum.droid.util.ValidationError;
import android.app.SearchManager;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

public class DroidHDContract {
	
	/**
     * Special value for {@link SyncColumns#UPDATED} indicating that an entry
     * has never been updated, or doesn't exist yet.
     */
    public static final long UPDATED_NEVER = -2;

    /**
     * Special value for {@link SyncColumns#UPDATED} indicating that the last
     * update time is unknown, usually when inserted from a local file source.
     */
    public static final long UPDATED_UNKNOWN = -1;
    
    
    public interface SyncColumns {
        /** Last time this entry was updated or synchronized. */
        String UPDATED = "updated";
    }
    
    interface ClientesColumns {
    	String cliente_id = "cliente_id";
        String cliente_nome = "nome";
        String cliente_tel = "tel";
        String cliente_tel2 = "tel2";
        String cliente_email = "email";
        String cliente_end = "end";
        String cliente_num = "num";
        String cliente_comp = "comp";
        String cliente_bairro = "bairro";
        String cliente_cidade = "cidade";
        String cliente_estado = "estado";
        String cliente_cep = "cep";
    }
    
    public static final String CONTENT_AUTHORITY = "oraculum.droid";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    
    private static final String PATH_CLIENTES = "clientes";
    
    public static final String PATH_AT = "at";
    public static final String PATH_BETWEEN = "between";
    public static final String PATH_NEW = "new";
    public static final String PATH_SEARCH = "search";
    public static final String PATH_SEARCH_SUGGEST = "search_suggest_query";
 
    public static class Clientes implements ClientesColumns, SyncColumns, BaseColumns {
    	public String _id;
    	public String nome;
		public String tel;
		public String tel2;
		public String email;
		public String end;
		public String num;
		public String comp;
		public String bairro;
		public String cidade;
		public String estado;
		public String cep;
    	
    	public static final Uri CONTENT_URI =
		        BASE_CONTENT_URI.buildUpon().appendPath(PATH_CLIENTES).build();
		
		public static final String CONTENT_TYPE =
		        "vnd.android.cursor.dir/vnd.oraculum.clientes";
		public static final String CONTENT_ITEM_TYPE =
		        "vnd.android.cursor.item/vnd.oraculum.clientes";
		
		public static final String SEARCH_SNIPPET = "search_snippet";
		
		/** Count of {@link Sessions} inside given block. */
        public static final String CLIENTES_COUNT = "sessions_count";

        /**
         * Flag indicating that at least one {@link Sessions#SESSION_ID} inside
         * this block has {@link Sessions#SESSION_STARRED} set.
         */
        public static final String CONTAINS_STARRED = "contains_starred";

		
		// TODO: shortcut primary track to offer sub-sorting here
		/** Default "ORDER BY" clause. */
		public static final String DEFAULT_SORT = ClientesColumns.cliente_nome + " COLLATE NOCASE ASC";
		
		/** Build {@link Uri} for requested {@link #_id}. */
		public static Uri buildClienteUri(String clienteId) {
		    return CONTENT_URI.buildUpon().appendPath(clienteId).build();
		}
		
		public static Uri buildClientesAtDirUri(long time) {
            return CONTENT_URI.buildUpon().appendPath(PATH_AT).appendPath(String.valueOf(time))
                    .build();
        }
		
		public static Uri buildSearchUri(String query) {
			return CONTENT_URI.buildUpon().appendPath(PATH_SEARCH).appendPath(query).build();
		}
		
		public static boolean isSearchUri(Uri uri) {
		    List<String> pathSegments = uri.getPathSegments();
		    return pathSegments.size() >= 2 && PATH_SEARCH.equals(pathSegments.get(1));
		}
		
		public static String getClienteId(Uri uri) {
	        return uri.getPathSegments().get(1);
	    }
		
		public static boolean isNewUri(Uri uri) {
			List<String> pathSegments = uri.getPathSegments();
	        return PATH_NEW.equals(pathSegments.get(1));
		}

		public static String getSearchQuery(Uri uri) {
		    return uri.getPathSegments().get(2);
		}
		
		/**
		 * Generate a {@link #SESSION_ID} that will always match the requested
		 * {@link Sessions} details.
		 */
		public static String generateClienteId(String nome) {
		    return ParserUtils.sanitizeId(nome);
		}
		
		public static class SearchSuggest {
	        public static final Uri CONTENT_URI =
	                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SEARCH_SUGGEST).build();

	        public static final String DEFAULT_SORT = SearchManager.SUGGEST_COLUMN_TEXT_1
	                + " COLLATE NOCASE ASC";
	    }
		
		public boolean isValid() throws ValidationError {
			ValidationError error = new ValidationError();
			if ("".equals(nome))
				error.addMessage("Nome é obrigatório");
			
			if (error.getLength() > 0)
				throw error;
			
			return true;
		}
		
		public ContentValues toContentValues() {
			ContentValues vals = new ContentValues();

			vals.put(Clientes.cliente_id, this._id);
			vals.put(Clientes.cliente_nome, this.nome);
			vals.put(Clientes.cliente_tel, this.tel);
			vals.put(Clientes.cliente_tel2, this.tel2);
			vals.put(Clientes.cliente_email, this.email);
			vals.put(Clientes.cliente_end, this.end);
			vals.put(Clientes.cliente_num, this.num);
			vals.put(Clientes.cliente_comp, this.comp);
			vals.put(Clientes.cliente_bairro, this.bairro);
			vals.put(Clientes.cliente_cidade, this.cidade);
			vals.put(Clientes.cliente_estado, this.estado);
			vals.put(Clientes.cliente_cep, this.cep);

			return vals;
		}

    }

}
