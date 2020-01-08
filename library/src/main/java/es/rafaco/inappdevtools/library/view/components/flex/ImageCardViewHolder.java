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

import android.content.Context;

import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.integrations.CustomToast;
import es.rafaco.inappdevtools.library.storage.db.entities.Screenshot;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;
import es.rafaco.inappdevtools.library.view.utils.ImageLoaderAsyncTask;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//@import androidx.cardview.widget.CardView;
//#else
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
//#endif

public class ImageCardViewHolder extends FlexibleViewHolder {

    private CardView cardView;
    public TextView title, count;
    public ImageView thumbnail, overflow;

    public ImageCardViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        cardView = view.findViewById(R.id.card_view);
        title = view.findViewById(R.id.title);
        count = view.findViewById(R.id.subtitle);
        thumbnail = view.findViewById(R.id.thumbnail);
        overflow = view.findViewById(R.id.overflow);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        final Screenshot data = (Screenshot) abstractData;
        if (data!=null){

            title.setText(data.getActivityName());
            count.setText(Humanizer.getElapsedTime(data.getDate()));

            // loading album cover using Glide library
            //Glide.with(mContext).load(screenshot.getThumbnail()).into(holder.thumbnail);
            //ImageLoader.loadInto(screenshot.getPath(), holder.thumbnail);
            new ImageLoaderAsyncTask(thumbnail).execute(data.getPath());

            overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(overflow);
                }
            });
        }
    }

    //region [ ACTIONS POP UP ]

    private void showPopupMenu(View view) {

        Context wrapper = new ContextThemeWrapper(cardView.getContext(),
                R.style.LibPopupMenuStyle);
        PopupMenu popup = new PopupMenu(wrapper, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.screenshots, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            int i = menuItem.getItemId();
            if (i == R.id.action_preview) {
                Iadt.showMessage("Preview");
                return true;
            } else if (i == R.id.action_open) {
                CustomToast.show(cardView.getContext(), "Open", CustomToast.TYPE_WARNING);

                return true;
            } else if (i == R.id.action_delete) {
                Iadt.showMessage("Delete");
                return true;
            }
            return false;
        }
    }

    //endregion
}
