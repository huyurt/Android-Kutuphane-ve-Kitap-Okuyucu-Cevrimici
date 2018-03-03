package kutuphane.com.kutuphane;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ContactActivity extends AppCompatActivity {
    private Strings strings = new Strings();
    private Context context;
    private RelativeLayout relativeLayoutGizle;
    private WebView webView;
    private TextView textViewMail;
    private TextView textViewAppAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        context = getApplicationContext();
        getSupportActionBar().show();
        getSupportActionBar().setTitle("İletişim");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        relativeLayoutGizle = findViewById(R.id.relativeLayoutIletisimGizle);
        webView = findViewById(R.id.webViewIletisim);
        textViewMail = findViewById(R.id.textViewIletisimMail);
        textViewAppAbout = findViewById(R.id.textViewIletisimAppAbout);

        textViewMail.setText(strings.getMail());

        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = packageInfo.versionName;
            textViewAppAbout.setText(getResources().getString(R.string.app_name) + "\nv" + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl("file:///android_asset/iletisim");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                relativeLayoutGizle.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}