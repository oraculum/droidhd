package oraculum.droid.ui;

import oraculum.droid.R;
import oraculum.droid.provider.DroidHDContract;
import oraculum.droid.provider.DroidHDContract.Clientes;
import oraculum.droid.util.NotifyingAsyncQueryHandler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ClientesFragment  extends ListFragment implements
		NotifyingAsyncQueryHandler.AsyncQueryListener {
	
	private static final String STATE_CHECKED_POSITION = "checkedPosition";
	
	private Cursor mCursor;
	private CursorAdapter mAdapter;
	private NotifyingAsyncQueryHandler mHandler;
	private int mCheckedPosition = -1;
    private boolean mHasSetEmptyText = false;
    private Bundle arguments;
	
    public ClientesFragment(Bundle arg) {
        arguments = arg;
    }
    
    public ClientesFragment(){

    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mHandler = new NotifyingAsyncQueryHandler(getActivity().getContentResolver(), this);
		setHasOptionsMenu(true);
		
       	if (arguments == null)
       		reloadFromArguments(getArguments());
       	else
       		reloadFromArguments(arguments);
	}
	
	public void reloadFromArguments(Bundle arguments) {
        // Teardown from previous arguments
        if (mCursor != null) {
            getActivity().stopManagingCursor(mCursor);
            mCursor = null;
        }

        mCheckedPosition = -1;
        setListAdapter(null);

        mHandler.cancelOperation(SearchQuery._TOKEN);

        // Load new arguments
        final Intent intent = BaseActivity.fragmentArgumentsToIntent(arguments);
        final Uri clienteUri = intent.getData();
        final int clienteQueryToken;

        if (clienteUri == null) {
            return;
        }

        String[] projection;
        if (!DroidHDContract.Clientes.isSearchUri(clienteUri)) {
            mAdapter = new ClienteAdapter(getActivity());
            projection = ClientesQuery.PROJECTION;
            clienteQueryToken = ClientesQuery._TOKEN;

        } else {
            mAdapter = new SearchAdapter(getActivity());
            projection = SearchQuery.PROJECTION;
            clienteQueryToken = SearchQuery._TOKEN;
        }

        setListAdapter(mAdapter);

        // Start background query to load enderecos
        mHandler.startQuery(clienteQueryToken, null, clienteUri, projection, null, null,
                DroidHDContract.Clientes.DEFAULT_SORT);
    }
	
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.clientes_menu_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_add_cliente) {
            if (mHasSetEmptyText) {
                Toast.makeText(getActivity(), R.string.btn_add_cliente,
                        Toast.LENGTH_SHORT).show();
                
                createNewCliente();
            } else {
            	Toast.makeText(getActivity(), R.string.btn_add_cliente,
                        Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
	}
	
	private void createNewCliente() {
    	final Uri clienteUri = Clientes.buildClienteUri(DroidHDContract.PATH_NEW);
    	final Intent intent = new Intent(Intent.ACTION_VIEW, clienteUri);
    	((BaseActivity) getActivity()).openActivityOrFragment(intent);
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
        if (savedInstanceState != null) {
            mCheckedPosition = savedInstanceState.getInt(STATE_CHECKED_POSITION, -1);
        }

        if (!mHasSetEmptyText) {
            setEmptyText(getString(R.string.empty_clientes));
            mHasSetEmptyText = true;
        }
	}
	
	/** {@inheritDoc} */
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {
        if ( mCursor != null ) {
        	// In case cancelOperation() doesn't work and we end up with consecutive calls to this
            // callback.
        	getActivity().stopManagingCursor(mCursor);
        	mCursor = null;
        }
        
        mCursor = cursor;
        getActivity().startManagingCursor(mCursor);
        mAdapter.changeCursor(cursor);
        
        if ( mCheckedPosition >= 0 && getView() != null ) {
        	getListView().setItemChecked(mCheckedPosition, true);
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        final Cursor cursor = (Cursor) mAdapter.getItem(position);
        final String clienteId = cursor.getString(ClientesQuery.CLIENTE_ID);
        final Uri clienteUri = DroidHDContract.Clientes.buildClienteUri(clienteId);

        final Intent intent = new Intent(Intent.ACTION_VIEW, clienteUri);       
        
        ((BaseActivity) getActivity()).openActivityOrFragment(intent);
        getListView().setItemChecked(position, true);
        mCheckedPosition = position;
        
    }
    
    public class ClienteAdapter extends CursorAdapter {
    	
        private Activity mActivity;

        public ClienteAdapter(Activity activity) {
            super(activity, null);
            mActivity = activity;
        }
        
        /** {@inheritDoc} */
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return mActivity.getLayoutInflater().inflate(R.layout.list_item_clientes_link, parent,
                    false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
        	final TextView titleView = (TextView) view.findViewById(R.id.cliente_title);
        	final TextView subtitleView = (TextView) view.findViewById(R.id.cliente_subtitle);
        	
        	titleView.setText(cursor.getString(ClientesQuery.NOME));
        	subtitleView.setText(cursor.getString(ClientesQuery.TEL));
        }

    }
    
    /**
     * {@link CursorAdapter} that renders a {@link SearchQuery}.
     */
    private class SearchAdapter extends CursorAdapter {
        public SearchAdapter(Context context) {
            super(context, null);
        }

        /** {@inheritDoc} */
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return getActivity().getLayoutInflater().inflate(R.layout.list_item_clientes_link, parent,
                    false);
        }

        /** {@inheritDoc} */
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
        	final TextView titleView = (TextView) view.findViewById(R.id.cliente_title);
        	final TextView subtitleView = (TextView) view.findViewById(R.id.cliente_subtitle);
        	
        	titleView.setText(cursor.getString(SearchQuery.NOME));
        	subtitleView.setText(cursor.getString(SearchQuery.TEL));
        }
    }
    
    /** {@link oraculum.droid.provider.DroidHDContract.Clientes} query parameters. */
    public interface ClientesQuery {
        int _TOKEN = 0x1;

        String[] PROJECTION = {
        		BaseColumns._ID,
                DroidHDContract.Clientes.cliente_id,   
                DroidHDContract.Clientes.cliente_nome,
                DroidHDContract.Clientes.cliente_tel,
        };       

        int _ID = 0;
        int CLIENTE_ID = 1;
        int NOME = 2;
        int TEL = 3;
    }
    
    /** {@link oraculum.droid.provider.DroidHDContract.Clientes} search query
     * parameters. */
    private interface SearchQuery {
        int _TOKEN = 0x3;

        String[] PROJECTION = {
        		BaseColumns._ID,
        		DroidHDContract.Clientes.cliente_id, 
                DroidHDContract.Clientes.cliente_nome,
                DroidHDContract.Clientes.cliente_tel,   
        };       
		@SuppressWarnings("unused")
		int _ID = 0;
		@SuppressWarnings("unused")
		int CLIENTE_ID = 1;
        int NOME = 2;
        int TEL = 3;
    }
    
    
	
}
