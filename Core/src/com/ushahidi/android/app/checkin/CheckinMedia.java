
package com.ushahidi.android.app.checkin;

public class CheckinMedia {

    private int mediaId = 0;

    private int checkinId = 0;

    private String thumbnailLink = "";

    private String mediumLink = "";

    public CheckinMedia() {
        mediaId = 0;
        checkinId = 0;
        thumbnailLink = "";
        mediumLink = "";

    }
    
    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }
    
    public void setCheckinId(int checkinId) {
        this.checkinId = checkinId;
    }
    
    public void setThumbnailLink(String thumbnailLink){
        this.thumbnailLink = thumbnailLink;
    }
    
    public void setMediumLink(String mediumLink) {
        this.mediumLink = mediumLink;
    }
    
    public int getMediaId() {
        return this.mediaId;
    }
    
    public int getCheckinId() {
        return this.checkinId;
    }
    
    public String getThumbnailLink() {
        return this.thumbnailLink;
    }
    
    public String getMediumLink() {
        return this.mediumLink;
    }

}
