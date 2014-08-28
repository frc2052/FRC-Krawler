package com.team2052.frckrawler.fragment.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.models.User;

/**
 * @author Adam
 */
public class EditUserDialogFragment extends DialogFragment implements View.OnClickListener {
    public static final String USER_ID = "USER_ID";
    private User mUser;

    public static EditUserDialogFragment newInstance(User user) {
        EditUserDialogFragment fragment = new EditUserDialogFragment();
        Bundle b = new Bundle();
        b.putLong(USER_ID, user.getId());
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        mUser = User.load(User.class, b.getLong(USER_ID));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogactivity_edit_user, null);
        view.findViewById(R.id.saveUser).setOnClickListener(this);
        view.findViewById(R.id.remove).setOnClickListener(this);
        view.findViewById(R.id.cancel).setOnClickListener(this);
        getDialog().setTitle("Edit User");
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        ((EditText)view.findViewById(R.id.nameVal)).setText(mUser.name);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveUser:
                mUser.name = ((TextView) getView().findViewById(R.id.nameVal)).getText().toString();
                mUser.save();
                getActivity().startActivityForResult(getActivity().getIntent(), 10);
                dismiss();
                break;
            case R.id.remove:
                mUser.delete();
                //Call onResume
                getActivity().startActivityForResult(getActivity().getIntent(), 10);
                dismiss();
                break;
            case R.id.cancel:
                dismiss();
        }
    }
}
