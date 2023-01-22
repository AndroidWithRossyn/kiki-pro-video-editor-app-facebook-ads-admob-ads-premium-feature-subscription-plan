package com.ridercode.provideo_editor.model;

public class GridIcons {
    private String label;
    private int icon;
    private boolean isPro;

    public GridIcons(String label, int icon, boolean isPro) {
        this.label = label;
        this.icon = icon;
        this.isPro = isPro;
    }

    public boolean isPro() {
        return isPro;
    }

    public void setPro(boolean pro) {
        isPro = pro;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
