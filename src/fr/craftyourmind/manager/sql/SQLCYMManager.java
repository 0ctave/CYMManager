package fr.craftyourmind.manager.sql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import fr.craftyourmind.manager.CYMClan;
import fr.craftyourmind.manager.CYMNPC;
import fr.craftyourmind.manager.CYMPlayer;
import fr.craftyourmind.manager.CYMReputation;
import fr.craftyourmind.manager.Plugin;

public class SQLCYMManager {

    static private String name_db;
    static private String base;
    static private String login;
    static private String pass;
    static private String host;

    private static boolean run = false;
    private static final List<AbsSQL> queries = new ArrayList<AbsSQL>();
    private static final List<AbsSQL> addqueries = Collections.synchronizedList(new ArrayList<AbsSQL>());

    public static AbsSQLCYMCnx minicnx;

    private static Thread thread;

    public static void addQuery(AbsSQL query) {
        addqueries.add(query);
        if (thread == null) {
            thread = new Thread() {

                @Override
                public void run() {
                    try {
                        Connection cnx = getCnx();
                        boolean run = true;
                        while (run) {
                            synchronized (addqueries) {
                                queries.addAll(addqueries);
                                addqueries.clear();
                            }
                            for (AbsSQL sql : queries) {
                                sql.cnx = cnx;
                                sql.run();
                            }
                            queries.clear();
                            synchronized (addqueries) {
                                if (addqueries.isEmpty()) {
                                    run = false;
                                    thread = null;
                                }
                            }
                        }
                        cnx.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
    }

    static public void init() {
        try {
            File d = Plugin.it.getDataFolder();
            d.mkdir();
            File c = new File(d, "configSQL.yml");
            YamlConfiguration conf = YamlConfiguration.loadConfiguration(c);
            if (c.createNewFile()) {
                conf.set("base_type_(sqlite, mysql)", "sqlite");
                conf.set("prefix", "mini_");
                conf.createSection("SQLite");
                conf.set("name_db", "cymmanager");
                conf.createSection("MySQL");
                conf.set("base", "minecraft");
                conf.set("login", "root");
                conf.set("pass", "");
                conf.set("host", "localhost");
                conf.save(c);
            }
            String base_type = conf.getString("base_type_(sqlite, mysql)");
            String prefix = conf.getString("prefix");
            name_db = conf.getString("name_db");
            base = conf.getString("base");
            login = conf.getString("login");
            pass = conf.getString("pass");
            host = conf.getString("host");
            AbsSQL.T_CLAN = prefix + "clan";
            AbsSQL.T_PLAYER = prefix + "player";
            AbsSQL.T_REPUTATION = prefix + "reputation";
            AbsSQL.T_REPUTEPOINT = prefix + "reputepoint";
            AbsSQL.T_NPC = prefix + "npc";
            AbsSQL.T_REPUTECLAN = prefix + "reputeclan";

            if (base_type.equalsIgnoreCase("mysql"))
                minicnx = new CYMMySQL(base, login, pass, host);
            else if (base_type.equalsIgnoreCase("sqlite"))
                minicnx = new CYMSQLite(Plugin.it.getDataFolder().getPath() + File.separator, name_db);

            if (minicnx == null) {
                Plugin.log("Error type data base.");
                return;
            }
            createTable();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static protected Connection getCnx() throws SQLException {
        return minicnx.getCnx();
    }

    private static void createTable() {
        try {
            Connection cnx = getCnx();
            Statement state = cnx.createStatement();

            state.executeUpdate(minicnx.init(AbsSQL.T_CLAN).addPK("id").addAI("id").addInt("id").addVarC("name", 50).addInt("archi").addLong("dateCreation").addInt("penalty").addInt("chef").sqlCreate());
            state.executeUpdate(minicnx.init(AbsSQL.T_PLAYER).addPK("id").addAI("id").addInt("id").addVarC("name", 50).addInt("level").addInt("clan").sqlCreate());
            state.executeUpdate(minicnx.init(AbsSQL.T_REPUTATION).addPK("id").addAI("id").addInt("id").addVarC("name", 100).addTxt("descriptive").sqlCreate());
            state.executeUpdate(minicnx.init(AbsSQL.T_REPUTEPOINT).addPK("idRepute").addPK("idPlayer").addInt("idRepute").addInt("idPlayer").addInt("points").addInt("param").sqlCreate());
            state.executeUpdate(minicnx.init(AbsSQL.T_NPC).addPK("id").addInt("id").addVarC("name", 100).addTxt("urlSkin").addBool("isBlock").addVarC("world", 100).addInt("x").addInt("y").addInt("z").addTxt("text").addInt("distanceDrawIcon").addDefaultValue("16").sqlCreate());
            state.executeUpdate(minicnx.init(AbsSQL.T_REPUTECLAN).addPK("idRepute").addPK("idClan").addInt("idRepute").addInt("idClan").addInt("points").addInt("param").sqlCreate());


        } catch (SQLException e) {
            minicnx.errorCreate(e);
        }
    }

    static public void load() {
        try {
            Plugin.log("Minicraft SQL Load ...");
            if (minicnx == null) {
                Plugin.log("Error type data base.");
                return;
            }
            Connection cnx = getCnx();

            AbsSQL.init(cnx);

            // REPUTATION
            Statement state4 = cnx.createStatement();
            ResultSet rsReputation = state4.executeQuery("select * from " + AbsSQL.T_REPUTATION);
            while (rsReputation.next()) {
                CYMReputation rep = new CYMReputation(rsReputation.getString("name"));
                rep.id = rsReputation.getInt("id");
                rep.descriptive = rsReputation.getString("descriptive");
            }

            // MINICRAFT CLAN
            Map<CYMClan, Integer> clanChef = new HashMap<CYMClan, Integer>();
            Statement state1 = cnx.createStatement();
            ResultSet rsClan = state1.executeQuery("select * from " + AbsSQL.T_CLAN);
            while (rsClan.next()) {
                CYMClan mc = new CYMClan(rsClan.getString("name"));
                mc.id = rsClan.getInt("id");
                mc.archi = rsClan.getInt("archi");
                mc.dateCreation = rsClan.getLong("dateCreation");
                mc.penalty = rsClan.getInt("penalty");
                clanChef.put(mc, rsClan.getInt("chef"));
                CYMClan.addLoad(mc);
            }

            // MINICRAFT PLAYER
            CYMPlayer.newNobody();
            Statement state2 = cnx.createStatement();
            ResultSet rsPlayer = state2.executeQuery("select * from " + AbsSQL.T_PLAYER);
            while (rsPlayer.next()) {
                CYMPlayer mp = new CYMPlayer(rsPlayer.getString("name"));
                mp.id = rsPlayer.getInt("id");
                mp.level = rsPlayer.getInt("level");
                mp.setClan(CYMClan.get(rsPlayer.getInt("clan")));
            }

            // PLAYER REPUTE POINTS
            Statement state3 = cnx.createStatement();
            ResultSet rsReputePts = state3.executeQuery("select * from " + AbsSQL.T_REPUTEPOINT);
            while (rsReputePts.next()) {
                CYMPlayer mp = CYMPlayer.get(rsReputePts.getInt("idPlayer"));
                if (mp != null)
                    mp.addReputeLoad(rsReputePts.getInt("idRepute"), rsReputePts.getInt("points"), rsReputePts.getInt("param"));
            }

            // MINICRAFT NPC
            Statement state5 = cnx.createStatement();
            ResultSet rsNpc = state5.executeQuery("select * from " + AbsSQL.T_NPC);
            while (rsNpc.next()) {
                CYMNPC npc = new CYMNPC(rsNpc.getInt("id"), rsNpc.getString("name"));
                npc.urlSkin = rsNpc.getString("urlSkin");
                npc.isBlock = rsNpc.getBoolean("isBlock");
                npc.world = Bukkit.getWorld(rsNpc.getString("world"));
                npc.x = rsNpc.getInt("x");
                npc.y = rsNpc.getInt("y");
                npc.z = rsNpc.getInt("z");
                npc.text = rsNpc.getString("text");
                npc.distanceDrawIcon = rsNpc.getInt("distanceDrawIcon");
                if (npc.isBlock) npc.addBlockNpcMeta();
                else CYMNPC.add(npc);
            }

            // CLANS REPUTE POINTS
            Statement state6 = cnx.createStatement();
            ResultSet rsReputeClans = state3.executeQuery("select * from " + AbsSQL.T_REPUTECLAN);
            while (rsReputeClans.next()) {
                CYMClan mc = CYMClan.get(rsReputeClans.getInt("idClan"));
                if (mc != null)
                    mc.addReputeLoad(rsReputeClans.getInt("idRepute"), rsReputeClans.getInt("points"), rsReputeClans.getInt("param"));
            }

            // CLAN CHEF
            for (Entry<CYMClan, Integer> entry : clanChef.entrySet())
                entry.getKey().chef = CYMPlayer.get(entry.getValue());

            cnx.close();
            Plugin.log("Minicraft SQL Load Finish ! !");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getIdPlayer(String name) {
        try {
            Statement state = getCnx().createStatement();
            ResultSet rs = state.executeQuery("select * from " + AbsSQL.T_PLAYER + " WHERE name = \"" + name + "\"");
            while (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getIdClan(String name) {
        try {
            Statement state = getCnx().createStatement();
            ResultSet rs = state.executeQuery("select * from " + AbsSQL.T_CLAN + " WHERE name = \"" + name + "\"");
            while (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void create(CYMClan mc) {
        new SQLCYMClan(AbsSQL.CREATE, mc).initID().go();
    }

    public static void save(CYMClan mc) {
        new SQLCYMClan(AbsSQL.SAVE, mc).go();
    }

    public static void delete(CYMClan mc) {
        new SQLCYMClan(AbsSQL.DELETE, mc).go();
    }

    public static void create(CYMPlayer mp) {
        new SQLCYMPlayer(AbsSQL.CREATE, mp).initID().go();
    }

    public static void save(CYMPlayer mp) {
        new SQLCYMPlayer(AbsSQL.SAVE, mp).go();
    }

    public static void delete(CYMPlayer mp) {
        new SQLCYMPlayer(AbsSQL.DELETE, mp).go();
    }

    public static void create(CYMPlayer mp, CYMReputation repute) {
        new SQLCYMPlayer(AbsSQL.CREATEREPUTE, mp, repute).go();
    }

    public static void save(CYMPlayer mp, CYMReputation repute) {
        new SQLCYMPlayer(AbsSQL.UPDATEREPUTE, mp, repute).go();
    }

    public static void create(CYMNPC npc) {
        new SQLCYMNPC(AbsSQL.CREATE, npc).go();
    }

    public static void save(CYMNPC npc) {
        new SQLCYMNPC(AbsSQL.SAVE, npc).go();
    }

    public static void delete(CYMNPC npc) {
        new SQLCYMNPC(AbsSQL.DELETE, npc).go();
    }

    public static void create(CYMReputation rep) {
        new SQLCYMReputation(AbsSQL.CREATE, rep).initID().go();
    }

    public static void save(CYMReputation rep) {
        new SQLCYMReputation(AbsSQL.SAVE, rep).go();
    }

    public static void delete(CYMReputation rep) {
        new SQLCYMReputation(AbsSQL.DELETE, rep).go();
    }

    public static void create(CYMClan mc, CYMReputation repute) {
        new SQLCYMClan(AbsSQL.CREATEREPUTE, mc, repute).go();
    }

    public static void save(CYMClan mc, CYMReputation repute) {
        new SQLCYMClan(AbsSQL.UPDATEREPUTE, mc, repute).go();
    }

    public static void delete(CYMClan mc, CYMReputation repute) {
        new SQLCYMClan(AbsSQL.DELETEREPUTE, mc, repute).go();
    }
}
