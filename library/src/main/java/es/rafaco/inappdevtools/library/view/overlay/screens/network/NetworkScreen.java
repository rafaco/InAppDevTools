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

package es.rafaco.inappdevtools.library.view.overlay.screens.network;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.core.view.ViewCompat;
//@import androidx.appcompat.widget.AppCompatButton;
//@import androidx.recyclerview.widget.DefaultItemAnimator;
//@import androidx.recyclerview.widget.LinearLayoutManager;
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
//#endif

import com.readystatesoftware.chuck.internal.data.ChuckContentProvider;
import com.readystatesoftware.chuck.sample.HttpBinService;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.integrations.cursoradapter.NetworkCursorAdapter;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;

public class NetworkScreen extends Screen {

    ContentResolver contentResolver;
    private NetworkCursorAdapter adapter;
    private RecyclerView recyclerView;
    private TextView welcome;
    private TextView emptyView;

    public NetworkScreen(ScreenManager manager) {
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
            HttpBinService.simulation(Iadt.getOkHttpClient());
        }
        else if (selected == R.id.action_send)
        {
            //TODO: send all errors
            Iadt.showMessage("Not already implemented");
        }
        return super.onMenuItemClick(item);
    }

    //endregion

}
