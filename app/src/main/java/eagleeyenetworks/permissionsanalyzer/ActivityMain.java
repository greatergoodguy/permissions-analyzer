package eagleeyenetworks.permissionsanalyzer;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import eagleeyenetworks.permissionsanalyzer.util.CachedFileProvider;
import eagleeyenetworks.permissionsanalyzer.util.UtilLogger;
import eagleeyenetworks.permissionsanalyzer.util.UtilToast;


public class ActivityMain extends ActionBarActivity {

    protected final String TAG = getClass().getSimpleName();

    List<Pair<String, String>> nameAndPermissions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.activity_main);

        nameAndPermissions.add(new Pair<>("Nonexistent Permission", "android.permission.NONEXISTENT_PERMISSION"));
        nameAndPermissions.add(new Pair<>("Access Network State", "android.permission.ACCESS_NETWORK_STATE"));
        nameAndPermissions.add(new Pair<>("Access Wifi State", "android.permission.ACCESS_WIFI_STATE"));
        nameAndPermissions.add(new Pair<>("Access Fine Location", "android.permission.ACCESS_FINE_LOCATION"));
        nameAndPermissions.add(new Pair<>("Authenticate Accounts", "android.permission.AUTHENTICATE_ACCOUNTS"));
        nameAndPermissions.add(new Pair<>("Get Accounts", "android.permission.GET_ACCOUNTS"));
        nameAndPermissions.add(new Pair<>("Internet", "android.permission.INTERNET"));
        nameAndPermissions.add(new Pair<>("Manage Accounts", "android.permission.MANAGE_ACCOUNTS"));
        nameAndPermissions.add(new Pair<>("Use Credentials", "android.permission.USE_CREDENTIALS"));
        nameAndPermissions.add(new Pair<>("Wake Lock", "android.permission.WAKE_LOCK"));
        nameAndPermissions.add(new Pair<>("Write External Storage", "android.permission.WRITE_EXTERNAL_STORAGE"));
        nameAndPermissions.add(new Pair<>("Write Sync Settings", "android.permission.WRITE_SYNC_SETTINGS"));

        nameAndPermissions.add(new Pair<>("c2dm.permission.RECEIVE", "com.google.android.c2dm.permission.RECEIVE"));
        nameAndPermissions.add(new Pair<>("c2dm.intent.RECEIVE", "com.google.android.c2dm.intent.RECEIVE"));
        nameAndPermissions.add(new Pair<>("mobileapp.permission.C2D_MESSAGE", "com.eagleeye.mobileapp.permission.C2D_MESSAGE"));

//        for(Pair<String, String> nameAndPermission : nameAndPermissions) {
//            String name = nameAndPermission.first;
//            String permission = nameAndPermission.second;
//
//            createPermissionTest(name, permission);
//        }

        final Activity activityThis = ActivityMain.this;
        LinearLayout root = (LinearLayout) findViewById(R.id.main_root);

        TextView tvTutorial = new TextView(activityThis);
        tvTutorial.setText("Pressing Analyze will check the permissions on this device, and then send an email with results to Eagle Eye's Android Engineer.");
        tvTutorial.setTextColor(Color.BLACK);
        root.addView(tvTutorial);

        Button button = new Button(activityThis);
        button.setText("Analyze");
        root.addView(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, String>() {

                    private static final String FILE_NAME = "permission_analysis.txt";

                    @Override
                    protected String doInBackground(Void... params) {

                        StringBuilder sbResult = new StringBuilder();

                        for(Pair<String, String> nameAndPermission : nameAndPermissions) {
                            String name = nameAndPermission.first;
                            String permission = nameAndPermission.second;

                            boolean hasPermission = activityThis.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
                            //UtilToast.showToast(activityThis, "hasPermission: " + hasPermission);
                            //UtilLogger.log(TAG, "hasPermission: " + hasPermission + " - " + name);
                            sbResult.append("hasPermission: " + hasPermission + " - " + name + "\n");
                        }

                        String result = sbResult.toString();
                        return result;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        UtilToast.showToast(activityThis, "Finished Analysis");
                        UtilLogger.log(TAG, result);

                        try {
                            createCachedFile(activityThis, FILE_NAME, result);

                            Intent intentEmail = new Intent(Intent.ACTION_SEND);
                            intentEmail.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                            intentEmail.setType("plain/text");

                            String recipient = "tyu@eagleeyenetworks.com";
                            String subject = "Permission Analysis";

                            intentEmail.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{recipient});
                            intentEmail.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
                            //intentEmail.putExtra(android.content.Intent.EXTRA_TEXT, result);
                            intentEmail.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://" + CachedFileProvider.AUTHORITY + "/" + FILE_NAME));
                            startActivity(intentEmail);
                        }
                        catch (ActivityNotFoundException e) {
                            UtilToast.showToast(activityThis, "Gmail is not available on this device");
                        }
                    }

                    public void createCachedFile(Context context, String fileName, String content) {

                        try {
                            File cacheFile = new File(context.getCacheDir() + File.separator + fileName);
                            cacheFile.createNewFile();

                            FileOutputStream fos    = new FileOutputStream(cacheFile);
                            OutputStreamWriter osw  = new OutputStreamWriter(fos, "UTF8");
                            PrintWriter pw          = new PrintWriter(osw);

                            pw.println(content);

                            pw.flush();
                            pw.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    public void createLocalFile(String text) {
                        try {
                            FileOutputStream fos = activityThis.openFileOutput("permission_analysis.txt", Context.MODE_APPEND | Context.MODE_PRIVATE);
                            fos.write(text.getBytes());
                            fos.close();}
                        catch (FileNotFoundException exception) {
                            exception.printStackTrace();}
                        catch (IOException exception) {
                            exception.printStackTrace();}
                    }
                }.execute();
            }
        });
    }

    void createPermissionTest(final String buttonText, final String permission) {
        final Activity activityThis = ActivityMain.this;
        LinearLayout root = (LinearLayout) findViewById(R.id.main_root);

        Button button = new Button(activityThis);
        button.setText(buttonText);
        root.addView(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasPermission = activityThis.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
                //UtilToast.showToast(activityThis, "hasPermission: " + hasPermission);
                UtilLogger.log(TAG, "hasPermission: " + hasPermission + " - " + buttonText);
            }
        });
    }
}
