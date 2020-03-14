package net.wasamon.geister.player;

import net.wasamon.geister.utils.Direction;

import net.wasamon.geister.utils.*;
import net.wasamon.geister.server.*;
import java.util.*;

public class MctsPlayer extends BasePlayer{
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
	    if(0> dir || dir > 3){
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
	double uctValue;
	double winRateSave;
	Node child;
	
	List<mctsItem> list=new ArrayList<mctsItem>();
	
	Map<String ,Node>children;
	
	Node(String state){
	    this.state=state;
	    this.w=0;
	    this.n=0;
	    this.uctValue=0;
	    list=setItem(this.state);
	    this.children= new HashMap<String ,MctsPlayer.Node>();
	    this.child=null;
	}

	//this method is expand.it trust that method is used by  parent node
	public void expand(String s){
	    this.children.put(s,new Node(s));	
	}
	
	public boolean nearlyEquals(String s){
	    String key=this.state;
	    for(int i=0;i<key.length();i=i+3){
		if(Integer.parseInt(key.substring(i,i+1)) != Integer.parseInt(s.substring(i,i+1)) || Integer.parseInt(key.substring(i+1,i+2)) != Integer.parseInt(s.substring(i+1,i+2)))
		    return false;	 
	    }
	    return true;	    	    
	    	    
	}
	    
	
	//this method returns the node which is key of parameter,
	//if the node is not founded,this method returns null
	public Node nodeSearch(String s){	    
	    int i=0;
	    
	    if(this.children == null){
		return null;
	    }
	    	    if(root.state.equals(s)){
	    //if(root.nearlyEquals(s)){
		    return this;
		}
	    Node  v=null;
	    for(String key:this.children.keySet()){
		Node nxt=this.children.get(key);
		if(key.equals(s)){
		    //		    System.out.println("HIT popotan");
	    	    return nxt;
		}
	    }
	    
	    for(String key:this.children.keySet()){
		Node nxt=this.children.get(key);
		if(key.equals(s)){
			    //if(nxt.nearlyEquals(s)){
		    return nxt;
		}
		else{		    
		    v=nxt.nodeSearch(s);
		    if(v!=null){
			break;
		    }
		}
	    }

	    if(v!=null){
		return v;
	    }else{
		return null;
	    }
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
	    mctsItem itemDummy = v.list.get(no);
	    int x=itemDummy.getX(),y=itemDummy.getY();
	    int moveNo;
	    String exColor;
	    if(v.escaped(dir,no,itemDummy)){
		//	System.out.println("escaped item");
		return v.decodeItem();
	    }
	    if(!itemDummy.isMove(dir)){
		//		System.out.println("this moveing is unalbe");
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
		moveNo=this.isPosition(x,y);
		if(moveNo == -1){
		    itemDummy.setX(x);
		    itemDummy.setY(y);
		    return v.decodeItem();
		}else{
		    if(0<=no && no<=7){		    
			if(0<=moveNo && moveNo<=7){
			    return null;
			}else {
			    itemDummy.setX(x);
			    itemDummy.setY(y);
			    
			    itemDummy= v.list.get(moveNo);
			    itemDummy.setX(9);
			    itemDummy.setY(9);
			    itemDummy.setColor("p");
					    
			    return v.decodeItem();
			}
		    }else{
			if(8<=moveNo && moveNo<=15){
			    return null;
			}else {
			    itemDummy.setX(x);
			    itemDummy.setY(y);

			    itemDummy= v.list.get(moveNo);
			    itemDummy.setX(9);
			    itemDummy.setY(9);
			    exColor=itemDummy.getColor();
			    exColor=exColor.toLowerCase();
			    itemDummy.setColor(exColor);
					    
			    return v.decodeItem();
			}
			
		    }
		    
		}
		
	    }
	}

	public boolean escaped(int dir,int no,mctsItem item ){
	    int x=item.getX(),y=item.getY();
	    if(item.getColor().equals("R")){
		return false;
	    }
	    if(0<=no && no<=7){
		if(dir==2 && x==5 && y==0){
		    item.setX(8);
		    item.setY(8);
		    return true;
		}else if(dir==3 && x==0 && y==0){
		    item.setX(8);
		    item.setY(8);
		    return true;
		}		       
	    }else if(8<=no && no<=15){
		if(dir==2 && x==5 && y==5){
		    System.out.println("Abanana");
		    item.setX(8);
		    item.setY(8);
		    return true;
		}else if(dir==3 && x==0 && y==5){
		    System.out.println("Bbanana");
		    item.setX(8);
		    item.setY(8);
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
		System.out.println("n:::"+nxt.n+",w:::"+nxt.w);
		//nxt.printItem(nxt.list);
		nxt.printAllInformation();
	    }
	}	
    }

    public Node root;
    //    public Node parent;
    public List<Node>  sameTest=new ArrayList<Node>();

    public List<Integer> takeItem=new ArrayList<Integer>();
    boolean winOwn=false;
    boolean winOpposite=false;
    static int oppositeBcolor=0;
    static int oppositeRcolor=0;

    static int aaa=0;
    static int bbb=0;
    static int ccc=0;
    static int ddd=0;
    static int eee=0;       
        
    public Node addNode(String s , Node p){
	if(root==null){
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
		if(!sameTest.contains(n)){
		    sameTest.add(n);
		}
		return n;
	    }else{
		p.expand(s);
		return p.children.get(s);
		
	    }
	}
    }

