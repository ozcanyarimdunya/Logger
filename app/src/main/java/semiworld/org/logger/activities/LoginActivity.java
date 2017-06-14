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
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.activeandroid.query.Select;

import butterknife.BindView;
import butterknife.ButterKnife;
import semiworld.org.logger.R;
import semiworld.org.logger.models.Setting;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.txtLoginPass) EditText txtPassword;
    @BindView(R.id.btnLogin) Button btnLogin;
    @BindView(R.id.btnHidden) Button btnHidden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        btnHidden.setBackgroundColor(Color.TRANSPARENT);
        final Setting setting = new Select().from(Setting.class).orderBy("id DESC").executeSingle();

        if (!setting.passActivated) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                String pass = String.valueOf(txtPassword.getText().toString());
                if (!TextUtils.isEmpty(pass) && String.valueOf(setting.password).equals(pass)) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    txtPassword.setError("Eksik veya hatalı giriş!");
                }
            }
        });
        btnHidden.setOnLongClickListener(new View.OnLongClickListener() {
            @Override public boolean onLongClick(View v) {
                setting.password = "123";
                setting.save();
                Toast.makeText(LoginActivity.this, "Şifre 123 olarak değiştirildi!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
}
