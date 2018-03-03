package kutuphane.com.kutuphane;

public class ItemObject {
    private String kitapId;
    private String kitapZamani;
    private String kitapIsmi;
    private String kitapYazari;
    private String kitapKategorisi;
    private String kitapOzeti;
    private String kitapKapak;
    private String kitapBoyut;

    private String okuyucuIsmi;
    private String okuyucuKapak;
    private String okuyucuPaketIsmi;

    public ItemObject(String kitapId, String kitapZamani, String kitapIsmi, String kitapYazari, String kitapKategorisi, String kitapOzeti, String kitapKapak, String kitapBoyut) {
        this.kitapId = kitapId;
        this.kitapZamani = kitapZamani;
        this.kitapIsmi = kitapIsmi;
        this.kitapYazari = kitapYazari;
        this.kitapKategorisi = kitapKategorisi;
        this.kitapOzeti = kitapOzeti;
        this.kitapKapak = kitapKapak;
        this.kitapBoyut = kitapBoyut;
    }

    public ItemObject(String okuyucuIsmi, String okuyucuKapak, String okuyucuPaketIsmi) {
        this.okuyucuIsmi = okuyucuIsmi;
        this.okuyucuKapak = okuyucuKapak;
        this.okuyucuPaketIsmi = okuyucuPaketIsmi;
    }

    /** Kutuphane */
    public String getKitapId() {
        return kitapId;
    }

    public void setKitapId(String kitapId) {
        this.kitapId = kitapId;
    }

    public String getKitapZamani() {
        return kitapZamani;
    }

    public void setKitapZamani(String kitapZamani) {
        this.kitapZamani = kitapZamani;
    }

    public String getKitapIsmi() {
        return kitapIsmi;
    }

    public void setKitapIsmi(String kitapIsmi) {
        this.kitapIsmi = kitapIsmi;
    }

    public String getKitapYazari() {
        return kitapYazari;
    }

    public void setKitapYazari(String kitapYazari) {
        this.kitapYazari = kitapYazari;
    }

    public String getKitapKategorisi() {
        return kitapKategorisi;
    }

    public void setKitapKategorisi(String kitapKategorisi) {
        this.kitapKategorisi = kitapKategorisi;
    }

    public String getKitapOzeti() {
        return kitapOzeti;
    }

    public void setKitapOzeti(String kitapOzeti) {
        this.kitapOzeti = kitapOzeti;
    }

    public String getKitapKapak() {
        return kitapKapak;
    }

    public void setKitapKapak(String kitapKapak) {
        this.kitapKapak = kitapKapak;
    }

    public String getKitapBoyut() {
        return kitapBoyut;
    }

    public void setKitapBoyut(String kitapBoyut) {
        this.kitapBoyut = kitapBoyut;
    }

    /** Okuyucular */
    public String getOkuyucuKapak() {
        return okuyucuKapak;
    }

    public String getOkuyucuIsmi() {
        return okuyucuIsmi;
    }

    public String getOkuyucuPaketIsmi() {
        return okuyucuPaketIsmi;
    }
}