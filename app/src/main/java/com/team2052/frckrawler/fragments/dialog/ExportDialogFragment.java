package com.team2052.frckrawler.fragments.dialog;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;
import com.team2052.frckrawler.activities.DatabaseActivity;
import com.team2052.frckrawler.activities.HasComponent;
import com.team2052.frckrawler.database.metric.CompilerManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.di.FragmentComponent;
import com.team2052.frckrawler.fragments.dialog.events.ProgressDialogUpdateEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Adam
 * @since 3/10/2015.
 */
public class ExportDialogFragment extends BaseProgressDialog {
    public static final int EXPORT_TYPE_RAW = 1;
    public static final int EXPORT_TYPE_NORMAL = 0;
    private static final String TAG = "ExportDialogFragment";
    private static final String EXPORT_TYPE = "EXPORT_TYPE_EXTRA";
    private CompilerManager compilerManager;
    private Event event;

    public static ExportDialogFragment newInstance(Event event, int export_type) {
        ExportDialogFragment exportDialogFragment = new ExportDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(DatabaseActivity.PARENT_ID, event.getId());
        bundle.putInt(EXPORT_TYPE, export_type);
        exportDialogFragment.setArguments(bundle);
        return exportDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        event = mDbManager.getEventsTable().load(getArguments().getLong(DatabaseActivity.PARENT_ID));

        EventBus.getDefault().register(this);

        if (getActivity() instanceof HasComponent) {
            FragmentComponent component = ((HasComponent) getActivity()).getComponent();
            compilerManager = component.compilerManager();

            checkPermissionAndDoExport();
        } else {
            dismiss();
        }
    }

    private void checkPermissionAndDoExport() {
        RxPermissions.getInstance(getActivity())
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        doExport();
                    } else {
                        Toast.makeText(getActivity(), "Cannot export file, please grant permission", Toast.LENGTH_LONG).show();
                        dismiss();
                    }
                });
    }

    private void doExport() {
        final int exportType = getArguments().getInt(EXPORT_TYPE);
        Observable<List<List<String>>> exportObservable = getExportObservable(exportType);
        Observable<File> summaryFile = getExportFile(exportType);

        keepScreenOn(true);
        compilerManager.writeToFile(exportObservable, summaryFile)
                .observeOn(Schedulers.computation())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(this::shareFile, onError -> {
                    onError.printStackTrace();
                    dismiss();
                }, this::dismiss);
    }

    private void shareFile(File file) {
        MediaScannerConnection.scanFile(getActivity(), new String[]{file.getAbsolutePath()}, null, null);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        shareIntent.setType("file/csv");
        startActivity(Intent.createChooser(shareIntent, "Share CSV with..."));
        AndroidSchedulers.mainThread().createWorker().schedule(() -> keepScreenOn(false));
        dismiss();
    }

    private void keepScreenOn(boolean on) {
        if (on) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private Observable<List<List<String>>> getExportObservable(int type) {
        switch (type) {
            case 1:
                return compilerManager.getRawExport(event);
            default:
                return compilerManager.fullExportObservable(event);
        }
    }

    private Observable<File> getExportFile(int type) {
        switch (type) {
            case 1:
                return compilerManager.getRawExportFile(event);
            default:
                return compilerManager.getSummaryFile(event);
        }
    }

    @Subscribe
    public void onEvent(ProgressDialogUpdateEvent event) {
        AndroidSchedulers.mainThread().createWorker().schedule(() -> ((ProgressDialog) getDialog()).setMessage(event.message));
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    @Override
    public CharSequence getMessage() {
        return "Generating CSV";
    }
}
