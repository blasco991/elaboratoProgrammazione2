package mvc.view;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JButton;

/**
 *
 * @author Solomon Marian & Luca Negrini
 */
public class Cell extends JButton{
    
    private Image image;
    protected int x, y;

    public Cell(int x, int y) {
        super();
        this.x = x;
        this.y = y;
    }
    
    

    public void setImage(Image image) {
        this.image = image;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
    }
    
}
