package com.example.swiftcart;

public class Product {

    private String imgUrl;
    private String name;


    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Product(String imgUrl, String name) {
        this.imgUrl = imgUrl;
        this.name = name;
    }
}

