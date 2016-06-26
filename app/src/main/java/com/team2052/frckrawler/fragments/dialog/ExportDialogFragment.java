package com.team2052.frckrawler.fragments.dialog;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.WindowManager;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;
import com.team2052.frckrawler.activities.BaseActivity;
import com.team2052.frckrawler.activities.HasComponent;
import com.team2052.frckrawler.database.CompilerManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.di.FragmentComponent;
import com.team2052.frckrawler.fragments.dialog.events.ProgressDialogUpdateEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Adam
 * @since 3/10/2015.
 */
public class ExportDialogFragment extends BaseProgressDialog {
    private static final String TAG = "ExportDialogFragment";

    public static ExportDialogFragment newInstance(Event event) {
        ExportDialogFragment exportDialogFragment = new ExportDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(BaseActivity.PARENT_ID, event.getId());
        exportDialogFragment.setArguments(bundle);
        return exportDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (getActivity() instanceof HasComponent) {
            RxPermissions.getInstance(getActivity()).request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(granted -> {
                if (granted) {
                    FragmentComponent component = ((HasComponent) getActivity()).getComponent();
                    CompilerManager compilerManager = component.compilerManager();
                    Event event = mDbManager.getEventsTable().load(getArguments().getLong(BaseActivity.PARENT_ID));
                    //Event is null, should not continue
                    if (event == null) {
                        dismiss();
                    }

                    File file = getFile(event);

                    //Something went wrong trying to create a file, should not continue
                    if (file == null) {
                        dismiss();
                    }

                    compilerManager.writeToFile(compilerManager.fullExportObservable(event), file)
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(onNext -> {
                                MediaScannerConnection.scanFile(getActivity(), new String[]{onNext.getAbsolutePath()}, null, null);
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(onNext));
                                shareIntent.setType("file/csv");
                                startActivity(Intent.createChooser(shareIntent, "Share CSV with..."));
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                dismiss();
                            }, Throwable::printStackTrace);
                } else {
                    Toast.makeText(getActivity(), "Cannot export file, please grant permission", Toast.LENGTH_LONG).show();
                    dismiss();
                }
            });
        } else {
            dismiss();
        }
    }

    @Subscribe
    public void onEvent(ProgressDialogUpdateEvent event){
        ((ProgressDialog) getDialog()).setMessage(event.message);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private File getFile(Event event) {
        File fileSystem = Environment.getExternalStorageDirectory();
        File file = null;
        if (fileSystem.canWrite()) {
            File directory = new File(fileSystem, "/FRCKrawler/Summaries/" + event.getGame().getName() + "/");
            if(!directory.exists()){
                directory.mkdirs();
            }
            try {
                file = File.createTempFile(
                        mDbManager.getGamesTable().load(event.getGame_id()).getName() + "_" + event.getName() + "_" + "Summary",  /* prefix */
                        ".csv",         /* suffix */
                        directory      /* directory */
                );
            } catch (IOException e) {
                dismiss();
            }
        }
        return file;
    }

    @Override
    public CharSequence getMessage() {
        return "Generating CSV";
    }
}
