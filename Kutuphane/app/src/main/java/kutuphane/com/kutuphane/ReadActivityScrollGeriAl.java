package kutuphane.com.kutuphane;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import static kutuphane.com.kutuphane.ReadActivity.*;

public class ReadActivityScrollGeriAl extends Activity {
    private Setting setting = new Setting();
    private ListView listView;
    private ListViewGeriAlAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_scroll_geri_al);
        Intent intent = getIntent();
        listView = findViewById(R.id.listViewReadScrollGeriAl);
        setting.fullsceenIn(listView);
        listAdapter = new ListViewGeriAlAdapter(this, scrollPositionGeriAl, scrollPositionGeriAlYuzde, scrollPositionGeriAlText, intent.getStringExtra("Kitap Id"), intent.getStringExtra("Kitap İsmi"));
        listView.setAdapter(listAdapter);
    }
}