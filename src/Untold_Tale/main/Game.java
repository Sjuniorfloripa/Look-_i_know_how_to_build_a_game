package Untold_Tale.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;
import com.Untold_tale.graphics.Spritesheet;
import com.Untold_tale.graphics.UI;
import com.untoldtale.world.World;
import entities.BulletShoot;
import entities.Enemy;
import entities.Entity;
import entities.Player;

public class Game extends Canvas implements Runnable,KeyListener,MouseListener
{
	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	public static final int WIDTH = 320;
	public static final int HEIGHT = 240;
	public static final int SCALE = 2;
	
	private int CUR_LEVEL = 1, MAX_LEVEL =2;
	private BufferedImage image;
	
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<BulletShoot> bullets;
	public static Spritesheet spritesheet;
	
	public static World world;
	public static Player player;
	public static Random rand;
	public UI ui;
	public static String gameState = "MENU";
	private boolean showMessageGameOver = true;
	private int framesGameOver = 0;
	public boolean restartGame = false;
	
	public Menu menu;
	
	public Game()
	{
		rand = new Random();
		addKeyListener(this);
		addMouseListener(this);
		setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		initFrame();
		//inicializando objetos
		ui = new UI();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bullets = new ArrayList<BulletShoot>();
	
		spritesheet = new Spritesheet("/Sprite-Simple_man_test-Sheet.png");
		player = new Player(0,0,16,16,spritesheet.getSprite(256, 0, 32, 32));
		world = new World("/level1.png");
		entities.add(player);
		
		menu = new Menu();
	}
	
	public void initFrame()
	{
		frame = new JFrame("Game #1");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public synchronized void start()
	{
		thread = new Thread(this);
		thread.start();
	}
	
	public synchronized void stop()
	{
		isRunning = false;
		try 
		{
			thread.join();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String args[])
	{
		Game game = new Game();
		game.start();
	}
	
	public void tick(){
		if(gameState == "NORMAL") {
			this.restartGame = false;
			for(int i = 0; i < entities.size(); i++)
			{
				Entity e = entities.get(i);
				e.tick();
			}
			for(int i = 0; i < bullets.size(); i++) {
				bullets.get(i).tick();
			}
			if(enemies.size() == 0) {
				//Avançar para o proximo level.
				CUR_LEVEL++;
				if(CUR_LEVEL > MAX_LEVEL) {
					CUR_LEVEL = 1;
				}
				String newWorld= "level"+CUR_LEVEL+".png";
				World.restartGame(newWorld);
			}
		}else if(gameState == "GAME_OVER") {
			this.framesGameOver++;
			if(this.framesGameOver == 30) {
				this.framesGameOver = 0;
				if(this.showMessageGameOver) 
					this.showMessageGameOver = false;
				else
					this.showMessageGameOver = true;
			}
			if(restartGame) {
				this.restartGame = false;
				this.gameState = "NORMAL";
				CUR_LEVEL = 1;
				String newWorld= "level"+CUR_LEVEL+".png";
				World.restartGame(newWorld);
			}
		}else if(gameState == "MENU") {
			menu.tick();
		}
	}
	
	public void render()
	{
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null)
		{
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = image.getGraphics();
		g.setColor(new Color(0,0,0));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		/*Renderização do jogo*/
		Graphics2D g2 = (Graphics2D) g;
		world.render(g);
		for(int i = 0; i < entities.size(); i++)
		{
			Entity e = entities.get(i);
			e.render(g);
		}
		for(int i = 0; i < bullets.size(); i++) {
			bullets.get(i).render(g);
		}
		ui.render(g);
		/***/
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
		g.setFont(new Font("arial",Font.BOLD,20));
		g.setColor(Color.white);
		g.drawString("Munição: " +player.ammo, 20, 60);
		if(gameState == "GAME_OVER") {
			Graphics2D g21 = (Graphics2D)g;
			g21.setColor(new Color(0,0,0,100));
			g21.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
			g.setFont(new Font("arial",Font.BOLD,68));
			g.setColor(Color.white);
			g.drawString("GAME OVER!", (WIDTH*SCALE)/2 -220, (HEIGHT*SCALE)/2 +20);
			g.setFont(new Font("arial",Font.BOLD,32));
			if(showMessageGameOver)
				g.drawString("> pressione ENTER para reiniciar<", (WIDTH*SCALE)/2 - 255, (HEIGHT*SCALE)/2 + 70);
		}else if(gameState == "MENU") {
			menu.render(g);
		}
		bs.show();
	}
	
	@Override
	public void run()
	{
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();
		while(isRunning)
		{
			long now = System.nanoTime();
			delta+= (now - lastTime) / ns;
			lastTime = now;
			if (delta >=1)
			{
				tick();
				render();
				frames ++;
				delta--;
			}
			if (System.currentTimeMillis() - timer >= 1000)
			{
				System.out.println("FPS: "+frames);
				frames = 0;
				timer+=1000;
			}
		}
		stop();
	}

	@Override
	public void keyTyped(KeyEvent e) {

		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D){
			player.right = true;
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A){
			player.left = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W){
			player.up = true;
			if(gameState == "MENU") {
				menu.up = true;
			}
			
		}else if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S){
			player.down = true;
			if(gameState == "MENU") {
				menu.down = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			player.shoot = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.restartGame = true;
			if(gameState == "MENU") {
				menu.enter = true;
			}
		}
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			gameState = "MENU";
			menu.pause = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D){
			player.right = false;
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A){
			player.left = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W){
			player.up = false;
		}else if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S){
			player.down = false;
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		player.mouseShoot = true;
		player.mx = (e.getX() / 2);
		player.my = (e.getY() / 2);
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}

//problemas relatados
//1. O player toma dano sem o enemy colidir em alguns momentos
//2. O player tem os tiros saindo somente do lado esquerdo.
//3. O sprite de dano recebido no Player não está renderizando
//4. O sprite de dano recebido no Enemy não está renderizando
//5. Não renderiza seta no "novo jogo" no menu
//6. Não reproduz sons de dano ao jogador