package com.tryouts.courierapplication.items;

public class User {

    private String mEmail;
    private String mUid;
    private String mName;
    private String mPhone;
    private String mRole;
    private String mInstanceId;

    public User() {
    }

    public User(String email) {
        this.mEmail = email;
    }

    public User(String email, String role) {
        this.mEmail = email;
        this.mRole = role;
    }

    public String getEmail() {
        return mEmail;
    }

     public String getRole() {
       return mRole;
     }

    public void setEmail(String email) {
        mEmail = email;
    }

      public void setRole(String role) {
          mRole = role;
      }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public String getInstanceId() {
        return mInstanceId;
    }

    public void setInstanceId(String instanceId) {
        mInstanceId = instanceId;
    }
}