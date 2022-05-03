package com.lab.common.util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import com.lab.common.data.SpaceMarine;


public interface CollectionManager {
    ResultStatusWorkWithColl addElement(SpaceMarine spMar);
    ResultStatusWorkWithColl addIfMin(SpaceMarine spMar);
    ResultStatusWorkWithColl clearCollection(String userName);
    <T> int countBySomeThing(Function<SpaceMarine, T> getter, T value);
    int getSize();
    <R> Map<R, List<SpaceMarine>> groupByField(Function<SpaceMarine, R> funct);
    LocalDateTime getTime();
    ArrayList<SpaceMarine> sortCollection();
    SpaceMarine findByID(Long id);
    ResultStatusWorkWithColl updateSpaceMarine(SpaceMarine newMarine, Long id);
    List<SpaceMarine> sortByCoordinates();
    SpaceMarine getMinElement();
    ResultStatusWorkWithColl removeIf(Predicate<SpaceMarine> predicate);
}
