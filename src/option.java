import static java.lang.Math.pow;

import java.awt.TextArea;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;









//import org.eclipse.core.runtime.IStatus;
//import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;


public class option {

	public ScrolledComposite sc;
	public static Composite duesx,duedx;
	static dbAuth a=new dbAuth();
	static Object[] auth=a.getAuth();
	static funzioni f=new funzioni(auth);
	public static ProgressBar pb,pbfast;
	public static int cc,cm;
	public static Label elabora,match;
	public static Vector<String> v; 
	public static Vector<Boolean> pro; 
	
	public Composite option(Composite content){
		content.setBackground(content.getDisplay().getSystemColor(SWT.COLOR_DARK_CYAN));
		
		Composite primo=new Composite(content,SWT.NONE);
		final Label contaLabel=new Label(primo,SWT.NONE);
		contaLabel.setText("Set consensus matrix length"+System.getProperty("line.separator")+"(from 4 to 30 elements)");
		final Spinner conta=new Spinner(primo, SWT.BORDER);
		conta.setSelection(14);
		conta.setMinimum(4);
		conta.setMaximum(30);
		final Label contaScale=new Label(primo,SWT.NONE);
		contaScale.setText("Set mutation: 0 ");
		final Scale scale=new Scale(primo,SWT.HORIZONTAL);
		scale.setMinimum(0);
		scale.setMaximum(9);
		scale.setIncrement(1);
		
		sc = new ScrolledComposite(primo,SWT.H_SCROLL|SWT.BORDER);
		final Composite temp=new Composite(sc,SWT.NONE);
		sc.setContent(temp);
		disegna(temp,14);
		temp.setSize(temp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		int promo=f.getAbsPromoterLength();
		int downs=f.getAbsDownstreamLength();
		Composite secondo=new Composite(content,SWT.NONE);
		duesx=new Composite(secondo,SWT.NONE);
		Image vuoto = new Image(duesx.getDisplay(), System.getProperty("user.dir")+"/library/vuoto.png");
		final Label info=new Label(duesx,SWT.WRAP);
		info.setText("Nucleotide sequence length: -"+promo+" bps ; +"+downs+" bps"+System.getProperty("line.separator")+" ");
		Label upstream=new Label(duesx,SWT.NONE);
		upstream.setText("Promoter/upstream: ");
		upstream.setAlignment(SWT.RIGHT);
		Text up=new Text(duesx,SWT.BORDER);
		up.setData("name","up");
		up.setData("length", promo);
		final Label void_upstream=new Label(duesx,SWT.NONE);
		void_upstream.setImage(vuoto);
		Label downstream=new Label(duesx,SWT.NONE);
		downstream.setText("Downstream: ");
		downstream.setAlignment(SWT.RIGHT);
		Text down=new Text(duesx,SWT.BORDER);
		down.setData("name","down");
		down.setData("length", downs);
		final Label void_downstream=new Label(duesx,SWT.NONE);
		void_downstream.setImage(vuoto);
		Combo scelta=new Combo(duesx,SWT.BORDER);
		scelta.add("Select search...");
		scelta.add("Search near pre-miRNA");
		scelta.add("Search near TSS");
		scelta.add("Custom search");
		scelta.select(0);
		final Label void_search=new Label(duesx,SWT.NONE);
		void_search.setImage(vuoto);
		Label select_mirna=new Label(duesx,SWT.NONE);
		select_mirna.setText("Select pre-miRNA: ");
		select_mirna.setAlignment(SWT.RIGHT);
		Text mirna=new Text(duesx,SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		mirna.setData("name","mirnaList");
		mirna.setData("length",-1);
		Label infoSearch=new Label(duesx,SWT.WRAP);
		infoSearch.setText("Searching selected pre-miRNA use " +
				"'%' to matches any number of characters, even zero characters or  " +
				"'_' to matches exactly one character");
		
		duedx=new Composite(secondo,SWT.NONE);
		
		
		final Label consensusList=new Label(duedx,SWT.NONE);
		consensusList.setData("nome","consensusList");
		consensusList.setText("Consensus list length: 0");
		final Label expand=new Label(duedx,SWT.NONE);
		expand.setData("nome","expandMutation");
		expand.setText("Mutated consensus list lenght: 0");
		final Label alghoritm=new Label(duedx,SWT.NONE);
		alghoritm.setData("nome","algoritmo");
		alghoritm.setText("Select pattern matching alghoritm: ");
		Group gruppo=new Group(duedx,SWT.SHADOW_IN);
		Button naive=new Button(gruppo,SWT.RADIO);
		naive.setSelection(true);
		naive.setText("Brute force");
		Button BM=new Button(gruppo,SWT.RADIO);
		BM.setText("Boyer-Moore");
		Button db=new Button(gruppo,SWT.RADIO);
		db.setText("MyIsam FullText");
		//Button KM=new Button(gruppo,SWT.RADIO);
		//KM.setText("Knut-Morris");
		
		Composite terzo=new Composite(content,SWT.NONE);
		elabora=new Label(terzo,SWT.NONE);
		elabora.setText("Elaborating miRNA: ");
		match=new Label(terzo,SWT.NONE);
		match.setText("Matching hit: 0");
		match.setData("hit",0);
		pb=new ProgressBar(terzo, SWT.SMOOTH);
		pbfast=new ProgressBar(terzo, SWT.SMOOTH);
		
		
		
		
		/*
		 * LISTENER
		 */
		conta.addListener(SWT.Selection,new Listener() {
		      public void handleEvent(Event e) {
		          int selection = conta.getSelection();
		          disegna(temp,selection);
		        }
		      });
		scale.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event event) {
		        contaScale.setText("Set mutation: " + scale.getSelection()+" ");
		      }
		    });
		Listener ma=new Listener(){
			public void handleEvent(Event event) {
				cc=combinazioni(temp);
				cm=combinazioniMutazione(temp);
				  ((Label)duedx.getChildren()[0]).setText("Consensus list length: "+cc);
		    	  ((Label)duedx.getChildren()[1]).setText("Mutated consensus list lenght: "+cm);
		    	
		      }};
		     
