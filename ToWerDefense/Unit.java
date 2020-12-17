import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.border.*;
import java.util.*;

class Unit extends Canvas implements  ActionListener , MouseListener
{
	String name;
	JLabel lifeJL, speedJL, 특징JL, 상태JL;
	String type;
	int life;
	int speed = 100;
	int x = 70;
	int y = 0;
	int size;
	Graphics2D g2;
	int wayCount = 0;
	int wayPoint[][] = {{65,215}, {155,215},{155,65},{365,65},
		{365,155},{245,155},{245,245},{435,245}};
	int wayPoint2[][] = {{60,225},{165,225},{165,75},{360,75},
		{360,150},{240,150},{240,255},{435,255}};
	int myWay[][];
	Land land[][];
	Land currentLand;
	GameManager gm;
	boolean 데미지;
	JPanel inforPan;
	double 라디안;
	Color damageColor;
	boolean 얼음,중독;
	int poisionDamage;
	boolean 이뮨;
				
	javax.swing.Timer timer;

	Unit(int way, Land lands[][], GameManager gm, String n , int l , int s , String t, int size)
	{
		life = l;
		this.speed = s;
		name = n;
		this.gm = gm;
		type = t;
		this.size = size;
		lifeJL = new JLabel("라이프 : " + String.valueOf(life));
		speedJL = new JLabel("스피드 : " + String.valueOf(speed));
		특징JL = new JLabel("특징 : 없음");
		상태JL = new JLabel("상태 : 양호");
		inforPan = new JPanel();
		setBounds(70, 0, size,size);
		if (way == 0)
			myWay = wayPoint;
		else
			myWay = wayPoint2;
		land = lands;
		timer = new javax.swing.Timer(speed, this);		

		//timer.start();
	}

	class IceThread extends Thread
	{
		public void run()
		{
			얼음 = true;
			if (중독)
				상태JL.setText("상태 : 얼음 + 중독");
			else
				상태JL.setText("상태 : 얼음");

			timer.setDelay(speed + speed / 3);
			speedJL.setText("스피드 : " + String.valueOf(speed + speed / 3));
			try{
				Thread.sleep(5000);
			}catch(Exception e){
				System.out.println(e);
			}
			timer.setDelay(speed);
			얼음 = false;
			speedJL.setText("스피드 : " + String.valueOf(speed));
			if (중독)
				상태JL.setText("상태 : 중독");
			else
				상태JL.setText("상태 : 양호");
		}
	};

	class PoisionThread extends Thread
	{
		public void run()
		{
			중독 = true;
			if (얼음)
				상태JL.setText("상태 : 얼음 + 중독");
			else
				상태JL.setText("상태 : 중독");

			for (int i = 0 ; i < 10 ; i++ )
			{
				damage(poisionDamage, Color.green , false);
				try{
					Thread.sleep(700);
				}catch(Exception e){
					System.out.println(e);
				}
			}
			timer.setDelay(speed);
			중독 = false;
			if (얼음)
				상태JL.setText("상태 : 얼음");
			else
				상태JL.setText("상태 : 양호");
		}
	};

	public void moveStart(){
		timer.start();
	}
	public void moveStop(){
		timer.stop();
	}
	
	public void showInformation()
	{
		//return new JPanel();		
	}

	public void paint(Graphics g)
	{}

	public void move()
	{}

	public void damage(int d , Color c , boolean 범위)
	{
		
		if (범위)
		{
			Vector<Unit> units = currentLand.getUnitVec();
			int i = currentLand.getY()/30;
			int j = currentLand.getX()/30;
			if (i-1 > -1)
			land[i-1][j].범위공격(d, c, false);
			if (j-1 > -1)
				land[i][j-1].범위공격(d, c, false);
			land[i][j].범위공격(d, c, false);
			if (j+1 < 15)
				land[i][j+1].범위공격(d, c, false);
			if (i+1 < 15 )
				land[i+1][j].범위공격(d, c, false);
		}

		damageColor = c;
		life = life - d;
		lifeJL.setText("라이프 : " + String.valueOf(life));
		데미지 = true;

		if (life < 1)
		{
			currentLand.remove(this);
			dead();
		}
		else 
		{
			if (이뮨)
			{}
			else
			{
				if (c == Color.white && 얼음 == false)				
					new IceThread().start();
				if (c == Color.green && 중독 == false){
					poisionDamage = d / 5;
					new PoisionThread().start();				
				}
			}
		}		
		repaint();				
	}

