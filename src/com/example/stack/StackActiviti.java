package com.example.stack;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Build;

public class StackActiviti extends Activity {
	private static StacksDataSource datasource;
	public static List<Stack> stackValues;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stack_activiti);

		// Button btn = (Button) findViewById(R.id.buttonMenu);
		// registerForContextMenu(btn);

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
		menu.setHeaderTitle("Context Menu");
		menu.add(0, v.getId(), 0, "Action 1");
		menu.add(0, v.getId(), 0, "Action 2");

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getTitle() == "Action 1") {
			Toast.makeText(this, "Action 1 invoked", Toast.LENGTH_SHORT).show();
		} else if (item.getTitle() == "Action 2") {
			Toast.makeText(this, "Action 2 invoked", Toast.LENGTH_SHORT).show();
		} else {
			return false;
		}
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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements
			OnClickListener {
		Button bPop;
		Button bPush;
		Button bDeLast;
		EditText inputTextOkno;

		public PlaceholderFragment() {
			stackValues = new ArrayList<StackActiviti.Stack>();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View rootView = inflater.inflate(R.layout.fragment_stack_activiti,
					container, false);
			bPop = (Button) rootView.findViewById(R.id.buttonPop);
			bPush = (Button) rootView.findViewById(R.id.buttonPush);
			bDeLast = (Button) rootView.findViewById(R.id.buttonDeLast);
			bPop.setOnClickListener(this);
			bPush.setOnClickListener(this);
			bDeLast.setOnClickListener(this);
			inputTextOkno = (EditText) rootView
					.findViewById(R.id.editTextInput);

			return rootView;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String toastMessage;
			if (((Button) v).getId() == bPush.getId()) {

				String messageStack = inputTextOkno.getText().toString();
				datasource.createStack(messageStack);

				toastMessage = String.format(
						getResources().getString(R.string.push_Message),
						messageStack, datasource.getCountStacks());

				Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT)
						.show();

			}
			if (((Button) v).getId() == bPop.getId()) {
				datasource.beTrans();
				Stack firstStack = datasource.getFirstStack();
				if (firstStack != null) {
					long id = firstStack.getId();
					int err = datasource.deleteStack(id);
					if (err == -1) {
						toastMessage = getResources().getString(R.string.error);
					}

					toastMessage = String.format(
							getResources().getString(R.string.pop_Message),
							firstStack.getStack(), datasource.getCountStacks());

				} else {
					toastMessage = getResources().getString(R.string.error);

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
						toastMessage = getResources().getString(R.string.error);

					}
					datasource.endTrans();

					toastMessage = String.format(
							getResources().getString(R.string.delete_Message),
							lastStack.getStack(), datasource.getCountStacks());

				} else {
					toastMessage = getResources().getString(R.string.error);

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

		public void deleteDatabaseStack() {
			beTrans();
			database.execSQL(DB_Helper.DROP_TABLE);
			database.execSQL(DB_Helper.DATABASE_CREATE);
			endTrans();

		}

		public void clearDatabaseStack() {
			beTrans();
			database.delete(DB_Helper.TABLE_STACKS, "1", null);
			endTrans();

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
