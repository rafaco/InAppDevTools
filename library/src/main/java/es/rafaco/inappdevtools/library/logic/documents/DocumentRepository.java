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
import android.text.TextUtils;

import java.lang.reflect.InvocationTargetException;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.documents.generators.AbstractDocumentGenerator;
import es.rafaco.inappdevtools.library.storage.files.utils.FileCreator;

public class DocumentRepository {

    public static AbstractDocumentGenerator getGenerator(Document document) {
        //TODO: default to current session
        //Used by Info documents
        long sessionId = IadtController.get().getSessionManager().getCurrent().getUid();
        return buildGenerator(document, sessionId);
    }

    public static AbstractDocumentGenerator getGenerator(Document document, Object param) {
        return buildGenerator(document, param);
    }

    public static AbstractDocumentGenerator buildGenerator(Document document, Object param) {
        Context context = IadtController.get().getContext();
        try {
            Class[] cArg = new Class[3];
            cArg[0] = Context.class;
            cArg[1] = document.getClass();
            cArg[2] = document.getParamClass();
            return document.getGeneratorClass()
                    .getDeclaredConstructor(cArg)
                    .newInstance(context, document, param);
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



    public static String saveDocument(Document document, Object data){
        AbstractDocumentGenerator detailGenerator = getGenerator(document, data);
        String formattedDocument = getFormatter().formatDocument(detailGenerator.getTitle(),
                detailGenerator.getData().toString());

        String filePath = FileCreator.withContent(
                detailGenerator.getSubfolder(),
                detailGenerator.getFilename(),
                formattedDocument);

        if (TextUtils.isEmpty(filePath)){
            return null;
        }

        return filePath;
    }



    private static DocumentFormatter formatter;

    public static DocumentFormatter getFormatter(){
        if (formatter == null){
            formatter = new DocumentFormatter();
        }
        return formatter;
    }
}
