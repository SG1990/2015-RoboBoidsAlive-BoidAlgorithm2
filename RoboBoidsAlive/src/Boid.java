import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Random;


public class Boid extends WorldObject {
	
	//private final int size = 14;
	private final double sizeX = 12.5;
    private final double sizeY = 22.5;
	private final double radius = 35;		
	private final double angle = 20;		
	private final double minDistance = 17;	
	private final double strictMinDistance = 11.5;
	private final double maxVelocity = 3;
	private double vx, vy, oldvx, oldvy;
	
	public double getVX() { return vx; }
	public void setVX(double vx) { this.vx = vx; }
	
	public double getVY() { return vy; }
	public void setVY(double vy) { this.vy = vy; }
	
	public double getSizeX() { return sizeY; }
	public double getSizeY() { return sizeY; }
	
	public Boid() {
		Random r = new Random();
		vx = r.nextInt(4) - 2;
		vy = r.nextInt(4) - 2;
	}
	
	public void move() {		
		//get neighbours		
		ArrayList<Boid> neighbours = getNeighbours();		
				
		//Apply Boid rules
		if(neighbours.size() != 0){
			double[] v1, v2, v3, v4;
					
			v1 = matchVelocity(neighbours); 
			v2 = keepDistance(neighbours);
			v3 = flyTowardsTheCentre(neighbours);
			v4 = addNoise();
					
			oldvx = vx;
			oldvy = vy;
			
			vx = vx + v1[0] + v2[0] + v3[0] + v4[0];
			vy = vy + v1[1] + v2[1] + v3[1] + v4[1];
			
			if((Math.abs(vx - oldvx) > 0.15))
					vx = oldvx;
			if((Math.abs(vy - oldvy) > 0.15))
					vy = oldvy;
		}	
		
//		World world = World.getInstance(); //TODO: Not driving through other robots
//		for(Boid n : world.boids) {
//			if(n != this) {
//				
//			}			
//		}
		
		x = x + vx;
		y = y + vy;
		
		checkVelocity();
		checkBounds();
	}
	
	private ArrayList<Boid> getNeighbours() {
		World world = World.getInstance();
		ArrayList<Boid> neighbours = new ArrayList<Boid>();
		
		for(Boid n : world.boids) {
			if(n != this) {
				//check if in range - if distance is less than our radius; get from the readings
				if(Math.sqrt(Math.pow(n.getX() - x, 2) + Math.pow(n.getY() - y, 2)) <= radius)	{				
					if(getAngleBetween(vx, vy, n.getX() - x, n.getY() - y) <= angle)
						neighbours.add(n);			
				}
			}			
		}
		
		return neighbours;
	}	

	private double[] matchVelocity(ArrayList<Boid> neighbours) { //the tricky one, we need to get the speeds somehow
		double[] v1 = new double[2];
		v1[0] = 0;
		v1[1] = 0;
		
		for(Boid n : neighbours) {
			v1[0] += n.getVX();
			v1[1] += n.getVY();
		}		
		
		if(neighbours.size() > 0) {
			v1[0] = v1[0] / neighbours.size();
			v1[1] = v1[1] / neighbours.size();
		}
		
		v1[0] = (v1[0] - vx) * 0.1;
		v1[1] = (v1[1] - vy) * 0.1;
		
		return v1;
	}
	
	private double[] keepDistance(ArrayList<Boid> neighbours) {	
		double[] v2 = new double[2];
		v2[0] = 0;
		v2[1] = 0;
		
		for(Boid n : neighbours) {
			double xDiff = n.getX() - x;
			double yDiff = n.getY() - y;
			
			double[] roboCoords = getRobotCoords(n.getX(), n.getY());
			double rxDiff = roboCoords[0] - x;
			double ryDiff = roboCoords[1] - y;
			
			double d = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
			if(d <= minDistance){						
				
				v2[0] -= (((rxDiff * minDistance) / d) - rxDiff) * (0.15 / neighbours.size());
        		v2[1] -= (((ryDiff * minDistance) / d) - ryDiff) * (0.15 / neighbours.size());
			}
		}
		
		return v2;
	}

	private double[] flyTowardsTheCentre(ArrayList<Boid> neighbours) {	//ok!
		double avgD = 0;
		double[] v3 = new double[2];
		v3[0] = 0;
		v3[1] = 0;
		
		for(Boid n : neighbours) {
			double[] roboCoords = getRobotCoords(n.getX(), n.getY());
			avgD = avgD + Math.sqrt(Math.pow(roboCoords[0] - x, 2) + Math.pow(roboCoords[1] - y, 2));
		}
		
		if(neighbours.size() > 0)
			avgD = avgD / neighbours.size();
		
		for(Boid n : neighbours) {
			double[] roboCoords = getRobotCoords(n.getX(), n.getY());
			double xDiff = roboCoords[0] - x;
			double yDiff = roboCoords[1] - y;
			
			if(Math.abs(xDiff) > minDistance) {
				double d = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
				
				v3[0] += ((xDiff * (d - avgD))/d) * (0.15 / neighbours.size()); 
				v3[1] += ((yDiff * (d - avgD))/d) * (0.15 / neighbours.size());
			}			 
		}
		
		return v3;		
	}
	
