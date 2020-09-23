package com.bestowing.restaurant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class StoreInfo implements Serializable {
    private String placeName;
    private String id;
    private String phone;
    private String road_address_name;
    private ArrayList<String> photos;
    private Map<String, Boolean> like;
    private Long likeNum;
    private double rating;
    private double x;
    private double y;
}
