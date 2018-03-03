package kutuphane.com.kutuphane;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class IstatistiklerActivity extends Activity {
    private Context context;
    private Filez filez;
    private ImageButton imageButton;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_istatistikler);
        context = getApplicationContext();
        filez = new Filez(context);

        imageButton = findViewById(R.id.imageButtonIstatistikler);
        textView = findViewById(R.id.textViewIstatistikler);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        String[] array = filez.dosyaIcerikAsset("istatistikler").split("\\|");
        String sonuc = "";
        for (String s : array) {
            sonuc += s + "\n\n";
        }
        textView.setText(sonuc.trim());
    }
}