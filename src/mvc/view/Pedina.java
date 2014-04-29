package mvc.view;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

/**
 *
 * @author Solomon Marian & Luca Negrini
 */
public class Pedina extends JToggleButton{
    
    private Image image;
    private View view;

    public Pedina(ImageIcon icon, View view) {
        super(icon);
        image = icon.getImage();
        this.view = view;
        addActionListener(view);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        int a = (this.getSize().height - image.getHeight(this));
        int b = (this.getSize().width - image.getWidth(this));
        g.drawImage(image, (b/2), (a/2), this);
    }

    @Override
    protected void paintBorder(Graphics grphcs) {
        //super.paintBorder(grphcs); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