    public boolean winJudge(Node n){
	int rcnt=0,bcnt=0,pcnt=0;
	for(int i=0;i<n.list.size();i++){
	    if(n.list.get(i).getX()==8 && n.list.get(i).getY()==8){
		if(0<=i && i<=7){
		    aaa++;
		    winOwn=true;
		    return true;
		}else if(8<=i && i<=15){
		    bbb++;
		    winOpposite=true;
		    return true;
		}		
		
	    }
	}

	for(int i=0;i<8;i++){
	    if("R".equals( n.list.get(i).getColor() )){
		rcnt++;
	    }else if("B".equals( n.list.get(i).getColor() )){
		bcnt++;
	    }
	}
	for(int i=8;i<16;i++){
	    if(!takeItem.contains(i) && "p".equals( n.list.get(i).getColor() )){
		pcnt++;
	    }	    
	}
	if(rcnt==0){
	    ccc++;
	    winOwn=true;
	    return true;
	}else if(bcnt==0){
	    ddd++;
	    winOpposite=true;
	    return true;
	}else if(pcnt>(3-oppositeRcolor)){	    
	    eee++;
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
	    System.out.println("parent child i:"+i);
	    v=sameTest.get(i);
	    System.out.println("parent state:"+v.state);
	    for(String key:v.children.keySet()){
		Node nxt=v.children.get(key);
		System.out.println("          child state:"+nxt.state);
		nxt2=nxt;
	    }	    
	}

	
    }
    
