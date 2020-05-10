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
import android.util.Log;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentData;
import es.rafaco.inappdevtools.library.logic.documents.generators.AbstractDocumentGenerator;
import es.rafaco.inappdevtools.library.storage.files.utils.FileCreator;
import es.rafaco.inappdevtools.library.storage.files.utils.FileProviderUtils;
import es.rafaco.inappdevtools.library.view.components.cards.CardData;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class DocumentRepository {

    private static DocumentFormatter formatter;

    private DocumentRepository() {
        throw new IllegalStateException("Utility class");
    }

    public static DocumentFormatter getFormatter(){
        if (formatter == null){
            formatter = new DocumentFormatter();
        }
        return formatter;
    }

    public static DocumentData getDocument(DocumentType documentType) {
        return getDocument(documentType, getDefaultParam());
    }

    public static DocumentData getDocument(DocumentType documentType, Object param) {
        AbstractDocumentGenerator generator = getGenerator(documentType, param);
        if (generator == null)
            return null;
        return generator.getData();
    }


    public static AbstractDocumentGenerator getGenerator(DocumentType documentType) {
        return buildGenerator(documentType, getDefaultParam());
    }

    public static AbstractDocumentGenerator getGenerator(DocumentType documentType, Object param) {
        return buildGenerator(documentType, param);
    }

    private static long getDefaultParam() {
        //It currently pass the current session id, needed by Info documents
        return IadtController.get().getSessionManager().getCurrentUid();
    }


    public static String getDocumentPath(DocumentType documentType, Object data) {
        String savedPath = getStoredDocumentPath(documentType, data);
        if (TextUtils.isEmpty(savedPath)){
            savedPath = storeDocument(documentType, data);
        }else{
            Log.v(Iadt.TAG, "Document retrieve: " + documentType.getName() + " " + data.toString());
        }
        return savedPath;
    }

    public static String storeDocument(DocumentType documentType, Object data) {
        AbstractDocumentGenerator generator = getGenerator(documentType, data);
        if (generator == null){
            return null;
        }

        String formattedDocument = getFormatter().formatDocument(generator.getTitle(),
                generator.getData().toString());
        String filePath = FileCreator.withContent(
                generator.getSubfolder(),
                generator.getFilename(),
                formattedDocument);
        if (TextUtils.isEmpty(filePath)){
            return null;
        }

        return filePath;
    }

    private static String getStoredDocumentPath(DocumentType documentType, Object data){
        AbstractDocumentGenerator generator = getGenerator(documentType, data);
        if (generator != null &&
                FileCreator.exists(generator.getSubfolder(), generator.getFilename())) {
            return FileCreator.getPath(generator.getSubfolder(), generator.getFilename());
        }
        return null;
    }

    private static AbstractDocumentGenerator buildGenerator(DocumentType documentType, Object param) {
        Context context = IadtController.get().getContext();
        try {
            Class[] cArg = new Class[3];
            cArg[0] = Context.class;
            cArg[1] = documentType.getClass();
            cArg[2] = documentType.getParamClass();
            return documentType.getGeneratorClass()
                    .getDeclaredConstructor(cArg)
                    .newInstance(context, documentType, param);
        }
        catch (Exception e) {
            IadtController.get().handleInternalException("DocumentRepository buildGenerator", e);
        }
        return null;
    }

    public static void shareDocument(DocumentType documentType, Object param) {
        DocumentData document = getDocument(documentType, param);
        String path = getDocumentPath(documentType, param);
        if (document!=null && !TextUtils.isEmpty(path))
            FileProviderUtils.sendExternally(document.getTitle(), path);
        else
            Iadt.showError("Unable to build the document to share");
    }

    public static CardData getCardDataLink(DocumentType documentType, final Class<? extends Screen> screenClass, final String param) {
        DocumentData reportData = DocumentRepository.getDocument(documentType);
        if (reportData == null)
            return null;

        String shortTitle = Humanizer.removeTailStartingWith(reportData.getTitle(), " from");
        return new CardData(shortTitle,
                reportData.getOverview(),
                reportData.getIcon(),
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(screenClass, param);
                    }
                });
    }
}
