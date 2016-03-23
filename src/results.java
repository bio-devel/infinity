
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;


public class results {

	static getValore g=new getValore();
	static dbAuth a=new dbAuth();
	static Object[] auth=a.getAuth();
	static funzioni f=new funzioni(auth);
	public static Label number,found,esec;
	public static Composite table_composite,primo;
	public static Listener paintListener;
	//private Object[] data;
	
	
	public static String exec_time(long time){
		String esec=null;
		long y=0;
		//System.out.println(time);
		int min=(int)(time/60000);
		y=time-(min*60000);
		int sec=(int)(y/1000);
		int milli=(int)(y-(sec*1000));
		esec=min+" min "+sec+" sec "+milli+" msec";
		return esec;
	}
	
	public Composite results(Composite content,final ConcurrentHashMap<Integer,Object> mirna,final funzioni f, final Object[] infoOpzioni,long time){
		/*
		 * data[0]=Hashmap<String,boolean>
		 * data[1]= Object[3]-> 0=inizio,1=fine,2=ricerca tss/mirna,3=hashmap id mirna
		 * data[2]= int per algoritmo
		 */
		Object[] data=new Object[4]; 
		System.arraycopy((Object[])(infoOpzioni[1]), 0, data, 0, 4);//(Object[])(info[1]));
		content.setBackground(new Color(content.getDisplay(),0,69,139));
		primo=new Composite(content,SWT.BORDER);
		primo.setData("name","primo");
		Composite secondo=new Composite(content,SWT.BORDER);
		secondo.setData("name","secondo");
		final Composite terzo=new Composite(content,SWT.BORDER);
		terzo.setData("name","terzo");
		
		Composite primoSx=new Composite(primo,SWT.NONE);
		primoSx.setData("name","primoSx");
		Label consensus=new Label(primoSx,SWT.NONE); //consensus length
		consensus.setText("Consensus matrix length: ");
		consensus.setData("name","consensus");
		found=new Label(primoSx,SWT.NONE); //mirna trovati
		found.setData("name","found");
		number=new Label(primoSx,SWT.NONE); //numero occorrenze totali
		number.setData("name","number");
		esec=new Label(primoSx,SWT.NONE); //tempo esecuzione
		esec.setData("name","esec");
		esec.setText("Execution time: "+exec_time(time));
		number.setText("Total number of occurrence: 0");
		found.setText("pre-miRNA found: 0 on "+mirna.size());
		
		Composite primoCen=new Composite(primo,SWT.NONE);
		primoCen.setData("name","primoCen");
		Label info=new Label(primoCen,SWT.WRAP);
		info.setData("name","info");
	    info.setText("Window length (filtering result list, relative to TSS):");
	    Label upLbl=new Label(primoCen,SWT.NONE);
	    upLbl.setData("name","upLbl");
	    upLbl.setText("Upstream: ");
	    Text up= new Text(primoCen,SWT.BORDER);
	    up.setData("name","up");
	    if(data[0]!=null){
	    	 up.setText(data[0].toString());
	    }else{
	    	up.setText("0");
	    }
	   
	    Label dwLbl=new Label(primoCen,SWT.NONE);
	    dwLbl.setData("name","dwLbl");
	    dwLbl.setText("Downstream: ");
	    Text down= new Text(primoCen,SWT.BORDER);
	    down.setData("name","down");
	    if(data[1]!=null){
	    	 down.setText(data[1].toString());
	    }else{
	    	down.setText("0");
	    }
	    Button filter=new Button(primoCen,SWT.PUSH);
	    filter.setData("name","filter");
		filter.setText("Filter");
		
		Composite primoDx=new Composite(primo,SWT.NONE);
		primoDx.setData("name","primoDx");
		Label searchLabel=new Label(primoDx,SWT.NONE);
		searchLabel.setData("name","searchLabel");
		searchLabel.setText("Search pre-miRNA: ");
		final Text searchMirna=new Text(primoDx,SWT.BORDER);
		searchMirna.setData("name","searchmirna");
		Button search=new Button(primoDx,SWT.PUSH);
		search.setData("name","search");
		search.setText("Search");
		
		
		
		table_composite=new Composite(secondo,SWT.NONE);
		table_composite.setData("name","table_composite");
		final Table table=new Table(table_composite,SWT.BORDER|SWT.FULL_SELECTION|SWT.V_SCROLL|SWT.MULTI);
	    table.setHeaderVisible(true);
	    table.setLinesVisible(true);
	    table.setSortDirection(SWT.DOWN);
	    table.setData("name","table");
	    String[] titles = {" pre-miRNA "," Location "," TSS(distance) ","Hits","Consensus"," Occurrence "};
	    int scelta=Integer.parseInt(data[2].toString());
	    switch (scelta){
	    case 1:
	    	titles[5]+="near miRNA (-"+data[0]+";+"+data[1]+")";
	    	break;
	    case 2:
	    	titles[5]+="from TSS (";
	    	if(Integer.parseInt(data[0].toString()) < 0)
	    		titles[5]+=data[0]+";";
	    	else titles[5]+="-"+data[0]+";";
	    	if(Integer.parseInt(data[1].toString()) < 0)
	    		titles[5]+=data[1]+")";
	    	else titles[5]+="+"+data[1]+")";
	    	///////titles[5]+="from TSS ("+data[0]+";"+data[1]+")";
	    	break;
	    case 3:
	    	titles[5]+="Absolute ("+data[0]+";"+data[1]+")";
	    	break;
	    }
	    Font font=new Font(table.getDisplay(), "Courier", 9, SWT.NORMAL);
	    table.setFont(font);
	
	    
		/*
		 * LISTENER
		 */
	    
	   
		  Listener sortListener = new Listener() {  
		         public void handleEvent(Event e) { 
		        	 Object[][] item=new Object[table.getItemCount()][table.getColumnCount()+2];
		        	 TableColumn column = (TableColumn)e.widget;
		        	 int index=table.indexOf(column);
		        	 if(index==2) index=0;
		        	 if(index==3) index=5;
		        	 for(int y=0;y<table.getItemCount();y++){
		        		 if(table.getItem(y).getData("distance")==null) item[y][0]=0;
		        		 else item[y][0]=table.getItem(y).getData("distance");
		        		 item[y][1]=table.getItem(y).getData("id");
		        		 for(int x=2;x<item[0].length;x++){
		        			  item[y][x]=table.getItem(y).getText(x-2);
		        		 }
		        	 }
		        	 sortTable s=new sortTable();
		        	 if(table.getSortDirection()==SWT.UP){
		        		  item=s.mergeSort(item, index, SWT.DOWN);
		        		  table.setSortDirection(SWT.DOWN);
		        	 }else{
		        		 item=s.mergeSort(item, index, SWT.UP);
		        		 table.setSortDirection(SWT.UP);
		        	 }
		        	 //table.setSortColumn(column);
		        	 table.setRedraw(false);
		        	 //for(int y=0;y<item.length;y++) System.out.println("y:"+y+" -> "+item[y][0]);
		        	 for(int y=0;y<item.length;y++){
		        		 table.getItem(y).setData("distance",item[y][0]);
		        		 table.getItem(y).setData("id",item[y][1]);
		        		 for(int x=2;x<item[0].length;x++) table.getItem(y).setText(x-2,item[y][x].toString());
		        	 }
		        	 item=null;
		        	 System.gc();
		        	 table.setRedraw(true);
		         }};
	    
	    
	   paintListener = new Listener(){
	          public void handleEvent(Event event) {
	            switch (event.type) {
	            case SWT.MeasureItem: {
	              TableItem item = (TableItem) event.item;
	              String text = getText(item, event.index);
	              Point size = event.gc.textExtent(text);
	              //event.width = size.x;
	              event.height = Math.max(event.height, size.y);
	              //event.height = Math.max(event.gc.textExtent("a").y, size.y);
	              break;
	            }
	            case SWT.PaintItem: {
	              TableItem item = (TableItem) event.item;
	              String text = getText(item, event.index);
	              Point size = event.gc.textExtent(text);
	              int offsetY=0;
	              int offsetX;
	             /* if(event.index<5){
	            	  offsetY=Math.max(0, ((event.height - size.y) / 2));
	              }else{
	            	  offsetY=0;}*/
	              if(item.getParent().getColumn(event.index).getAlignment()==SWT.CENTER){
	            	  offsetX=Math.max(0, (item.getParent().getColumn(event.index).getWidth() - size.x) / 2);
	              }else{offsetX=0;}
	              event.gc.drawText(text, event.x+offsetX, event.y + offsetY, true);
	              break;
	            }
	            case SWT.EraseItem: {
	            		 event.detail &= ~SWT.FOREGROUND;
	              break;
	            }
	            }//switch
	          }//listener
		};
	    

		 search.addListener(SWT.Selection, new Listener(){
	       	 public void handleEvent(Event event) {
	       		 if(searchMirna.getText().length()>0){
	       			 ConcurrentHashMap<Integer,Object> temp=new ConcurrentHashMap<Integer,Object>();
	       			 for(Integer key:mirna.keySet()){
	       				 if(f.getMirnaName(key).contains(searchMirna.getText())){
	       					 temp.put(key, mirna.get(key));
	       				 }
	       			 }
	       			reload(table, paintListener, temp, infoOpzioni, f, primo);
	       			temp=null;
	       			System.gc();
	       		 }else reload(table, paintListener, mirna, infoOpzioni, f, primo);
	       	 }});
		
		filter.addListener(SWT.Selection, new Listener(){
	    	  public void handleEvent(Event e) {
	    		  reload(table, paintListener, mirna, infoOpzioni, f, primo);
	    	  }
	    });
		
		 table.addListener(SWT.MouseDoubleClick, new Listener() {
		      public void handleEvent(Event e) {
		    	  if(table.getSelectionCount()>0){
		    		   pannello p=new pannello();
		    		   p.visualizza(table.getSelection()[0], (Object[][])mirna.get(Integer.parseInt(table.getSelection()[0].getData("id").toString())), f);
		    	  }
		    	
		      }});
		 
		 table.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event e) {
		        g.valore=table.getSelection();
		        g.valore=getItem();
		      }
		    });
		
		/*
		 * LAYOUT
		 */

		GridLayout grid=new GridLayout(1,true);
		content.setLayout(grid);
		primo.setLayoutData(new GridData(SWT.FILL, SWT.UP, true, false, 1 , 1));
		secondo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1 , 1));
		terzo.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1 , 1));
		
		primo.setLayout(new GridLayout(3,true));
		primoSx.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1 , 1));
		primoCen.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 1 , 1));
		primoDx.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 1 , 1));
		primoSx.setLayout(grid);
		consensus.setLayoutData(new GridData(SWT.FILL, SWT.UP, true, true, 1 , 1));
		found.setLayoutData(new GridData(SWT.FILL, SWT.UP, true, true, 1 , 1));
		number.setLayoutData(new GridData(SWT.FILL, SWT.UP, true, true, 1 , 1));
		esec.setLayoutData(new GridData(SWT.FILL, SWT.UP, true, true, 1 , 1));
		primoDx.setLayout(new GridLayout(2,false));
		searchLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1 , 1));
		GridData gData=new GridData();
		gData.minimumWidth=150;
		gData.grabExcessHorizontalSpace=true;
		gData.horizontalAlignment=SWT.FILL;
		gData.verticalAlignment=SWT.CENTER;
		searchMirna.setLayoutData(gData);
		search.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2 , 1));
		
		primoCen.setLayout(new GridLayout(2,true));
		info.setLayoutData(new GridData(SWT.LEFT, SWT.UP, true, true, 2, 1));
		upLbl.setLayoutData(new GridData(SWT.RIGHT, SWT.UP, true, true, 1, 1));
		up.setLayoutData(new GridData(SWT.FILL, SWT.UP, true, true, 1, 1));
		dwLbl.setLayoutData(new GridData(SWT.RIGHT, SWT.UP, true, true, 1, 1));
		down.setLayoutData(new GridData(SWT.FILL, SWT.UP, true, true, 1, 1));
		filter.setLayoutData(new GridData(SWT.FILL, SWT.UP, true, true, 2, 1));
		
		//GridLayout down_grid=new GridLayout(1,true);
		secondo.setLayout(grid);
		table_composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		//table_composite.setLayout(down_grid);
		//table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		terzo.setLayout(new GridLayout(2,true));
		
		
		
		 TableColumnLayout layout = new TableColumnLayout();
	     table_composite.setLayout( layout );
	     for (int i = 0; i < titles.length; i++) {
		          TableColumn column = new TableColumn(table, SWT.NONE);
		          switch(i){
		          case 0:   column.setAlignment(SWT.LEFT);
		          			layout.setColumnData( column, new ColumnWeightData( 10 ) );
		          			break;
		          case 1:   column.setAlignment(SWT.CENTER);
		          			layout.setColumnData( column, new ColumnWeightData( 20 ) );
		          			break;
		          case 2:   column.setAlignment(SWT.CENTER);
		          			layout.setColumnData( column, new ColumnWeightData( 10 ) );
		          			column.addListener(SWT.Selection, sortListener); 
							break;
		          case 3:   column.setAlignment(SWT.CENTER);
	    					layout.setColumnData( column, new ColumnWeightData( 4 ) );
	    					column.addListener(SWT.Selection, sortListener); 
	    					break;
		          case 4:   column.setAlignment(SWT.CENTER);
	    					layout.setColumnData( column, new ColumnWeightData( 10 ) );
	    					break;
		          case 5:   column.setAlignment(SWT.LEFT);
							layout.setColumnData( column, new ColumnWeightData( 45 ) );
							break;
		          }
		          column.setText(titles[i]);
		        }
	     table.setSortDirection(SWT.DOWN);
	     
			if(mirna!=null && !mirna.isEmpty()){
				reload(table, paintListener, mirna, infoOpzioni, f, primo);
				g.valore=table.getItems();
			}
		
		return content;
	}
	
