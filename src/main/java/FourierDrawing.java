import java.awt.BasicStroke;
import java.awt.Button;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTextField;

import org.apache.commons.math3.complex.Complex;

public class FourierDrawing extends JFrame implements WindowListener, MouseListener {
	private static final long serialVersionUID = -4655249361815548007L;

	
	/**
	 * 
	 *     f
	 * Cn= | e^(-2 pi i n t) f(t) dt
	 *     j
	 */
	
	static int width=600;
	static int height=600;
	static int SIZE=11;
	static double DT=SIZE*0.00001;
	static double MAX=SIZE/4;		
	static int SLEEP=SIZE/10;
	
	static Complex[] Cn= new Complex[SIZE];
	static final Complex E = new Complex(Math.E);
	static ArrayList<long[]> previous;
	static ArrayList<long[]> previaux;
	
	boolean drawn=false;
	static BasicStroke B1=new BasicStroke();
	static BasicStroke B3=new BasicStroke(5);
	
    void init() {
		previous= new ArrayList<long[]>();
		previaux= new ArrayList<long[]>();
		Cn [0]= new Complex(Math.random(),Math.random()-0.5);
		for (int i=1;i<Cn.length;i++) {
			Cn[i]=new Complex(1/(2*i),Math.random()-0.5);
		//	Cn[i]=new Complex(1);
		}
	}

	public static void main(String[] args) {

		System.out.println("NEW");
		FourierDrawing fd=new FourierDrawing();
		System.out.println("BOUNDS");
		fd.setBounds(300, 300, width, height);
		fd.init();
		System.out.println("VISIBLE");
		fd.setVisible(true);
		System.out.println("LISTENER");
		fd.addWindowListener(fd);
		fd.addMouseListener(fd);
		
	}
	
	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		if (drawn) {
			return;
		}
		//System.out.println("START PAINTING...");
		Graphics2D g2= (Graphics2D) g;
	//	g2.setBackground(Color.WHITE);
	//	g2.setColor(Color.WHITE);
		g2.clearRect(0, 0, WIDTH-1, HEIGHT-1);
		
		double t=0;
		while (t<=1+DT){
		
			Complex ct=compute(t, g2);
			
			mainPlot (g2, ct);

		//	System.out.println(t+"\t"+ct);
			t=t+DT;
		
			try {
				Thread.sleep(SLEEP);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		drawn=true;

	}
	
	static Complex compute(double t, Graphics2D g2) {
		Complex c=new Complex(0);
		Complex prev=new Complex(0);
		if(previaux.isEmpty()) {
			previaux.add(0,new long[]{300,300});
		}
		for (int i=0;i<Cn.length;i++) {
			int n = 1+i-Cn.length/2;
			Complex exp=new Complex(0,-2*Math.PI*n*t);
			Complex aux=Cn[i].multiply(E.pow(exp));
			c=c.add(aux);
			auxPlot(g2,prev,c, i);
			prev=c;
		}
		return c;
	}

	static void auxPlot (Graphics2D g2, Complex c1, Complex c2, int i) {
		
		long x,xx=0;
		long y,yy=0;
		

		double half=width/2;
				
		x=Math.round(half + c1.getReal()*half/MAX);
		y=Math.round(half + c1.getImaginary()*half/MAX);

		xx=Math.round(half + c2.getReal()*half/MAX);
		yy=Math.round(half + c2.getImaginary()*half/MAX);
		
		if(previaux.size()>i+1) {
		//	System.out.println(toString(previaux));
			g2.setColor(g2.getBackground());
			g2.drawLine((int)previaux.get(i+1)[0],(int)previaux.get(i+1)[1],(int)previaux.get(i)[0],(int)previaux.get(i)[1]);
			previaux.remove(i+1);

		}
		
		g2.setStroke(B1);
		g2.setColor(new Color((i+1)*255*255*255/Cn.length));
		g2.drawLine((int)x,(int)y,(int)xx,(int)yy);
		
		previaux.add(i+1,new long[] {xx,yy});		
	}

	static void mainPlot (Graphics2D g2, Complex c) {
		
		long x=0;
		long y=0;
		
		double half=width/2;
				
		x=Math.round(half + c.getReal()*half/MAX);
		y=Math.round(half + c.getImaginary()*half/MAX);

		g2.setStroke(B3);

		if(previous.size()>2) {
			g2.setColor(Color.BLACK);
			for (int i=1;i<previous.size();i++) {
				g2.drawLine((int)previous.get(i-1)[0],
						(int)previous.get(i-1)[1],
						(int)previous.get(i)[0],
						(int)previous.get(i)[1]);
			} //remarca todo lo anterior
			g2.setColor(Color.RED);
			g2.drawLine((int)x,(int)y,(int)previous.get(previous.size()-1)[0],(int)previous.get(previous.size()-1)[1]);
	
		}
		
		previous.add(new long[] {x,y});
		
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

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		//System.out.println(e);
		if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) == MouseEvent.BUTTON1_MASK) {
			drawn=false;
			init();

			repaint();
		}
		
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	private static String toString (ArrayList<long[]> a){
		String s="[";
		for (int i=0;i< a.size();i++) {
			s+="("+a.get(i)[0]+","+a.get(i)[1]+") ";
		}
		return s+"]";
	}

	
}