	public void actionPerformed(ActionEvent ae)
	{
		boolean xx = false;
		boolean yy = false;
				
		if (myWay[wayCount][0] > x)
			x += 5;
		else if (myWay[wayCount][0] < x)
			x -= 5;
		else
			xx = true;

		if (myWay[wayCount][1] > y)
			y += 5;
		else if (myWay[wayCount][1] < y)
			y -= 5;
		else
			yy = true;
		
		setBounds(x,y,size,size);
		turn();
		//repaint();
		
		int imsiX = x/30;
		int imsiY = y/30;
		if (currentLand != land[imsiY][imsiX] )
		{
			if (currentLand != null)
				currentLand.removeUnitVec(this);
			currentLand = land[imsiY][imsiX];
			currentLand.addUnitVec(this);
		}
		겨냥타워(currentLand.getTower());
		
		if ( xx && yy)
		{
			wayCount++;			

			if (wayCount == 8)
			{
				gm.exitUnit();
				timer.stop();
				currentLand.remove(this);
				dead();
			}
		}
	}
	public void turn(){}

	public String getType(){
		return type;
	}

	public void 겨냥타워(Vector <Tower>towerVec)
	{
		for (int i = 0 ; i < towerVec.size() ; i++ )
		{
			Tower t = towerVec.get(i);
			if (t.getAttactType().equals("지상+공중"))
				t.사정거리계산(this);
			else if (type.equals(t.getAttactType()))
				t.사정거리계산(this);			
		}
	}

	public void dead()
	{
		timer.stop();
		gm.deadUnit(this);		
		Vector <Tower> tv = currentLand.getTower();
		for (int i = 0 ; i < tv.size() ; i++ )
		{
			Tower t = tv.get(i);
			t.deadTarget(this);
		}
	}

	public void launchInforPan()
	{
		inforPan.removeAll();
		inforPan.setBorder(new TitledBorder(""));
		inforPan.setLayout(new BoxLayout(inforPan, BoxLayout.Y_AXIS));
		inforPan.setAlignmentX(inforPan.LEFT_ALIGNMENT );
		inforPan.add(new JLabel("이름 : " + name));
		inforPan.add(new JLabel("타입 : " + type));
		inforPan.add(lifeJL);
		inforPan.add(speedJL);
		inforPan.add(상태JL);
		inforPan.add(특징JL);
		inforPan.repaint();
	}

	public void mousePressed (MouseEvent  e)
	{
		launchInforPan();
		gm.showInfor(inforPan);
		GameManager.select = e.getComponent();
	}
    public void mouseClicked (MouseEvent  e) {}// 
    public void mouseEntered (MouseEvent  e){}// 
    public void mouseExited (MouseEvent  e){}
    public void mouseReleased (MouseEvent  e){}
	
}

class NormalUnit extends Unit
{
	Image img;
	NormalUnit(int way, Land land[][], GameManager gm , int level)
	{
		super(way, land, gm, "동그라미유닛" , 20 * level * level, 100 , "지상", 20);
		Toolkit toolit = Toolkit.getDefaultToolkit();
		img = toolit.getImage("./이미지/땅.gif");
		특징JL.setText("특징 : 없음");
	}
	public void paint(Graphics g)
	{		
		if(g2 == null)
        {
            g2 = (Graphics2D)getGraphics();
            g2.translate(10,10);
        }

		g.drawImage(img,0,0,20,20,this);
		if (GameManager.select == this)
		{
			g.setColor(Color.blue);
			g.fillRect(1,1,18,18);			
		}
				
		if (데미지)
		{
			g2.setColor(damageColor);
			g2.fillOval(-7, -7, 14,14);
			데미지 = false;
		}
		else
		{
			g2.setColor(Color.white);
			g2.fillOval(-7, -7, 14,14);
		}
		g2.setColor(Color.black);
		g2.fillOval(1,-4,4,4);
		g2.fillOval(1,1,4,4);
		if (얼음)
		{
			g.setColor(Color.white);
			g.drawOval(0,0,19,19);			
		}
		if (중독)
		{
			g.setColor(Color.green);
			g.drawOval(1,1,17,17);
		}
	}
	public void turn()
	{
		int wayX = myWay[wayCount][0];
	    int wayY = myWay[wayCount][1];
		g2.rotate(-1*라디안);
		repaint();
	    라디안 = Math.atan2(wayY - y, wayX - x);
		g2.rotate(라디안);
	    repaint();
	}
}

