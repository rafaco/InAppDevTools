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

package es.rafaco.inappdevtools.library.view.components.flex;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.view.View;

import es.rafaco.inappdevtools.library.R;


public class EditTextViewHolder extends FlexibleViewHolder {

    private final TextInputEditText editText;
    private final TextInputLayout layout;

    public EditTextViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);

        //this.titleView = view.findViewById(R.id.title);
        this.editText = view.findViewById(R.id.text_input_edit_txt);
        this.layout = view.findViewById(R.id.text_input_layout);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        final EditTextData data = (EditTextData) abstractData;
        if (data!=null){
            //titleView.setText(data.getTitle());
            editText.setHint(data.getTitle());
            editText.setText(data.getText());
            editText.addTextChangedListener(data.getTextWatcher());

            if (data.getLineNumber()>0){
                //editText.setSingleLine(false);
                //editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                //editText.setLines(data.getLineNumber());
                //editText.setMaxLines(data.getLineNumber() + 10);
                //editText.setHorizontallyScrolling(false);
            }

            if (data.getMaxLength() > 0){
                layout.setCounterEnabled(true);
                layout.setCounterMaxLength(data.getMaxLength());
            }
        }
    }
}
