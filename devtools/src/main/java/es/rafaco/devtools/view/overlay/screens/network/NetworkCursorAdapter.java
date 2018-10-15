package es.rafaco.devtools.view.overlay.screens.network;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.readystatesoftware.chuck.internal.data.HttpTransaction;
import com.readystatesoftware.chuck.internal.data.LocalCupboard;

import es.rafaco.devtools.R;
import es.rafaco.devtools.view.overlay.OverlayUIService;
import es.rafaco.devtools.view.overlay.layers.NavigationStep;
import es.rafaco.devtools.view.overlay.screens.network.detail.NetworkDetailScreen;

/**
 * Created by skyfishjy on 10/31/14.
 */
public class NetworkCursorAdapter extends CursorRecyclerViewAdapter<NetworkCursorAdapter.ViewHolder>{

    private final int colorDefault;
    private final int colorRequested;
    private final int colorError;
    private final int color500;
    private final int color400;
    private final int color300;

    public NetworkCursorAdapter(Context context,Cursor cursor){
        super(context,cursor);

        colorDefault = ContextCompat.getColor(context, R.color.chuck_status_default);
        colorRequested = ContextCompat.getColor(context, R.color.chuck_status_requested);
        colorError = ContextCompat.getColor(context, R.color.chuck_status_error);
        color500 = ContextCompat.getColor(context, R.color.chuck_status_500);
        color400 = ContextCompat.getColor(context, R.color.chuck_status_400);
        color300 = ContextCompat.getColor(context, R.color.chuck_status_300);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView code;
        public final TextView path;
        public final TextView host;
        public final TextView start;
        public final TextView duration;
        public final TextView size;
        public final ImageView ssl;
        HttpTransaction transaction;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            code = (TextView) view.findViewById(R.id.code);
            path = (TextView) view.findViewById(R.id.path);
            host = (TextView) view.findViewById(R.id.host);
            start = (TextView) view.findViewById(R.id.start);
            duration = (TextView) view.findViewById(R.id.duration);
            size = (TextView) view.findViewById(R.id.size);
            ssl = (ImageView) view.findViewById(R.id.ssl);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tool_network_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        //NetworkItem item = NetworkItem.fromCursor(cursor);
        //viewHolder.mTextView.setText(item.getName());

        final HttpTransaction transaction = LocalCupboard.getInstance().withCursor(cursor).get(HttpTransaction.class);
        holder.path.setText(transaction.getMethod() + " " + transaction.getPath());
        holder.host.setText(transaction.getHost());
        holder.start.setText(transaction.getRequestStartTimeString());
        holder.ssl.setVisibility(transaction.isSsl() ? View.VISIBLE : View.GONE);
        if (transaction.getStatus() == HttpTransaction.Status.Complete) {
            holder.code.setText(String.valueOf(transaction.getResponseCode()));
            holder.duration.setText(transaction.getDurationString());
            holder.size.setText(transaction.getTotalSizeString());
        } else {
            holder.code.setText(null);
            holder.duration.setText(null);
            holder.size.setText(null);
        }
        if (transaction.getStatus() == HttpTransaction.Status.Failed) {
            holder.code.setText("!!!");
        }
        setStatusColor(holder, transaction);
        holder.transaction = transaction;
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setTag(transaction.getId());
                onHolderClick(v);
            }
        });
    }

    private void onHolderClick(View v) {
        //Todo
        NavigationStep step = new NavigationStep(NetworkDetailScreen.class, String.valueOf(v.getTag()));
        OverlayUIService.performNavigationStep(step);
    }

    private void setStatusColor(ViewHolder holder, HttpTransaction transaction) {
        int color;
        if (transaction.getStatus() == HttpTransaction.Status.Failed) {
            color = colorError;
        } else if (transaction.getStatus() == HttpTransaction.Status.Requested) {
            color = colorRequested;
        } else if (transaction.getResponseCode() >= 500) {
            color = color500;
        } else if (transaction.getResponseCode() >= 400) {
            color = color400;
        } else if (transaction.getResponseCode() >= 300) {
            color = color300;
        } else {
            color = colorDefault;
        }
        holder.code.setTextColor(color);
        holder.path.setTextColor(color);
    }
}
