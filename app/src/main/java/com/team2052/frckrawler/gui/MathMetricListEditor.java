package com.team2052.frckrawler.gui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.team2052.frckrawler.database.models.Metric;


public class MathMetricListEditor extends ListEditor implements
        DialogInterface.OnClickListener
{

    private Metric[] choices;

    public MathMetricListEditor(Context _context, String[] _values, Metric[] _choices)
    {

        super(_context, _values);
        choices = _choices;
    }

    @Override
    protected void onAddButtonClicked()
    {
        CharSequence[] c = new CharSequence[choices.length];

        for (int i = 0; i < choices.length; i++)
            c[i] = choices[i].name;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Metric...");
        builder.setItems(c, this);
        builder.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        addValue(Long.toString(choices[which].getId()), choices[which].name);
        dialog.dismiss();
    }
}