	private double[] addNoise() {
		double[] v4 = new double[2];
		v4[0] = 0;
		v4[1] = 0;
		
		v4[0] = ((Math.random() - 0.5) * maxVelocity) * 0.05;
		v4[1] = ((Math.random() - 0.5) * maxVelocity) * 0.05;
		
		return v4;
	}
	
	private double[] getRobotCoords(double nx, double ny) {
		World world = World.getInstance();
		double[] gCoords = new double[] {nx, ny};
		
		//get xy from the local system
		double[] lCoords = toLocal(gCoords);
		
		//calculate d and round it up
		double realD = Math.sqrt(Math.pow(lCoords[0], 2) + Math.pow(lCoords[1], 2));
		if(realD < 5) realD = 5;
		else if(realD > 45) realD = 45;
		else realD = Math.round(realD/5) * 5;
		
		//calculate alpha and round it up
		double realA = getAngleBetween(lCoords[0], lCoords[1], 0, 2);
		realA = Math.toDegrees(realA);
		if (realA > 90) realA = 90;
		else realA = Math.round(realA/10) * 10;			
		
		//get values from the lookup table
		double tA = world.lookupTable[(int) realA/10][(int)realD/5 - 1][0];
		double tD = world.lookupTable[(int) realA/10][(int)realD/5 - 1][1];
		tD = percentToPX(tD);
		
		//get xy according to the lookup table data
		double rX, rY;
		tA = Math.toRadians(tA);
		rX = tD * Math.sin(tA);
		rY = tD * Math.cos(tA);
		if(lCoords[0] < 0) rX = -rX;
		
		//transform to global
		double[] rCoords = new double[] {rX, rY};
		return toGlobal(rCoords);
	}
	
	private double[] toLocal(double[] coords) {				
		//translate neighbour
		double nX = coords[0] - x;
		double nY = coords[1] - y;
		
		//rotate neighbour
		double a = getAngleBetween(vx, vy, 0, 2);
		if (vx < 0) a = -a;
		
		double[] localCoords = new double[2];
		localCoords[0] = (nX * Math.cos(a)) - (nY * Math.sin(a));
		localCoords[1] = (nX * Math.sin(a)) + (nY * Math.cos(a));
		
		return localCoords;
	}
	
	private double[] toGlobal(double[] coords) {				
		//rotate neighbour
		double a = getAngleBetween(vx, vy, 0, 2);
		if (vx >= 0) a = -a;
		
		double[] localCoords = new double[2];
		localCoords[0] = (coords[0] * Math.cos(a)) - (coords[1] * Math.sin(a));
		localCoords[1] = (coords[0] * Math.sin(a)) + (coords[1] * Math.cos(a));
		
		//translate neighbour
		localCoords[0] += x;
		localCoords[1] += y;		
		
		return localCoords;
	}
	
	private double getAngleBetween(double x1, double y1, double x2, double y2) {		
		double aob = (x1 * x2) + (y1 * y2);
		double axb = (Math.sqrt(Math.pow(x1, 2) + Math.pow(y1, 2))) * 
				(Math.sqrt(Math.pow(x2, 2) + Math.pow(y2, 2)));
		
		return Math.acos(aob/axb);
	}
	
	private void checkVelocity() {
		if (Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2)) > maxVelocity){
	    	vx *= 0.75;
			vy *= 0.75;			
		}
	}
	
	private void checkBounds() {
		World world = World.getInstance() ;
		if(x < 0) x = world.getBoundsX();
		if(x > world.getBoundsX()) x = 0;
		if(y < -sizeY) y = world.getBoundsY();
		if(y > world.getBoundsY()) y = 0;		
	}
	
	public static double percentToPX(double percent){
		//the ratio chosen due to the greatest consistency of measurements at 40cm
		return percent * (40 / 86.5);
	}

	@Override
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create(); //TODO Move to boid class
        g2d.setColor(Color.orange);
        
        AffineTransform oldTransform = g2d.getTransform();
        g2d.translate(x, y);
        
        GeneralPath rectangle = new GeneralPath();  
        rectangle.moveTo(-sizeX/2, -sizeY/2);
        rectangle.lineTo(-sizeX/2, sizeY/2);
        rectangle.lineTo(sizeX/2, sizeY/2);
        rectangle.lineTo(sizeX/2, -sizeY/2);
        rectangle.closePath();
        
        GeneralPath head = new GeneralPath();  
        head.moveTo(-sizeX/2, -sizeY/2);
        head.lineTo(-sizeX/2, -sizeY/4);
        head.lineTo(sizeX/2, -sizeY/4);
        head.lineTo(sizeX/2, -sizeY/2);
        head.closePath();   
        
        //rotate
        if (vx < 0){
     	      g2d.rotate(Math.toRadians(-90.0 + Math.atan(vy/vx)*180.0/Math.PI));
        }
        else {
             g2d.rotate(Math.toRadians(90.0 + Math.atan(vy/vx)*180.0/Math.PI));
        }
        
        g2d.fill(rectangle);
        g2d.setColor(Color.red);
        g2d.fill(head);   
        g2d.setTransform(oldTransform);        
        g2d.dispose();		
	}
}
