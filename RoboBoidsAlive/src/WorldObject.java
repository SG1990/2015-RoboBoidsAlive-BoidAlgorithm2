import java.awt.Graphics;


public abstract class WorldObject {
	protected double x, y;
    
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    
    public abstract void draw(Graphics g);
}
