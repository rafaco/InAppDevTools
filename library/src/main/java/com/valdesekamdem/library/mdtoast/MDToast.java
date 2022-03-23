/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * This is a modified source from project MaterialDesign-Toast, which is available
 * without license at https://github.com/valdesekamdem/MaterialDesign-Toast
 *
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

/*
 * Changelog:
 *     - Added previous attribution notice and this changelog
 *     - Imports resources from es.rafaco.inappdevtools.library.R
 *     - Conditional imports for AndroidX builds
 *     - Using UiUtils.setBackground()
 */

package com.valdesekamdem.library.mdtoast;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

//#ifdef ANDROIDX
//@import androidx.annotation.DrawableRes;
//@import androidx.annotation.StringRes;
//@import androidx.core.content.ContextCompat;
//#else
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
//#endif

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.inappdevtools.library.R;
import org.inappdevtools.library.view.utils.UiUtils;

/**
 * Material Design Toast class
 *
 * Created by Valdèse Kamdem on 03/11/2016.
 */

public class MDToast extends Toast {

    public static final int TYPE_INFO = 0;
    public static final int TYPE_SUCCESS = 1;
    public static final int TYPE_WARNING = 2;
    public static final int TYPE_ERROR = 3;

    public static int LENGTH_LONG = Toast.LENGTH_LONG;
    public static int LENGTH_SHORT = Toast.LENGTH_SHORT;

    private Context mContext;
    private View mView;
    private int mType;

    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public MDToast(Context context) {
        super(context);
        mContext = context;
    }

    public static MDToast makeText(Context context, String message) {
        return makeText(context, message, LENGTH_SHORT, TYPE_INFO);
    }

    public static MDToast makeText(Context context, String message, int duration) {
        return makeText(context, message, duration, TYPE_INFO);
    }

    public static MDToast makeText(Context context, String message, int duration, int type) {
        MDToast mdToast = new MDToast(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_toast_container, null);

        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        TextView text = (TextView) view.findViewById(R.id.text);

        switch (type) {
            case TYPE_SUCCESS:
                icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check_circle_white_24dp));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    UiUtils.setBackground(view, ContextCompat.getDrawable(context, R.drawable.custom_toast_success_background));
                } else view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSuccess));
                break;
            case TYPE_WARNING:
                icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_warning_white_24dp));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    UiUtils.setBackground(view, ContextCompat.getDrawable(context, R.drawable.custom_toast_warn_background));
                } else view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWarning));
                break;
            case TYPE_ERROR:
                icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_error_white_24dp));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    UiUtils.setBackground(view, ContextCompat.getDrawable(context, R.drawable.custom_toast_error_background));
                } else view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorError));
                break;
            default:
                icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_info_white_24dp));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    UiUtils.setBackground(view, ContextCompat.getDrawable(context, R.drawable.custom_toast_info_background));
                } else view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorInfo));
                break;
        }

        text.setText(message);
        mdToast.setDuration(duration);
        mdToast.setView(view);

        mdToast.mView = view;
        mdToast.mType = type;
        return mdToast;
    }

    @Override
    public void setText(@StringRes int resId) {
        setText(mContext.getString(resId));
    }

    @Override
    public void setText(CharSequence s) {
        if (mView == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        TextView tv = (TextView) mView.findViewById(R.id.text);
        if (tv == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        tv.setText(s);
    }

    /**
     * Set the icon resource id to display in the MD toast.
     * @param iconId    the resource id.
     */
    public void setIcon(@DrawableRes int iconId) {
        setIcon(ContextCompat.getDrawable(mContext, iconId));
    }

    /**
     * Set the icon to display in the MD toast.
     * @param icon  the drawable to set as icon.
     */
    public void setIcon(Drawable icon) {
        if (mView == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        ImageView iv = (ImageView) mView.findViewById(R.id.icon);
        if (iv == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        iv.setImageDrawable(icon);
    }

    /**
     * Set the type of the MDToast.
     * @param type  the type to set.
     */
    public void setType(int type) {
        mType = type;
    }

    /**
     * @return  the type of MDToast which is actual used.
     */
    public int getType() {
        return mType;
    }
}
