package luis_santiago.com.ailrun.POJOS;

import java.util.HashMap;
import java.util.WeakHashMap;

/**
 * Created by Luis Santiago on 9/16/18.
 */
public class User {
    private String name;
    private String urlImage;
    private String uid;
    private Integer weight;
    private Integer height;

    public void setName(String name) {
        this.name = name;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

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


    public HashMap<String, Object> toHash() {
        HashMap<String, Object> hashMap = new HashMap();
        hashMap.put("name", name);
        hashMap.put("profileUrl", urlImage);
        if (height != null) {
            hashMap.put("height", height);
        }

        if (weight != null) {
            hashMap.put("weight", weight);
        }
        return hashMap;
    }
}
