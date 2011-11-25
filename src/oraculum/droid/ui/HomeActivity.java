package oraculum.droid.ui;

import oraculum.droid.R;
import oraculum.droid.service.SyncService;
import oraculum.droid.util.DetachableResultReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.widget.Toast;

public class HomeActivity extends BaseActivity {

	public static final String TAG = "HomeActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		getActivityHelper().setupActionBar(null, 0);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		getActivityHelper().setupHomeActivity();

	}

	private void updateRefreshStatus(boolean refreshing) {
		getActivityHelper().setRefreshActionButtonCompatState(refreshing);
	}

	/**
	 * A non-UI fragment, retained across configuration changes, that updates
	 * its activity's UI when sync status changes.
	 */
	public static class SyncStatusUpdaterFragment extends Fragment implements
			DetachableResultReceiver.Receiver {
		public static final String TAG = SyncStatusUpdaterFragment.class
				.getName();

		private boolean mSyncing = false;
		private DetachableResultReceiver mReceiver;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setRetainInstance(true);
			mReceiver = new DetachableResultReceiver(new Handler());
			mReceiver.setReceiver(this);
		}

		/** {@inheritDoc} */
		public void onReceiveResult(int resultCode, Bundle resultData) {
			HomeActivity activity = (HomeActivity) getActivity();
			if (activity == null) {
				return;
			}

			switch (resultCode) {
			case SyncService.STATUS_RUNNING: {
				mSyncing = true;
				break;
			}
			case SyncService.STATUS_FINISHED: {
				mSyncing = false;
				break;
			}
			case SyncService.STATUS_ERROR: {
				// Error happened down in SyncService, show as toast.
				mSyncing = false;
				final String errorText = getString(R.string.toast_sync_error,
						resultData.getString(Intent.EXTRA_TEXT));
				Toast.makeText(activity, errorText, Toast.LENGTH_LONG).show();
				break;
			}
			}

			activity.updateRefreshStatus(mSyncing);
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			((HomeActivity) getActivity()).updateRefreshStatus(mSyncing);
		}
	}

}
