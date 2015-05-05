import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;


public final class World extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private Random r = new Random();
	
	private static final int boundsX = 800;
	private static final int boundsY = 600;
	
	public ArrayList<Boid> boids = new ArrayList<Boid>();
	
	private static World instance = null;	
	private World() {
		this.setBackground(Color.white);
		
		Timer timer = new Timer(1000/60, this);
        timer.start();
	}
	
	public static World getInstance(){
		if(instance == null)
			instance = new World();
		
		return instance;
	}
	
	public int getBoundsX() { return boundsX; }
	public int getBoundsY() { return boundsY; }
	
	public void createBoid(){
		Boid b = new Boid() ;
        b.setX(r.nextInt(getBoundsX() - (int) b.getSize()));
        b.setY(r.nextInt(getBoundsY() - (int) b.getSize()));       

        boids.add(b);       
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintBoids(g);
    }

	private void paintBoids(Graphics g) {
		for(Boid b : boids)
			b.draw(g);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for(Boid b : boids)
			b.move();	
		
		repaint();
	}	
}
