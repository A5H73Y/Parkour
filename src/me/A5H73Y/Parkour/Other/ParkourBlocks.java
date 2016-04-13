package me.A5H73Y.Parkour.Other;

import java.io.Serializable;

import org.bukkit.Material;

public class ParkourBlocks implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Material finish, climb, launch, speed, repulse, norun, nopotion, doublejump;
	
	
	/**
	 * New as of 4.0. Each course will have it's own ParkourBlocks. This will contain all the known blocks from previous version,
	 * however each course can have it's own individual type of block. This is been requested several times and this seemed a good
	 * way to resolve it.
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
