package com.kiminouso.kimivanish;

import me.tippie.tippieutils.storage.SQLStorage;
import me.tippie.tippieutils.storage.annotations.SqlQuery;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class Storage extends SQLStorage {
    private final KimiVanish plugin;

    public Storage(KimiVanish plugin){
        super(plugin, org.h2.Driver.load(), SQLType.H2, new File(plugin.getDataFolder(), "user-preferences"));

        this.plugin = plugin;

        try {
            runResourceScript("db.sql");
        } catch (SQLException | IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not connect to H2 Database for Vanish settings...", e);
        }
    }

    @SqlQuery("SELECT * FROM USER_PREFERENCES WHERE VANISH_USER = ?")
    public CompletableFuture<List<VanishUser>> findVanishUser(UUID target) {
        return prepareStatement((stmt) -> {
            try {
                stmt.setObject(1, target);
                ResultSet rs = stmt.executeQuery();
                List<VanishUser> result = new ArrayList<>();
                while (rs.next()) {
                    VanishUser entry = new VanishUser(
                            rs.getObject("VANISH_USER", UUID.class),
                            rs.getBoolean("ITEM_SETTING"),
                            rs.getBoolean("INTERACT_SETTING"),
                            rs.getBoolean("NOTIFY_SETTING"),
                            rs.getBoolean("LOCATION_SETTING"),
                            rs.getBoolean("NIGHTVISION_SETTING"),
                            rs.getBoolean("FLIGHT_SETTING")
                    );
                    result.add(entry);
                }
                return result;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).exceptionally((e) -> {
            e.printStackTrace();
            return null;
        });
    }

    @SqlQuery("UPDATE USER_PREFERENCES SET ITEM_SETTING = ? WHERE VANISH_USER = ?")
    public CompletableFuture<Void> setItemSetting(UUID uuid, boolean setting) {
        return prepareStatement((stmt) -> {
            try {
                stmt.setBoolean(1, setting);
                stmt.setObject(2, uuid);
                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return (Void) null;
        }).exceptionally((e) -> {
            e.printStackTrace();
            return null;
        });
    }

    @SqlQuery("UPDATE USER_PREFERENCES SET INTERACT_SETTING = ? WHERE VANISH_USER = ?")
    public CompletableFuture<Void> setInteractSetting(UUID uuid, boolean setting) {
        return prepareStatement((stmt) -> {
            try {
                stmt.setBoolean(1, setting);
                stmt.setObject(2, uuid);
                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return (Void) null;
        }).exceptionally((e) -> {
            e.printStackTrace();
            return null;
        });
    }

    @SqlQuery("UPDATE USER_PREFERENCES SET NOTIFY_SETTING = ? WHERE VANISH_USER = ?")
    public CompletableFuture<Void> setNotifySetting(UUID uuid, boolean setting) {
        return prepareStatement((stmt) -> {
            try {
                stmt.setBoolean(1, setting);
                stmt.setObject(2, uuid);
                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return (Void) null;
        }).exceptionally((e) -> {
            e.printStackTrace();
            return null;
        });
    }

    @SqlQuery("UPDATE USER_PREFERENCES SET NIGHTVISION_SETTING = ? WHERE VANISH_USER = ?")
    public CompletableFuture<Void> setNightvisionSetting(UUID uuid, boolean setting) {
        return prepareStatement((stmt) -> {
            try {
                stmt.setBoolean(1, setting);
                stmt.setObject(2, uuid);
                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return (Void) null;
        }).exceptionally((e) -> {
            e.printStackTrace();
            return null;
        });
    }

    @SqlQuery("UPDATE USER_PREFERENCES SET LOCATION_SETTING = ? WHERE VANISH_USER = ?")
    public CompletableFuture<Void> setLocationSetting(UUID uuid, boolean setting) {
        return prepareStatement((stmt) -> {
            try {
                stmt.setBoolean(1, setting);
                stmt.setObject(2, uuid);
                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return (Void) null;
        }).exceptionally((e) -> {
            e.printStackTrace();
            return null;
        });
    }

    @SqlQuery("UPDATE USER_PREFERENCES SET FLIGHT_SETTING = ? WHERE VANISH_USER = ?")
    public CompletableFuture<Void> setFightSetting(UUID uuid, boolean setting) {
        return prepareStatement((stmt) -> {
            try {
                stmt.setBoolean(1, setting);
                stmt.setObject(2, uuid);
                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return (Void) null;
        }).exceptionally((e) -> {
            e.printStackTrace();
            return null;
        });
    }

    @SqlQuery("INSERT INTO USER_PREFERENCES (VANISH_USER, ITEM_SETTING, INTERACT_SETTING, NOTIFY_SETTING, LOCATION_SETTING, NIGHTVISION_SETTING, FLIGHT_SETTING) VALUES (?, ?, ?, ?, ?, ?, ?)")
    public CompletableFuture<Void> registerVanishUser(UUID uuid, boolean itemSetting, boolean interactSetting, boolean notifySetting, boolean locationSetting, boolean nightVisionSetting, boolean flightSetting) {
        return prepareStatement((stmt) -> {
            try {
                stmt.setObject(1, uuid);
                stmt.setBoolean(2, itemSetting);
                stmt.setBoolean(3, interactSetting);
                stmt.setBoolean(4, notifySetting);
                stmt.setBoolean(5, locationSetting);
                stmt.setBoolean(6, nightVisionSetting);
                stmt.setBoolean(7, flightSetting);
                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return (Void) null;
        }).exceptionally((e) -> {
            e.printStackTrace();
            return null;
        });
    }

    public static record VanishUser(UUID uuid, boolean notifySetting, boolean itemSetting, boolean interactSetting, boolean locationSetting, boolean nightVisionSetting, boolean flightSetting) { }
}
