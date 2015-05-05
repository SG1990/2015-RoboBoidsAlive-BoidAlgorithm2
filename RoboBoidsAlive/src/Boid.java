import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Random;


public class Boid extends WorldObject {
	
	private final int size = 14;			//20
	private final double radius = 40 ;		//50
	private final double angle = 20;		//120
	private final double minDistance = 20;	//20
	private final double maxVelocity = 3;
	private double vx, vy;
	
	public double getVX() { return vx; }
	public void setVX(double vx) { this.vx = vx; }
	
	public double getVY() { return vy; }
	public void setVY(double vy) { this.vy = vy; }
	
	public double getSize() { return size; }
	
	public Boid() {
		Random r = new Random();
		vx = r.nextInt(4) - 2;
		vy = r.nextInt(4) - 2;
	}
	
	public void move() {		
		//Apply Boid rules
		double[] v1, v2, v3, v4;
		
		//get neighbours		
		ArrayList<Boid> neighbours = getNeighbours();
		
		v1 = matchVelocity(neighbours); 
		v2 = keepDistance(neighbours);
		v3 = flyTowardsTheCentre(neighbours);
		v4 = addNoise();
		
		double oldVX = vx;
		double oldVY = vy;
		
		vx = vx + v1[0] + v2[0] + v3[0] + v4[0];
		vy = vy + v1[1] + v2[1] + v3[1] + v4[1];
		
		if(neighbours.size() == 0){
			vx = oldVX;
			vy = oldVY;
		}
		
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
			double xDiff = n.getX() - x; //to local coords
			double yDiff = n.getY() - y;
			
//			double[] xyDiff = getLocalNeighbour(n);
			
			double d = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
			if(d <= minDistance){						
				
				v2[0] -= (((xDiff * minDistance) / d) - xDiff) * (0.15 / neighbours.size());
        		v2[1] -= (((yDiff * minDistance) / d) - yDiff) * (0.15 / neighbours.size());
				
//				v2[0] -= (((xyDiff[0] * minDistance) / d) - xyDiff[0]) * (0.15 / neighbours.size());
//        		v2[1] -= (((xyDiff[1] * minDistance) / d) - xyDiff[1]) * (0.15 / neighbours.size());
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
			avgD = avgD + Math.sqrt(Math.pow(n.getX() - x, 2) + Math.pow(n.getY() - y, 2));
		}
		
		if(neighbours.size() > 0)
			avgD = avgD / neighbours.size();
		
		for(Boid n : neighbours) {
			double xDiff = n.getX() - x;
			double yDiff = n.getY() - y;
			
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
		
		v4[0] = ((Math.random() - 0.5) * maxVelocity) * 0.1;
		v4[1] = ((Math.random() - 0.5) * maxVelocity) * 0.1;
		
		return v4;
	}
	
//	private double[] getLocalNeighbour(Boid n) {		
//		double xDiff = Math.abs(n.getX() - x);
//		double yDiff = Math.abs(n.getY() - y);
//		double d = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2)); //given
//		
//		//translate neighbour
//		double nX = n.getX() - x;
//		double nY = n.getY() - y;
//		
//		//rotate neighbour
//		double a = getAngleBetween(vx, vy, 0, 2);
//		if (vx >= 0) a = -a;
//		
//		double oldnX = nX;
//		nX = (nX * Math.cos(a)) + (nY * Math.sin(a));	
//		nY = (-oldnX * Math.sin(a)) + (nY * Math.cos(a));
//		
//		//get the angle between neighbour and self
//		double b = getAngleBetween(nX, nY, 0, 2);   //given
//		if (nX >= 0) b = -b;
//		
//		//get the neighbour's coordinates; TODO: from maps!
//		
//				
//		//rotate back
//		a = -a;
//		double[] localCoords = new double[2];
//		localCoords[0] = (nX * Math.cos(a)) + (nY * Math.sin(a));
//		localCoords[1] = (-nX * Math.sin(a)) + (nY * Math.cos(a));
//		
//		return localCoords;
//	}
	
	private double[] toLocal(double[] coords) {		
		double xDiff = Math.abs(coords[0] - x);
		double yDiff = Math.abs(coords[1] - y);
		double d = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2)); //given
		
		//translate neighbour
		double nX = coords[0] - x;
		double nY = coords[1] - y;
		
		//rotate neighbour
		double a = getAngleBetween(vx, vy, 0, 2);
		if (vx >= 0) a = -a;
		
		double[] localCoords = new double[2];
		localCoords[0] = (nX * Math.cos(a)) + (nY * Math.sin(a));
		localCoords[1] = (-nX * Math.sin(a)) + (nY * Math.cos(a));
		
		return localCoords;
	}
	
	private double[] toGlobal(double[] coords) {		
		double xDiff = Math.abs(coords[0] - x);
		double yDiff = Math.abs(coords[1] - y);
		double d = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2)); //given
		
		//rotate neighbour
		double a = getAngleBetween(vx, vy, 0, 2);
		if (vx < 0) a = -a;
		
		double[] localCoords = new double[2];
		localCoords[0] = (coords[0] * Math.cos(a)) + (coords[1] * Math.sin(a));
		localCoords[1] = (-coords[0] * Math.sin(a)) + (coords[1] * Math.cos(a));
		
		//translate neighbour
		localCoords[0] += x;
		localCoords[1] += y;		
		
		return localCoords;
	}
	
//	private double getDirectedAngleBetween(double xDiff, double yDiff) {
//	double alpha = getAngleBetween(vx, vy, xDiff, yDiff);
//			
//	//check direction
//	double ad = getAngleBetween(xDiff, yDiff, 2, 0);
//	double av = getAngleBetween(vx, vy, 2, 0);
//	
//	if(vy >= 0 && yDiff > 0) {
//		if(av < ad) alpha = -alpha;
//	}
//	else if (vy < 0 && yDiff < 0) {
//		if(av >= ad) alpha = -alpha;
//	}
//	else if (vy >= 0 && yDiff < 0) {
//		if(av + ad >= 180) alpha = -alpha;
//	}
//	else {
//		if(av + ad < 180) alpha = -alpha;
//	}
//	
//	return alpha;
//}
	
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
		if(y < -size) y = world.getBoundsY();
		if(y > world.getBoundsY()) y = 0;		
	}

	@Override
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create(); //TODO Move to boid class
        g2d.setColor(Color.orange);
        
        AffineTransform oldTransform = g2d.getTransform();
        g2d.translate(x, y);
        
        Path2D.Double triangle = new Path2D.Double();  
        triangle.moveTo(-(size/2), size);
        triangle.lineTo(0, 0);
        triangle.lineTo((size/2), size);  
        triangle.closePath();        
        
        //rotate
        if (vx < 0){
     	      g2d.rotate(Math.toRadians(-90.0 + Math.atan(vy/vx)*180.0/Math.PI));
        }
        else {
             g2d.rotate(Math.toRadians(90.0 + Math.atan(vy/vx)*180.0/Math.PI));
        }
        
        g2d.fill(triangle);        
        g2d.setTransform(oldTransform);        
        g2d.dispose();		
	}
}
