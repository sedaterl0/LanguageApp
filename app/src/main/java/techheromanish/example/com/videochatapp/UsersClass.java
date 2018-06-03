package techheromanish.example.com.videochatapp;

public class UsersClass {


    String kullaniciAd;
    //String kullaniciKodAd;
    String kullaniciMilliyet;

    public UsersClass(String kullaniciAd, String kullaniciMilliyet) {
        this.kullaniciAd = kullaniciAd;
        //   this.kullaniciKodAd = kullaniciKodAd;
        this.kullaniciMilliyet = kullaniciMilliyet;
    }

    public UsersClass() {

    }

    public String getKullaniciAd() {
        return kullaniciAd;
    }

    public void setKullaniciAd(String kullaniciAd) {
        this.kullaniciAd = kullaniciAd;
    }


    public String getKullaniciMilliyet() {
        return kullaniciMilliyet;
    }

    public void setKullaniciMilliyet(String kullaniciMilliyet) {
        this.kullaniciMilliyet = kullaniciMilliyet;
    }
}
