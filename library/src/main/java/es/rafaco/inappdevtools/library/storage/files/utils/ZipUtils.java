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

package es.rafaco.inappdevtools.library.storage.files.utils;

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

import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;

public class ZipUtils {

    private static final int BUFFER = 80000;

    /*public void test(){

        String[] s = new String[2];

        // Type the path of the files in here
        s[0] = inputPath + "/image.jpg";
        s[1] = inputPath + "/textfile.txt"; // /sdcard/ZipDemo/textfile.txt

        // first parameter is d files second parameter is zip file name
        ZipUtils zipManager = new ZipUtils();
        zipManager.zip(s, inputPath + inputFile);
        zipManager.unzip(inputPath + inputFile, outputPath);
    }*/

    public void zip(List<String> fileList, String zipFileName) {
        BufferedInputStream origin = null;
        ZipOutputStream out = null;
        try {
            FileOutputStream dest = new FileOutputStream(zipFileName);
            out = new ZipOutputStream(new BufferedOutputStream(dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < fileList.size(); i++) {
                String filePath = fileList.get(i);
                String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                Log.v("Compress", "Adding: " + fileName);

                FileInputStream fi = new FileInputStream(filePath);
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
            IOUtil.closeQuietly(origin, out);
        }
    }

    public void zip(Map<String, List<String>> filesMap, String zipFileName) {
        BufferedInputStream origin = null;
        ZipOutputStream out = null;
        try {
            FileOutputStream dest = new FileOutputStream(zipFileName);
            out = new ZipOutputStream(new BufferedOutputStream(dest));
            byte data[] = new byte[BUFFER];

            for (Map.Entry<String,List<String>> folderGroup : filesMap.entrySet()){
                String currentFolder = folderGroup.getKey();
                List<String> currentFiles = folderGroup.getValue();

                for (String localFilePath : currentFiles) {
                    String fileName = localFilePath.substring(localFilePath.lastIndexOf("/") + 1);
                    String zipFilePath = currentFolder + "/" + fileName;
                    FriendlyLog.logDebug("ReportSender: Adding " + zipFilePath);

                    FileInputStream fi = new FileInputStream(localFilePath);
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
            IOUtil.closeQuietly(origin, out);
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
