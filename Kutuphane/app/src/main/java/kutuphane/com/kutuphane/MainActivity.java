package kutuphane.com.kutuphane;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.*;
import layout.*;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import static layout.Kutuphane.aramaYapilabilir;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Setting setting = new Setting();
    private Encryption encryption = new Encryption();
    private Strings strings = new Strings();
    private Context context;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Filez filez;
    private SearchView searchView;
    public static int gridColumnSayisi;
    public static int kitapHeightUzunlugu;
    public static double kitapOrani = 1.5;
    public static double kucultmeOrani = 1;
    public static int seciliFragment;
    public static int gridLayoutPosition = 0;
    public static boolean aramaYapiliyor = false;
    public static List<ItemObject> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        context = getApplicationContext();
        filez = new Filez(getApplicationContext());
        Filez fileKutuphane = new Filez(getApplicationContext(), encryption.md5(strings.getDosyaAdiKutuphane()), strings.getSharedPreferenceKutuphane(), null);
        String version = "";
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (fileKutuphane.veriAl(strings.getSharedPreferenceKutuphaneListesi()).equals("") || !fileKutuphane.veriAl(strings.getSharedPreferenceCurrentVersiyon()).equals(version) || !fileKutuphane.veriAl(strings.getSharedPreferenceKutuphaneListesiDurum()).equals("Tamam")) {
            fileKutuphane.veriKaydet(strings.getSharedPreferenceCurrentVersiyon(), version);
            int kutuphaneToplamKitapSayisi = 0;
            int kutuphaneKitapIndeks = 0;
            for (int j = 1; j <= strings.getKutuphaneListeSayisi(); j++) {
                try {
                    JSONArray jsonArray = new JSONArray(filez.dosyaIcerikAsset("kutuphane" + j));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        fileKutuphane.veriKaydet(strings.getSharedPreferenceKutuphaneListesi() + String.valueOf(++kutuphaneKitapIndeks), String.valueOf(jsonArray.getJSONObject(i)));
                    }
                    kutuphaneToplamKitapSayisi += jsonArray.length();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            fileKutuphane.veriKaydet(strings.getSharedPreferenceKutuphaneListesi(), String.valueOf(kutuphaneToplamKitapSayisi));
            fileKutuphane.veriKaydet(strings.getSharedPreferenceKutuphaneListesiDurum(), "Tamam");
        }

        gridColumnSayisi = getGridColumn();
        sharedPreferenceSetting(fileKutuphane);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        if (savedInstanceState == null) {
            MenuItem item = navigationView.getMenu().getItem(0);
            onNavigationItemSelected(item);
            seciliFragment = 1;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        MenuBuilder menuBuilder = (MenuBuilder) menu;
        menuBuilder.setOptionalIconsVisible(true);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewDetachedFromWindow(View arg0) {
                aramaYapiliyor = false;
                if (seciliFragment == 1) {
                    Kitaplar.recyclerViewAdapterKitap.itemListGeriYukle();
                    Kitaplar.recyclerViewAdapterKitap.itemListSort(false);
                    if (gridLayoutPosition <= Kitaplar.recyclerViewAdapterKitap.getItemCount()) {
                        Kitaplar.recyclerViewKitap.scrollToPosition(gridLayoutPosition);
                    } else {
                        Kitaplar.recyclerViewKitap.scrollToPosition(Kitaplar.recyclerViewAdapterKitap.getItemCount() - 1);
                    }
                } else if (seciliFragment == 2) {
                    Favoriler.recyclerViewAdapterFavori.itemListGeriYukle();
                    Favoriler.recyclerViewAdapterFavori.itemListSort(false);
                    if (gridLayoutPosition <= Favoriler.recyclerViewAdapterFavori.getItemCount()) {
                        Favoriler.recyclerViewFavori.scrollToPosition(gridLayoutPosition);
                    } else {
                        Favoriler.recyclerViewFavori.scrollToPosition(Favoriler.recyclerViewAdapterFavori.getItemCount() - 1);
                    }
                } else if (seciliFragment == 3) {
                    Kutuphane.recyclerViewAdapterKutuphane.setItemList(itemList);
                    Kutuphane.recyclerViewAdapterKutuphane.itemListSort(false);
                    if (gridLayoutPosition <= Kutuphane.recyclerViewAdapterKutuphane.getItemCount()) {
                        Kutuphane.recyclerViewKutuphane.scrollToPosition(gridLayoutPosition);
                    } else {
                        Kutuphane.recyclerViewKutuphane.scrollToPosition(Kutuphane.recyclerViewAdapterKutuphane.getItemCount() - 1);
                    }
                }
            }

            @Override
            public void onViewAttachedToWindow(View arg0) {
                aramaYapiliyor = true;
                if (seciliFragment == 1) {
                    gridLayoutPosition = Kitaplar.gridLayoutManagerPosition;
                } else if (seciliFragment == 2) {
                    gridLayoutPosition = Favoriler.gridLayoutManagerPosition;
                } else if (seciliFragment == 3) {
                    gridLayoutPosition = Kutuphane.gridLayoutManagerPosition;
                    itemList = Kutuphane.recyclerViewAdapterKutuphane.getItemList();
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (seciliFragment == 1) {
                    Kitaplar.recyclerViewAdapterKitap.getFilter(Kitaplar.recyclerViewAdapterKitap.getItemListYedek()).filter(query);
                    Kitaplar.recyclerViewKitap.scrollToPosition(0);
                } else if (seciliFragment == 2) {
                    Favoriler.recyclerViewAdapterFavori.getFilter(Favoriler.recyclerViewAdapterFavori.getItemListYedek()).filter(query);
                    Favoriler.recyclerViewFavori.scrollToPosition(0);
                } else if (seciliFragment == 3 && aramaYapilabilir) {
                    Kutuphane.recyclerViewAdapterKutuphane.getFilter(itemList).filter(query);
                    Kutuphane.recyclerViewKutuphane.scrollToPosition(0);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Context context = getApplicationContext();
        // View view = this.findViewById(android.R.id.content).getRootView();
        if (id == R.id.action_settings1) {

        } else if (id == R.id.action_settings2) {
            Filez file2 = new Filez(context, encryption.md5(strings.getDosyaAdiZaman()), strings.getSharedPreferenceZaman(), new Host().getAdresZaman());
            Filez file3 = new Filez(context, encryption.md5(strings.getDosyaAdiVersiyon()), strings.getSharedPreferenceVersiyon(), new Host().getAdresVersiyon());
            Update update = new Update(context, this, file3, strings.getDosyaAdiVersiyonEki());
            update.dosyaOlusturVersiyonMain(file3, file2);
        } else if (id == R.id.action_settings3) {
            Intent intent = new Intent(context, IstatistiklerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if (id == R.id.action_settings4) {
            Intent intent = new Intent(context, ContactActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if (id == R.id.action_sort) {
            Intent intent = new Intent(context, SortActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if (id == R.id.action_search) {

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean geriTusunaCiftTiklandi = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (geriTusunaCiftTiklandi) {
                super.onBackPressed();
                return;
            }
            geriTusunaCiftTiklandi = true;
            Toast.makeText(this, strings.getCik(), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    geriTusunaCiftTiklandi = false;
                }
            }, 2000);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;
        if (id == R.id.nav_kitaplar) {
            seciliFragment = 1;
            fragmentClass = Kitaplar.class;
        } else if (id == R.id.nav_favoriler) {
            seciliFragment = 2;
            fragmentClass = Favoriler.class;
        } else if (id == R.id.nav_kutuphane) {
            seciliFragment = 3;
            fragmentClass = Kutuphane.class;
        } else if (id == R.id.nav_okuyucular) {
            seciliFragment = 4;
            fragmentClass = Okuyucular.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayout1, fragment).commit();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private int getGridColumn() {
        String recyclerViewType = filez.veriAl(strings.getSharedPreferenceRecyclerViewType());
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels;
        float dpWidth = displayMetrics.widthPixels;
        final int rotation = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return setting.gridColumn_0_180(context, filez, recyclerViewType, dpHeight, dpWidth);
            case Surface.ROTATION_90:
                return setting.gridColumn_90_270(context, filez, recyclerViewType, dpHeight, dpWidth);
            case Surface.ROTATION_180:
                return setting.gridColumn_0_180(context, filez, recyclerViewType, dpHeight, dpWidth);
            default:
                return setting.gridColumn_90_270(context, filez, recyclerViewType, dpHeight, dpWidth);
        }
    }

    private void sharedPreferenceSetting(Filez filez) {
        if (filez.veriAl(strings.getSharedPreferenceRadioButtonSort()).equals("")) {
            filez.veriKaydet(strings.getSharedPreferenceRadioButtonSort(), "1");
        }
        if (filez.veriAl(strings.getSharedPreferenceSwitchSort()).equals("")) {
            filez.veriKaydet(strings.getSharedPreferenceSwitchSort(), "false");
        }
        if (filez.veriAl(strings.getSharedPreferenceRecyclerViewType()).equals("")) {
            filez.veriKaydet(strings.getSharedPreferenceRecyclerViewType(), "1");
        }
        if (filez.veriAl(strings.getSharedPreferenceGridLayoutSayisiOnceki()).equals("")) {
            filez.veriKaydet(strings.getSharedPreferenceGridLayoutSayisiOnceki(), String.valueOf(gridColumnSayisi));
        }
    }
}