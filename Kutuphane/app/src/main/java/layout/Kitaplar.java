package layout;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import kutuphane.com.kutuphane.*;

import java.util.ArrayList;
import java.util.List;

import static kutuphane.com.kutuphane.MainActivity.gridColumnSayisi;

public class Kitaplar extends Fragment {
    private OnFragmentInteractionListener mListener;
    private Encryption encryption = new Encryption();
    private Host host = new Host();
    private Strings strings = new Strings();
    private Setting setting = new Setting();
    private Filez2 filez2 = new Filez2();
    private Activity activity;
    private Context context;
    private RelativeLayout relativeLayoutYukleniyor;
    private RelativeLayout relativeLayoutUyari;
    public static TextView textViewKitaplar;
    private Filez fileKitap;
    private Filez fileFavori;
    private Filez file2;
    private Filez file3;
    private Update update;
    public static GridLayoutManager gridLayoutManagerKitaplar;
    public static RecyclerView recyclerViewKitap;
    public static RecyclerViewAdapter recyclerViewAdapterKitap;
    private List<ItemObject> list;
    public static int gridLayoutManagerPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_kitaplar, container, false);

        baslat(view);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void baslat(View view) {
        activity = getActivity();
        context = getActivity().getApplicationContext();
        relativeLayoutYukleniyor = view.findViewById(R.id.relativeLayoutKitaplar);
        relativeLayoutUyari = view.findViewById(R.id.relativeLayoutKitaplarUyari);
        textViewKitaplar = view.findViewById(R.id.textViewKitapYokKitaplar);
        Button buttonUyari = view.findViewById(R.id.buttonUyariKitaplar);
        buttonUyari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileKitap.versiyonKontrolu(activity, relativeLayoutUyari, update, file3, file2, recyclerViewKitap);
            }
        });
        fileKitap = new Filez(context, encryption.md5(strings.getDosyaAdiKitaplar()), strings.getSharedPreferenceKitaplar(), null);
        fileFavori = new Filez(context, encryption.md5(strings.getDosyaAdiFavoriler()), strings.getSharedPreferenceFavoriler(), null);
        file2 = new Filez(context, encryption.md5(strings.getDosyaAdiZaman()), strings.getSharedPreferenceZaman(), host.getAdresZaman());
        file3 = new Filez(context, encryption.md5(strings.getDosyaAdiVersiyon()), strings.getSharedPreferenceVersiyon(), host.getAdresVersiyon());
        update = new Update(context, getActivity(), file3, strings.getDosyaAdiVersiyonEki());

        gridLayoutManagerKitaplar = new GridLayoutManager(getActivity(), gridColumnSayisi);
        recyclerViewKitap = view.findViewById(R.id.recycler_viewKitaplar);
        recyclerViewKitap.setHasFixedSize(true);
        recyclerViewKitap.setLayoutManager(gridLayoutManagerKitaplar);
        recyclerViewKitap.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                gridLayoutManagerPosition = gridLayoutManagerKitaplar.findFirstVisibleItemPosition();
            }
        });

        if (!fileKitap.dosyaKontrolBaslangic() || !fileFavori.dosyaKontrolBaslangic() || !file2.dosyaKontrolBaslangic()) {
            if(!fileKitap.dosyaKontrolBaslangic()) {
                filez2.dosyaOlustur(fileKitap, filez2.bas);
            }
            if(!fileFavori.dosyaKontrolBaslangic()) {
                filez2.dosyaOlustur(fileFavori, filez2.bas);
            }
            fileKitap.versiyonKontrolu(activity, relativeLayoutUyari, update, file3, file2, recyclerViewKitap);
        } else {
            if (!fileKitap.veriKarsilastir(fileKitap.getSharedPreferenceAdi()) || !fileFavori.veriKarsilastir(fileFavori.getSharedPreferenceAdi()) || !file2.veriKarsilastir(file2.getSharedPreferenceAdi())) {
                if (!fileKitap.veriKarsilastir(fileKitap.getSharedPreferenceAdi())) {
                    filez2.dosyaDegisti(context, fileKitap, fileKitap.getSharedPreferenceAdi());
                }
                if (!fileFavori.veriKarsilastir(fileFavori.getSharedPreferenceAdi())) {
                    filez2.dosyaDegisti(context, fileFavori, fileFavori.getSharedPreferenceAdi());
                }
                fileKitap.versiyonKontrolu(activity, relativeLayoutUyari, update, file3, file2, recyclerViewKitap);
            } else {
                if (!file2.zamanKarsilastir()) {
                    fileKitap.versiyonKontrolu(activity, relativeLayoutUyari, update, file3, file2, recyclerViewKitap);
                } else {
                    recyclerViewKitap.setVisibility(View.VISIBLE);
                }
            }
        }
        list = new ArrayList<>();
        list = fileKitap.recyclerViewYukle();
        recyclerViewAdapterKitap = new RecyclerViewAdapter(context, list);
        recyclerViewKitap.setAdapter(recyclerViewAdapterKitap);
        recyclerViewAdapterKitap.itemListSort(true);
        relativeLayoutYukleniyor.setVisibility(View.GONE);
        if(recyclerViewAdapterKitap.getItemCount() != 0) {
            textViewKitaplar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setting.gridLayoutSetting(context, fileKitap);
        gridLayoutManagerKitaplar = new GridLayoutManager(getActivity(), gridColumnSayisi);
        recyclerViewKitap.setHasFixedSize(true);
        recyclerViewKitap.setLayoutManager(gridLayoutManagerKitaplar);
        recyclerViewKitap.scrollToPosition(gridLayoutManagerPosition);
    }
}