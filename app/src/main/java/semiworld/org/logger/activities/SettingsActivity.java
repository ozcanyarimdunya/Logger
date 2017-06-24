/*
 *                    GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 ******************************************************************************/

package semiworld.org.logger.activities;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import semiworld.org.logger.R;
import semiworld.org.logger.models.Setting;
import semiworld.org.logger.models.Version;

public class SettingsActivity extends BaseActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.switchParola) Switch switchPassword;
    @BindView(R.id.txtDuration) ScrollableNumberPicker txtDuration;
    @BindView(R.id.txtPassword) TextView txtPassword;
    DownloadManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Init();
    }

    private void Init() {
        Setting setting = new Select().from(Setting.class).orderBy("id DESC").executeSingle();
        if (setting.passActivated) {
            switchPassword.setChecked(true);
            txtPassword.setVisibility(View.VISIBLE);
        }
        txtPassword.setText(String.valueOf(setting.password == null ? "" : setting.password));
        txtDuration.setValue(setting.duration);

        switchPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    txtPassword.setVisibility(View.VISIBLE);
                } else {
                    txtPassword.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_save_setting:
                Setting setting = new Setting();
                if (switchPassword.isChecked() && TextUtils.isEmpty(txtPassword.getText().toString())) return false;

                setting.passActivated = switchPassword.isChecked();
                setting.password = String.valueOf(txtPassword.getText().toString());
                setting.duration = txtDuration.getValue();
                setting.save();
                onBackPressed();
                break;
            case R.id.action_check_update:
                try {
                    checkForUpdates();
                } catch (Exception e) {
                    Toasty.error(SettingsActivity.this, "Failed! Maybe you should check you internet connection", Toast.LENGTH_LONG).show();
                }
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkForUpdates() {
        final Version version = new Select().from(Version.class).orderBy("id DESC").executeSingle();
        AppUpdaterUtils utils = new AppUpdaterUtils(SettingsActivity.this)
                .setUpdateFrom(UpdateFrom.GITHUB)
                .setGitHubUserAndRepo("ozcaan11", "Logger")
                .withListener(new AppUpdaterUtils.UpdateListener() {
                    @Override public void onSuccess(Update update, Boolean isUpdateAvailable) {
                        String url = String.valueOf(update.getUrlToDownload() + "/download/" + update.getLatestVersion() + "/app-debug.apk");
                        if (!String.valueOf(version.latest).equals(update.getLatestVersion())) {
                            if (isUpdateAvailable) {
                                checkPermissionAndDownloadApp(url, version.latest, update.getLatestVersion(), update.getReleaseNotes());
                                version.latest = update.getLatestVersion();
                                version.save();
                            }
                        } else {
                            Toasty.success(SettingsActivity.this, "You are using the latest version: " + update.getLatestVersion(), Toast.LENGTH_LONG)
                                    .show();
                        }
                    }

                    @Override public void onFailed(AppUpdaterError appUpdaterError) {
                    }
                });
        utils.start();
    }

    private void checkPermissionAndDownloadApp(final String url, final String oldVersion, final String version, final String releaseNotes) {
        PermissionListener listener = new PermissionListener() {
            @Override public void onPermissionGranted() {
                new MaterialDialog.Builder(SettingsActivity.this)
                        .title("Logger " + version + " available!")
                        .content("Update Logger " + oldVersion + " to " + version + " now.\nNew features:\n" + releaseNotes)
                        .iconRes(android.R.drawable.ic_menu_upload)
                        .positiveText("Download")
                        .negativeText("Cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                                dialog.getActionButton(DialogAction.POSITIVE).setText("DOWNLOADING ..");

                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                                request.setDescription("New version of Logger");
                                request.setTitle("Logger");
                                // in order for this if to run, you must use the android 3.2 to compile your app
                                request.allowScanningByMediaScanner();
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "app-debug.apk");

                                // get download service and enqueue file
                                manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                final Long enq = manager.enqueue(request);

                                BroadcastReceiver receiver = new BroadcastReceiver() {
                                    @Override public void onReceive(Context context, Intent intent) {
                                        String action = intent.getAction();
                                        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                                            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                                            DownloadManager.Query query = new DownloadManager.Query();
                                            query.setFilterById(enq);
                                            manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                            Cursor c = manager.query(query);
                                            if (c.moveToFirst()) {
                                                int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                                                if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                                                    String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                                                    dialog.dismiss();
                                                    intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.setDataAndType(Uri.parse(uriString),
                                                            manager.getMimeTypeForDownloadedFile(downloadId));
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    try {
                                                        startActivity(intent);
                                                    } catch (Exception e) {
                                                        onRestart();
                                                        Toasty.success(SettingsActivity.this, "Application successfully updated to " + version + " " +
                                                                "version.").show();
                                                    }
                                                } else {
                                                    Toast.makeText(context, "Download unsuccessful!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    }
                                };
                                registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .autoDismiss(false)
                        .canceledOnTouchOutside(false)
                        .show();
            }

            @Override public void onPermissionDenied(ArrayList<String> deniedPermissions) {

            }
        };


        new TedPermission(SettingsActivity.this).setPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE)
                .setPermissionListener(listener)
                .check();
    }
}
