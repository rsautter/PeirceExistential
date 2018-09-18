import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseListner extends MouseAdapter{
    Visao v;
    TriggerType ButtonType;
    public MouseListner(Visao v,TriggerType ButtonType){
        this.v = v;
        this.ButtonType = ButtonType;
    }
    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        super.mouseClicked(mouseEvent);
        switch(ButtonType){
            case RandomButton:
                v.generateRandomInput();
                break;
            case EvalAbsButton:
                v.evaluateAbs();
                break;
            case EvalPButton:
                v.evaluateP();
                break;
            case SaveButton:
                v.savePanel();
                break;
            case NextButton:
                v.nextState();
                break;
            case PreviousButton:
                v.previousState();
                break;
            case ViewLogButton:
                v.showLog();
                break;
            case AddPremiseButton:
                v.addPremise();
                break;
            case RemovePremiseButton:
                v.removePremise();
                break;
            case ApplyRule:
                v.applyDeductionRule();
                break;
            case ClearSelection:
                v.clearSelection();
                break;
        }
    }
}

