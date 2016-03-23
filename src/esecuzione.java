import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.eaio.stringsearch.BoyerMooreHorspool;
import com.eaio.stringsearch.BoyerMooreHorspoolRaita;



public class esecuzione {
	
	static dbAuth a=new dbAuth();
	static Object[] auth=a.getAuth();
	static funzioni f=new funzioni(auth);
	public static ConcurrentHashMap<Integer,Object> mdb=new ConcurrentHashMap<Integer,Object>();
	
	
	
	public static int blocco(int size){
		if(size==2) return size; //minimo blocco è 2
		if(size%2==0){
			return blocco(size/2);
		}else{
			return size;
		}
	}
	
	public  ConcurrentHashMap<Integer,Object> manager(Object[] data, funzioni f){
		 /*
		  * data[0]: consensus
		  * data[1]: opzioni {upstream,downstream,locazione,hash id mirna}
		  * data[2]: algoritmo
		  */
		 
		 HashMap<Integer,Object> mirna;
		 //HashMap<String,Vector<Integer>> result;
		 HashMap<String,Integer> search;
		 if(data[1]==null){
			 //prendi stream d'ingresso
			 	/*FileDialog fd = new FileDialog(new Shell(), SWT.OPEN);
		        fd.setText("Open");
		        fd.setFilterPath("C:/");
		        String[] filterExt = { "*.txt", "*.doc", ".rtf", "*.*" };
		        fd.setFilterExtensions(filterExt);
		        String selected = fd.open(); 
		        String stream="";
		        try {
		   			 BufferedReader input = new BufferedReader(new FileReader(selected));
		   			 String text;
		   			 while((text = input.readLine()) != null){
		   				stream+=text;
		   				 }
		   			stream=stream.replaceAll(System.getProperty("line.separator"), "");
		   			 } catch (IOException ioException) {
		   			 }
		        if(stream.length()>0){
		        	
		        	search=new Object[((String[])data[0]).length+1][2];
		        	for(int y=0;y<((String[])data[0]).length;y++){
		        		search[y][0]=((String[])data[0])[y];
		        		search[y][1]=0;
		        	}
		        	search[search.length-1][0]=stream;
		        	search[search.length-1][1]=null;
		        	//for(int y=0;y<search.length;y++) System.out.println(search[y][0].toString());
		        	result=FB(search);
		        	//System.out.println(result.length);
		        	displaySequence d=new displaySequence();
		        	d.getDisplay(null, result, f, stream);
		        }
		        return null;*/
		 }
		 //System.out.println("mirna lenght manager: "+((HashMap<Integer,Object>)((Object[])data[1])[3]).size());
		 if(((Object[])data[1])[3]!=null){
			 if(((HashMap<Integer,Object>)((Object[])data[1])[3]).size()>0){
				 						mirna=(HashMap<Integer,Object>)((Object[])data[1])[3];
			 }else{
				 mirna=f.getIds();
			 }
		 }else{
			 mirna=f.getIds();
		 }
		
		 long temp1;
		 int counter=0;
		 int counterhit;
		 if(!mirna.isEmpty()){ 
			 //System.out.println("2x"+mirna.size()+"x"+option.getConsensusMax());
			 option.resetLen(true);
			 option.resetLen(false);
			 option.updatebarLen(mirna.size(),false);
			 Set<Integer> keySet=mirna.keySet();
			 int algoritmo=Integer.parseInt(data[2].toString());
			 //System.out.println("algoritmo "+algoritmo);
			 //int cpus = Runtime.getRuntime().availableProcessors();
			 int maxThreads = 10;
			 maxThreads = (maxThreads > 0 ? maxThreads : 1);
			 //int HashSize=blocco(((HashMap<String,Boolean>)data[0]).size()); //cerco il blocco minimo(numero disparo) per l'hashmap
			 //System.out.println(HashSize+" N Thread : "+(((HashMap<String,Boolean>)data[0]).size()/HashSize));
			 HashMap<String,Boolean> parte_delle_consensus;
			 ExecutorService executor;
			// ExecutorService executorResult;
			 List<Future<Integer[]>> list;
			 //List<Future<Object[]>> listaRisultati;
			 List<Callable<Integer[]>> task=new ArrayList<Callable<Integer[]>>();
			 //List<Callable<Object[]>> taskResult=new ArrayList<Callable<Object[]>>();
			 for(Integer key:keySet){
				 ConcurrentHashMap<String,Integer> ConsensusMatrix=new ConcurrentHashMap<String,Integer>();
				 //System.out.println("matrix init size "+ConsensusMatrix.size());
				 option.setProgress(false,1);
					 //int mc=0;
				counterhit=0;
				parte_delle_consensus = new  HashMap<String,Boolean>();	
				f.prepara_stream((Object[])data[1], key);
				executor = Executors.newFixedThreadPool(maxThreads);
				list = new ArrayList<Future<Integer[]>>();	
				option.setElaboraMirna("elaborating in "+f.getMirnaName(key));
				option.updatebarLen(((HashMap<String,Boolean>)data[0]).size(), true);
				System.out.println("-------------------------------------------------------------------");
				System.out.println("start active threads: "+java.lang.Thread.activeCount());
				System.out.println(++counter+") "+key+" - "+f.getMirnaName(key));
		  		//System.out.println("Elaborate: "+((HashMap<String,Boolean>)data[0]).size()+"/"+blocco(((HashMap<String,Boolean>)data[0]).size()));
		  		System.out.print("Thread: ");
		  		temp1=System.currentTimeMillis();
		  		for(String t:((HashMap<String,Boolean>)data[0]).keySet()){
						 parte_delle_consensus.put(t, ((HashMap<String,Boolean>)data[0]).get(t));
						 if(parte_delle_consensus.size() == blocco(((HashMap<String,Boolean>)data[0]).size())){
							 System.out.print("+ ");
							 Callable<Integer[]> worker = new GetStream(key, 
									 								   parte_delle_consensus,
									 								   algoritmo);
							 //list.add(executor.submit(worker));
							 //task.add(worker);
							 executor.submit(worker);
							 parte_delle_consensus= new  HashMap<String,Boolean>();
							 
						 }
					 }
					 //
					/* try {
						list=executor.invokeAll(task);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}*/
					 executor.shutdown();
					 while(!executor.isTerminated()){ }
					/* for(Future<Integer[]> future_result:list){
						 
						 try {
							 if(future_result.get()!=null){
								 //System.out.println(future_result.get()[1]);
								 if(future_result.get()[0]==(key)){
									 
									 counterhit=counterhit+future_result.get()[1];
								 }
								
							 }
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					 }*/
					 
					 //list.clear();
					 //list=null; 
					 //task.clear();
					 //task=null;
					 parte_delle_consensus.clear();
					 parte_delle_consensus=null;
					 System.gc();
					 
					 //search=f.getUpstreams(((HashMap<String,Boolean>)data[0]), (Object[])data[1], key);
					 //option.setElaboraMirna("searching in "+f.getMirnaName(key));
					 
					/* if(ConsensusMatrix!=null){
						 if(!ConsensusMatrix.isEmpty()){
							 option.resetLen(true);
							 executorResult = Executors.newFixedThreadPool(maxThreads);
							 listaRisultati= new ArrayList<Future<Object[]>>();
							 HashMap<String,Integer> parte_preElaborata=new HashMap<String,Integer>();
							 System.out.println("Searching by "+ConsensusMatrix.size()+"/"+blocco(ConsensusMatrix.size()));
							 System.out.print("Thread: ");
							 for(String t:ConsensusMatrix.keySet()){
								 parte_preElaborata.put(t, ConsensusMatrix.get(t));
									 if(parte_preElaborata.size() == blocco(ConsensusMatrix.size())){
										 Callable<Object[]> worker = new RicercaParallela(algoritmo,
												 										  parte_preElaborata,
												 										  key);
										 //listaRisultati.add(executorResult.submit(worker));
										 //taskResult.add(worker);
										 executorResult.submit(worker);
										 parte_preElaborata = new  HashMap<String,Integer>();
										 System.out.print("+ ");
									 }
							}
							 	ConsensusMatrix.clear();
							 	ConsensusMatrix=null;
							 	parte_preElaborata.clear();
							 	parte_preElaborata=null;
							 	executorResult.shutdown();
								while(!executorResult.isTerminated()){ }
							 	 System.out.println();
								 listaRisultati.clear();
								 listaRisultati=null; 
								 System.gc();
								 
							
						 }else{
							 System.out.println(" NOT FOUND");
						 }
						 
						 }else{
							 System.out.println(" NOT FOUND"); 
						 }*/
					 temp1=System.currentTimeMillis()-temp1;
					 System.out.println();
					 //System.out.println("Counterhit: "+counterhit);
					 System.out.println("end active threads: "+java.lang.Thread.activeCount()+" -> "+results.exec_time(temp1));
				
				 
		 }//fine for keyset
			 
		 }
		 //System.out.println(mdb.size()); 
		 return esecuzione.mdb;
	 }
	
	
	public static synchronized void updateResult(int id,HashMap<String,Vector<Integer>> hit){
		System.out.println();
		 int y=0;
		 if(!mdb.containsKey(id)){
			 
			 for(String h:hit.keySet()){
				 if(hit.get(h)!=null)
					 y=+hit.get(h).size();
			 }
			 System.out.print("Inserimento id:"+id+" ->"+y);
			 mdb.put(id, hit);
		 }else{
			
			 HashMap<String,Vector<Integer>> temp=(HashMap<String,Vector<Integer>>)mdb.get(id);
			 for(String h:hit.keySet()){
				 if(hit.get(h)!=null){
					 temp.put(h, hit.get(h));
					 y=+hit.get(h).size();
				 }
					
			 }
			 System.out.print("Aggiornamento id:"+id+" ->"+y);
			 mdb.put(id, temp);
		 }
		
	}
	
	
	public static synchronized void updateSearch(int id,HashMap<String,Vector<Integer>> hit){
		System.out.println();
		 if(!mdb.containsKey(id)){
			 System.out.print("Inserimento id:"+id);
			 mdb.put(id, hit);
		 }else{
			 System.out.print("Aggiornamento id:"+id);
			 HashMap<String,Vector<Integer>> temp=(HashMap<String,Vector<Integer>>)mdb.get(id);
			 for(String h:hit.keySet()){
				 temp.put(h, hit.get(h));
			 }
			 mdb.put(id, temp);
		 }
		
	}
	 
