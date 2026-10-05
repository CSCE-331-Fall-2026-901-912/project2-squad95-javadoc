package com.app;
// Source - https://stackoverflow.com/a/51457200
// Posted by Slaw
// Retrieved 2026-10-02, License - CC BY-SA 4.0

import javafx.beans.NamedArg;

public class Cashier {

    private final String name;
    private boolean housePet;

    public Cashier(@NamedArg("name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isHousePet() {
        return housePet;
    }

    public void setHousePet(boolean housePet) {
        this.housePet = housePet;
    }

    @Override
    public String toString() {
        return "Animal[name=" + name + ", housePet=" + housePet + "]";
    }

}
