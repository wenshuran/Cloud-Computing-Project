package controller;

public class Event {
    private String user;
    private String title;
    private String organiser;
    private String startTime;
    private String endTime;
    private String content;

    public Event(String user, String title, String organiser, String startTime, String endTime, String content) {
        this.user = user;
        this.title = title;
        this.organiser = organiser;
        this.startTime = startTime;
        this.endTime = endTime;
        this.content = content;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrganiser() {
        return organiser;
    }

    public void setOrganiser(String organiser) {
        this.organiser = organiser;
    }


    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
