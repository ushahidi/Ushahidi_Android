package com.ushahidi.android.app.data;

public class UsersData {
    private int userId = 0;
    private String userName = "";
    private String color = "";
    
    public UsersData() {
        
    }
    
    public void setId(int id) {
        this.userId = id;
    }
    
    public int getId() {
        return userId;
    }
    
    public void setUserName( String username ) {
        this.userName = username;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public void setColor( String color) {
        this.color = color;
    }
    
    public String getColor() {
        return this.color;
    }
}
