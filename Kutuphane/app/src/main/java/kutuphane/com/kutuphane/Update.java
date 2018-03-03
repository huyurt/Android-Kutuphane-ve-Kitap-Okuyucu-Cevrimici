package kutuphane.com.kutuphane;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Update {
    private Encryption encryption = new Encryption();
    private Strings strings = new Strings();
    private Context context;
    private Activity activity;
    private ProgressDialog progressDialog;
    private ProgressDialog progressDialogSoru;
    private ProgressDialog progressDialogKontrol;
    private Filez file;
    private String ek;

    public Update(Context context, final Activity activity, Filez file, String ek) {
        this.context = context;
        this.activity = activity;
        this.file = file;
        this.ek = ek;

        progressDialog = new ProgressDialog(activity, R.style.ProgressDialog);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        progressDialogSoru = new ProgressDialog(activity, R.style.ProgressDialog);
        progressDialogSoru.setCanceledOnTouchOutside(false);
        progressDialogSoru.setCancelable(false);
        progressDialogSoru.setButton(DialogInterface.BUTTON_NEGATIVE, "Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialogSoru.dismiss();
                activity.finish();
            }
        });
        progressDialogSoru.setButton(DialogInterface.BUTTON_POSITIVE, "Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialogSoru.dismiss();
                guncelle();
            }
        });

        progressDialogKontrol = new ProgressDialog(activity, R.style.ProgressDialog);
        progressDialogKontrol.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialogKontrol.setCanceledOnTouchOutside(false);
        progressDialogKontrol.setCancelable(false);
        progressDialogKontrol.setMessage(strings.getGuncellemeKonrol());
    }

    public void guncelle() {
        new AsyncTask<String, Integer, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.setMax(Integer.parseInt(file.versiyonKarsilastir(file.getSharedPreferenceAdi())[1]));
                progressDialog.setProgress(0);
                progressDialog.setMessage(context.getResources().getString(R.string.fragment_kutuphane1));
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... sUrl) {
                InputStream input = null;
                OutputStream output = null;
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(file.getAdres0() + file.versiyonKarsilastir(file.getSharedPreferenceAdi())[0] + ek);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return null;
                    }
                    int fileLength = connection.getContentLength();
                    input = connection.getInputStream();
                    output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + file.getDosyaYolu() + "/" + file.versiyonKarsilastir(file.getSharedPreferenceAdi())[0] + ".apk");
                    byte data[] = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        if (isCancelled()) {
                            input.close();
                            return null;
                        }
                        total += count;
                        progressDialog.setProgress((int) total);
                        if (fileLength > 0)
                            publishProgress((int) (total * 100 / fileLength));
                        output.write(data, 0, count);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (output != null)
                            output.close();
                        if (input != null)
                            input.close();
                    } catch (IOException ignored) {
                    }

                    if (connection != null)
                        connection.disconnect();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                progressDialog.dismiss();
                guncelleSil();
                if (result != null) {
                    Toast.makeText(context, strings.getGuncellemeHata() + " " + strings.getInternetBaglantisiYok(), Toast.LENGTH_LONG).show();
                } else {
                    if (Build.VERSION.SDK_INT >= 24) {
                        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(Environment.getExternalStoragePublicDirectory("Android/data/" + file.getDosyaYolu()), file.versiyonKarsilastir(file.getSharedPreferenceAdi())[0] + ".apk"));
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                        intent.setDataAndType(uri, "application/vnd.android" + ".package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        context.startActivity(intent);
                        activity.finish();
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "Android/data/" + file.getDosyaYolu() + "/" + file.versiyonKarsilastir(file.getSharedPreferenceAdi())[0] + ".apk")), "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        activity.finish();
                    }
                }
            }
        }.execute();
    }

    public void dosyaOlusturVersiyonMain(final Filez file, final Filez file2) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                progressDialogKontrol.show();
            }

            @Override
            protected String doInBackground(String... params) {
                return file.request(params[0]);
            }

            @Override
            protected void onPostExecute(String sonuc) {
                progressDialogKontrol.dismiss();
                if (!sonuc.equals(strings.getInternetBaglantisiYok())) {
                    file.veriKaydet(file.getSharedPreferenceAdi(), sonuc);
                    if (!file.versiyonKarsilastir(file.getSharedPreferenceAdi())[1].equals("0")) {
                        file2.dosyaSil(Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + file2.getDosyaYolu() + "/" + encryption.md5(strings.getDosyaAdiZaman()));
                        progressDialogSoru.setMessage("Yeni versiyon indirilsin mi? (" + String.format("%.2f", Integer.parseInt(file.versiyonKarsilastir(file.getSharedPreferenceAdi())[1]) / 1048576.0) + " MB)");
                        progressDialogSoru.show();
                    } else {
                        file.dosyaSil(Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + file.getDosyaYolu() + "/" + file.versiyonKarsilastir(file.getSharedPreferenceAdi())[0] + ".apk");
                        file2.dosyaOlustur(file2, file2.getAdres1());
                        Toast.makeText(context, "Uygulama güncel.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, sonuc, Toast.LENGTH_LONG).show();
                }
            }
        }.execute(file.getAdres1());
    }

    public void guncelleBaslat() {
        progressDialogSoru.setMessage("Yeni versiyon indirilsin mi? (" + String.format("%.2f", Integer.parseInt(file.versiyonKarsilastir(file.getSharedPreferenceAdi())[1]) / 1048576.0) + " MB)");
        progressDialogSoru.show();
    }

    private void guncelleSil() {
        file.veriSil(strings.getSharedPreferenceKutuphaneListesi());
        file.veriSil(strings.getSharedPreferenceListViewKategori());
        file.veriSil(strings.getSharedPreferenceListViewYazar());
    }
}