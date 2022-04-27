package com.lab.server.util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.lab.common.data.SpaceMarine;
import com.lab.common.util.CollectionManager;


public class SpaceMarineCollection implements CollectionManager {
    private final HashSet<SpaceMarine> spaceMarineSet;
    private final LocalDateTime initializationTime;
    private final TreeSet<Long> usedID;

    public SpaceMarineCollection() {
        spaceMarineSet = new HashSet<>();
        initializationTime = LocalDateTime.now();
        usedID = new TreeSet<>();
    }

    public SpaceMarineCollection(HashSet<SpaceMarine> spaceMarineSet) {
        this.spaceMarineSet = spaceMarineSet;
        initializationTime = LocalDateTime.now();
        usedID = new TreeSet<>();
    }

    public boolean addElement(SpaceMarine element) {
        if (Objects.equals(element.getID(), null)) {
            if (usedID.isEmpty()) {
                element.setID(1L);
                usedID.add(1L);
            } else {
                element.setID(usedID.last() + 1);
                usedID.add(usedID.last() + 1);
            }
        } else if (usedID.contains(element.getID())) {
            element.setID(usedID.last() + 1);
            usedID.add(usedID.last() + 1);
        } else {
            usedID.add(element.getID());
        }
        return spaceMarineSet.add(element);
    }

    public boolean addIfMin(SpaceMarine addSpaceMarine) {
        if (spaceMarineSet.size() == 0) {
            return addElement(addSpaceMarine);
        } else {
            SpaceMarine minSpaceMarine = spaceMarineSet.stream().min((o1, o2) -> o1.compareTo(o2)).orElse(new SpaceMarine());
            if (addSpaceMarine.compareTo(minSpaceMarine) < 0) {
                return addElement(addSpaceMarine);
            } else {
                return false;
            }
        }
    }

    public SpaceMarine getMinElement() {
        return spaceMarineSet.stream().min((o1, o2) -> o1.compareTo(o2)).orElse(new SpaceMarine());
    }

    public boolean removeElement(SpaceMarine element) {
        usedID.remove(element.getID());
        return spaceMarineSet.remove(element);
    }

    public boolean removeIf(Predicate<SpaceMarine> condition) {
        Set<SpaceMarine> removeSet = spaceMarineSet.stream().filter(condition).collect(Collectors.toSet());
        if (removeSet.isEmpty()) {
            return false;
        }
        spaceMarineSet.removeAll(removeSet);
        usedID.removeAll(removeSet.stream().map(SpaceMarine::getID).collect(Collectors.toSet()));
        return true;
    }

    public LocalDateTime getTime() {
        return initializationTime;
    }

    public int getSize() {
        return spaceMarineSet.size();
    }

    public HashSet<SpaceMarine> getCollection() {
        return spaceMarineSet;
    }

    public Long getLastId() {
        return usedID.last();
    }

    public boolean clearCollection() {
        usedID.clear();
        spaceMarineSet.clear();
        return true;
    }

    public ArrayList<SpaceMarine> sortCollection() {
        ArrayList<SpaceMarine> list = new ArrayList<SpaceMarine>(getCollection());
        Collections.sort(list);
        return list;
    }

    public <R> int countBySomeThing(Function<SpaceMarine, R> getter, R value) {
        return Math.toIntExact(spaceMarineSet.stream().filter((spMar) -> Objects.equals(getter.apply(spMar), value)).count());
    }

    public <R> Map<R, List<SpaceMarine>> groupByField(Function<SpaceMarine, R> getter) {
        Map<R, List<SpaceMarine>> outputMap = new HashMap<>();
        outputMap = spaceMarineSet.stream().collect(Collectors.groupingBy(getter::apply));
        return outputMap;
    }


    public SpaceMarine findByID(Long id) {
        return (SpaceMarine) spaceMarineSet.stream().filter((spMar) -> id.equals(spMar.getID())).findFirst().orElse(null);
    }

    public boolean updateSpaceMarine(SpaceMarine newMarine, Long id) {
        newMarine.setID(id);
        return removeElement(findByID(id)) && addElement(newMarine);
    }

    public List<SpaceMarine> sortByCoordinates() {
        return spaceMarineSet.stream().sorted(Comparator.comparing(SpaceMarine::getCoordinates)).collect(Collectors.toList());
    }

    @Override
    public boolean removeById(Long id) {
        return spaceMarineSet.removeIf(spaceMarine -> spaceMarine.getID().equals(id));
    }

    public boolean checkContains(SpaceMarine spMar) {
        return spaceMarineSet.contains(spMar);
    }

    public Set<SpaceMarine> getSpMarIf(Predicate<SpaceMarine> predicate) {
        return spaceMarineSet.stream().filter(predicate).collect(Collectors.toSet());
    }
}
