package net.wasamon.geister.player;

import net.wasamon.geister.utils.Direction;

import net.wasamon.geister.utils.*;
import net.wasamon.geister.server.*;
import java.util.*;

public class TestNodePlayer extends BasePlayer{
    public class mctsItem{
	int x;
	int y;
	String color;

	mctsItem(int x,int y,String color){
	    this.x=x;
	    this.y=y;
	    this.color=color;
	}

	public int getX(){
	    return this.x;
	}

	public int getY(){
	    return this.y;
	}

	public String getColor(){
	    return this.color;
	}

	public void setX(int x){
	    this.x=x;
	}

	public void setY(int y){
	    this.y=y;
	}

	public void setColor(String color){
	    this.color=color;
	}

	/*rule of direction
	  0...N
	  1...S
	  2...E
	  3...W
	*/
	public boolean isMove(int dir){
	    if(0> dir && dir > 3){
		System.out.println("direction is failed");	    
		return false;
	    }
	    if(dir == 0 ){
		if(0<= (this.y-1) && (this.y-1)<=5)
		    return true;
	    }else if(dir == 1){
		if(0<= (this.y+1) && (this.y+1)<=5)
		    return true;
	    }else if(dir == 2){
		if(0<= (this.x+1) && (this.x+1)<=5)
		    return true;
	    }else if(dir == 3){
		if(0<= (this.x-1) && (this.x-1)<=5)
		    return true;
	    }
	    return false;
	}


    }

    public class Node{
	int w; //times of winner
	int n; //times of visited
	String state;//Board information

	List<mctsItem> list=new ArrayList<mctsItem>();
	
	Map<String ,Node>children;
	
	Node(String state){
	    this.state=state;
	    this.w=0;
	    this.n=1;
	    list=setItem(this.state);
	    this.children= new HashMap<String ,TestNodePlayer.Node>();
	}
	
	//this method is expand.it trust that method is used by  parent node
	public void expand(String s){
	    System.out.println("expand method is performed");
	    this.children.put(s,new Node(s));	
	}

	
	//this method returns the node which is key of parameter,
	//if the node is not founded,this method returns null
	public Node nodeSearch(String s){	    
	    int i=0;
	    i++;
	    if(this.children == null){
		return null;
	    }
	    if(root.state.equals(s)){
		return this;
	    }
	    for(String key:this.children.keySet()){
		Node nxt=this.children.get(key);
		if(key.equals(s)){
		    return nxt;
		}
		else{
		     nxt.nodeSearch(s);
		}
	    }
		    
	    return null;
	}
	
	public List<mctsItem>  setItem(String key){
	    List<mctsItem> list=new ArrayList<mctsItem>();
	    int x,y;
	    String color;
	    for(int i=0;i<key.length();i=i+3){
		x=Integer.parseInt(key.substring(i,i+1));
		y=Integer.parseInt(key.substring(i+1,i+2));
		color=key.substring(i+2,i+3);
		list.add(new mctsItem(x,y,color));
			 
	    }
	    return list;
	}

	public int isPosition(int x,int y){
	    for(int i=0;i<this.list.size();i++){
		if(x==this.list.get(i).getX() && y==this.list.get(i).getY()){
		    return i;
		}
	    }
	    return -1;			    		
	    
	}
	
	public String moveItem(int dir,int no){
	    Node v = new Node(this.state); 
	    mctsItem item = v.list.get(no);
	    int x=item.getX(),y=item.getY();
	    int moveNo;
	    if(escaped(dir,no,item)){
		System.out.println("escaped item");
		return v.decodeItem();
	    }
	    if(!item.isMove(dir)){
		System.out.println("this moveing is unalbe");
		return null;
	    }else{
		if(dir == 0 ){
		    y=y-1;
		}else if(dir == 1){
		    y=y+1;
		}else if(dir == 2){
		    x=x+1;
		}else if(dir == 3){
		    x=x-1;
		}
		moveNo=isPosition(x,y);
		if(moveNo == -1){
		    item.setX(x);
		    item.setY(y);
		    System.out.println("SET position:x--"+x+",y--"+y);
		    return v.decodeItem();
		}else{
		    if(0<=no && no<=7){		    
			if(0<=moveNo && moveNo<=7){
			    System.out.println("There is already a item");
			    return null;
			}else {
			    item.setX(x);
			    item.setY(y);

			    item= v.list.get(moveNo);
			    item.setX(9);
			    item.setY(9);
			    item.setColor("p");
			    System.out.println("SET position:x--"+x+",y--"+y);
			    System.out.println("get an opposite item");
					    
			    return v.decodeItem();
			}
		    }else{
			if(8<=moveNo && moveNo<=15){
			    System.out.println("There is already a item");
			    return null;
			}else {
			    item.setX(x);
			    item.setY(y);

			    item= v.list.get(moveNo);
			    item.setX(9);
			    item.setY(9);
			    item.setColor("f");
			    System.out.println("SET position:x--"+x+",y--"+y);
			    System.out.println("get an own item");
					    
			    return v.decodeItem();
			}
			
		    }
		    
		}
		
	    }
	}

