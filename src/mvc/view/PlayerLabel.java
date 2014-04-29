package mvc.view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

/**
 *
 * @author Solomon Marian & Luca Negrini
 */
public class PlayerLabel extends JPanel {

    private final JLabel playerName;
    private final ImageIcon icon, border;

    public PlayerLabel(String name, boolean position) {
        // true = bottom, false = top
        super();
        setLayout(new FlowLayout());
        setFont(new Font("Arial", Font.BOLD, 5));
        playerName = new JLabel(name.toUpperCase());
        add(playerName);
        int top = position ? 2 : 4;
        int bottom = position ? 4 : 2;
        border = new ImageIcon(getClass().getResource("/resources/images3.jpg"));
        setBorder(new MatteBorder(top, 4, bottom, 4, border));
        icon = new ImageIcon(getClass().getResource("/resources/images2.jpg"));
    }

    public void hilightPlayer() {
        playerName.setForeground(Color.red);
    }

    public void lowlightPlayer() {
        playerName.setForeground(Color.black);
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        super.paintComponent(grphcs);
        grphcs.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);  
    }
    
    

}
