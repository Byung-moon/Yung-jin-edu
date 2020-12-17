import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.border.*;
import java.util.*;

public class TowerDefense 
{
    JFrame frame;
	Container con;
	GameManager gm;

	TowerDefense()
	{
		frame = new JFrame("타워디펜스");
		con = frame.getContentPane();
		launchFrame();
	}
        
    public void launchFrame()
    {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       	gm = new GameManager(this);
		con.add(gm);
		frame.setSize(750,550);
        frame.setVisible(true);
    }

    public static void main(String[] args)
    {
        TowerDefense td = new TowerDefense();
        //td.launchFrame();
    }
	public void 게임초기화()
	{
		gm.setVisible(false);
		con.remove(gm);
		gm = new GameManager(this);
		con.add(gm);
		con.repaint();
	}
}

class GameManager extends JPanel implements ActionListener
{
	int money = 300;
	int level = 0;
	int nextTime = 30;
	int score = 0;
	int life = 20;
	boolean playing;
	Thread game = new Thread()
	{
		public void run()
		{
			for (int i = 0 ; i < 40 ; i++ )
			{
				if (playing)
				{				
					level++;
					topPan.setLevelJL(level);
					for (int j = 0 ; j < 10 ; j++ )
					{
						if (playing)
						{
							makeUnit();
							if (i % 5 == 4){
								try{Thread.sleep(1000*10);}
								catch(Exception e){System.out.println(e);}
								break;
							}
							try{Thread.sleep(1000);}
							catch(Exception e){System.out.println(e);}
						}
					}

					money = money + 10 + i * 10;
					
					for (int k = 0 ; k < 20 ; k++ )
					{
						if (playing)
						{
							nextTime--;
							topPan.setNextTimeJL(nextTime);
							try{Thread.sleep(500);}
							catch(Exception e){System.out.println(e);}
						}
					}
					nextTime = 20;
					topPan.setNextTimeJL(nextTime);
				}
			}
		}
	};
	TowerDefense td;
	GroundPan ground;
	TopPan topPan;
	LeftPan leftPan;
	RightPan rightPan;
	static Component select;
	Vector <Tower>allTower = new Vector<Tower>();

	GameManager(TowerDefense td)
	{
		this.td = td;
		ground = new GroundPan(this);
		topPan = new TopPan(money);
		leftPan = new LeftPan(ground);
		rightPan = new RightPan(this);
		launchManager();
	}

	public void launchManager()
	{
		setLayout(null);
		setBounds(10,5,750,550);
		setBorder(new TitledBorder("managerPan"));
		add(topPan);
		add(leftPan);
		add(rightPan);
	}
	public void buyTower(String name , int cost)
	{
		Tower tower = null;
		System.out.println(name);
		if (money >= cost)
		{
			if (name.equals("기본타워"))
				tower = new NomalTower(this);
			else if (name.equals("파이어타워"))
				tower = new FireTower(this);
			else if (name.equals("아이스타워"))
				tower = new IceTower(this);
			else if (name.equals("에어타워"))
				tower = new AirTower(this);
			else if (name.equals("독타워"))
				tower = new PoisonTower(this);
			else 
				System.out.println("만족하는 타워가 없습니다.");
			tower.addMouseListener(tower);
			ground.bildTower(tower , cost);
		}
		else
			System.out.println("돈이부족해요");
	}
	public void buy(int cost)
	{
		money = money - cost;
		topPan.setMoneyJL(money);
	}
	public void sellTower(Tower t, int cost)
	{
		money = money + cost;
		topPan.setMoneyJL(money);
		ground.removeTower(t);
	}
	public void upgrade(Tower t , int cost)
	{
		if (money < cost){
			System.out.println("업그래이드 비용이 부족합니다.");
		}
		else
		{
			money = money - cost;
			topPan.setMoneyJL(money);
			t.upgrade();
		}
	}
	public void addAllTower(Tower t){
		allTower.add(t);
	}
	public void repaintAllTower()
	{
		for (int i = 0 ;  i < allTower.size() ; i++ )
		{
			Tower t = allTower.get(i);
			t.repaint();
		}
	}
	public void removeAllTower(Tower t){
		allTower.remove(t);
	}

	public Unit makeUnit()
	{
		Unit unit = null;
		if (level%5 == 1)
			unit = new NormalUnit(0, ground.getLand(), this , level);
		else if (level % 5 == 2)
			unit = new 오각Unit(0, ground.getLand(), this , level);
		else if (level % 5 == 3)
			unit = new 이뮨Unit(0, ground.getLand(), this , level);
		else if (level%5 == 4)		
			unit = new FlyUnit(0, ground.getLand(), this , level);
		else if (level % 5 == 0)
			unit = new BossUnit(0, ground.getLand(), this , level);
		else
			System.out.println("맞는타입의 유닛이 없습니다.");
				
		unit.addMouseListener(unit);
		ground.addUnit(unit);
		ground.repaint();
		unit.moveStart();
		return unit;
	}

	public void actionPerformed(ActionEvent ae)
	{
		if (playing == false)
		{
			level = 0;
			playing = true;
			game.start();
			rightPan.setEnabledStartBtn(false);
		}
	}

	public void showInfor(JPanel jp){
		rightPan.showInfor(jp);
	}

