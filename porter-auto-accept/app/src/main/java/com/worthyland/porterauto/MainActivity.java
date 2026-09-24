package com.worthyland.porterauto;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.provider.Settings;
import android.widget.*;

public class MainActivity extends Activity {
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout box = new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(32,32,32,32);
        TextView title = new TextView(this); title.setText("Porter Auto Accept"); title.setTextSize(24); box.addView(title);
        TextView info = new TextView(this); info.setText("Configure filters, then enable the Accessibility Service. Porter acceptance is performed with a rightward swipe."); box.addView(info);
        EditText fare = field("Minimum fare (₹)", "300"); box.addView(fare);
        EditText pickup = field("Maximum pickup distance (km)", "5"); box.addView(pickup);
        EditText drop = field("Minimum drop distance (km)", "0"); box.addView(drop);
        EditText areas = field("Allowed areas (comma separated, optional)", ""); box.addView(areas);
        CheckBox auto = new CheckBox(this); auto.setText("Enable auto accept"); box.addView(auto);
        Button save = new Button(this); save.setText("Save settings"); box.addView(save);
        Button accessibility = new Button(this); accessibility.setText("Open Accessibility Settings"); box.addView(accessibility);
        save.setOnClickListener(v -> { getSharedPreferences("rules",0).edit().putString("fare",fare.getText().toString()).putString("pickup",pickup.getText().toString()).putString("drop",drop.getText().toString()).putString("areas",areas.getText().toString()).putBoolean("auto",auto.isChecked()).apply(); Toast.makeText(this,"Saved",Toast.LENGTH_SHORT).show(); });
        accessibility.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        setContentView(box);
    }
    EditText field(String hint,String value){ EditText e=new EditText(this); e.setHint(hint); e.setText(value); e.setSingleLine(true); return e; }
}
