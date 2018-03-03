package kutuphane.com.kutuphane;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.uncopt.android.widget.text.justify.JustifiedTextView;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.util.List;

import static kutuphane.com.kutuphane.MainActivity.*;
import static layout.Favoriler.recyclerViewAdapterFavori;
import static layout.Kitaplar.recyclerViewAdapterKitap;
import static layout.Kutuphane.recyclerViewAdapterKutuphane;

public class DetailActivity extends AppCompatActivity {
    private Strings strings = new Strings();
    private Encryption encryption = new Encryption();
    private Filez2 filez2 = new Filez2();
    private Filez filezKitap;
    private Context context;
    private List<ItemObject> list;
    private ImageView imageView1;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private JustifiedTextView textView4;
    private Button button1;
    private Button button2;
    private Button button3;
    private int kitapPosition;
    private String kitapId;
    private String kitapIsmi;
    private String kitapYazari;
    private String kitapKategorisi;
    private String kitapOzeti;
    private String kitapBoyutu;
    private File file1;
    private File file2;
    private Uri uri1;
    private Uri uri2;
    private String kitapMd5;
    private FTPClient ftpClient;
    private byte[] kitapKapak;
    private boolean indirmeBasladi = false;
    private ProgressDialog progressDialogMain;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        context = getApplicationContext();
        if (seciliFragment == 1) {
            list = recyclerViewAdapterKitap.getItemList();
        } else if (seciliFragment == 2) {
            list = recyclerViewAdapterFavori.getItemList();
        } else if (seciliFragment == 3) {
            list = recyclerViewAdapterKutuphane.getItemList();
        }
        filezKitap = new Filez(context, encryption.md5(strings.getDosyaAdiKitaplar()), strings.getSharedPreferenceKitaplar(), null);
        if (filezKitap.veriAl(strings.getSharedPreferenceKitapIndirmeDurumu() + kitapId + kitapIsmi).equals("")) {
            filezKitap.veriKaydet(strings.getSharedPreferenceKitapIndirmeDurumu() + kitapId + kitapIsmi, "0");
        }

        Intent intent = getIntent();
        kitapPosition = Integer.parseInt(intent.getStringExtra("Kitap Position"));
        kitapId = intent.getStringExtra("Kitap Id");
        kitapIsmi = intent.getStringExtra("Kitap İsmi");
        kitapYazari = intent.getStringExtra("Kitap Yazarı");
        kitapKategorisi = intent.getStringExtra("Kitap Kategorisi");
        kitapOzeti = intent.getStringExtra("Kitap Özeti");
        kitapKapak = intent.getByteArrayExtra("Kitap Kapağı Base64");
        kitapBoyutu = intent.getStringExtra("Kitap Boyutu");

