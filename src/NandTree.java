
import org.antlr.v4.runtime.tree.Tree;
import org.antlr.v4.runtime.tree.Trees;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;



public class NandTree {
    private Tree tree;
    private String nodeValue;
    private NandTree dir,esq;
    private boolean changed = false;
    private int cutLevel=0;
    private NandTree cutLevelRoot;
    private Shape sh;


    public NandTree(String nodeValue){
        this.nodeValue = nodeValue;
        this.dir = null;
        this.esq = null;
        this.sh = null;
        cutLevelRoot=null;
    }
    public NandTree(Tree pt,String[] dict){
        tree = pt;
        dir = null;
        esq = null;
        this.sh = null;
        cutLevelRoot=null;
        boolean parenthesisFound = true;
        while(parenthesisFound == true){
            parenthesisFound=false;
            switch(tree.getChildCount()) {
                case 1: // Leaf node (or variable)
                    nodeValue = Trees.getNodeText(tree.getChild(0), Arrays.asList(dict));
                    break;
                case 2: // 'Not' case
                    nodeValue = Trees.getNodeText(tree.getChild(0), Arrays.asList(dict));
                    dir = new NandTree(tree.getChild(1), dict);
                    break;
                case 3:// 'Or','And', '->', '()' case
                    if (Trees.getNodeText(tree.getChild(0), Arrays.asList(dict)).compareTo("(")==0){//'()' case
                        tree = tree.getChild(1);
                        parenthesisFound = true;
                    }
                    else{//'Or','And', '->' case
                        nodeValue = Trees.getNodeText(tree.getChild(1), Arrays.asList(dict));
                        esq = new NandTree(tree.getChild(0), dict);
                        dir = new NandTree(tree.getChild(2), dict);
                    }
                    break;
            }
        }
    }
    public String toString(String out, int i){
        int j;
        String newOut=out;
        if(dir!=null)
            newOut = dir.toString(out,i+1);
        for(j=0;j<i;j++){
            newOut += "\t";
        }
        newOut += nodeValue+"("+cutLevel+")\n";
        if(esq!=null)
            newOut = esq.toString(newOut,i+1);
        return newOut;
    }
    public String toString(){
        return toString("",0);
    }
    public boolean isEqual(NandTree nt){
        return System.identityHashCode(nt)==System.identityHashCode(this);
    }
    public boolean isSemanticValueEqual(NandTree nt){
        if(nt==null)
            return false;
        if(nodeValue.compareTo(nt.nodeValue)!=0)
            return false;
        if(nt.dir!=null & dir==null)
            return false;
        if(dir!=null & nt.dir==null)
            return false;
        if(nt.esq!=null & esq==null)
            return false;
        if(esq!=null & nt.esq==null)
            return false;
        boolean equal=true;
        if(dir!=null)
            equal = equal & dir.isSemanticValueEqual(nt.dir);
        if(esq!=null)
            equal = equal & esq.isSemanticValueEqual(nt.esq);
        return equal;
    }

    //gets and sets
    public String getNodeValue(){return nodeValue;}
    public void setRight(NandTree nt) { dir=nt;}
    public void setLeft(NandTree nt){esq = nt;}
    public NandTree getRight(){return dir;}
    public NandTree getLeft(){return esq;}
    public void setChanged(boolean changed){this.changed=changed;}

