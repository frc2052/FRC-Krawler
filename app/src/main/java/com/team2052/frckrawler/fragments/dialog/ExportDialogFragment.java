package com.team2052.frckrawler.fragments.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.support.v4.content.FileProvider;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.team2052.frckrawler.activities.DatabaseActivity;
import com.team2052.frckrawler.activities.HasComponent;
import com.team2052.frckrawler.database.metric.CompileUtil;
import com.team2052.frckrawler.database.metric.Compiler;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.di.FragmentComponent;
import com.team2052.frckrawler.fragments.dialog.events.ProgressDialogUpdateEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
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

    private static final int CREATE_FILE_CODE = 12;

    @Inject
    Compiler compiler;

    private Event event;
    private Subscription subscription;

    private int exportType;

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

        event = mRxDbManager.getEventsTable().load(getArguments().getLong(DatabaseActivity.PARENT_ID));

        exportType = getArguments().getInt(EXPORT_TYPE);

        EventBus.getDefault().register(this);

        if (getActivity() instanceof HasComponent) {
            FragmentComponent component = ((HasComponent) getActivity()).getComponent();
            component.inject(this);
            checkPermissionAndDoExport();
        } else {
            dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_FILE_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                doExport(uri);
                return;
            }
        }

        Toast.makeText(getActivity(), "Failed to create export file, please try again", Toast.LENGTH_LONG).show();
        dismiss();
    }

    private void checkPermissionAndDoExport() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            createFile();
            return;
        }
        RxPermissions.getInstance(getActivity())
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        createFile();
                    } else {
                        Toast.makeText(getActivity(), "Cannot export file, please grant permission", Toast.LENGTH_LONG).show();
                        dismiss();
                    }
                }, FirebaseCrash::report);
    }

    private void createFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            createFileWithScopedStorage();
        } else {
            getExportFile(exportType)
              .map( file -> {
                  Uri fileUri = FileProvider.getUriForFile(
                    getActivity(),
                    getActivity().getApplicationContext()
                      .getPackageName() + ".provider", file);
                  return fileUri;
              })
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(
                uri -> {
                    doExport(uri);
                },
                error -> {
                    System.out.println("Failed to create file: " + error);
                    Toast.makeText(getActivity(), "Failed to export file, please try again", Toast.LENGTH_LONG).show();
                    dismiss();
                }
              );
        }
    }

    private void createFileWithScopedStorage() {
        String fileName;
        switch (exportType) {
            case EXPORT_TYPE_RAW: {
                fileName = CompileUtil.rawExportFileName(event);
                break;
            }
            default: {
                fileName = CompileUtil.summaryFileName(event);
                break;
            }
        }

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("file/csv");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);

        startActivityForResult(intent, CREATE_FILE_CODE);
    }

    private void doExport(Uri exportUri) {
        Observable<List<List<String>>> exportObservable = getExportObservable(exportType);

        try {
            // TODO this is in need of a complete rewrite
            OutputStream stream = getContext().getContentResolver().openOutputStream(exportUri, "w");
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            keepScreenOn(true);
            subscription = exportObservable
              .map( export -> { return CompileUtil.writeToFile.call(writer, export); })
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(
                success -> {
                    shareFile(exportUri);
                },
                onError -> {
                  onError.printStackTrace();
                  Toast.makeText(getActivity(), "Failed to write to export file, please try again", Toast.LENGTH_LONG).show();
                  dismiss();
              }, this::dismiss);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Failed to open export file, please try again", Toast.LENGTH_LONG).show();
            dismiss();
        }
    }

    private void shareFile(Uri uri) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);

        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("file/csv");
        startActivity(Intent.createChooser(shareIntent, "Share CSV with..."));
        AndroidSchedulers.mainThread().createWorker().schedule(() -> keepScreenOn(false));
        dismiss();
    }

    private void keepScreenOn(boolean on) {
        if (getActivity() == null || getActivity().getWindow() == null)
            return;

        if (on) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private Observable<List<List<String>>> getExportObservable(int type) {
        switch (type) {
            case EXPORT_TYPE_RAW:
                return compiler.getRawExport(event);
            default:
                return compiler.getSummaryExport(event);
        }
    }

    private Observable<File> getExportFile(int type) {
        switch (type) {
            case EXPORT_TYPE_RAW:
                return CompileUtil.getRawExportFile(event);
            default:
                return CompileUtil.getSummaryFile(event);
        }
    }

    @Subscribe
    public void onEvent(ProgressDialogUpdateEvent event) {
        AndroidSchedulers.mainThread().createWorker().schedule(() -> ((ProgressDialog) getDialog()).setMessage(event.message));
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (subscription != null) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }


    @Override
    public CharSequence getMessage() {
        return "Generating CSV";
    }
}
