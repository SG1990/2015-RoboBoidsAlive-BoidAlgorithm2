import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;


public final class World extends JPanel implements ActionListener, KeyListener, MouseListener {

	private static final long serialVersionUID = 1L;
	private Random r = new Random();
	
	private static World instance = null;	
	private World() {
		this.setBackground(Color.white);
		this.setSize(new Dimension(boundsX, boundsY));
		this.addKeyListener(this);
		this.addMouseListener(this);
		
        timer.start();
        timerStatus = true;
        
        setFocusable(true);
        requestFocusInWindow();
	}
	
	public static World getInstance(){
		if(instance == null)
			instance = new World();
		
		return instance;
	}
	
	Timer timer = new Timer(1000/60, this);
	private static boolean timerStatus;
	
	private static final int boundsX = 800;
	private static final int boundsY = 600;
	
	public int getBoundsX() { return boundsX; }
	public int getBoundsY() { return boundsY; }
	
	public ArrayList<Boid> boids = new ArrayList<Boid>();
	
	public double[][][] lookupTable  = new double[][][] {
			{{1,14},{1,25},{1,36},{2,43},{2,53},{2,64},{0,75},{0,86},{0/97}},
			{{6,13},{3,23},{7,29},{9,42},{6,57},{6,68},{7,78},{5,86},{5,96}},
			{{13,11},{9,22},{19,31},{17,44},{18,56},{19,69},{22,77},{12,87},{4,96}},
			{{20,11},{8,23},{25,31},{25,44},{25,54},{25,67},{25,75},{15,84},{6,93}},
			{{18,13},{8,23},{25,30},{25,46},{25,56},{25,60},{22,78},{14,89},{4,96}},
			{{18,13},{9,22},{25,25},{25,37},{25,49},{25,62},{25,75},{19,88},{12,90}},
			{{8,6},{14,17},{7,24},{25,30},{25,42},{25,50},{19,80},{12,86},{4,94}},
			{{8,6},{14,17},{5,26},{25,34},{25,46},{25,53},{25,66},{25,87},{19,82}},
			{{18,13},{10,21},{25,32},{25,43},{25,52},{25,63},{25,75},{14,86},{8,92}},
			{{23,8},{11,20},{25,30},{25,46},{25,58},{25,69},{0,0},{4,96},{0,100}}
	};
	
	private Boid selectedBoid = null;
	private void setSelected(Boid b){
		if (selectedBoid != null)
			selectedBoid.setSelected(false);
		
		selectedBoid = b;
		
		if (b != null)
			selectedBoid.setSelected(true);
	}
		
	public void createBoid(){
		Boid b = new Boid() ;
        b.setX(r.nextInt(getBoundsX() - (int) b.getSizeX()));
        b.setY(r.nextInt(getBoundsY() - (int) b.getSizeY()));       

        boids.add(b);       
	}
	
	public void createBoid(double x, double y){
		Boid b = new Boid() ;
        b.setX(x);
        b.setY(y);       

        boids.add(b);       
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintBoids(g);
    }

	private void paintBoids(Graphics g) {
		
		if (selectedBoid != null)
			selectedBoid.drawScanArea(g);
		
		for(Boid b : boids)
			b.draw(g);
		drawSelectedBoidInfo(g);
	}

	private void drawSelectedBoidInfo(Graphics g) {
		if (selectedBoid != null){
			
			Graphics2D g2d = (Graphics2D) g.create();
			
        	g2d.setColor(Color.black);
        	g2d.drawString("Pos ["+(int)selectedBoid.x+","+(int)selectedBoid.y+"]", (int)selectedBoid.x+5, (int)selectedBoid.y+13); 
        	g2d.drawString("Vel ["+String.format("%1$,.2f",selectedBoid.vx)+","+String.format("%1$,.2f", selectedBoid.vy)+"]", (int)selectedBoid.x+5, (int)selectedBoid.y); 
        	g2d.drawString("Ngb ["+selectedBoid.getNeighbours().size()+"]", (int)selectedBoid.x+5, (int)selectedBoid.y-13); 
        	
        	g2d.dispose();
        }
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for(Boid b : boids)
			b.move();	
		
		repaint();
	}	

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() != ' ')
			return;
		
		if(timerStatus) {
			timer.stop();
			timerStatus = false;
			System.out.println("Pause!");
		}
		else {
			timer.start();
			timerStatus = true;
			System.out.println("Unpause!");
		}		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void mouseClicked(MouseEvent e) {
		for(Boid b : boids){
			if (dist(e.getPoint().x, e.getPoint().y, (int)b.x, (int)b.y) < b.getRadius()){
				
				if (b == selectedBoid)
					setSelected(null);
				else 
					setSelected(b);
				
				this.repaint();
				return;
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}
	
	private double dist(int x1, int y1, int x2, int y2){
		
		int x = x2 - x1;
		int y = y2 - y1;
		
		return Math.sqrt(x*x + y*y);
	}
}