	public boolean escaped(int dir,int no,mctsItem item ){
	    int x=item.getX(),y=item.getY();
	    if(0<=no && no<=7){
		if(dir==2 && x==5 && y==0){
		    item.setX(8);
		    item.setY(8);
		    escapedOwnFlag = true;
		    return true;
		}else if(dir==3 && x==0 && y==0){
		    item.setX(8);
		    item.setY(8);
		    escapedOwnFlag = true;
		    return true;
		}		       
	    }else if(8<=no && no<=15){
		if(dir==2 && x==5 && y==5){
		    item.setX(8);
		    item.setY(8);
		    escapedOppositeFlag = true;
		    return true;
		}else if(dir==3 && x==0 && y==5){
		    item.setX(8);
		    item.setY(8);
		    escapedOppositeFlag = true;
		    return true;
		}		       
	    }
	    return false;
	}
	
	
    
    
	public String decodeItem(){
	    StringBuffer buf = new StringBuffer();
	    String sx,sy,scolor;
	    for(int i=0;i<this.list.size();i++){
		sx=String.valueOf(this.list.get(i).getX());
		sy=String.valueOf(this.list.get(i).getY());
		scolor=this.list.get(i).getColor();
		buf.append(sx);
		buf.append(sy);
		buf.append(scolor);
		
	    }
	    
	    return buf.toString();
	    
	}
    
    
	public void printItem(){
	
	    for(int i=0;i<this.list.size();i++){
		System.out.println("list information::x--"+this.list.get(i).getX()+",y--"+this.list.get(i).getY());
		System.out.println("color:"+this.list.get(i).getColor());
	    }
	    
	}
    
	public void printAllInformation(){
	    if(this == root)
		System.out.println(root.state);
	    if(this.children == null){
		return;
	    }
	    int i=0;
	    for(String key:this.children.keySet()){		
		System.out.println("times of i ==== "+i++);
		Node nxt=this.children.get(key);
		System.out.println(nxt.state);
		//nxt.printItem(nxt.list);
		nxt.printAllInformation();
	    }
	}	
    }

    public Node root;
    //    public Node parent;
    public List<Node>  sameTest=new ArrayList<Node>();

    boolean escapedOwnFlag; 
    boolean escapedOppositeFlag;
    boolean winOwn;
    boolean winOpposite;
    public void initJudge(){
	this.escapedOwnFlag=false; 
	this.escapedOppositeFlag=false;
	this.winOwn=false;
	this.winOpposite=false;
    }

        
    public Node addNode(String s , Node p){
	if(root==null){
	    System.out.println("root hit!!!!!!");
	    root=new Node(s);
	    return root;
	}

	Node v = root;
	if(p == null){
	    v.expand(s);
	    return v.children.get(s);
	      
	}
	else {
	    Node n = v.nodeSearch(s);

	    if(n != null){
		System.out.println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
		sameTest.add(n);
		return n;
	    }else{
		p.expand(s);
		return p.children.get(s);
		
	    }
	}
    }

    public boolean winJudge(Node n){
	int rcnt=0,bcnt=0,ucnt=0;
	if(escapedOwnFlag){
	    winOwn=true;
	    return true;
	}else if(escapedOppositeFlag){
	    winOpposite=true;
	    return true;
	}

	for(int i=0;i<n.list.size();i++){
	    if("R".equals( n.list.get(i).getColor() )){
		rcnt++;
	    }else if("B".equals( n.list.get(i).getColor() )){
		bcnt++;
	    }else if("u".equals( n.list.get(i).getColor() )){
		ucnt++;
	    }
	}
	if(rcnt==0){
	    System.out.println("HIT!! red");
	    winOwn=true;
	    return true;
	}else if(bcnt==0){
	    System.out.println("HIT!! blue");
	    winOpposite=true;
	    return true;
	}else if(ucnt<5){
	    System.out.println("HIT!! u-ray");
	    winOpposite=true;
	    return true;
	}

	return false;

    }

