
package com.example.chattingprogrammingclient;
public class Data {

    String userId;
    String msg;
    // String time;

    // getter and setter
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

//    public String getTime() {
//        return time;
//    }
//
//    public void setTime(String time) {
//        this.time = time;
//    }

    // Constructor
    public Data(String userId, String msg) {
        this.userId = userId;
        this.msg = msg;
        // this.time = time;
    }

}
