package oraculum.droid.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class DialogsTools {
	public static void showWarnDialog(Context context, String title, String msg) {
		new AlertDialog.Builder(context)
				.setIcon(android.R.drawable.ic_dialog_alert).setTitle(title)
				.setMessage(msg).setPositiveButton("OK", null).show();
	}
	public static void showQuestionDialog(Context context, String title, String msg,
			DialogInterface.OnClickListener yesListener, DialogInterface.OnClickListener noListener) {
		new AlertDialog.Builder(context)
				.setIcon(android.R.drawable.ic_dialog_alert).setTitle(title)
				.setMessage(msg).setPositiveButton("Sim", (OnClickListener) yesListener)
				.setNegativeButton("Não", (OnClickListener) noListener).show();
	}
}