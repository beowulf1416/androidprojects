package org.tomale.inventory.android.activity;

import org.tomale.inventory.R;
import org.tomale.inventory.R.layout;

import android.app.Activity;
import android.os.Bundle;

public class PhysicalInventoryActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}