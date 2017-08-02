package com.rollup.journey.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zq on 2017/2/7.
 */

public class GsonformatTest implements Parcelable {

    private GsonformatTest() {
    }

    private static class newInstance{
        private static GsonformatTest test = new GsonformatTest();
    }

    public static GsonformatTest getInstant(){
        return newInstance.test;
    }

    /**
     * name : 王五
     * gender : man
     * age : 15
     * height : 140cm
     * addr : {"province":"fujian","city":"quanzhou","code":"300000"}
     * hobby : [{"name":"billiards","code":"1"},{"name":"computerGame","code":"2"}]
     */



    private String name;
    private String gender;
    private int age;
    private String height;
    private AddrBean addr;
    private List<HobbyBean> hobby;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public AddrBean getAddr() {
        return addr;
    }

    public void setAddr(AddrBean addr) {
        this.addr = addr;
    }

    public List<HobbyBean> getHobby() {
        return hobby;
    }

    public void setHobby(List<HobbyBean> hobby) {
        this.hobby = hobby;
    }

    public static class AddrBean implements Parcelable {
        /**
         * province : fujian
         * city : quanzhou
         * code : 300000
         */

        private String province;
        private String city;
        private String code;

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.province);
            dest.writeString(this.city);
            dest.writeString(this.code);
        }

        public AddrBean() {
        }

        protected AddrBean(Parcel in) {
            this.province = in.readString();
            this.city = in.readString();
            this.code = in.readString();
        }

        public static final Creator<AddrBean> CREATOR = new Creator<AddrBean>() {
            @Override
            public AddrBean createFromParcel(Parcel source) {
                return new AddrBean(source);
            }

            @Override
            public AddrBean[] newArray(int size) {
                return new AddrBean[size];
            }
        };
    }

    public static class HobbyBean {
        /**
         * name : billiards
         * code : 1
         */

        private String name;
        private String code;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.gender);
        dest.writeInt(this.age);
        dest.writeString(this.height);
        dest.writeParcelable(this.addr, flags);
        dest.writeList(this.hobby);
    }

    protected GsonformatTest(Parcel in) {
        this.name = in.readString();
        this.gender = in.readString();
        this.age = in.readInt();
        this.height = in.readString();
        this.addr = in.readParcelable(AddrBean.class.getClassLoader());
        this.hobby = new ArrayList<HobbyBean>();
        in.readList(this.hobby, HobbyBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<GsonformatTest> CREATOR = new Parcelable.Creator<GsonformatTest>() {
        @Override
        public GsonformatTest createFromParcel(Parcel source) {
            return new GsonformatTest(source);
        }

        @Override
        public GsonformatTest[] newArray(int size) {
            return new GsonformatTest[size];
        }
    };
}
