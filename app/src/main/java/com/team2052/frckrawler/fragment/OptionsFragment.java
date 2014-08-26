package com.team2052.frckrawler.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Game;

public class OptionsFragment extends Fragment implements View.OnClickListener {
    public static final String PREFS_NAME = "userSettings";
    public static final String PREFS_IS_WRITTEN = "isWritten";
    public static final String PREFS_COMPILE_WEIGHT = "compileWeight";
    public static final String PREFS_GENERATE_ROBOTS = "generateRobots";
    public static final String PREFS_ROBOT_GAME = "robotGame";
    private SharedPreferences preferences;
    private String gameName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_options, null);
        preferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        ((EditText) v.findViewById(R.id.weightValue)).setText(Float.toString(preferences.getFloat(PREFS_COMPILE_WEIGHT, 1.0f)));
        boolean generate = preferences.getBoolean(PREFS_GENERATE_ROBOTS, false);
        gameName = preferences.getString(PREFS_ROBOT_GAME, "none");
        if (generate) {
            ToggleButton generateButton = (ToggleButton) v.findViewById(R.id.generateRobots);
            generateButton.setTextOn(gameName);
            generateButton.setChecked(true);
        }

        v.findViewById(R.id.saveOptions).setOnClickListener(this);
        v.findViewById(R.id.restoreDefaults).setOnClickListener(this);
        v.findViewById(R.id.cancelOptions).setOnClickListener(this);
        v.findViewById(R.id.weightHelp).setOnClickListener(this);
        v.findViewById(R.id.generateRobots).setOnClickListener(this);
        v.findViewById(R.id.generateHelp).setOnClickListener(this);
        return v;
    }

    public static void restoreDefaultOptions(Context context, boolean overwrite) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        if (!prefs.getBoolean(PREFS_IS_WRITTEN, false) || overwrite) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PREFS_IS_WRITTEN, true);
            editor.putFloat(PREFS_COMPILE_WEIGHT, 1.0f);
            editor.putBoolean(PREFS_GENERATE_ROBOTS, false);
            editor.putString(PREFS_ROBOT_GAME, "none");
            editor.commit();
        }
        //TODO set text
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveOptions:
                SharedPreferences.Editor editor = preferences.edit();
                try {
                    float weight = Float.parseFloat(((EditText) getView().findViewById(R.id.weightValue)).getText().toString());
                    editor.putFloat(PREFS_COMPILE_WEIGHT, weight);
                    boolean generateRobots = ((ToggleButton) getView().findViewById(R.id.generateRobots)).isChecked();
                    editor.putBoolean(PREFS_GENERATE_ROBOTS, generateRobots);
                    editor.putString(PREFS_ROBOT_GAME, gameName);
                    editor.putBoolean(PREFS_IS_WRITTEN, true);
                    editor.commit();
                } catch (NumberFormatException e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Options Not Saved");
                    builder.setMessage("You must enter a number for the weight when " + "compiling data.");
                    builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }

                break;

            case R.id.restoreDefaults:
                restoreDefaultOptions(getActivity(), true);
                break;

            case R.id.cancelOptions:
                break;

            case R.id.weightHelp:
                AlertDialog.Builder weightBuilder = new AlertDialog.Builder(getActivity());
                weightBuilder.setTitle("About Compiled Data Weight");
                weightBuilder.setMessage("The weight for compiled data is how many times " +
                        "a match's data is worth in the averaging process, compared to the " +
                        "previous match. Values " +
                        " greater than 1 make later matches more valuable, and values " +
                        "less than 1 make earlier matches more valuable. A value of 1 makes" +
                        "all matches equal. This " +
                        "applies to math, counter, slider, boolean, and numeric chooser " +
                        "metrics that track match data. For example, " +
                        "a robot receives a score of 10 in a given addbutton for match #1, and " +
                        "a score of 20 for the same addbutton in match #5. If the weight " +
                        "is 1.5, the average shown for compiled data will be 17.5. Match #5 " +
                        "counts more towards the average. Likewise, if the weight is set " +
                        "to 0.5, the average shown will be 12.5. Match #1 is more 'valuable' " +
                        "in the averaging process. Be careful, values larger than 2 or 3 will" +
                        " seriously skew data towards later matches. The default setting is 1.");
                weightBuilder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                weightBuilder.show();
                break;

            case R.id.generateRobots:
                final ToggleButton b = (ToggleButton) view;
                if (b.isChecked()) {
                    Game[] games = DBManager.getInstance(getActivity()).getAllGames();
                    final CharSequence[] choices = new CharSequence[games.length];

                    for (int i = 0; i < games.length; i++)
                        choices[i] = games[i].getName();

                    AlertDialog.Builder generateBuilder = new AlertDialog.Builder(getActivity());
                    generateBuilder.setTitle("Choose a game for new robots");
                    generateBuilder.setItems(choices, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            gameName = choices[which].toString();
                            b.setText(choices[which]);
                            dialog.dismiss();
                        }
                    });

                    AlertDialog gameDialog = generateBuilder.create();
                    gameDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface dialog) {
                            b.setChecked(false);
                        }
                    });
                    gameDialog.show();

                } else {
                    gameName = "none";
                }

                break;

            case R.id.generateHelp:
                AlertDialog.Builder generateBuilder = new AlertDialog.Builder(getActivity());
                generateBuilder.setTitle("About Generating Robots");
                generateBuilder.setMessage("Setting this option to 'On' generates a robot with " +
                        "the specified Game every time a new Team is added to the database. " +
                        "This is extremely useful for adding new Teams to the database for " +
                        "this first time, or for the next competition season.");
                generateBuilder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                generateBuilder.show();
                break;
        }
    }
}
