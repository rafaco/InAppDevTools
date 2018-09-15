package es.rafaco.devtools.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import java.util.List;

public class RecyclerViewUtils {

    public static void updateEmptyState(final RecyclerView recyclerView, final View emptyView, final List<?> data) {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (data.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
                else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }

                //recyclerView.requestLayout();
            }
        });
    }
}