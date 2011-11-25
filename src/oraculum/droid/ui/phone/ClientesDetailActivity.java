package oraculum.droid.ui.phone;

import oraculum.droid.ui.BaseSinglePaneActivity;
import oraculum.droid.ui.ClientesDetailFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ClientesDetailActivity extends BaseSinglePaneActivity {
	@Override
    protected Fragment onCreatePane() {
        return new ClientesDetailFragment();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
    }

}
