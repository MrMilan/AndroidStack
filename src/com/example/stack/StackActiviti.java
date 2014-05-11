package com.example.stack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StackActiviti extends Activity {
	private static StacksDataSource datasource;
	public static EditText editTextInput;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stack_activiti);

		getActionBar().setTitle(R.string.app_name);

		datasource = new StacksDataSource(this);
		datasource.open();

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle(getResources().getString(R.string.clearMenu));
		menu.add(0, R.id.clearInput, 0, getResources()
				.getString(R.string.clear));
		menu.add(0, R.id.clearDB, 0, getResources()
				.getString(R.string.clear_DB));
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		String toastMessage = null;
		if (item.getItemId() == R.id.clearInput) {
			editTextInput.setText("");
			toastMessage = getResources()
					.getString(R.string.message_ClearInput);
		} else if (item.getItemId() == R.id.clearDB) {
			if (datasource.clearDatabaseStack() != 0) {
				toastMessage = getResources().getString(
						R.string.errorMessage_EmptyDB);
			} else {
				toastMessage = getResources().getString(
						R.string.message_ClearDB);
			}
		} else {
			toastMessage = getResources().getString(
					R.string.errorMessage_Global);
			Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
			return false;
		}
		Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stack_activiti, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		String languageToLoad = "en";
		if (id == R.id.iteMcsech) {
			languageToLoad = "cs";
			changeLang(languageToLoad);
			return true;
		}
		if (id == R.id.iteMengo) {
			changeLang(languageToLoad);
			return true;
		}
		if (id == R.id.iteMshoot) {
			// System.exit(0);
			// this.finish();
			android.os.Process.killProcess(android.os.Process.myPid());
			// finish();
			// Process.killProcess(android.os.Process.myPid());
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * @param languageToLoad
	 */
	public void changeLang(String languageToLoad) {
		Locale locale = new Locale(languageToLoad);

		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		Resources resources = getResources();
		resources.updateConfiguration(config, resources.getDisplayMetrics());
		recreate();

	}

	/**
	 * A placeholder fragment containing a simple view.
	 */

	public static class PlaceholderFragment extends Fragment implements
			OnClickListener {
		Button bPop;
		Button bPush;
		Button bDeLast;

		Button bClear;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View rootView = inflater.inflate(R.layout.fragment_stack_activiti,
					container, false);
			bPop = (Button) rootView.findViewById(R.id.buttonPop);
			bPush = (Button) rootView.findViewById(R.id.buttonPush);
			bDeLast = (Button) rootView.findViewById(R.id.buttonDeLast);
			editTextInput = (EditText) rootView
					.findViewById(R.id.editTextInput);
			editTextInput.setOnClickListener(this);
			bPop.setOnClickListener(this);
			bPush.setOnClickListener(this);
			bDeLast.setOnClickListener(this);

			bClear = (Button) rootView.findViewById(R.id.buttonMenu);
			registerForContextMenu(bClear);

			return rootView;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String toastMessage;

			if (((Button) v).getId() == bClear.getId()) {

				toastMessage = getResources().getString(
						R.string.message_ClearMenu);

			}
			if (((Button) v).getId() == bPush.getId()) {

				String messageStack = editTextInput.getText().toString();
				int toasTime;
				if (messageStack.matches("")||messageStack.matches("\\s+")) {
					toastMessage = getResources().getString(
							R.string.errorMessage_EmptyInput);
					toasTime = Toast.LENGTH_LONG;
				} else {
					datasource.createStack(messageStack);
					toasTime = Toast.LENGTH_SHORT;
					toastMessage = String.format(
							getResources().getString(R.string.message_Push),
							messageStack, datasource.getCountStacks());
				}
				Toast.makeText(getActivity(), toastMessage, toasTime)
						.show();

			}
			if (((Button) v).getId() == bPop.getId()) {
				datasource.beTrans();
				Stack firstStack = datasource.getFirstStack();
				if (firstStack != null) {
					long id = firstStack.getId();
					int err = datasource.deleteStack(id);
					if (err == -1) {
						toastMessage = getResources().getString(
								R.string.errorMessage_EmptyDB);
					}

					toastMessage = String.format(
							getResources().getString(R.string.message_Pop),
							firstStack.getStack(), datasource.getCountStacks());

				} else {
					toastMessage = getResources().getString(
							R.string.errorMessage_Global);

				}
				datasource.endTrans();

				Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT)
						.show();

			}

			if (((Button) v).getId() == bDeLast.getId()) {
				// delete last
				datasource.beTrans();
				Stack lastStack = datasource.getLastStack();
				if (lastStack != null) {
					long id = lastStack.getId();

					int err = datasource.deleteStack(id);
					if (err == -1) {
						toastMessage = getResources().getString(
								R.string.errorMessage_EmptyDB);

					}
					datasource.endTrans();
					toastMessage = String.format(
							getResources().getString(R.string.message_Delete),
							lastStack.getStack(), datasource.getCountStacks());

				} else {
					toastMessage = getResources().getString(
							R.string.errorMessage_Global);
				}
				Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT)
						.show();

			}
		}

	}

	// ziskano z tutu
	// http://www.vogella.com/tutorials/AndroidSQLite/article.html

	class DB_Helper extends SQLiteOpenHelper {

		public static final String TABLE_STACKS = "stacks";
		public static final String COLUMN_ID = "_id";
		public static final String COLUMN_STACK = "stack";

		private static final String DATABASE_NAME = "stack.db";
		private static final int DATABASE_VERSION = 1;
		private static final String DROP_TABLE = "DROP TABLE " + TABLE_STACKS;

		// Database creation sql statement
		private static final String DATABASE_CREATE = "CREATE TABLE "
				+ TABLE_STACKS + "(" + COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_STACK
				+ " TEXT NOT NULL);";

		public DB_Helper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(DB_Helper.class.getName(), "Upgrading database from version "
					+ oldVersion + " to " + newVersion
					+ ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_STACKS);
			onCreate(db);

		}

	}

	public class Stack {
		private long id;
		private String stack;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getStack() {
			return stack;
		}

		public void setStack(String stack) {
			this.stack = stack;
		}

		// Will be used by the ArrayAdapter in the ListView
		@Override
		public String toString() {
			return stack;
		}
	}

	public class StacksDataSource {

		// Database fields
		private SQLiteDatabase database;
		private DB_Helper dbHelper;
		private String[] allColumns = { DB_Helper.COLUMN_ID,
				DB_Helper.COLUMN_STACK };

		public StacksDataSource(Context context) {
			dbHelper = new DB_Helper(context);
		}

		public void open() throws SQLException {
			database = dbHelper.getWritableDatabase();
		}

		public void close() {
			dbHelper.close();
		}

		public Stack createStack(String stack) {
			ContentValues values = new ContentValues();
			values.put(DB_Helper.COLUMN_STACK, stack);
			long insertId = database.insert(DB_Helper.TABLE_STACKS, null,
					values);
			Cursor cursor = database.query(DB_Helper.TABLE_STACKS, allColumns,
					DB_Helper.COLUMN_ID + " = " + insertId, null, null, null,
					null);
			cursor.moveToFirst();
			Stack newStack = cursorToStack(cursor);
			cursor.close();
			return newStack;
		}

		public Stack getFirstStack() {
			if (getCountStacks() > 0) {
				Cursor c = database.query(DB_Helper.TABLE_STACKS, null, null,
						null, null, null, DB_Helper.COLUMN_ID + " DESC", "1");
				c.moveToFirst();
				Stack newStack = cursorToStack(c);
				c.close();
				return newStack;
			} else {
				return null;
			}
		}

		public Stack getLastStack() {
			if (getCountStacks() > 0) {
				Cursor c = database.query(DB_Helper.TABLE_STACKS, null, null,
						null, null, null, DB_Helper.COLUMN_ID + " ASC", "1");
				c.moveToFirst();
				Stack newStack = cursorToStack(c);
				c.close();
				return newStack;
			} else {
				return null;
			}
		}

		public long getIdLastStack() {
			if (getCountStacks() > 0) {
				Cursor c = database.query(DB_Helper.TABLE_STACKS,
						new String[] { "max(" + DB_Helper.COLUMN_ID + ")" },
						null, null, null, null, null);
				c.moveToFirst();
				int rowID = c.getInt(0);
				c.close();
				return rowID;
			} else {
				return -1;
			}
		}

		public long getIdFirstStack() {
			if (getCountStacks() > 0) {
				Cursor c = database.query(DB_Helper.TABLE_STACKS,
						new String[] { "min(" + DB_Helper.COLUMN_ID + ")" },
						null, null, null, null, null);
				c.moveToFirst();
				int rowID = c.getInt(0);
				c.close();
				return rowID;
			} else {
				return -1;
			}
		}

		public int deleteStack(long id) {

			if (getCountStacks() > 0) {
				database.delete(DB_Helper.TABLE_STACKS, DB_Helper.COLUMN_ID
						+ " = " + id, null);

				return 0;
			} else {
				return -1;
			}
		}

		public int deleteDatabaseStack() {
			if (getCountStacks() > 0) {
				database.beginTransaction();
				database.execSQL(DB_Helper.DROP_TABLE);
				database.execSQL(DB_Helper.DATABASE_CREATE);
				database.setTransactionSuccessful();
				database.endTransaction();
				return 0;
			} else {
				return -1;
			}

		}

		public int clearDatabaseStack() {
			if (getCountStacks() > 0) {
				beTrans();
				database.delete(DB_Helper.TABLE_STACKS, "1", null);
				endTrans();
				return 0;
			} else {
				return -1;
			}

		}

		public void beTrans() {
			database.beginTransaction();
		}

		public void endTrans() {
			database.setTransactionSuccessful();
			database.endTransaction();
		}

		public long getCountStacks() {
			Cursor cursor = database.query(DB_Helper.TABLE_STACKS, allColumns,
					null, null, null, null, null);
			long count = cursor.getCount();
			cursor.close();
			return count;
		}

		public List<Stack> getAllStacks() {
			List<Stack> stacks = new ArrayList<Stack>();

			Cursor cursor = database.query(DB_Helper.TABLE_STACKS, allColumns,
					null, null, null, null, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Stack stack = cursorToStack(cursor);
				stacks.add(stack);
				cursor.moveToNext();
			}
			// make sure to close the cursor
			cursor.close();
			return stacks;
		}

		private Stack cursorToStack(Cursor cursor) {
			Stack stack = new Stack();
			stack.setId(cursor.getLong(0));
			stack.setStack(cursor.getString(1));
			return stack;
		}
	}
}