    public void printTest(){
	Node v = null;
	Node nxt2=null;
	System.out.println("same test");
	for(int i=0;i<sameTest.size();i++){
	    v=sameTest.get(i);
	    System.out.println("parent state:"+v.state);
	    for(String key:v.children.keySet()){
		Node nxt=v.children.get(key);
		System.out.println("          child state:"+nxt.state);
		nxt2=nxt;
	    }	    
	}
	System.out.println("print test of list");
	v.printItem();

	System.out.println("print test of decode");
	System.out.println(v.decodeItem());

	System.out.println("print test of moveItem::node--2nd nxt");
	System.out.println("before:"+nxt2.state);
	getNextHand(nxt2.state,nxt2);
	System.out.println("after :"+nxt2.moveItem(0,3));

	for(int i=0;i<sameTest.size();i++){
	    v=sameTest.get(i);
	    System.out.println("parent state:"+v.state);
	    for(String key:v.children.keySet()){
		Node nxt=v.children.get(key);
		System.out.println("          child state:"+nxt.state);
		nxt2=nxt;
	    }	    
	}

	
    }
    
    public void getNextHand(String curentState,Node parent){
	Random rand = new Random(Calendar.getInstance().getTimeInMillis());
	String key = null;
	int num,dir;
	Node opposite = null;
	Node save;
	
	if(root == null)
	    root=addNode(curentState,null);

	//node v is root of this method
	System.out.println("masakane masakane");
	Node v =root.nodeSearch(curentState);
	//curentStaten have not set on node
	//* parent is un
	if(v == null){
	    System.out.println("masakane HIT");
	    v=addNode(curentState,parent);
	}

	save=v;
	for( int i=0;i<100;i++){
	    initJudge();
	    boolean flag=true;
	    v=save;
	    System.out.println("GAME START^^^^GAME START^^^^GAME START^^^^GAME START^^^^GAME START^^^^GAME START^^^^GAME START");
		while(flag){
	    //	    for(int j=0;j<3;j++){
		//my turn
		System.out.println("MY TURN");
		System.out.println("before:"+v.state);
		while(key==null){
		    num = rand.nextInt(8);
		    dir = rand.nextInt(4);		    
		    //    num=3;
		    //dir=0;
		    System.out.println("erabaretano ha (dir,num) = ("+dir+","+num+")");
		    key = v.moveItem(dir,num);
		}
		System.out.println("after :"+key);
		opposite = new Node(key);
		key=null;

		if(winJudge(opposite)){
		    System.out.println("how many times of i:"+i);
		    flag=false;
		}
	    
		//your turn
		System.out.println("YOUR TURN");
		System.out.println("before:"+key);
		while(key==null){
		    num = rand.nextInt(8)+8;
		    dir = rand.nextInt(4);
		    //num=9;
		    //dir=1;
		    System.out.println("erabaretano ha (dir,num) = ("+dir+","+num+")");
		    key = opposite.moveItem(dir,num);
		}
		System.out.println("after :"+key);
		v = addNode(key,v);
		key=null;

		if(winJudge(v)){
		    System.out.println("how many times of i:"+i);
		    flag=false;
		}	      
	    }
	    System.out.println("GAME SET----GAME SET----GAME SET----GAME SET----GAME SET----GAME SET");
	}
	

    }
    
    public static void main(String[] args) throws Exception{
	TestNodePlayer p = new TestNodePlayer();
	
	String test;
	Node parent = null;
	
	p.init(args[0], Integer.parseInt(args[1]));
	System.out.println(p.setRedItems("BCDE"));
	
	Direction[] dirs = {
	    Direction.NORTH,
	    Direction.SOUTH,
	    Direction.WEST,
	    Direction.EAST,
	    

	    Direction.NORTH,
	    Direction.NORTH,
	    Direction.NORTH,
	    Direction.EAST,
	    Direction.WEST,
	    Direction.SOUTH,
	    Direction.NORTH,
	    Direction.NORTH,
	    Direction.WEST,
	    Direction.WEST,
	};
	test=p.waitBoardInfo();
	test=test.substring(5-1);
	parent=p.addNode(test,parent);
	
	p.printBoard();	
	for(Direction d: dirs){
	    Thread.sleep(2);
	    if(p.isEnded() == true) break;
	    System.out.println(p.move("a", d));
	    test=p.waitBoardInfo();

	    test=test.substring(5-1);
	    parent=p.addNode(test,parent);
		  
	    p.printBoard();
	}

       	p.root.printAllInformation();
	p.printTest();
	//		p.root.printAllInformation();
	if(p.isWinner()){
	    System.out.println("won");
	}else{
	    System.out.println("lost");
	}
    }

}
