package es.rafaco.inappdevtools.library.view.components.flex;

import android.view.View;

import es.rafaco.compat.AppCompatTextView;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.storage.db.entities.AnalysisItem;

public class AnalysisViewHolder extends FlexibleViewHolder {

    private final AppCompatTextView nameView;
    private final AppCompatTextView countView;
    private final AppCompatTextView percentageView;

    public AnalysisViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        this.nameView = view.findViewById(R.id.name);
        this.countView = view.findViewById(R.id.count);
        this.percentageView = view.findViewById(R.id.percentage);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        AnalysisItem data = (AnalysisItem) abstractData;
        nameView.setText(data.getName());
        countView.setText(String.valueOf(data.getCount()));
        percentageView.setText(String.valueOf(data.getPercentage()) + "% ");
    }
}
