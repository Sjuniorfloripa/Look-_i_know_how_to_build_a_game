package Untold_Tale.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Menu {
	
	public String[] options = {"Novo jogo", "Carregar jogo", "Sair"};
	
	public int currentOption = 0;
	public int maxOption = options.length -1;
	
	public boolean up,down, enter;
	public boolean pause = false;
	
	public void tick() {
		if(up) {
			up=false;
			currentOption--;
			if(currentOption < 0) {
				currentOption = maxOption;
			}
		}
		if(down) {
			down=false;
			currentOption++;
			if(currentOption > maxOption) {
				currentOption = 0;
			}
		}
		if(enter) {
			Sound.music.loop();
			enter = false;
			if(options[currentOption] == "Novo jogo" || options[currentOption] == "continuar") {
				Game.gameState = "NORMAL";
				pause = false;
			}else if(options[currentOption] == "Sair") {
				System.exit(1);
			}
		}
	}
	
	public void render (Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(new Color(0,0,0,100));
		g2.fillRect(0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);
		g.setColor(Color.WHITE);
		g.setFont(new Font("arial", Font.BOLD,35));
		g.drawString(">Look! I know how to build a Game<", (Game.WIDTH*Game.SCALE)/2 -295, (Game.HEIGHT*Game.SCALE)/2 -150);
		
		//opções de menu
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD,30));
		if(pause == false)
		g.drawString("Novo jogo", (Game.WIDTH*Game.SCALE)/2 -100, 200);
		else
			g.drawString("Resumir", (Game.WIDTH*Game.SCALE)/2 -100, 200);
		g.drawString("Carregar jogo", (Game.WIDTH*Game.SCALE)/2 -100, 250);
		g.drawString("Sair", (Game.WIDTH*Game.SCALE)/2 -100, 300);
		
		if(options[currentOption] == "Novo jogo") {
			g.drawString(">", (Game.WIDTH*Game.SCALE)/2 -120, 200);
		}else if(options[currentOption] == "Carregar jogo") {
			g.drawString(">", (Game.WIDTH*Game.SCALE)/2 -120, 250);
		}else if(options[currentOption] == "Sair") {
			g.drawString(">", (Game.WIDTH*Game.SCALE)/2 -120, 300);
		}
	}
}
