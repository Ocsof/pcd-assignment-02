package ass02.task.view;

import ass02.task.model.Body;
import ass02.task.model.Boundary;
import ass02.task.model.Point2D;

import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class SimulationPanel extends JPanel implements KeyListener {

    private List<Body> bodies;
    private Boundary bounds;

    private long nIter;
    private double vt;
    private double scale = 1;

    private long dx;
    private long dy;

    public SimulationPanel(final int w, final int h){
        setSize(w,h);
        this.dx = w/2 - 20;
        this.dy = h/2 - 20;
        this.addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        requestFocusInWindow();
    }

    public void paint(Graphics g){
        if (this.bodies != null) {
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            g2.clearRect(0,0,this.getWidth(),this.getHeight());


            int x0 = getXcoord(this.bounds.getX0());
            int y0 = getYcoord(this.bounds.getY0());

            int wd = getXcoord(bounds.getX1()) - x0;
            int ht = y0 - getYcoord(bounds.getY1());

            g2.drawRect(x0, y0 - ht, wd, ht);

            bodies.forEach( b -> {
                Point2D p = b.getPos();
                int radius = (int) (10*scale);
                if (radius < 1) {
                    radius = 1;
                }
                g2.drawOval(getXcoord(p.getX()),getYcoord(p.getY()), radius, radius);
            });
            String time = String.format("%.2f", vt);
            g2.drawString("Bodies: " + bodies.size() + " - vt: " + time + " - nIter: " + nIter + " (UP for zoom in, DOWN for zoom out)", 2, 20);
        }
    }

    private int getXcoord(double x) {
        return (int)(dx + x*dx*scale);
    }

    private int getYcoord(double y) {
        return (int)(dy - y*dy*scale);
    }

    public void display(List<Body> bodies, double vt, long iter, Boundary bounds){
        this.bodies = bodies;
        this.bounds = bounds;
        this.vt = vt;
        this.nIter = iter;
    }

    public void updateScale(double k) {
        scale *= k;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 38){  		/* KEY UP */
            scale *= 1.1;
        } else if (e.getKeyCode() == 40){  	/* KEY DOWN */
            scale *= 0.9;
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}
