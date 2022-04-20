package ass02.task.view;


import ass02.task.model.Body;
import ass02.task.model.Boundary;
import ass02.task.model.Flag;

import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SimulationFrame extends JFrame implements ActionListener {
    private SimulationPanel panel;
    private Flag stopFlag;
    private JButton continueButton;
    private JButton pauseButton;

    public SimulationFrame(final int width, final int height, Flag stopFlag){
        this.stopFlag = stopFlag;
        setTitle("Bodies Simulation");
        setSize(width,height);
        setResizable(false);
        panel = new SimulationPanel(width,height);
        getContentPane().add(BorderLayout.CENTER, panel);

        this.continueButton = new JButton("continue");
        this.pauseButton = new JButton("pause");
        JPanel controlPanel = new JPanel();
        controlPanel.add(continueButton);
        controlPanel.add(pauseButton);
        continueButton.addActionListener(this);
        pauseButton.addActionListener(this);

        getContentPane().add(BorderLayout.SOUTH, controlPanel);

        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent ev){
                System.exit(-1);
            }
            public void windowClosed(WindowEvent ev){
                System.exit(-1);
            }
        });
        this.setVisible(true);
    }

    public void display(List<Body> bodies, double vt, long iter, Boundary bounds){
        try {
            SwingUtilities.invokeLater(() -> {  //todo è ok oppure andava lasciato invokeAndWait??
                panel.display(bodies, vt, iter, bounds);
                repaint();
            });
        } catch (Exception ex) {}
    };

    public void actionPerformed(ActionEvent ev){
        String cmd = ev.getActionCommand();
        if (cmd.equals("continue")){
            this.stopFlag.set(false);
            this.continueButton.setEnabled(false);
            this.pauseButton.setEnabled(true);
        } else if (cmd.equals("pause")){
            this.stopFlag.set(true);
            this.continueButton.setEnabled(true);
            this.pauseButton.setEnabled(false);
        }
    }

    public void updateScale(double k) {
        panel.updateScale(k);
    }
}

