/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
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

package org.inappdevtools.library.logic.builds;

import android.content.Context;

import org.inappdevtools.library.storage.db.IadtDatabase;
import org.inappdevtools.library.storage.db.entities.Build;
import org.inappdevtools.library.storage.db.entities.BuildDao;
import org.inappdevtools.library.storage.prefs.utils.NewBuildPrefs;
import org.inappdevtools.library.logic.log.FriendlyLog;
import org.inappdevtools.library.logic.utils.ThreadUtils;

public class BuildManager {

    private final Context context;
    Build build;

    public BuildManager(Context context) {
        this.context = context;

        FriendlyLog.logDebug("Init build manager");

        if (isNew()){
            build = createNewBuild();
            storeDocuments();
        }
        else{
            build = getDao().getLast();
        }
    }

    public boolean isNew() {
        return NewBuildPrefs.isNewBuild();
    }

    public Build getCurrent() {
        return build;
    }

    public long getCurrentId() {
        return build.getUid();
    }

    private Build createNewBuild() {
        Build newBuild = new Build();
        newBuild.setDate(NewBuildPrefs.getBuildTime());
        long newBuildId = getDao().insert(newBuild);
        newBuild.setUid(newBuildId);
        return newBuild;
    }

    public void updateCurrent(Build updated) {
        getDao().update(updated);
        build = updated;
    }

    //TODO: Remove Build.firstSession
    @Deprecated
    public void updateFirstSession(long firstSessionId) {
        build.setFirstSession(firstSessionId);
        getDao().update(build);
    }

    private BuildDao getDao() {
        return IadtDatabase.get().buildDao();
    }

    public Context getContext() {
        return context;
    }

    public void destroy() {
        //TODO
    }

    public void storeDocuments() {
        final long currentBuildId = build.getUid();
        ThreadUtils.runOnBack("Iadt-SaveCurrentBuildFiles",
                new Runnable() {
                    @Override
                    public void run() {
                        BuildFilesRepository.saveCurrentBuildFiles(currentBuildId);
                    }
                });
    }
}