        getSupportActionBar().show();
        getSupportActionBar().setTitle(kitapIsmi);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        kitapMd5 = encryption.md5(kitapId + kitapIsmi);
        uri1 = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(Environment.getExternalStoragePublicDirectory("Android/data/" + filezKitap.getDosyaYolu()), kitapMd5 + ".epub"));
        uri2 = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(Environment.getExternalStoragePublicDirectory("Android/data/" + filezKitap.getDosyaYolu()), kitapMd5 + ".pdf"));
        file1 = new File(Environment.getExternalStorageDirectory(), "Android/data/" + filezKitap.getDosyaYolu() + "/" + kitapMd5 + ".epub");
        file2 = new File(Environment.getExternalStorageDirectory(), "Android/data/" + filezKitap.getDosyaYolu() + "/" + kitapMd5 + ".pdf");

        imageView1 = findViewById(R.id.imageViewDetailKapak);
        textView1 = findViewById(R.id.textViewDetailAd);
        textView2 = findViewById(R.id.textViewDetailYazar);
        textView3 = findViewById(R.id.textViewDetailKategori);
        textView4 = findViewById(R.id.textViewDetailOzet);
        button1 = findViewById(R.id.buttonDetail1);
        button2 = findViewById(R.id.buttonDetail2);
        button3 = findViewById(R.id.buttonDetail3);

        progressDialogMain = new ProgressDialog(this, R.style.ProgressDialog);
        progressDialog = new ProgressDialog(this, R.style.ProgressDialog);
        progressDialog.setMax(Integer.parseInt(kitapBoyutu));
        progressDialog.setMessage(kitapIsmi + " indiriliyor.");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        try {
            imageView1.setImageBitmap(filez2.decodeBase64(Base64.encodeToString(kitapKapak, Base64.DEFAULT)));
        } catch (NullPointerException e) {

        }
        textView1.setText(kitapIsmi);
        textView2.setText(kitapYazari);
        textView3.setText(filezKitap.kategori(kitapKategorisi));
        textView4.setText(kitapOzeti);
        if (kitapYazari.equals("")) {
            textView2.setVisibility(View.GONE);
        }

        button1.setText(getString(R.string.activity_detail_button1));
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!file1.exists() && !file2.exists()) {
                    if (!indirmeBasladi) {
                        ftpClient = new FTPClient();
                        kitapIndirBaslat(progressDialogMain, kitapBoyutu, ftpClient, progressDialog, kitapId, kitapIsmi, file1, file2, uri1, uri2);
                    }
                } else {
                    startActivity(filezKitap.kitapOku(file1, file2, uri1, uri2));
                }
            }
        });

        button2.setText(buttonTuru(filezKitap, kitapId, kitapIsmi, getString(R.string.activity_detail_button3), getString(R.string.activity_detail_button4)));
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (button2.getText().equals(getString(R.string.activity_detail_button3))) {
                    try {
                        filez2.kitapEkle(filezKitap, list, kitapPosition, ((BitmapDrawable) imageView1.getDrawable()).getBitmap());
                        button2.setText(R.string.activity_detail_button4);
                    } catch (NullPointerException e) {
                        Toast.makeText(context, strings.getInternetBaglantisiYok(), Toast.LENGTH_SHORT).show();
                    }
                    if (seciliFragment == 1) {
                        recyclerViewAdapterKitap.setItemList(filezKitap.recyclerViewYukle());
                    }
                } else {
                    try {
                        filez2.kitapKaldir(filezKitap, list, kitapPosition, ((BitmapDrawable) imageView1.getDrawable()).getBitmap());
                        button2.setText(R.string.activity_detail_button3);
                    } catch (NullPointerException e) {
                        Toast.makeText(context, strings.getInternetBaglantisiYok(), Toast.LENGTH_SHORT).show();
                    }
                    if (seciliFragment == 1) {
                        recyclerViewAdapterKitap.setItemList(filezKitap.recyclerViewYukle());
                    }
                }
            }
        });

        button3.setText(getString(R.string.activity_detail_button2));
        if (file1.exists() || file2.exists()) {
            button3.setVisibility(View.VISIBLE);
        }
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, kitapIsmi + " silindi.", Toast.LENGTH_LONG).show();
                button3.setVisibility(View.GONE);
                file1.delete();
                file2.delete();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    private String buttonTuru(Filez file, String kitapIsmi, String kitapId, String buttonText1, String buttonText2) {
        String tur = buttonText2;
        if (seciliFragment != 1) {
            if (!filez2.kitapIsmiEslestirme(file, kitapId, kitapIsmi)) {
                tur = buttonText1;
            }
        }
        return tur;
    }

    private void kitapIndir(final FTPClient ftpClient, final ProgressDialog progressDialog, final String kitapId, final String kitapIsmi, final File file1, final File file2, final Uri uri1, final Uri uri2) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                progressDialog.setMessage(kitapIsmi + " indiriliyor.");
                progressDialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                return filezKitap.kitapIndirFTP(ftpClient, progressDialog, kitapId, kitapIsmi);
            }

            @Override
            protected void onPostExecute(String sonuc) {
                if (sonuc.equals("")) {
                    button3.setVisibility(View.VISIBLE);
                    filezKitap.veriKaydet(strings.getSharedPreferenceKitapIndirmeDurumu() + kitapId + kitapIsmi, "1");
                    startActivity(filezKitap.kitapOku(file1, file2, uri1, uri2));
                } else if (sonuc.equals(strings.getInternetBaglantisiYok() + "1")) {
                    Toast.makeText(context, strings.getInternetBaglantisiYok2(), Toast.LENGTH_LONG).show();
                } else if (sonuc.equals(strings.getInternetBaglantisiYok() + "2")) {
                    Toast.makeText(context, "Kitap silinmiş.", Toast.LENGTH_LONG).show();
                }
                indirmeBasladi = false;
                progressDialog.setProgress(0);
                progressDialog.dismiss();
            }
        }.execute();
    }

    private void kitapIndirBaslat(final ProgressDialog progressDialogMain, String kitapBoyut, final FTPClient ftpClient, final ProgressDialog progressDialog, final String kitapId, final String kitapIsmi, final File file1, final File file2, final Uri uri1, final Uri uri2) {
        progressDialogMain.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialogMain.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.ic_file_download));
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
                indirmeBasladi = true;
                kitapIndir(ftpClient, progressDialog, kitapId, kitapIsmi, file1, file2, uri1, uri2);
            }
        });
        progressDialogMain.show();
    }
}