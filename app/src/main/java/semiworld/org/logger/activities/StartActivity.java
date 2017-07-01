/*
 *                    GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 ******************************************************************************/

package semiworld.org.logger.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import com.activeandroid.query.Select;
import com.marcoscg.shortcuthelper.ShortcutHelper;

import semiworld.org.logger.R;
import semiworld.org.logger.models.Setting;

public class StartActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Intent login = new Intent(Intent.ACTION_VIEW, Uri.EMPTY, StartActivity.this, LoginActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Intent second = new Intent(Intent.ACTION_VIEW, Uri.EMPTY, StartActivity.this, SecondActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ShortcutHelper.with(this)
                .createShortcut(
                        "New Log",
                        "Add New Log",
                        android.R.drawable.ic_input_add,
                        second)
                .createShortcut(
                        "Logger",
                        "Launch App",
                        R.drawable.apple,
                        login)
                .go();


        Setting setting = new Select().from(Setting.class).orderBy("id DESC").executeSingle();
        int duration = setting.duration;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
                finish();
            }
        }, duration * 1000);
    }
}
