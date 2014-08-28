package com.team2052.frckrawler.fragment.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.ListViewAdapter;

/**
 * @author Adam
 */
public class ImportDataSimpleDialogFragment extends DialogFragment {

    private String[] yearDropDownItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        yearDropDownItems = new String[GlobalValues.MAX_COMP_YEAR - GlobalValues.FIRST_COMP_YEAR + 2];
        yearDropDownItems[0] = "Select Year";
        for (int i = 1; i < yearDropDownItems.length; i++) {
            yearDropDownItems[i] = Integer.toString(GlobalValues.MAX_COMP_YEAR - i + 1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_import_simple, null);
        ((Spinner) view.findViewById(R.id.imoprt_year_spinner)).setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, yearDropDownItems));
        getDialog().setTitle("Import Event");
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return view;
    }
}
