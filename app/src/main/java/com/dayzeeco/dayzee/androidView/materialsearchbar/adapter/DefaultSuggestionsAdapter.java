package com.dayzeeco.dayzee.androidView.materialsearchbar.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dayzeeco.dayzee.R;


public class DefaultSuggestionsAdapter extends SuggestionsAdapter<String, DefaultSuggestionsAdapter.SuggestionHolder> {
    private SuggestionsAdapter.OnItemViewClickListener listener;

    public DefaultSuggestionsAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    public void setListener(SuggestionsAdapter.OnItemViewClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getSingleViewHeight() {
        return 32;
    }

    @Override
    public DefaultSuggestionsAdapter.SuggestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.item_last_request, parent, false);
        return new DefaultSuggestionsAdapter.SuggestionHolder(view);
    }

    @Override
    public void onBindSuggestionHolder(String suggestion, SuggestionHolder holder, int position) {
        holder.text.setText(getSuggestions().get(position));
    }

    public interface OnItemViewClickListener {
        void OnItemClickListener(int position, View v);
        void OnItemDeleteListener(int position, View v);}

    class SuggestionHolder extends RecyclerView.ViewHolder {
        private final TextView text;
        private final ImageView iv_delete;

        public SuggestionHolder(final View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            iv_delete = itemView.findViewById(R.id.iv_delete);
            itemView.setOnClickListener(v -> {
                v.setTag(getSuggestions().get(getAdapterPosition()));
                listener.OnItemClickListener(getAdapterPosition(), v);
            });
            iv_delete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position >= 0 && position < getSuggestions().size()) {
                    v.setTag(getSuggestions().get(getAdapterPosition()));
                    listener.OnItemDeleteListener(getAdapterPosition(), v);
                }
            });
        }
    }
}
