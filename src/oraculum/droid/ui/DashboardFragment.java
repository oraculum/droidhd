package oraculum.droid.ui;

import oraculum.droid.R;
import oraculum.droid.provider.DroidHDContract.Clientes;
import oraculum.droid.ui.tablet.ClientesMultiPaneActivity;
import oraculum.droid.util.UIUtils;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DashboardFragment extends Fragment {
	public void fireTrackerEvent(String label) {
		Log.i("Home Screen Dashboard: Click", label);        
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container);
        
        
        // Vincular event handlers
        root.findViewById(R.id.home_btn_clientes).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                fireTrackerEvent("Clientes");
             // Iniciar lista de clientes
                if (UIUtils.isHoneycombTablet(getActivity())) {
                    startActivity(new Intent(getActivity(), ClientesMultiPaneActivity.class));
                } else {
                	final Intent intent = new Intent(Intent.ACTION_VIEW, Clientes.CONTENT_URI);
                	intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.title_list_clientes));
                    startActivity(intent);
                }   
            } 
        });
        return root;
    }
}
