package io.github.linead.nametags.domain;

import java.util.Date;

public class Attendee {

    private String id;
    private String name;
    private Date joined;
    private String pictureUrl;
    private String pictureData;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getJoined() {
        return joined;
    }

    public void setJoined(Date joined) {
        this.joined = joined;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getPictureData() {
        return pictureData;
    }

    public void setPictureData(String pictureData) {
        this.pictureData = pictureData;
    }

    public String getPhoto() {
        if(this.pictureUrl != null) {
            return String.format("[imageUrl:%s]", pictureUrl);
        } else if (this.pictureData != null) {
            return String.format("image:base64:%s", pictureData);
        } else {
            String default_image = "http://photos3.meetupstatic.com/photos/event/7/3/a/2/global_414329602.jpeg";
            return String.format("[imageUrl:%s]", default_image);

        }
    }
}
