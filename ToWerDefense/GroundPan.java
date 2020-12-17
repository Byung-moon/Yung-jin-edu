import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.border.*;
import java.util.*;

class GroundPan extends JPanel implements MouseListener, MouseMotionListener
{
    int mouseX;
    int mouseY;
    Land land[][] = new Land[10][15];
	Boolean position[][] = new Boolean[20][30];
	Tower bildTower;
	JLayeredPane landPan;
	JPanel mousePan;
	int cost;
	GameManager gm;

    GroundPan(GameManager gameM)
    {
        //setBorder(new TitledBorder("groundPan"));
		gm = gameM;
		super.setBounds(5,10,490,350);
		super.setLayout(null);
		for (int i = 0; i < 20 ; i++ )
			for (int j = 0; j < 30 ; j++ )
				position[i][j] = new Boolean(false);
		addLand();
    }

	public void addLand()
	{
		landPan = new JLayeredPane();
		landPan.setBounds(5,0,200,500);
		landPan.setLayout(null);
		mousePan = new JPanel();
		mousePan.setBounds(0,0,30,30);
		mousePan.setBackground(Color.white);
		landPan.add(mousePan, new Integer(70));
		mousePan.setVisible(false);
		

		landPan.setBorder(new TitledBorder(""));
		int landType[][] = {
			{2,2,1,2,2,2,2,2,2,2,2,2,2,2,2},
			{2,0,1,0,0,0,0,0,0,0,0,0,0,0,2},
			{2,0,1,0,0,1,1,1,1,1,1,1,1,0,2},
			{2,0,1,0,0,1,0,0,0,0,0,0,1,0,2},
			{2,0,1,0,0,1,0,0,0,0,0,0,1,0,2},
			{2,0,1,0,0,1,0,0,1,1,1,1,1,0,2},
			{2,0,1,0,0,1,0,0,1,0,0,0,0,0,2},
			{2,0,1,1,1,1,0,0,1,0,0,0,0,0,2},
			{2,0,0,0,0,0,0,0,1,1,1,1,1,1,1},
			{2,2,2,2,2,2,2,2,2,2,2,2,2,2,2}
			};
		
		for (int i = 0 ; i < 10 ; i++ )
		{
			for (int j = 0; j < 15; j++)
			{
				if (landType[i][j] == 0)
				{
					land[i][j] = new Land(j*30, i*30, 0);
					position[i*2][j*2] = true;
					position[i*2][j*2+1] = true;
					position[i*2+1][j*2] = true;
					position[i*2+1][j*2+1] = true;
					
				}
				else if (landType[i][j] == 1){
					land[i][j] = new Land(j*30, i*30, 1);					
				}
				else 
					land[i][j] = new Land(j*30, i*30, 2);
								
				land[i][j].setBounds(j*30, i*30, 30, 30);
				landPan.add(land[i][j], new Integer(10));
			}
		}
		
		landPan.setBounds(10,40,450,300);
		landPan.addMouseListener(this);
		landPan.addMouseMotionListener(this);
		add(landPan);
		
	}

	public void bildTower(Tower bt ,int buyCost) {
		bildTower = bt;
		System.out.println(bt);
		cost = buyCost;
	}

	public void mousePressed (MouseEvent  e)
	{
		int pI = e.getY()/15;
		int pJ = e.getX()/15;
		
		if (bildTower != null && position[pI][pJ] && position[pI][pJ+1] && position[pI+1][pJ] && position[pI+1][pJ+1])
		{
			int x= e.getX() - e.getX()%15;
			int y = e.getY() - e.getY()%15;
			//System.out.println("position[" + e.getX()/15 + "][" + e.getY()/15 + "]");			
			bildTower.setXY(x,y, pI, pJ);
			bildTower.setBounds(x, y, 30,30);
			landPan.add(bildTower , new Integer(50));
			Tower.Æ÷Åº pt = bildTower.getÆ÷Åº();
			pt.setXY(x+10 , y + 10);
			pt.setBounds(x+10, y + 10, 10, 10);
			landPan.add(pt , new Integer(60));
			bildTower.Á¤Âû¿µ¿ª(e.getY()/30 ,e.getX()/30, land);
			gm.addAllTower(bildTower);
			bildTower = null;
			mousePan.setVisible(false);
			
			position[pI][pJ] = false;
			position[pI][pJ+1] = false;
			position[pI+1][pJ] = false;
			position[pI+1][pJ+1] = false;			
			gm.buy(cost);
		}
		else{
			bildTower = null;
			mousePan.setVisible(false);
		}
	}
	
