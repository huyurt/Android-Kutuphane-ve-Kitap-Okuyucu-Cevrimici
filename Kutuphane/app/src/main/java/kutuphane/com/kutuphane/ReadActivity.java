package kutuphane.com.kutuphane;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.*;

public class ReadActivity extends AppCompatActivity {
    private Setting setting = new Setting();
    private Strings strings = new Strings();
    private Host host = new Host();
    private Handler handler = new Handler();
    private Context context;
    private Filez filez;
    private ReadSetting readSetting;
    private Toolbar toolbar;
    private Window window;
    private WindowManager windowManager;
    private BroadcastReceiver broadcastReceiver1;
    private BroadcastReceiver broadcastReceiver2;
    private FrameLayout frameLayout;
    private RelativeLayout relativeLayoutProgressBar;
    private TextView textView1Sarj;
    private TextView textView2Sayfa;
    private DigitalClock digitalClock3Saat;
    private TextView textViewSayfa;
    private LinearLayout linearLayoutAlt;
    private SeekBar seekBar;
    private TextView textViewStatus;
    private ImageButton imageButton0GeriAl;
    private ImageButton imageButton1Ekran;
    private ImageButton imageButton2Tema;
    private ImageButton imageButton3Ses;
    private ImageButton imageButton4Sitil;
    private ImageButton imageButton5Ayrac;
    private ImageButton imageButton6Not;
    private ImageButton imageButton7Icindekiler;
    private RelativeLayout relativeLayout;
    private ObservableWebView webView;
    // kitapId eklenecek
    private String kitapId;
    private String kitapIsmi;
    private Document document;
    private int webViewScrollPosition = 0;
    private String searchQuery = "";
    private int maksimumWebViewScroll = 0;
    public static ArrayList<String> scrollPositionGeriAl;
    public static ArrayList<String> scrollPositionGeriAlYuzde;
    public static ArrayList<String> scrollPositionGeriAlText;

    private boolean geriTusunaCiftTiklandi = false;
    private boolean fullscreen = true;
    private boolean baslat = true;
    private boolean aramaYapilabilir = false;
    private boolean seekBarDegisti = false;
    private boolean temaDegisti = false;

    ////////////////////////////////////////////////////
    private LinearLayout linearLayoutSeslendir;
    private LinearLayout linearLayoutSeslendirAyar;
    private ImageButton imageButtonSeslendir1Geri;
    private ImageButton imageButtonSeslendir2OynatDuraklat;
    private ImageButton imageButtonSeslendir3İleri;
    private ImageButton imageButtonSeslendir4Durdur;
    private ImageButton imageButtonSeslendir5Kapat;
    private ImageButton imageButtonSeslendir6Ayar;
    private SeekBar seekBarSeslendir1Hiz;
    private SeekBar seekBarSeslendir2Perde;
    private TextView textViewSeslendir1Hiz;
    private TextView textViewSeslendir2Perde;
    private TextToSpeech textToSpeech;
    private ArrayList<String> seslendirmeListesi;
    private int seslendirmeIndeks;

    private boolean seslendirmeAyarGosteriliyor = false;
    private boolean seslendirmeBasladi = false;
    private boolean seslendirmeDuraklatıldı = false;
    private boolean seslendirmeGeri = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        Intent intent = getIntent();
        scrollPositionGeriAl = new ArrayList<>();
        scrollPositionGeriAlYuzde = new ArrayList<>();
        scrollPositionGeriAlText = new ArrayList<>();
        kitapId = intent.getStringExtra("Kitap Id");
        kitapIsmi = intent.getStringExtra("Kitap İsmi");
        context = getApplicationContext();
        window = getWindow();
        windowManager = getWindowManager();
        broadcastReceiver1 = new BatteryBroadcastReceiver();
        broadcastReceiver2 = new BatteryBroadcastReceiver2();
        filez = new Filez(context);
        readSetting = new ReadSetting(context, filez, kitapId, kitapIsmi);
        if (!filez.veriAl(strings.getSharedPreferenceKitapScrollViewType() + kitapId + kitapIsmi).equals("") && !filez.veriAl(strings.getSharedPreferenceKitapScrollViewType() + kitapId + kitapIsmi).equals(String.valueOf(context.getResources().getConfiguration().orientation))) {
            ekraniDondur(context);
        } else {
            if (context.getResources().getConfiguration().orientation == 1) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
        }