	/* 
	 public void FB(ConcurrentHashMap<String,Integer> search,int id){
		 //System.out.println("lenght: "+search.length);
		 //HashMap<String,Vector<Integer>> result=new HashMap<String,Vector<Integer>>();
		 //boolean insert=false;
		 String stream=f.get_temporary_stream(""+id);
		 for(String temp:search.keySet()){
			  //result[y][0]=search[y][0]; 
			  //if(search[y][1]!=null)
				  //result.put(temp,checkOccurs(temp,stream,search.get(temp)));
			 //aggiorna(temp,checkOccurs(temp,stream,search.get(temp)));
			  //if(!insert && ((Vector<Integer>)result[y][1])!=null) insert=true;
		 }
		 //if(result.size()>0) return result;
		 //else return null;
	 }
	 
	 public Vector<Integer> checkOccurs(String toCheck, String s2,Integer lastCursor){
		// System.out.println("toCheck: "+toCheck+" s2: "+s2);
			if(lastCursor!=0) lastCursor--;
		 	Vector<Integer> listOccorrenze= new Vector<Integer>();
			while(s2.indexOf(toCheck,lastCursor)!=-1)
							{ 
							listOccorrenze.addElement(s2.indexOf(toCheck,lastCursor));
							option.setMatchingCount(1);
							lastCursor = (s2.indexOf(toCheck, lastCursor)) + toCheck.length();
							}//fine while
			
			if(listOccorrenze.size()>0) return listOccorrenze;
			else {
				listOccorrenze=null;
				System.gc();
				return null;
			}
			
		}
	 
	 public void BM(ConcurrentHashMap<String,Integer> toSearch,int id){
		 Vector<Integer> hit=new Vector<Integer>();
		 HashMap<String,Vector<Integer>> consensus_hit=new HashMap<String,Vector<Integer>>();
		 String stream=f.get_temporary_stream(""+id);
		 
		 for(String temp:toSearch.keySet()){//ricerca tutte le consensus
			 //option.setProgress(true);
			 hit=searching(temp,stream,toSearch.get(temp));
			 //System.out.println("BM-> "+id+" "+temp+" "+hit);
			 consensus_hit.put(temp, hit);
			 option.setProgress(true);
		 }//for
		
		 esecuzione.mdb.put(id, consensus_hit);
		 
			 //for(String c:consensus_hit.keySet()){
			//	 System.out.println("esterno: "+c+" "+consensus_hit.get(c));
			 //}
		 
		 //if(result.size()>0) return result;
		 //else return null;
		 
	 }
	 
	 
	 
	 public Vector<Integer> searching(String consensus,String s2,Integer lastCursor){
		 if(lastCursor!=0) lastCursor--;
		 //System.out.println("lunghezza consensus:"+consensus.length());
		 BoyerMooreHorspool b=new BoyerMooreHorspool();
		 Vector<Integer> occorrenze=new Vector<Integer>();
		 Object preProcess=b.processString(consensus);
		 char[] pattern=consensus.toCharArray();
		 char[] text=s2.toCharArray();
		 int lunghezza=consensus.length();
		 if(lastCursor==-1) return null;
		 while(lastCursor!=-1){   //ricerca pattern in stream
					lastCursor=b.searchChars(text, lastCursor, pattern, preProcess);
					if(lastCursor!=-1){
						//System.out.println("hit -> "+lastCursor);
						option.setMatchingCount(1);
						occorrenze.add(lastCursor);
						lastCursor+=lunghezza;
					}
					  } //while
		 pattern=null;
		 text=null;
		 System.gc();
		 if(occorrenze.isEmpty()) return null;
		 else return occorrenze;
		 }
	
	 
	 public static void aggiorna2(int key,HashMap<String,Vector<Integer>> hit){
		 try{
			 esecuzione.mdb.put(key, hit);
			}catch(java.lang.NullPointerException e){
				e.printStackTrace();
			}
	 }
	 */
	 
	/* public HashMap<String,Vector<Integer>> dbF(HashMap<String,Boolean> hashConsensus,String id){
		 HashMap<String,Vector<Integer>> result=new HashMap<String,Vector<Integer>>();
		 HashMap<String,Boolean> pattern=f.fulltext_preprocessing(id,hashConsensus);
		 Vector<Integer> hit=null;
		 option.updatebarLen(hashConsensus.size(), true);
		 for(String temp:pattern.keySet()){
			 if(pattern.get(temp)){
					hit=f.fulltext_search(temp,id);
			 		if(hit!=null)
			 			if(hit.size()>0)
			 					option.setMatchingCount(hit.size());
				    result.put(temp,hit);
			 }
			 	option.setProgress(true);
		 }
		 if(result.size()>0) return result;
		 else return null;
	 }*/
	 
	 
}
