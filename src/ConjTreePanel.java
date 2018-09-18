import javax.swing.*;
import java.awt.*;

public class ConjTreePanel extends JPanel {
    private Visao nt;
    public ConjTreePanel(Visao nt){
        this.nt=nt;
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.clearRect(0,0,getWidth(),getHeight());
        if(nt.getNandTree()!=null)
            nt.getNandTree().paint(g,getWidth(),getHeight());
    }
}
