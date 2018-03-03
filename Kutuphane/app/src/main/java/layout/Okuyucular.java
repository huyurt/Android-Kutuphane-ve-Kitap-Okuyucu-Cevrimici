package layout;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.*;
import com.squareup.picasso.Picasso;
import kutuphane.com.kutuphane.Filez;
import kutuphane.com.kutuphane.ItemObject;
import kutuphane.com.kutuphane.R;
import kutuphane.com.kutuphane.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Okuyucular extends Fragment {
    private Strings strings = new Strings();
    private Context context;
    private Filez filez;
    private ListView listView;
    private List<ItemObject> list;
    private ArrayList<String> arrayList;
    private ListViewAdapter listViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        filez = new Filez(context);
        list = new ArrayList<>();
        arrayList = new ArrayList<String>(Arrays.asList(filez.dosyaIcerikAsset(strings.getDosyaAdiAssetOkuyucu()).split("\\|")));
        for (int i = 0; i < arrayList.size(); i++) {
            ItemObject itemObject = new ItemObject(arrayList.get(i), arrayList.get(++i), arrayList.get(++i));
            list.add(itemObject);
        }
        listViewAdapter = new ListViewAdapter(list);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_okuyucular, container, false);
        listView = view.findViewById(R.id.listViewOkuyucular);
        listView.setAdapter(listViewAdapter);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class ListViewAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private List<ItemObject> liste;

        public ListViewAdapter(List<ItemObject> liste) {
            this.liste = liste;
        }

        @Override
        public int getCount() {
            return liste.size();
        }

        @Override
        public Object getItem(int position) {
            return liste.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.list_view_okuyucular, null);
            ImageView imageView = view.findViewById(R.id.imageViewListOkuyucular);
            TextView textView = view.findViewById(R.id.textViewListOkuyucular);

            Picasso.with(context).load(liste.get(position).getOkuyucuKapak()).into(imageView);
            textView.setText(liste.get(position).getOkuyucuIsmi());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    try {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + liste.get(position).getOkuyucuPaketIsmi()));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + liste.get(position).getOkuyucuPaketIsmi()));
                    }
                    startActivity(intent);
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(context, liste.get(position).getOkuyucuIsmi() + " indir.", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            return view;
        }
    }
}