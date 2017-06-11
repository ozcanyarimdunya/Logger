/*
 *                    GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 ******************************************************************************/

package semiworld.org.logger.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import semiworld.org.logger.R;
import semiworld.org.logger.classes.Note;

public class SecondActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.editTextThoughts)
    EditText editTextThought;
    private Note note;
    private Long noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // This is for showing or hiding menu item programmatically
        invalidateOptionsMenu();

        // We are getting extras to decide if it is a new note request or update
        Intent intent = getIntent();
        noteId = intent.getLongExtra("_id", 0L);
        if (noteId > 0) {
            note = new Select().from(Note.class).where("id=?", noteId).executeSingle();
            editTextThought.setText(note.Text);

            // This for hiding keyboard etc..
            editTextThought.setFocusable(false);
            editTextThought.setFocusableInTouchMode(false);
            editTextThought.setClickable(false);
        }


    }

    // If it is a new note then you may hide some menu items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_second, menu);
        MenuItem mode = menu.findItem(R.id.action_mode);
        MenuItem delete = menu.findItem(R.id.action_delete);
        if (noteId == 0) {
            mode.setVisible(false);
            delete.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_save:
                createOrUpdateNote();
                break;
            case R.id.action_mode:
                selectMode(item);
                break;
            case R.id.action_delete:
                deleteNote();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // If intent has extras that means this is recovery of data -- update
    // else create new one
    // then save
    private void createOrUpdateNote() {
        String thoughts = String.valueOf(editTextThought.getText());
        if (!TextUtils.isEmpty(thoughts)) {
            if (noteId == 0L) {
                note = new Note(thoughts, Calendar.getInstance(Locale.getDefault()).getTime());
            } else {
                note.Text = thoughts;
            }
            note.save();
            super.onBackPressed();
        }
    }

    // Toggle between menu item to show keyboard
    private void selectMode(MenuItem item) {
        if (item.getIcon().getConstantState()
                .equals(getDrawable(android.R.drawable.ic_menu_edit).getConstantState())) {

            item.setIcon(android.R.drawable.ic_menu_view);

            editTextThought.setFocusable(true);
            editTextThought.setFocusableInTouchMode(true);
            editTextThought.setClickable(true);
            editTextThought.requestFocus();
            editTextThought.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            editTextThought.setSelection(editTextThought.getText().length());

            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, 0);
            }
        } else {
            item.setIcon(android.R.drawable.ic_menu_edit);
            editTextThought.setFocusable(false);
            editTextThought.setFocusableInTouchMode(false);
            editTextThought.setClickable(false);

            View view = editTextThought;
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextThought.getWindowToken(), 0);
            }
        }
    }

    // Show an alert dialog to confirm delete
    private void deleteNote() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(SecondActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(SecondActivity.this);
        }
        builder.setTitle("Delete?")
                .setMessage("Do you really want to delete?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new Delete().from(Note.class).where("id=?", noteId).execute();
                        onBackPressed();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_menu_delete)
                .show();
    }
}
