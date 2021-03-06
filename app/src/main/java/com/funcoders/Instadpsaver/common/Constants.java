package com.funcoders.Instadpsaver.common;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;


import androidx.core.app.ActivityCompat;

import com.funcoders.Instadpsaver.R;
import com.funcoders.Instadpsaver.bean.ContactModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.funcoders.Instadpsaver.MainActivity.TAG;

public class Constants {


    public static final String PREFS_NAME = "instaData_pref";
    public static final String isfromRegister = "isfromRegister";
    public static final String isContactsPosted = "isContactsPosted";
    public static final String userName = "userEmail";
    public static final String userPassword = "userPassword";
    public static final int PERMISSION_INITIAL_REQUEST = 919;
    public static final String[] MONTHS = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public static final String[] MONTHS_NUMBER = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    public static final String EXTRA_COMEFROM = "CommingFrom";
    public static final String EXTRA_HISTORY_InstaID = "InstaID";


    public static void displayLongToast(Context mContext, String message) {
        try {
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ProgressDialog showProgressDialog(Context mContext, String message) {
        ProgressDialog pdLoadDialog = null;
        try {
            pdLoadDialog = ProgressDialog.show(mContext, null, null, true);
            pdLoadDialog.setContentView(R.layout.elemento_progress_splash);
            pdLoadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            pdLoadDialog.show();
            pdLoadDialog.setCancelable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pdLoadDialog;
    }

    public static boolean isNetworkAvailable(Context context) {
        @SuppressLint("WrongConstant") ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static String getCurrentDate() {
        String currentDateTimeString1 = (String) DateFormat.format("yyyy-MM-dd", new Date());
        return getCurrentDateInHyphenFormatDDMMYYYY(currentDateTimeString1);
    }

    public static String getCurrentDateInHyphenFormatDDMMYYYY(String dateStr) {
        String datefrt = "";
        if (dateStr != null) {
            String date = dateStr.substring(8, 10);
            int mnt = Integer.parseInt(dateStr.substring(5, 7));
            String yr = dateStr.substring(0, 4);
            datefrt = date + "-" + MONTHS_NUMBER[mnt - 1] + "-" + yr;
        }

        return datefrt;
    }


    public static void alertDilog(Context mcontext, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext, R.style.UtilsDialogThemes);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //  Toast.makeText(CreateVisit.this, "No button Clicked!", Toast.LENGTH_LONG).show();
                dialog.cancel();
            }
        });
        AlertDialog diag = builder.create();
        diag.show();


    }

    /*dialog box with positive, negative button*/
    public static void dialogBoxWithButton(Context context, String title, String message, String positiveButton, String negativeButton, final DialogCallBack dialogCallBack) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.UtilsDialogTheme);
            builder.setCancelable(false);
            if (!title.equalsIgnoreCase("")) {
                builder.setTitle(title);
            }
            builder.setMessage(message).setCancelable(false).setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    if (dialogCallBack != null)
                        dialogCallBack.clickedStatus(true);
                }
            });
            if (!negativeButton.equalsIgnoreCase("")) {
                builder.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (dialogCallBack != null)
                            dialogCallBack.clickedStatus(false);
                    }
                });
            }
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static List<ContactModel> getContacts(Context ctx) {
        List<ContactModel> list = new ArrayList<>();
       // int maxcount=100;
        ContentResolver contentResolver = ctx.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(ctx.getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));

                    Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id));
                    Uri pURI = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

                    Bitmap photo = null;
                    if (inputStream != null) {
                        photo = BitmapFactory.decodeStream(inputStream);
                    }
                    while (cursorInfo.moveToNext()) {
                        ContactModel info = new ContactModel();
                        info.id = id;
                        info.name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String mobile=cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        mobile = mobile.replaceAll("\\s", "");
                        mobile = mobile.replaceAll("-", "");
                        info.setMobileNumber(mobile); ;
                        /*info.photo = photo;
                        info.photoURI= pURI;*/
                        list.add(info);

                    }

                    cursorInfo.close();
                }
              /*  maxcount--;
                if(maxcount==0)
                {
                    break;
                }*/
            }
            cursor.close();
        }

        List<ContactModel> noRepeat = new ArrayList<ContactModel>();
        for (ContactModel event : list) {
            boolean isFound = false;
            for (ContactModel e : noRepeat) {
                if (e.getMobileNumber().equals(event.getMobileNumber()) || (e.equals(event))) {
                    isFound = true;
                    break;
                }
            }
            if (!isFound) noRepeat.add(event);
        }

      /*  for(int i=0; i<list.size()-1; i++) {
            ListIterator<?> iter = list.listIterator(i+1);
            while(iter.hasNext()) {
                if(list.get(i).equals(iter.next())) {
                    iter.remove();
                }
            }
        }*/




        return noRepeat;
    }

    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
    };


    public static void requestPermission(Activity activity) {
        int storage = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int contacts = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS);

        if (storage != PackageManager.PERMISSION_GRANTED || contacts != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    Constants.PERMISSIONS_STORAGE,
                    101
            );
        }
    }

    public static void openPlayStore(Activity context, String appPackageName) {
        try {
            try {
                Intent intentNavPrevScreen = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + appPackageName));
                intentNavPrevScreen.setFlags(67108864);
                context.startActivity(intentNavPrevScreen);
                context.finishAffinity();
                System.exit(0);
            } catch (ActivityNotFoundException var4) {
                Intent intentNavPrevScreen = new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
                intentNavPrevScreen.setFlags(67108864);
                context.startActivity(intentNavPrevScreen);
                context.finishAffinity();
                System.exit(0);
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    private void logStatusToStorage(String data,Context mcontext,String username) {

        File path = null, extraLogPath = null;

        System.out.println("logStatusToStorage  "+data);
        try {
            if (ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                path = new File(Environment.getExternalStoragePublicDirectory("")+"/InstaProfileSaver",
                        username+".txt");
                if (!path.exists()) {
                    try {
                        path.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (path.exists()) {

                        BufferedWriter writer = new BufferedWriter(new FileWriter(path.getAbsolutePath(), true));
                        writer.write(data);
                        writer.newLine();
                        writer.close();
                    }
                } catch (Exception e) {
                    //Log.e(TAG, "Log file error", e);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public static void createLogDirectory() {
        String folder_main = "InstaProfileSaver";
        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }
        String path = f.getAbsolutePath();
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
       // Log.d("Files", "Size: "+ files.length);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = dateFormat.parse(Constants.getCurrentDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -6);
        String yesterdayAsString = dateFormat.format(calendar.getTime());

        String dtStart = yesterdayAsString;
        Date dateCOMPARE = null;
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            dateCOMPARE  = format.parse(dtStart);
            System.out.println(dateCOMPARE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(files!=null) {
            for (int i = 0; i < files.length; i++) {
                Log.d("Files", "FileName:" + files[i].getName());
                String dateFIle = files[i].getName();
                dateFIle = dateFIle.replace(".txt", "");
                SimpleDateFormat fileformat = new SimpleDateFormat("dd-MM-yyyy");
                Date pastdate = null;
                try {
                    pastdate = format.parse(dateFIle);
                    System.out.println(pastdate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (pastdate.compareTo(dateCOMPARE) < 0) {
                    files[i].delete();
                }
            }
        }

        String  dateAsFileName = Constants.getCurrentDate();
        File dateFile = new File(Environment.getExternalStoragePublicDirectory("")+"/TrackerLogs",
                dateAsFileName+".txt");
        if (!dateFile.exists()) {
            try {
                dateFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
