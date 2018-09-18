import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.Tree;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.round;

public class Visao {
    private JButton evalAb;
    private JPanel panel1;
    private JTextField form;
    private JButton saveFigureButton;
    private JButton randomGen;
    private JPanel paintArea;
    private JTextField auxVariableEval;
    private JButton nextStateButton;
    private JButton backStateButton;
    private JButton historyButton;
    private JPanel ConjTreePanel;
    private JComboBox premiseCBox;
    private JButton removePremiseButton;
    private JRadioButton addDoubleNegationAbs;
    private JRadioButton removeDoubleNegationAbs;
    private JButton addPremiseButton;
    private JRadioButton iterationAbs;
    private JRadioButton deiterationAbs;
    private NandTree ntTemp;
    private List<NandTree> saves;
    private Log log;
    private JTabbedPane mainPane;
    private JButton applyRuleButton;
    private JRadioButton addDoubleNegationP;
    private JRadioButton firstItemSelectionButton;
    private JRadioButton secondItemRadioButton;
    private JButton clearSelectionButton;
    private JButton evalP;
    private JRadioButton removeDoubleNegationP;
    private JRadioButton iterationP;
    private JRadioButton deiterationP;
    private JRadioButton evenCutP;
    private JRadioButton oddCutP;
    private JButton applyPRuleButton;
    private JPanel ConclusionField;
    private JPanel AbsurdumPanel;
    private JPanel PeircePanel;
    private JTabbedPane rulePane;
    private int stateIndex;
    private VisaoState state;


    public Visao() {
        evalAb.addMouseListener(new MouseListner(this, TriggerType.EvalAbsButton));
        evalP.addMouseListener(new MouseListner(this, TriggerType.EvalPButton));
        randomGen.addMouseListener(new MouseListner(this, TriggerType.RandomButton));
        saveFigureButton.addMouseListener(new MouseListner(this, TriggerType.SaveButton));
        backStateButton.addMouseListener(new MouseListner(this,TriggerType.PreviousButton));
        nextStateButton.addMouseListener(new MouseListner(this,TriggerType.NextButton));
        historyButton.addMouseListener(new MouseListner(this,TriggerType.ViewLogButton));
        addPremiseButton.addMouseListener(new MouseListner(this,TriggerType.AddPremiseButton));
        removePremiseButton.addMouseListener(new MouseListner(this,TriggerType.RemovePremiseButton));
        applyRuleButton.addMouseListener(new MouseListner(this,TriggerType.ApplyRule));
        applyPRuleButton.addMouseListener(new MouseListner(this,TriggerType.ApplyRule));
        clearSelectionButton.addMouseListener(new MouseListner(this,TriggerType.ClearSelection));
        saves = new ArrayList<NandTree>();
        state = VisaoState.None;
        log = new Log();
        stateIndex = -1;
        ntTemp = null;
    }
    public void removePremise(){
        if(premiseCBox.getItemCount()>0){
            premiseCBox.removeItemAt(premiseCBox.getSelectedIndex());
        }
    }
    public void clearSelection(){
        ((PeircePanel)paintArea).clear();
    }
    public void addPremise(){
        try {
            NandTree tnt = buildTree(form.getText());
            premiseCBox.addItem(form.getText());
            form.setText("");
        }catch (RuntimeException ex){
            JFrame frame = new JFrame("Syntax Error");
            JOptionPane.showMessageDialog(frame, ex.getMessage());
        }
    }

