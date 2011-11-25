package oraculum.droid.ui.phone;

import oraculum.droid.ui.BaseSinglePaneActivity;
import oraculum.droid.ui.ClientesFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ClientesActivity extends BaseSinglePaneActivity {
	@Override
    protected Fragment onCreatePane() {
        return new ClientesFragment();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
    }

}