    public int[] getNextHand(String curentState,Node parent){
	Random rand = new Random(Calendar.getInstance().getTimeInMillis());
	String key = null;
	int num,dir;
	Node opposite = null;
	Node save;

	Stack<Node> stack = new Stack<Node>();
	
	if(root == null)
	    root=addNode(curentState,null);

	//node v is root of this method
	//Node v =root.nodeSearch(curentState);
	Node v=null;
	//	System.out.println("nodeSearch in GNH"); 		    
	if(parent==null)
	    v=root.nodeSearch(curentState);
	else
	    v =parent.nodeSearch(curentState);
	
	//curentStaten have not set on node
	//* parent is un
	if(v == null){
	  
	    v=addNode(curentState,parent);	    
	}

	save=v;
	for( int i=0;i<50;i++){
	    boolean flag=true;
	    v=save;
	    //System.out.println("GAME START^^^^GAME START^^^^GAME START^^^^GAME START^^^^GAME START^^^^GAME START^^^^GAME START");
	    //System.out.println(i+"kaime");	    
	    while(flag){
		//my turn
		//System.out.println("MY TURN");
		//System.out.println("before:"+v.state);
		while(key==null){
		    num = rand.nextInt(8);
		    dir = rand.nextInt(4);		    
		    
		    //System.out.println("erabaretano ha (dir,num) = ("+dir+","+num+")");
		    key = v.moveItem(dir,num);
		}
		//System.out.println("after :"+key);
		opposite = new Node(key);
		key=null;

		if(winJudge(opposite)){
		    //System.out.println("how many times of i:"+i);
		    //System.out.println(opposite.state);
		    flag=false;
		}
	    
		//your turn
		//System.out.println("YOUR TURN");
		//System.out.println("before:"+key);
		while(key==null){
		    num = rand.nextInt(8)+8;
		    dir = rand.nextInt(4);
		    
		    //System.out.println("erabaretano ha (dir,num) = ("+dir+","+num+")");
		    key = opposite.moveItem(dir,num);
		}
		//System.out.println("after :"+key);
		v = addNode(key,v);
		stack.add(v);
		key=null;

		if(winJudge(v)){
		    //System.out.println("how many times of i:"+i);
		    flag=false;
		}	      
	    }

	    while(!stack.empty()){
		Node reward = stack.pop();
		if(winOwn){
		    reward.n++;
		    reward.w++;
		}else if(winOpposite){
		    reward.n++;
		    reward.w--;
		}else{
		    //System.out.println("NO HIT");
		}
	    }
	
	    winOwn= false;
	    winOpposite=false;

	    //System.out.println("GAME SET----GAME SET----GAME SET----GAME SET----GAME SET----GAME SET");
	}

	System.out.println("\ncolor count");
	System.out.println("B count:"+oppositeBcolor);
	System.out.println("R count:"+oppositeRcolor);	    

	
	//	Node test=root.nodeSearch(curentState);	
	//Node maxUctNode=uct(save);
       	//int testBox[]=searchDirection(save,maxUctNode);
	int testBox[]=evaluation(save);


	return testBox;
    }

    public int[] searchDirection(Node parent,Node child){
	int a[]= new int[2];
	int dirX=0,dirY=0;
	a[0]=8;
	//System.out.println("parent:"+parent.state);
	//System.out.println("child:"+child.state);
	for(int i=0;i<8;i++){
	    if(child.list.get(i).getX()==9 )
		continue;
	    if(child.list.get(i).getX()==8){
		if(parent.list.get(i).getX()==0 && parent.list.get(i).getY()==0 ){
		    a[0]=i;
		    a[1]=2;
		}else if(parent.list.get(i).getX()==5 && parent.list.get(i).getY()==0){
		    a[0]=i;
		    a[1]=1;
		}
		  
	    }
		
	    dirX=parent.list.get(i).getX()-child.list.get(i).getX();
	    dirY=parent.list.get(i).getY()-child.list.get(i).getY();
	    if(dirX==-1){
		a[0]=i;
		a[1]=1;
	    }else if(dirX==1){
		a[0]=i;
		a[1]=2;
	    }else if(dirY==-1){
		a[0]=i;
		a[1]=3;
	    }else if(dirY==1){
		a[0]=i;
		a[1]=0;
	    }
	}

	if(a[0]==8){
	    //System.out.println("black coffe");
	    int saveOwn=0,saveOpposite=0;
	    for(int i=0;i<8;i++){
		if(parent.list.get(i).getX() != child.list.get(i).getX() && parent.list.get(i).getY() != child.list.get(i).getY())
		    saveOwn=i;
	    }
	    for(int i=8;i<16;i++){
		if(parent.list.get(i).getX() != child.list.get(i).getX() || parent.list.get(i).getY() != child.list.get(i).getY())
		    if(child.list.get(i).getX()!=9){
			//System.out.println("bakeratta");
			saveOpposite=i;
		    }
	    }
	    dirX=parent.list.get(saveOwn).getX()-child.list.get(saveOpposite).getX();
	    dirY=parent.list.get(saveOwn).getY()-child.list.get(saveOpposite).getY();
	    // System.out.println("dirX:"+dirX+",dirY:"+dirY);
	    //System.out.println("saveOwn:"+saveOwn);
	    //System.out.println("saveOpposite:"+saveOpposite);	    
	    if(dirX==-1){
		a[0]=saveOwn;
		a[1]=1;
	    }else if(dirX==1){
		a[0]=saveOwn;
		a[1]=2;
	    }else if(dirY==-1){
		a[0]=saveOwn;
		a[1]=3;
	    }else if(dirY==1){
		a[0]=saveOwn;
		a[1]=0;
	    }

	}

	
	return a;
    }
    
