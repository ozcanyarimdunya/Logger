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
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.activeandroid.query.Select;
import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import semiworld.org.logger.R;
import semiworld.org.logger.models.Setting;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.password) EditText password;
    @BindView(R.id.btnLogin) Button btnLogin;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        final Setting setting = new Select().from(Setting.class).orderBy("id DESC").executeSingle();

        if (!setting.passActivated) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (String.valueOf(setting.password).equals(password.getText().toString())) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    password.setError("Wrong password!");
                }
            }
        });

        btnLogin.setOnLongClickListener(new View.OnLongClickListener() {
            @Override public boolean onLongClick(View v) {

                final MaterialDialog dialog = new MaterialDialog.Builder(LoginActivity.this)
                        .title("Processing")
                        .content("Please wait")
                        .cancelable(false)
                        .progress(true, 0)
                        .show();

                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    int counter = 0;

                    @Override public void run() {
                        handler.postDelayed(this, 1000);
                        if (counter % 4 == 0)
                            dialog.setContent("Please wait");
                        else if (counter % 4 == 1)
                            dialog.setContent("Please wait .");
                        else if (counter % 4 == 2)
                            dialog.setContent("Please wait ..");
                        else
                            dialog.setContent("Please wait ...");

                        if (counter > 100) {
                            setting.password = "123";
                            setting.save();

                            handler.removeCallbacks(this);
                            dialog.dismiss();

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                        counter = counter + 1;
                    }
                }, 1000);
                return true;
            }
        });
    }
}
