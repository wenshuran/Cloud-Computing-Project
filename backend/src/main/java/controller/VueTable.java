package controller;

public class VueTable {
    private String user;
    private String title;
    private String Date;

    public VueTable(String user, String title, String date) {
        this.user = user;
        this.title = title;
        Date = date;
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

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
