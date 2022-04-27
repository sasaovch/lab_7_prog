package com.lab.common.data;

import java.io.Serializable;
import java.util.Objects;


public class Chapter implements Serializable {
    private static final long serialVersionUID = -890751278908056646L;
    private Long id;
    private String name; //Поле не может быть null, Строка не может быть пустой
    private String parentLegion;
    private long marinesCount; //Значение поля должно быть больше 0, Максимальное значение поля: 1000
    private String world; //Поле не может быть null

    public Chapter(Long id, String name, String parentLegion, long marinesCount, String world) {
        this.setId(id);
        this.setName(name);
        this.setParentLegion(parentLegion);
        this.setMarinesCount(marinesCount);
        this.setWorld(world);
    }

    public Chapter(String name, String parentLegion, long marinesCount, String world) {
        this.setName(name);
        this.setParentLegion(parentLegion);
        this.setMarinesCount(marinesCount);
        this.setWorld(world);
    }

    public Chapter() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMarinesCount(long marinesCount) {
        this.marinesCount = marinesCount;
    }

    public void setParentLegion(String parentLegion) {
        this.parentLegion = parentLegion;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public String getWorld() {
        return world;
    }

    public long getMarinesCount() {
        return marinesCount;
    }

    public String getParentLegion() {
        return parentLegion;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "   Name: " + name + "\n   ParentLegion: " + parentLegion + "\n   MarinesCount: " + marinesCount
                + "\n   World: " + world;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parentLegion, marinesCount, world);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Chapter compChap = (Chapter) object;
        return (Objects.equals(name, compChap.getName())) && (Objects.equals(parentLegion, compChap.getParentLegion()))
            && (Objects.equals(marinesCount, compChap.getMarinesCount())) && (Objects.equals(world, compChap.getWorld()));
    }
}
