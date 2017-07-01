/*
 *                    GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 ******************************************************************************/

package semiworld.org.logger.utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import com.activeandroid.query.Select;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;

import semiworld.org.logger.models.DownloadModel;
import semiworld.org.logger.models.Version;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created on 30.06.2017.
 */

public class UpdateManager {
    public static DownloadModel checkForUpdate(Context context) {
        final Version version = new Select().from(Version.class).orderBy("id DESC").executeSingle();

        final DownloadModel model = new DownloadModel();
        model.setLatestVersion(version.latest);

        AppUpdaterUtils utils = new AppUpdaterUtils(context)
                .setUpdateFrom(UpdateFrom.GITHUB)
                .setGitHubUserAndRepo("ozcaan11", "Logger")
                .withListener(new AppUpdaterUtils.UpdateListener() {

                    @Override public void onSuccess(Update update, Boolean isUpdateAvailable) {
                        String latestVersion = update.getLatestVersion();
                        if (!String.valueOf(version.latest).equals(latestVersion)) {
                            if (isUpdateAvailable) {
                                String url = String.valueOf(update.getUrlToDownload() + "/download/" + latestVersion + "/app-debug.apk");
                                model.setUrl(url);
                            }
                        }
                    }

                    @Override public void onFailed(AppUpdaterError appUpdaterError) {
                    }
                });

        utils.start();
        return model;
    }

    public static void downloadUpdate(Context context, String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("New version of Logger");
        request.setTitle("Logger");
        // in order for this if to run, you must use the android 3.2 to compile your app
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "app-debug.apk");

        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        final Long enq = manager.enqueue(request);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(enq);
                    DownloadManager manager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                    Cursor c = manager.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                            String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            Intent installationIntent = new Intent(Intent.ACTION_VIEW);
                            installationIntent.setDataAndType(Uri.parse(uriString), "application/vnd.android.package-archive");
                            context.startActivity(installationIntent);
                        }
                    }
                }
            }
        };
        context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
}