    public Node uct(Node n){
	double total=0.0;
	Node save=null;
	Random r = new Random(Calendar.getInstance().getTimeInMillis());
	for(String key:n.children.keySet()){
	    Node kari=n.children.get(key);
	    total=total+kari.n;
	}
	//double epsiron=1E-5;
	double epsiron=0.0;
	
	double c=Math.sqrt(2);

	for(String key:n.children.keySet()){
	    Node v=n.children.get(key);
	    if(v.n==0){
		continue;
	    }
	    double winRate=(double)v.w/((double)v.n+epsiron);
	    double searchValue=c*Math.sqrt(Math.log(total)/((double)v.n+epsiron));
	    v.uctValue=winRate+searchValue;					   

	    
	    System.out.println(v.state);
	    System.out.println("uctValue:"+v.uctValue+"------winRate:"+winRate+",searchValue:"+searchValue);
	    System.out.println("total-n:"+total);
	    System.out.println("n:"+v.n+",,,w:"+v.w);	    
	    System.out.println();

	    
	    if(save==null){
		save=v;
	    }else if(save.uctValue<v.uctValue){
		save=v;
	    }else if(save.uctValue==v.uctValue){
		if(save.n<v.n)
		    save=v;
	    }	
	}
	System.out.println("erabaretanoha:"+save.state);
	

	return save;
	
    }

    public class winset{
	int tw;
	int tn;

	public winset(int tw,int tn){
	    this.tw=tw;
	    this.tn=tn;
	}
    }

    public int[] evaluation(Node n){
	List<Integer> retKey=new ArrayList<Integer>();
	double save=0.0;
	double saveTotaln=0.0;
	//double epsiron=1E-5;
	double epsiron=0.0;
	Map<List,winset> map=new HashMap<List,winset>();

	System.out.println("\nevaluation start");
	for(String s:n.children.keySet()){
	    Node v=n.children.get(s);	    
	    if(v.n==0){
		continue;
	    }
	    int dirnum[]=searchDirection(n,v);
	    List<Integer> key=new ArrayList<Integer>();
	    key.add(dirnum[0]);
	    key.add(dirnum[1]);
	    //	    System.out.println("parent:"+n.state);
	    //System.out.println("parent:"+v.state);	    
	    //System.out.println("num-"+key.get(0)+",dir-"+key.get(1));
	    winset ws=map.get(key);
	    if(ws!=null){
		ws.tn=ws.tn+v.n;
		ws.tw=ws.tw+v.w;		
	    }else{
		map.put(key,new winset(v.w,v.n));       	    
	    }
	}
	for(List<Integer> key:map.keySet()){
	    winset ws=map.get(key);
	    double winRate=(double)ws.tw/((double)ws.tn+epsiron);	    	       
	    System.out.println("\n"+"num-"+key.get(0)+",dir-"+key.get(1));	    
	    System.out.println("win times-"+ws.tw+",visit times"+ws.tn);
	    System.out.println("winRate:"+winRate);
	    if(save==0.0){
		save=winRate;
		saveTotaln=ws.tn;
		retKey=key;
	    }else if(save<winRate){
		save=winRate;
		saveTotaln=ws.tn;
		retKey=key;
	    }else if(save==winRate){
		if(saveTotaln<ws.tn){
		    save=winRate;
		    saveTotaln=ws.tn;
		    retKey=key;
		}
	    }
	}
	int returnDirnum[]=new int[2];
	returnDirnum[0]=retKey.get(0);
	returnDirnum[1]=retKey.get(1);	
	System.out.println("times of escaped own items :"+aaa);
	System.out.println("times of escaped opposite items :"+bbb);
	System.out.println("times of taken blue items :"+ccc);
	System.out.println("times of taken red items :"+ddd);
	System.out.println("times of take p items :"+eee);
	aaa=0;bbb=0;ccc=0;ddd=0;eee=0;
	System.out.println("erabaretanoha:::num-"+returnDirnum[0]+",dir-"+returnDirnum[1]);

	return returnDirnum;
	
    }

