import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.eaio.stringsearch.BoyerMooreHorspool;


public class GetStream implements Callable<Integer[]> {
	

	private int id,al;
	private Object[] opzioni;
	private HashMap<String,Boolean> matrice;

	public GetStream(int mId,HashMap<String,Boolean> matrix_consensus,int algoritmo){
		//numero cassetto da 0 a n
		this.id=mId;		 //id del mirna
		this.matrice=matrix_consensus; //Hashmap<consensus,T/F se è diretta o inversa complementare>
		this.al=algoritmo;
	}
	

	
	public Integer[] call(){
		dbAuth a=new dbAuth();
		Object[] auth=a.getAuth();
		funzioni f=new funzioni(auth);
		Integer[] result=new Integer[2];
		String stream;
		HashMap<String,Integer> temp=new HashMap<String,Integer>();
			int i;
			//System.out.println("select: "+select);
			Object select;
			//Long time=System.currentTimeMillis();
			try{
				//System.out.println("matrice consensus: "+matrice.size());
				select=f.selectString("SELECT tm.stream AS first "
						+ "FROM temp_stream tm WHERE tm.id='"+this.id+"'", "first");
				if(select!=null){ //serve per le ricerche attorno al tss, per capire se c'è o meno
					stream=select.toString();
					  for(String temp_consensus:matrice.keySet()){
							//select="SELECT LOCATE('"+temp_consensus+"',tm.stream) AS first "
							//		+ "FROM temp_stream tm WHERE tm.id='"+this.id+"'";
							if((i=stream.indexOf(temp_consensus))!=-1){
								temp.put(temp_consensus, i);
							}
							
							}
				}
				
			//t=System.currentTimeMillis()-t;
				//System.out.println("--"+Thread.currentThread().getName()+" "+results.exec_time((System.currentTimeMillis()-t)));
			}catch(Exception e){
				e.printStackTrace();
				//System.out.println("stream: "+stream);
			}
	//System.out.println(Thread.currentThread().getName()+" end -> "+temp.size());
	ExecutorService exe = Executors.newFixedThreadPool(10);
	HashMap<String,Integer> parte_preElaborata=new HashMap<String,Integer>();
	List<Callable<Integer>> task=new ArrayList<Callable<Integer>>();
	List<Future<Integer>> list =  new ArrayList<Future<Integer>>();	
	//System.out.print("("+temp.size()+"/"+esecuzione.blocco(temp.size())+")");
	for(String t:temp.keySet()){
			 parte_preElaborata.put(t, temp.get(t));
			 if(parte_preElaborata.size() == esecuzione.blocco(temp.size())){
				 Callable<Integer> worker = new RicercaParallela(al,
						 										  parte_preElaborata,
						 										  id);
				 exe.submit(worker);
				 //task.add(worker);
				 parte_preElaborata = new  HashMap<String,Integer>();
			 }
		
	}
	//System.out.print(") ");
	parte_preElaborata.clear();
 	parte_preElaborata=null;
 	/* try {
			list=exe.invokeAll(task);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
 	exe.shutdown();
	while(!exe.isTerminated()){ }
	int hit=0;
	/* for(Future<Integer> r:list){
		 try {
			 if(r.get()!=null){
					 hit=hit+r.get();
			 }
		 }catch(Exception e){e.printStackTrace();}}*/
	temp=null;
	System.gc();
	result[0]=id;
	result[1]=hit;
	return result;
}//fine run
}//fine class
