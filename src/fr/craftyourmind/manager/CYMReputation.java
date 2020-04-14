package fr.craftyourmind.manager;

import java.util.ArrayList;
import java.util.List;

import fr.craftyourmind.manager.sql.SQLCYMManager;

public class CYMReputation {

	private static List<CYMReputation> list = new ArrayList<CYMReputation>();
	
	public int id = 0;
	public String name = "";
	public String descriptive = "";
	
	public CYMReputation(String name) {
		this.name = name;
		add(this);
	}
	
	public void create(){
		SQLCYMManager.create(this);
	}
	
	public void save(){
		SQLCYMManager.save(this);
	}

	public void delete(){
		list.remove(this);
		SQLCYMManager.delete(this);
		for(CYMPlayer p : CYMPlayer.getList())
			p.getReputations().remove(this);
		for(CYMClan c : CYMClan.get())
			c.getReputations().remove(this);
	}
	
	public static void add(CYMReputation rep){
		list.add(rep);
	}
	
	public static List<CYMReputation> get(){
		return list;
	}
	
	public static CYMReputation get(String name){
		for(CYMReputation rep : list)
			if(rep.name.equalsIgnoreCase(name)) return rep;
		return null;
	}
	
	public static CYMReputation get(int index){
		if(index >= 0 && index < list.size()) return list.get(index);
		return null;
	}
	
	public static CYMReputation getById(int id){
		for(CYMReputation rep : list){
			if(rep.id == id) return rep;
		}
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this.getClass() != obj.getClass())
			return false;
		CYMReputation o = (CYMReputation)obj;
		return this.id == o.id;
	}
}
