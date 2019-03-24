package es.rafaco.inappdevtools.library.view.overlay.screens.network;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.readystatesoftware.chuck.internal.data.ChuckContentProvider;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.utils.AppInfoUtils;
import es.rafaco.inappdevtools.library.logic.utils.AppUtils;
import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;

public class NetworkScreen extends OverlayScreen {

    ContentResolver contentResolver;
    private NetworkCursorAdapter adapter;
    private RecyclerView recyclerView;
    private TextView welcome;
    private TextView emptyView;

    public NetworkScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Network log";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_network_body; }

    @Override
    public int getToolbarLayoutId() {
        return R.menu.errors;
    }

    @Override
    protected void onCreate() {
        //Nothing needed
    }


    @Override
    protected void onStart(ViewGroup view) {
        initView(bodyView);

        Cursor cursor = getCursor();
        initAdapter(cursor);
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        //Nothing needed
    }

    @Override
    protected void onDestroy() {
        //Nothing needed
    }


    private void initView(View toolView) {
        welcome = toolView.findViewById(R.id.welcome);
        welcome.setText(getWelcomeMessage());
    }

    private Cursor getCursor() {
        Uri transactionUri = Uri.parse("content://" +
                getContext().getPackageName() +
                ".chuck.provider/transaction");
        String[] partialProjection = new String[] {
                "_id",
                "requestDate",
                "tookMs",
                "method",
                "host",
                "path",
                "scheme",
                "requestContentLength",
                "responseCode",
                "error",
                "responseContentLength"
        };
        String sortOrder = "requestDate DESC";

        contentResolver = getContext().getContentResolver();
        return contentResolver.query(transactionUri, partialProjection, null, null, sortOrder);
    }

    private void initAdapter(Cursor cursor){

        adapter = new NetworkCursorAdapter(getContext(), cursor);

        recyclerView = getView().findViewById(R.id.list);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        emptyView = getView().findViewById(R.id.empty_view);
        AppCompatButton traffic1 = getView().findViewById(R.id.traffic_1);
        traffic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });
    }













    private void onClearAll() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                getContext().getContentResolver().delete(ChuckContentProvider.TRANSACTION_URI, null, null);
            }
        });
    }


    public String getWelcomeMessage(){
        return "All network traffic going trow Retrofit.";
    }





    //region [ TOOL BAR ]

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int selected = item.getItemId();
        if (selected == R.id.action_delete)
        {
            onClearAll();
        }
        else if (selected == R.id.action_simulate)
        {
            HttpBinService.simulation(DevTools.getOkHttpClient());
        }
        else if (selected == R.id.action_send)
        {
            //TODO: send all errors
            DevTools.showMessage("Not already implemented");
        }
        return super.onMenuItemClick(item);
    }

    //endregion

}
