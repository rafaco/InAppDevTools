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

package es.rafaco.inappdevtools.library.logic.documents;

import android.content.Context;

import java.lang.reflect.InvocationTargetException;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.reports.SessionDetailGenerator;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;

public enum DetailDocument {

    SESSION("Session", R.string.gmd_extension, SessionDetailGenerator.class, Session.class);

    private String title;
    private final int icon;
    private final Class<? extends AbstractDocumentGenerator> generatorClass;
    private final Class<?> paramClass;

    DetailDocument(String title, int icon, Class<? extends AbstractDocumentGenerator> generatorClass, Class<?> paramClass) {
        this.title = title;
        this.paramClass = paramClass;
        this.icon = icon;
        this.generatorClass = generatorClass;
    }

    public String getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public Class<? extends AbstractDocumentGenerator> getGeneratorClass() {
        return generatorClass;
    }

    public Class<?> getParamClass() {
        return paramClass;
    }

    public static DetailDocument[] getValues() {
        return values();
    }

    public AbstractDocumentGenerator getGenerator(Object paramValue) {
        try {
            Class[] cArg = new Class[3];
            cArg[0] = Context.class;
            cArg[1] = DetailDocument.class;
            cArg[2] = paramClass;
            Context context = IadtController.get().getContext();
            return generatorClass.getDeclaredConstructor(cArg)
                    .newInstance(context, this, paramValue);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
