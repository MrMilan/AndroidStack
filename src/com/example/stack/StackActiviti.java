package com.example.stack;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;

public class StackActiviti extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stack_activiti);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
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
    public static class PlaceholderFragment extends Fragment implements OnClickListener{
    	Button bPop;
    	Button bPush;
    	EditText inputTextOkno;
    	ArrayList<String> tasks;
    	

        public PlaceholderFragment() {
        	tasks = new ArrayList<String>();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	
            View rootView = inflater.inflate(R.layout.fragment_stack_activiti, container, false);
            bPop = (Button) rootView.findViewById(R.id.buttonPop);
            bPush = (Button) rootView.findViewById(R.id.buttonPush);
            bPop.setOnClickListener(this);
            bPush.setOnClickListener(this);
            inputTextOkno = (EditText) rootView.findViewById(R.id.editTextInput);
            
            return rootView;
        }

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (((Button) v).getId() == bPush.getId()) {
				Toast.makeText(getActivity(), "Word"+ inputTextOkno.getText()+"is saved", Toast.LENGTH_SHORT).show();
				
			}
			if (((Button) v).getId() == bPop.getId()) {
				Toast.makeText(getActivity(), "Button bPop poped...", Toast.LENGTH_SHORT).show();
				
			}
		}
    }

}
