/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2020 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.rafaco.inappdevtools.demo.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.ActionBar;
//@import androidx.appcompat.app.AppCompatActivity;
//@import androidx.appcompat.widget.Toolbar;
//@import com.google.android.material.floatingactionbutton.FloatingActionButton;
//#else
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
//#endif

import es.rafaco.inappdevtools.demo.R;
import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.storage.files.utils.ScreenshotUtils;

public class CrashActivity extends AppCompatActivity {

    private TextView countDown;
    private int counter = -1;
    private TextView firstContent;
    private TextView secondContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Iadt.trackUserAction("User clicked on FloatingActionButton");
                AlertDialog alertDialog = new AlertDialog.Builder(CrashActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Simple alert message");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                ScreenshotUtils.takeAndSave(false);
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });


        final Button customButton = findViewById(R.id.custom_button);
        countDown = findViewById(R.id.count_down);
        firstContent = findViewById(R.id.main_content);
        secondContent = findViewById(R.id.secondary_content);

        firstContent.setText("We detect any crash on your app and immediately show you full details on the same screen. Try it now!");
        secondContent.setText("Crash message, exception, cause, logs, repro steps, report button and visual stacktrace with navigation through to your source lines.");
        customButton.setText("CRASH\nME");

        customButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customButton.setVisibility(View.GONE);
                startCountDown();
            }
        });
    }

    private void startCountDown() {
        counter = 3;
        new CountDownTimer(3000, 1000){
            public void onTick(long millisUntilFinished){
                countDown.setText(String.valueOf(counter));
                counter--;
            }
            public  void onFinish(){
                countDown.setText("BOOM!");
                Iadt.crashBackgroundThread();
            }
        }.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
