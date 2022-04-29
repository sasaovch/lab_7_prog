package com.lab.server.util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.lab.common.data.SpaceMarine;


public class SpaceMarineCollection {
    private final Collection<SpaceMarine> spaceMarineSet;
    private final LocalDateTime initializationTime;
    private final Collection<Long> usedID;

    public SpaceMarineCollection() {
        spaceMarineSet = Collections.synchronizedSet(new HashSet<>());
        initializationTime = LocalDateTime.now();
        usedID = Collections.synchronizedSet(new HashSet<Long>());
    }

    public boolean addElement(SpaceMarine spaceMarine) {
        if (Objects.isNull(spaceMarine.getID())) {
            if (usedID.isEmpty()) {
                spaceMarine.setID(1L);
                usedID.add(1L);
            } else {
                spaceMarine.setID(getLastId() + 1);
                usedID.add(getLastId() + 1);
            }
        } else if (usedID.contains(spaceMarine.getID())) {
            spaceMarine.setID(getLastId() + 1);
            usedID.add(getLastId() + 1);
        } else {
            usedID.add(spaceMarine.getID());
        }
        return spaceMarineSet.add(spaceMarine);
    }

    public boolean addIfMin(SpaceMarine addSpaceMarine) {
        if (spaceMarineSet.size() == 0) {
            return addElement(addSpaceMarine);
        } else {
            if (addSpaceMarine.compareTo(getMinElement()) < 0) {
                return addElement(addSpaceMarine);
            } else {
                return false;
            }
        }
    }

    public SpaceMarine getMinElement() {
        return spaceMarineSet.stream().min((o1, o2) -> o1.compareTo(o2)).orElse(new SpaceMarine());
    }

    public boolean removeElement(SpaceMarine spaceMarine) {
        usedID.remove(spaceMarine.getID());
        return spaceMarineSet.remove(spaceMarine);
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

    public Long getLastId() {
        return usedID.stream().max((o1, o2) -> {
            Long result = o1 - o2;
            return result.intValue();
        }).get();
    }

    public boolean clearCollection() {
        usedID.clear();
        spaceMarineSet.clear();
        return true;
    }

    public ArrayList<SpaceMarine> sortCollection() {
        ArrayList<SpaceMarine> list = new ArrayList<SpaceMarine>(spaceMarineSet);
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
        SpaceMarine oldMarine = findByID(id);
        newMarine.setID(oldMarine.getID());
        newMarine.setTime(oldMarine.getCreationDateTime());
        return removeElement(oldMarine) && addElement(newMarine);
    }

    public List<SpaceMarine> sortByCoordinates() {
        return spaceMarineSet.stream().sorted(Comparator.comparing(SpaceMarine::getCoordinates)).collect(Collectors.toList());
    }

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
