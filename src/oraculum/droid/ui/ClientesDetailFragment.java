package oraculum.droid.ui;

import java.util.UUID;

import oraculum.droid.R;
import oraculum.droid.provider.DroidHDContract;
import oraculum.droid.provider.DroidHDContract.Clientes;
import oraculum.droid.ui.ClientesFragment.ClientesQuery;
import oraculum.droid.util.DialogsTools;
import oraculum.droid.util.NotifyingAsyncQueryHandler;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ClientesDetailFragment extends Fragment implements
					NotifyingAsyncQueryHandler.AsyncQueryListener,
					CompoundButton.OnCheckedChangeListener{

	private Uri mClienteUri;
	private Clientes cliente;
	private ViewGroup mRootView;
	private NotifyingAsyncQueryHandler mCliente;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent intent = BaseActivity
				.fragmentArgumentsToIntent(getArguments());
		mClienteUri = intent.getData();

		if (mClienteUri == null) {
			return;
		}

		setHasOptionsMenu(true);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (mClienteUri == null) {
			return;
		}

		if ( Clientes.isNewUri(mClienteUri) ) {
			// Create one new Familia object
			cliente = new Clientes();
			//updateEnderecoDetailFragment();
			return;
		} else {
			mCliente = new NotifyingAsyncQueryHandler(getActivity()
					.getContentResolver(), this);
			mCliente.startQuery(ClienteQuery._TOKEN, mClienteUri,
					ClienteQuery.PROJECTION);
		}

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	
		mRootView = (ViewGroup) inflater.inflate(
				R.layout.clientes_form, null);
		
		Button btnSalvar = (Button) mRootView.findViewById(R.id.btnSalvar);
		btnSalvar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onSave();
			}
		});
		
		Button btnVoltar = (Button) mRootView.findViewById(R.id.btnVoltar);
		btnVoltar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				redirectListClientes();
			}
		});
		
		Button btnApagar = (Button) mRootView.findViewById(R.id.btnApagar);
		if ( Clientes.isNewUri(mClienteUri) ) {
			btnApagar.setVisibility(View.INVISIBLE);
		}
		else {
			btnApagar.setVisibility(View.VISIBLE);
			btnApagar.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					showAlertDel();
				}
			});
		}
		
		
		// Load UF list
		final Spinner ufSpinner = (Spinner) mRootView.findViewById(R.id.spEstado);
		final ArrayAdapter<CharSequence> ufApapter = ArrayAdapter.createFromResource(mRootView.getContext(), R.array.UfLabels, android.R.layout.simple_spinner_item);
		ufSpinner.setAdapter(ufApapter);
		// definindo rio como default
		ufSpinner.setSelection(18);
		
		return mRootView;
	}
	
	private Boolean validateCliente() {
		try {
			cliente.nome = ((EditText)mRootView.findViewById(R.id.txtNome)).getText().toString();
			cliente.tel = ((EditText)mRootView.findViewById(R.id.txtTel)).getText().toString();
			cliente.tel2 = ((EditText)mRootView.findViewById(R.id.txtTel2)).getText().toString();
			cliente.email = ((EditText)mRootView.findViewById(R.id.txtEmail)).getText().toString();
			cliente.end = ((EditText)mRootView.findViewById(R.id.txtEnd)).getText().toString();
			cliente.num = ((EditText)mRootView.findViewById(R.id.txtNum)).getText().toString();
			cliente.comp = ((EditText)mRootView.findViewById(R.id.txtComp)).getText().toString();
			cliente.bairro = ((EditText)mRootView.findViewById(R.id.txtBairro)).getText().toString();
			cliente.cidade = ((EditText)mRootView.findViewById(R.id.txtCidade)).getText().toString();
			cliente.cep = ((EditText)mRootView.findViewById(R.id.txtCEP)).getText().toString();
			
			int ufPosition = ((Spinner)mRootView.findViewById(R.id.spEstado)).getSelectedItemPosition();
			cliente.estado = mRootView.getResources().getStringArray(R.array.UfValues)[ufPosition];
			
			cliente._id = UUID.randomUUID().toString();
			
			return cliente.isValid();
		} catch (Exception e) {
			DialogsTools.showWarnDialog(getActivity(), "Aviso", e.toString());
		}
		return false;
	}
	
	private void onSave() {
		if (validateCliente()) {
			mCliente = new NotifyingAsyncQueryHandler(getActivity()
					.getContentResolver(), this);
			
			// Save or Update
			if ( Clientes.isNewUri(mClienteUri)) {
				mCliente.startInsert(mClienteUri, cliente.toContentValues());
			} else {
				mCliente.startUpdate(mClienteUri, cliente.toContentValues());
			}
			
			Toast.makeText(getActivity(), R.string.save_cliente,
	                Toast.LENGTH_SHORT).show();
			
			redirectListClientes();
		}
	}
	
	private void showAlertDel() {
		AlertDialog.Builder delAlert = new AlertDialog.Builder(getActivity())
        .setTitle(R.string.alerta_title)
        .setIcon(android.R.drawable.ic_delete)
        .setMessage(getString(R.string.alerta_text))
        .setCancelable(false)
        .setPositiveButton(R.string.accept,
            new android.content.DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    onDelete();
                    dialog.dismiss();
                }
            })
        .setNegativeButton(R.string.decline,
            new android.content.DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
	
	delAlert.show();
	}
	
	private void onDelete() {
		mCliente.startDelete(mClienteUri);
		
		Toast.makeText(getActivity(), R.string.del_cliente,
                Toast.LENGTH_SHORT).show();
		
		redirectListClientes();
		
	}
	
	private void redirectListClientes() {
		final Intent intent = new Intent(Intent.ACTION_VIEW, Clientes.CONTENT_URI);
    	intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.title_list_clientes));
        startActivity(intent);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	}
	


	@Override
	public void onQueryComplete(int token, Object cookie, Cursor cursor) {
		if (getActivity() == null) {
			return;
		}

		if (token == ClientesQuery._TOKEN) {
			try {
				cliente = new Clientes();
	
				if (!cursor.moveToFirst()) {
					return;
				}
	
				cliente._id = (cursor.getString(ClienteQuery.cliente_id));
				cliente.nome = (cursor.getString(ClienteQuery.nome));
				cliente.tel = (cursor.getString(ClienteQuery.tel));
				cliente.tel2 = (cursor.getString(ClienteQuery.tel2));
				cliente.email = (cursor.getString(ClienteQuery.email));
				cliente.end = (cursor.getString(ClienteQuery.end));
				cliente.num = (cursor.getString(ClienteQuery.num));
				cliente.comp = (cursor.getString(ClienteQuery.comp));
				cliente.bairro = (cursor.getString(ClienteQuery.bairro));
				cliente.cidade = (cursor.getString(ClienteQuery.cidade));
				cliente.estado = (cursor.getString(ClienteQuery.estado));
				cliente.cep = (cursor.getString(ClienteQuery.cep));
				
				updateClienteDetailFragment();
			} finally {
				cursor.close();
			}
		}
		

	}
	
	private void updateClienteDetailFragment() {
		final EditText nomeText = (EditText) mRootView
				.findViewById(R.id.txtNome);
		final EditText telText = (EditText) mRootView
				.findViewById(R.id.txtTel);
		final EditText tel2Text = (EditText) mRootView
				.findViewById(R.id.txtTel2);
		final EditText emailText = (EditText) mRootView
				.findViewById(R.id.txtEmail);
		final EditText endText = (EditText) mRootView
				.findViewById(R.id.txtEnd);
		final EditText numText = (EditText) mRootView
				.findViewById(R.id.txtNum);
		final EditText compText = (EditText) mRootView
				.findViewById(R.id.txtComp);
		final EditText bairroText = (EditText) mRootView
				.findViewById(R.id.txtBairro);
		final EditText cidadeText = (EditText) mRootView
				.findViewById(R.id.txtCidade);
		final EditText cepText = (EditText) mRootView
				.findViewById(R.id.txtCEP);
		
		// Load UF list
		final Spinner estadoSpinner = (Spinner) mRootView.findViewById(R.id.spEstado);
		final ArrayAdapter<CharSequence> estadoApapter = ArrayAdapter.createFromResource(mRootView.getContext(), R.array.UfLabels, android.R.layout.simple_spinner_item);
		estadoSpinner.setAdapter(estadoApapter);
		
		nomeText.setText(cliente.nome);
		telText.setText(cliente.tel);
		tel2Text.setText(cliente.tel2);
		emailText.setText(cliente.email);
		endText.setText(cliente.end);
		numText.setText(cliente.num);
		compText.setText(cliente.comp);
		bairroText.setText(cliente.bairro);
		cidadeText.setText(cliente.cidade);
		cepText.setText(cliente.cep);
		
		String[] estadoValues = mRootView.getResources().getStringArray(R.array.UfValues);
		for ( int i = 0; i < estadoValues.length; i++ ) {
			if ( estadoValues[i].equals(cliente.estado) ) {
				estadoSpinner.setSelection(i);
				break;
			}
		}
	}
	
	
	/** {@link oraculum.droid.provider.DroidHDContract.Clientes} query parameters. */
    public interface ClienteQuery {
        int _TOKEN = 0x1;

        String[] PROJECTION = {  
        		DroidHDContract.Clientes.cliente_id,
        		DroidHDContract.Clientes.cliente_nome,
                DroidHDContract.Clientes.cliente_tel,
                DroidHDContract.Clientes.cliente_tel2,
                DroidHDContract.Clientes.cliente_email,
                DroidHDContract.Clientes.cliente_end,
                DroidHDContract.Clientes.cliente_num,
                DroidHDContract.Clientes.cliente_comp,
                DroidHDContract.Clientes.cliente_bairro,
                DroidHDContract.Clientes.cliente_cidade,
                DroidHDContract.Clientes.cliente_estado,
                DroidHDContract.Clientes.cliente_cep,
        };    
        int cliente_id = 0;
        int nome = 1;
        int tel = 2;
        int tel2 = 3;
        int email = 4;
        int end = 5;
        int num = 6;
        int comp = 7;
        int bairro = 8;
        int cidade = 9;
        int estado = 10;
        int cep = 11;
    }



}
