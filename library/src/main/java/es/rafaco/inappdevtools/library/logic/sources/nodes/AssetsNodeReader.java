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

package es.rafaco.inappdevtools.library.logic.sources.nodes;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;

public class AssetsNodeReader extends AbstractNodeReader{

    private final Context context;

    public AssetsNodeReader(Context context, String prefix) {
        super();
        this.context = context;
        this.prefix = prefix;
        root = new StandardNode("root", "/", true);
        addEntry(prefix, prefix + "/", true);
    }

    public AssetsNodeReader(Context context, AbstractNodeReader previousReader, String prefix) {
        super(previousReader, prefix);
        this.context = context;
    }

    @Override
    public AbstractNode populate() {
        AssetManager aMan = context.getApplicationContext().getAssets();

        List<String> categories = new ArrayList<>();
        try {
            categories =  Arrays.asList(aMan.list(""));
        } catch (IOException e) {
            FriendlyLog.logException("Exception", e);
        }

        for (String category: categories) {
            String categoryPath = prefix + "/" + category + "/";
            addEntry(category, categoryPath, true);

            List<String> resources = new ArrayList<>();
            try {
                resources =  Arrays.asList(aMan.list(category));
            } catch (IOException e) {
                FriendlyLog.logException("Exception", e);
            }

            for (String resource: resources) {
                boolean isDirectory = resource.endsWith("/");
                addEntry(resource, categoryPath + resource, isDirectory);
            }
        }
        return root;
    }


    protected AbstractNode addEntry(String entryName, String entryPath, boolean isDirectory) {
        AbstractNode node = collected.get(entryPath);
        if(node != null) {
            // already in the map
            return node;
        }
        node = new StandardNode(entryName, entryPath, isDirectory);
        collected.put(entryPath, node);
        findParent(node);
        return node;
    }

    @Override
    protected AbstractNode createParentNode(String parentName) {
        String shortName = parentName.substring(parentName.lastIndexOf("/"),
                parentName.length()-1);
        return addEntry(shortName, parentName, true);
    }
}