    public void mouseClicked (MouseEvent  e) {}// 
    public void mouseEntered (MouseEvent  e)
	{
		if (bildTower != null)
			mousePan.setVisible(true);		
	}// 
    public void mouseExited (MouseEvent  e)
	{		
		if (bildTower != null)
			mousePan.setVisible(false);		
	}// 
    public void mouseReleased (MouseEvent  e){}
	
	public void mouseMoved(MouseEvent e)
	{
		if (bildTower != null)
		{
			//System.out.println("¸Þ·Õ");
			int x = e.getX() - e.getX()%15;
			int y = e.getY() - e.getY()%15;
			mousePan.setBounds(x, y, 30 ,30);
			
			int pI = e.getY()/15;
			int pJ = e.getX()/15;
			if (position[pI][pJ] && position[pI][pJ+1] && position[pI+1][pJ] && position[pI+1][pJ+1])
				mousePan.setBackground(Color.white);
			else
				mousePan.setBackground(Color.red);
		}
	}
	public void mouseDragged(MouseEvent e)
	{}

	public Land[][] getLand(){
		return land;
	}

	public void addUnit(Unit unit)
	{
		landPan.add(unit , new Integer(100));
	}

	public void removeUnit(Unit u)
	{
		landPan.remove(u);
		u.removeMouseListener(u);
		u = null;
		landPan.repaint();
	}
	public void removeTower(Tower t)
	{
		if (t == null)
		{}
		else
		{
			landPan.remove(t);
			t.removeMouseListener(t);
			int psI = t.getPositionI();
			int psJ = t.getPositionJ();
			//land[psI][psJ].removeTower(t);
			position[psI][psJ] = true;
			position[psI][psJ] = true;
			position[psI][psJ+1] = true;
			position[psI+1][psJ] = true;
			position[psI+1][psJ+1] = true;
			for (int i = 0 ; i < 10 ; i++ )
				for (int j = 0 ; j < 15 ; j++ )
					land[i][j].removeTower(t);			
		
			t = null;
			landPan.repaint();
		}
	}	
}

class Land extends JPanel
{
	int x;
	int y;
	int type = 0;
	Vector <Unit>unitVec;
	Vector <Tower>towerVec;
	Image img;
	boolean mouseIn;

	Land(int x, int y , int t)
	{
		this.x = x;
		this.y = y;
		type = t;
		unitVec = new Vector<Unit>();
		towerVec = new Vector<Tower>();
		Toolkit toolit = Toolkit.getDefaultToolkit();
		try{
			if ( t == 1)
				img = toolit.getImage("./ÀÌ¹ÌÁö/¶¥.gif");
		else if (t == 0)
				img = toolit.getImage("./ÀÌ¹ÌÁö/Ç®1.gif");
		else
				img = toolit.getImage("./ÀÌ¹ÌÁö/»ê.gif");		
		}catch(Exception e){
			System.out.println(e);
		}		
	}

	public void clearTowerVec(){
		towerVec.clear();
	}

	public void paintComponent(Graphics g)
	{
		g.drawImage(img,0,0,30,30,this);
		if (mouseIn){
			g.setColor(Color.red);
			g.fillRect(0,0,30,30);
		}				
	}

	public void ¹üÀ§°ø°Ý(int d, Color c, boolean ¹üÀ§)
	{
		if (type == 1)
		{
			for (int i = 0 ; i < unitVec.size() ; i++ )
			{
				Unit unit = unitVec.get(i);
				unit.damage(d,c,¹üÀ§);
			}
		}
	}

	public int getX(){
		return x;
	}
	public int getY() {
		return y;
	}
	public int getType(){
		return type;
	}
	
	public Vector<Unit> getUnitVec()
	{
		return unitVec;
	}

	public void addUnitVec(Unit u)
	{
		if (unitVec.contains(u))
			System.out.println("+u");
		else
			unitVec.add(u);		
	}
	public void removeUnitVec(Unit u)
	{
		if (unitVec.contains(u))
		{
			unitVec.remove(u);
			for (int i = 0 ; i < towerVec.size() ; i++ )
			{
				Tower t = towerVec.get(i);
				t.lostUnit(u);
				//System.out.println("À¯´ÖÅ»Ãâ");
			}
		}
		else
			System.out.println("-u");
	}

	public Vector<Tower> getTower()	{
		return towerVec;
	}
	public void addTower(Tower t)
	{
		if (towerVec.contains(t))
			System.out.println("+t");
		else
			towerVec.add(t);
	}
	public void removeTower(Tower t)
	{
		if (towerVec.contains(t))
			towerVec.remove(t);
		else
		{//System.out.println("-t");
		}
	}

	public void setMouseIn(boolean m){
		mouseIn = m;
	}
}