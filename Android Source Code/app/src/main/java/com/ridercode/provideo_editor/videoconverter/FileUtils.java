package com.ridercode.provideo_editor.videoconverter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.ridercode.provideo_editor.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

public class FileUtils {
    public static String getExtension(String str) {
        if (str == null) {
            return null;
        }
        int lastIndexOf = str.lastIndexOf(".");
        return lastIndexOf >= 0 ? str.substring(lastIndexOf).toLowerCase() : "";
    }

    public static String getTargetFileName(Context context, String str) {
        final String name = new File(str).getAbsoluteFile().getName();
        StringBuilder sb = new StringBuilder();
        sb.append(context.getExternalMediaDirs()[0].toString());
        sb.append("/");
        sb.append(context.getResources().getString(R.string.MainFolderName));
        sb.append("/");
        sb.append(context.getResources().getString(R.string.VideoConverter));
        File opFile = new File(sb.toString());
        Log.d("absPath=null", String.valueOf(opFile.mkdirs()));
        String[] files = opFile.getAbsoluteFile().list(
                new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {


                        if (name != null) {
                            StringBuilder sb1 = new StringBuilder();
                            sb1.append(VideoConverteractivity.outputformat);
                            sb1.append("-");
                            if (name.startsWith(sb1.toString())) {
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append(name.substring(0, name.lastIndexOf(".")));
                                sb2.append(".");
                                sb2.append(VideoConverteractivity.outputformat);
                                if (name.endsWith(sb2.toString())) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                }
        );

        Log.d("files=null", String.valueOf(files==null));
        List asList = Arrays.asList(files);
        int i = 0;
        while (true) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(VideoConverteractivity.outputformat);
            sb2.append("-");
            int i2 = i + 1;
            sb2.append(String.format("%03d", new Object[]{Integer.valueOf(i)}));
            sb2.append("-");
            sb2.append(str.substring(str.lastIndexOf("/") + 1, str.lastIndexOf(".")));
            sb2.append(".");
            sb2.append(VideoConverteractivity.outputformat);
            String sb3 = sb2.toString();
            if (!asList.contains(sb3)) {
                StringBuilder sb4 = new StringBuilder();
                sb4.append(context.getExternalMediaDirs()[0].toString());
                sb4.append("/");
                sb4.append(context.getResources().getString(R.string.MainFolderName));
                sb4.append("/");
                sb4.append(context.getResources().getString(R.string.VideoConverter));
                return new File(sb4.toString(), sb3).getPath();
            }
            i = i2;
        }
    }

    public static Uri getUri(File file) {
        if (file != null) {
            return Uri.fromFile(file);
        }
        return null;
    }
}
