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
import android.os.Bundle;
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
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import semiworld.org.logger.R;
import semiworld.org.logger.models.DownloadModel;
import semiworld.org.logger.models.Setting;
import semiworld.org.logger.models.Version;
import semiworld.org.logger.utils.UpdateManager;

public class SettingsActivity extends BaseActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.switchParola) Switch switchPassword;
    @BindView(R.id.txtDuration) ScrollableNumberPicker txtDuration;
    @BindView(R.id.txtPassword) TextView txtPassword;

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
                if (!saveSettings()) return false;
                onBackPressed();
                break;
            case R.id.action_check_update:
                checkForUpdates();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean saveSettings() {
        Setting setting = new Setting();
        if (switchPassword.isChecked() && TextUtils.isEmpty(txtPassword.getText().toString())) return false;

        setting.passActivated = switchPassword.isChecked();
        setting.password = String.valueOf(txtPassword.getText().toString());
        setting.duration = txtDuration.getValue();
        setting.save();
        return true;
    }

    private void checkForUpdates() {
        final DownloadModel model = UpdateManager.checkForUpdate(SettingsActivity.this);
        final Version version = new Select().from(Version.class).orderBy("id DESC").executeSingle();

        PermissionListener listener = new PermissionListener() {
            @Override public void onPermissionGranted() {
                if (String.valueOf(model.getLatestVersion()).equals(version.latest)) {
                    Toasty.info(SettingsActivity.this, "You are using latest version: " + model.getLatestVersion(), Toast.LENGTH_SHORT).show();
                    return;
                }
                new MaterialDialog.Builder(SettingsActivity.this)
                        .title("Logger " + model.getLatestVersion() + " available!")
                        .content("Update Logger now!")
                        .iconRes(android.R.drawable.ic_menu_upload)
                        .positiveText("Update")
                        .negativeText("Cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                                version.latest = model.getLatestVersion();
                                version.save();

                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                                dialog.getActionButton(DialogAction.NEGATIVE).setEnabled(false);
                                dialog.getActionButton(DialogAction.POSITIVE).setText("UPDATING ...");
                                UpdateManager.downloadUpdate(SettingsActivity.this, model.getUrl());
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
                Toasty.error(SettingsActivity.this, "Permission required!\nSettings > Apps > Logger > Permission", Toast.LENGTH_SHORT).show();
            }
        };

        new TedPermission(SettingsActivity.this)
                .setPermissionListener(listener)
                .setPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})
                .check();
    }
}
