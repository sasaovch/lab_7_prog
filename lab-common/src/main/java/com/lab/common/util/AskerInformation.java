package com.lab.common.util;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import com.lab.common.data.AstartesCategory;
import com.lab.common.data.Chapter;
import com.lab.common.data.Coordinates;
import com.lab.common.data.SpaceMarine;
import com.lab.common.exception.IncorrectData;
import com.lab.common.exception.IncorrectDataOfFileException;

public class AskerInformation {
    private static final int MIN_HEALTH = 1;
    private static final int MIN_HEARTCOUNT = 1;
    private static final int MAX_HEARTCOUNT = 3;
    private static final int MIN_MARINESCOUNT = 0;
    private static final int MAX_MARINESCOUNT = 1000;


    private AskerInformation() {
    }

    public static String askName(IOManager ioManager) throws IOException, IncorrectDataOfFileException {
        String name = asker(arg -> arg,
                         arg -> ((String) arg).length() > 0,
                         "Enter name (String)",
                         "The string must not be empty.", false,
                         ioManager.getFileMode(),
                         ioManager);
        return name;
    }

    public static Coordinates askCoordinates(IOManager ioManager) throws IOException, IncorrectDataOfFileException, IncorrectData {
        double coordinateX = asker(Double::parseDouble,
                                arg -> true,
                                "Enter coordinates: X (double)",
                                "Incorrect input. X must be double.",
                                false,
                                ioManager.getFileMode(),
                                ioManager);
        Long coordinateY = asker(Long::parseLong,
                                arg -> true,
                                "Enter coordinates: Y (Long)",
                                "Incorrect input. Y must be Long and not null.",
                                false,
                                ioManager.getFileMode(),
                                ioManager);
        Coordinates cor = new Coordinates(coordinateX, coordinateY);
        return cor;
    }

    public static Integer askHealth(IOManager ioManager) throws IOException, IncorrectDataOfFileException {
        Integer health = asker(Integer::parseInt,
                           arg -> ((Integer) arg) >= MIN_HEALTH,
                           "Enter the level of health (Integer)",
                           "Health must be Integer, not null and greater than zero.",
                           false,
                           ioManager.getFileMode(),
                           ioManager);
        return health;
    }

    public static Integer askHeartCount(IOManager ioManager) throws IOException, IncorrectDataOfFileException {
        Integer heartCount = asker(Integer::parseInt,
                               arg -> MIN_HEARTCOUNT <= ((Integer) arg) && arg <= MAX_HEARTCOUNT,
                               "Enter heart count: from 1 to 3 (Integer)",
                               "Heartcount must be form 1 to 3 (Integer)",
                               false,
                               ioManager.getFileMode(),
                               ioManager);
        return heartCount;
    }

    public static Boolean askLoyal(IOManager ioManager) throws IOException, IncorrectDataOfFileException {
        Boolean loyal = asker(arg -> {
                    if (!(arg.equals("false") || arg.equals("true") || arg.equals(""))) {
                        throw new NumberFormatException();
                    }
                    return Boolean.parseBoolean(arg);
                                    },
                              arg -> true,
                              "Enter loyal: true, false or null - empty line.",
                              "Incorrect input - loyal is only true, false or null - empty line.",
                              true,
                              ioManager.getFileMode(),
                              ioManager);
        return loyal;
    }

    public static AstartesCategory askCategory(IOManager ioManager) throws IOException, IncorrectDataOfFileException {
        AstartesCategory category = asker(arg -> AstartesCategory.valueOf(arg.toUpperCase()),
                                 arg -> true,
                                 "Enter category: " + AstartesCategory.listOfCategory(),
                                 "The category is not in the list.",
                                 false,
                                 ioManager.getFileMode(),
                                 ioManager);
        return category;
    }

    public static Chapter askChapter(IOManager ioManager) throws IOException, IncorrectDataOfFileException, IncorrectData {
        String name = asker(arg -> arg, arg -> true, "Enter name of chapter, empty line if chapter is null",
                         "", true, ioManager.getFileMode(), ioManager);
            if (!Objects.equals(name, null)) {
                String parentLegion = asker(arg -> arg, arg -> true, "Enter parent Legion of chapter",
                                        "", false, ioManager.getFileMode(), ioManager);
                Long marinesCount = asker(Long::parseLong, arg -> MIN_MARINESCOUNT < ((Long) arg) && ((Long) arg) <= MAX_MARINESCOUNT,
                                     "Enter marines count of chapter: from 1 to 1000 (Integer)",
                                     "Marines count must be Integer, not null and from 1 to 1000.", false,
                                     ioManager.getFileMode(), ioManager);
                String world = asker(x -> x, arg -> ((String) arg).length() > 0,  "Enter name (String)",
                              "The string must not be empty.", false,
                              ioManager.getFileMode(), ioManager);
                Chapter chapter = new Chapter(name, parentLegion, marinesCount, world);
                return chapter;
            } else {
                return null;
            }
    }

    public static  SpaceMarine askMarine(IOManager ioManager) {
        try {
            String name = askName(ioManager);
            Coordinates coordinates = askCoordinates(ioManager);
            Integer health = askHealth(ioManager);
            Integer heartCount = askHeartCount(ioManager);
            Boolean loyal = askLoyal(ioManager);
            AstartesCategory category = askCategory(ioManager);
            Chapter chapter = askChapter(ioManager);
            SpaceMarine newSpMar = new SpaceMarine(name, coordinates, health, heartCount, loyal, category, chapter);
            newSpMar.setTime(LocalDateTime.now());
            return newSpMar;
        } catch (IncorrectDataOfFileException | IOException | IncorrectData e) {
            return null;
        }
    }

    public static int askTypeOfAuthin(IOManager ioManager) throws IOException, IncorrectDataOfFileException {
        return asker(Integer::parseInt, (s) -> (s == 0 || s == 1),"Enter '1' for sign in, '2' for sign up", "It's not correct", false, false, ioManager);
    }

    public static <T> T asker(Function<String, T> function,
                       Predicate<T> predicate,
                       String askField,
                       String wrongValue,
                       Boolean nullable,
                       Boolean fileMode,
                       IOManager ioManager) throws IOException, IncorrectDataOfFileException {
        String stringIn;
        T value;
        while (true) {
            ioManager.println(askField);
            ioManager.prompt();
            try {
                stringIn = ioManager.readLine().trim();
                if ("".equals(stringIn) && nullable) {
                    return null;
                }
                value = function.apply(stringIn);
            } catch (IllegalArgumentException e) {
                ioManager.printerr(wrongValue);
                if (fileMode) {
                    throw new IncorrectDataOfFileException();
                }
                continue;
            }
            if (predicate.test(value)) {
                return value;
            } else {
                ioManager.printerr(wrongValue);
                if (fileMode) {
                    throw new IncorrectDataOfFileException();
                }
            }
        }
    }
}
