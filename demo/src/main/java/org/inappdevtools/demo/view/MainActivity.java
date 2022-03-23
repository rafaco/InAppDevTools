/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
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

package org.inappdevtools.demo.view;

import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Menu;
import android.view.MenuItem;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AppCompatActivity;
//@import androidx.appcompat.widget.Toolbar;
//@import com.google.android.material.snackbar.Snackbar;
//#else
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
//#endif

import org.inappdevtools.library.Iadt;
import org.inappdevtools.demo.R;
import org.inappdevtools.demo.api.DemoAPI;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DemoAPI demoAPI = new DemoAPI();
        demoAPI.start(getApplicationContext());

        //Custom event samples
        Iadt.buildEvent("Custom event sample: develop event")
                .fire();

        Iadt.buildEvent("Custom event sample: reproduction step event")
                .setCategory("YourCategory")
                .setSubcategory("YourSubcategory")
                .isInfo()
                .fire();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            //TODO after publication on Play Store
            //Iadt.shareDemo();
            Snackbar.make(findViewById(android.R.id.content), "Sharing library, app not already published", Snackbar.LENGTH_LONG).show();
            Iadt.shareLibrary();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    //TODO: refactor to BaseActivity or MyActivityLifecycleCallbacks

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                Iadt.isEnabled()) {
            return Iadt.getGestureDetector().onGenericMotionEvent(event);
        }
        return super.onGenericMotionEvent(event);
    }

    //TODO: refactor to BaseActivity or MyActivityLifecycleCallbacks

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (Iadt.isEnabled()) {
            //TODO: Work in progress
            //Iadt.getGestureDetector().onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }
}
