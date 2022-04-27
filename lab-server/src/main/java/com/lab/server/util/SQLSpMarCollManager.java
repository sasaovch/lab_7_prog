package com.lab.server.util;

import java.io.IOException;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import java.util.logging.Logger;

import com.lab.common.data.AstartesCategory;
import com.lab.common.data.Chapter;
import com.lab.common.data.Coordinates;
import com.lab.common.data.SpaceMarine;
import com.lab.common.util.CollectionManager;

public class SQLSpMarCollManager implements CollectionManager {

    private static final Logger LOGGER = Logger.getLogger("SQLCollManager");//LoggerFactory.getLogger(CollectionManager.class);
    private final Connection connectionDB;
    private final SpaceMarineCollection spaceMarineCollection;

    public SQLSpMarCollManager(Connection connectionDB) throws SQLException, NumberFormatException, IOException {
        this.connectionDB = connectionDB;
        spaceMarineCollection = deSerialize(connectionDB);
    }

    public SpaceMarineCollection deSerialize(Connection connectionDB)  throws IOException, SQLException, NumberFormatException {
        SpaceMarineCollection spaceMarineCollection = new SpaceMarineCollection();
        Statement stat = connectionDB.createStatement();
        ResultSet res = stat.executeQuery("SELECT * FROM spacemarine");
        while (res.next()) {
            spaceMarineCollection.addElement(mapRowToSpaceMarine(res));
        }
        LOGGER.info("Collection has been created");
        return spaceMarineCollection;
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
        try {
            String getChapter = "SELECT * FROM chapter WHERE id=?;";
            String getCoordinates = "SELECT * FROM coordinates WHERE id=?;";
            PreparedStatement prepStat = connectionDB.prepareStatement(getChapter);
            if (Objects.nonNull(res.getString("chapter"))) {
                prepStat.setLong(1, res.getLong("chapter"));
                ResultSet resChapter = prepStat.executeQuery();
                resChapter.next();
                newSpacMar.setChapter(new Chapter(resChapter.getLong("id"), resChapter.getString("name"), resChapter.getString("parent_legion"), resChapter.getInt("marines_count"), resChapter.getString("world")));
            }
            prepStat = connectionDB.prepareStatement(getCoordinates);
            prepStat.setInt(1, res.getInt("coordinates"));
            ResultSet resCoord = prepStat.executeQuery();
            resCoord.next();
            newSpacMar.setCoordinates(new Coordinates(resCoord.getLong("id"), resCoord.getDouble("x"), resCoord.getLong("y")));
        } catch (SQLException e) {
            e.printStackTrace(); // think
        }
        return newSpacMar;
    }

    private void prepareStatSpMar(PreparedStatement stat, SpaceMarine spaceMarine) throws SQLException {
        int indexColumn = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        stat.setString(indexColumn++, spaceMarine.getName());
        stat.setLong(indexColumn++, spaceMarine.getCoordinates().getId());
        stat.setInt(indexColumn++, spaceMarine.getHealth());
        stat.setInt(indexColumn++, spaceMarine.getHeartCount());
        if (Objects.isNull(spaceMarine.getLoyal())) {
            stat.setNull(indexColumn++, Types.NULL);
        } else {
            stat.setBoolean(indexColumn++, spaceMarine.getLoyal());
        }
        if (Objects.isNull(spaceMarine.getChapter())) {
            stat.setNull(indexColumn++, Types.NULL);
        } else {
            stat.setLong(indexColumn++, spaceMarine.getChapter().getId());
        }
        stat.setLong(indexColumn++, 1L /*spaceMarine.getOwnerId()*/);
        stat.setObject(indexColumn++, spaceMarine.getCategory().name().toLowerCase(), Types.OTHER);
        stat.setTimestamp(indexColumn++, Timestamp.valueOf(spaceMarine.getCreationDateTime().format(formatter)));
    }

    private void prepareStatCoord(PreparedStatement stat, Coordinates coordinates) throws SQLException {
        int indexColumn = 1;
        stat.setDouble(indexColumn++, coordinates.getX());
        stat.setLong(indexColumn++, coordinates.getY());
    }

    private void prepareStatChapt(PreparedStatement stat, Chapter chapter) throws SQLException {
        int indexColumn = 1;
        stat.setString(indexColumn++, chapter.getName());
        stat.setString(indexColumn++, chapter.getParentLegion());
        stat.setLong(indexColumn++, chapter.getMarinesCount());
        stat.setString(indexColumn++, chapter.getWorld());
    }

