package oraculum.droid.ui;

import oraculum.droid.R;
import oraculum.droid.provider.DroidHDContract.Clientes;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class SearchActivity extends BaseSinglePaneActivity {

    private String mQuery;
    //ClientesFragment mClientesFragment;
    
    @Override
    protected Fragment onCreatePane() {
    	Intent intent = getIntent();        
        mQuery = intent.getStringExtra(SearchManager.QUERY);
    	return new ClientesFragment(getClientesFragmentArguments());
    	}
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final CharSequence title = getString(R.string.title_search_query, mQuery);
        getActivityHelper().setActionBarTitle(title);
        
    }
    
    private Bundle getClientesFragmentArguments() {
        return intentToFragmentArguments(
                new Intent(Intent.ACTION_VIEW, Clientes.buildSearchUri(mQuery)));
    }
}