    public static void main(String[] args) throws Exception {
	MctsPlayer p = new MctsPlayer();
        p.init(args[0], Integer.parseInt(args[1]));
        System.out.println(p.setRedItems("BCDE"));
        Random r = new Random(Calendar.getInstance().getTimeInMillis());
        Direction[] dirs = new Direction[] { Direction.NORTH, Direction.EAST, Direction.WEST, Direction.SOUTH };
	int cnt = 0;
       
	String stringBoardInfo;
	Node parent=null;
	Node save=null;
        GAME_LOOP: while (true) {
	    System.out.println("GAME_LOOP start");
            stringBoardInfo=p.waitBoardInfo();
	    stringBoardInfo=stringBoardInfo.substring(5-1);

	    System.out.println("tikan");
	    System.out.println("before:"+stringBoardInfo);
	    String ownItem=stringBoardInfo.substring(0,24);
	    String oppositeItem=stringBoardInfo.substring(24);
	    oppositeItem=oppositeItem.replace("r","p");
	    oppositeItem=oppositeItem.replace("b","p");
	    stringBoardInfo=ownItem.concat(oppositeItem);
	    System.out.println("after:"+stringBoardInfo);

	    save=p.addNode(stringBoardInfo,parent);

	    Item[] oppositeItems=p.getOppositeItems();
	    int bcount=0,rcount=0;
	    // System.out.println("symbol test");
	    int j=0;
	    for(Item i:oppositeItems){
		//System.out.println(i.getColor().getSymbol());
		if(i.getColor().getSymbol().equals("B")){
		    bcount++;
		    p.takeItem.add(j+8);
		}
		else if(i.getColor().getSymbol().equals("R")){
		    rcount++;
		    p.takeItem.add(j+8);
		}
		j++;
	    }
	    p.takeItem=new ArrayList<Integer>(new HashSet<>(p.takeItem));
	    oppositeBcolor=bcount;
	    oppositeRcolor=rcount;
	    
	    
            p.printBoard();
            if (p.isEnded() == true)
                break GAME_LOOP;
            Item[] own = p.getOwnItems();
            MY_TURN: while (true) {
		System.out.println("MY_TURN start");
		int nxt[]=p.getNextHand(stringBoardInfo,parent);
                int i = nxt[0];		
                int d = nxt[1];
                if (own[i].isMovable(dirs[d])) {
		    System.out.println("i:"+i+"  d:"+d);
		    System.out.println("position:x--"+own[i].getX()+"  y--"+own[i].getY());		   
		    p.move(own[i].getName(), dirs[d]);
                    p.printBoard();
                    break MY_TURN;
                }else{
		    System.out.println("false");
		    System.out.println("i:"+i+"  d:"+d);
		    System.out.println("position:x--"+own[i].getX()+"  y--"+own[i].getY());		   
		}
	    }
	    parent=save;
	    System.out.println(cnt++);
	    //	    parent=save;
	}
	if (p.isWinner()) {
	    System.out.println("won");
	} else if (p.isLoser()) {
	    System.out.println("lost");
	} else if (p.isDraw()) {
	    System.out.println("draw");
	}
	p.root.printAllInformation();
	p.printTest();
	System.out.println("testnotest:"+0/1);
    }

}

    
    
