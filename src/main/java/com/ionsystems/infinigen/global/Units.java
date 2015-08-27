package main.java.com.ionsystems.infinigen.global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.unitBuilder.Unit;

public class Units {
	private static HashMap<String, ArrayList<Unit>> units = new HashMap<String, ArrayList<Unit>>();
	private static ArrayList<Unit> unitList = new ArrayList<Unit>();
	private static HashSet<String> unitTypes = new HashSet<String>();

	public static HashMap<String, ArrayList<Unit>> getUnits() {
		return units;
	}

	public static void setUnits(HashMap<String, ArrayList<Unit>> units) {
		Units.units = units;
	}
	
	public static ArrayList<Unit> getAllType(String unit){
		return units.get(unit);
	}
	
	public static ArrayList<Unit> getUnitList(){
		return unitList;
	}
	
	public static void addUnit(Unit unit){
		try{
			units.get(unit.getName()).add(unit);
		}catch(NullPointerException e){
			//No list exists so make one
			units.put(unit.getName(), new ArrayList<Unit>());
			units.get(unit.getName()).add(unit); //Then add it
		}
		unitList.add(unit);
		unitTypes.add(unit.getName());
	}
	
	public static void removeUnit(Unit unit){
		units.get(unit.getName()).remove(unit);
		unitList.remove(unit);
		if(units.get(unit.getName()).isEmpty()){ //There are no more of this unit currently.
			unitTypes.remove(unit.getName());
		}
	}

	public static ArrayList<PhysicsEntity> getEntities() {
		ArrayList<PhysicsEntity> entities = new ArrayList<PhysicsEntity>();
		for(Unit u : unitList){
			entities.addAll(u.getEntities());
		}
		return entities;
	}
		
}
