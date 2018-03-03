package kutuphane.com.kutuphane;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.Filter;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static kutuphane.com.kutuphane.MainActivity.*;
import static layout.Favoriler.textViewFavoriler;
import static layout.Kitaplar.textViewKitaplar;
import static layout.Kutuphane.tabHost;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {
    private List<ItemObject> itemList;
    private List<ItemObject> itemListYedek;
    private Encryption encryption = new Encryption();
    private Strings strings = new Strings();
    private Filez2 filez2 = new Filez2();
    private Context context;
    private Filez filez;
    private File file1;
    private File file2;

    public RecyclerViewAdapter(Context context, List<ItemObject> itemList) {
        this.context = context;
        this.itemList = itemList;
        this.itemListYedek = itemList;
    }

    public List<ItemObject> getItemList() {
        return itemList;
    }

    public void setItemList(List<ItemObject> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    public List<ItemObject> getItemListYedek() {
        return itemListYedek;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_list, null);
        RecyclerViewHolders recyclerViewHolders = new RecyclerViewHolders(layoutView);
        layoutView.setLongClickable(false);
        layoutView.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeRight() {
                if (seciliFragment == 3) {
                    tabHost.setCurrentTab(tabHost.getCurrentTab() - 1);
                }
            }
        });
        return recyclerViewHolders;
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        holder.relativeLayout.getLayoutParams().width = (int) ((kitapHeightUzunlugu / kitapOrani) / kucultmeOrani);
        holder.relativeLayout.getLayoutParams().height = kitapHeightUzunlugu;
        holder.kitapIsim.setText(itemList.get(position).getKitapIsmi());
        holder.kitapYazar.setText(itemList.get(position).getKitapYazari());
        holder.kitapKategori.setText("Kategori: ");
        holder.kitapKategori.setText(holder.kitapKategori.getText() + new Filez(holder.itemView.getContext()).kategori(itemList.get(position).getKitapKategorisi()));
        holder.kitapBoyut.setText(String.format("%.2f", Integer.parseInt(itemList.get(position).getKitapBoyut()) / 1048576.0) + " MB");
        if (seciliFragment == 1 || seciliFragment == 2) {
            holder.kitapKapak.setImageBitmap(filez2.decodeBase64(itemList.get(position).getKitapKapak()));
        } else {
            Picasso.with(holder.itemView.getContext()).load(itemList.get(position).getKitapKapak()).into(holder.kitapKapak);
        }
        holder.kitapIsimArka.setText(itemList.get(position).getKitapIsmi());
        holder.kitapYazarArka.setText(itemList.get(position).getKitapYazari());
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final Filez fileKitap = new Filez(context, encryption.md5(strings.getDosyaAdiKitaplar()), strings.getSharedPreferenceKitaplar(), null);
                final Filez fileFavori = new Filez(context, encryption.md5(strings.getDosyaAdiFavoriler()), strings.getSharedPreferenceFavoriler(), null);
                final int popupTuru = popupMenuTuru(fileKitap, fileFavori, itemList, position);
                Context wrapper = new ContextThemeWrapper(view.getContext(), R.style.AppTheme_PopupOverlay);
                final PopupMenu popupMenu = new PopupMenu(wrapper, holder.imageButton);
                if (popupTuru == 1) {
                    popupMenu.getMenuInflater().inflate(R.menu.menu_card_view1, popupMenu.getMenu());
                } else if (popupTuru == 2) {
                    popupMenu.getMenuInflater().inflate(R.menu.menu_card_view2, popupMenu.getMenu());
                } else if (popupTuru == 3) {
                    popupMenu.getMenuInflater().inflate(R.menu.menu_card_view3, popupMenu.getMenu());
                } else if (popupTuru == 4) {
                    popupMenu.getMenuInflater().inflate(R.menu.menu_card_view4, popupMenu.getMenu());
                } else if (popupTuru == 5) {
                    popupMenu.getMenuInflater().inflate(R.menu.menu_card_view5, popupMenu.getMenu());
                } else if (popupTuru == 6) {
                    popupMenu.getMenuInflater().inflate(R.menu.menu_card_view6, popupMenu.getMenu());
                } else if (popupTuru == 7) {
                    popupMenu.getMenuInflater().inflate(R.menu.menu_card_view7, popupMenu.getMenu());
                } else {
                    popupMenu.getMenuInflater().inflate(R.menu.menu_card_view8, popupMenu.getMenu());
                }
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.menuCardView1) {
                            holder.onClickOkuma(view, itemList);
                        } else if (id == R.id.menuCardView2) {
                            holder.onClickBilgiler(view, itemList);
                        } else if (id == R.id.menuCardView3) {
                            if (popupTuru == 1 || popupTuru == 3 || popupTuru == 5 || popupTuru == 7) {
                                try {
                                    filez2.kitapEkle(fileKitap, itemList, position, ((BitmapDrawable) holder.kitapKapak.getDrawable()).getBitmap());
                                } catch (NullPointerException e) {
                                    Toast.makeText(context, strings.getInternetBaglantisiYok(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                try {
                                    filez2.kitapKaldir(fileKitap, itemList, position, ((BitmapDrawable) holder.kitapKapak.getDrawable()).getBitmap());
                                } catch (NullPointerException e) {
                                    Toast.makeText(context, strings.getInternetBaglantisiYok(), Toast.LENGTH_SHORT).show();
                                }
                                if (seciliFragment == 1) {
                                    recylerViewItemSil(position, textViewKitaplar);
                                }
                            }
                        } else if (id == R.id.menuCardView4) {
                            if (popupTuru == 1 || popupTuru == 2 || popupTuru == 5 || popupTuru == 6) {
                                try {
                                    filez2.kitapEkle(fileFavori, itemList, position, ((BitmapDrawable) holder.kitapKapak.getDrawable()).getBitmap());
                                } catch (NullPointerException e) {
                                    Toast.makeText(context, strings.getInternetBaglantisiYok(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                try {
                                    filez2.kitapKaldir(fileFavori, itemList, position, ((BitmapDrawable) holder.kitapKapak.getDrawable()).getBitmap());
                                } catch (NullPointerException e) {
                                    Toast.makeText(context, strings.getInternetBaglantisiYok(), Toast.LENGTH_SHORT).show();
                                }
                                if (seciliFragment == 2) {
                                    recylerViewItemSil(position, textViewFavoriler);
                                }
                            }
                        } else if (id == R.id.menuCardView5) {
                            Toast.makeText(holder.itemView.getContext(), itemList.get(position).getKitapIsmi() + " silindi.", Toast.LENGTH_LONG).show();
                            file1.delete();
                            file2.delete();
                        } else if (id == R.id.menuCardView6) {
                            holder.onClickGozat(view, itemList);
                        }
                        return true;
                    }
                });
            }
        });
    }

    private int popupMenuTuru(Filez fileKitaplar, Filez fileFavoriler, List<ItemObject> liste, int position) {
        String kitapMd5 = encryption.md5(liste.get(position).getKitapId() + liste.get(position).getKitapIsmi());
        file1 = new File(Environment.getExternalStorageDirectory(), "Android/data/" + filez.getDosyaYolu() + "/" + kitapMd5 + ".epub");
        file2 = new File(Environment.getExternalStorageDirectory(), "Android/data/" + filez.getDosyaYolu() + "/" + kitapMd5 + ".pdf");
        boolean eslestiKitap = false;
        boolean eslestiFavori = false;
        if (seciliFragment == 1) {
            eslestiKitap = true;
            if (filez2.kitapIsmiEslestirme(fileFavoriler, liste.get(position).getKitapId(), liste.get(position).getKitapIsmi())) {
                eslestiFavori = true;
            }
        } else if (seciliFragment == 2) {
            eslestiFavori = true;
            if (filez2.kitapIsmiEslestirme(fileKitaplar, liste.get(position).getKitapId(), liste.get(position).getKitapIsmi())) {
                eslestiKitap = true;
            }
        } else if (seciliFragment == 3) {
            if (filez2.kitapIsmiEslestirme(fileKitaplar, liste.get(position).getKitapId(), liste.get(position).getKitapIsmi())) {
                eslestiKitap = true;
            }
            if (filez2.kitapIsmiEslestirme(fileFavoriler, liste.get(position).getKitapId(), liste.get(position).getKitapIsmi())) {
                eslestiFavori = true;
            }
        }
        if (!eslestiKitap && !eslestiFavori) {
            if (file1.exists() || file2.exists()) {
                return 5;
            }
            return 1;
        } else if (eslestiKitap && !eslestiFavori) {
            if (file1.exists() || file2.exists()) {
                return 6;
            }
            return 2;
        } else if (!eslestiKitap && eslestiFavori) {
            if (file1.exists() || file2.exists()) {
                return 7;
            }
            return 3;
        } else {
            if (file1.exists() || file2.exists()) {
                return 8;
            }
            return 4;
        }
    }

    private void recylerViewItemSil(int position, TextView textView) {
        itemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, itemList.size());
        if (aramaYapiliyor) {
            if (seciliFragment == 1) {
                Filez fK = new Filez(null, encryption.md5(strings.getDosyaAdiKitaplar()), null, null);
                itemListYedek = fK.recyclerViewYukle();
            } else if (seciliFragment == 2) {
                Filez fF = new Filez(null, encryption.md5(strings.getDosyaAdiFavoriler()), null, null);
                itemListYedek = fF.recyclerViewYukle();
            }
        }
        if(getItemCount() == 0) {
            textView.setVisibility(View.VISIBLE);
        }
    }

    public Filter getFilter(final List<ItemObject> liste) {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().toLowerCase().trim();
                List<ItemObject> filteredList = new ArrayList<>();
                String[] searchArray = charString.split(" ");
                for (ItemObject item : liste) {
                    for (String search : searchArray) {
                        if (item.getKitapIsmi().toLowerCase().contains(search)
                                || item.getKitapKategorisi().toLowerCase().contains(search)
                                || item.getKitapOzeti().toLowerCase().contains(search)
                                || item.getKitapYazari().toLowerCase().contains(search)) {
                            if (!filteredList.contains(item))
                                filteredList.add(item);
                        }
                    }
                }
                itemList = filteredList;
                FilterResults filterResults = new FilterResults();
                filterResults.values = itemList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                itemList = (List<ItemObject>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public Filter getFilterKategori(final String queryKategori, final String queryYazar) {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<ItemObject> filteredList = new ArrayList<>();
                String[] searchArrayKategori = queryKategori.toLowerCase().trim().split("\\|");
                String[] searchArrayYazar = queryYazar.toLowerCase().trim().split("\\|");
                for (int i = 0; i < searchArrayKategori.length; i++) {
                    searchArrayKategori[i] = searchArrayKategori[i].replaceAll("\\([0-9]+\\)", "").trim();
                }
                for (int i = 0; i < searchArrayYazar.length; i++) {
                    searchArrayYazar[i] = searchArrayYazar[i].replaceAll("\\([0-9]+\\)", "").trim();
                }
                for (ItemObject item : itemListYedek) {
                    for (String searchKategori : searchArrayKategori) {
                        for (String searchYazar : searchArrayYazar) {
                            if (searchKategori.equals("\uFEFFtümü") && searchYazar.equals("\uFEFFtümü")) {
                                if (!filteredList.contains(item)) {
                                    filteredList.add(item);
                                }
                            } else if (searchKategori.equals("\uFEFFtümü") && !searchYazar.equals("\uFEFFtümü")) {
                                if (item.getKitapYazari().toLowerCase().contains(searchYazar)) {
                                    if (!filteredList.contains(item)) {
                                        filteredList.add(item);
                                    }
                                }
                            } else if (!searchKategori.equals("\uFEFFtümü") && searchYazar.equals("\uFEFFtümü")) {
                                if (item.getKitapKategorisi().toLowerCase().contains(searchKategori)) {
                                    if (!filteredList.contains(item)) {
                                        filteredList.add(item);
                                    }
                                }
                            } else {
                                if (item.getKitapKategorisi().toLowerCase().contains(searchKategori) && item.getKitapYazari().toLowerCase().contains(searchYazar)) {
                                    if (!filteredList.contains(item)) {
                                        filteredList.add(item);
                                    }
                                }
                            }
                        }
                    }
                }
                itemList = filteredList;
                FilterResults filterResults = new FilterResults();
                filterResults.values = itemList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                itemList = (List<ItemObject>) filterResults.values;
                MainActivity.itemList = itemList;
                itemListSort(false);
                notifyDataSetChanged();
            }
        };
    }

    public void itemListGeriYukle() {
        if (itemList != itemListYedek) {
            itemList = itemListYedek;
            notifyDataSetChanged();
        }
    }

    public void itemListSort(boolean baslangic) {
        filez = new Filez(context);
        final String sortAdi = filez.veriAl(strings.getSharedPreferenceRadioButtonSort());
        final String sortTuru = filez.veriAl(strings.getSharedPreferenceSwitchSort());
        if (seciliFragment != 3) {
            if (!sortAdi.equals("1")) {
                Collections.sort(itemList, new Comparator<ItemObject>() {
                    @Override
                    public int compare(ItemObject lhs, ItemObject rhs) {
                        switch (sortAdi) {
                            case "2":
                                if (sortTuru.equals("false")) {
                                    return rhs.getKitapIsmi().compareTo(lhs.getKitapIsmi());
                                }
                                return lhs.getKitapIsmi().compareTo(rhs.getKitapIsmi());
                            default:
                                if (sortTuru.equals("false")) {
                                    return rhs.getKitapYazari().compareTo(lhs.getKitapYazari());
                                }
                                return lhs.getKitapYazari().compareTo(rhs.getKitapYazari());
                        }
                    }
                });
            } else {
                if (!baslangic) {
                    if (seciliFragment == 1) {
                        Filez fileKitap = new Filez(context, encryption.md5(strings.getDosyaAdiKitaplar()), strings.getSharedPreferenceKitaplar(), null);
                        itemList = fileKitap.recyclerViewYukle();
                    } else if (seciliFragment == 2) {
                        Filez fileFavori = new Filez(context, encryption.md5(strings.getDosyaAdiFavoriler()), strings.getSharedPreferenceFavoriler(), null);
                        itemList = fileFavori.recyclerViewYukle();
                    }
                }
            }
        } else {
            Collections.sort(itemList, new Comparator<ItemObject>() {
                @Override
                public int compare(ItemObject lhs, ItemObject rhs) {
                    switch (sortAdi) {
                        case "1":
                            if (sortTuru.equals("false")) {
                                return rhs.getKitapZamani().compareTo(lhs.getKitapZamani());
                            }
                            return lhs.getKitapZamani().compareTo(rhs.getKitapZamani());
                        case "2":
                            if (sortTuru.equals("false")) {
                                return rhs.getKitapIsmi().compareTo(lhs.getKitapIsmi());
                            }
                            return lhs.getKitapIsmi().compareTo(rhs.getKitapIsmi());
                        default:
                            if (sortTuru.equals("false")) {
                                return rhs.getKitapYazari().compareTo(lhs.getKitapYazari());
                            }
                            return lhs.getKitapYazari().compareTo(rhs.getKitapYazari());
                    }
                }
            });
        }
    }
}