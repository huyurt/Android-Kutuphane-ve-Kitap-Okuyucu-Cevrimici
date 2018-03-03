package kutuphane.com.kutuphane;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;

import java.io.*;
import java.util.List;

public class Filez2 {
    public String bas = "[";
    private String kitapId = "{\"kitapId\":\"";
    private String kitapZamani = "\",\"kitapZamani\":\"";
    private String kitapIsmi = "\",\"kitapIsmi\":\"";
    private String kitapYazari = "\",\"kitapYazari\":\"";
    private String kitapKategorisi = "\",\"kitapKategorisi\":\"";
    private String kitapOzeti = "\",\"kitapOzeti\":\"";
    private String kitapKapak = "\",\"kitapKapak\":\"";
    private String kitapBoyut = "\",\"kitapBoyut\":\"";
    private String son1 = "\"},";
    private String son2 = "\"}";
    private String enSon = "\"}]";

    public void dosyaOlustur(Filez file, String icerik) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Android/data/" + file.getDosyaYolu());
            File f = new File(root, file.getDosyaAdi());
            FileOutputStream fileStream = new FileOutputStream(f);
            OutputStreamWriter writer = new OutputStreamWriter(fileStream, "UTF-8");
            writer.append(icerik);
            writer.flush();
            writer.close();
            file.veriKaydet(file.getSharedPreferenceAdi(), icerik);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dosyaDegisti(Context context, Filez file, String sharedPreferenceAdi) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPreferenceAdi, Context.MODE_PRIVATE);
        String icerik = sharedPreferences.getString(sharedPreferenceAdi, "");
        dosyaOlustur(file, icerik);
    }

    public void kitapEkle(Filez file, List<ItemObject> liste, int position, Bitmap kitapKapakBitmap) {
        if (!file.dosyaKontrolBaslangic()) {
            dosyaOlustur(file, bas);
        }
        String ekle;
        if (!String.valueOf(file.dosyaIcerik()).equals("[")) {
            ekle = String.valueOf(file.dosyaIcerik()).substring(0, file.dosyaIcerik().length() - 3);
            ekle += son1 + kitapId + liste.get(position).getKitapId() + kitapZamani + liste.get(position).getKitapZamani() +
                    kitapIsmi + liste.get(position).getKitapIsmi() + kitapYazari + liste.get(position).getKitapYazari() +
                    kitapKategorisi + liste.get(position).getKitapKategorisi() + kitapOzeti + liste.get(position).getKitapOzeti() +
                    kitapKapak + getKitapKapakBase64(kitapKapakBitmap) + kitapBoyut + liste.get(position).getKitapBoyut() + enSon;
            dosyaOlustur(file, duzelt(ekle));
        } else {
            ekle = file.dosyaIcerik() + kitapId + liste.get(position).getKitapId() + kitapZamani + liste.get(position).getKitapZamani() +
                    kitapIsmi + liste.get(position).getKitapIsmi() + kitapYazari + liste.get(position).getKitapYazari() +
                    kitapKategorisi + liste.get(position).getKitapKategorisi() + kitapOzeti + liste.get(position).getKitapOzeti() +
                    kitapKapak + getKitapKapakBase64(kitapKapakBitmap) + kitapBoyut + liste.get(position).getKitapBoyut() + enSon;
            dosyaOlustur(file, duzelt(ekle));
        }
    }

    public void kitapKaldir(Filez file, List<ItemObject> liste, int position, Bitmap kitapKapakBitmap) {
        if (!file.dosyaKontrolBaslangic()) {
            dosyaOlustur(file, bas);
        }
        if (!String.valueOf(file.dosyaIcerik()).equals("[")) {
            String dosyaIcerik = String.valueOf(file.dosyaIcerik());
            String silinecek = duzelt(kitapId + liste.get(position).getKitapId() + kitapZamani + liste.get(position).getKitapZamani() +
                    kitapIsmi + liste.get(position).getKitapIsmi() + kitapYazari + liste.get(position).getKitapYazari() +
                    kitapKategorisi + liste.get(position).getKitapKategorisi() + kitapOzeti + liste.get(position).getKitapOzeti() +
                    kitapKapak + getKitapKapakBase64(kitapKapakBitmap) + kitapBoyut + liste.get(position).getKitapBoyut());
            String silinecek1 = silinecek + son1;
            String silinecek2 = "," + silinecek + son2;
            String silinecek3 = silinecek + son2 + "]";

            try {
                dosyaIcerik = dosyaIcerik.replace(silinecek1, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                dosyaIcerik = dosyaIcerik.replace(silinecek2, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                dosyaIcerik = dosyaIcerik.replace(silinecek3, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            dosyaOlustur(file, dosyaIcerik);
        }
    }

    private String duzelt(String icerik) {
        return icerik.replaceAll("[\\n\\r]+", "\\\\n\\\\n");
    }

    public boolean kitapIsmiEslestirme(Filez file, String kitapId, String kitapIsmi) {
        String dosyaIcerik = String.valueOf(file.dosyaIcerik());
        boolean eslesti = false;
        if (!dosyaIcerik.equals("[")) {
            if (dosyaIcerik.contains(kitapId) && dosyaIcerik.contains(kitapIsmi)) {
                eslesti = true;
            }
        }
        return eslesti;
    }

    private String getKitapKapakBase64(Bitmap kitapKapakBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        kitapKapakBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public byte[] getKitapKapakByte(Bitmap kitapKapakBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        kitapKapakBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}