class ImageUnit extends Unit
{
	Image img,img1,img2,img3,image;
	int moveCount;
	Toolkit toolkit;
	ImageUnit(int way, Land land[][], GameManager gm, String name, int level, String type)
	{
		//Unit(int way, Land lands[][], GameManager gm, String n , int l , int s , String t, int size)
		super(way, land, gm, name , 20 * level * level, 100, type, 20);
		toolkit = Toolkit.getDefaultToolkit();
		img = toolkit.getImage("./이미지/땅.gif");		
	}
	public void paint(Graphics g)
	{		
		if(g2 == null)
        {
            g2 = (Graphics2D)getGraphics();
            g2.translate(10,10);            
        }

		g.drawImage(img,0,0,20,20,this);
		if (GameManager.select == this)
		{
			g.setColor(Color.blue);
			g.fillRect(1,1,18,18);			
		}
				
		if(moveCount%3 == 0)
			image = img1;			
		else if (moveCount%3 == 1)
			image = img2;		
		else if (moveCount%3 == 2)
			image = img3;
		else
			image = img2;
		moveCount++;

		g2.drawImage(image,-10,-10,20,20,this);
				
		if (데미지)
		{
			g.setColor(damageColor);
			g.fillOval(2, 2, 15,15);
			데미지 = false;
		}
		if (얼음)
		{
			g.setColor(Color.white);
			g.drawOval(0,0,19,19);			
		}
		if (중독)
		{
			g.setColor(Color.green);
			g.drawOval(1,1,17,17);
		}
	}
	public void turn()
	{
		int wayX = myWay[wayCount][0];
	    int wayY = myWay[wayCount][1];
		g2.rotate(-1*라디안);
		repaint();
	    라디안 = Math.atan2(wayY - y, wayX - x);
		g2.rotate(라디안);
	    repaint();	
	}
}

class FlyUnit extends ImageUnit
{
	FlyUnit(int way, Land land[][], GameManager gm, int level)
	{
		//ImageUnit(int way, Land land[][], GameManager gm, String name, int level, String type)
		super(way, land, gm, "용" , level, "공중");
		img1 = toolkit.getImage("./이미지/용1.gif");
		img2 = toolkit.getImage("./이미지/용2.gif");
		img3 = toolkit.getImage("./이미지/용3.gif");
		image = img2;
		특징JL.setText("특징 : 날라다님");
	}	
}

class BossUnit extends ImageUnit
{
	BossUnit(int way, Land land[][], GameManager gm, int level)
	{
		//ImageUnit(int way, Land land[][], GameManager gm, String name, int level, String type)
		super(way, land, gm, "로봇" ,level, "지상");
		img1 = toolkit.getImage("./이미지/로봇-걸음1.gif");
		img2 = toolkit.getImage("./이미지/로봇-걸음.gif");
		img3 = toolkit.getImage("./이미지/로봇-걸음2.gif");
		image = img2;
		life = 30 * level * 10;
		lifeJL.setText("라이프 : " + String.valueOf(life));
		특징JL.setText("특징 : 체력 10배");
	}	
}
class 오각Unit extends ImageUnit
{
	오각Unit(int way, Land land[][], GameManager gm, int level)
	{
		//ImageUnit(int way, Land land[][], GameManager gm, String name, int level, String type)
		super(way, land, gm, "오각벌레" ,level, "지상");
		img1 = toolkit.getImage("./이미지/오각벌레1.gif");
		img2 = toolkit.getImage("./이미지/오각벌레2.gif");
		img3 = toolkit.getImage("./이미지/오각벌레3.gif");
		image = img2;
		speed = 70;
		timer.setDelay(speed);
		speedJL.setText("스피드 : "+ String.valueOf(speed));
		특징JL.setText("특징 : 빨리달리기");
	}	
}

class 이뮨Unit extends ImageUnit
{
	이뮨Unit(int way, Land land[][], GameManager gm, int level)
	{
		//ImageUnit(int way, Land land[][], GameManager gm, String name, int level, String type)
		super(way, land, gm, "자동차" ,level, "지상");
		img1 = toolkit.getImage("./이미지/자동차1.gif");
		img2 = toolkit.getImage("./이미지/자동차2.gif");
		img3 = toolkit.getImage("./이미지/자동차3.gif");
		image = img2;
		이뮨 = true;
		특징JL.setText("특징 : 마법에 안걸림");
	}	
}

