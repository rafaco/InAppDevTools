package es.rafaco.inappdevtools.library.view.utils;

//#ifdef MODERN
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

import android.view.View;
import java.util.List;

import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;

public class RecyclerViewUtils {

    private RecyclerViewUtils() { throw new IllegalStateException("Utility class"); }


    public static void updateEmptyState(final RecyclerView recyclerView, final View emptyView, final List<?> data) {
        ThreadUtils.runOnMain(new Runnable() {
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
            }
        });
    }
}
