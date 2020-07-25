import java.awt.BasicStroke;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;

import org.apache.commons.math3.complex.Complex;

public class FourierDrawing3 extends JFrame implements WindowListener, WindowStateListener, MouseListener, MouseMotionListener, MouseWheelListener {
	private static final long serialVersionUID = -4655249361815548007L;

	static int width=600;
	static int height=600;
	static int half=width/2;
	static BasicStroke B1=new BasicStroke();
	static BasicStroke B3=new BasicStroke(3);
	static boolean drawn = false;
	static boolean clear=true;
	static boolean fourier=false;
	Point anterior, previous=null;
	BufferedImage keep=null;

	
	static int SIZE=101;
	static double dt=0.01;
	static int initial=-(SIZE-1)/2;
	static int ending=(SIZE-1)/2;
				
	static int SLEEP=50;
	
	static ArrayList<Point> myDraw = new ArrayList<Point>();	
	static ArrayList<Point> fourierDraw = new ArrayList<Point>();	
	
	static ArrayList<Complex> Cn = new ArrayList<Complex>();	
	static ArrayList<Complex> Ft = new ArrayList<Complex>();	
	
	static Complex E=new Complex (Math.E,0);	

    void init() {
		this.setBounds(300, 300, width, height);
		keep=new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		myDraw=new ArrayList<FourierDrawing3.Point>();

	}

	public static void main(String[] args) {

		FourierDrawing3 fd=new FourierDrawing3();
		
		fd.init();

		fd.addWindowListener(fd);
		fd.addWindowStateListener(fd);
		fd.addMouseListener(fd);
		fd.addMouseMotionListener(fd);
		fd.addMouseWheelListener(fd);
		
		fd.setVisible(true);

		
	}
	
	@Override
	public void paint(Graphics g) {

		Graphics2D g2 = (Graphics2D) keep.getGraphics();
		
		if (!drawn) {
			//System.out.println("Vamos a pintar de nuevo...");
			if (clear) {
				clearAll(g);
				
			}

			if (!fourier) {
				g2.setStroke(B3);
				g2.setColor(Color.YELLOW);
				if (clear) {
					System.out.println("Vamos a pintar todo el raton...");
					for (Point p: myDraw) {
						//pinto toda la traza del raton
						if (anterior!=null) {
							drawLine(g2,Color.YELLOW,anterior.getX(),anterior.getY(),p.getX(), p.getY());
						} else {
							drawLine(g2,Color.YELLOW,p.getX(),p.getY(),p.getX(), p.getY());
						}
						anterior=p;
					}
				} else if (myDraw.size()>0) {
					//System.out.println("Vamos a pintar solo el ultimo...");
	
					//pinto solo el ultimo punto añadido a la traza
					Point p=myDraw.get(myDraw.size()-1);
					if (anterior!=null) {
						drawLine(g2,Color.YELLOW,anterior.getX(),anterior.getY(),p.getX(), p.getY());
					} else {
						drawLine(g2,Color.YELLOW,p.getX(),p.getY(),p.getX(), p.getY());
					}
					anterior=p;
				}
			} else {
				//calcalamos cada F(t) y vamos pintando...
				System.err.println("Vamos a pintar el fourier en rojo...");

				double t=0;
				Complex ft;
				Point p;

		
				while (t<=1) {
					
					clearAll(g);
					g.drawImage(keep, 0, 0, width, height, null);
					ft=computeFt(t,g);
					
					p = new Point((int)Math.round(ft.getReal()),(int)Math.round(ft.getImaginary()));
					//System.out.println(p);
					g2.setStroke(B3);
					g2.setColor(Color.RED);
					if (previous!=null) {
						drawLine(g2,Color.RED,p.getX(),p.getY(), previous.getX(),previous.getY());
					} else {
						drawLine(g2,Color.RED,p.getX(),p.getY(), p.getX(), p.getY());
					}
					previous=p;
					t = t + dt;
					//slow down 
					try {Thread.sleep(SLEEP);} catch (Exception e) {}

				}
				fourier=false;
				previous=null;
			}
		} else {
			System.err.println("Ya estaba todo pintado...");
		}
		g.drawImage(keep, 0, 0, width, height, null);
		drawn =true;
		
	}

	private static void drawLine (Graphics g, Color c, int x1, int y1, int x2, int y2) {
		g.setColor(c);
		g.drawLine(x1+half, y1+half, x2+half, y2+half);
	}

