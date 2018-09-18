import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Log extends ArrayList<LogEntry> {
    private JTable jt;
    private DefaultTableModel model;
    public Log(){
        super();
        model = new DefaultTableModel();
        jt = new JTable(model);
        model.addColumn("Operation");
        model.addColumn("System State");
    }
    public void show(JFrame jf)
    {
        JScrollPane sp=new JScrollPane(jt);
        JButton jb = new JButton("Save Log");
        jb.addMouseListener(
                new MouseAdapter(){
                    @Override
                    public void mouseClicked(MouseEvent mouseEvent){
                        super.mouseClicked(mouseEvent);
                        JFrame tempframe = new JFrame();
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setFileFilter(new FileNameExtensionFilter("Comma Separate Value", "csv", "CSV"));
                        int response = (int) fileChooser.showSaveDialog(tempframe);
                        if (response == JFileChooser.APPROVE_OPTION) {
                            System.out.println("Saving");
                            try {
                                FileWriter targetFile = new FileWriter(fileChooser.getSelectedFile());
                                targetFile.write("Operation,System State");
                                for(int i=0;i<size();i++)
                                    targetFile.write("\n"+get(i).getOperation()+","+get(i).getState());
                                targetFile.flush();
                                targetFile.close();
                                System.out.println("Saved");
                            } catch (IOException e) { e.printStackTrace(); }
                        }
                    }
                }
        );

        jf.setLayout(new BorderLayout());
        jf.add(sp);
        jf.add(jb,BorderLayout.SOUTH);
    }
    public void update(int position){
        //Empty:
        while(model.getRowCount()>0) {
            model.removeRow(0);
        }

        //re-adding
        for(int i=0;i<super.size();i++){
            Object[] row ={get(i).getOperation(),get(i).getState()};
            model.addRow(row);
        }
        jt.setRowSelectionInterval(position,position);
    }
}
