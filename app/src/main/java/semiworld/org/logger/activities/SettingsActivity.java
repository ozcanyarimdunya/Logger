/*
 *                    GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 ******************************************************************************/

package semiworld.org.logger.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.activeandroid.query.Select;

import butterknife.BindView;
import butterknife.ButterKnife;
import semiworld.org.logger.R;
import semiworld.org.logger.models.Setting;

public class SettingsActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.switchParola) Switch switchParola;
    @BindView(R.id.txtDuration) EditText txtDuration;
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
            switchParola.setChecked(true);
            txtPassword.setVisibility(View.VISIBLE);
        }
        txtPassword.setText(String.valueOf(setting.password == null ? "" : setting.password));
        txtDuration.setText(String.valueOf(setting.duration));

        switchParola.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

        if (id == R.id.action_save_setting) {
            Setting setting = new Setting();
            if (switchParola.isChecked() && TextUtils.isEmpty(txtPassword.getText().toString())) return false;
            if (TextUtils.isEmpty(txtDuration.getText().toString())) return false;

            setting.passActivated = switchParola.isChecked();
            setting.password = String.valueOf(txtPassword.getText().toString());
            int duration = Integer.parseInt(String.valueOf(txtDuration.getText().toString()));

            if (duration > 30)
                duration = 30;

            setting.duration = duration;
            setting.save();
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
