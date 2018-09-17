package luis_santiago.com.ailrun.POJOS;

/**
 * Created by Luis Santiago on 9/16/18.
 */
public class User {
    private String name;
    private String urlImage;
    private String uid;

    public User(String name, String urlImage, String uid) {
        this.name = name;
        this.urlImage = urlImage;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public String getUid() {
        return uid;
    }
}
