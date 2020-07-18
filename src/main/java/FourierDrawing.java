import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;

public class FourierDrawing extends JFrame implements WindowListener {
	private static final long serialVersionUID = -4655249361815548007L;
	static int width=600;
	static int height=600;
	
	/**
	 * 
	 *     f
	 * Cn= | e^(-2pi i n t) f(t) dt
	 *     j
	 */
	
	static Complex[] Cn= new Complex[10];
	static final Complex E = new Complex(Math.E);
	static long[] previous,preprevious;

	static void init() {
		for (int i=0;i<Cn.length;i++) {
			Cn[i]=new Complex(Math.random(),Math.random());
		}
	}

	public static void main(String[] args) {

		FourierDrawing fd=new FourierDrawing();
		fd.setBounds(300, 300, width, height);
		fd.addWindowListener(fd);
		init();
		fd.setVisible(true);
		
	}
	
	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		Graphics2D g2= (Graphics2D) g;
		
		double t=0.0;
		double dt=0.005;

		Complex exp;
		while (t<=1){
		
			exp = new Complex(0,t);

			Complex ct=compute(exp);
			
			plot (g2, ct);

			System.out.println(t+"\t"+ct);
			t=t+dt;
		
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	static Complex compute(Complex exp) {
		Complex c=new Complex(0);
		for (int i=0;i<Cn.length;i++) {
			c=c.add(Cn[i].multiply(E.pow(exp.multiply(i-Cn.length/2).multiply(-2*Math.PI))));
		}
		return c;
	}

	void plot (Graphics2D g2, Complex c) {
		
		long x=0;
		long y=0;
		
		double max=10;
		double half=width/2;
				
		x=Math.round(half + c.getReal()*half/max);
		y=Math.round(half + c.getImaginary()*half/max);

		if(previous == null) {
			//g2.drawLine((int)x,(int)y,(int)x,(int)y);
		} else if(preprevious==null) {
			//g2.setColor(Color.RED);
			//g2.drawLine((int)x,(int)y,(int)previous[0],(int)previous[1]);
		} else {
			//g2.setStroke(new BasicStroke(4));
			//g2.setColor(g2.getBackground());
			//g2.drawLine((int)previous[0],(int)previous[1],(int)preprevious[0],(int)preprevious[1]);
			//g2.setStroke(new BasicStroke(4));
			g2.setColor(Color.BLACK);
			g2.drawLine((int)previous[0],(int)previous[1],(int)preprevious[0],(int)preprevious[1]);

			g2.setColor(Color.RED);
			g2.setStroke(new BasicStroke(3));
			g2.drawLine((int)x,(int)y,(int)previous[0],(int)previous[1]);
	
		}
		preprevious = previous;
		previous = new long[] {x,y};
		
	}
	
	
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		System.out.println("opened");
	}

	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		System.out.println("closing");
		System.exit(0);
		
	}

	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		System.out.println("closed");

	}

	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		System.out.println("iconified");

	}

	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	

	
}
