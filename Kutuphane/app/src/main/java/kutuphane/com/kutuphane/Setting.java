package kutuphane.com.kutuphane;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;

import static kutuphane.com.kutuphane.MainActivity.gridColumnSayisi;
import static kutuphane.com.kutuphane.MainActivity.kitapOrani;
import static kutuphane.com.kutuphane.MainActivity.kitapHeightUzunlugu;

public class Setting {
    private Strings strings = new Strings();

    public void gridLayoutSetting(Context context, Filez filez) {
        if (context.getResources().getConfiguration().orientation == 1) {
            if (filez.veriAl(strings.getSharedPreferenceRecyclerViewType()).equals("2")) {
                if (filez.veriAl(strings.getSharedPreferenceGridLayoutSayisiOnceki()).equals("5")) {
                    filez.veriKaydet(strings.getSharedPreferenceGridLayoutSayisiOnceki(), "3");
                    gridColumnSayisi = 1;
                } else if (filez.veriAl(strings.getSharedPreferenceGridLayoutSayisiOnceki()).equals("6")) {
                    filez.veriKaydet(strings.getSharedPreferenceGridLayoutSayisiOnceki(), "4");
                    gridColumnSayisi = 2;
                }
            } else {
                if (gridColumnSayisi == 2) {
                    filez.veriKaydet(strings.getSharedPreferenceGridLayoutSayisiOnceki(), "5");
                    gridColumnSayisi = 3;
                } else if (gridColumnSayisi == 3 && filez.veriAl(strings.getSharedPreferenceGridLayoutSayisiOnceki()).equals("6")) {
                    filez.veriKaydet(strings.getSharedPreferenceGridLayoutSayisiOnceki(), "6");
                    gridColumnSayisi = 4;
                } else if (gridColumnSayisi == 5) {
                    filez.veriKaydet(strings.getSharedPreferenceGridLayoutSayisiOnceki(), "3");
                    gridColumnSayisi = 3;
                } else if (gridColumnSayisi == 6) {
                    filez.veriKaydet(strings.getSharedPreferenceGridLayoutSayisiOnceki(), "4");
                    gridColumnSayisi = 4;
                }
            }
        } else if (context.getResources().getConfiguration().orientation == 2) {
            if (filez.veriAl(strings.getSharedPreferenceRecyclerViewType()).equals("2")) {
                if (filez.veriAl(strings.getSharedPreferenceGridLayoutSayisiOnceki()).equals("3")) {
                    filez.veriKaydet(strings.getSharedPreferenceGridLayoutSayisiOnceki(), "5");
                    gridColumnSayisi = 2;
                } else if (filez.veriAl(strings.getSharedPreferenceGridLayoutSayisiOnceki()).equals("4")) {
                    filez.veriKaydet(strings.getSharedPreferenceGridLayoutSayisiOnceki(), "6");
                    gridColumnSayisi = 3;
                }
            } else {
                if (gridColumnSayisi == 1) {
                    filez.veriKaydet(strings.getSharedPreferenceGridLayoutSayisiOnceki(), "3");
                    gridColumnSayisi = 5;
                } else if (gridColumnSayisi == 2 && filez.veriAl(strings.getSharedPreferenceGridLayoutSayisiOnceki()).equals("4")) {
                    filez.veriKaydet(strings.getSharedPreferenceGridLayoutSayisiOnceki(), "4");
                    gridColumnSayisi = 6;
                } else if (gridColumnSayisi == 3) {
                    filez.veriKaydet(strings.getSharedPreferenceGridLayoutSayisiOnceki(), "5");
                    gridColumnSayisi = 5;
                } else if (gridColumnSayisi == 4) {
                    filez.veriKaydet(strings.getSharedPreferenceGridLayoutSayisiOnceki(), "6");
                    gridColumnSayisi = 6;
                }
            }
        }
    }

    public void setTab(TabHost tabHost, int yazarId, int kategoriId, int recyclerViewId) {
        tabHost.setup();
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("Yazar");
        tabSpec.setContent(yazarId);
        tabSpec.setIndicator("Yazar");
        tabHost.addTab(tabSpec);
        tabSpec = tabHost.newTabSpec("Kategori");
        tabSpec.setContent(kategoriId);
        tabSpec.setIndicator("Kategori");
        tabHost.addTab(tabSpec);
        tabSpec = tabHost.newTabSpec("Kitaplar");
        tabSpec.setContent(recyclerViewId);
        tabSpec.setIndicator("Kitaplar");
        tabHost.addTab(tabSpec);
        tabHost.setCurrentTabByTag("Kitaplar");
    }

