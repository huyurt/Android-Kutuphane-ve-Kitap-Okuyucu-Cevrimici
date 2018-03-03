package kutuphane.com.kutuphane;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import layout.Favoriler;
import layout.Kitaplar;
import layout.Kutuphane;

import static kutuphane.com.kutuphane.MainActivity.gridColumnSayisi;
import static kutuphane.com.kutuphane.MainActivity.seciliFragment;

public class SortActivity extends Activity {
    private Strings strings = new Strings();
    private Context context;
    private Filez filez;
    private String radioButtonSortSelected;
    private String switchSortSelected;
    private String recyclerViewTypeSelected;
    private RelativeLayout relativeLayoutSwitch;
    private ImageButton imageButton;
    private RadioGroup radioGroupSort;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private Switch switchSort;
    private CheckBox checkBox1;
    private CheckBox checkBox2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);
        context = this;
        filez = new Filez(context);
        radioButtonSortSelected = filez.veriAl(strings.getSharedPreferenceRadioButtonSort());
        switchSortSelected = filez.veriAl(strings.getSharedPreferenceSwitchSort());
        recyclerViewTypeSelected = filez.veriAl(strings.getSharedPreferenceRecyclerViewType());

        relativeLayoutSwitch = findViewById(R.id.relativeLayoutSortSwitch);
        imageButton = findViewById(R.id.imageButtonSort);
        radioGroupSort = findViewById(R.id.radioGroupSort);
        radioButton1 = findViewById(R.id.radioButtonSort1);
        radioButton2 = findViewById(R.id.radioButtonSort2);
        radioButton3 = findViewById(R.id.radioButtonSort3);
        switchSort = findViewById(R.id.switchSort1);
        checkBox1 = findViewById(R.id.checkBoxRecyclerViewType1);
        checkBox2 = findViewById(R.id.checkBoxRecyclerViewType2);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (seciliFragment != 3 && radioButtonSortSelected.equals("1")) {
            relativeLayoutSwitch.setVisibility(View.GONE);
        } else {
            relativeLayoutSwitch.setVisibility(View.VISIBLE);
        }

        if (recyclerViewTypeSelected.equals("1")) {
            checkBox1.setChecked(true);
        } else if (recyclerViewTypeSelected.equals("2")) {
            checkBox2.setChecked(true);
        }
        checkBox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBox1.setChecked(true);
            }
        });
        checkBox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBox2.setChecked(true);
            }
        });
        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    checkBox2.setChecked(true);
                    filez.veriKaydet(strings.getSharedPreferenceRecyclerViewType(), "2");
                    filez.veriKaydet(strings.getSharedPreferenceGridLayoutSayisiOnceki(), String.valueOf(gridColumnSayisi));
                } else {
                    checkBox2.setChecked(false);
                    filez.veriKaydet(strings.getSharedPreferenceRecyclerViewType(), "1");
                }
            }
        });
        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    checkBox1.setChecked(true);
                } else {
                    checkBox1.setChecked(false);
                }
            }
        });

        switch (radioButtonSortSelected) {
            case "1":
                radioButton1.toggle();
                break;
            case "2":
                radioButton2.toggle();
                break;
            case "3":
                radioButton3.toggle();
                break;
            default:
                radioButton1.toggle();
                filez.veriKaydet(strings.getSharedPreferenceRadioButtonSort(), "1");
                break;
        }
        radioGroupSort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radioButtonSort1) {
                    if (seciliFragment != 3) {
                        relativeLayoutSwitch.setVisibility(View.GONE);
                    }
                    filez.veriKaydet(strings.getSharedPreferenceRadioButtonSort(), "1");
                } else if (i == R.id.radioButtonSort2) {
                    relativeLayoutSwitch.setVisibility(View.VISIBLE);
                    filez.veriKaydet(strings.getSharedPreferenceRadioButtonSort(), "2");
                } else {
                    relativeLayoutSwitch.setVisibility(View.VISIBLE);
                    filez.veriKaydet(strings.getSharedPreferenceRadioButtonSort(), "3");
                }
            }
        });

        if (switchSortSelected.equals("true")) {
            switchSort.setChecked(true);
        } else {
            switchSort.setChecked(false);
            filez.veriKaydet(strings.getSharedPreferenceSwitchSort(), "false");
        }
        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    filez.veriKaydet(strings.getSharedPreferenceSwitchSort(), "true");
                } else {
                    filez.veriKaydet(strings.getSharedPreferenceSwitchSort(), "false");
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        if (!radioButtonSortSelected.equals(filez.veriAl(strings.getSharedPreferenceRadioButtonSort())) || !switchSortSelected.equals(filez.veriAl(strings.getSharedPreferenceSwitchSort()))) {
            if (seciliFragment == 1) {
                Kitaplar.recyclerViewAdapterKitap.itemListSort(false);
                Kitaplar.recyclerViewAdapterKitap.notifyDataSetChanged();
            } else if (seciliFragment == 2) {
                Favoriler.recyclerViewAdapterFavori.itemListSort(false);
                Favoriler.recyclerViewAdapterFavori.notifyDataSetChanged();
            } else if (seciliFragment == 3) {
                Kutuphane.recyclerViewAdapterKutuphane.itemListSort(false);
                Kutuphane.recyclerViewAdapterKutuphane.notifyDataSetChanged();
            }
        }
        if (!recyclerViewTypeSelected.equals(filez.veriAl(strings.getSharedPreferenceRecyclerViewType()))) {
            if (filez.veriAl(strings.getSharedPreferenceRecyclerViewType()).equals("1")) {
                if (gridColumnSayisi == 1) {
                    gridColumnSayisi = 3;
                } else if (gridColumnSayisi == 2 && filez.veriAl(strings.getSharedPreferenceGridLayoutSayisiOnceki()).equals("5")) {
                    gridColumnSayisi = 5;
                } else if (gridColumnSayisi == 2 && filez.veriAl(strings.getSharedPreferenceGridLayoutSayisiOnceki()).equals("4")) {
                    gridColumnSayisi = 4;
                } else if (gridColumnSayisi == 3) {
                    gridColumnSayisi = 6;
                }
            } else if (filez.veriAl(strings.getSharedPreferenceRecyclerViewType()).equals("2")) {
                if (gridColumnSayisi == 3) {
                    gridColumnSayisi = 1;
                } else if (gridColumnSayisi == 5) {
                    gridColumnSayisi = 2;
                } else if (gridColumnSayisi == 4) {
                    gridColumnSayisi = 2;
                } else if (gridColumnSayisi == 6) {
                    gridColumnSayisi = 3;
                }
            }
            if (seciliFragment == 1) {
                Kitaplar.gridLayoutManagerKitaplar.setSpanCount(gridColumnSayisi);
            } else if (seciliFragment == 2) {
                Favoriler.gridLayoutManagerFavoriler.setSpanCount(gridColumnSayisi);
            } else if (seciliFragment == 3) {
                Kutuphane.gridLayoutManager.setSpanCount(gridColumnSayisi);
            }
        }
    }
}