public static void update_number_consensus(final int num,final int tot){
	Display.getCurrent().syncExec(new Runnable() {
	    public void run() { 
	    	results.number.setText("Total number of occurrence: "+num+" on "+tot);
	    }});
}	

public static void update_number_mirna(final int num,final int tot){
	Display.getCurrent().syncExec(new Runnable() {
	    public void run() { 
	    	results.number.setText("Total number of occurrence: "+num+" on "+tot);
	    }});
}	
	
public static String set_UpperCase_Mutation(String consensus,Boolean straight){
	/*
	 * straight=true -> consensus
	 * straight=false-> reverse complementary
	 */
	StringBuilder elemento;
	char[] ch;
	//System.out.println("elemento: "+consensus);
	if(straight){
	//consensus
		//consensus=consensus;
		for(int y=0;y<consensus.length();y++){
			//controllo posizione dove può esserci mutazione
			if(option.get_mutation_element(y)){
				//elemento=option.get_consensus_element(y,false);
				//System.out.println("-->"+elemento);
				if(option.get_consensus_element(y,false).indexOf(consensus.charAt(y))==-1){
					//se non è presente il carattere, allora è mutato
					//ch=consensus.toCharArray();
					//ch[y]=Character.toUpperCase(consensus.charAt(y));
					//consensus=new String(ch);
					elemento=new StringBuilder(consensus);
					elemento.setCharAt(y, Character.toUpperCase(consensus.charAt(y)));
					consensus=elemento.toString();
					//System.out.println("->"+consensus);
				}
			}
		}
		ch=null;
		System.gc();
	}else {
		//consensus inversa complementare
		//consensus=consensus.substring(1);
		for(int y=0;y<consensus.length();y++){
			if(option.get_mutation_element(consensus.length()-1-y)){
				//elemento=option.get_consensus_element(consensus.length()-1-y,true);
				if(option.get_consensus_element(consensus.length()-1-y,true).indexOf(consensus.charAt(y))==-1){
					//System.out.println("-->"+elemento);
					//ch=consensus.toCharArray();
					//ch[y]=Character.toUpperCase(consensus.charAt(y));
					//consensus=new String(ch);
					elemento=new StringBuilder(consensus);
					elemento.setCharAt(y, Character.toUpperCase(consensus.charAt(y)));
					consensus=elemento.toString();
					//System.out.println("->"+consensus);
				}
			}
		}
		
	}
	
	
	return consensus;
}	
	

