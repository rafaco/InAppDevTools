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

package es.rafaco.inappdevtools.demo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//@import androidx.annotation.Nullable;
//@import androidx.fragment.app.Fragment;
//@import androidx.appcompat.widget.AppCompatButton;
//#else
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
//#endif

import es.rafaco.compat.CardView;
import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.demo.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CardView card_crash = view.findViewById(R.id.card_view_crash);
        card_crash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Iadt.trackUserAction("User clicked on Crash section");
                Intent intent = new Intent(getContext(), CrashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                startActivity(intent);
            }
        });

        CardView card_messages = view.findViewById(R.id.card_view_messages);
        card_messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Iadt.trackUserAction("User clicked on Messages section");
                Intent intent = new Intent(getContext(), MessagesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                startActivity(intent);
            }
        });

        CardView card_browse = view.findViewById(R.id.card_view_browse);
        card_browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Iadt.trackUserAction("User clicked on Browse Demo");
                Intent intent = new Intent(getContext(), ItemListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                startActivity(intent);
            }
        });

        CardView card_help = view.findViewById(R.id.card_view_help);
        card_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Iadt.trackUserAction("User clicked on help button");
                Iadt.viewReadme();
            }
        });


        // Dialog bouncing playground
        /*final View finalView = view;
        finalView.postDelayed(new Runnable() {
            @Override
            public void run() {
                finalView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getContext(), CrashActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        startActivity(intent);
                    }
                }, 1000);
            }
        }, 1000);*/
    }
}
