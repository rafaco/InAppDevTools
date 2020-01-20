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

import es.rafaco.inappdevtools.library.logic.documents.data.DocumentData;

public class DocumentManager {

    private final Context context;

    public DocumentManager(Context context) {
        this.context = context;
    }


    public DocumentData getInfoData(InfoDocument document) {
        return getInfoGenerator(document).getData();
    }

    public AbstractDocumentGenerator getInfoGenerator(InfoDocument document) {
        return buildGenerator(document, null);
    }

    public DocumentData getDetailData(DetailDocument document, Object param) {
        return getDetailGenerator(document, param).getData();
    }

    public AbstractDocumentGenerator getDetailGenerator(DetailDocument document, Object param) {
        return buildGenerator(document, param);
    }

    public AbstractDocumentGenerator buildGenerator(Object document, Object param) {
        try {
            if (document instanceof InfoDocument) {
                InfoDocument doc = (InfoDocument) document;
                Class[] cArg = new Class[2];
                cArg[0] = Context.class;
                cArg[1] = doc.getClass();
                return doc.getGeneratorClass()
                        .getDeclaredConstructor(cArg)
                        .newInstance(context, doc);
            }
            else if (document instanceof DetailDocument){
                DetailDocument doc = (DetailDocument) document;
                Class[] cArg = new Class[3];
                cArg[0] = Context.class;
                cArg[1] = doc.getClass();
                cArg[2] = doc.getParamClass();
                return doc.getGeneratorClass()
                        .getDeclaredConstructor(cArg)
                        .newInstance(context, doc, param);
            }
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
