package me.A5H73Y.Parkour.Other;

import java.io.Serializable;

import org.bukkit.Material;

public class ParkourBlocks implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Material finish, climb, launch, speed, repulse, norun, nopotion, doublejump;
	
	
	/**
	 * New as of 4.0. I've rethought how I'm going to achieve this. There will be ParkourBlock types, each with an identifier.
	 * For example if I was going to have different themes throughout the server, each course theme would have it's own set of ParkourBlocks.
	 * The course then gets linked to a parkourBlock set and not the other way around.
	 * 
	 * @param finish
	 * @param climb
	 * @param launch
	 * @param speed
	 * @param repulse
	 * @param norun
	 * @param nopotion
	 * @param doublejump
	 */
	public ParkourBlocks(Material finish, Material climb, Material launch, Material speed, Material repulse, Material norun, Material nopotion, Material doublejump){
		this.finish = finish;
		this.climb = climb;
		this.launch = launch;
		this.speed = speed;
		this.repulse = repulse;
		this.norun = norun;
		this.nopotion = nopotion;
		this.doublejump = doublejump;
	}

	public Material getFinish() {
		return finish;
	}

	public void setFinish(Material finish) {
		this.finish = finish;
	}

	public Material getClimb() {
		return climb;
	}

	public void setClimb(Material climb) {
		this.climb = climb;
	}

	public Material getLaunch() {
		return launch;
	}

	public void setLaunch(Material launch) {
		this.launch = launch;
	}

	public Material getSpeed() {
		return speed;
	}

	public void setSpeed(Material speed) {
		this.speed = speed;
	}

	public Material getRepulse() {
		return repulse;
	}

	public void setRepulse(Material repulse) {
		this.repulse = repulse;
	}

	public Material getNorun() {
		return norun;
	}

	public void setNorun(Material norun) {
		this.norun = norun;
	}

	public Material getNopotion() {
		return nopotion;
	}

	public void setNopotion(Material nopotion) {
		this.nopotion = nopotion;
	}

	public Material getDoublejump() {
		return doublejump;
	}

	public void setDoublejump(Material doublejump) {
		this.doublejump = doublejump;
	}
}