    public static int getLevels(NandTree nt){
        if(nt.getRight()==null & nt.getLeft()==null){
            return 1;
        }else{
            int maxdir = 0;
            int maxesq = 0;
            if(nt.getRight()!=null)
                maxdir = getLevels(nt.getRight());
            if(nt.getLeft()!=null)
                maxesq = getLevels(nt.getLeft());
            return Math.max(maxesq,maxdir)+1;
        }
    }
    public void updateCutLevel(){
        updateCutLevel(0,this);
    }
    protected void updateCutLevel(int cutLevel, NandTree root){
        cutLevelRoot = root;
        this.cutLevel=cutLevel;
        if(nodeValue.compareTo("not")==0) {
            if (dir != null)
                dir.updateCutLevel(cutLevel + 1,dir);
        }
        else{
            if (dir != null)
                dir.updateCutLevel(cutLevel,root);
            if (esq != null)
                esq.updateCutLevel(cutLevel,root);
        }
    }
    public static NandTree transformNandTree(NandTree nt) {
        boolean changed = true;
        NandTree temp = nt;
        while (changed) {
            temp = temp.rebuildTree();
            changed = temp.doesSomeneChanged();
        }
        return temp;
    }
    public void paint(Graphics jp, int w, int h){
        jp.setColor(Color.WHITE);
        jp.fillRect(0,0,w, h);
        jp.setColor(Color.BLACK);
        paint(jp,w/2,20,(w-10)/2,h-40,getLevels(this));
    }
    public void paint(Graphics g,int minx,int miny,int w,int h, int levels){
        int textSize = g.getFontMetrics().stringWidth(nodeValue);
        g.drawOval(minx-textSize/2,miny-15,2*textSize,20);
        g.drawString(nodeValue,minx,miny);
        if (esq != null) {
            if(dir!=null){
                g.drawLine(minx+textSize/2+5,miny+5,minx + w/2+g.getFontMetrics().stringWidth(dir.nodeValue)/2, miny + (h)/levels-15);
                dir.paint(g,minx + w/2, miny + (h)/levels,w/2,h,levels);
                g.drawLine(minx+textSize/2+5,miny+5,minx - w/2+g.getFontMetrics().stringWidth(dir.nodeValue)/2, miny + (h)/levels-15);
            }
            esq.paint(g,minx - w/2, miny + (h)/levels,w/2,h,levels);
        } else if (dir != null) {
            g.drawLine(minx+textSize/2+5,miny+5,minx+g.getFontMetrics().stringWidth(dir.nodeValue)/2,miny + (h)/levels-15);
            if(dir!=null)
                dir.paint(g,minx, miny + (h)/levels,w/2,h,levels);
        }
    }
    public void paintPeirce(Graphics g,int w,int h,NandTree selected, NandTree second){
        g.setColor(Color.WHITE);
        g.fillRect(0,0,w, h);
        g.setColor(Color.BLACK);
        paintPeirce((Graphics2D) g,0,0,w,h,selected,second);
    }
    protected void paintPeirce(Graphics2D jp,int minx,int miny,int w,int h,NandTree selected, NandTree second){
        if(selected!=null){
            if(isEqual(selected)){
                jp.setColor(Color.RED);
                jp.setStroke(new BasicStroke(2.0f));
            }
        }
        if(second!=null){
            if(isEqual(second)){
                jp.setColor(Color.BLUE);
                jp.setStroke(new BasicStroke(2.0f));
            }
        }
        Color tempColor = jp.getColor();
        if(nodeValue.compareTo("not") == 0) {
            this.sh = new Ellipse2D.Double(minx,miny,w,h);
            if(cutLevel%2==0){

                jp.setColor(Color.LIGHT_GRAY);
                jp.fill(this.sh);
                jp.setColor(tempColor);
                jp.draw(this.sh);
            }else {
                jp.setColor(Color.WHITE);
                jp.fill(this.sh);
                jp.setColor(tempColor);
                jp.draw(this.sh);
            }
            if(dir!=null)
                dir.paintPeirce(jp, minx+w/8, miny+h/8, 6*w/8 , 6*h/8,selected,second);
        }
        else if(nodeValue.compareTo("and") == 0){
            this.sh = new Ellipse2D.Double(minx+3*w/8,miny+3*h/8,w/4,h/4);
            if(dir!=null)
                dir.paintPeirce(jp, minx + w / 2, miny + h / 4, w / 2, h / 2,selected,second);
            if(esq!=null)
                esq.paintPeirce(jp, minx, miny + h / 4, w / 2, h / 2,selected,second);
        }
        else{
            int textsize = jp.getFontMetrics().stringWidth(nodeValue);
            this.sh = new Ellipse2D.Double(minx+w/2-textsize,miny+h/4-textsize,3*textsize+10,3*textsize+10);
            jp.drawString(nodeValue,minx+w/2,miny+h/4);
        }
        if(second!=null){
            if(isEqual(second)){
                jp.setStroke(new BasicStroke(1.0f));
                jp.setColor(Color.BLACK);
            }
        }
        if(selected!=null){
            if(isEqual(selected)){
                jp.setStroke(new BasicStroke(1.0f));
                jp.setColor(Color.BLACK);
            }
        }
    }
    protected boolean doesSomeneChanged(){
        boolean doeschanged = changed;
        if(dir!=null){
            doeschanged = doeschanged | dir.doesSomeneChanged();
        }
        if(esq!=null){
            doeschanged = doeschanged | esq.doesSomeneChanged();
        }
        return doeschanged;
    }
    //a step to transform the tree in a NandTree
    protected NandTree rebuildTree(){
        if(dir!=null){
            dir = dir.rebuildTree();
        }
        if(esq!=null){
            esq = esq.rebuildTree();
        }
        if(nodeValue.compareTo("not") == 0){// ~(~a) = a
            if(dir.getNodeValue().compareTo("not") == 0){//dupla negacao
                System.out.println("Rule 1");
                dir.getRight().setChanged(true);
                return dir.getRight();
            }
        }
        else if(nodeValue.compareTo("or") == 0){ // a V b = ~( ~a ^ ~b )
            System.out.println("Rule 2");
            NandTree rebuiltNot = new NandTree("not");
            NandTree rebuiltNotLeft = new NandTree("not");
            NandTree rebuiltNotRight = new NandTree("not");
            NandTree rebuiltAnd = new NandTree("and");

            rebuiltNot.setRight(rebuiltAnd);
            rebuiltAnd.setLeft(rebuiltNotLeft);
            rebuiltAnd.setRight(rebuiltNotRight);

            rebuiltNotLeft.setRight(this.getLeft());
            rebuiltNotRight.setRight(this.getRight());
            rebuiltNot.setChanged(true);
            return rebuiltNot;
        }
        else if(nodeValue.compareTo("->") == 0){
            System.out.println("Rule 3");
            NandTree rebuiltOr = new NandTree("or");
            NandTree rebuildNot = new NandTree("not");
            rebuiltOr.setLeft(rebuildNot);
            rebuildNot.setRight(getLeft());
            rebuiltOr.setRight(getRight());
            rebuiltOr.setChanged(true);
            return rebuiltOr;
        }
        changed=false;
        return this;
    }
    @Override
    public Object clone(){
        NandTree output = new NandTree(this.nodeValue);
        if(dir!=null)
            output.dir = (NandTree) dir.clone();
        if(esq!=null)
            output.esq = (NandTree) esq.clone();
        return (Object)output;
    }
    public void applyRule(EvalRules rule, NandTree aux) throws Exception {
        NandTree copy;
        switch(rule){
            case DoubleNegationAdd:
                copy =  (NandTree) clone();
                NandTree doubleNot = new NandTree("not");
                dir = doubleNot;
                esq = null;
                nodeValue = "not";
                doubleNot.dir = copy;
                break;
            case DoubleNegationRemove:
                if(nodeValue.compareTo("not")!=0){
                    throw new Exception("Double negation removal - missing negation");
                }
                if(dir == null){
                    throw new Exception("Double negation removal - missing second negation");
                }
                if(dir.nodeValue.compareTo("not")!=0){
                    throw new Exception("Double negation removal - missing second negation");
                }
                if(dir.dir!=null) {
                    nodeValue = dir.dir.getNodeValue();
                    esq = dir.dir.esq;
                    dir = dir.dir.dir;
                }else{
                    nodeValue = "";
                    esq = null;
                    dir = null;
                }
                break;
            case EvenRemove:
                if(this.cutLevel%2==0 && nodeValue.compareTo("and")!= 0 & nodeValue.compareTo("not")!=0){
                    nodeValue = "";
                }else{
                    throw new Exception("Even cut removal rule - not even");
                }
                break;
            case OddAdd:
                if(this.cutLevel%2!=0 && aux!=null){
                    copy = (NandTree) clone();
                    nodeValue = "and";
                    dir = copy;
                    esq = aux;
                }else{
                    throw new Exception("Odd cut addition rule - not odd");
                }
                break;
            case Iteration:
                if(aux.cutLevelRoot.searchConjuntionSubtree(this)==null)
                    throw new Exception("Iteration rule - first item (red) is not a subitem of the second item (blue)");
                if(nodeValue.compareTo("not") == 0){
                    if(dir!=null) {
                        NandTree and = new NandTree("and");
                        and.dir = dir;
                        and.esq = (NandTree) aux.clone();
                        dir = and;
                    } else
                        dir = (NandTree) aux.clone();

                } else {
                    NandTree temp = (NandTree) clone();
                    nodeValue = "and";
                    esq = temp;
                    dir = (NandTree) aux.clone();
                }
                break;
            case Deiteration:
                if(!aux.isSemanticValueEqual(this))
                    throw new Exception("Deiteration rule - Items are different");
                else if(aux.cutLevelRoot.searchConjuntionSubtree(this)==null)
                    throw new Exception("Deiteration rule - first item (red) were not produced by the second item (blue)");
                else
                    nodeValue = "";
                break;
        }
    }
    private NandTree searchConjuntionSubtree(NandTree search){
        NandTree out= null;
        if(isEqual(search))
            out = this;
        else if(nodeValue.compareTo("and")==0 || nodeValue.compareTo("not")==0 ){
            if(esq!=null)
                out = esq.searchConjuntionSubtree(search);
            if(dir!=null && out == null)
                out = dir.searchConjuntionSubtree(search);
        }
        return out;
    }
    public static boolean sameLevel( NandTree nt1, NandTree nt2){
        return (nt1.cutLevel==nt2.cutLevel && nt1.cutLevelRoot.isEqual(nt2.cutLevelRoot));
    }
    public static NandTree cleanNull(NandTree nt){
        NandTree output=nt;
        AtomicBoolean hasChanged;
        do{
            hasChanged = new AtomicBoolean(false);
            if(output!=null)
                output = output.cleanNull(hasChanged);
            else
                break;
        }while(hasChanged.get());
        return output;
    }
    private NandTree cleanNull(AtomicBoolean hasChanged){
        if(getNodeValue().compareTo("")==0){
            hasChanged.set(true);
            return null;
        }
        if(getNodeValue().compareTo("and")==0){
            if(esq==null) {
                if (dir != null)
                    return dir.cleanNull(hasChanged);
                return null;
            }
            if(dir==null){
                if(esq!=null)
                    return esq.cleanNull(hasChanged);
                return null;
            }

        }
        if(dir!=null){
            dir = dir.cleanNull(hasChanged);
        }
        if(esq!=null){
            esq = esq.cleanNull(hasChanged);
        }
        return this;
    }
    public NandTree serachPoint(Point2D p){
        NandTree temp = null;
        if(dir!=null){
            temp = dir.serachPoint(p);
        }
        if(esq!=null  & temp==null){
            temp = esq.serachPoint(p);
        }
        if(sh!=null & temp==null){
            if(sh.contains(p)){
                temp=this;
            }
        }
        return temp;
    }

    public String inlineString() {
        String out;
        if(nodeValue.compareTo("and") == 0){
            out = "(";
            if(esq!=null)
                out += esq.inlineString();
            out+=" and ";
            if(dir!=null)
                out+=dir.inlineString();
            out+=")";
        }
        else if(nodeValue.compareTo("not") == 0){
            out = "not( ";
            if(dir!=null)
                out += dir.inlineString();
            out+= ")";
        }
        else{
            out = nodeValue;
        }
        return out;
    }
}
