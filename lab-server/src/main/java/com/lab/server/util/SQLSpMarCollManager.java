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

public class SQLSpMarCollManager implements CollectionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLSpMarCollManager.class);
    private final Connection connectionDB;
    private SpaceMarineCollection spaceMarineCollection;

    public SQLSpMarCollManager(Connection connectionDB) throws SQLException {
        this.connectionDB = connectionDB;
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
            String getChapter = "SELECT * FROM chapter WHERE id=?;";
            prepStat = connectionDB.prepareStatement(getChapter);
            prepStat.setLong(1, res.getLong("chapter"));
            ResultSet resChapter = prepStat.executeQuery();
            resChapter.next();
            newSpacMar.setChapter(new Chapter(resChapter.getLong("id"), resChapter.getString("name"), resChapter.getString("parent_legion"), resChapter.getInt("marines_count"), resChapter.getString("world")));
        }
        String getCoordinates = "SELECT * FROM coordinates WHERE id=?;";
        prepStat = connectionDB.prepareStatement(getCoordinates);
        prepStat.setInt(1, res.getInt("coordinates"));
        ResultSet resCoord = prepStat.executeQuery();
        resCoord.next();
        newSpacMar.setCoordinates(new Coordinates(resCoord.getLong("id"), resCoord.getDouble("x"), resCoord.getLong("y")));
        newSpacMar.setOwnerName(res.getString("owner_name"));
        return newSpacMar;
    }

    private void prepareStatSpMar(PreparedStatement stat, SpaceMarine spaceMarine) throws SQLException {
        int indexColumn = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // name, time, coordinates, health, heart_count, loyal, astartes_category, chapter, owner_name
        stat.setString(indexColumn++, spaceMarine.getName());
        stat.setTimestamp(indexColumn++, Timestamp.valueOf(spaceMarine.getCreationDateTime().format(formatter)));
        stat.setLong(indexColumn++, spaceMarine.getCoordinates().getId());
        stat.setInt(indexColumn++, spaceMarine.getHealth());
        stat.setInt(indexColumn++, spaceMarine.getHeartCount());
        if (Objects.isNull(spaceMarine.getLoyal())) {
            stat.setNull(indexColumn++, Types.NULL);
        } else {
            stat.setBoolean(indexColumn++, spaceMarine.getLoyal());
        }
        stat.setObject(indexColumn++, spaceMarine.getCategory().name().toLowerCase(), Types.OTHER);
        if (Objects.isNull(spaceMarine.getChapter())) {
            stat.setNull(indexColumn++, Types.NULL);
        } else {
            stat.setLong(indexColumn++, spaceMarine.getChapter().getId());
        }
        stat.setString(indexColumn++, spaceMarine.getOwnerName());
    }

    private void prepareStatCoord(PreparedStatement stat, Coordinates coordinates) throws SQLException {
        int indexColumn = 1;
        // x, y
        stat.setDouble(indexColumn++, coordinates.getX());
        stat.setLong(indexColumn++, coordinates.getY());
    }

    private void prepareStatChapt(PreparedStatement stat, Chapter chapter) throws SQLException {
        int indexColumn = 1;
        // name, parent_legion, marines_count, world
        stat.setString(indexColumn++, chapter.getName());
        stat.setString(indexColumn++, chapter.getParentLegion());
        stat.setLong(indexColumn++, chapter.getMarinesCount());
        stat.setString(indexColumn++, chapter.getWorld());
    }

    @Override
    public boolean addElement(SpaceMarine spMar) {
        String insertSpaceMar = "INSERT INTO spacemarine VALUES ("
                     + "    default,?,?,?,?,?,?,?::astartes_category,?,?) RETURNING id"; // name, time, coordinates, health, heart_count, loyal, astartes_category, chapter, owner_name
        String insertCoord = "INSERT INTO coordinates VALUES ("
                     + "    default,?,?) RETURNING id"; // x, y
        String insertChapter = "INSERT INTO chapter VALUES ("
                     + "    default,?,?,?,?) RETURNING id"; // name, parent_legion, marines_count, world
        if (spaceMarineCollection.checkContains(spMar)) {
            return false;
        }
        try (
            PreparedStatement statCoord = connectionDB.prepareStatement(insertCoord);
            PreparedStatement statChapter = connectionDB.prepareStatement(insertChapter);
            PreparedStatement statSpMar = connectionDB.prepareStatement(insertSpaceMar);
        ) {
            //lock.lock();
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
            return true;
        } catch (SQLException e) {
            LOGGER.error("Failed to insert element into spacemarine database", e);
            return false;
        } finally {
            //lock.unlock();
        }
    }

    @Override
    public boolean addIfMin(SpaceMarine spMar) {
        if (getSize() == 0) {
            return addElement(spMar);
        }
        if (spMar.compareTo(getMinElement()) < 0) {
            return addElement(spMar);
        }
        return false;
    }

    @Override
    public boolean clearCollection() {
        try (Statement stat = connectionDB.createStatement()) {
            stat.execute("DELETE FROM spacemarine *");
            stat.execute("DELETE FROM coordinates *");
            stat.execute("DELETE FROM chapter *");
            spaceMarineCollection.clearCollection();
            return true;
        } catch (SQLException e) {
            //LOGGER.error("Failed to clear table", e);
            LOGGER.info("Failed to clear table");
            return false;
        } finally {
            //lock.unlock();
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

    @Override
    public boolean removeById(Long id) {
        String removeSpacMar = "DELETE FROM spacemarine WHERE id=?";
        String removeCoord = "DELETE FROM coordinates WHERE id=?";
        String removeChapter = "DELETE FROM chapter WHERE id=?";
        try {
            PreparedStatement statCoord = connectionDB.prepareStatement(removeCoord);
            PreparedStatement statChapter = connectionDB.prepareStatement(removeChapter);
            PreparedStatement statSpMar = connectionDB.prepareStatement(removeSpacMar);
            //lock.lock();
            statSpMar.setLong(1, id);
            statCoord.setLong(1, spaceMarineCollection.findByID(id).getCoordinates().getId());
            if (Objects.nonNull(spaceMarineCollection.findByID(id).getChapter())) {
                statChapter.setLong(1, spaceMarineCollection.findByID(id).getChapter().getId());
            }
            if (statSpMar.executeUpdate() > 0) {
                spaceMarineCollection.removeById(id);
                statChapter.executeQuery();
                statCoord.executeQuery();
            }
            return true;
        } catch (SQLException e) {
            //LOGGER.error("Failed to insert element into DB", e);
            LOGGER.info("Failed to inser element into DB");
            return false;
        } finally {
            //lock.unlock();
        }
    }

    @Override
    public SpaceMarine findByID(Long id) {
        return spaceMarineCollection.findByID(id);
    }

    @Override
    public boolean updateSpaceMarine(SpaceMarine newMarine, Long id) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // name, time, coordinates, health, heart_count, loyal, astartes_category, chapter, owner_name
        String updSpaceMar = "UPDATE spacemarine SET "
                     + "name=?, "
                     + "creating_date_time=?, " 
                     + "coordinates=?, "
                     + "health=?, "
                     + "heart_count=?, "
                     + "loyal=?, "
                     + "astartes_category=?::astartes_category, "
                     + "chapter=? "
                     + "WHERE id=? ";
        String updCoord = "UPDATE coordinates SET "
                     + "x=?, "
                     + "y=? "
                     + "WHERE id=? ";
        String updChapter = "UPDATE chapter SET "
                     + "name=?, "
                     + "parent_legion=?, "
                     + "marines_count=?, "
                     + "world=? "
                     + "WHERE id=? ";
        try (
            PreparedStatement getSpMarData = connectionDB.prepareStatement("SELECT chapter, coordinates, creating_date_time, owner_name FROM spacemarine WHERE id=?");
            PreparedStatement statCoord = connectionDB.prepareStatement(updCoord);
            PreparedStatement statChapter = connectionDB.prepareStatement(updChapter);
            PreparedStatement statSpMar = connectionDB.prepareStatement(updSpaceMar);
        ) {
            //lock.lock();
            getSpMarData.setLong(1, id);
            ResultSet dataOfOldSpMar = getSpMarData.executeQuery();
            if (!dataOfOldSpMar.next()) {
                return false;
            };
            if (!dataOfOldSpMar.getString("owner_name").equals(newMarine.getOwnerName())) {
                return false;
            }
            prepareStatCoord(statCoord, newMarine.getCoordinates());
            // update old coordinates
            statCoord.setLong(3, dataOfOldSpMar.getLong("coordinates"));
            statCoord.executeUpdate();
            // set up new id coordinates
            newMarine.getCoordinates().setId(dataOfOldSpMar.getLong("coordinates"));
            // check chapter is not null in new spaceMarine
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
                    prepareStatChapt(statChapter, newMarine.getChapter());
                    statChapter.setLong(5, dataOfOldSpMar.getLong("chapter"));
                    statChapter.executeUpdate();
                    newMarine.getChapter().setId(dataOfOldSpMar.getLong("chapter"));
                }
            }
            newMarine.setTime(LocalDateTime.parse(dataOfOldSpMar.getString("creating_date_time"), formatter));
            prepareStatSpMar(statSpMar, newMarine);
            statSpMar.setLong(9, id);
            int count = statSpMar.executeUpdate();
            if (count > 0) {
                spaceMarineCollection.updateSpaceMarine(newMarine, id);
            }
            return true;
        } catch (SQLException e) {
            //LOGGER.error("Failed to insert element into DB", e);
            LOGGER.info("Failed to update element");
            e.printStackTrace();
            return false;
        } finally {
            //lock.unlock();
        }
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
    public boolean removeIf(Predicate<SpaceMarine> predicate) {
        try {
            Set<SpaceMarine> removeSet = spaceMarineCollection.getSpMarIf(predicate);
            if (removeSet.size() == 0) {
                return false;
            }
            for (SpaceMarine removeSpMar: removeSet) {
                removeById(removeSpMar.getID());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
