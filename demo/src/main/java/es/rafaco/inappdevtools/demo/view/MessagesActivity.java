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

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.ActionBar;
//@import androidx.appcompat.app.AppCompatActivity;
//@import androidx.appcompat.widget.Toolbar;
//#else
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
//#endif

import es.rafaco.compat.CardView;
import es.rafaco.inappdevtools.demo.R;
import es.rafaco.inappdevtools.library.Iadt;

public class MessagesActivity extends AppCompatActivity {

    private TextView firstContent;
    private TextView secondContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        firstContent = findViewById(R.id.main_content);
        firstContent.setText("You can show special toast messages only for your internal users. Try it now!");

        CardView devCard = findViewById(R.id.card_view_dev);
        devCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Iadt.buildMessage("This is a DEV message").fire();
            }
        });

        CardView infoCard = findViewById(R.id.card_view_info);
        infoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Iadt.buildMessage("This is a INFO message").isInfo().fire();
            }
        });

        CardView warningCard = findViewById(R.id.card_view_warning);
        warningCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Iadt.buildMessage("This is a WARNING message").isWarning().fire();
            }
        });

        CardView errorCard = findViewById(R.id.card_view_error);
        errorCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Iadt.buildMessage("This is a ERROR message").isError().fire();
            }
        });


        secondContent = findViewById(R.id.secondary_content);
        secondContent.setText("This messages will be shown when this library is enabled and will be ignored on your release builds.\n\nYour internal users can easily distinguish them from the standard toast as they are shown in a top position and they are colored base on the severity. This messages will auto generate an event.");
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
