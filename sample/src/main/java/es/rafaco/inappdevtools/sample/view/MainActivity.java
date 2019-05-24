package es.rafaco.inappdevtools.sample.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

//#ifdef MODERN
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
//#else
//@import android.support.v7.app.AppCompatActivity;
//@import android.support.v7.widget.AppCompatButton;
//@import android.support.design.widget.FloatingActionButton;
//@import android.support.design.widget.Snackbar;
//@import android.support.v7.widget.Toolbar;
//#endif

import es.rafaco.inappdevtools.sample.R;
import es.rafaco.inappdevtools.sample.SampleApp;
import es.rafaco.inappdevtools.sample.api.Controller;
import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendlyLog.log("I", "User", "Touch", "User clicked on FloatingActionButton");
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                throw new NullPointerException("A simulated exception from MainActivity fab button");
            }
        });

        AppCompatButton showTools = findViewById(R.id.show_tools);
        showTools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendlyLog.log("I", "User", "Touch", "User clicked on ShowDevTools");
                DevTools.openTools(false);
            }
        });

        AppCompatButton browseDemo = findViewById(R.id.browse);
        browseDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendlyLog.log("I", "User", "Touch", "User clicked on Browse Demo");
                Intent intent = new Intent(MainActivity.this.getApplicationContext(), ItemListActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        Controller controller = new Controller();
        controller.start(getApplicationContext());

        Log.d(SampleApp.TAG, "MainActivity onCreate() performed");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    //TODO: refactor to BaseActivity or MyActivityLifecycleCallbacks

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return DevTools.getWatcherManager().getGestureDetector().onGenericMotionEvent(event);
        }
        return super.onGenericMotionEvent(event);
    }

    //TODO: refactor to BaseActivity or MyActivityLifecycleCallbacks

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        DevTools.getWatcherManager().getGestureDetector().onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
}
