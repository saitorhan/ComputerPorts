package com.saitorhan.computerports;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.saitorhan.computerports.db.DbCrud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.list);
        editText = findViewById(R.id.editTextSeach);

        if (!checkData()) {
            return;
        }

        DbCrud dbCRUD = new DbCrud(this, false);
        Cursor urls = dbCRUD.database.query("ports", new String[]{"_id", "servicename", "portnumber", "transport", "description"}, null, null, null, null, "RANDOM()", "20");

        if (urls.getCount() == 0) {
            Toast.makeText(this, getString(R.string.not_found), Toast.LENGTH_LONG).show();
            return;
        }

        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.layout, urls, new String[]{"_id", "servicename", "portnumber", "transport", "description"}, new int[]{R.id.textViewId, R.id.textViewService, R.id.textViewPort, R.id.textViewTransport, R.id.textViewDescription});
        listView.setAdapter(simpleCursorAdapter);
    }

    private boolean checkData() {
        DbCrud dbCrud = new DbCrud(this, true);
        Cursor cursorPorts = dbCrud.database.query("ports", new String[]{"_id"}, null, null, null, null, null, "1");
        if (cursorPorts.getCount() > 0) {
            return true;
        }

        BufferedReader reader = null;
        String line;
        String[] split;
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open("ports.txt")));
            String sql = "INSERT INTO ports(_id, servicename, portnumber, transport,description) VALUES(?, ?, ?, ?, ?)";
            dbCrud.database.beginTransaction();
            SQLiteStatement sqLiteStatement = dbCrud.database.compileStatement(sql);
            while ((line = reader.readLine()) != null) {
                split = line.split(";");

                if (split.length != 4 || split[1].isEmpty() || split[2].isEmpty() || split[3].isEmpty()) {
                    continue;
                }
                sqLiteStatement.bindString(1, UUID.randomUUID().toString());
                sqLiteStatement.bindString(2, split[0]);
                sqLiteStatement.bindString(3, split[1]);
                sqLiteStatement.bindString(4, split[2]);
                sqLiteStatement.bindString(5, split[3]);
                sqLiteStatement.execute();
                sqLiteStatement.clearBindings();
            }
            dbCrud.database.setTransactionSuccessful();
            dbCrud.database.endTransaction();
            return true;
        } catch (IOException exc) {
            return false;
        } catch (IllegalArgumentException exc) {
            return false;
        } catch (ArrayIndexOutOfBoundsException exc) {
            return false;
        } catch (Exception exc) {
            return false;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {

                }
            }
        }
    }


    public void searchClick(View view) {
        DbCrud dbCRUD = new DbCrud(this, false);
        Cursor urls = dbCRUD.database.query("ports", new String[]{"_id", "servicename", "portnumber", "transport", "description"}, "portnumber LIKE ? OR description LIKE ?", new String[]{"%" + editText.getText().toString() + "%", "%" + editText.getText().toString() + "%"}, null, null, "portnumber");

        if (urls.getCount() == 0) {
            Toast.makeText(this, getString(R.string.not_found), Toast.LENGTH_LONG).show();
        }
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.layout, urls, new String[]{"_id", "servicename", "portnumber", "transport", "description"}, new int[]{R.id.textViewId, R.id.textViewService, R.id.textViewPort, R.id.textViewTransport, R.id.textViewDescription});
        listView.setAdapter(simpleCursorAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.rateapp) {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        } else if (item.getItemId() == R.id.shareapp) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "https://play.google.com/store/apps/details?id=" + getPackageName());
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }

        return super.onOptionsItemSelected(item);
    }
}
