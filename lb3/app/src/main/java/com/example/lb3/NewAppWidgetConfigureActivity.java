package com.example.lb3;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class NewAppWidgetConfigureActivity extends AppCompatActivity {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private EditText urlEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.new_app_widget_configure);

        urlEditText = findViewById(R.id.url_edit_text);
        Button addButton = findViewById(R.id.add_button);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        addButton.setOnClickListener(v -> {
            String url = urlEditText.getText().toString().trim();
            if (url.isEmpty()) {
                url = "https://google.com";
            }

            SharedPreferences prefs = getSharedPreferences("WidgetPrefs", MODE_PRIVATE);
            prefs.edit().putString("url_" + appWidgetId, url).apply();

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            NewAppWidget.updateAppWidget(this, appWidgetManager, appWidgetId);

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        });
    }
}
