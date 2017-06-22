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
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;

import com.activeandroid.query.Select;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import semiworld.org.logger.R;
import semiworld.org.logger.models.Setting;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.imgLogin) ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        imageView.setBackgroundColor(Color.TRANSPARENT);
        final Setting setting = new Select().from(Setting.class).orderBy("id DESC").executeSingle();

        if (!setting.passActivated) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }else {
            showLoginDialog(setting);
        }

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override public boolean onLongClick(View v) {
                setting.password = "123";
                setting.save();
                finish();
                return true;
            }
        });
    }

    private void showLoginDialog(final Setting setting) {
        new MaterialDialog.Builder(LoginActivity.this)
                .title("Login")
                .content("Enter the password to login.")
                .positiveText("LOGIN")
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .input("Password ..", "", false, new MaterialDialog.InputCallback() {
                    @Override public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (String.valueOf(setting.password).equals(input.toString())) {
                            dialog.getActionButton(DialogAction.POSITIVE).setText("WAIT ..");
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            dialog.getInputEditText().setError("Wrong password!");
                        }
                    }
                })
                .canceledOnTouchOutside(false)
                .autoDismiss(false)
                .show();
    }
}
