import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PeircePanel extends JPanel {
    private Visao v;
    private NandTree selected;
    private NandTree secondSelect;

    public PeircePanel(Visao v){
        this.v = v;
        selected = null;
        secondSelect =null;
        addMouseListener(
                new MouseAdapter(){
                    @Override
                    public void mouseClicked(MouseEvent mouseEvent) {
                        super.mouseClicked(mouseEvent);
                        if(v.getNandTree()!=null) {
                            EvalRules current = v.getEvalRuleNumber();
                            if(!v.selectingFirstElement())
                                secondSelect = v.getNandTree().serachPoint(mouseEvent.getPoint());
                            else
                                selected = v.getNandTree().serachPoint(mouseEvent.getPoint());
                            repaint();
                        }
                    }
                }
        );
    }
    public NandTree getSelected() {
        return selected;
    }

    public NandTree getSecondSelect() { return secondSelect; }

    public void clear(){
        selected=null;
        secondSelect=null;
        repaint();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.clearRect(0,0,getWidth(),getHeight());
        if(v.getNandTree()!=null){
            v.getNandTree().updateCutLevel();
            v.getNandTree().paintPeirce(g,getWidth(),getHeight(),selected,secondSelect);
        }
    }
}
