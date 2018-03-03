package layout;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.*;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;

import android.widget.*;
import kutuphane.com.kutuphane.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static kutuphane.com.kutuphane.MainActivity.gridColumnSayisi;

public class Kutuphane extends Fragment {
    private OnFragmentInteractionListener mListener;
    private Encryption encryption = new Encryption();
    private Strings strings = new Strings();
    private Host host = new Host();
    private Setting setting = new Setting();
    private Activity activity;
    private Context context;
    private Filez fileKutuphane;
    private Filez file2;
    private Filez file3;
    private Update update;
    public static TabHost tabHost;
    private TabWidget tabWidget;
    private RelativeLayout relativeLayoutUyari;
    private RelativeLayout relativeLayoutProgressBarKutuphane;
    public static GridLayoutManager gridLayoutManager;
    public static RecyclerView recyclerViewKutuphane;
    public static RecyclerViewAdapter recyclerViewAdapterKutuphane;
    private List<ItemObject> list;
    public static int gridLayoutManagerPosition;
    private int kitapSayisi;
    private ListView listViewKategori;
    private ListView listViewYazar;
    private ArrayAdapter<String> arrayAdapterKategori;
    private ArrayAdapter<String> arrayAdapterYazar;
    private String[] listViewCheckedItemPositionsKategori;
    private String[] listViewCheckedItemPositionsYazar;
    public static boolean aramaYapilabilir = false;
    public static String queryKategori = "";
    public static String queryYazar = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_kutuphane, container, false);
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
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

    private void baslat(final View view) {
        activity = getActivity();
        context = getActivity().getApplicationContext();
        tabHost = view.findViewById(R.id.tabHostKutuphane);
        setting.setTab(tabHost, R.id.listViewKutuphaneYazar, R.id.listViewKutuphaneKategori, R.id.recycler_viewKutuphane);
        tabWidget = tabHost.getTabWidget();
        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            TextView textView = tabWidget.getChildAt(i).findViewById(android.R.id.title);
            textView.setTextColor(context.getResources().getColor(R.color.mainTextColor));
            textView.setAllCaps(false);
        }
        relativeLayoutUyari = view.findViewById(R.id.relativeLayoutKutuphaneUyari);
        relativeLayoutProgressBarKutuphane = view.findViewById(R.id.relativeLayoutProgressBarKutuphane);
        Button buttonUyari = view.findViewById(R.id.buttonUyariKutuphane);
        buttonUyari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileKutuphane.versiyonKontrolu(activity, relativeLayoutUyari, update, file3, file2, recyclerViewKutuphane);
            }
        });
        fileKutuphane = new Filez(context, encryption.md5(strings.getDosyaAdiKutuphane()), strings.getSharedPreferenceKutuphane(), host.getAdresKutuphane());
        file2 = new Filez(context, encryption.md5(strings.getDosyaAdiZaman()), strings.getSharedPreferenceZaman(), host.getAdresZaman());
        file3 = new Filez(context, encryption.md5(strings.getDosyaAdiVersiyon()), strings.getSharedPreferenceVersiyon(), host.getAdresVersiyon());
        update = new Update(context, getActivity(), file3, strings.getDosyaAdiVersiyonEki());
        arrayAdapterKategori = fileKutuphane.listViewAdapter(strings.getDosyaAdiAssetKategori());
        arrayAdapterYazar = fileKutuphane.listViewAdapter(strings.getDosyaAdiAssetYazar());
        arrayAdapterKategori = fileKutuphane.listViewAdapter(strings.getDosyaAdiAssetKategori());
        arrayAdapterYazar = fileKutuphane.listViewAdapter(strings.getDosyaAdiAssetYazar());

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals("Kitaplar") && aramaYapilabilir) {
                    queryKategori = "";
                    queryYazar = "";
                    if (!fileKutuphane.veriAl(strings.getSharedPreferenceListViewKategori()).equals("")) {
                        listViewCheckedItemPositionsKategori = fileKutuphane.veriAl(strings.getSharedPreferenceListViewKategori()).split(" ");
                        for (String s : listViewCheckedItemPositionsKategori) {
                            queryKategori += listViewKategori.getItemAtPosition(Integer.parseInt(s)) + "|";
                            listViewKategori.setItemChecked(Integer.parseInt(s), true);
                        }
                    }
                    if (!fileKutuphane.veriAl(strings.getSharedPreferenceListViewYazar()).equals("")) {
                        listViewCheckedItemPositionsYazar = fileKutuphane.veriAl(strings.getSharedPreferenceListViewYazar()).split(" ");
                        for (String s : listViewCheckedItemPositionsYazar) {
                            queryYazar += listViewYazar.getItemAtPosition(Integer.parseInt(s)) + "|";
                            listViewYazar.setItemChecked(Integer.parseInt(s), true);
                        }
                    }
                    recyclerViewAdapterKutuphane.getFilterKategori(queryKategori, queryYazar).filter("");
                }
            }
        });

        listViewKategori = view.findViewById(R.id.listViewKutuphaneKategori);
        listViewKategori.setAdapter(arrayAdapterKategori);
        if (!fileKutuphane.veriAl(strings.getSharedPreferenceListViewKategori()).equals("")) {
            listViewCheckedItemPositionsKategori = fileKutuphane.veriAl(strings.getSharedPreferenceListViewKategori()).split(" ");
            for (String s : listViewCheckedItemPositionsKategori) {
                queryKategori += listViewKategori.getItemAtPosition(Integer.parseInt(s)) + "|";
                listViewKategori.setItemChecked(Integer.parseInt(s), true);
            }
        } else {
            listViewKategori.setItemChecked(0, true);
        }
        setting.settingListView(listViewKategori, 0, fileKutuphane);
        listViewKategori.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeRight() {
                tabHost.setCurrentTab(tabHost.getCurrentTab() - 1);
            }

            @Override
            public void onSwipeLeft() {
                tabHost.setCurrentTab(tabHost.getCurrentTab() + 1);
            }
        });

        listViewYazar = view.findViewById(R.id.listViewKutuphaneYazar);
        listViewYazar.setAdapter(arrayAdapterYazar);
        if (!fileKutuphane.veriAl(strings.getSharedPreferenceListViewYazar()).equals("")) {
            listViewCheckedItemPositionsYazar = fileKutuphane.veriAl(strings.getSharedPreferenceListViewYazar()).split(" ");
            for (String s : listViewCheckedItemPositionsYazar) {
                queryYazar += listViewYazar.getItemAtPosition(Integer.parseInt(s)) + "|";
                listViewYazar.setItemChecked(Integer.parseInt(s), true);
            }
        } else {
            listViewYazar.setItemChecked(0, true);
        }
        setting.settingListView(listViewYazar, 1, fileKutuphane);
        listViewYazar.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeLeft() {
                tabHost.setCurrentTab(tabHost.getCurrentTab() + 1);
            }
        });

        gridLayoutManager = new GridLayoutManager(getActivity(), gridColumnSayisi);
        recyclerViewKutuphane = view.findViewById(R.id.recycler_viewKutuphane);
        recyclerViewKutuphane.setHasFixedSize(true);
        recyclerViewKutuphane.setLayoutManager(gridLayoutManager);

        if (!(file2.dosyaKontrolBaslangic())) {
            fileKutuphane.versiyonKontrolu(activity, relativeLayoutUyari, update, file3, file2, recyclerViewKutuphane);
        } else {
            if (!(file2.veriKarsilastir(file2.getSharedPreferenceAdi()))) {
                fileKutuphane.versiyonKontrolu(activity, relativeLayoutUyari, update, file3, file2, recyclerViewKutuphane);
            } else {
                if (!file2.zamanKarsilastir()) {
                    fileKutuphane.versiyonKontrolu(activity, relativeLayoutUyari, update, file3, file2, recyclerViewKutuphane);
                } else {
                    recyclerViewKutuphane.setVisibility(View.VISIBLE);
                }
            }
        }
        list = new ArrayList<>();
        if (!fileKutuphane.veriAl(strings.getSharedPreferenceKutuphaneListesi()).equals("")) {
            kitapSayisi = Integer.parseInt(fileKutuphane.veriAl(strings.getSharedPreferenceKutuphaneListesi()));
            dosyaOlustur1();
        }

        recyclerViewAdapterKutuphane = new RecyclerViewAdapter(context, list);
        recyclerViewKutuphane.setAdapter(recyclerViewAdapterKutuphane);
        recyclerViewKutuphane.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                gridLayoutManagerPosition = gridLayoutManager.findFirstVisibleItemPosition();
            }
        });
        recyclerViewKutuphane.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeRight() {
                tabHost.setCurrentTab(tabHost.getCurrentTab() - 1);
            }
        });
    }

    public void dosyaOlustur1() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (!fileKutuphane.veriAl(strings.getSharedPreferenceKutuphaneListesi()).equals("")) {
                    try {
                        for (int i = 0; i < Integer.parseInt(fileKutuphane.veriAl(strings.getSharedPreferenceKutuphaneListesi())); i++) {
                            JSONObject jsonObject = new JSONObject(fileKutuphane.veriAl(strings.getSharedPreferenceKutuphaneListesi() + String.valueOf(kitapSayisi)));
                            ItemObject itemObject = new ItemObject(jsonObject.getString(strings.getJsonKitapId()), jsonObject.getString(strings.getJsonKitapZamani()), jsonObject.getString(strings.getJsonKitapIsmi()), jsonObject.getString(strings.getJsonKitapYazari()), jsonObject.getString(strings.getJsonKitapKategorisi()), jsonObject.getString(strings.getJsonKitapOzeti()), jsonObject.getString(strings.getJsonKitapKapak()), jsonObject.getString(strings.getJsonKitapBoyut()));
                            list.add(itemObject);
                            --kitapSayisi;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                recyclerViewAdapterKutuphane.itemListSort(true);
//                recyclerViewAdapterKutuphane.notifyDataSetChanged();
                recyclerViewAdapterKutuphane.getFilterKategori(queryKategori, queryYazar).filter("");
                relativeLayoutProgressBarKutuphane.setVisibility(View.GONE);
                aramaYapilabilir = true;
            }
        }.execute();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setting.gridLayoutSetting(context, fileKutuphane);
        gridLayoutManager = new GridLayoutManager(getActivity(), gridColumnSayisi);
        recyclerViewKutuphane.setHasFixedSize(true);
        recyclerViewKutuphane.setLayoutManager(gridLayoutManager);
        recyclerViewKutuphane.scrollToPosition(gridLayoutManagerPosition);
    }
}