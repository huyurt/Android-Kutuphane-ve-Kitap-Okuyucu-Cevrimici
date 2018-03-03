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

public class Favoriler extends Fragment {
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
    public static TextView textViewFavoriler;
    private Filez fileFavori;
    private Filez file2;
    private Filez file3;
    private Update update;
    public static GridLayoutManager gridLayoutManagerFavoriler;
    public static RecyclerView recyclerViewFavori;
    public static RecyclerViewAdapter recyclerViewAdapterFavori;
    private List<ItemObject> list;
    public static int gridLayoutManagerPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_favoriler, container, false);

        baslat(view);

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        relativeLayoutYukleniyor = view.findViewById(R.id.relativeLayoutFavoriler);
        relativeLayoutUyari = view.findViewById(R.id.relativeLayoutFavorilerUyari);
        textViewFavoriler = view.findViewById(R.id.textViewKitapYokFavoriler);
        Button buttonUyari = view.findViewById(R.id.buttonUyariFavoriler);
        buttonUyari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileFavori.versiyonKontrolu(activity, relativeLayoutUyari, update, file3, file2, recyclerViewFavori);
            }
        });
        fileFavori = new Filez(context, encryption.md5(strings.getDosyaAdiFavoriler()), strings.getSharedPreferenceFavoriler(), null);
        file2 = new Filez(context, encryption.md5(strings.getDosyaAdiZaman()), strings.getSharedPreferenceZaman(), host.getAdresZaman());
        file3 = new Filez(context, encryption.md5(strings.getDosyaAdiVersiyon()), strings.getSharedPreferenceVersiyon(), host.getAdresVersiyon());
        update = new Update(context, getActivity(), file3, strings.getDosyaAdiVersiyonEki());

        gridLayoutManagerFavoriler = new GridLayoutManager(getActivity(), gridColumnSayisi);
        recyclerViewFavori = view.findViewById(R.id.recycler_viewFavoriler);
        recyclerViewFavori.setHasFixedSize(true);
        recyclerViewFavori.setLayoutManager(gridLayoutManagerFavoriler);
        recyclerViewFavori.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                gridLayoutManagerPosition = gridLayoutManagerFavoriler.findFirstVisibleItemPosition();
            }
        });

        if (!fileFavori.dosyaKontrolBaslangic() || !file2.dosyaKontrolBaslangic()) {
            filez2.dosyaOlustur(fileFavori, filez2.bas);
            fileFavori.versiyonKontrolu(activity, relativeLayoutUyari, update, file3, file2, recyclerViewFavori);
        } else {
            if (!fileFavori.veriKarsilastir(fileFavori.getSharedPreferenceAdi()) || !file2.veriKarsilastir(file2.getSharedPreferenceAdi())) {
                filez2.dosyaDegisti(context, fileFavori, fileFavori.getSharedPreferenceAdi());
                fileFavori.versiyonKontrolu(activity, relativeLayoutUyari, update, file3, file2, recyclerViewFavori);
            } else {
                if (!file2.zamanKarsilastir()) {
                    fileFavori.versiyonKontrolu(activity, relativeLayoutUyari, update, file3, file2, recyclerViewFavori);
                } else {
                    recyclerViewFavori.setVisibility(View.VISIBLE);
                }
            }
        }
        list = new ArrayList<>();
        list = fileFavori.recyclerViewYukle();
        recyclerViewAdapterFavori = new RecyclerViewAdapter(context, list);
        recyclerViewFavori.setAdapter(recyclerViewAdapterFavori);
        recyclerViewAdapterFavori.itemListSort(true);
        relativeLayoutYukleniyor.setVisibility(View.GONE);
        if(recyclerViewAdapterFavori.getItemCount() != 0) {
            textViewFavoriler.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setting.gridLayoutSetting(context, fileFavori);
        gridLayoutManagerFavoriler = new GridLayoutManager(getActivity(), gridColumnSayisi);
        recyclerViewFavori.setHasFixedSize(true);
        recyclerViewFavori.setLayoutManager(gridLayoutManagerFavoriler);
        recyclerViewFavori.scrollToPosition(gridLayoutManagerPosition);
    }
}