package com.healthai.healthcare;

import java.util.List;

public class HealthcareFacilityDto {

    private Long id;
    private String name;
    private String type; // Hospital, Clinic, Trauma Center, Diagnostic Lab
    private List<String> specialties;
    private String address;
    private String city;
    private String phone;
    private Double rating;
    private Double latitude;
    private Double longitude;
    private Boolean emergency24x7;
    private String openHours;
    private Double distanceKm;

    public HealthcareFacilityDto() {}

    public HealthcareFacilityDto(Long id, String name, String type, List<String> specialties,
                                 String address, String city, String phone, Double rating,
                                 Double latitude, Double longitude, Boolean emergency24x7,
                                 String openHours, Double distanceKm) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.specialties = specialties;
        this.address = address;
        this.city = city;
        this.phone = phone;
        this.rating = rating;
        this.latitude = latitude;
        this.longitude = longitude;
        this.emergency24x7 = emergency24x7;
        this.openHours = openHours;
        this.distanceKm = distanceKm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(List<String> specialties) {
        this.specialties = specialties;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getEmergency24x7() {
        return emergency24x7;
    }

    public void setEmergency24x7(Boolean emergency24x7) {
        this.emergency24x7 = emergency24x7;
    }

    public String getOpenHours() {
        return openHours;
    }

    public void setOpenHours(String openHours) {
        this.openHours = openHours;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }
}
