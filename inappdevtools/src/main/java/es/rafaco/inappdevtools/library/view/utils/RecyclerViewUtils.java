package es.rafaco.inappdevtools.library.view.utils;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import java.util.List;

import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;

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
