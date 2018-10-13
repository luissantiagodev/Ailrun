package luis_santiago.com.ailrun.POJOS;

import android.content.Intent;

import java.util.HashMap;
import java.util.WeakHashMap;

/**
 * Created by Luis Santiago on 9/16/18.
 */
public class User {
    private String name;
    private String urlImage;
    private String uid;
    private Double weight;
    private Double height;
    private Integer age;
    private Integer sexOption;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getSexOption() {
        return sexOption;
    }

    public void setSexOption(Integer sexOption) {
        this.sexOption = sexOption;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public User(String name, String urlImage, String uid) {
        this.name = name;
        this.urlImage = urlImage;
        this.uid = uid;
    }


    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
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
            hashMap.put("height", String.valueOf(height));
        }

        if (weight != null) {
            hashMap.put("weight", String.valueOf(weight));
        }

        if(age != null){
            hashMap.put("age", String.valueOf(age));
        }

        if(sexOption != null){
            hashMap.put("sexOption" , String.valueOf(sexOption));
        }

        return hashMap;
    }
}
