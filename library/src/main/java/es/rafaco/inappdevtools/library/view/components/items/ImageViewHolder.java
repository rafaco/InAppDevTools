/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2020 Rafael Acosta Alvarez
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

package es.rafaco.inappdevtools.library.view.components.items;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.components.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.components.FlexibleViewHolder;
import es.rafaco.inappdevtools.library.view.utils.ImageLoaderAsyncTask;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;


public class ImageViewHolder extends FlexibleViewHolder {

    private final ImageView imageView;

    public ImageViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        this.imageView = view.findViewById(R.id.image);
        //this.titleView = view.findViewById(R.id.title);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        final ImageData data = (ImageData) abstractData;
        if (data!=null){

            itemView.setActivated(true);

            /*titleView.setText(data.getTitle());
            if (data.getTitleColor()>0){
                titleView.setTextColor(ContextCompat.getColor(itemView.getContext(), data.getTitleColor()));
            }*/

            if (data.getHeight()>=0){
                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                layoutParams.height = data.getHeight();
                imageView.setLayoutParams(layoutParams);
            }

            if (!TextUtils.isEmpty(data.getImagePath())){
                new ImageLoaderAsyncTask(imageView).execute(data.getImagePath());
            }
            else if (data.getImageDrawable()!=null){
                imageView.setImageDrawable(data.getImageDrawable());
            }
            else if (data.getImageBitmap()!=null){
                imageView.setImageBitmap(data.getImageBitmap());
            }
            else {
                imageView.setVisibility(View.GONE);
            }

            if (data.getPerformer() != null){
                imageView.setClickable(true);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        data.getPerformer().run();
                    }
                });
            }
            else{
                imageView.setClickable(false);
                imageView.setOnClickListener(null);
                UiUtils.setBackground(imageView, null);
            }
        }
    }
}