	private void clearAll(Graphics g) {
		System.out.println("Vamos a borrar todo...");
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, this.getWidth()-1, this.getHeight()-1);
		g.setColor(Color.DARK_GRAY);
		drawLine(g,Color.DARK_GRAY,0,half, 0,-half);
		drawLine(g,Color.DARK_GRAY,half,0,-half,0);

	}

	/**
	 * 
	 *     f
	 * Cn= | e^(-2 pi i n t) f(t) dt
	 *     j
	 */
	static Complex computeCn(int n) {
		
		double t=0;
		Complex c=new Complex (0,0);
		int a=0; 
		while (t<1 && myDraw.size()>0) {
			Complex exp=new Complex(0,-2.0 * Math.PI * n * t);
			//Calcular Ft a partir del dibujo 
			a = (int)Math.round(myDraw.size()*t);
			if (a>=myDraw.size()) {
				a--;
			}
			c=c.add(E.pow(exp).multiply(new Complex(myDraw.get(a).getX(),(myDraw.get(a).getY()))).multiply(dt));
			
			t = t + dt;
		}
		return c;
	}

	
	/**
	 * 
	 *          
	 * F(t)= SUMn( Cn * e^(2 pi i n t) )
	 *          
	 */
	static Complex computeFt (double t,Graphics g) {
		Complex f=new Complex(0);
		Complex arrow=null;
		Complex incremento=null;
		BufferedImage trace=new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D)trace.getGraphics();
		for (int n = initial;n<=ending;n++) {
			Complex exp = new Complex(0, 2.0 * Math.PI * n * t);
			//ajustar N para que sea 0+ y usarlo en el arraylist
			incremento=E.pow(exp).multiply(Cn.get(n+ending));
			f=f.add(incremento);
			
			if(arrow!=null) {
				//System.out.println(f);
				g2.setStroke(B1);
				
				//ciruclo
				int x= (int) Math.round(arrow.getReal());
				int y= (int) Math.round(arrow.getImaginary());
				drawCenteredCircle(g2,Color.LIGHT_GRAY, x, y, 2* (int)Math.round(incremento.abs()));
				//flecha
				drawArrow (g2,Color.LIGHT_GRAY,(int) Math.round(arrow.getReal()),(int) Math.round( arrow.getImaginary()), (int) Math.round(f.getReal()), (int) Math.round(f.getImaginary()));

				
			}
			arrow=f;
		}
		g.drawImage(trace, 0, 0, width, height, null);
		return f;
	}
	
	private static void drawArrow(Graphics2D g, Color c, int x1, int y1, int x2, int y2) {
		
		drawLine(g,c,x1, y1, x2, y2);
		drawCenteredCircle(g, c, x2, y2, 5);
	}

	static void drawCenteredCircle(Graphics g, Color c, int x, int y, int r) {
		x = x-(r/2);
		y = y-(r/2);
		drawOval(g,c,x,y,r,r);
	}
	
private static void drawOval(Graphics g, Color c, int x, int y, int r, int r2) {
		// TODO Auto-generated method stub
		g.setColor(c);
		g.drawOval(x+half, y+half, r, r2);
	}

/**
 * Window	
 */
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		System.exit(0);
		
	}

	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

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
	
	public void windowStateChanged(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


/**
 * Mouse
 */
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		//System.out.println(e);
	/*	
		if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) == MouseEvent.BUTTON1_MASK) {
			drawn=false;
			init();
			repaint();
		}
	*/	
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		//inicializamos la lista de puntos guardados y borramos el dibujo
		init();
		anterior=null;
		clear=true;
		drawn=false;
		repaint();
	}

	public void mouseReleased(MouseEvent e) {
		//calculamos los nuevo coeficientes y pintamos la serie de Fourier
		Cn=new ArrayList<Complex>();
		
		for (int n=initial;n<=ending;n++) {
			Cn.add(computeCn(n));
		}
		
/*		
 			Cn.sort(new Comparator<Complex>() {

			public int compare(Complex o1, Complex o2) {
				if (o1.abs()>o2.abs()) {
					return 1;
				} else if (o1.abs()<o2.abs()) {
					return -1;
				} else {
					return 0;
				}
			}
		});
*/		
		fourier=true;
		drawn=false;
		repaint();
		
	}

/**
 * Mouse wheel
 */

	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		
	}

/**
 *  Mouse move
 */
	public void mouseDragged(MouseEvent e) {
		
		Point p= new Point(e.getX(), e.getY());
		clear=false;
		//System.out.println(p);
		
		//desde el centro
		p.setX(p.getX()-half);
		p.setY(p.getY()-half);
		
		myDraw.add(p);
		drawn=false;
		repaint();
		
	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	private class Point {
		private int x;
		private int y;
		
		public Point(int x, int y) {
			this.setX(x);
			this.setY(y);
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "("+getX()+","+getY()+")";
		}
	}

	
}
