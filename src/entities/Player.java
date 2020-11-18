package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import com.untoldtale.world.Camera;
import com.untoldtale.world.World;
import Untold_Tale.main.Game;

public class Player extends Entity
{
	public boolean right, up, left, down;
	public int right_dir = 0, left_dir = 1, up_dir = 3, down_dir = 4;
	public int dir = down_dir;
	public double speed = 1.4;
	
	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 2;
	private boolean moved = false;
	private BufferedImage[] upPlayer;
	private BufferedImage[] downPlayer;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	
	private BufferedImage playerDamage;
	public boolean hasgun = false;
	public int ammo = 0;
	public boolean isDamaged = false;
	public int damageFrames = 0;
	
	public boolean shoot = false, mouseShoot = false;
	
	public double life = 100, maxLife = 100;;
	public int mx, my;

	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		upPlayer = new BufferedImage[3];
		downPlayer = new BufferedImage[3];
		rightPlayer = new BufferedImage[3];
		leftPlayer = new BufferedImage[3];
		
		playerDamage = Game.spritesheet.getSprite(608, 0, 32, 32);//não está reproduzindo o sprite de dano.
		
		
		for(int i =0; i < 3; i++){
		rightPlayer[i] = Game.spritesheet.getSprite(256 + (i * 32), 0, 32, 32);
		}
		for(int i =0; i < 3; i++){
			leftPlayer[i] = Game.spritesheet.getSprite(352 + (i * 32), 0, 32, 32);
			}
		for(int i =0; i < 3; i++){
			downPlayer[i] = Game.spritesheet.getSprite(64 + (i * 32), 0, 32, 32);
			}
		for(int i =0; i < 3; i++){
			upPlayer[i] = Game.spritesheet.getSprite(160 + (i * 32), 0, 32, 32);
			}
		
	}
	public void tick() {
		moved = false;
		if(right && World.isFree((int)(x+speed),this.getY())){
			moved = true;
			dir = right_dir;
			x+=speed;
		}
		else if(left && World.isFree((int)(x-speed),this.getY())) {
			moved = true;
			dir = left_dir;
			x-=speed;
		}
		if(up && World.isFree(this.getX(),(int)(y-speed))) {
			moved = true;
			dir = up_dir;
			y-=speed;
		}
		else if(down && World.isFree(this.getX(), (int)(y+speed))) {
			moved = true;
			dir = down_dir;
			y+=speed;
		}
		if (moved) {
			frames++;
			if(frames == maxFrames){
				frames = 0;
				index++;
				if(index > maxIndex){
					index = 0;
				}
			}
			this.checkCollisionLifePack();
			this.checkCollisionAmmo();
			this.checkCollisionGun();
			
			if(isDamaged) {
				this.damageFrames++;
				if(this.damageFrames == 30) {
					this.damageFrames = 0;
					isDamaged = false;
				}
			}
			
			Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2), 0, World.WIDTH*32 - Game.WIDTH);
			Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT/2), 0, World.HEIGHT*32 - Game.HEIGHT);
		}
		 if (shoot) {
			 shoot= false;
			 if(hasgun && ammo>0) {
				 ammo--;
			 //criar bala e atirar
			 shoot = false;
			 int dx = 0;
			 int px = 0;
			 int py = 15;
			 if(dir == right_dir) {
				 px = 15;
				 dx = 1;
			 }else if(dir == left_dir){
				 px = -15;
				 dx = -1;
			 }else if(dir == up_dir) {
				 
			 }else if (dir == down_dir) {
				 
			 }
			 
			 BulletShoot bullet = new BulletShoot(this.getX()+px, this.getY()+py, 5, 5, null, dx, 0);
			 Game.bullets.add(bullet);
		 }
		 }
		 
		 if(mouseShoot) {
			 mouseShoot= false;
			 if(hasgun && ammo>0) {
				 ammo--;
			 
			 //criar bala e atirar
			 int px = 15, py = 15;
			 double angle = 0;
			 if(dir == right_dir) {
				 px=15;
				 angle = Math.atan2(my - (this.getY() + py - Camera.y), mx - (this.getX() + px - Camera.x));
			 }else {
				 px=-15;
				 angle = Math.atan2(my - (this.getY() + py - Camera.y), mx - (this.getX() + px - Camera.x));
			 }
			 
			 double dx = Math.cos(angle);
			 double dy = Math.sin(angle);
			 
			 BulletShoot bullet = new BulletShoot(this.getX()+px, this.getY()+py, 5, 5, null, dx, dy);
			 Game.bullets.add(bullet);
		 }
		 }
		if(life <= 0) {
			//Game Over!
			life=0;
			Game.gameState = "GAME_OVER";
		}
		updateCamera();
		
		}
	
		public void updateCamera() {
			Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2),0 ,World.WIDTH * 32 - Game.WIDTH);
			Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT/2),0 ,World.HEIGHT * 32 - Game.HEIGHT);
		}
	
		public void checkCollisionAmmo() {
			for(int i = 0; i < Game.entities.size(); i++) {
				Entity atual = Game.entities.get(i);
				if(atual instanceof Bullet) {
					if(Entity.isColliding(this, atual)) {
						ammo+=100;
						System.out.println("munição atual "+ammo);
						Game.entities.remove(atual);
					}
				}
			}
		}
		public void checkCollisionGun() {
			for(int i = 0; i < Game.entities.size(); i++) {
				Entity atual = Game.entities.get(i);
				if(atual instanceof Weapon) {
					if(Entity.isColliding(this, atual)) {
						hasgun = true;
						//System.out.println("pegou arma");
						Game.entities.remove(atual);
					}
				}
			}
		}
		
		public void checkCollisionLifePack() {
			for(int i = 0; i < Game.entities.size(); i++) {
				Entity atual = Game.entities.get(i);
				if(atual instanceof LifePack) {
					if(Entity.isColliding(this, atual)) {
						life+=10;
						if(life >=100)
							life=100;
						Game.entities.remove(atual);
					}
				}
			}
		
		}
	
		public void render(Graphics g) {
			if (!isDamaged) {

				if (dir == right_dir) {
					g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
					if (hasgun) {
						// desenhar arma para a direita.
						g.drawImage(Entity.GUN_RIGHT, this.getX() - Camera.x + 10, this.getY() - Camera.y + 5, null);
					}
				} else if (dir == left_dir) {
					g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
					if (hasgun) {
						// desenhar arma para a esquerda.
						g.drawImage(Entity.GUN_LEFT, this.getX() - Camera.x - 10, this.getY() - Camera.y + 5, null);
					}
				} else if (dir == down_dir) {
					g.drawImage(downPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
					if (hasgun) {
						// desenhar arma para a baixo.
						g.drawImage(Entity.GUN_DOWN, this.getX() - Camera.x, this.getY() - Camera.y + 8, null);
					}
				} else if (dir == up_dir) {
					g.drawImage(upPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
					if (hasgun) {
						// desenhar arma para a cima.
						g.drawImage(Entity.GUN_UP, this.getX() - Camera.x, this.getY() - Camera.y - 12, null);
						// Como fazer uma espécie de z-index para que o Sprite do Player seja Absoluto?

					}
				}

			} else {
				g.drawImage(playerDamage, this.getX() - Camera.x, this.getY() - Camera.y, null);
			}
		}
}