		scale.addListener(SWT.Selection, ma);
		
		
		/*
		 * LAYOUT
		 */
		GridLayout grid=new GridLayout(1,false);
		content.setLayout(grid);
		primo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1 , 1));
		secondo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1 , 1));
		terzo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1 , 1));
		
		GridLayout grid_primo=new GridLayout(4,true);
		primo.setLayout(grid_primo);
		contaLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1 , 1));
		conta.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1 , 1));
		contaScale.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1 , 1));
		scale.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1 , 1));
		sc.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 4 , 1));
		
		temp.setLayout(new GridLayout(temp.getChildren().length,true));
		
		secondo.setLayout(new GridLayout(2,true));
		duesx.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1 , 1));
		duedx.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1 , 1));
		
		duesx.setLayout(new GridLayout(4,false));
		GridData grid_data=new GridData();
		grid_data.horizontalAlignment=SWT.FILL;
		grid_data.verticalAlignment=SWT.FILL;
		grid_data.widthHint=200;
		GridData grid_icon=new GridData();
		grid_icon.horizontalAlignment=SWT.FILL;
		grid_icon.verticalAlignment=SWT.FILL;
		grid_icon.widthHint=24;
		info.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 4 , 1));
		upstream.setLayoutData(grid_data);
		up.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 2 , 1));
		void_upstream.setLayoutData(grid_icon);
		downstream.setLayoutData(grid_data);
		down.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2 , 1));
		void_downstream.setLayoutData(grid_icon);
		scelta.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 3 , 1));
		void_search.setLayoutData(grid_icon);
		select_mirna.setLayoutData(grid_data);
		grid_data=new GridData();
		grid_data.horizontalAlignment=SWT.FILL;
		grid_data.verticalAlignment=SWT.CENTER;
		grid_data.horizontalSpan=3;
		grid_data.heightHint=200;
		mirna.setLayoutData(grid_data);
		infoSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 4 , 1));
		
		GridLayout grid3=new GridLayout(2,false);
		terzo.setLayout(grid3);
		duedx.setLayout(new GridLayout(1,false));
		consensusList.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1 , 1));
		expand.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1 , 1));
		alghoritm.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1 , 1));
		gruppo.setLayout(new RowLayout(SWT.VERTICAL));
		
		match.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1 , 1));
		elabora.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1 , 1));
		pb.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2 , 1));
		pbfast.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2 , 1));
		return content;
	}
	
	public static int getConsensusMax(){
		int x=0;
		for(int y=0;y<duedx.getChildren().length;y++){
			if(duedx.getChildren()[y] instanceof org.eclipse.swt.widgets.Label){
				if(((Label)duedx.getChildren()[y]).getData("nome").equals("consensusList")){
					x=Integer.parseInt(((Label)duedx.getChildren()[y]).getText().substring(((Label)duedx.getChildren()[y]).getText().indexOf(": ")+2));
					break;
				}
			}
		}
		//((Label)duedx.getChildren()[0]).setText("Consensus list length: "+cc);
		return x;
	}
	
	public static void updatebarLen(final int len,final boolean fast){
		Display.getDefault().syncExec(new Runnable() {
		    public void run() {
		    	if(fast)option.pbfast.setMaximum(len); //sotto
				else option.pb.setMaximum(len);			//sopra
		    }});
	
		//System.out.println("barLength: "+len);
	}
	
	public static void resetLen(final boolean fast){
		Display.getDefault().syncExec(new Runnable() {
		    public void run() {
		    	if(fast)option.pbfast.setSelection(0);
				else option.pb.setSelection(0);;
		    }});
	}
	
	public static void setProgress(final boolean fast,final int hit){
		Display.getDefault().syncExec(new Runnable() {
		    public void run() {
		    	if(fast) option.pbfast.setSelection(pbfast.getSelection()+hit);
		    	else option.pb.setSelection(pb.getSelection()+hit);
		    }});
	}
	public static void setElaboraMirna(final String mirna){
		Display.getDefault().syncExec(new Runnable() {
		    public void run() {
		    	option.elabora.setText("Elaborate: "+mirna);
		    }});
	}
	
	public static void setMatchingCount(final int match){
		Display.getDefault().syncExec(new Runnable() {
		    public void run() {
		    	if(match==-1){
		    		//reset
		    		option.match.setText("Matching hit: 0");
			    	option.match.setData("hit",0);
		    	}else{
		    		int y=Integer.parseInt(option.match.getData("hit").toString())+match;
		    		option.match.setText("Matching hit: "+y);
		    		option.match.setData("hit",y);
		    	}
		    	
		    }});
	}
	
	public Integer getSequenceLength(Composite temp){
		int tot=0;
		for(int y=0;y<temp.getChildren().length;y++){
			if(((Text)((Composite)temp.getChildren()[y]).getChildren()[0]).getText().length()>0){
				tot++;
			}
		}
		return tot;
	}
	
	
	
	public void disegna(final Composite temp,int n){
		
		int t=temp.getChildren().length;
		FocusListener focusListener = new FocusListener(){
		      public void focusGained(FocusEvent event) {
		    	  ((Label)duedx.getChildren()[0]).setText("Consensus list length: "+combinazioni(temp));
		    	  ((Label)duedx.getChildren()[1]).setText("Mutated consensus list lenght: "+combinazioniMutazione(temp));
		      }
		      public void focusLost(FocusEvent event) {
		    	  ((Label)duedx.getChildren()[0]).setText("Consensus list length: "+combinazioni(temp));
		    	  ((Label)duedx.getChildren()[1]).setText("Mutated consensus list lenght: "+combinazioniMutazione(temp));
		        }
		      };
	  Listener ma=new Listener(){
					public void handleEvent(Event event) {
						  ((Label)duedx.getChildren()[0]).setText("Consensus list length: "+combinazioni(temp));
				    	  ((Label)duedx.getChildren()[1]).setText("Mutated consensus list lenght: "+combinazioniMutazione(temp));
				      }};
				      
	VerifyListener verifica= new VerifyListener() {
					      public void verifyText(VerifyEvent event) {
					    	  event.doit=false;
					    	  char c=event.character;
					    	  String s=((Text)event.widget).getText().trim();
					    	  Boolean ctr=true;
					    	  if (event.keyCode==8){event.doit=true;return;}
					    	  if(c=='a' || c=='t' || c=='g' || c=='c' ||
					    			  c=='A' || c=='T' || c=='G' || c=='C' ||
					    			  c=='n' || c=='N' || c=='r' || c=='R' ||
					    			  c=='S' || c=='s' || c=='H' || c=='h' ||
					    			  c=='W' || c=='w' || c=='Y' || c=='y' ||
					    			  c=='M' || c=='m' || c=='K' || c=='k' ||
					    			  c=='B' || c=='b' || c=='V' || c=='v' ||
					    			  c=='D' || c=='d' ){
					    		  //controllo che il carattere non sia gi� stato messo
					    		  for(int i=0;i<s.length();i++){
					    		  if(c==s.charAt(i)){
					    			  ctr=false;
					    			  break;
					    		  				}
					    		  	}
					    	  }//fine if
					    	  else{ctr=false;}
					    	  event.doit=ctr;
					      }};
		
		if(t-n<0){
			//aggiungo elementi
			for(int y=t;y<n;y++){
				Composite nuovo=new Composite(temp,SWT.BORDER);
				Text txt=new Text(nuovo,SWT.BORDER);
				txt.addFocusListener(focusListener);
				txt.addVerifyListener(verifica);
				Label lbl=new Label(nuovo,SWT.NONE);
				lbl.setText(""+(y+1));
				lbl.setAlignment(SWT.CENTER);
				Button check=new Button(nuovo,SWT.CHECK);
				check.setText("No mutation");
				check.setAlignment(SWT.CENTER);
				nuovo.setLayout(new GridLayout(1,true));
				txt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1 , 1));
				lbl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1 , 1));
				check.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1 , 1));
				check.addListener(SWT.Selection,ma);
			}
			
		}else{
			//elimino elementi
			for(int y=n;y<temp.getChildren().length;y++){
				temp.getChildren()[y].dispose();
			}
		}
		
		temp.setLayout(new GridLayout(temp.getChildren().length,true));
		temp.setSize(temp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		temp.layout();
	}
	
	
	public Integer combinazioni(Composite temp){
		int t=temp.getChildren().length;
		int c=0;
		Text txt;
		for(int y=0;y<t;y++){
			txt=((Text)((Composite)temp.getChildren()[y]).getChildren()[0]);
			if(txt.getText().length()>0) 
				if(c!=0) c=c*txt.getText().length();
				else c=txt.getText().length();
		}
		txt=null;
		System.gc();
		return c;
	}

	static int fatt(int x) {
	    int i;
	    int f=1;
	    for(i=1; i<=x; i=i+1) {
	      f=f*i;
	    }
	    return f;
	  }
	
	public Integer combinazioniMutazione(Composite temp){
		int t=temp.getChildren().length;
		int c=0; //total element can mute
		int m=0; //mutation
		Integer tot=0;
		for(int y=0;y<temp.getParent().getParent().getChildren().length;y++){
			if(temp.getParent().getParent().getChildren()[y] instanceof org.eclipse.swt.widgets.Scale){
				m=((Scale)temp.getParent().getParent().getChildren()[y]).getSelection();
				break;
			}
		}
		
		for(int y=0;y<t;y++){
			if(((Text)((Composite)temp.getChildren()[y]).getChildren()[0]).getText().length()>0){
				if(!((Button)((Composite)temp.getChildren()[y]).getChildren()[2]).getSelection()){
					c++;
				}
			}
				
		}
		if(m>c){
			MessageDialog dialog = new MessageDialog(temp.getShell(), "Warning in workspace", new Image(Display.getCurrent(), System.getProperty("user.dir")+"/library/DNA24x.png"),
				    "Mutation exceed matrix consensus length."+System.getProperty("line.separator")+"Possible mutation will be set to "+c, 
				    MessageDialog.WARNING, 
				    new String[] { "Proceed" }, 0);
			dialog.open();
			m=c;
			for(int y=0;y<temp.getParent().getParent().getChildren().length;y++){
				
				if(temp.getParent().getParent().getChildren()[y] instanceof org.eclipse.swt.widgets.Label){
					if(((Label)temp.getParent().getParent().getChildren()[y]).getText().split(":")[0].toString().trim().equals("Set mutation")) 
						((Label)temp.getParent().getParent().getChildren()[y]).setText("Set mutation: "+c);
				}
				if(temp.getParent().getParent().getChildren()[y] instanceof org.eclipse.swt.widgets.Scale){
					((Scale)temp.getParent().getParent().getChildren()[y]).setSelection(c);
				}
			}
		}
		if(m!=0 && c!=0) tot=(int)(pow(4,m)*((fatt(c)/(fatt(m)*fatt(c-m)))));
		//System.out.println("M:"+m+" c:"+c+" -> "+tot);
		return tot;
	}
	
	
	public HashMap<String,Boolean> getCoreConsensus(int c,Vector<String> v,HashMap<String,Boolean> consensus){
		option.updatebarLen(6,true);
		Vector<String> lista=new Vector<String>();
		Vector<Integer> lunghezze=new Vector<Integer>();
		for(int i=0;i<v.size();i++){
			lunghezze.add(v.elementAt(i).length());
		}
		option.setProgress(true,1);
		Vector<Integer> combinazioni=new Vector<Integer>();
		for(int y=0;y<lunghezze.size();y++){
			combinazioni.add(0);
		}
		option.setProgress(true,1);
		Integer[] pos={0,0,0,0};
		for(int i=0;i<lunghezze.size();i++){
			pos[lunghezze.elementAt(i)-1]++;
		}
		option.setProgress(true,1);
		String str="";
		for(int k=0;k<c;k++){
			for(int a=0;a<v.size();a++){
				try{
				str=str+v.elementAt(a).charAt(combinazioni.elementAt(a));
				}catch(Exception e){
					str=str;
				}
			}
			for(int j=combinazioni.size()-1;j>=0;j--){
				if(combinazioni.elementAt(j)<lunghezze.elementAt(j)-1){
					combinazioni.set(j, combinazioni.elementAt(j)+1);
					break;
				}else{combinazioni.set(j, 0);}
			}
			lista.add(str);
			str="";
		}
		option.setProgress(true,1);
		combinazioni=null;
		lunghezze=null;
		if(lista.size()==0){
			pos=null;
			lista=null;
			System.gc();
			return null;
		}
		//HashMap<String,Boolean> consensus=new HashMap<String,Boolean>();//=new String[lista.size()*2];
		
		for(int y=0;y<(lista.size());y++){
			consensus.put(lista.get(y), true);//="0"+lista.get(y/2);
			consensus.put(f.reverse_complementary(lista.get(y)), false);//[y]="1"+f.reverse_complementary(lista.get((y-1)/2));
			
		}
		option.setProgress(true,1);
		//for(int y=0;y<consensus.length;y++) System.out.println("consensus at "+y+" -> "+consensus[y]);
		pos=null;
		lista=null;
		System.gc();
		return consensus;
	}
	
	
	public static String get_consensus_element(int y,boolean reverse){
		if(reverse){
			return f.reverse_complementary(v.elementAt(y));
		}else{
			return v.elementAt(y);
		}
	}
	
	public static Boolean get_mutation_element(int y){
		//System.out.println("--> "+y+" "+!pro.elementAt(y));
		return !pro.elementAt(y);
	}
	
	
	
	public HashMap<String,Boolean> getConsensus(Composite temp){
		//la string 1+consensus è la reverse_complementary della consensus
		setElaboraMirna("building matrix");
		int c=combinazioni(temp);
		int t=temp.getChildren().length;
		int m=0; //numero mutazioni
		String toPermute="";
		v=new Vector<String>();
		pro=new Vector<Boolean>();
		
		for(int y=0;y<temp.getParent().getParent().getChildren().length;y++){
			//System.out.println(temp.getParent().getParent().getChildren()[y].getClass().getCanonicalName());
			if(temp.getParent().getParent().getChildren()[y] instanceof org.eclipse.swt.widgets.Scale){
				m=((Scale)temp.getParent().getParent().getChildren()[y]).getSelection(); //set numero mutazioni
				break;
			}
		}
		
		for(int y=0;y<t;y++){
			if(((Text)((Composite)temp.getChildren()[y]).getChildren()[0]).getText().length()>0){
				v.add(((Text)((Composite)temp.getChildren()[y]).getChildren()[0]).getText().toLowerCase());
				pro.add(((Button)((Composite)temp.getChildren()[y]).getChildren()[2]).getSelection()); //chi pu� mutare e chi no
				if(!((Button)((Composite)temp.getChildren()[y]).getChildren()[2]).getSelection()){
					if(m>0){
						toPermute+="n";
						//mutation.add("n");//inserisco ANY oppure la lettera
						m--;
						}else toPermute+="x";
							//toPermute+=(transformNucleotide(((Text)((Composite)temp.getChildren()[y]).getChildren()[0]).getText().toLowerCase(), true));
				}
			}
		}
		//System.out.println(toPermute);
		//HashMap<String,Boolean> consensus=getCoreConsensus(c, v,consensus); //consensus principale (inserita, senza mutazioni)
		//for(int y=0;y<mutation.size();y++)System.out.println("mutation: "+mutation.elementAt(y));
		//if(toPermute.contains("n")){
			//String[] extendedConsensus;
			HashMap<String,Boolean> finalConsensus=new HashMap<String,Boolean>(); //hashmap di tutte le consensus che ho e che posso trovare
			//for(int y=0;y<consensus.length;y++) finalConsensus.put(consensus[y], true);
			//consensus=null;
			
			//for(int y=0;y<mutation.size();y++) toPermute+=mutation.elementAt(y);
			
			//Vector<String> perm=provaMutate("ccaat", m);
			//System.out.println("toPermute "+toPermute);
			//for(int k=0;k<perm.size();k++) System.out.println(k+" "+perm.elementAt(k));
			
			if(toPermute.contains("n")){
				Vector<String> mutation=new Vector<String>(); 
				Vector<String> perm=permutation(toPermute);
				option.resetLen(true);
				for(int p=0;p<perm.size();p++){
					//ciclo su tutte le permutazioni
					mutation.removeAllElements();
					m=0; //mi indica la posizione della mutazione
					t=0; //numero combinazioni
					for(int y=0;y<pro.size();y++){ //per ogni mutazione devo ricostruire la matrice consensus (stored in mutation)
						//System.out.println(p+" "+pro.elementAt(y)+" "+perm.elementAt(p)+" "+m);
						if(pro.elementAt(y)) mutation.add(v.elementAt(y));
						else{
							
							if(perm.elementAt(p).charAt(m)=='n') mutation.add("agct");
							else mutation.add(v.elementAt(y));
							m++;
						}
						if(t!=0) t=t*mutation.elementAt(y).length();
						else t=mutation.elementAt(y).length();
						}
					//for(int h=0;h<mutation.size();h++) System.out.print(mutation.elementAt(h)+" ");
					//System.out.println();
					finalConsensus=getCoreConsensus(t, mutation,finalConsensus); //creo array di stringhe della consensus con mutazioni
					//for(int y=0;y<extendedConsensus.length;y++) finalConsensus.put(extendedConsensus[y], true); //uso hash perchè copio gli indici uguali
					//option.setProgress();
				}
				
			}else{
				finalConsensus=getCoreConsensus(c, v,finalConsensus); //consensus principale (inserita, senza mutazioni)
			}
			
			
		//}
		
		System.gc();
		System.out.println("lunghezza consensus: "+finalConsensus.size());
		return finalConsensus;
	}
	

	
	public String transformNucleotide(String s,boolean nucleo2char){
		String supp="";
		if(nucleo2char){
			switch(s.length()){
			case 0:
			case 1:
				supp+=s;
				break;
			case 2:
				if(s.equals("at") || s.equals("ta")) supp+="w";
				if(s.equals("ac") || s.equals("ca")) supp+="m";
				if(s.equals("ag") || s.equals("ga")) supp+="r";
				if(s.equals("ct") || s.equals("tc")) supp+="y";
				if(s.equals("gt") || s.equals("tg")) supp+="k";
				if(s.equals("cg") || s.equals("gc")) supp+="s";
				break;
			case 3:
				if(s.equals("atc") || s.equals("cta")|| s.equals("tac")|| s.equals("tca")|| s.equals("act")|| s.equals("cat")) supp+="h";
				if(s.equals("gtc") || s.equals("ctg")|| s.equals("tgc")|| s.equals("tcg")|| s.equals("gct")|| s.equals("cgt")) supp+="b";
				if(s.equals("agc") || s.equals("cga")|| s.equals("gac")|| s.equals("gca")|| s.equals("acg")|| s.equals("cag")) supp+="v";
				if(s.equals("atg") || s.equals("gta")|| s.equals("tag")|| s.equals("tga")|| s.equals("agt")|| s.equals("gat")) supp+="d";
				break;
			case 4: 
				supp+="n";
				break;
				}
		}else{
			switch(s.charAt(0)){
			case 'a':
			case 'g':
			case 'c':
			case 't': supp+=s; break;
			case 'n': supp+="agct";break;
			case 'r': supp+="ga";break;
			case 'w': supp+="at";break;
			case 'y': supp+="ct";break;
			case 'm': supp+="ac";break;
			case 'k': supp+="gt";break;
			case 's': supp+="gc";break;
			case 'h': supp+="act";break;
			case 'b': supp+="gct";break;
			case 'v': supp+="agc";break;
			case 'd': supp+="agt";break;
			}
			Integer[] pos={0,0,0,0};
			for (int i=0;i<supp.length();i++){
				if(supp.charAt(i)=='a'){pos[0]++;}
				if(supp.charAt(i)=='g'){pos[1]++;}
				if(supp.charAt(i)=='c'){pos[2]++;}
				if(supp.charAt(i)=='t'){pos[3]++;}
			}
			if(pos[0]>1){supp=supp.replace("a", "");supp=supp+"a"; }
			if(pos[1]>1){supp=supp.replaceAll("g", "");supp=supp+"g";}
			if(pos[2]>1){supp=supp.replaceAll("c", "");supp=supp+"c";}
			if(pos[3]>1){supp=supp.replaceAll("t", "");supp=supp+"t";}
			pos=null;
			System.gc();
		}
		return supp;
	}
	

	
	 public  Vector<String> permutation(String str) { 
		   Vector<String> v1=new Vector<String>();
		   permutation("", str,v1); 
		   return v1;
		 }

		 private static void permutation(String prefix, String str,Vector<String> v1) {
		    int n = str.length();
		    if (n == 0){
		    	boolean write=true; //controllo permutazioni uguali
		    	for(int y=0;y<v1.size();y++) if(prefix.equals(v1.elementAt(y))) write=false;
		    	if(write) v1.add(prefix);
		    }
		    else {
		        for (int i = 0; i < n; i++)
		           permutation(prefix + str.charAt(i), str.substring(0, i) + str.substring(i+1, n),v1);
		    }
		}
	
	
	
	public boolean controlOption(Composite temp){
		int p=1;
		/*
		 * P=0 warning
		 * P=-1 errore
		 * P=1 go
		 */
		getAlghoritm(duedx);
		Image err = new Image(temp.getDisplay(), System.getProperty("user.dir")+"/library/error.png");
		Image warn = new Image(temp.getDisplay(), System.getProperty("user.dir")+"/library/warning.png");
		for(int y=0;y<temp.getChildren().length;y++){
			if(temp.getChildren()[y].getClass().getCanonicalName().toString().equals("org.eclipse.swt.widgets.Text") ){
				if(Integer.parseInt(((Text)temp.getChildren()[y]).getData("length").toString())!=-1 && ((Text)temp.getChildren()[y]).getText().length()>0)
					if(Integer.parseInt(((Text)temp.getChildren()[y]).getData("length").toString())
							<Integer.parseInt(((Text)temp.getChildren()[y]).getText())){
						((Label)temp.getChildren()[y+1]).setImage(warn);
						p=0;
					}
				}
			if(temp.getChildren()[y].getClass().getCanonicalName().toString().equals("org.eclipse.swt.widgets.Combo")){
				if(((Combo)temp.getChildren()[y]).getSelectionIndex()==0){
						((Label)temp.getChildren()[y+1]).setImage(err);
						/*Status status = new Status(IStatus.ERROR, "My Plug-in ID", 0,
								"Select searching area", null); 
						ErrorDialog.openError(Display.getCurrent().getActiveShell(),
					            "Error", null, status); */
						p=-1;
				}
			}
			}
		if(p==0){
			MessageDialog dialog = new MessageDialog(temp.getShell(), "Warning in workspace", new Image(temp.getDisplay(), System.getProperty("user.dir")+"/library/DNA24x.png"),
				    "Warnings exist in required searching."+System.getProperty("line.separator")+"Proceed with launch?", 
				    MessageDialog.WARNING, 
				    new String[] { "Proceed","Cancel" }, 0);
			int result=dialog.open();
			if(result==1) return false;
			else return true;
		}
		if(p==1) return true;
		else return false;
		}
	
	
	public Integer getAlghoritm(Composite temp){
		for(int y=0;y<temp.getChildren().length;y++){
			if(temp.getChildren()[y] instanceof org.eclipse.swt.widgets.Group){
				for(int k=0;k<((Group)temp.getChildren()[y]).getChildren().length;k++){
				if(((Button)((Group)temp.getChildren()[y]).getChildren()[k]).getSelection()){
					//System.out.println(((Button)((Group)temp.getChildren()[y]).getChildren()[k]).getText());
					return k; 
					/*
					 * 0: forza bruta
					 * 1: BoyerMoore
					 * 2: KnutMorris
					 */
				}
				}
				
			}
		}
		return -1;
	}
	
	public static Object[] getOption(Composite temp){
		Object[] option=new Object[4];
		int p=0;
		Text tmp;
		Combo cmb;
		for(int y=0;y<temp.getChildren().length;y++){
			//System.out.println(temp.getChildren()[y].getClass().getCanonicalName()+" "+p);
			if(temp.getChildren()[y] instanceof org.eclipse.swt.widgets.Text){
				if(temp.getChildren()[y].getData("name")!=null){
					if(temp.getChildren()[y].getData("name").equals("up")){
						if(((Text)temp.getChildren()[y]).getText().trim().length()>0) option[0]=((Text)temp.getChildren()[y]).getText().trim();
						else option[0]=null;
					}
					if(temp.getChildren()[y].getData("name").equals("down")){
						if(((Text)temp.getChildren()[y]).getText().trim().length()>0) option[1]=((Text)temp.getChildren()[y]).getText().trim();
						else option[1]=null;
					}
					if(temp.getChildren()[y].getData("name").equals("mirnaList")){
						//tmp=;
						String[] lines=((Text)temp.getChildren()[y]).getText().split(System.getProperty("line.separator"));
						//HashMap<Integer,Object> id=new HashMap<Integer,Object>(); //mappa degli id dei mirna
						HashMap<Integer,Object> tempId=new HashMap<Integer,Object>();
						//hasmap tempId <id,nome mirna>
						for(int c=0;c<lines.length;c++){
									tempId=f.getMirnaId(lines[c],tempId);
								}
							System.out.println("mirna length: "+tempId.size());
							lines=null;
							System.gc();
							option[3]=tempId;
						/*if(id.isEmpty()) {
							option[3]=null; System.out.println("null");
						}
						else {
							option[3]=id;
						}*/
					}
				}
			}
			if(temp.getChildren()[y] instanceof org.eclipse.swt.widgets.Combo){
				cmb=((Combo)temp.getChildren()[y]);
				option[2]=cmb.getSelectionIndex();
				//if(cmb.getSelectionIndex()==3) return null;
			}
			
		}
		
		return option;
	}
	
	
	public Object[] getData(){
		boolean c=controlOption(duesx);
		//System.out.println("control: "+c);
		if(c){
			Object[] data=new Object[3];
			data[0]=getConsensus(((Composite)sc.getChildren()[0])); //consensus list (Hashmap<String,boolean>)
			data[1]=getOption(duesx); //opzioni di ricerca (text,text,int,hashmap)
			data[2]=getAlghoritm(duedx); //algoritmo di ricerca (integer)
			return data;
		}else return null;
		
	}
	
	public int fattoriale (int n) {
		 if (n >= 0) return (n <= 1) ? 1 : n * fattoriale(n-1);
		 else throw new IllegalArgumentException ("n must be non-negative, " + n + " given");
		 }
	
}


class dbAuth{
	Object[] getAuth(){
		Object[] data;
		File name = new File(System.getProperty("user.dir")+"/library/out.sys");
		 	if (name.isFile()){
		 		data=new Object[4];
		 	try {
			 BufferedReader input = new BufferedReader(new FileReader(name));
			 String text;
			 int y=0;
			 while((text = input.readLine()) != null){
				data[y]=text;
				y++;
				 }
			 input.close();
			 if(y==0) return null;
			 	else return data;
			 } 
		 	catch (IOException ioException) {
		 	}
		 	return null;
	  		} else return null;
	}
}


class assegna { 
	 Integer valore;
	 assegna(Integer valore) {
	 this.valore = valore;
	 }
	 }