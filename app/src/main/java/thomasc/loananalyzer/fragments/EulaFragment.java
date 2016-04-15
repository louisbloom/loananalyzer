/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import thomasc.loananalyzer.R;

public class EulaFragment extends DialogFragment implements
        DialogInterface.OnClickListener {

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
        return packageInfo;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
    }

    public static void show(FragmentActivity activity) {
        new EulaFragment().show(activity.getSupportFragmentManager(),
                "eulaDialog");
    }

    private static String readString(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return "";
        }

        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            return "";
        }
        return stringBuilder.toString();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        PackageInfo packageInfo = getPackageInfo(context);

        String eulaText;
        String changeLogText;
        InputStream inputStream;

        AssetManager assetManager = context.getAssets();

        try {
            inputStream = assetManager.open("EULA");
            eulaText = readString(inputStream);
            inputStream.close();
        } catch (IOException e) {
            eulaText = "";
        }

        try {
            inputStream = assetManager.open("ChangeLog.txt");
            changeLogText = readString(inputStream);
            inputStream.close();
        } catch (IOException e) {
            changeLogText = "";
        }

        String title = getString(R.string.app_name);
        if (packageInfo != null) {
            title += " v" + packageInfo.versionName;
        }

        String message;
        if (changeLogText.trim().equals("")) {
            message = eulaText;
        } else {
            message = changeLogText + "\n" + eulaText;
        }

        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, this)
                .create();
    }
}

