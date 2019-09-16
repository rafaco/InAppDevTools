package es.rafaco.inappdevtools.demo.view;

import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AppCompatActivity;
//@import androidx.appcompat.widget.Toolbar;
//@import com.google.android.material.floatingactionbutton.FloatingActionButton;
//@import com.google.android.material.snackbar.Snackbar;
//#else
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
//#endif

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.logic.config.Config;
import es.rafaco.inappdevtools.demo.R;
import es.rafaco.inappdevtools.demo.api.Controller;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;

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
                //throw new NullPointerException("A simulated exception from MainActivity fab button");
            }
        });

        Controller controller = new Controller();
        controller.start(getApplicationContext());
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
            Iadt.getConfig().getBoolean(Config.ENABLED)) {
            return Iadt.getGestureDetector().onGenericMotionEvent(event);
        }
        return super.onGenericMotionEvent(event);
    }

    //TODO: refactor to BaseActivity or MyActivityLifecycleCallbacks

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (Iadt.getConfig().getBoolean(Config.ENABLED)) {
            //TODO: Work in progress
            //Iadt.getGestureDetector().onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }
}