	public void deadUnit(Unit u)
	{
		ground.removeUnit(u);
		u = null;
		money = money + 1 + level/2;
		topPan.setMoneyJL(money);
		score = score + 10;
		topPan.setScoreJL(score);
	}
	public void exitUnit()
	{
		life -= 1;
		topPan.setLifeJL(life);
		if (life < 1)
		{
			if (playing)
			{
				playing = false;
				JOptionPane.showMessageDialog(new JFrame(),"게임오버","알림",JOptionPane.PLAIN_MESSAGE);
				System.out.println("게임오버");				
				td.게임초기화();
			}			
			rightPan.setEnabledStartBtn(true);
		}
	}
}

class TopPan extends JPanel
{
	JLabel levelJL, nextTimeJL, moneyJL, scoreJL , lifeJL;
	
	TopPan(int m)
	{
		levelJL = new JLabel("현제레벨 : 1");
		nextTimeJL = new JLabel("다음시간 : 20");
		moneyJL = new JLabel("돈 : " + String.valueOf(m) +"원");
		scoreJL = new JLabel("점수 : 0");
		lifeJL = new JLabel("생명 : 20");
		launchTopPan();
	}
	
	public void launchTopPan()
	{
		setBounds(10,20,720,30);
		setLayout(new FlowLayout(FlowLayout.CENTER, 50, 0));
		setBorder(new TitledBorder(""));
		add(lifeJL);
		add(levelJL);
		add(nextTimeJL);
		add(moneyJL);
		add(scoreJL);		
	}
	
	public void setLevelJL(int l) {
		levelJL.setText("현제레벨 : " + String.valueOf(l));
	}
	public void setNextTimeJL(int t) {
		nextTimeJL.setText("다음시간 : " + String.valueOf(t));
	}
	public void setMoneyJL(int m) {
		moneyJL.setText("돈 : " + String.valueOf(m));
	}
	public void setScoreJL(int s) {
		scoreJL.setText("점수 : " + String.valueOf(s));
	}
	public void setLifeJL(int l){
		lifeJL.setText("생명 : " + String.valueOf(l));
	}

}

class LeftPan extends JPanel
{
	GroundPan groundPan;
	JPanel unitLevelPan;
	String data[][] = {{"레벨1","레벨2","레벨3","레벨4","레벨5"},{"일반","달리기","이뮨","플라이","대장",}};
	String 속성[] = {"레벨1","레벨2","레벨3","레벨4","레벨5"};
	
	LeftPan(GroundPan gp)
	{
		groundPan = gp;
		unitLevelPan = new JPanel();
		launchLeftPan();
	}

	public void launchLeftPan()
	{
		setBounds(10,60,500,450);
		setLayout(null);
		setBorder(new TitledBorder(""));
		add(groundPan);
		unitLevelPan.setBounds(10,370,480,70);
		unitLevelPan.setBorder(new TitledBorder(""));
		JTable unitLevel = new JTable(data, 속성);
		unitLevelPan.add(unitLevel);
		add(unitLevelPan);
	}	
}

class RightPan extends JPanel
{
	JButton startBtn, nextBtn;
	CardLayout cardLayout = new CardLayout();
	JPanel inforPan, towerPan, informationPan;
	GameManager gm;

	RightPan(GameManager gm)
	{
		this.gm = gm;
		startBtn = new JButton("게임시작");
		startBtn.addActionListener(gm);
		nextBtn = new JButton("다음유닛");
		nextBtn.setEnabled(false);
		launchRightPan();
	}

	public void launchRightPan()
	{
		JPanel btnPan = new JPanel();
		btnPan.setBorder(new TitledBorder(""));
		btnPan.add(startBtn);
		btnPan.add(nextBtn);
		
		towerPan = new JPanel();
		towerPan.setBorder(new TitledBorder(""));
		Tower normalTw = new NomalTower(gm);
		normalTw.addMouseListener(normalTw);
		Tower fireTw = new FireTower(gm);
		fireTw.addMouseListener(fireTw);
		Tower iceTw = new IceTower(gm);
		iceTw.addMouseListener(iceTw);
		Tower airTw = new AirTower(gm);
		airTw.addMouseListener(airTw);
		Tower poisonTw = new PoisonTower(gm);
		poisonTw.addMouseListener(poisonTw);
		gm.addAllTower(normalTw);
		gm.addAllTower(fireTw);
		gm.addAllTower(iceTw);
		gm.addAllTower(airTw);
		gm.addAllTower(poisonTw);

		towerPan.add(normalTw);
		towerPan.add(fireTw);
		towerPan.add(iceTw);
		towerPan.add(airTw);
		towerPan.add(poisonTw);

		inforPan = new JPanel();
		inforPan.setBorder(new TitledBorder(""));
		inforPan.setLayout(cardLayout);
		informationPan = new JPanel();
		inforPan.add("디폴트", new JPanel());
		inforPan.add("정보", informationPan);
		cardLayout.show(inforPan,"디폴트");
						
		add(btnPan);
		add(towerPan);
		add(inforPan);
		setBounds(520,60,210,450);
		setBorder(new TitledBorder(""));
	}
	public void showInfor (JPanel in)
    {
		informationPan.removeAll();
		informationPan.add(in);
		cardLayout.show(inforPan,"디폴트");
		cardLayout.show(inforPan,"정보");
	}
	public void setEnabledStartBtn(boolean b)
	{
		startBtn.setEnabled(b);
	}
}