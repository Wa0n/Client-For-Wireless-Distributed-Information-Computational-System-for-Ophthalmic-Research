package com.pantasenko.retinaexam.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * @author Vladimir Pantasenko
 */
public class InstructionsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_instructions);
    }

}
