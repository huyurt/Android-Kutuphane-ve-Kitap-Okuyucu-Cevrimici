package kutuphane.com.kutuphane;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import okhttp3.*;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Filez {
    private Encryption encryption = new Encryption();
    private static Strings strings = new Strings();
    private static Host host = new Host();
    private List<ItemObject> list;
    private Context context;
    private String dosyaAdi;
    private String dosyaYolu = strings.getDosyaYolu();
    private String sharedPreferenceAdi;
    private static String adres0 = host.getAdres();
    private String adres1;

    public Filez(Context context) {
        this.context = context;
    }

    public Filez(Context context, String dosyaAdi, String sharedPreferenceAdi, String adres1) {
        this.context = context;
        this.dosyaAdi = dosyaAdi;
        this.sharedPreferenceAdi = sharedPreferenceAdi;
        this.adres1 = adres1;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getDosyaAdi() {
        return dosyaAdi;
    }

    public void setDosyaAdi(String dosyaAdi) {
        this.dosyaAdi = dosyaAdi;
    }

    public String getDosyaYolu() {
        return dosyaYolu;
    }

    public String getSharedPreferenceAdi() {
        return sharedPreferenceAdi;
    }

    public void setSharedPreferenceAdi(String sharedPreferenceAdi) {
        this.sharedPreferenceAdi = sharedPreferenceAdi;
    }

    public String getAdres0() {
        return adres0;
    }

    public String getAdres1() {
        return adres1;
    }

    public void setAdres1(String adres1) {
        this.adres1 = adres1;
    }

    public void kokOlustur(File root) {
        root.mkdirs();
    }

    public void dosyaOlustur(final Filez file, String adres) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                return request(params[0]);
            }

            @Override
            protected void onPostExecute(String sonuc) {
                try {
                    File root = new File(Environment.getExternalStorageDirectory(), "Android/data/" + file.getDosyaYolu());
                    File f = new File(root, file.getDosyaAdi());
                    FileOutputStream fileStream = new FileOutputStream(f);
                    OutputStreamWriter writer = new OutputStreamWriter(fileStream, "UTF-8");
                    writer.append(sonuc);
                    writer.flush();
                    writer.close();
                    file.veriKaydet(file.getSharedPreferenceAdi(), sonuc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.execute(adres);
    }

    public static String request(String adres) {
        String veri = null;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(adres0 + adres).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            veri = response.body().string();
        } catch (IOException e) {
            return strings.getInternetBaglantisiYok();
        }
        return veri;
    }

    public String dosyaIcerik() {
        File root = new File(Environment.getExternalStorageDirectory(), "Android/data/" + getDosyaYolu());
        File file = new File(root, getDosyaAdi());
        String text = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                text = line;
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    public void veriKaydet(String sharedPreferenceAdi, String sonuc) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(sharedPreferenceAdi, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(sharedPreferenceAdi, sonuc);
        editor.apply();
    }

    public boolean dosyaKontrolBaslangic() {
        File root = new File(Environment.getExternalStorageDirectory(), "Android/data/" + getDosyaYolu());
        File file = new File(root, getDosyaAdi());
        if (!root.exists()) {
            kokOlustur(root);
        } else {
            if (!file.exists()) {
                return false;
            }
        }
        return true;
    }

    public boolean veriKarsilastir(String sharedPreferenceAdi) {
        String icerik = veriAl(sharedPreferenceAdi);
        String dosyaIcerik = dosyaIcerik();
        if (icerik != null) {
            if (icerik.equals(dosyaIcerik.toString())) {
                return true;
            }
        }
        return false;
    }

    public String veriAl(String sharedPreferenceAdi) {
        return getContext().getSharedPreferences(sharedPreferenceAdi, Context.MODE_PRIVATE).getString(sharedPreferenceAdi, "");
    }

    public void veriSil(String sharedPreferenceAdi) {
        getContext().getSharedPreferences(sharedPreferenceAdi, Context.MODE_PRIVATE).edit().clear().apply();
    }

    public boolean zamanKarsilastir() {
        Long zaman1 = System.currentTimeMillis() / 1000;
        Long zaman2 = zaman1;
        String icerik = dosyaIcerik().toString();
        try {
            JSONArray jsonArray = new JSONArray(icerik);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                zaman2 = jsonObject.getLong("time");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (zaman1 - zaman2 < 1295000 && zaman1 - zaman2 > -1000) {
            return true;
        }
        return false;
    }

    public String[] versiyonKarsilastir(String sharedPreferenceAdi) {
        String currentVersiyon = BuildConfig.VERSION_NAME;
        String[] veri = {null, "0"};
        String lastVersiyon = null;
        String icerik = veriAl(sharedPreferenceAdi);
        try {
            JSONArray jsonArray = new JSONArray(icerik);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                lastVersiyon = jsonObject.getString(strings.getJsonApkVersiyon());
                veri[0] = jsonObject.getString(strings.getJsonApkAdres());
                veri[1] = jsonObject.getString(strings.getJsonApkBoyut());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!currentVersiyon.equals(lastVersiyon)) {
            return veri;
        }
        veri[1] = "0";
        return veri;
    }

    public void dosyaSil(String yol) {
        File file = new File(yol);
        file.delete();
    }

    public List<ItemObject> recyclerViewYukle() {
        String dosyaIcerik = dosyaIcerik().toString();
        list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(dosyaIcerik);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ItemObject itemObject = new ItemObject(jsonObject.getString(strings.getJsonKitapId()), jsonObject.getString(strings.getJsonKitapZamani()), jsonObject.getString(strings.getJsonKitapIsmi()), jsonObject.getString(strings.getJsonKitapYazari()), jsonObject.getString(strings.getJsonKitapKategorisi()), jsonObject.getString(strings.getJsonKitapOzeti()), jsonObject.getString(strings.getJsonKitapKapak()), jsonObject.getString(strings.getJsonKitapBoyut()));
                list.add(itemObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void versiyonKontrolu(final Activity activity, final RelativeLayout relativeLayoutUyari, final Update update, final Filez file, final Filez file2, final RecyclerView recyclerView) {
        final ProgressDialog progressDialog = new ProgressDialog(activity, R.style.ProgressDialog);
        new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(strings.getGuncellemeKonrol());
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                return request(params[0]);
            }

            @Override
            protected void onPostExecute(String sonuc) {
                progressDialog.dismiss();
                if (!sonuc.equals(strings.getInternetBaglantisiYok())) {
                    file.veriKaydet(file.getSharedPreferenceAdi(), sonuc);
                    relativeLayoutUyari.setVisibility(View.GONE);
                    if (!file.versiyonKarsilastir(file.getSharedPreferenceAdi())[1].equals("0")) {
                        file2.dosyaSil(Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + file2.getDosyaYolu() + "/" + encryption.md5(strings.getDosyaAdiZaman()));
                        update.guncelleBaslat();
                    } else {
                        file.dosyaSil(Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + file.getDosyaYolu() + "/" + file.versiyonKarsilastir(file.getSharedPreferenceAdi())[0] + ".apk");
                        file2.dosyaOlustur(file2, file2.getAdres1());
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    relativeLayoutUyari.setVisibility(View.VISIBLE);
                }
            }
        }.execute(file.getAdres1());
    }

    public String dosyaIcerikAsset(String dosyaAdi) {
        String text = "";
        try {
            InputStream inputStream = getContext().getAssets().open(dosyaAdi);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                text = line;
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    public String kategori(String yazi) {
        StringBuilder s = new StringBuilder();
        String[] dizi = yazi.split("\\|");
        for (int i = 0; i < dizi.length; i++) {
            if (i != dizi.length - 1) {
                s.append(dizi[i]);
                s.append(", ");
            } else {
                s.append(dizi[i]);
            }
        }
        return String.valueOf(s);
    }

    public ArrayAdapter<String> listViewAdapter(String dosyaAdi) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_multiple_choice, dosyaIcerikAsset(dosyaAdi).split("\\|")) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(Color.parseColor("#DDDDDD"));
                return view;
            }
        };
    }

    public String kitapIndirFTP(final FTPClient ftpClient, final ProgressDialog progressDialog, String kitapId, String kitapIsmi) {
        File file = new File(Environment.getExternalStorageDirectory(), "Android/data/" + getDosyaYolu() + "/" + encryption.md5(kitapId + kitapIsmi));
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(strings.getJsonKitapId(), kitapId)
                .addFormDataPart(strings.getJsonKitapIsmi(), kitapIsmi)
                .addFormDataPart("kitapIndirmeDurumu", veriAl(strings.getSharedPreferenceKitapIndirmeDurumu() + kitapId + kitapIsmi)).build();
        Request request = new Request.Builder()
                .url(host.getAdres() + host.getAdresKutuphane())
                .method("POST", RequestBody.create(null, new byte[0]))
                .post(requestBody).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            String sonuc = response.body().string();
            JSONArray jsonArray = new JSONArray(sonuc);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            ftpClient.connect(jsonObject.getString(strings.getJsonKitapS()));
            ftpClient.login(jsonObject.getString(strings.getJsonKitapK1()), jsonObject.getString(strings.getJsonKitapK2()));
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setDataTimeout(10000);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setCopyStreamListener(new CopyStreamAdapter() {
                @Override
                public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
                    progressDialog.setProgress((int) totalBytesTransferred);
                    ftpClient.setDataTimeout(0);
                }
            });
            OutputStream outputStream = null;
            try {
                file = new File(Environment.getExternalStorageDirectory(), "Android/data/" + getDosyaYolu() + "/" + encryption.md5(kitapId + kitapIsmi) + jsonObject.getString(strings.getJsonKitapTur()));
                file.createNewFile();
                outputStream = new BufferedOutputStream(new FileOutputStream(file));
                ftpClient.retrieveFile(jsonObject.getString(strings.getJsonKitapLink()), outputStream);
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (ftpClient.isConnected()) {
                    ftpClient.disconnect();
                }
            }
        } catch (IOException e) {
            if(file.exists()) {
                file.delete();
            }
            return strings.getInternetBaglantisiYok() + "1";
        } catch (JSONException e) {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("kitapGonderen", "System")
                    .addFormDataPart(strings.getJsonKitapId(), kitapId)
                    .addFormDataPart(strings.getJsonKitapIsmi(), kitapIsmi)
                    .addFormDataPart("kitapKonu", kitapId + kitapIsmi + " silinmiş ya da hatalı.").build();
            request = new Request.Builder()
                    .url(host.getAdres() + host.getAdresHata())
                    .method("POST", RequestBody.create(null, new byte[0]))
                    .post(requestBody).build();
            try {
                if(file.exists()) {
                    file.delete();
                }
                okHttpClient.newCall(request).execute();
            } catch (IOException e2) {

            }
            return strings.getInternetBaglantisiYok() + "2";
        }
        return "";
    }

    public Intent kitapOku(File file1, File file2, Uri uri1, Uri uri2) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= 24) {
            if (file1.exists()) {
                intent.setDataAndType(uri1, "application/epub+zip");
            } else {
                intent.setDataAndType(uri2, "application/pdf");
            }
        } else {
            if (file1.exists()) {
                intent.setDataAndType(Uri.fromFile(file1), "application/epub+zip");
            } else {
                intent.setDataAndType(Uri.fromFile(file2), "application/pdf");
            }
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return Intent.createChooser(intent, "Kitap okuyucusunu seçin.");
    }

    public void kitapIndir(final FTPClient ftpClient, final ProgressDialog progressDialog, final String kitapId, final String kitapIsmi, final String kitapBoyut, final File file1, final File file2, final Uri uri1, final Uri uri2) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                progressDialog.setMessage(kitapIsmi + " indiriliyor.");
                progressDialog.setProgress(0);
                progressDialog.setMax(Integer.parseInt(kitapBoyut));
                progressDialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                return kitapIndirFTP(ftpClient, progressDialog, kitapId, kitapIsmi);
            }

            @Override
            protected void onPostExecute(String sonuc) {
                if (sonuc.equals("")) {
                    veriKaydet(strings.getSharedPreferenceKitapIndirmeDurumu() + kitapId + kitapIsmi, "1");
                    getContext().startActivity(kitapOku(file1, file2, uri1, uri2));
                } else if (sonuc.equals(strings.getInternetBaglantisiYok() + "1")) {
                    Toast.makeText(getContext(), strings.getInternetBaglantisiYok2(), Toast.LENGTH_LONG).show();
                } else if (sonuc.equals(strings.getInternetBaglantisiYok() + "2")) {
                    Toast.makeText(getContext(), strings.getFtpKitapSilinmis(), Toast.LENGTH_LONG).show();
                }
                progressDialog.setProgress(0);
                progressDialog.dismiss();
            }
        }.execute();
    }

    public void kitapIndirBaslat(final ProgressDialog progressDialogMain, final String kitapBoyut, final FTPClient ftpClient, final ProgressDialog progressDialog, final String kitapId, final String kitapIsmi, final File file1, final File file2, final Uri uri1, final Uri uri2) {
        if (!file1.exists() && !file2.exists()) {
            progressDialogMain.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialogMain.setIndeterminateDrawable(getContext().getResources().getDrawable(R.drawable.ic_file_download));
            progressDialogMain.setMessage(kitapIsmi + " indirilsin mi? (" + String.format("%.2f", Integer.parseInt(kitapBoyut) / 1048576.0) + " MB)");
            progressDialogMain.setButton(DialogInterface.BUTTON_NEGATIVE, "Hayır", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressDialogMain.dismiss();
                }
            });
            progressDialogMain.setButton(DialogInterface.BUTTON_POSITIVE, "Evet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressDialogMain.dismiss();
                    kitapIndir(ftpClient, progressDialog, kitapId, kitapIsmi, kitapBoyut, file1, file2, uri1, uri2);
                }
            });
            progressDialogMain.show();
        } else {
            getContext().startActivity(kitapOku(file1, file2, uri1, uri2));
        }
    }
}