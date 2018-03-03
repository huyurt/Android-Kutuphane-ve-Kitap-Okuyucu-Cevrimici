package kutuphane.com.kutuphane;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import java.util.ArrayList;

import static kutuphane.com.kutuphane.ReadActivity.scrollPositionGeriAl;
import static kutuphane.com.kutuphane.ReadActivity.scrollPositionGeriAlText;
import static kutuphane.com.kutuphane.ReadActivity.scrollPositionGeriAlYuzde;

public class ListViewGeriAlAdapter extends ArrayAdapter<String> {
    private Strings strings = new Strings();
    private Filez filez;
    private Context context;
    private ArrayList<String> values;
    private ArrayList<String> yuzde;
    private ArrayList<String> text;
    private String kitapId;
    private String kitapIsim;

    public ListViewGeriAlAdapter(Context context, ArrayList<String> values, ArrayList<String> yuzde, ArrayList<String> text, String kitapId, String kitapIsim) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
        this.yuzde = yuzde;
        this.text = text;
        this.kitapId = kitapId;
        this.kitapIsim = kitapIsim;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = layoutInflater.inflate(R.layout.list_view_list, parent, false);
        filez = new Filez(context);
        Button button = rowView.findViewById(R.id.buttonReadListViewGeriAl);
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        String[] textArray;
        if(text.get(position).length() < 25) {
            textArray = text.get(position).trim().split("\n");
        } else {
            textArray = text.get(position).substring(0, 25).trim().split("\n");
        }
        String textString = "";
        for(String s : textArray) {
            textString += s.trim() + " ";
        }
        button.setText(yuzde.get(position) + " " + textString.trim() + " (" + String.valueOf(Integer.parseInt(values.get(position)) + 1) + "s.)");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filez.veriKaydet(strings.getSharedPreferenceKitapScrollGeriAl(), scrollPositionGeriAl.get(position));
                scrollPositionGeriAl.remove(position);
                scrollPositionGeriAlYuzde.remove(position);
                scrollPositionGeriAlText.remove(position);
                ((Activity)context).finish();
            }
        });
        return rowView;
    }
}