    private void saveState(EvalRules rule){
        String operation;
        switch(rule){
            case DoubleNegationAdd:
                operation = "Add Double Negation";
                break;
            case DoubleNegationRemove:
                operation = "Remove Double Negation";
                break;
            case Iteration:
                operation = "Iteration";
                break;
            case Deiteration:
                operation = "Deiteration";
                break;
            case EvenRemove:
                operation = "Remove at even level";
                break;
            case OddAdd:
                operation = "Add at odd level";
                break;
            default:
                operation = "-";
                break;
        }
        saveState(operation);
    }
    private void saveState(String op){
        stateIndex++;
        while(saves.size()>stateIndex){
            saves.remove(stateIndex);
            log.remove(stateIndex);
        }
        if(ntTemp!=null) {
            LogEntry le = new LogEntry();
            le.setOperation(op);
            le.setState(ntTemp.inlineString());
            log.add(le);
            log.update(stateIndex);
            saves.add(stateIndex, (NandTree) ntTemp.clone());
        }
        else
            saves.add(stateIndex,null);
    }
    private void clearHist(){
        stateIndex=-1;
        log = new Log();
        saves = new ArrayList<NandTree>();
    }
    private void createUIComponents() {
        // TODO: place custom component creation code here
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            paintArea = new PeircePanel(this);
            ConjTreePanel = new ConjTreePanel(this);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
    public NandTree buildTree(String s) throws RuntimeException{
        System.out.println("Input:"+s);
        CharStream in = CharStreams.fromString(s);
        LogicLexer lexer = new LogicLexer(in);
        lexer.addErrorListener(new ErrorListner());
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LogicParser parser = new LogicParser(tokens);
        System.out.println("Parser built.");
        parser.addErrorListener(new ErrorListner());
        Tree tree = parser.start();
        System.out.println("Parser Tree built.");
        NandTree nt = new NandTree(tree.getChild(0),parser.getRuleNames());
        System.out.println("Tree rebuilt.");
        nt = NandTree.transformNandTree(nt);
        System.out.println("Nand tree built.");
        return nt;
    }

    public void evaluateP(){
        try{
            if(premiseCBox.getItemCount()>0) {
                String conclusion = form.getText();
                for(int i=0;i<premiseCBox.getItemCount();i++){
                    String prem = (String) premiseCBox.getItemAt(i);
                    conclusion += " and ("+prem+")";
                }
                ntTemp = buildTree(conclusion);
                clearHist();
                saveState("-");
                paintArea.repaint();
                ConjTreePanel.repaint();
                state = VisaoState.Peirce;
                rulePane.setEnabledAt(0,false);
                rulePane.setEnabledAt(1,true);
                rulePane.setSelectedIndex(1);
            }else{
                JFrame frame = new JFrame("Error");
                JOptionPane.showMessageDialog(frame, "Set at least one premise.");
            }
        } catch (RuntimeException ex){
            JFrame frame = new JFrame("Syntax Error");
            JOptionPane.showMessageDialog(frame, ex.getMessage());
        }

    }
    public void evaluateAbs(){
        try{
            if(premiseCBox.getItemCount()>0) {
                String conclusion = "not( " + form.getText() + ")";
                for(int i=0;i<premiseCBox.getItemCount();i++){
                    String prem = (String) premiseCBox.getItemAt(i);
                    conclusion += " and ("+prem+")";
                }
                ntTemp = buildTree(conclusion);
                clearHist();
                saveState("-");
                paintArea.repaint();
                ConjTreePanel.repaint();
                state = VisaoState.Absurdum;
                rulePane.setEnabledAt(0,true);
                rulePane.setEnabledAt(1,false);
                rulePane.setSelectedIndex(0);
            }else{
                JFrame frame = new JFrame("Error");
                JOptionPane.showMessageDialog(frame, "Set at least one premise.");
            }
        } catch (RuntimeException ex){
            JFrame frame = new JFrame("Syntax Error");
            JOptionPane.showMessageDialog(frame, ex.getMessage());
        }

    }
    private int randomStringList( List<String> s,String c){
        List<Integer> temp = new ArrayList<Integer>();
        for(int i=0;i<s.size();i++){
            if(s.get(i).compareTo(c)==0){
                temp.add(i);
            }
        }
        if(temp.size()>1) {
            Collections.shuffle(temp);
        }
        return temp.get(0);
    }
    public void generateRandomInput(){
        String newExpr = "";
        List<String> temp = new ArrayList<String>();
        temp.add("expr");
        int exprLen = 3;
        for(int i=0;i<exprLen;i++){
            int randomPosition = randomStringList(temp,"expr");
            int rand = (int) round(Math.random()*4.0);
            switch(rand){
                case 0:
                    temp.set(randomPosition,"(");
                    temp.add(randomPosition+1,"expr");
                    temp.add(randomPosition+2,")");
                    break;
                case 1:
                    temp.set(randomPosition,"not ");
                    temp.add(randomPosition+1,"expr");
                    break;
                case 2:
                    temp.set(randomPosition,"expr");
                    temp.add(randomPosition+1," and ");
                    temp.add(randomPosition+2,"expr");
                    break;
                case 3:
                    temp.set(randomPosition,"expr");
                    temp.add(randomPosition+1," or ");
                    temp.add(randomPosition+2,"expr");
                    break;
                case 4:
                    temp.set(randomPosition, "expr");
                    temp.add(randomPosition+1," -> ");
                    temp.add(randomPosition+2,"expr");
                    break;
            }
        }
        for(String s:temp){
            if(s.compareTo("expr")==0){
                newExpr += (char)((int)(Math.random()*26)+(int)'a');
            }else {
                newExpr += s;
            }
        }
        form.setText(newExpr);
    }
    public void plotTree(){
        this.plotTree(form.getText());
    }
    public void plotTree(String s){
        try {
            CharStream in = CharStreams.fromString(s);
            LogicLexer lexer = new LogicLexer(in);
            lexer.addErrorListener(new ErrorListner());
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            LogicParser parser = new LogicParser(tokens);
            parser.addErrorListener(new ErrorListner());
            Tree tree = parser.start();
            JFrame frame = new JFrame("Abstract Syntax Tree");
            JPanel panel = new JPanel();
            NandTree nt = new NandTree(tree.getChild(0),parser.getRuleNames());
            nt = NandTree.transformNandTree(nt);
            System.out.print(nt.toString());
            TreeViewer viewr = new TreeViewer(Arrays.asList(parser.getRuleNames()), tree.getChild(0));
            panel.add(viewr);
            frame.add(panel);
            frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            frame.setSize(200,200);
            frame.setVisible(true);
        }catch (RuntimeException ex){
            JFrame frame = new JFrame("Syntax Error");
            JOptionPane.showMessageDialog(frame, ex.getMessage());
        }

    }
    private String getFileExtension(File file) throws Exception {
        String name = file.getName();
        String ext = name.substring(name.lastIndexOf(".") + 1);
        String[] pos = {"gif","png","jpg"};
        for(int i = 0;i<pos.length;i++){
            if(ext.toLowerCase().compareTo(pos[i])==0){
                return ext;
            }
        }
        throw new Exception("Invalid extension type. Accepted types are: PNG, JPG, GIF.");
    }
    public EvalRules getEvalRuleNumber(){
        if(state==VisaoState.Absurdum){
            if(addDoubleNegationAbs.isSelected()){
                return EvalRules.DoubleNegationAdd;
            } else if(removeDoubleNegationAbs.isSelected()){
                return EvalRules.DoubleNegationRemove;
            } else if(iterationAbs.isSelected()){
                return EvalRules.Iteration;
            } else if(deiterationAbs.isSelected()){
                return EvalRules.Deiteration;
            }
        }else if(state==VisaoState.Peirce) {
            if(addDoubleNegationP.isSelected()){
                return EvalRules.DoubleNegationAdd;
            } else if(removeDoubleNegationP.isSelected()){
                return EvalRules.DoubleNegationRemove;
            } else if(iterationP.isSelected()){
                return EvalRules.Iteration;
            } else if(deiterationP.isSelected()){
                return EvalRules.Deiteration;
            } else if(evenCutP.isSelected()){
                return EvalRules.EvenRemove;
            } else if(oddCutP.isSelected()){
                return EvalRules.OddAdd;
            }
        }
        return EvalRules.None;
    }
    public void applyDeductionRule(){
        EvalRules rule = getEvalRuleNumber();
        if(state==VisaoState.Absurdum &&(rule==EvalRules.EvenRemove||rule==EvalRules.OddAdd)){
            JFrame frame = new JFrame("Invalid rule");
            JOptionPane.showMessageDialog(frame, "The system state was set to Ad Absurdum mode.\n");
        }else if(ntTemp!=null)
            applyDeductionRule(((PeircePanel)paintArea).getSelected());
    }
    public void applyDeductionRule(NandTree subtree){
        ntTemp.updateCutLevel();
        NandTree auxTree = null;
        if(subtree ==null){
            subtree= ntTemp;
        }
        if(getEvalRuleNumber()==EvalRules.OddAdd){
            try {
                auxTree = buildTree(auxVariableEval.getText());
            }catch(RuntimeException ex){
                JFrame frame = new JFrame("Syntax Error");
                JOptionPane.showMessageDialog(frame, ex.getMessage());
                return;
            }
        }
        if(getEvalRuleNumber()==EvalRules.Iteration ||getEvalRuleNumber()==EvalRules.Deiteration){
            auxTree =  ((PeircePanel)paintArea).getSecondSelect();
        }
        if(subtree != null){
            try {
                System.out.println("Subtree:\n"+subtree);
                subtree.applyRule(getEvalRuleNumber(),auxTree);
                ntTemp= NandTree.cleanNull(ntTemp);
                System.out.println("Tree:\n"+getNandTree());
                paintArea.revalidate();
                paintArea.repaint();
                saveState(getEvalRuleNumber());
                ((PeircePanel)paintArea).clear();
                firstItemSelectionButton.setSelected(true);
            } catch (Exception e) {
                JFrame frame = new JFrame("Error");
                JOptionPane.showMessageDialog(frame, e.getMessage());
            }
        }

    }
    public void savePanel() {
        JFrame tempframe = new JFrame();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "gif"));
        int response = (int) fileChooser.showSaveDialog(tempframe);
        if (response == JFileChooser.APPROVE_OPTION) {
            System.out.println("Saving");
            File targetFile = fileChooser.getSelectedFile();

            if(ntTemp!=null) {
                BufferedImage bi = new BufferedImage(paintArea.getWidth(), paintArea.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = bi.createGraphics();
                if(mainPane.getSelectedIndex()==0)
                    ntTemp.paintPeirce(g2, paintArea.getWidth(), paintArea.getHeight(), null, null);
                else if(mainPane.getSelectedIndex()==1)
                    ntTemp.paint(g2, paintArea.getWidth(), paintArea.getHeight());
                try {
                    String ext = getFileExtension(targetFile);
                    ImageIO.write((RenderedImage) bi, ext, targetFile);
                } catch (IOException e) {
                    JFrame frame = new JFrame("Print error!");
                    JOptionPane.showMessageDialog(frame, e.getMessage());
                } catch (Exception e) {
                    JFrame frame = new JFrame("Print error!");
                    JOptionPane.showMessageDialog(frame, e.getMessage());
                }
            }
            else{
                JFrame frame = new JFrame("Print error!");
                JOptionPane.showMessageDialog(frame, "Press evaluate to generate the figure.");
            }

        }
    }
    public boolean selectingFirstElement(){
        return firstItemSelectionButton.isSelected();
    }
    public void showLog(){
        JFrame jf = new JFrame("Log");
        log.show(jf);
        jf.setSize(paintArea.getWidth(),paintArea.getHeight());
        jf.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        jf.setVisible(true);
        jf.validate();
        jf.repaint();
    }
    public void nextState(){

        if(saves.size()>stateIndex+1){
            stateIndex++;
            ntTemp = (NandTree) saves.get(stateIndex).clone();
            paintArea.repaint();
            log.update(stateIndex);
        }
    }
    public void previousState(){
        System.out.println(" "+saves.size()+" "+stateIndex);
        if(stateIndex>0){
            stateIndex--;
            ntTemp = (NandTree) saves.get(stateIndex).clone();
            paintArea.repaint();
            log.update(stateIndex);
        }
    }
    public NandTree getNandTree(){
        return ntTemp;
    }

    public static void main(String[] args){
        JFrame frame = new JFrame("Logic");
        frame.setContentPane(new Visao().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
