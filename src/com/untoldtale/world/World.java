 package com.untoldtale.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.Untold_tale.graphics.Spritesheet;

import Untold_Tale.main.Game;
import entities.Bullet;
import entities.Enemy;
import entities.Entity;
import entities.LifePack;
import entities.Player;
import entities.Weapon;

public class World 
{
	private static Tile[] tiles;
	public static int WIDTH, HEIGHT;
	public static final int TILE_SIZE = 32;
	
	public World(String path){
		try {
			BufferedImage map = ImageIO.read(getClass().getResource(path));
			int[] pixels = new int[map.getWidth() * map.getHeight()];
			WIDTH = map.getWidth();
			HEIGHT = map.getHeight();
			tiles = new Tile[map.getWidth() * map.getHeight()];
			map.getRGB(0, 0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());
			for (int xx = 0; xx < map.getWidth(); xx++)
			{
				for (int yy = 0; yy < map.getHeight(); yy++)
				{
					int pixelAtual = pixels[xx + (yy * map.getWidth())];
					tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 32, yy * 32, Tile.TILE_FLOOR);
					
					if (pixelAtual == 0xff000000){
						//floor
						tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 32, yy * 32, Tile.TILE_FLOOR);
						
					}else if(pixelAtual == 0xffffffff){
						//wall
						tiles[xx + (yy * WIDTH)] = new WallTile(xx * 32, yy * 32, Tile.TILE_WALL);
						
					}else if(pixelAtual == 0xff0011ff) {
						//player
						Game.player.setX(xx * 32);
						Game.player.setY(yy * 32);
						
					}else if(pixelAtual == 0xffff0000) {
						//Enemy
						Enemy en = new Enemy(xx*32, yy*32, 32, 32, Entity.ENEMY_EN);
						Game.entities.add(en);
						Game.enemies.add(en);
						
					}else if(pixelAtual == 0xffffa100) {
						//Weapon
						Game.entities.add(new Weapon(xx*32, yy*32, 32, 32, Entity.WEAPON_EN));
						
					}else if (pixelAtual == 0xffd77bba) {
						//Life Pack
						LifePack pack = new LifePack(xx*32, yy*32, 32, 32, Entity.LIFEPACK_EN);
						//pack.setMask(8,8,8,8);
						Game.entities.add(pack);
						//Player n�o est� pegando os Lifepack.(lifepack n�o some ao colidir)
						
					}else if (pixelAtual == 0xfffbf236) {
						//bullet
						Game.entities.add(new Bullet(xx*32, yy*32, 32, 32, Entity.BULLET_EN));
					}
				}
			}
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean isFree(int xnext, int ynext) {
			int x1 = xnext / TILE_SIZE;
			int y1 = ynext / TILE_SIZE;
			
			int x2 = (xnext + TILE_SIZE - 1) / TILE_SIZE;
			int y2 = ynext / TILE_SIZE;
			
			int x3 = xnext / TILE_SIZE;
			int y3 = (ynext + TILE_SIZE -1) /TILE_SIZE;
			
			int x4 = (xnext + TILE_SIZE -1) / TILE_SIZE;
			int y4 = (ynext + TILE_SIZE -1) /TILE_SIZE;
			return !(tiles[x1 + (y1 * World.WIDTH)] instanceof WallTile ||
					tiles[x2 + (y2 * World.WIDTH)] instanceof WallTile ||
					tiles[x3 + (y3 * World.WIDTH)] instanceof WallTile ||
					tiles[x4 + (y4 * World.WIDTH)] instanceof WallTile);
	}
	public static void restartGame(String level){
		Game.entities = new ArrayList<Entity>();
		Game.enemies = new ArrayList<Enemy>();
		Game.spritesheet = new Spritesheet("/Sprite-Simple_man_test-Sheet.png");
		Game.player = new Player(0,0,16,16,Game.spritesheet.getSprite(256, 0, 32, 32));
		Game.world = new World("/"+level);
		Game.entities.add(Game.player);
		return;
	}	
	
	public void render(Graphics g)
	{
		int xstart = Camera.x / 32;
		int ystart = Camera.y / 32;
		
		int xfinal = xstart + (Game.WIDTH / 32);
		int yfinal  = ystart + (Game.HEIGHT / 32);
		
		for(int xx = xstart; xx <= xfinal; xx++) {
			for(int yy = ystart; yy <= yfinal; yy++) {
				if(xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT)
					continue;
				Tile tile = tiles[xx + (yy * WIDTH)];
				tile.render(g);
			}
		}
	}
}
