package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.untoldtale.world.Camera;

import Untold_Tale.main.Game;

public class BulletShoot extends Entity{
	
	private double dx;
	private double dy;
	private double spd = 4;
	private int life = 200, curLife =0;
	
	public BulletShoot(int x, int y, int width, int height, BufferedImage sprite, double dx2, double dy2) {
		super(x, y, width, height, sprite);
		this.dx = dx2;
		this.dy = dy2;
	}
	
	public void tick(){
		x+=dx*spd;
		y+=dy*spd;
		curLife++;
		if(curLife == life) {
			Game.bullets.remove(this);
			return;
		}
	}
	
	public void render(Graphics g) {
		g.setColor(Color.black);
		g.fillOval(this.getX() - Camera.x, this.getY() - Camera.y, width, height);
	}
}
