package oraculum.droid.io;

import java.io.IOException;
import java.util.ArrayList;

import oraculum.droid.provider.DroidHDContract;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;

public class RemoteDroidHDHandler extends XmlHandler {
	
	@SuppressWarnings("unused")
	private RemoteExecutor mExecutor;

	public RemoteDroidHDHandler(RemoteExecutor executor) {
        super(DroidHDContract.CONTENT_AUTHORITY);
        mExecutor = executor;
    }

	@Override
	public ArrayList<ContentProviderOperation> parse(XmlPullParser parser,
			ContentResolver resolver) throws XmlPullParserException,
			IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
