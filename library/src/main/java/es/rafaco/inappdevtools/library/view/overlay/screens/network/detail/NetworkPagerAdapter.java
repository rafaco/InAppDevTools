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

package es.rafaco.inappdevtools.library.view.overlay.screens.network.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//#else
import android.support.annotation.NonNull;
//#endif

import com.readystatesoftware.chuck.internal.data.HttpTransaction;

import nevet.me.wcviewpager.ObjectAtPositionPagerAdapter;

public class NetworkPagerAdapter extends ObjectAtPositionPagerAdapter {

    private HttpTransaction transaction;

    public NetworkPagerAdapter(HttpTransaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return NetworkPage.values()[position].getTitle();
    }

    @Override
    public int getCount() {
        return NetworkPage.values().length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }

    @Override
    public Object instantiateItemObject(ViewGroup container, int position) {
        NetworkPage page = NetworkPage.values()[position];

        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        ViewGroup layout = (ViewGroup) inflater.inflate(page.getLayoutResId(), container, false);
        container.addView(layout);

        page.getViewHolder().onCreatedView(layout);
        page.getViewHolder().populateUI(transaction);

        return layout;
    }

    @Override
    public void destroyItemObject(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
