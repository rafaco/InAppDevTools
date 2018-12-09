package es.rafaco.inappdevtools.view.overlay.screens.network.detail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.readystatesoftware.chuck.internal.data.HttpTransaction;

import es.rafaco.inappdevtools.R;

public enum NetworkPage {

    DETAIL("OVERVIEW", R.layout.tool_network_detail_overview),
    REQUEST("REQUEST", R.layout.tool_network_detail_payload),
    RESPONSE("RESPONSE", R.layout.tool_network_detail_payload);

    private String mTitle;
    private int mLayoutResId;
    private TransactionViewHolder viewHolder;

    NetworkPage(String title, int layoutResId) {
        mTitle = title;
        mLayoutResId = layoutResId;

        if (getTitle().equals("OVERVIEW")){
            viewHolder = new TransactionOverviewViewHolder();
        }else if (getTitle().equals("REQUEST")){
            viewHolder = new TransactionPayloadViewHolder(TransactionPayloadViewHolder.TYPE_REQUEST);
        }else if (getTitle().equals("RESPONSE")){
            viewHolder = new TransactionPayloadViewHolder(TransactionPayloadViewHolder.TYPE_RESPONSE);
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

    public TransactionViewHolder getViewHolder() {
        return viewHolder;
    }

    public interface TransactionViewHolder{
        View getContentView();
        View onCreatedView(ViewGroup view);
        void populateUI(HttpTransaction transaction);
    }

    private class TransactionOverviewViewHolder implements TransactionViewHolder{
        TextView url;
        TextView method;
        TextView protocol;
        TextView status;
        TextView response;
        TextView ssl;
        TextView requestTime;
        TextView responseTime;
        TextView duration;
        TextView requestSize;
        TextView responseSize;
        TextView totalSize;
        private View contentView;

        @Override
        public View getContentView() {
            return contentView;
        }

        @SuppressLint("WrongViewCast")
        @Override
        public View onCreatedView(ViewGroup view){
            contentView = view.findViewById(R.id.content);
            url = (TextView) view.findViewById(com.readystatesoftware.chuck.R.id.url);
            method = (TextView) view.findViewById(com.readystatesoftware.chuck.R.id.method);
            protocol = (TextView) view.findViewById(com.readystatesoftware.chuck.R.id.protocol);
            status = (TextView) view.findViewById(com.readystatesoftware.chuck.R.id.status);
            response = (TextView) view.findViewById(com.readystatesoftware.chuck.R.id.response);
            ssl = (TextView) view.findViewById(com.readystatesoftware.chuck.R.id.ssl);
            requestTime = (TextView) view.findViewById(com.readystatesoftware.chuck.R.id.request_time);
            responseTime = (TextView) view.findViewById(com.readystatesoftware.chuck.R.id.response_time);
            duration = (TextView) view.findViewById(com.readystatesoftware.chuck.R.id.duration);
            requestSize = (TextView) view.findViewById(com.readystatesoftware.chuck.R.id.request_size);
            responseSize = (TextView) view.findViewById(com.readystatesoftware.chuck.R.id.response_size);
            totalSize = (TextView) view.findViewById(com.readystatesoftware.chuck.R.id.total_size);
            return view;
        }

        @Override
        public void populateUI(HttpTransaction transaction){
            if (transaction != null) {
                url.setText(transaction.getUrl());
                method.setText(transaction.getMethod());
                protocol.setText(transaction.getProtocol());
                status.setText(transaction.getStatus().toString());
                response.setText(transaction.getResponseSummaryText());
                ssl.setText((transaction.isSsl() ? com.readystatesoftware.chuck.R.string.chuck_yes : com.readystatesoftware.chuck.R.string.chuck_no));
                requestTime.setText(transaction.getRequestDateString());
                responseTime.setText(transaction.getResponseDateString());
                duration.setText(transaction.getDurationString());
                requestSize.setText(transaction.getRequestSizeString());
                responseSize.setText(transaction.getResponseSizeString());
                totalSize.setText(transaction.getTotalSizeString());
            }
        }
    }
    private class TransactionPayloadViewHolder implements TransactionViewHolder{

        public static final int TYPE_REQUEST = 0;
        public static final int TYPE_RESPONSE = 1;

        private static final String ARG_TYPE = "type";

        private View contentView;
        TextView headers;
        TextView body;
        private int type;

        public TransactionPayloadViewHolder(int type) {
            this.type = type;
        }

        @Override
        public View getContentView() {
            return contentView;
        }
        @Override
        public View onCreatedView(ViewGroup view) {
            contentView = view.findViewById(R.id.content);
            headers = (TextView) view.findViewById(com.readystatesoftware.chuck.R.id.headers);
            body = (TextView) view.findViewById(com.readystatesoftware.chuck.R.id.body);
            return view;        }

        @Override
        public void populateUI(HttpTransaction transaction) {
            if (transaction != null) {
                switch (type) {
                    case TYPE_REQUEST:
                        setText(transaction.getRequestHeadersString(true),
                                transaction.getFormattedRequestBody(), transaction.requestBodyIsPlainText());
                        break;
                    case TYPE_RESPONSE:
                        setText(transaction.getResponseHeadersString(true),
                                transaction.getFormattedResponseBody(), transaction.responseBodyIsPlainText());
                        break;
                }
            }
        }

        private void setText (String headersString, String bodyString,boolean isPlainText){
            Context context = headers.getContext();
            headers.setVisibility((TextUtils.isEmpty(headersString) ? View.GONE : View.VISIBLE));
            headers.setText(Html.fromHtml(headersString));
            if (!isPlainText) {
                body.setText(context.getString(com.readystatesoftware.chuck.R.string.chuck_body_omitted));
            } else {
                body.setText(bodyString);
            }
        }
    }
}