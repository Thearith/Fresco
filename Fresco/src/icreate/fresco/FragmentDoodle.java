package icreate.fresco;

import com.actionbarsherlock.app.SherlockFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class FragmentDoodle extends Fragment {
	EditText editText;

	public static FragmentDoodle createFragment(String content) {
		Bundle bundle = new Bundle();
		bundle.putString(Constant.CONTENT, content);
		
		FragmentDoodle fragment = new FragmentDoodle();
		fragment.setArguments(bundle);
		
		return fragment;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_text, container, false);
		
		String content = this.getArguments().getString(Constant.CONTENT);
		
		editText = (EditText) view.findViewById(R.id.cardEditText);
		editText.setText(content);
		editText.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				String content = editText.getText().toString();
				((AddEditActivity)getActivity()).setContent(content);
			}
			
		});
        
        return view;
    }

}