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

package org.inappdevtools.library.storage.files.utils;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.inappdevtools.library.logic.log.FriendlyLog;
import org.inappdevtools.library.view.utils.PathUtils;

public class ZipUtils {

    private static final int BUFFER = 80000;

    public void zip(List<String> fileList, String zipFileName) {
        FileOutputStream dest = null;
        BufferedInputStream origin = null;
        ZipOutputStream out = null;
        FileInputStream fi = null;
        try {
            dest = new FileOutputStream(zipFileName);
            out = new ZipOutputStream(new BufferedOutputStream(dest));
            byte[] data = new byte[BUFFER];

            for (int i = 0; i < fileList.size(); i++) {
                String filePath = fileList.get(i);
                String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
                Log.v("Compress", "Adding: " + fileName);

                fi = new FileInputStream(filePath);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(fileName);
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
            out.close();
        } catch (Exception e) {
            FriendlyLog.logException("Error zipping report", e);
        }
        finally {
            IOUtil.closeQuietly(origin, out, dest, fi);
        }
    }

    public void zip(Map<String, List<String>> filesMap, String zipFileName) {
        BufferedInputStream origin = null;
        FileOutputStream dest = null;
        ZipOutputStream out = null;
        FileInputStream fi = null;
        try {
            dest = new FileOutputStream(zipFileName);
            out = new ZipOutputStream(new BufferedOutputStream(dest));
            byte[] data = new byte[BUFFER];

            for (Map.Entry<String,List<String>> folderGroup : filesMap.entrySet()){
                String currentFolder = folderGroup.getKey();
                List<String> currentFiles = folderGroup.getValue();

                for (String localFilePath : currentFiles) {
                    String fileName = localFilePath.substring(localFilePath.lastIndexOf('/') + 1);
                    String zipFilePath = PathUtils.join(currentFolder, fileName);
                    FriendlyLog.logDebug("ReportSender: Adding " + zipFilePath);

                    fi = new FileInputStream(localFilePath);
                    origin = new BufferedInputStream(fi, BUFFER);

                    ZipEntry entry = new ZipEntry(zipFilePath);
                    out.putNextEntry(entry);

                    int count;
                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                    }
                    origin.close();
                }
            }
            out.close();
        }
        catch (Exception e) {
            FriendlyLog.logException("Error zipping report", e);
        }
        finally {
            IOUtil.closeQuietly(origin, out, dest, fi);
        }
    }

    public void unzip(String zipFile, String outputFolder) {
        //create target location folder if not exist
        createDirIfNotExist(outputFolder);
        FileOutputStream fout = null;
        ZipInputStream zin = null;
        try {
            zin = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {

                //create dir if required while unzipping
                if (ze.isDirectory()) {
                    createDirIfNotExist(ze.getName());
                } else {
                    fout = new FileOutputStream(outputFolder + ze.getName());
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        fout.write(c);
                    }

                    zin.closeEntry();
                    fout.close();
                }
            }
            zin.close();
        } catch (Exception e) {
            FriendlyLog.logException("Error unzipping report", e);
        }
        finally{
            IOUtil.closeQuietly(fout, zin);
        }
    }

    private void createDirIfNotExist(String path){
        File dir = new File(path);
        if( !dir.exists() ){
            dir.mkdirs();
        }
    }
}
