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
	
	private static final int boundsX = 800;
	private static final int boundsY = 600;
	
	public int getBoundsX() { return boundsX; }
	public int getBoundsY() { return boundsY; }
	
	public ArrayList<Boid> boids = new ArrayList<Boid>();
	
	public double[][][] lookupTable  = new double[][][] {
			{{-1,14},{-1,25},{1,36},{2,43},{2,53},{2,64},{0,75},{0,86},{0/97}},
			{{6,13},{3,23},{7,29},{9,42},{6,57},{6,68},{7,78},{5,86},{5,96}},
			{{13,11},{9,22},{19,31},{17,44},{18,56},{19,69},{22,77},{12,87},{4,96}},
			{{20,11},{8,23},{25,31},{25,44},{25,54},{25,67},{25,75},{15,84},{6,93}},
			{{18,13},{8,23},{25,30},{25,46},{25,56},{25,60},{22,78},{14,89},{4,96}},
			{{18,13},{9,22},{25,25},{25,37},{25,49},{25,62},{0,0},{0,0},{0,0}},
			{{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}},
			{{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}},
			{{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}},
			{{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}}
	};	
	
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
