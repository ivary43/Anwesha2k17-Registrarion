package com.anweshainfo.anwesha_registration.model;

/**
 * Created by mayank on 22/1/18.
 */

public class Participant {
    private String name;
    private String anwid;

    public Participant(String name, String anwid) {
        this.name = name;
        this.anwid = anwid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnwid() {
        return anwid;
    }

    public void setAnwid(String anwid) {
        this.anwid = anwid;
    }
}
