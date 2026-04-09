package com.example.lb1;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAnimate = findViewById(R.id.btn_animate);
        TextView tvName = findViewById(R.id.tv_name);

        Animation buttonAnim = AnimationUtils.loadAnimation(this, R.anim.button_anim);
        Animation textRotate = AnimationUtils.loadAnimation(this, R.anim.text_rotate);

        // По нажатию на кнопку — кнопка увеличивается, ФИО вращается
        btnAnimate.setOnClickListener(v -> {
            v.startAnimation(buttonAnim);
            tvName.startAnimation(textRotate);
        });
    }
}
