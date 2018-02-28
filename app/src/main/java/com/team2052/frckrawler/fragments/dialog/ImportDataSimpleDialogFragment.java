package com.team2052.frckrawler.fragments.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.crash.FirebaseCrash;
import com.team2052.frckrawler.Constants;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.DatabaseActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.adapters.items.ListElement;
import com.team2052.frckrawler.adapters.items.ListItem;
import com.team2052.frckrawler.adapters.items.elements.SimpleListElement;
import com.team2052.frckrawler.data.RxDBManager;
import com.team2052.frckrawler.data.tba.v3.ConnectionChecker;
import com.team2052.frckrawler.data.tba.v3.TBA;
import com.team2052.frckrawler.interfaces.RefreshListener;
import com.team2052.frckrawler.models.Season;

import java.util.Calendar;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Used to import a event to a game in the most simple way for the user.
 *
 * @author Adam
 */
public class ImportDataSimpleDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

    private String[] yearDropDownItems;
    private Spinner yearSpinner;
    private Spinner eventSpinner;
    private Season season;
    private boolean isConnected;
    private Subscription eventSubscription;

    /**
     * Used to create the dialog. To import the event to the game
     *
     * @param game_id the game that the event will eventually be imported to.
     * @return The fragment with the specific arguments to run the dialog
     */
    public static ImportDataSimpleDialogFragment newInstance(long game_id) {
        ImportDataSimpleDialogFragment fragment = new ImportDataSimpleDialogFragment();
        Bundle b = new Bundle();
        b.putLong(DatabaseActivity.Companion.getPARENT_ID(), game_id);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.season = RxDBManager.Companion.getInstance(getActivity()).getSeasonsTable().load(getArguments().getLong(DatabaseActivity.Companion.getPARENT_ID()));
        isConnected = ConnectionChecker.isConnectedToInternet(getActivity());

        // Get year
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        //TBA usually updates events around october
        if (calendar.get(Calendar.MONTH) > Calendar.SEPTEMBER) {
            year += 1;
        }

        if (year < Constants.FIRST_COMP_YEAR) {
            year = Constants.MAX_COMP_YEAR;
        }

        yearDropDownItems = new String[year - Constants.FIRST_COMP_YEAR + 1];
        for (int i = 0; i < yearDropDownItems.length; i++) {
            yearDropDownItems[i] = Integer.toString(year - i);
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_import_simple, null);
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setPositiveButton("Import", (dialog, which) -> {
            if (eventSpinner.getSelectedItem() == null) {
                return;
            }

            ImportEventDataDialog.newInstance(((ListElement) eventSpinner.getSelectedItem()).getKey(), season).show(ImportDataSimpleDialogFragment.this.getFragmentManager(), "importDialog");
        });
        b.setNegativeButton("Cancel", (dialog, which) -> {
            ImportDataSimpleDialogFragment.this.dismiss();
        });
        b.setNeutralButton("Add Manual", ((dialog, which) -> {
            AddEventDialogFragment.newInstance(season).show(getParentFragment().getChildFragmentManager(), "addEvent");
        }));
        yearSpinner = (Spinner) view.findViewById(R.id.import_year_spinner);
        yearSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, yearDropDownItems));
        yearSpinner.setOnItemSelectedListener(this);
        eventSpinner = (Spinner) view.findViewById(R.id.import_event_spinner);
        if (!isConnected) {
            yearSpinner.setVisibility(View.GONE);
            eventSpinner.setVisibility(View.GONE);
            view.findViewById(R.id.no_connection).setVisibility(View.VISIBLE);
        }
        b.setView(view);
        b.setTitle("Import Event");
        return b.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (eventSubscription != null && !eventSubscription.isUnsubscribed()) {
            eventSubscription.unsubscribe();
        }

        if (getParentFragment() != null && getParentFragment() instanceof RefreshListener) {
            ((RefreshListener) getParentFragment()).refresh();
        }
        super.onDismiss(dialog);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (isConnected) {
            eventSpinner.setVisibility(View.GONE);

            getDialog().findViewById(R.id.progress).setVisibility(View.VISIBLE);
            eventSubscription = TBA.requestEventsYear(Integer.parseInt((String) yearSpinner.getSelectedItem()))
                    .flatMap(Observable::from)
                    .map(event -> (ListItem) new SimpleListElement(event.getName(), event.getFmsid()))
                    .toList()
                    .map(simpleListElements -> new ListViewAdapter(getActivity(), simpleListElements))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(listViewAdapter -> eventSpinner.setAdapter(listViewAdapter),
                            throwable -> {
                                throwable.printStackTrace();
                                FirebaseCrash.report(throwable);
                            }, () -> {
                                eventSpinner.setVisibility(View.VISIBLE);
                                getDialog().findViewById(R.id.progress).setVisibility(View.GONE);
                            });
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}


