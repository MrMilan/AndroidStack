package com.example.stack;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;

public class StackActiviti extends Activity {
	private static StacksDataSource datasource;
	public static List<Stack> stackValues; 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stack_activiti);
		
		datasource = new StacksDataSource(this);
	    datasource.open();

	    if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
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
				
				toastMessage = "Word" + messageStack + "is saved"+" items in stack is"+(stackValues.size()+1);
				
				Toast.makeText(getActivity(),toastMessage,Toast.LENGTH_SHORT).show();
				
				

			}
			if (((Button) v).getId() == bPop.getId()) {
				int first =stackValues.size()-1;
				long id =stackValues.get(first).getId();
				datasource.deleteStack(id);
				stackValues.remove(first);
				toastMessage ="PoPed first..."+" size is"+stackValues.size();
				Toast.makeText(getActivity(),toastMessage ,	Toast.LENGTH_SHORT).show();
				

			}
			
			if (true) {
				//delete last
				int last =stackValues.size()-1;
				long id =stackValues.get(last).getId();
				datasource.deleteStack(id);
				stackValues.remove(last);
				
				toastMessage ="Deleted last..."+" size is"+stackValues.size();
				
				Toast.makeText(getActivity(),toastMessage ,Toast.LENGTH_SHORT).show();
				
				

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
		private static final String DROP_TABLE="DROP TABLE " + TABLE_STACKS;

		// Database creation sql statement
		private static final String DATABASE_CREATE = "create table "
				+ TABLE_STACKS + "(" + COLUMN_ID
				+ " integer primary key autoincrement, " + COLUMN_STACK
				+ " text not null);";

		
		
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

		public long getIdLastStack()
		{
			Cursor c = database.query(DB_Helper.TABLE_STACKS,
					new String[] { "max(" + DB_Helper.COLUMN_ID + ")" },null, null, null, null,
					null);
			c.moveToFirst();
			int rowID = c.getInt(0);
			return rowID;
			
		}
		
		public long getIdFirstStack()
		{
			Cursor c = database.query(DB_Helper.TABLE_STACKS,
					new String[] { "min(" + DB_Helper.COLUMN_ID + ")" },null, null, null, null,
					null);
			c.moveToFirst();
			int rowID = c.getInt(0);
			return rowID;
			
		}
		
		public void deleteStack(long id) {
			database.delete(DB_Helper.TABLE_STACKS, DB_Helper.COLUMN_ID + " = "
					+ id, null);
		}
		
		public void clearDatabaseStack() {
			database.execSQL(DB_Helper.DROP_TABLE);
			database.execSQL(DB_Helper.DATABASE_CREATE);
			
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