public static String get_hit_fromTSS(int posizione,
								int id, //id mirna
 								int Uprelative){
	int Tssdist=0;
	if(f.getMirnaTSS(id)!= null){
		Tssdist=f.getMirnaDistanceFromTSS(id);
	}else{
		return " ";
	}
	int temp;
	
	Uprelative=Tssdist-Math.abs( Uprelative );
	temp=(Uprelative+posizione);
	//System.out.println(temp);
	return (temp+" ");
}


public static String get_hit(int posizione,int mirnaLen,int Uprelative,int scelta){
	
	String hit=null;
	switch(scelta){
	case 1:
		Uprelative=0-Uprelative;
		int temp;
		temp=Uprelative+posizione;
		if(temp>=0){
			if(temp-mirnaLen<0){
				//la posizione di hit è dentro il mirna
				hit="<"+temp+">";
			}else{
				//la posizione di hit è oltre la fine del mirna
				hit=""+(temp-mirnaLen);
			}
		}else{
			//la posizione di hit è nel promoter
			hit=""+temp;
		}
		break;
	case 2:
		hit=""+(posizione-Uprelative);
		break;
	}
	////System.out.println(posizione+" -> "+hit);
	return (hit+" ");
}

public static void async_Update(final ConcurrentHashMap<Integer,Object> mirna,final Object[] infoOpzioni,final long time){
	Display.getDefault().syncExec(new Runnable() {
	    public void run() {
	    	if(table_composite!=null){
	    		results.esec.setText("Execution time: "+results.exec_time(time));
	    		for(int y=0;y<table_composite.getChildren().length;y++){
	    			if(table_composite.getChildren()[y] instanceof org.eclipse.swt.widgets.Table){
	    				results.reload((Table)table_composite.getChildren()[y], 
	    								paintListener, 
	    								mirna, 
	    								infoOpzioni, 
	    								f, 
	    								primo);
	    					
	    			}
	    		}
	    	}
	    	
	    	
	    }});
	
}

	
public static void reload(Table table,Listener paintListener,ConcurrentHashMap<Integer,Object> mirna,Object[] infoOpzioni,funzioni f,Composite primo){				
	table.removeListener(SWT.MeasureItem, paintListener);
	table.removeListener(SWT.PaintItem, paintListener);
	table.removeListener(SWT.EraseItem, paintListener);
	table.removeAll();
	Object[] data=new Object[4];
	//for(int y=0;y<4;y++) data[y]=((Object[])(infoOpzioni[1]))[y];
	System.arraycopy((Object[])(infoOpzioni[1]), 0, data, 0, 4);
	Object[] locate;
	String l,occur,occurTSS;
	int tot=0;
	int visible=0;
	Set<Integer>keySet=mirna.keySet();
	int scelta=Integer.parseInt(data[2].toString());
	int numero_occorrenze=0;
	int m=0;
	int s=mirna.size();
	for(Integer key:keySet){
	  if(mirna.get(key)!=null){
		//System.out.println("Riga "+(++m)+" di "+s);
		TableItem item=new TableItem(table,SWT.NONE);
		item.setText(0, f.getMirnaName(key));
		l=f.getMirnaTSS(key)+" ("+f.getMirnaDistanceFromTSS(key)+")";
		//System.out.println("item "+item.getParent().getColumnCount());
		if(l.startsWith("null")){
			item.setText(2, "");
		} else{
			item.setText(2, l);
		}
		locate=f.getLocation(key);
		if(locate==null) continue;
		//for(int y=0;y<data.length;y++) if(data[y]!=null)System.out.println("data: "+y+" "+data[y].toString());
		l="Chr"+locate[2].toString()+": ";
		//System.out.println("scelta "+scelta);
		//for(int y=0;y<locate.length;y++) if(locate[y]!=null)System.out.println("locate: "+y+" "+locate[y].toString());
		
		switch(scelta){
		case 1://cerco intorno al mirna
			if(locate[3].toString().equals("+")){
				if(data[0]!=null) {
					l+=Integer.parseInt(locate[0].toString())-Integer.parseInt(data[0].toString())+" - ";
				}else{
					l+=Integer.parseInt(locate[0].toString())+" - ";
				};
				if(data[1]!=null){
					l+=""+(Integer.parseInt(locate[0].toString())+Integer.parseInt(locate[4].toString())+Integer.parseInt(data[1].toString()));
				}
				else{
					l+=""+(Integer.parseInt(locate[0].toString())+Integer.parseInt(locate[4].toString()));
				};
			}else{
				if(data[1]!=null){
					l+=Integer.parseInt(locate[0].toString())-Integer.parseInt(locate[4].toString())-Integer.parseInt(data[1].toString())+" - ";
				}else l+=Integer.parseInt(locate[0].toString())-Integer.parseInt(locate[4].toString())+" - ";
				if(data[0]!=null){
					l+=Integer.parseInt(locate[0].toString())+Integer.parseInt(data[0].toString());
				}else l+=Integer.parseInt(locate[0].toString());
			}
			
			break;
		case 2:
			//cerco intorno al tss
			if(locate[1]==null) continue; //se esiste un tss==null
			if(locate[3].toString().equals("+")){
				if(data[0]!=null) {
					l+=Integer.parseInt(locate[1].toString())-Math.abs(Integer.parseInt(data[0].toString()))+" - ";
				}else{
					l+=(Integer.parseInt(locate[0].toString())-1)+" - ";
				};
				if(data[1]!=null){
					l+=""+(Integer.parseInt(locate[1].toString())+Math.abs(Integer.parseInt(data[1].toString())));
				}
				else{
					l+=""+(Integer.parseInt(locate[0].toString())+1);
				};
			}else{
				
				if(data[1]!=null){
					l+=""+(Integer.parseInt(locate[1].toString())-Math.abs( Integer.parseInt(data[1].toString())) )+" - ";
				}else{
					l+=""+(Integer.parseInt(locate[0].toString())-1)+" - ";
				};
				if(data[0]!=null) {
					l+=Integer.parseInt(locate[1].toString())+ Math.abs( Integer.parseInt(data[0].toString()) );
				}else{
					l+=(Integer.parseInt(locate[0].toString())+1);
				};
			} 
			break;
		}
		

		
		l+=" ["+locate[3]+"]";
		item.setText(1,l);
		l="";
		occur="";
		occurTSS="";
		int c=0;
		int downLimit=0;
		int upLimit=0;
		//System.out.println(relative);
		if(((Text)(((Composite)primo.getChildren()[1])).getChildren()[2]).getText().length()>0){
			upLimit=Integer.parseInt(data[0].toString())-
								Integer.parseInt(((Text)(((Composite)primo.getChildren()[1])).getChildren()[2]).getText());
			if(upLimit<=0){
				//è la prima visualizzazione e li devo vedere tutte le hit
				upLimit=0;
			}
			//upLimit=(-1)*Math.abs(Integer.parseInt(((Text)(((Composite)primo.getChildren()[1])).getChildren()[2]).getText()));
		}else ((Text)(((Composite)primo.getChildren()[1])).getChildren()[2]).setText("0");
		if(((Text)(((Composite)primo.getChildren()[1])).getChildren()[4]).getText().length()>0){
			downLimit=Integer.parseInt(data[0].toString())+Integer.parseInt(locate[4].toString())+
					Integer.parseInt(((Text)(((Composite)primo.getChildren()[1])).getChildren()[4]).getText());
			//downLimit=Math.abs(Integer.parseInt(((Text)(((Composite)primo.getChildren()[1])).getChildren()[4]).getText()));
		}else ((Text)(((Composite)primo.getChildren()[1])).getChildren()[4]).setText("0");
		 
		//System.out.println(mirna);
		if(((HashMap<String,Vector<Integer>>)mirna.get(key))!=null){
			for(String temp:((HashMap<String,Vector<Integer>>)mirna.get(key)).keySet()){
				(((Label)((Composite)primo.getChildren()[0]).getChildren()[0])).setText("Consensus matrix length: "+((HashMap<String,Vector<Integer>>)mirna.get(key)).size());
				if(((Vector<Integer>)((HashMap<String,Vector<Integer>>)mirna.get(key)).get(temp))!=null){
					c+=((Vector<Integer>)((HashMap<String,Vector<Integer>>)mirna.get(key)).get(temp)).size(); //hit
					l+=set_UpperCase_Mutation(temp,((HashMap<String,Boolean>)infoOpzioni[0]).get(temp))+System.getProperty("line.separator"); //consensus
					for(int y=0;y<((Vector<Integer>)((HashMap<String,Vector<Integer>>)mirna.get(key)).get(temp)).size();y++){
						if(true){
						//if(((Vector<Integer>)((HashMap<String,Vector<Integer>>)mirna.get(key)).get(temp)).elementAt(y)>=upLimit &&
						//		((Vector<Integer>)((HashMap<String,Vector<Integer>>)mirna.get(key)).get(temp)).elementAt(y)<=downLimit){
							
											occur+=get_hit(((Vector<Integer>)((HashMap<String,Vector<Integer>>)mirna.get(key)).get(temp)).elementAt(y),//posizione hit
															Integer.parseInt(locate[4].toString()), //mirna length
															Math.abs( Integer.parseInt(data[0].toString()) ), //valore relativo di inizio ricerca
															scelta); //controllo se è attorno al TSS o no
											switch(scelta){
											case 1:
												occurTSS+=get_hit_fromTSS(((Vector<Integer>)((HashMap<String,Vector<Integer>>)mirna.get(key)).get(temp)).elementAt(y),//posizione hit, 
														key, //id del mirna
														Integer.parseInt(data[0].toString()));//valore relativo di inizio ricerca
												break;
											case 2:
												occurTSS=occur;
												break;
											}
											numero_occorrenze++;
									}//fine if uplimit e downlimit
					
							}//fine for per la stampa delle hit
					//System.out.println(occurTSS);
					occurTSS+=System.getProperty("line.separator");
					occur+=System.getProperty("line.separator");
				}
				
			}
		}
		/*for(int y=0;y<((HashMap<String,Vector<Integer>>)mirna.get(key)).size();y++){
			//System.out.println("elemento: "+((Object[][])mirna.get(key))[y][0].toString()+" at "+y);
			if(((Vector<Integer>)((Object[][])mirna.get(key))[y][1])!=null && 
					((Vector<Integer>)((Object[][])mirna.get(key))[y][1]).size()>0){
				    (((Label)((Composite)primo.getChildren()[0]).getChildren()[0])).setText("Consensus matrix length: "+((Object[][])mirna.get(key)).length);
					c+=((Vector<Integer>)((Object[][])mirna.get(key))[y][1]).size(); //hit
					l+=set_UpperCase_Mutation(((Object[][])mirna.get(key))[y][0].toString())+System.getProperty("line.separator"); //consensus
					for(int o=0;o<((Vector<Integer>)((Object[][])mirna.get(key))[y][1]).size();o++){
						if((((Vector<Integer>)((Object[][])mirna.get(key))[y][1]).elementAt(o))>=upLimit &&
								(((Vector<Integer>)((Object[][])mirna.get(key))[y][1]).elementAt(o))<=downLimit){
										occur+=get_hit((((Vector<Integer>)((Object[][])mirna.get(key))[y][1]).elementAt(o)),//posizione mirna
														Integer.parseInt(locate[4].toString()), //mirna length
															Integer.parseInt(data[0].toString())); //valore relativo di inizio ricerca
										numero_occorrenze++;
						}
												}
										occur+=System.getProperty("line.separator");
							
				}
		}*/
		tot+=c;
		item.setText(3, ""+c);
		item.setText(4, ""+l);
		item.setText(5, ""+occur);
		item.setData("distance",f.getDistanceFromPrecursor(key));
		//System.out.println(occurTSS);
		item.setData("occurTSS",occurTSS);
		item.setData("host",f.getMirnaHost(key));
		item.setData("id",key);
		
		 (((Label)((Composite)primo.getChildren()[0]).getChildren()[1])).setText("pre-miRNA found: "+table.getItemCount()+" on "+mirna.size());
	}
	}
	update_number_consensus(numero_occorrenze,tot);
	 //(((Label)((Composite)primo.getChildren()[0]).getChildren()[2])).setText("Total number of occurrence: "+visible+" on "+tot);
	 table.addListener(SWT.MeasureItem, paintListener);
     table.addListener(SWT.PaintItem, paintListener);
     table.addListener(SWT.EraseItem, paintListener);
     g.valore=table.getItems();
     primo.getParent().layout();
     }
	
	
	  String getText(TableItem item, int column) {
          String text = item.getText(column);
          return text;
        }
      

	TableItem[] getItem() {
		//System.out.println("getValore: "+g.valore.length);
    return g.valore;
  }
}

class getValore { 
	 TableItem[] valore;
	 public TableItem[] get() {
	 return this.valore = valore;
	 }
	 }

