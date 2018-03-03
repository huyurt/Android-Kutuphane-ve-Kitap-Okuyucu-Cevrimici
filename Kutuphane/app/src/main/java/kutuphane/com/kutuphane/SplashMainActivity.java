package kutuphane.com.kutuphane;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class SplashMainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_main);

        TextView textView = findViewById(R.id.textView1);
        String fontIsmi = getResources().getString(R.string.splash_screen_font);
        Typeface typeface = Typeface.createFromAsset(getAssets(), fontIsmi);
        textView.setTypeface(typeface);

        int splashGoruntulemeZamani = 1000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashMainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, splashGoruntulemeZamani);
    }
}