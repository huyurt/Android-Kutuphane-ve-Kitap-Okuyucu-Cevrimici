package kutuphane.com.kutuphane;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.*;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.util.List;

import static kutuphane.com.kutuphane.MainActivity.seciliFragment;
import static layout.Favoriler.recyclerViewAdapterFavori;
import static layout.Kitaplar.recyclerViewAdapterKitap;
import static layout.Kutuphane.recyclerViewAdapterKutuphane;

public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Filez filez;
    private Filez2 filez2 = new Filez2();
    private Encryption encryption = new Encryption();
    public RelativeLayout relativeLayout;
    public TextView kitapIsim;
    public TextView kitapYazar;
    public TextView kitapKategori;
    public TextView kitapBoyut;
    public ImageView kitapKapak;
    public ImageButton imageButton;
    public TextView kitapIsimArka;
    public TextView kitapYazarArka;
    public ProgressDialog progressDialogMain;
    public ProgressDialog progressDialog;

    public RecyclerViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        filez = new Filez(itemView.getContext());
        relativeLayout = itemView.findViewById(R.id.cardView);
        kitapIsim = itemView.findViewById(R.id.kitapIsim);
        kitapYazar = itemView.findViewById(R.id.kitapYazar);
        kitapKategori = itemView.findViewById(R.id.kitapKategori);
        kitapKapak = itemView.findViewById(R.id.kitapKapak);
        kitapBoyut = itemView.findViewById(R.id.kitapBoyut);
        imageButton = itemView.findViewById(R.id.imageButton);
        kitapIsimArka = itemView.findViewById(R.id.kitapIsimArka);
        kitapYazarArka = itemView.findViewById(R.id.kitapYazarArka);
        progressDialogMain = new ProgressDialog(itemView.getContext(), R.style.ProgressDialog);
        progressDialog = new ProgressDialog(itemView.getContext(), R.style.ProgressDialog);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    @Override
    public void onClick(View view) {
        if (seciliFragment == 1) {
            onClickOkuma(view, recyclerViewAdapterKitap.getItemList());
        } else if (seciliFragment == 2) {
            onClickOkuma(view, recyclerViewAdapterFavori.getItemList());
        } else if (seciliFragment == 3) {
            onClickBilgiler(view, recyclerViewAdapterKutuphane.getItemList());
        }
    }

    public void onClickBilgiler(View view, List<ItemObject> liste) {
        Intent intent = new Intent(view.getContext(), DetailActivity.class);
        intent.putExtra("Kitap Position", String.valueOf(getPosition()));
        intent.putExtra("Kitap Id", liste.get(getAdapterPosition()).getKitapId());
        intent.putExtra("Kitap İsmi", liste.get(getAdapterPosition()).getKitapIsmi());
        intent.putExtra("Kitap Yazarı", liste.get(getAdapterPosition()).getKitapYazari());
        intent.putExtra("Kitap Kategorisi", liste.get(getAdapterPosition()).getKitapKategorisi());
        intent.putExtra("Kitap Boyutu", liste.get(getAdapterPosition()).getKitapBoyut());
        intent.putExtra("Kitap Özeti", liste.get(getAdapterPosition()).getKitapOzeti());
        if (seciliFragment == 1 || seciliFragment == 2) {
            intent.putExtra("Kitap Kapağı Base64", filez2.getKitapKapakByte(filez2.decodeBase64(liste.get(getAdapterPosition()).getKitapKapak())));
        } else {
            try {
                intent.putExtra("Kitap Kapağı Base64", filez2.getKitapKapakByte(((BitmapDrawable) kitapKapak.getDrawable()).getBitmap()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        view.getContext().startActivity(intent);
    }

    public void onClickOkuma(View view, List<ItemObject> liste) {
        String kitapMd5 = encryption.md5(liste.get(getAdapterPosition()).getKitapId() + liste.get(getAdapterPosition()).getKitapIsmi());
        Uri uri1 = FileProvider.getUriForFile(view.getContext(), BuildConfig.APPLICATION_ID + ".provider", new File(Environment.getExternalStoragePublicDirectory("Android/data/" + filez.getDosyaYolu()), kitapMd5 + ".epub"));
        Uri uri2 = FileProvider.getUriForFile(view.getContext(), BuildConfig.APPLICATION_ID + ".provider", new File(Environment.getExternalStoragePublicDirectory("Android/data/" + filez.getDosyaYolu()), kitapMd5 + ".pdf"));
        File file1 = new File(Environment.getExternalStorageDirectory(), "Android/data/" + filez.getDosyaYolu() + "/" + kitapMd5 + ".epub");
        File file2 = new File(Environment.getExternalStorageDirectory(), "Android/data/" + filez.getDosyaYolu() + "/" + kitapMd5 + ".pdf");
        FTPClient ftpClient = new FTPClient();
        filez.kitapIndirBaslat(progressDialogMain, liste.get(getAdapterPosition()).getKitapBoyut(), ftpClient, progressDialog, liste.get(getAdapterPosition()).getKitapId(), liste.get(getAdapterPosition()).getKitapIsmi(), file1, file2, uri1, uri2);
    }

    public void onClickGozat(View view, List<ItemObject> liste){
        Intent intent = new Intent(view.getContext(), ReadActivity.class);
        intent.putExtra("Kitap Id", liste.get(getAdapterPosition()).getKitapId());
        intent.putExtra("Kitap İsmi", liste.get(getAdapterPosition()).getKitapIsmi());
        view.getContext().startActivity(intent);
    }
}