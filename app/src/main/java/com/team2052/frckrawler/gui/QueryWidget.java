package com.team2052.frckrawler.gui;

import android.R;
import android.content.Context;
import android.os.AsyncTask;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.structures.Metric;
import com.team2052.frckrawler.database.structures.Query;

import java.util.ArrayList;

public class QueryWidget extends LinearLayout implements OnClickListener {

    int metricType;

    Button addButton;
    Metric[] metrics;
    ArrayList<QueryItem> querys;

    public QueryWidget(Context _context, Metric[] _metrics, int _metricType) {
        this(_context, _metrics, new Query[0], _metricType);
    }

    public QueryWidget(Context _context, Metric[] _metrics, Query[] _querys,
                       int _metricType) {
        super(_context);
        metricType = _metricType;
        metrics = _metrics;
        querys = new ArrayList<QueryItem>();

        if (_querys != null)
            for (Query q : _querys)
                querys.add(new QueryItem(getContext(), q));

        setOrientation(LinearLayout.VERTICAL);

        addButton = new Button(getContext());
        addButton.setText("Add Query...");
        addButton.setOnClickListener(this);
        addView(addButton);
    }

    public Query[] getQuerys() {
        Query[] q = new Query[querys.size()];

        for (int i = 0; i < q.length; i++)
            try {
                q[i] = querys.get(i).getQuery();
            } catch (QueryIncompleteException e) {
                Log.e("FRCKrawler", "QueryIncompleteException in QueryWidget");
            }

        return q;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == addButton.getId()) {
            querys.add(new QueryItem(getContext()));
            removeAllViews();
            for (QueryItem i : querys)
                addView(i);
            addView(addButton);
        }
    }

    public void refresh() {
        removeAllViews();
        for (QueryItem i : querys)
            addView(i);
        addView(addButton);
    }


    /**
     * **
     * Class: QueryItem
     *
     * @author Charles Hofer
     *         <p/>
     *         Description: a query element in the list of querys
     *         ***
     */

    private class QueryItem extends LinearLayout implements OnItemSelectedListener,
            OnClickListener {

        volatile boolean isComplete;
        volatile Button removeButton;
        volatile MyTextView frontText;
        volatile MyTextView equalTextView;
        volatile MyTextView isUsuallyTextView;
        volatile Spinner metricSelector;
        volatile Spinner operationSelector;
        volatile Spinner booleanValueSelector;
        volatile Spinner chooserValueSelector;
        volatile EditText valueEnterer;
        volatile Query query;

        public QueryItem(Context c) {
            this(c, null);
        }

        public QueryItem(Context c, Query q) {
            super(c);
            query = q;
            isComplete = false;
            removeButton = new MyButton(getContext(), "Remove Query", this);
            removeButton.setId(1);
            frontText = new MyTextView(getContext(), "Get robot when ", 18);
            equalTextView = new MyTextView(getContext(), " = ", 18);
            isUsuallyTextView = new MyTextView(getContext(), " is usually ", 18);
            metricSelector = new Spinner(getContext());
            operationSelector = new Spinner(getContext());
            booleanValueSelector = new Spinner(getContext());
            chooserValueSelector = new Spinner(getContext());
            valueEnterer = new EditText(getContext());
            setOrientation(LinearLayout.VERTICAL);
            new CreateUITask().execute(this);
        }

        @Override
        public void onItemSelected(AdapterView<?> adapter, View v, int pos,
                                   long id) {
            Log.d("FRCKrawler", "Item selected");
            if (adapter.getId() == metricSelector.getId()) {
                if (metrics[pos].getType() == DBContract.BOOLEAN &&
                        metricType != Query.TYPE_MATCH_DATA) {
                    this.removeAllViews();
                    addView(frontText);
                    addView(metricSelector);
                    addView(equalTextView);
                    addView(booleanValueSelector);
                    addView(removeButton);

                } else if (metrics[pos].getType() == DBContract.CHOOSER &&
                        metricType != Query.TYPE_MATCH_DATA) {
                    this.removeAllViews();
                    addView(frontText);
                    addView(metricSelector);
                    addView(equalTextView);
                    chooserValueSelector.setAdapter(new ArrayAdapter<Object>(getContext(),
                            R.layout.simple_spinner_item, metrics[pos].getRange()));
                    int selPos = -1;
                    if (query != null) {
                        for (int i = 0; i < metrics[pos].getRange().length; i++) {
                            if (query.getMetricValue().equals(metrics[pos]
                                    .getRange()[i])) {
                                selPos = i;
                                break;
                            }
                        }
                        if (selPos != -1)
                            chooserValueSelector.setSelection(selPos);
                    }
                    addView(chooserValueSelector);
                    addView(removeButton);

                } else if (metrics[pos].getType() == DBContract.CHOOSER &&
                        metricType == Query.TYPE_MATCH_DATA && !metrics[pos].isNumericChooser()) {
                    this.removeAllViews();
                    addView(frontText);
                    addView(metricSelector);
                    addView(isUsuallyTextView);
                    chooserValueSelector.setAdapter(new ArrayAdapter<Object>(getContext(),
                            R.layout.simple_spinner_item, metrics[pos].getRange()));
                    int selPos = -1;
                    if (query != null) {
                        for (int i = 0; i < metrics[pos].getRange().length; i++) {
                            if (query.getMetricValue().equals(metrics[pos]
                                    .getRange()[i])) {
                                selPos = i;
                                break;
                            }
                        }
                        if (selPos != -1)
                            chooserValueSelector.setSelection(selPos);
                    }
                    addView(chooserValueSelector);
                    addView(removeButton);


                } else if (metrics[pos].getType() == DBContract.TEXT) {
                    this.removeAllViews();
                    addView(frontText);
                    addView(metricSelector);
                    addView(equalTextView);
                    addView(valueEnterer);
                    valueEnterer.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                    addView(removeButton);

                } else {
                    this.removeAllViews();
                    addView(frontText);
                    addView(metricSelector);
                    addView(operationSelector);
                    addView(valueEnterer);
                    valueEnterer.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    addView(removeButton);
                }

                isComplete = true;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapter) {
        }

        public Query getQuery() throws QueryIncompleteException {
            if (!isComplete)
                throw new QueryIncompleteException();

            int metricID = metrics[metricSelector.getSelectedItemPosition()].getID();
            int operation;
            String value;

            boolean chooserIsNumeric = true;
            if (metrics[metricSelector.getSelectedItemPosition()].getType() ==
                    DBContract.CHOOSER) {
                for (Object o : metrics[metricSelector.getSelectedItemPosition()].getRange())
                    try {
                        Double.parseDouble(o.toString());
                    } catch (NumberFormatException e) {
                        chooserIsNumeric = false;
                        break;
                    }
            } else {
                chooserIsNumeric = false;
            }

            if (metrics[metricSelector.getSelectedItemPosition()].getType() ==
                    DBContract.BOOLEAN && metricType != Query.TYPE_MATCH_DATA) {
                operation = Query.COMPARISON_EQUAL_TO;
                value = booleanValueSelector.getSelectedItem().toString();

            } else if (metrics[metricSelector.getSelectedItemPosition()].getType() ==
                    DBContract.CHOOSER && metricType != Query.TYPE_MATCH_DATA) {
                operation = Query.COMPARISON_EQUAL_TO;
                value = chooserValueSelector.getSelectedItem().toString();

            } else if (metrics[metricSelector.getSelectedItemPosition()].getType() ==
                    DBContract.CHOOSER && metricType == Query.TYPE_MATCH_DATA &&
                    !chooserIsNumeric) {
                operation = Query.COMPARISON_CHOOSER_COMPARE;
                value = chooserValueSelector.getSelectedItem().toString();

            } else {
                operation = operationSelector.getSelectedItemPosition() + 1;
                value = valueEnterer.getText().toString();
            }

            return new Query(metricType, metricID, operation, value);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == removeButton.getId()) {
                querys.remove(this);
                refresh();
            }
        }

        private class CreateUITask extends AsyncTask<QueryItem, Void, Void> {

            @Override
            protected Void doInBackground(QueryItem... params) {
                String[] metricNames = new String[metrics.length];
                for (int i = 0; i < metricNames.length; i++)
                    metricNames[i] = metrics[i].getMetricName();


                metricSelector.setId(1);
                metricSelector.setOnItemSelectedListener(params[0]);
                metricSelector.setAdapter(new ArrayAdapter<String>(getContext(),
                        R.layout.simple_spinner_item, metricNames));

                operationSelector.setId(2);
                operationSelector.setOnItemSelectedListener(params[0]);
                operationSelector.setAdapter(new ArrayAdapter<String>(getContext(),
                        R.layout.simple_spinner_item,
                        new String[]{" = ", " < ", " > "}));

                booleanValueSelector.setId(3);
                booleanValueSelector.setAdapter(new ArrayAdapter<String>(getContext(),
                        R.layout.simple_spinner_item, new String[]{"true", "false"}));

                valueEnterer.setHint("Value");

                if (query != null) {
                    int selection = 0;

                    for (int i = 0; i < metrics.length; i++) {
                        if (query.getMetricID() == metrics[i].getID()) {
                            selection = i;
                            break;
                        }
                    }

                    metricSelector.setSelection(selection);

                    if (query.getType() != Query.TYPE_MATCH_DATA &&
                            metrics[selection].getType() == DBContract.BOOLEAN) {
                        if (query.getMetricValue().equals("false"))
                            booleanValueSelector.setSelection(1);
                    } else {
                        operationSelector.setSelection(query.getComparison() - 1);
                        valueEnterer.setText(query.getMetricValue());
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                addView(removeButton);
                addView(frontText);
                addView(metricSelector);
                refresh();
            }
        }
    }


    /**
     * **
     * Class: QueryIncompleteException
     */
    private class QueryIncompleteException extends Exception {
    }
}
