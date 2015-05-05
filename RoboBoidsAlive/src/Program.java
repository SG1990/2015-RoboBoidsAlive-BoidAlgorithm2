import javax.swing.JFrame;

public class Program {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		World world = World.getInstance();
		
		// Initialise and show world
		JFrame f = new JFrame();
        f.setSize(world.getBoundsX(),world.getBoundsY());
        f.setTitle("Boids Alive");
        f.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(world);    
        f.setResizable(false);
        f.setVisible(true);
		
        for(int i = 0 ; i < 25; i++) 
        	world.createBoid();   
	}

}