    public void settingListView(final ListView listView, final int listViewTur, final Filez filez) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 && listView.isItemChecked(position)) {
                    for (int i = 1; i < parent.getCount(); i++) {
                        listView.setItemChecked(i, false);
                    }
                } else if (position == 0 && !listView.isItemChecked(position)) {
                    listView.setItemChecked(position, true);
                } else if (position != 0 && listView.isItemChecked(position)) {
                    listView.setItemChecked(0, false);
                } else if (position != 0 && !listView.isItemChecked(0)) {
                    int isaretli = 0;
                    for (int i = 1; i < parent.getCount(); i++) {
                        if (listView.isItemChecked(i)) {
                            isaretli++;
                            break;
                        }
                    }
                    if (isaretli == 0) {
                        listView.setItemChecked(0, true);
                    }
                }
                String checkedItems = "";
                for (int i = 0; i < parent.getCount(); i++) {
                    if (listView.isItemChecked(i)) {
                        checkedItems += i + " ";
                    }
                }
                if (listViewTur == 0) {
                    filez.veriKaydet(strings.getSharedPreferenceListViewKategori(), checkedItems);
                } else {
                    filez.veriKaydet(strings.getSharedPreferenceListViewYazar(), checkedItems);
                }
            }
        });
    }

    private boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    public int gridColumn_0_180(Context context, Filez filez, String recyclerViewType, float dpHeight, float dpWidth) {
        if (dpHeight >= dpWidth || !isTablet(context)) {
            kitapHeightUzunlugu = (int) (dpWidth / 3 * kitapOrani);
            if (recyclerViewType.equals("2")) {
                filez.veriKaydet(strings.getSharedPreferenceGridLayoutSayisiOnceki(), "3");
                filez.veriKaydet(strings.getSharedPreferenceCihaz(), strings.getCihaz1());
                return 1;
            } else {
                filez.veriKaydet(strings.getSharedPreferenceCihaz(), strings.getCihaz2());
                return 3;
            }
        }
        kitapHeightUzunlugu = (int) (dpWidth / 6 * kitapOrani);
        if (recyclerViewType.equals("2")) {
            filez.veriKaydet(strings.getSharedPreferenceGridLayoutSayisiOnceki(), "6");
            filez.veriKaydet(strings.getSharedPreferenceCihaz(), strings.getCihaz1());
            return 3;
        } else {
            filez.veriKaydet(strings.getSharedPreferenceCihaz(), strings.getCihaz2());
            return 6;
        }
    }

    public int gridColumn_90_270(Context context, Filez filez, String recyclerViewType, float dpHeight, float dpWidth) {
        if (dpHeight <= dpWidth || !isTablet(context)) {
            kitapHeightUzunlugu = (int) (dpHeight / 3 * kitapOrani);
            if (recyclerViewType.equals("2")) {
                filez.veriKaydet(strings.getSharedPreferenceGridLayoutSayisiOnceki(), "5");
                filez.veriKaydet(strings.getSharedPreferenceCihaz(), strings.getCihaz1());
                return 2;
            } else {
                filez.veriKaydet(strings.getSharedPreferenceCihaz(), strings.getCihaz2());
                return 5;
            }
        }
        kitapHeightUzunlugu = (int) (dpHeight / 6 * kitapOrani);
        if (recyclerViewType.equals("2")) {
            filez.veriKaydet(strings.getSharedPreferenceGridLayoutSayisiOnceki(), "4");
            filez.veriKaydet(strings.getSharedPreferenceCihaz(), strings.getCihaz1());
            return 2;
        } else {
            filez.veriKaydet(strings.getSharedPreferenceCihaz(), strings.getCihaz2());
            return 4;
        }
    }



    public int getStatusBarHeight(Window window) {
        Rect rectangle = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight= contentViewTop - statusBarHeight;
        return statusBarHeight;
    }

    @SuppressLint("NewApi")
    public int getSoftButtonsBarHeight(WindowManager windowManager) {
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        windowManager.getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    @SuppressLint("NewApi")
    public int getSoftButtonsBarWidth(WindowManager windowManager) {
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int usableWidth = metrics.widthPixels;
        windowManager.getDefaultDisplay().getRealMetrics(metrics);
        int realWidth = metrics.widthPixels;
        if (realWidth > usableWidth) {
            return realWidth - usableWidth;
        } else {
            return 0;
        }
    }

    public void fullsceenIn (View view) {
        view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    public void fullscreenOut (View view) {
        view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}