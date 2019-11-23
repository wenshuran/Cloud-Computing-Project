package controller;

import java.util.ArrayList;

public class ShareEvent {
    private String from;
    private String to;
    private ArrayList<VueTable> tables;

    public String getFrom() {
        return from;
    }

    public ArrayList<VueTable> getTables() {
        return tables;
    }

    public void setTables(ArrayList<VueTable> tables) {
        this.tables = tables;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
