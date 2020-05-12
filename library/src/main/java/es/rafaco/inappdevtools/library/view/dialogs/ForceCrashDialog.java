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

package es.rafaco.inappdevtools.library.view.dialogs;

import android.content.DialogInterface;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.crash.CrashGenerator;
import es.rafaco.inappdevtools.library.logic.crash.ForcedRuntimeException;
import es.rafaco.inappdevtools.library.view.components.items.ButtonFlexData;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.view.components.cards.CardData;
import es.rafaco.inappdevtools.library.view.components.FlexAdapter;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AlertDialog;
//#else
import android.support.v7.app.AlertDialog;
//#endif

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

public class ForceCrashDialog extends IadtDialogBuilder {

    public ForceCrashDialog() {
        super();
    }

    @Override
    public void onBuilderCreated(AlertDialog.Builder builder) {
        builder
                .setTitle("Force a crash")
                .setMessage("You can force a crash now to test our library")
                .setIcon(R.drawable.ic_bug_report_white_24dp)
                .setNeutralButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onDisable();
                    }
                })
                .setCancelable(true);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.flexible_container, null);
        builder.setView(dialogView);

        List<Object> data = new ArrayList<>();
        int cardBackground = R.color.iadt_surface_top;

        
        data.add(new CardData("Background thread",
                "Crash a new thread on background loop",
                R.string.gmd_line_style,
                new Runnable() {
                    @Override
                    public void run() {
                        Log.i(Iadt.TAG, getContext().getString(R.string.simulated_crash_background));
                        final Exception cause = new TooManyListenersException(getContext().getString(R.string.simulated_crash_cause));
                        ThreadUtils.runOnBack(new Runnable() {
                            @Override
                            public void run() {
                                throw new ForcedRuntimeException(getContext().getString(R.string.simulated_crash_background), cause);
                            }
                        });
                    }
                }).setBgColor(cardBackground));

        data.add(new CardData("Main thread",
                "Crash a new thread on main loop",
                R.string.gmd_layers,
                new Runnable() {
                    @Override
                    public void run() {
                        Log.i(Iadt.TAG, getContext().getString(R.string.simulated_crash_foreground));
                        final Exception cause = new TooManyListenersException(getContext().getString(R.string.simulated_crash_cause));
                        ThreadUtils.runOnMain(new Runnable() {
                            @Override
                            public void run() {
                                throw new ForcedRuntimeException(getContext().getString(R.string.simulated_crash_foreground), cause);
                            }
                        });
                    }
                }).setBgColor(cardBackground));


        data.add(new CardData("Main Loop",
                "Crash current main loop",
                R.string.gmd_flash_on,
                new Runnable() {
                    @Override
                    public void run() {
                        Log.i(Iadt.TAG, getContext().getString(R.string.simulated_crash_foreground));
                        final Exception cause = new TooManyListenersException(getContext().getString(R.string.simulated_crash_cause));
                        throw new ForcedRuntimeException(getContext().getString(R.string.simulated_crash_foreground), cause);
                    }
                }).setBgColor(cardBackground));

        data.add(new ButtonFlexData("IndexOutOfBounds",
                R.drawable.ic_bug_report_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        CrashGenerator.indexOutOfBounds();
                    }
                }));

        data.add(new ButtonFlexData("NullPointer",
                R.drawable.ic_bug_report_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        CrashGenerator.nullPointer();
                    }
                }));

        data.add(new ButtonFlexData("ZeroDivision",
                R.drawable.ic_bug_report_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        CrashGenerator.arithmeticException();
                    }
                }));

        data.add(new ButtonFlexData("StackOverflow",
                R.drawable.ic_bug_report_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        CrashGenerator.stackOverflow();
                    }
                }));

        FlexAdapter presetAdapter = new FlexAdapter(FlexAdapter.Layout.GRID, 2, data);
        RecyclerView recyclerView = dialogView.findViewById(R.id.flexible);
        recyclerView.setAdapter(presetAdapter);
    }

    public void onDisable() {
    }
}
