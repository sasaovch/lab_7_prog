package com.lab.common.data;

import java.io.Serializable;
import java.util.Objects;


public class Coordinates implements Serializable, Comparable<Coordinates> {
    private static final long serialVersionUID = -6025385286863260340L;
    private Long id;
    private double x;
    private Long y; //Поле не может быть null

    public Coordinates(Long id, double x, Long y) {
        setX(x);
        setY(y);
        setId(id);
    }

    public Coordinates(double x, Long y) {
        setX(x);
        setY(y);
    }

    public Coordinates() {
    }

    public Long getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    public void setY(Long y) {
        this.y = y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Coordinates: X - " + x + " | Y - " + y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Coordinates compCoor = (Coordinates) object;
        return (Objects.equals(x, compCoor.getX())) && (Objects.equals(y, compCoor.getY()));
    }

    @Override
    public int compareTo(Coordinates o) {
        if (x == o.getX()) {
            return Math.toIntExact(y - o.getY());
        } else {
            double res = x - o.getX();
            int i = (int) res;
            return i;
        }
    }
}
