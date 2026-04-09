package com.example.lb4;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private TextView hintTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.edit_text);
        hintTextView = findViewById(R.id.hint_text_view);

        registerForContextMenu(editText);
    }

    // Options menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_show_hint) {
            item.setChecked(!item.isChecked());
            hintTextView.setVisibility(item.isChecked() ? View.VISIBLE : View.GONE);
            return true;
        } else if (id == R.id.menu_clear) {
            editText.setText("");
            return true;
        } else if (id == R.id.menu_about) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.menu_about)
                    .setMessage(R.string.about_message)
                    .setPositiveButton("OK", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Context menu

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.ctx_clear) {
            editText.setText("");
            return true;
        } else if (id == R.id.ctx_uppercase) {
            editText.setText(editText.getText().toString().toUpperCase());
            return true;
        } else if (id == R.id.ctx_lowercase) {
            editText.setText(editText.getText().toString().toLowerCase());
            return true;
        }
        return super.onContextItemSelected(item);
    }
}
