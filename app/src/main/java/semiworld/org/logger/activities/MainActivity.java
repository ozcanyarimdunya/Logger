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
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import semiworld.org.logger.R;
import semiworld.org.logger.adapters.NoteAdapter;
import semiworld.org.logger.models.Note;

public class MainActivity extends AppCompatActivity {

    /*
    Here we have initialize of view or component of ui
    ButterKnife save our times to initialize every ui component by writing findViewById and then casting
    This is standart definition for ButtonKnife
    Then after you HAVE TO initialize ButterKnife in onCreate method or else it will return null
     */
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.my_recycler_view)
    RecyclerView recyclerView;

    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Note> noteList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Init();
    }

    private void Init() {
        adapter = new NoteAdapter(noteList);
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

/*
 * run this codes once!

        Intent login = new Intent(Intent.ACTION_VIEW, Uri.EMPTY, MainActivity.this, LoginActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Intent second = new Intent(Intent.ACTION_VIEW, Uri.EMPTY, MainActivity.this, SecondActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ShortcutHelper.with(this)
                .createShortcut(
                        "New Note",
                        "Add New Note",
                        android.R.drawable.ic_input_add,
                        second)
                .createShortcut(
                        "Your Story",
                        "Launch App",
                        R.drawable.heart,
                        login)
                .go();
*/
    }

    /*
    * Here we get our data from database and put it in a temp list then add them all to master list
      */
    @Override
    protected void onResume() {
        super.onResume();
        List<Note> notes = new Select().from(Note.class).orderBy("id DESC").execute();
        noteList.clear();
        noteList.addAll(notes);
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.fab)
    void fab_clicked() {
        startActivity(new Intent(getApplicationContext(), SecondActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_info) {
            WhoAmI();
        }
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }


    private void WhoAmI() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(MainActivity.this);
        }
        builder.setTitle("INFO")
                .setMessage("\n\nDesigned by @ozcaan11  |  2017")
                .setIcon(android.R.drawable.ic_menu_info_details).show();
    }
}
