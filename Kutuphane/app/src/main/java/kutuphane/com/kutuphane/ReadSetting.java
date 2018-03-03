package kutuphane.com.kutuphane;

import android.content.Context;
import android.graphics.Color;
import android.widget.DigitalClock;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadSetting {
    private Strings strings = new Strings();
    private Filez filez;
    private Context context;
    private String kitapId;
    private String kitapIsmi;

    public ReadSetting(Context context, Filez filez, String kitapId, String kitapIsmi) {
        this.context = context;
        this.filez = filez;
        this.kitapId = kitapId;
        this.kitapIsmi = kitapIsmi;
    }

    public void scriptBaslat(ObservableWebView webView) {
        String currentTema = filez.veriAl(strings.getSharedPreferenceKitapTema());
        String color;
        if (currentTema.equals(strings.getKitapTemaDark())) {
            color = strings.getKitapTemaColorSiyah();
        } else {
            color = strings.getKitapTemaColorBeyaz();
        }
        String js = "function mScript(){" +
                "var script = document.createElement('script');" +
                "script.setAttribute('src', 'file:///android_asset/javascript/jquery-3.2.1.min.js');" +
                "script.setAttribute('type', 'text/javascript');" +
                "document.body.appendChild(script);" +
                "var d = document.getElementsByTagName('body')[0];" +
                "d.style.margin = 0;" +
                "var ourH = window.innerHeight;" +
                "var ourW = window.innerWidth;" +
                "var fullH = d.scrollHeight;" +
                "var pageCount = Math.ceil(fullH / ourH);" +
                "var newW = pageCount * ourW;" +
                "d.style.height = ourH+'px';" +
                "d.style.width = newW+'px';" +
                "d.style.webkitColumnGap = '0px';" +
                "d.style.webkitColumnCount = pageCount;" +
                "d.style.color = '" + color + "';" +
                "return Math.ceil(d.scrollWidth / ourW);" +
                "}";
        webView.loadUrl("javascript:" + js);
        webView.loadUrl("javascript:alert(mScript())");
    }

    public void scriptGetSayfa(ObservableWebView webView) {
        String js = "function mScript(a, b){" +
                "var caretRangeStart = document.caretRangeFromPoint(a, b);" +
                "var caretRangeEnd = document.caretRangeFromPoint(a + window.innerWidth - 1, b + window.innerHeight - 1);" +
                "var range = document.createRange();" +
                "range.setStart(caretRangeStart.startContainer, caretRangeStart.startOffset);" +
                "range.setEnd(caretRangeEnd.endContainer, caretRangeEnd.endOffset);" +
                "return range.toString();" +
                "}";
        webView.loadUrl("javascript:" + js);
        webView.loadUrl("javascript:alert(mScript(0, 0))");
    }

    public void scriptBackground(ObservableWebView webView, Filez filez, boolean baslat) {
        String currentTema = filez.veriAl(strings.getSharedPreferenceKitapTema());
        String jsLight = "function mScript(){" +
                "var d = document.getElementsByTagName('body')[0];" +
                "d.style.color = '" + strings.getKitapTemaColorBeyaz() + "';" +
                "}";
        String jsDark = "function mScript(){" +
                "var d = document.getElementsByTagName('body')[0];" +
                "d.style.color = '" + strings.getKitapTemaColorSiyah() + "';" +
                "}";
        if(baslat) {
            if (currentTema.equals(strings.getKitapTemaDark())) {
                webView.loadUrl("javascript:" + jsDark);
            } else {
                webView.loadUrl("javascript:" + jsLight);
            }
        } else {
            if (currentTema.equals(strings.getKitapTemaDark())) {
                webView.loadUrl("javascript:" + jsLight);
            } else {
                webView.loadUrl("javascript:" + jsDark);
            }
        }
        webView.loadUrl("javascript:alert(mScript())");
    }

    public void scriptStringHighlight(ObservableWebView webView, String text) {
        String js = "function mScript() {" +
                "var searching, limitsearch, countsearch;" +
                "var searchedText = \"" + text + "\";" +
                "var page = $('#ray-all_text');" +
                "if (searchedText != \"\") {" +
                "if (searching != searchedText) {" +
                "page.find('font').contents().unwrap();" +
                "var pageText = page.html();" +
                "var theRegEx = new RegExp(\"(\" + searchedText + \")\", \"igm\");" +
                "var newHtml = pageText.replace(theRegEx, \"<font>$1</font>\");" +
                "page.html(newHtml);" +
                "searching = searchedText;" +
                "limitsearch = page.find('font').length;" +
                "countsearch = 0;" +
                "} else {" +
                "countsearch < limitsearch - 1 ? countsearch++ : countsearch = 0;" +
                "console.log(countsearch + '---' + limitsearch)" +
                "}" +
                "var actual = $(\"#ray-all_text font\").eq(countsearch);" +
                "$('.active').removeClass('active');" +
                "actual.addClass('active');" +
                "var $container = $(\"html,body\");" +
                "var $scrollTo = $('.active');" +
                "$container.animate({scrollTop: 0, scrollLeft: $scrollTo.offset().left - $container.offset().left + $container.scrollTop()}, 10);" +
                "}" +
                "}";
        webView.loadUrl("javascript:" + js);
        webView.loadUrl("javascript:alert(mScript())");
    }

    public String getSayfa(Document document, String mesaj) {
        Document document2 = Jsoup.parse(mesaj);
        Pattern pattern = Pattern.compile(regexEkle(document2.text()) + "(.*)");
        Matcher matcher = pattern.matcher(document.text());
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    private String regexEkle(String ifade) {
        ifade = ifade.replace("\\", "\\\\");
        ifade = ifade.replace(".", "\\.");
        ifade = ifade.replace("?", "\\?");
        ifade = ifade.replace("+", "\\+");
        ifade = ifade.replace("*", "\\*");
        ifade = ifade.replace("$", "\\$");
        ifade = ifade.replace("-", "\\-");
        ifade = ifade.replace("&", "\\&");
        ifade = ifade.replace("|", "\\|");
        ifade = ifade.replace("\"", "\\\"");
        ifade = ifade.replace("(", "\\(");
        ifade = ifade.replace(")", "\\)");
        ifade = ifade.replace("{", "\\{");
        ifade = ifade.replace("}", "\\}");
        ifade = ifade.replace("<", "\\<");
        ifade = ifade.replace(">", "\\>");
        ifade = ifade.replace("[", "\\[");
        ifade = ifade.replace("]", "\\]");
        return ifade;
    }

    public String regexSil(String ifade) {
        ifade = ifade.replace("\\", "");
        ifade = ifade.replace("+", "");
        ifade = ifade.replace("*", "");
        ifade = ifade.replace("$", "");
        ifade = ifade.replace("-", "");
        ifade = ifade.replace("&", "");
        ifade = ifade.replace("|", "");
        ifade = ifade.replace("\"", "");
        ifade = ifade.replace("”", "");
        ifade = ifade.replace("“", "");
        ifade = ifade.replace("’", "");
        ifade = ifade.replace("'", "");
        ifade = ifade.replace("(", "");
        ifade = ifade.replace(")", "");
        ifade = ifade.replace("{", "");
        ifade = ifade.replace("}", "");
        ifade = ifade.replace("<", "");
        ifade = ifade.replace(">", "");
        ifade = ifade.replace("[", "");
        ifade = ifade.replace("]", "");
        return ifade;
    }

    public void setBackgroundRengi(RelativeLayout relativeLayout, ObservableWebView webView, TextView textViewSarj, TextView textViewSayfa, DigitalClock digitalClock) {
        if (filez.veriAl(strings.getSharedPreferenceKitapTema()).equals(strings.getKitapTemaDark())) {
            textViewSarj.setTextColor(Color.parseColor(strings.getKitapTemaColorBeyaz()));
            textViewSayfa.setTextColor(Color.parseColor(strings.getKitapTemaColorBeyaz()));
            digitalClock.setTextColor(Color.parseColor(strings.getKitapTemaColorBeyaz()));
            relativeLayout.setBackgroundColor(Color.parseColor(strings.getKitapTemaBackgroundBeyaz()));
            webView.setBackgroundColor(Color.parseColor(strings.getKitapTemaBackgroundBeyaz()));
            filez.veriKaydet(strings.getSharedPreferenceKitapTema(), strings.getKitapTemaLight());
        } else {
            textViewSarj.setTextColor(Color.parseColor(strings.getKitapTemaColorSiyah()));
            textViewSayfa.setTextColor(Color.parseColor(strings.getKitapTemaColorSiyah()));
            digitalClock.setTextColor(Color.parseColor(strings.getKitapTemaColorSiyah()));
            relativeLayout.setBackgroundColor(Color.parseColor(strings.getKitapTemaBackgroundSiyah()));
            webView.setBackgroundColor(Color.parseColor(strings.getKitapTemaBackgroundSiyah()));
            filez.veriKaydet(strings.getSharedPreferenceKitapTema(), strings.getKitapTemaDark());
        }
    }

    public void setBackground(RelativeLayout relativeLayout, ObservableWebView webView, TextView textViewSarj, TextView textViewSayfa, DigitalClock digitalClock) {
        if (filez.veriAl(strings.getSharedPreferenceKitapTema()).equals(strings.getKitapTemaDark())) {
            textViewSarj.setTextColor(Color.parseColor(strings.getKitapTemaColorSiyah()));
            textViewSayfa.setTextColor(Color.parseColor(strings.getKitapTemaColorSiyah()));
            digitalClock.setTextColor(Color.parseColor(strings.getKitapTemaColorSiyah()));
            relativeLayout.setBackgroundColor(Color.parseColor(strings.getKitapTemaBackgroundSiyah()));
            webView.setBackgroundColor(Color.parseColor(strings.getKitapTemaBackgroundSiyah()));
        } else {
            textViewSarj.setTextColor(Color.parseColor(strings.getKitapTemaColorBeyaz()));
            textViewSayfa.setTextColor(Color.parseColor(strings.getKitapTemaColorBeyaz()));
            digitalClock.setTextColor(Color.parseColor(strings.getKitapTemaColorBeyaz()));
            relativeLayout.setBackgroundColor(Color.parseColor(strings.getKitapTemaBackgroundBeyaz()));
            webView.setBackgroundColor(Color.parseColor(strings.getKitapTemaBackgroundBeyaz()));
        }
    }

    public void seekBarStatus(TextView textViewSayfa, TextView textView, int progress, int maksimum) {
        int sayi = progress * 1000 / maksimum;
        int sayiTam = sayi / 10;
        int sayiKusur = sayi % 10;
        textViewSayfa.setText((progress + 1) + " / " + (maksimum + 1));
        if (sayiTam != 100) {
            if (sayiTam > 9) {
                textView.setText(sayiTam + "." + sayiKusur + "%");
            } else {
                textView.setText(sayiTam + "." + sayiKusur + "  %");
            }
        } else {
            textView.setText(sayiTam + " %");
        }
    }
}