import java.awt.Graphics;

import javax.swing.JPanel;


public abstract class WorldObject {

	private static final long serialVersionUID = 1L;
	protected double x, y;
    
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    
    public abstract void draw(Graphics g);
}
