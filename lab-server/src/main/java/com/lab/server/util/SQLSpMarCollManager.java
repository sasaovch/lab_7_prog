package com.lab.server.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lab.common.data.AstartesCategory;
import com.lab.common.data.Chapter;
import com.lab.common.data.Coordinates;
import com.lab.common.data.SpaceMarine;
import com.lab.common.util.CollectionManager;
import com.lab.common.util.ResultStatusWorkWithColl;

public class SQLSpMarCollManager implements CollectionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLSpMarCollManager.class);
    private final Connection connectionDB;
    private SpaceMarineCollection spaceMarineCollection;

    public SQLSpMarCollManager(Connection connectionDB) throws SQLException {
        this.connectionDB = connectionDB;
        CreateSQLTable.createTypeCategory(connectionDB);
        CreateSQLTable.createCoordTable(connectionDB);
        CreateSQLTable.createChapterTable(connectionDB);
        CreateSQLTable.createSpMarTable(connectionDB);
        deSerialize();
    }

    private void deSerialize() throws SQLException {
        spaceMarineCollection = new SpaceMarineCollection();
        Statement stat = connectionDB.createStatement();
        ResultSet res = stat.executeQuery("SELECT * FROM spacemarine");
        while (res.next()) {
            spaceMarineCollection.addElement(mapRowToSpaceMarine(res));
        }
        LOGGER.info("SpaceMarine collection has been created.");
    }

    private SpaceMarine mapRowToSpaceMarine(ResultSet res) throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        SpaceMarine newSpacMar = new SpaceMarine();

        newSpacMar.setName(res.getString("name"));
        newSpacMar.setID(res.getLong("id"));
        newSpacMar.setTime(LocalDateTime.parse(res.getString("creating_date_time"), formatter));
        newSpacMar.setLoyal(res.getString("loyal") == null ? null : Boolean.parseBoolean(res.getString("loyal")));
        newSpacMar.setHealth(Integer.parseInt(res.getString("health")));
        newSpacMar.setHeartCount(Integer.parseInt(res.getString("heart_count")));
        newSpacMar.setCategory(AstartesCategory.valueOf(res.getString("astartes_category").toUpperCase()));

        PreparedStatement prepStat;
        if (Objects.nonNull(res.getString("chapter"))) {
            String getChapter = "SELECT * FROM chapter WHERE id=?";
            prepStat = connectionDB.prepareStatement(getChapter);
            prepStat.setLong(1, res.getLong("chapter"));
            ResultSet resChapter = prepStat.executeQuery();
            resChapter.next();
            newSpacMar.setChapter(new Chapter(resChapter.getLong("id"),
                                    resChapter.getString("name"),
                                    resChapter.getString("parent_legion"),
                                    resChapter.getInt("marines_count"),
                                    resChapter.getString("world")));
        }

        String getCoordinates = "SELECT * FROM coordinates WHERE id=?";
        prepStat = connectionDB.prepareStatement(getCoordinates);
        prepStat.setInt(1, res.getInt("coordinates"));
        ResultSet resCoord = prepStat.executeQuery();
        resCoord.next();
        newSpacMar.setCoordinates(new Coordinates(resCoord.getLong("id"), resCoord.getDouble("x"), resCoord.getLong("y")));
        newSpacMar.setOwnerName(res.getString("owner_name"));
        return newSpacMar;
    }

    private void prepareStatSpMar(PreparedStatement stat, SpaceMarine spaceMarine) throws SQLException {
        final int indexNameSpMar = 1;
        final int indexTime = 2;
        final int indexIdCoord = 3;
        final int indexHealth = 4;
        final int indexHeartCount = 5;
        final int indexLoyal = 6;
        final int indexCategory = 7;
        final int indexIdChapter = 8;
        final int indexOwnerName = 9;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        stat.setString(indexNameSpMar, spaceMarine.getName());
        stat.setTimestamp(indexTime, Timestamp.valueOf(spaceMarine.getCreationDateTime().format(formatter)));
        stat.setLong(indexIdCoord, spaceMarine.getCoordinates().getId());
        stat.setInt(indexHealth, spaceMarine.getHealth());
        stat.setInt(indexHeartCount, spaceMarine.getHeartCount());
        if (Objects.isNull(spaceMarine.getLoyal())) {
            stat.setNull(indexLoyal, Types.NULL);
        } else {
            stat.setBoolean(indexLoyal, spaceMarine.getLoyal());
        }
        stat.setObject(indexCategory, spaceMarine.getCategory().name().toLowerCase(), Types.OTHER);
        if (Objects.isNull(spaceMarine.getChapter())) {
            stat.setNull(indexIdChapter, Types.NULL);
        } else {
            stat.setLong(indexIdChapter, spaceMarine.getChapter().getId());
        }
        stat.setString(indexOwnerName, spaceMarine.getOwnerName());
    }

    private void prepareStatCoord(PreparedStatement stat, Coordinates coordinates) throws SQLException {
        final int indexX = 1;
        final int indexY = 2;
        stat.setDouble(indexX, coordinates.getX());
        stat.setLong(indexY, coordinates.getY());
    }

    private void prepareStatChapt(PreparedStatement stat, Chapter chapter) throws SQLException {
        final int indexNameChap = 1;
        final int indexParentLeg = 2;
        final int indexMarinesCount = 3;
        final int indexWorld = 4;
        stat.setString(indexNameChap, chapter.getName());
        stat.setString(indexParentLeg, chapter.getParentLegion());
        stat.setLong(indexMarinesCount, chapter.getMarinesCount());
        stat.setString(indexWorld, chapter.getWorld());
    }

    @Override
    public ResultStatusWorkWithColl addElement(SpaceMarine spMar) {
        String insertSpaceMar = "INSERT INTO spacemarine VALUES ("
                     + "    default,?,?,?,?,?,?,?::astartes_category,?,?) RETURNING id";
        String insertCoord = "INSERT INTO coordinates VALUES ("
                     + "    default,?,?) RETURNING id";
        String insertChapter = "INSERT INTO chapter VALUES ("
                     + "    default,?,?,?,?) RETURNING id";
        if (spaceMarineCollection.checkContains(spMar)) {
            return ResultStatusWorkWithColl.False;
        }
        try (
            PreparedStatement statCoord = connectionDB.prepareStatement(insertCoord);
            PreparedStatement statChapter = connectionDB.prepareStatement(insertChapter);
            PreparedStatement statSpMar = connectionDB.prepareStatement(insertSpaceMar);
        ) {
            prepareStatCoord(statCoord, spMar.getCoordinates());
            ResultSet resCoord = statCoord.executeQuery();
            resCoord.next();
            spMar.getCoordinates().setId(resCoord.getLong("id"));
            if (Objects.nonNull(spMar.getChapter())) {
                prepareStatChapt(statChapter, spMar.getChapter());
                ResultSet resChapt = statChapter.executeQuery();
                resChapt.next();
                spMar.getChapter().setId(resChapt.getLong("id"));
            }
            prepareStatSpMar(statSpMar, spMar);
            ResultSet resSpMar = statSpMar.executeQuery();
            resSpMar.next();
            spMar.setID(resSpMar.getLong("id"));
            spaceMarineCollection.addElement(spMar);
            return ResultStatusWorkWithColl.True;
        } catch (SQLException e) {
            LOGGER.error("Failed to insert element into spacemarine database", e);
            return ResultStatusWorkWithColl.Error;
        }
    }

    @Override
    public ResultStatusWorkWithColl addIfMin(SpaceMarine spMar) {
        if (getSize() == 0) {
            return addElement(spMar);
        }
        if (spMar.compareTo(getMinElement()) < 0) {
            return addElement(spMar);
        }
        return ResultStatusWorkWithColl.False;
    }

    @Override
    public ResultStatusWorkWithColl clearCollection(String userName) {
        String clearStat = "DELETE FROM spacemarine WHERE owner_name=? RETURNING id";
        try (PreparedStatement stat = connectionDB.prepareStatement(clearStat)) {
            stat.setString(1, userName);
            ResultSet res = stat.executeQuery();
            while (res.next()) {
                spaceMarineCollection.removeById(res.getLong("id"));
            }
            return ResultStatusWorkWithColl.True;
        } catch (SQLException e) {
            LOGGER.error("Failed to clear table with spacemarine", e);
            return ResultStatusWorkWithColl.Error;
        }
    }

    @Override
    public <T> int countBySomeThing(Function<SpaceMarine, T> getter, T value) {
        return spaceMarineCollection.countBySomeThing(getter, value);
    }

    @Override
    public int getSize() {
        return spaceMarineCollection.getSize();
    }

    @Override
    public <R> Map<R, List<SpaceMarine>> groupByField(Function<SpaceMarine, R> funct) {
        return spaceMarineCollection.groupByField(funct);
    }

    @Override
    public LocalDateTime getTime() {
        return spaceMarineCollection.getTime();
    }

    @Override
    public ArrayList<SpaceMarine> sortCollection() {
        return spaceMarineCollection.sortCollection();
    }

    private ResultStatusWorkWithColl removeById(Long id) {
        String removeSpacMar = "DELETE FROM spacemarine WHERE id=?";
        String removeCoord = "DELETE FROM coordinates WHERE id=?";
        String removeChapter = "DELETE FROM chapter WHERE id=?";
        try {
            PreparedStatement statCoord = connectionDB.prepareStatement(removeCoord);
            PreparedStatement statChapter = connectionDB.prepareStatement(removeChapter);
            PreparedStatement statSpMar = connectionDB.prepareStatement(removeSpacMar);
            statSpMar.setLong(1, id);
            statCoord.setLong(1, spaceMarineCollection.findByID(id).getCoordinates().getId());
            if (statSpMar.executeUpdate() > 0) {
                statCoord.executeUpdate();
                if (Objects.nonNull(spaceMarineCollection.findByID(id).getChapter())) {
                    statChapter.setLong(1, spaceMarineCollection.findByID(id).getChapter().getId());
                    statChapter.executeUpdate();
                }
                spaceMarineCollection.removeById(id);
            }
            return ResultStatusWorkWithColl.True;
        } catch (SQLException e) {
            LOGGER.error("Failed to delete spacemarine", e);
            return ResultStatusWorkWithColl.Error;
        }
    }

    @Override
    public SpaceMarine findByID(Long id) {
        return spaceMarineCollection.findByID(id);
    }

    @Override
    public ResultStatusWorkWithColl updateSpaceMarine(SpaceMarine newMarine, Long id) {
        final int indexOfCoordId = 3;
        final int indexOfSpMarId = 9;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String updSpaceMar = createUpdateQuerySpMar();
        String updCoord = createUpdateQueryCoord();
        String updChapter = createUpdateQueryChapter();
        try (
            PreparedStatement getSpMarData = connectionDB.prepareStatement("SELECT chapter, coordinates, creating_date_time, owner_name FROM spacemarine WHERE id=?");
            PreparedStatement statCoord = connectionDB.prepareStatement(updCoord);
            PreparedStatement statChapter = connectionDB.prepareStatement(updChapter);
            PreparedStatement statSpMar = connectionDB.prepareStatement(updSpaceMar);
        ) {
            getSpMarData.setLong(1, id);
            ResultSet dataOfOldSpMar = getSpMarData.executeQuery();
            if (!checkAbilityToUpdate(dataOfOldSpMar, newMarine)) {
                return ResultStatusWorkWithColl.False;
            }
            prepareStatCoord(statCoord, newMarine.getCoordinates());
            // update old coordinates
            statCoord.setLong(indexOfCoordId, dataOfOldSpMar.getLong("coordinates"));
            statCoord.executeUpdate();
            // set up new id coordinates
            newMarine.getCoordinates().setId(dataOfOldSpMar.getLong("coordinates"));
            // check chapter is not null in new spaceMarine
            setChapterForUpdate(dataOfOldSpMar, newMarine, statChapter);
            newMarine.setTime(LocalDateTime.parse(dataOfOldSpMar.getString("creating_date_time"), formatter));
            prepareStatSpMar(statSpMar, newMarine);
            statSpMar.setLong(indexOfSpMarId, id);
            int count = statSpMar.executeUpdate();
            if (count > 0) {
                spaceMarineCollection.updateSpaceMarine(newMarine, id);
            }
            return ResultStatusWorkWithColl.True;
        } catch (SQLException e) {
            LOGGER.error("Failed to update spacemarine", e);
            return ResultStatusWorkWithColl.Error;
        }
    }

    private boolean checkAbilityToUpdate(ResultSet dataOfOldSpMar, SpaceMarine newMarine) throws SQLException {
        if (!dataOfOldSpMar.next()) {
            return false;
        }
        if (!dataOfOldSpMar.getString("owner_name").equals(newMarine.getOwnerName())) {
            return false;
        }
        return true;
    }

    private void setChapterForUpdate(ResultSet dataOfOldSpMar, SpaceMarine newMarine, PreparedStatement preparChStatement) throws SQLException {
        final int indexOfChapterId = 5;
        if (Objects.nonNull(newMarine.getChapter())) {
            // check if chapter in old spaceMarine is null
            if (dataOfOldSpMar.getLong("chapter") == 0L) {
                String addChapter = "INSERT INTO chapter VALUES ("
                + "    default,?,?,?,?) RETURNING id";
                PreparedStatement statAddChapter = connectionDB.prepareStatement(addChapter);
                prepareStatChapt(statAddChapter, newMarine.getChapter());
                ResultSet addChapterRes = statAddChapter.executeQuery();
                addChapterRes.next();
                newMarine.getChapter().setId(addChapterRes.getLong("id"));
            } else {
                prepareStatChapt(preparChStatement, newMarine.getChapter());
                preparChStatement.setLong(indexOfChapterId, dataOfOldSpMar.getLong("chapter"));
                preparChStatement.executeUpdate();
                newMarine.getChapter().setId(dataOfOldSpMar.getLong("chapter"));
            }
        }
    }

    private String createUpdateQuerySpMar() {
        return "UPDATE spacemarine SET "
        + "name=?, "
        + "creating_date_time=?, "
        + "coordinates=?, "
        + "health=?, "
        + "heart_count=?, "
        + "loyal=?, "
        + "astartes_category=?::astartes_category, "
        + "chapter=? "
        + "WHERE id=? ";
    }

    private String createUpdateQueryCoord() {
        return "UPDATE coordinates SET "
        + "x=?, "
        + "y=? "
        + "WHERE id=? ";
    }

    private String createUpdateQueryChapter() {
        return "UPDATE chapter SET "
        + "name=?, "
        + "parent_legion=?, "
        + "marines_count=?, "
        + "world=? "
        + "WHERE id=? ";
    }

    @Override
    public List<SpaceMarine> sortByCoordinates() {
        return spaceMarineCollection.sortByCoordinates();
    }

    @Override
    public SpaceMarine getMinElement() {
        return spaceMarineCollection.getMinElement();
    }

    @Override
    public ResultStatusWorkWithColl removeIf(Predicate<SpaceMarine> predicate) {
        try {
            Set<SpaceMarine> removeSet = spaceMarineCollection.getSpMarIf(predicate);
            if (removeSet.size() == 0) {
                return ResultStatusWorkWithColl.False;
            }
            for (SpaceMarine removeSpMar: removeSet) {
                removeById(removeSpMar.getID());
            }
            return ResultStatusWorkWithColl.True;
        } catch (Exception e) {
            LOGGER.error("Failed to remove spacemarine", e);
            return ResultStatusWorkWithColl.Error;
        }
    }
}
