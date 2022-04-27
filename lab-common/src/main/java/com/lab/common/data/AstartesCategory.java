package com.lab.common.data;

public enum AstartesCategory {
    AGGRESSOR("aggressor"),
    INCEPTOR("inceptor"),
    TACTICAL("tactical"),
    CHAPLAIN("chaplain"),
    HELIX("helix");

    private final String name;

    private AstartesCategory(String name) {
        this.name = name;
    }

    public static String listOfCategory() {
        String listofCategory = "";
        for (AstartesCategory category : values()) {
            listofCategory += category.name() + ", ";
        }
        return listofCategory.substring(0, listofCategory.length() - 2);
    }

    @Override
    public String toString() {
        return name;
    }
}
