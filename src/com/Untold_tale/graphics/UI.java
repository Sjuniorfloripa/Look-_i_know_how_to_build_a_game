package com.Untold_tale.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import Untold_Tale.main.Game;

public class UI {
	public void render(Graphics g) {
		g.setColor(Color.red);
		g.fillRect(10,10,70,10);
		g.setColor(Color.green);
		g.fillRect(10, 10, (int)((Game.player.life/Game.player.maxLife)*70), 10);
		g.setColor(Color.black);
		g.setFont(new Font("arial", Font.PLAIN, 10));
		g.drawString((int)Game.player.life+"/"+(int)Game.player.maxLife, 12, 18);
	}
}