    @Override
    public boolean addElement(SpaceMarine spMar) {
        String insertSpaceMar = "INSERT INTO spacemarine VALUES ("
                     + "    default,?,?,?,?,?,?,?,?::astartes_category,?) RETURNING id"; //name, time, coordinates, health, heartCount, loyal, category, chapter
        String insertCoord = "INSERT INTO coordinates VALUES ("
                     + "    default,?,?) RETURNING id";
        String insertChapter = "INSERT INTO chapter VALUES ("
                     + "    default,?,?,?,?) RETURNING id";
        if (spaceMarineCollection.checkContains(spMar)) {
            return false;
        }
        try {
            PreparedStatement statCoord = connectionDB.prepareStatement(insertCoord);
            PreparedStatement statChapter = connectionDB.prepareStatement(insertChapter);
            PreparedStatement statSpMar = connectionDB.prepareStatement(insertSpaceMar);
            //lock.lock();
            prepareStatCoord(statCoord, spMar.getCoordinates());
            if (Objects.nonNull(spMar.getChapter())) {
                prepareStatChapt(statChapter, spMar.getChapter());
                ResultSet resChapt = statChapter.executeQuery();
                resChapt.next();
                spMar.getChapter().setId(resChapt.getLong("id"));
            }
            ResultSet resCoord = statCoord.executeQuery();
            resCoord.next();
            spMar.getCoordinates().setId(resCoord.getLong("id"));
            prepareStatSpMar(statSpMar, spMar);
            ResultSet resSpMar = statSpMar.executeQuery();
            resSpMar.next();
            spMar.setID(resSpMar.getLong("id"));
            spaceMarineCollection.addElement(spMar);
            return true;
        } catch (SQLException e) {
            //LOGGER.error("Failed to insert element into DB", e);

            LOGGER.info("Failed to inser element into DB");
            e.printStackTrace();
            return false;
        } finally {
            //lock.unlock();
        }
    }

    @Override
    public boolean addIfMin(SpaceMarine spMar) {
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
            statChapter.setLong(1, spaceMarineCollection.findByID(id).getChapter().getId());
            if (statSpMar.executeUpdate() > 0) {
                statChapter.executeQuery();
                statCoord.executeQuery();
                spaceMarineCollection.removeById(id);
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
    public HashSet<SpaceMarine> getCollection() {
        return spaceMarineCollection.getCollection();
    }

    @Override
    public SpaceMarine findByID(Long id) {
        return spaceMarineCollection.findByID(id);
    }

    @Override
    public boolean updateSpaceMarine(SpaceMarine newMarine, Long id) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String updSpaceMar = "UPDATE spacemarine SET "
                     + "name=?, " //name, time, coordinates, health, heartCount, loyal, category, chapter
                     + "coordinates=?,"
                     + "health=?, "
                     + "heart_count=?, "
                     + "loyal=?, "
                     + "chapter=?, "
                     + "owner_id=?,"
                     + "astartes_category=?::astartes_category, "
                     + "creating_date_time=? " 
                     + "WHERE id=?";
        String updCoord = "UPDATE coordinates SET "
                     + "x=?, "
                     + "y=? "
                     + "WHERE id=?";
        String updChapter = "UPDATE chapter SET "
                     + "name=?, "
                     + "parent_legion=?, "
                     + "marines_count=?, "
                     + "world=? "
                     + "WHERE id=?";
        try {
            PreparedStatement getSpMar = connectionDB.prepareStatement("SELECT chapter, coordinates, creating_date_time FROM spacemarine WHERE id=?");
            PreparedStatement statCoord = connectionDB.prepareStatement(updCoord);
            PreparedStatement statChapter = connectionDB.prepareStatement(updChapter);
            PreparedStatement statSpMar = connectionDB.prepareStatement(updSpaceMar);
            //lock.lock();
            getSpMar.setLong(1, id);
            ResultSet idOfElements = getSpMar.executeQuery();
            if (!idOfElements.next()) {
                return false;
            };
            prepareStatCoord(statCoord, newMarine.getCoordinates());
            statCoord.setLong(3, idOfElements.getLong("coordinates"));
            statCoord.executeUpdate();
            newMarine.getCoordinates().setId(idOfElements.getLong("coordinates"));
            if (Objects.nonNull(newMarine.getChapter())) {
                prepareStatChapt(statChapter, newMarine.getChapter());
                statChapter.setLong(5, idOfElements.getLong("chapter"));
                statChapter.executeUpdate();
                newMarine.getChapter().setId(idOfElements.getLong("chapter"));
            }
            newMarine.setTime(LocalDateTime.parse(idOfElements.getString("creating_date_time"), formatter));
            prepareStatSpMar(statSpMar, newMarine);
            statSpMar.setLong(10, id);
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
        Set<SpaceMarine> removeSet = spaceMarineCollection.getSpMarIf(predicate);
        for (SpaceMarine removeSpMar: removeSet) {
            removeById(removeSpMar.getID());
        }
        return spaceMarineCollection.removeIf(predicate);
    }
}
