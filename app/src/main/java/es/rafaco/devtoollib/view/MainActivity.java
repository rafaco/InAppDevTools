package es.rafaco.devtoollib.view;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import es.rafaco.devtoollib.R;
import es.rafaco.devtoollib.SampleApp;
import es.rafaco.devtoollib.api.Controller;
import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.logic.steps.FriendlyLog;
import es.rafaco.devtools.logic.integrations.RunnableConfig;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendlyLog.log("I", "User", "Touch", "User clicked on FloatingActionButton");
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                throw new NullPointerException("A simulated exception from MainActivity fab button");
                /*Intent intent = new Intent(getApplicationContext(), CrashActivity.class);
                intent.putExtra("TITLE", "title");
                intent.putExtra("MESSAGE", "exMessage");
                getApplicationContext().startActivity(intent);*/
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
                Intent intent = new Intent(getApplicationContext(), ItemListActivity.class);
                startActivity(intent);
            }
        });

        Controller controller = new Controller();
        controller.start(getApplicationContext());

        Log.d(SampleApp.TAG, "MainActivity onCreate() performed");
        //DevTools.breakpoint(this);

        DevTools.addCustomRunnable(new RunnableConfig("Show message",
                R.drawable.ic_run_white_24dp,
                () -> DevTools.showMessage("Mostrando mensaje"),
                () -> DevTools.showMessage("Mensaje mostrado")));

        DevTools.addCustomRunnable(new RunnableConfig("Select API...",
                R.drawable.ic_settings_white_24dp,
                () -> DevTools.showMessage("Not already implemented")));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        FriendlyLog.log("W", "User", "Touch", "User touch:" + ev.toString());
        return super.dispatchTouchEvent(ev);
    }
}
