import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import com.eaio.stringsearch.BoyerMooreHorspool;


public class RicercaParallela  implements Callable<Integer> {

	
	private int id;
	private HashMap<String,Integer> mirna_matrix;
	private int al;

	public RicercaParallela(Integer algoritmo,HashMap<String,Integer> ConsensusMatrix,Integer key){
		this.id=key;
		this.al=algoritmo;
		this.mirna_matrix=ConsensusMatrix;
	}
	
	 public Vector<Integer> checkOccurs(String toCheck, String s2,Integer lastCursor){
			// System.out.println("toCheck: "+toCheck+" s2: "+s2);
				if(lastCursor!=0) lastCursor--;
			 	Vector<Integer> listOccorrenze= new Vector<Integer>();
				while(s2.indexOf(toCheck,lastCursor)!=-1)
								{ 
								listOccorrenze.addElement(s2.indexOf(toCheck,lastCursor));
								//option.setMatchingCount(1);
								lastCursor = (s2.indexOf(toCheck, lastCursor)) + toCheck.length();
								}//fine while
				
				if(listOccorrenze.size()>0) return listOccorrenze;
				else {
					listOccorrenze=null;
					System.gc();
					return null;
				}
				
			}
	 
	 public Vector<Integer> searching(String consensus,char[] s2,Integer lastCursor){
		 if(lastCursor!=0) lastCursor--;
		 Vector<Integer> occorrenze=new Vector<Integer>();
		 BoyerMooreHorspool b=new BoyerMooreHorspool();
		 Object preProcess=b.processString(consensus);
		 char[] pattern=consensus.toCharArray();
		 int lunghezza=consensus.length();
		 if(lastCursor==-1) return null;
		 while(lastCursor!=-1){   //ricerca pattern in stream
					lastCursor=b.searchChars(s2, lastCursor, pattern, preProcess);
					if(lastCursor!=-1){
						//System.out.println("hit -> "+lastCursor);
						//option.setMatchingCount(1);
						occorrenze.add(lastCursor);
						lastCursor+=lunghezza;
					}
					  } //while
		 pattern=null;
		 //text=null;
		 System.gc();
		 if(occorrenze.isEmpty()) return null;
		 else return occorrenze;
		 }
	
	 
	public Integer call(){
		 dbAuth a=new dbAuth();
		 Object[] auth=a.getAuth();
		 //Integer result;
		 funzioni f=new funzioni(auth);
		 Vector<Integer> hit=new Vector<Integer>();
		 HashMap<String,Vector<Integer>> consensus_hit=new HashMap<String,Vector<Integer>>();
		 String stream=f.get_temporary_stream(""+id);
		 Long t=System.currentTimeMillis();
		 switch(al){
		 case 0:
			 for(String c:mirna_matrix.keySet()){
				 hit=checkOccurs(c, stream, mirna_matrix.get(c));
				 consensus_hit.put(c, hit);
				 //option.setProgress(true);
			 }
			 break;
		 case 1:
			 //System.out.println(Thread.currentThread().getName()+" "+mirna_matrix.size());
			 char[] text=stream.toCharArray();
			 for(String c:mirna_matrix.keySet()){
				 hit=searching(c,text,mirna_matrix.get(c));
				 consensus_hit.put(c, hit);
				 //option.setProgress(true);
			 }
			 text=null;
			 break;
		 }
		 //result[0]=id;
		 //result[1]=consensus_hit;
		 //result=consensus_hit.size();
		 esecuzione.updateResult(id, consensus_hit);
		 //result[2]=System.currentTimeMillis()-t;
		 //Thread.currentThread().
		 //System.out.println("--"+Thread.currentThread().getName()+" "+results.exec_time((System.currentTimeMillis()-t)));
		 return 0;
		
	}
}