        frameLayout = findViewById(R.id.readSeceneklerUst);
        frameLayout.setPadding(0, setting.getStatusBarHeight(window), 0, 0);
        toolbar = findViewById(R.id.readToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(kitapIsmi);

        relativeLayoutProgressBar = findViewById(R.id.relativeLayoutReadProgressBar);
        linearLayoutAlt = findViewById(R.id.linearLayoutReadAlaniAlt);
        seekBar = findViewById(R.id.seekBarReadScroll);
        textViewStatus = findViewById(R.id.textViewReadScroll);
        textViewSayfa = findViewById(R.id.textViewReadScrollSayfa);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                readSetting.seekBarStatus(textView2Sayfa, textViewStatus, progress, seekBar.getMax());
                textViewSayfa.setText(textView2Sayfa.getText());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (!seekBarDegisti) {
                    seekBarDegisti = true;
                    textViewSayfa.setText(textView2Sayfa.getText());
                    textViewSayfa.setVisibility(ViewPager.VISIBLE);
                    readSetting.scriptGetSayfa(webView);
                    scrollPositionGeriAl.add(String.valueOf(seekBar.getProgress()));
                    scrollPositionGeriAlYuzde.add(textViewStatus.getText().toString());
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textViewSayfa.setVisibility(ViewPager.GONE);
                imageButton0GeriAl.setVisibility(View.VISIBLE);
                webView.setScrollPosition(seekBar.getProgress());
            }
        });
        imageButton0GeriAl = findViewById(R.id.buttonRead0Scroll);
        imageButton0GeriAl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadActivity.this, ReadActivityScrollGeriAl.class);
                intent.putExtra("Kitap Id", kitapId);
                intent.putExtra("Kitap İsmi", kitapIsmi);
                startActivity(intent);
            }
        });
        imageButton0GeriAl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, getResources().getString(R.string.activity_read_button0), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        imageButton1Ekran = findViewById(R.id.buttonRead1Ekran);
        imageButton1Ekran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filez.veriKaydet(strings.getSharedPreferenceKitapScrollView() + kitapId + kitapIsmi, String.valueOf(seekBar.getProgress()));
                filez.veriKaydet(strings.getSharedPreferenceKitapToplamScrollView() + kitapId + kitapIsmi, String.valueOf(seekBar.getMax()));
                ekraniDondur(context);
            }
        });
        imageButton1Ekran.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, getResources().getString(R.string.activity_read_button1), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        imageButton2Tema = findViewById(R.id.buttonRead2Tema);
        imageButton2Tema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!temaDegisti) {
                    temaDegisti = true;
                    gizle();
                    readSetting.scriptBackground(webView, filez, baslat);
                }
            }
        });
        imageButton2Tema.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, getResources().getString(R.string.activity_read_button2), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        imageButton3Ses = findViewById(R.id.buttonRead3Ses);
        imageButton3Ses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutSeslendir.setVisibility(View.VISIBLE);
            }
        });
        imageButton3Ses.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, getResources().getString(R.string.activity_read_button3), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        imageButton4Sitil = findViewById(R.id.buttonRead4Sitil);
        imageButton4Sitil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Progress...", Toast.LENGTH_LONG).show();
            }
        });
        imageButton4Sitil.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, getResources().getString(R.string.activity_read_button4), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        imageButton5Ayrac = findViewById(R.id.buttonRead5Ayrac);
        imageButton5Ayrac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Progress...", Toast.LENGTH_LONG).show();
            }
        });
        imageButton5Ayrac.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, getResources().getString(R.string.activity_read_button5), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        imageButton6Not = findViewById(R.id.buttonRead6Not);
        imageButton6Not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Progress...", Toast.LENGTH_LONG).show();
            }
        });
        imageButton6Not.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, getResources().getString(R.string.activity_read_button6), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        imageButton7Icindekiler = findViewById(R.id.buttonRead7Icindekiler);
        imageButton7Icindekiler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Progress...", Toast.LENGTH_LONG).show();
            }
        });
        imageButton7Icindekiler.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, getResources().getString(R.string.activity_read_button7), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        relativeLayout = findViewById(R.id.relativeLayoutReadAlani);
        textView1Sarj = findViewById(R.id.textViewReadAlani1Sarj);
        textView2Sayfa = findViewById(R.id.textViewReadAlani2Sayfa);
        digitalClock3Saat = findViewById(R.id.digitalClockReadAlani3Saat);

        webView = findViewById(R.id.readAlani);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(final WebView view, String url) {
                readSetting.scriptBaslat(webView);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                if (aramaYapilabilir) {
                    if (seekBarDegisti) {
                        if (message.equals("")) {
                            message = "Son";
                        }
                        scrollPositionGeriAlText.add(message.trim());
                        seekBarDegisti = false;
                    } else if (temaDegisti) {
                        readSetting.setBackgroundRengi(relativeLayout, webView, textView1Sarj, textView2Sayfa, digitalClock3Saat);
                        temaDegisti = false;
                    } else if (baslat) {
                        readSetting.setBackground(relativeLayout, webView, textView1Sarj, textView2Sayfa, digitalClock3Saat);
                        relativeLayout.setVisibility(View.VISIBLE);
                        relativeLayoutProgressBar.setVisibility(View.GONE);
                        baslat = false;
                    } else if (seslendirmeBasladi) {
                        seslendirmeIndeks = 0;
                        seslendirmeListesi = new ArrayList<>(Arrays.asList(message.split("\\.")));
                        for (int i = 0; i < seslendirmeListesi.size(); i++) {
                            if (!readSetting.regexSil(seslendirmeListesi.get(i)).trim().equals("")) {
                                seslendirmeListesi.set(i, readSetting.regexSil(seslendirmeListesi.get(i)).trim() + ".");
                            } else {
                                seslendirmeListesi.remove(i);
                            }
                        }
                        if (!seslendirmeGeri) {
                            handler.post(seslendir);
                        } else {
                            seslendirmeGeri = false;
                            seslendirmeIndeks = seslendirmeListesi.size() - 1;
                            handler.post(seslendir);
                        }
                    } else {

                    }
                } else {
                    int pageCount = Integer.parseInt(message);
                    webView.setPageCount(pageCount);
                    if(pageCount > 1) {
                        seekBar.setMax(pageCount - 1);
                    } else {
                        seekBar.setMax(1);
                    }
                    String sSayfa = filez.veriAl(strings.getSharedPreferenceKitapScrollView() + kitapId + kitapIsmi);
                    String tSayfa = filez.veriAl(strings.getSharedPreferenceKitapToplamScrollView() + kitapId + kitapIsmi);
                    int currentSayfa;
                    if (!sSayfa.equals("")) {
                        int sonSayfa = Integer.parseInt(sSayfa);
                        if (!tSayfa.equals("")) {
                            int toplamSayfa = Integer.parseInt(tSayfa);
                            if (toplamSayfa <= pageCount) {
                                currentSayfa = (int) Math.ceil((double) ((pageCount - 1) * sonSayfa / (double) toplamSayfa));
                            } else {
                                currentSayfa = (int) Math.floor((double) ((pageCount - 1) * sonSayfa / (double) toplamSayfa));
                            }
                            filez.veriSil(strings.getSharedPreferenceKitapToplamScrollView() + kitapId + kitapIsmi);
                            filez.veriKaydet(strings.getSharedPreferenceKitapScrollView() + kitapId + kitapIsmi, String.valueOf(currentSayfa));
                            webView.setScrollPosition(currentSayfa);
                        } else {
                            webView.setScrollPosition(sonSayfa);
                        }
                    }
                    aramaYapilabilir = true;
                    readSetting.scriptBackground(webView, filez, baslat);
                }
                result.confirm();
                return true;
            }
        });
        webView.setOnTouchListener(new OnSwipeTouchListenerWebView(context) {
            public void onClick() {
                fullscreenToogle();
            }
        });
        webView.setOnScrollChangedCallback(new ObservableWebView.OnScrollChangedCallback() {
            public void onScroll(int l, int t) {
                filez.veriKaydet(strings.getSharedPreferenceKitapScrollView() + kitapId + kitapIsmi, String.valueOf(seekBar.getProgress()));
                filez.veriKaydet(strings.getSharedPreferenceKitapScrollViewType() + kitapId + kitapIsmi, String.valueOf(context.getResources().getConfiguration().orientation));
                seekBar.setProgress(webView.getCurrentPage());
            }
        });

        if (!filez.veriAl(strings.getSharedPreferenceKitap() + kitapId + kitapIsmi).equals("")) {
            document = Jsoup.parse(filez.veriAl(strings.getSharedPreferenceKitap() + kitapId + kitapIsmi));
            webView.loadDataWithBaseURL(null, document.html(), "text/html", "UTF-8", null);
        } else {
            kitap(kitapId, kitapIsmi, webView);
        }
        gizle();
        linearLayoutAlt.setPadding(0, 0, setting.getSoftButtonsBarWidth(windowManager), setting.getSoftButtonsBarHeight(windowManager));

        ///////////////////////////////////////////////////////////////////////////////////////////////
        linearLayoutSeslendir = findViewById(R.id.linearLayoutSeslendir);
        linearLayoutSeslendirAyar = findViewById(R.id.linearLayoutSeslendirAyar);
        imageButtonSeslendir1Geri = findViewById(R.id.imageButtonSeslendir1Geri);
        imageButtonSeslendir2OynatDuraklat = findViewById(R.id.imageButtonSeslendir2OynatDuraklat);
        imageButtonSeslendir3İleri = findViewById(R.id.imageButtonSeslendir3İleri);
        imageButtonSeslendir4Durdur = findViewById(R.id.imageButtonSeslendir4Durdur);
        imageButtonSeslendir5Kapat = findViewById(R.id.imageButtonSeslendir5Kapat);
        imageButtonSeslendir6Ayar = findViewById(R.id.imageButtonSeslendir6Ayar);
        seekBarSeslendir1Hiz = findViewById(R.id.seekBarReadSeslendir1Hiz);
        seekBarSeslendir2Perde = findViewById(R.id.seekBarReadSeslendir2Perde);
        textViewSeslendir1Hiz = findViewById(R.id.textViewSeslendir1Hiz);
        textViewSeslendir2Perde = findViewById(R.id.textViewSeslendir2Perde);
        linearLayoutSeslendir.setPadding(0, 0, setting.getSoftButtonsBarWidth(windowManager), setting.getSoftButtonsBarHeight(windowManager));

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(new Locale("tr"));
                    if (!filez.veriAl(strings.getSharedPreferenceKitapTTSHiz()).equals("")) {
                        textToSpeech.setSpeechRate(Float.parseFloat(filez.veriAl(strings.getSharedPreferenceKitapTTSHiz())) / 10);
                    }
                    if (!filez.veriAl(strings.getSharedPreferenceKitapTTSPerde()).equals("")) {
                        textToSpeech.setPitch(Float.parseFloat(filez.veriAl(strings.getSharedPreferenceKitapTTSPerde())) / 10);
                    }
                } else {
                    Toast.makeText(context, strings.getSesVerisiYok(), Toast.LENGTH_LONG).show();
                }
            }
        });
        seekBarSeslendir1Hiz.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewSeslendir1Hiz.setText("x" + ((float) progress / 10));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() == 0) {
                    seekBar.setProgress(1);
                }
                textToSpeech.setSpeechRate((float) seekBar.getProgress() / 10);
                filez.veriKaydet(strings.getSharedPreferenceKitapTTSHiz(), String.valueOf(seekBar.getProgress()));
                if (seslendirmeBasladi) {
                    seslendirmeBasladi = false;
                    if (textToSpeech.isSpeaking()) {
                        textToSpeech.stop();
                    }
                    handler.postDelayed(seslendir, 500);
                }
            }
        });
        seekBarSeslendir2Perde.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewSeslendir2Perde.setText("x" + ((float) progress / 10));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() == 0) {
                    seekBar.setProgress(1);
                }
                textToSpeech.setPitch((float) seekBar.getProgress() / 10);
                filez.veriKaydet(strings.getSharedPreferenceKitapTTSPerde(), String.valueOf(seekBar.getProgress()));
                if (seslendirmeBasladi) {
                    seslendirmeBasladi = false;
                    if (textToSpeech.isSpeaking()) {
                        textToSpeech.stop();
                    }
                    handler.postDelayed(seslendir, 500);
                }
            }
        });
        if (!filez.veriAl(strings.getSharedPreferenceKitapTTSHiz()).equals("")) {
            seekBarSeslendir1Hiz.setMax(30);
            seekBarSeslendir1Hiz.setProgress(Integer.parseInt(filez.veriAl(strings.getSharedPreferenceKitapTTSHiz())));
            textToSpeech.setSpeechRate((float) seekBar.getProgress() / 10);
        } else {
            seekBarSeslendir1Hiz.setMax(30);
            seekBarSeslendir1Hiz.setProgress(10);
        }
        if (!filez.veriAl(strings.getSharedPreferenceKitapTTSPerde()).equals("")) {
            seekBarSeslendir2Perde.setMax(30);
            seekBarSeslendir2Perde.setProgress(Integer.parseInt(filez.veriAl(strings.getSharedPreferenceKitapTTSPerde())));
            textToSpeech.setPitch((float) seekBar.getProgress() / 10);
        } else {
            seekBarSeslendir2Perde.setMax(30);
            seekBarSeslendir2Perde.setProgress(10);
        }
        imageButtonSeslendir1Geri.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, getResources().getString(R.string.activity_read_seslendir_button1), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        imageButtonSeslendir2OynatDuraklat.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!seslendirmeBasladi) {
                    Toast.makeText(context, getResources().getString(R.string.activity_read_seslendir_button2_1), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, getResources().getString(R.string.activity_read_seslendir_button2_2), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        imageButtonSeslendir3İleri.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, getResources().getString(R.string.activity_read_seslendir_button3), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        imageButtonSeslendir4Durdur.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, getResources().getString(R.string.activity_read_seslendir_button4), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        imageButtonSeslendir5Kapat.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, getResources().getString(R.string.activity_read_seslendir_button5), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        imageButtonSeslendir6Ayar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, getResources().getString(R.string.activity_read_seslendir_button6), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        imageButtonSeslendir1Geri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seslendirmeBasladi) {
                    seslendirmeBasladi = false;
                    if (textToSpeech.isSpeaking()) {
                        textToSpeech.stop();
                    }
                    if (seslendirmeIndeks <= 0) {
                        if (webView.getCurrentPage() > 0) {
                            webView.setScrollPosition(webView.getCurrentPage() - 1);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    seslendirmeBasladi = true;
                                    seslendirmeGeri = true;
                                    readSetting.scriptGetSayfa(webView);
                                }
                            }, 500);
                        } else {
                            seslendirmeDuraklatıldı = false;
                            imageButtonSeslendir2OynatDuraklat.setImageResource(R.drawable.ic_read_seslendir_oynat);
                        }
                    } else {
                        handler.postDelayed(seslendirGeri, 500);
                    }
                }
            }
        });
        imageButtonSeslendir3İleri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seslendirmeBasladi) {
                    seslendirmeBasladi = false;
                    if (textToSpeech.isSpeaking()) {
                        textToSpeech.stop();
                    }
                    if (seslendirmeIndeks >= seslendirmeListesi.size() - 1) {
                        if (webView.getCurrentPage() <= webView.getPageCount() - 1) {
                            webView.setScrollPosition(webView.getCurrentPage() + 1);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    seslendirmeBasladi = true;
                                    readSetting.scriptGetSayfa(webView);
                                }
                            }, 500);
                        } else {
                            seslendirmeDuraklatıldı = false;
                            imageButtonSeslendir2OynatDuraklat.setImageResource(R.drawable.ic_read_seslendir_oynat);
                        }
                    } else {
                        handler.postDelayed(seslendirIleri, 500);
                    }
                }
            }
        });
        imageButtonSeslendir2OynatDuraklat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seslendirmeBasladi) {
                    seslendirmeBasladi = false;
                    seslendirmeDuraklatıldı = true;
                    if (textToSpeech.isSpeaking()) {
                        textToSpeech.stop();
                    }
                    imageButtonSeslendir2OynatDuraklat.setImageResource(R.drawable.ic_read_seslendir_oynat);
                } else {
                    seslendirmeBasladi = true;
                    if (!seslendirmeDuraklatıldı) {
                        readSetting.scriptGetSayfa(webView);
                    } else {
                        seslendirmeDuraklatıldı = true;
                        textToSpeech.speak(seslendirmeListesi.get(seslendirmeIndeks), TextToSpeech.QUEUE_FLUSH, null);
                    }
                    imageButtonSeslendir2OynatDuraklat.setImageResource(R.drawable.ic_read_seslendir_duraklat);
                }
            }
        });
        imageButtonSeslendir4Durdur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seslendirmeBasladi = false;
                seslendirmeDuraklatıldı = false;
                if (textToSpeech.isSpeaking()) {
                    textToSpeech.stop();
                }
                imageButtonSeslendir2OynatDuraklat.setImageResource(R.drawable.ic_read_seslendir_oynat);
            }
        });
        imageButtonSeslendir5Kapat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seslendirmeAyarGosteriliyor = false;
                linearLayoutSeslendirAyar.setVisibility(View.GONE);
                if (seslendirmeBasladi) {
                    gizle();
                } else {
                    linearLayoutSeslendir.setVisibility(View.GONE);
                }
            }
        });
        imageButtonSeslendir6Ayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!seslendirmeAyarGosteriliyor) {
                    seslendirmeAyarGosteriliyor = true;
                    linearLayoutSeslendirAyar.setVisibility(View.VISIBLE);
                } else {
                    seslendirmeAyarGosteriliyor = false;
                    linearLayoutSeslendirAyar.setVisibility(View.GONE);
                }
            }
        });

        Toast.makeText(context, strings.getGelistiriciUyarisi(), Toast.LENGTH_SHORT).show();
    }







    @Override
    protected void onResume() {
        super.onResume();
        if (scrollPositionGeriAl.size() == 0) {
            imageButton0GeriAl.setVisibility(View.GONE);
        }
        String yeniScrollPosition = filez.veriAl(strings.getSharedPreferenceKitapScrollGeriAl());
        if (!yeniScrollPosition.equals("")) {
            webView.setScrollPosition(Integer.parseInt(yeniScrollPosition));
            filez.veriSil(strings.getSharedPreferenceKitapScrollGeriAl());
        }
    }

    private void kitap(final String kitapId, final String kitapIsmi, final ObservableWebView webView) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                relativeLayout.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... params) {
                String icerik = null;
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("kitapIsmi", params[0])
                        .build();
                Request request = new Request.Builder()
                        .url(host.getAdresGozat() + host.getAdresKitap())
                        .method("POST", RequestBody.create(null, new byte[0]))
                        .post(requestBody)
                        .build();
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    JSONArray jsonArray = new JSONArray(response.body().string());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        icerik = jsonObject.getString(strings.getJsonKitap());
                    }
                } catch (IOException e) {
                    return strings.getInternetBaglantisiYok();
                } catch (JSONException e) {
                    return strings.getInternetBaglantisiYok();
                }
                return icerik;
            }

            @Override
            protected void onPostExecute(String sonuc) {
                if (!sonuc.equals(strings.getInternetBaglantisiYok())) {
                    document = Jsoup.parse(sonuc);
                    webView.loadDataWithBaseURL(null, document.html(), "text/html", "UTF-8", null);
                    filez.veriKaydet(strings.getSharedPreferenceKitap() + kitapId + kitapIsmi, document.html());
                } else {
                    finish();
                }
            }
        }.execute(kitapIsmi);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_read, menu);
        MenuBuilder menuBuilder = (MenuBuilder) menu;
        menuBuilder.setOptionalIconsVisible(true);

        MenuItem searchItem = menu.findItem(R.id.action_read_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        android.widget.Toolbar.LayoutParams navButtonsParams = new android.widget.Toolbar.LayoutParams(toolbar.getHeight() * 2 / 3, toolbar.getHeight() * 2 / 3);
        Button buttonBack = new Button(context);
        buttonBack.setBackground(getResources().getDrawable(R.drawable.ic_read_search_back));
        Button buttonForward = new Button(context);
        buttonForward.setBackground(getResources().getDrawable(R.drawable.ic_read_search_forward));
        ((LinearLayout) searchView.getChildAt(0)).addView(buttonBack, navButtonsParams);
        ((LinearLayout) searchView.getChildAt(0)).addView(buttonForward, navButtonsParams);
        ((LinearLayout) searchView.getChildAt(0)).setGravity(Gravity.BOTTOM);
/*        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewDetachedFromWindow(View arg0) {
                webView.clearMatches();
                webView.setScrollX(webViewScrollPosition);
            }

            @Override
            public void onViewAttachedToWindow(View v) {
                if (aramaYapilabilir) {
                    webViewScrollPosition = webView.getScrollX();
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                if (aramaYapilabilir) {
                    searchQuery = newText;
                    webView.findAllAsync(newText);
                }
                return false;
            }
        });
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aramaYapilabilir && !searchQuery.equals("")) {
                    webView.findNext(false);
                }
            }
        });
        buttonForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aramaYapilabilir && !searchQuery.equals("")) {
                    webView.findNext(true);
                }
            }
        });*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            geriTusunaCiftTiklandi = true;
        }
        if (id == R.id.action_read_search) {
            Toast.makeText(context, "Progress", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (geriTusunaCiftTiklandi) {
            super.onBackPressed();
            return;
        }
        geriTusunaCiftTiklandi = true;
        Toast.makeText(context, strings.getCik(), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                geriTusunaCiftTiklandi = false;
            }
        }, 2000);
    }

    @Override
    public void finish() {
        if (textToSpeech.isSpeaking()) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        gizle();
        if (filez.veriAl(strings.getSharedPreferenceKitap() + kitapId + kitapIsmi).equals("")) {
            Toast.makeText(context, strings.getInternetBaglantisiYok2(), Toast.LENGTH_LONG).show();
        } else {
            filez.veriKaydet(strings.getSharedPreferenceKitapScrollView() + kitapId + kitapIsmi, String.valueOf(seekBar.getProgress()));
            filez.veriKaydet(strings.getSharedPreferenceKitapScrollViewType() + kitapId + kitapIsmi, String.valueOf(context.getResources().getConfiguration().orientation));
        }
        super.finish();
    }

    private void ekraniDondur(Context context) {
        if (context.getResources().getConfiguration().orientation == 1) {
            filez.veriKaydet(strings.getSharedPreferenceKitapScrollViewType() + kitapId + kitapIsmi, String.valueOf(2));
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            filez.veriKaydet(strings.getSharedPreferenceKitapScrollViewType() + kitapId + kitapIsmi, String.valueOf(1));
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
    }

    private Runnable hide = new Runnable() {
        @Override
        public void run() {
            frameLayout.setVisibility(View.GONE);
            linearLayoutAlt.setVisibility(View.GONE);
            linearLayoutSeslendir.setVisibility(View.GONE);
            linearLayoutSeslendirAyar.setVisibility(View.GONE);
            seslendirmeAyarGosteriliyor = false;
            fullscreen = true;
        }
    };

    private Runnable show = new Runnable() {
        @Override
        public void run() {
            frameLayout.setVisibility(View.VISIBLE);
            linearLayoutAlt.setVisibility(View.VISIBLE);
            if (seslendirmeBasladi) {
                linearLayoutSeslendir.setVisibility(View.VISIBLE);
            }
            fullscreen = false;
        }
    };

    private void gizle() {
        handler.removeCallbacks(show);
        handler.post(hide);
        setting.fullsceenIn(relativeLayout);
    }

    private void goster() {
        setting.fullscreenOut(relativeLayout);
        handler.removeCallbacks(hide);
        handler.post(show);
    }

    private void fullscreenToogle() {
        if (fullscreen) {
            goster();
        } else {
            gizle();
        }
    }

    @Override
    protected void onStart() {
        registerReceiver(broadcastReceiver1, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        registerReceiver(broadcastReceiver2, new IntentFilter(TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED));
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(broadcastReceiver1);
        unregisterReceiver(broadcastReceiver2);
        super.onStop();
    }

    private class BatteryBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            textView1Sarj.setText(level + "%");
        }
    }

    private class BatteryBroadcastReceiver2 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (seslendirmeBasladi) {
                if (seslendirmeIndeks >= seslendirmeListesi.size() - 1) {
                    if (webView.getCurrentPage() < webView.getPageCount() - 1) {
                        webView.setScrollPosition(webView.getCurrentPage() + 1);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                readSetting.scriptGetSayfa(webView);
                            }
                        }, 500);
                    } else {
                        seslendirmeBasladi = false;
                        seslendirmeDuraklatıldı = false;
                        imageButtonSeslendir2OynatDuraklat.setImageResource(R.drawable.ic_read_seslendir_oynat);
                    }
                } else {
                    handler.postDelayed(seslendirIleri, 500);
                }
            }
        }
    }

    private Runnable seslendir = new Runnable() {
        @Override
        public void run() {
            seslendirmeBasladi = true;
            textToSpeech.speak(seslendirmeListesi.get(seslendirmeIndeks), TextToSpeech.QUEUE_FLUSH, null);
        }
    };

    private Runnable seslendirIleri = new Runnable() {
        @Override
        public void run() {
            seslendirmeBasladi = true;
            textToSpeech.speak(seslendirmeListesi.get(++seslendirmeIndeks), TextToSpeech.QUEUE_FLUSH, null);
        }
    };

    private Runnable seslendirGeri = new Runnable() {
        @Override
        public void run() {
            seslendirmeBasladi = true;
            textToSpeech.speak(seslendirmeListesi.get(--seslendirmeIndeks), TextToSpeech.QUEUE_FLUSH, null);
        }
    };
}