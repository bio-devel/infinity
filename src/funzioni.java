import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Shell;


public class funzioni {

	
	//private String DB_CONN_STRING = "jdbc:mysql://";
	private String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
	/*private String USER_NAME = "root";
	private String PASSWORD = "luca";*/

	private static Connection conn = null;


	public funzioni(Object[] data){
		if(data == null || data[1] == null) return;
		String DB_CONN_STRING = "jdbc:mysql://"+data[3]+"/";
		String USER_NAME = data[1].toString();
		String PASSWORD = data[2].toString();
		
		//System.out.println(DB_CONN_STRING+" - "+USER_NAME+" - "+PASSWORD);
		
		if(data[0].toString().length()>0){DB_CONN_STRING+=data[0].toString();}
		
		
		
		if (conn != null) {
			return;
		}
		
		try {
			Class.forName(DRIVER_CLASS_NAME).newInstance();
		} catch (Exception ex) {
				
	   			System.out.println("Check classpath. Cannot load db driver: "+ DRIVER_CLASS_NAME) ;
		}
		try {
			conn = DriverManager.getConnection(DB_CONN_STRING, USER_NAME, PASSWORD);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println();
			 MessageDialog dialog = new MessageDialog(
						new Shell(), 
						"Workspace Error", 
						null, 
						"Driver loaded, but cannot connect to db: "+
						System.getProperty("line.separator") + DB_CONN_STRING + " "+USER_NAME+":"+ PASSWORD+";",
						MessageDialog.ERROR, 
						new String[]{"OK"}, 
						0);	
 		 dialog.open();
 		dbOption o=new dbOption(null, true); //inizializza la prima connessione al database
		}
	}//fine connector
	
	
	public Combo lista(Combo c){
		ResultSet rs = null;
		Statement stmt = null;
		try{
			String select="show databases";
			stmt = (Statement)conn.createStatement();
			rs=((java.sql.Statement)stmt).executeQuery(select);
			while(rs.next()){
				c.add(rs.getString("Database"));
			}
			rs.last();
			if(rs.getRow()>0){
				c.add("Select schema...",0);
				c.select(0);
			} 
		}catch(Exception e){e.printStackTrace();}
		return c;
	}
	
	
	public void setUpdate(){
		ResultSet rs = null;
		Statement stmt = null;
		HashMap<Integer,String> ids=new HashMap<Integer,String>();
		
		try{
			String select="SELECT id,location FROM tss-mirna.tss_position_mirstart";
			String update,location;
			//System.out.println(select);
			stmt = (Statement)conn.createStatement();
			rs=((java.sql.Statement)stmt).executeQuery(select);
			while(rs.next()) ids.put(rs.getInt("id"), rs.getString("location"));
			Set<Integer> keySet=ids.keySet();
			for(Integer key:keySet){
					location=ids.get(key).split(" ")[1].toString();
					update="UPDATE tss_position_mirstart t SET location='"+location+"' WHERE id='"+key+"'";
					System.out.println(update);
					((java.sql.Statement)stmt).execute(update);
				}
				stmt.close();
		}catch(Exception e){e.printStackTrace();}
	}
	
	public HashMap<Integer,Object> getMirnaId(String mirna, HashMap<Integer,Object> ids){
		ResultSet rs = null;
		Statement stmt = null;
		//HashMap<Integer,Object> ids=new HashMap<Integer,Object>();
		try{
			String select="SELECT id FROM mirna_upstream WHERE mirna LIKE '"+mirna+"'";
			//System.out.println(select);
			stmt = (Statement)conn.createStatement();
			rs=((java.sql.Statement)stmt).executeQuery(select);
			if(rs.next()){
				rs.beforeFirst();
			}else{
				System.out.println(mirna);
			}
			while(rs.next()){
					ids.put(rs.getInt("id"), mirna);
					//System.out.println(rs.getInt("id")+" "+mirna);
				}
				stmt.close();
				return ids;
				
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	public Integer getMirnaId_fromName(String mirna){
		ResultSet rs = null;
		Statement stmt = null;
		int id=-1;
		//HashMap<Integer,Object> ids=new HashMap<Integer,Object>();
		try{
			String select="SELECT id FROM tss_position_mirstart WHERE name='"+mirna+"'";
			//System.out.println(select);
			stmt = (Statement)conn.createStatement();
			rs=((java.sql.Statement)stmt).executeQuery(select);
			if(rs.next()){
					id=rs.getInt("id");
				}
				stmt.close();
				return id;
				
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	public Integer getPromoterLength(Integer id){
		ResultSet rs = null;
		Statement stmt = null;
		int t=-1;
		try{
			String select="SELECT id FROM mirna_upstream WHERE id='"+id+"'";
			stmt = (Statement)conn.createStatement();
			rs=((java.sql.Statement)stmt).executeQuery(select);
			if(rs.next()){
				stmt.close();
				select="SELECT if(m.strand=1,(t.location-m.start),(m.end-t.location)) AS promoter " +
						"FROM mirna_upstream m,tss_position_mirstart t WHERE m.id=t.id AND m.id="+id;
				//System.out.println(select);
				stmt = (Statement)conn.createStatement();
				rs=((java.sql.Statement)stmt).executeQuery(select);
				if(rs.next()){
					t=rs.getInt("promoter");
				}
				stmt.close();
			}
		
			return t;
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	public Integer getDownstreamLength(Integer id){
		ResultSet rs = null;
		Statement stmt = null;
		int t=0;
		try{
			
			String select="select t.id,if(m.strand=1,(m.end-t.lunghezza-t.location+1),(t.location-m.start-t.lunghezza+1)) AS downstream " +
					"FROM mirna_upstream m,tss_position_mirstart t where m.id=t.id AND m.id="+id;
			stmt = (Statement)conn.createStatement();
			rs=((java.sql.Statement)stmt).executeQuery(select);
			if(rs.next()){
					t=rs.getInt("downstream");
				}else{
					stmt.close();
					return null;
				}
			stmt.close();
			return t;
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	public Integer getAbsDownstreamLength(){
		ResultSet rs = null;
		Statement stmt = null;
		int t=0;
		try{
			String select="SELECT (m.end-t.lunghezza-t.location+1) AS downstream,(m.end-m.start) AS lunghezza  " +
					"FROM mirna_upstream m,tss_position_mirstart t where m.id=t.id AND m.strand=1 AND t.lunghezza>0 " +
					"ORDER BY lunghezza DESC " +
					"LIMIT 1";
			stmt = (Statement)conn.createStatement();
			rs=((java.sql.Statement)stmt).executeQuery(select);
			if(rs.next()){
					t=rs.getInt("downstream");
				}else{
					stmt.close();
					return 0;
				}
			stmt.close();
			return t;
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	
	public Integer getAbsPromoterLength(){
		ResultSet rs = null;
		Statement stmt = null;
		int t=0;
		try{
			String select="SELECT (t.location-m.start) AS promoter,(m.end-m.start) AS lunghezza " +
					"FROM mirna_upstream m,tss_position_mirstart t where m.id=t.id AND m.strand=1 " +
					"ORDER BY lunghezza DESC " +
					"LIMIT 1";
			//System.out.println(select);
			stmt = (Statement)conn.createStatement();
			rs=((java.sql.Statement)stmt).executeQuery(select);
			if(rs.next()){
					t=rs.getInt("promoter");
				}else{
					stmt.close();
					return 0;
				}
			stmt.close();
			return t;
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	public String getStream(Integer id){
		ResultSet rs = null;
		Statement stmt = null;
		try{
			String select="SELECT upstream FROM mirna_upstream WHERE id='"+id+"'";
			System.out.println(select);
			stmt = (Statement)conn.createStatement();
			rs=((java.sql.Statement)stmt).executeQuery(select);
			if(rs.next()){
					String upstream=rs.getString("upstream");
					stmt.close();
					return upstream;
				}else{
					stmt.close();
					return null;
				}
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	
	
	public String reverse_complementary(String toReverse){
		String revComp="";
		for(int i=toReverse.length()-1;i>=0;i--){
			if(toReverse.charAt(i)=='a'){revComp=revComp + "t";}
			if(toReverse.charAt(i)=='t'){revComp=revComp + "a";}
			if(toReverse.charAt(i)=='g'){revComp=revComp + "c";}
			if(toReverse.charAt(i)=='c'){revComp=revComp + "g";}
		}
		return revComp;
	}
	
	public Integer getNumber(){
		ResultSet rs = null;
		Statement stmt = null;
		try{
			String select="SELECT COUNT(*) AS tot FROM mirna_upstream";
			stmt = (Statement)conn.createStatement();
			rs=((java.sql.Statement)stmt).executeQuery(select);
			if(rs.next()){
				int y=rs.getInt("tot");
				stmt.close();
				return y;
				}
				else{
					stmt.close();
					return null;
				}
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	
	
	public String getMirnaName(Integer id){
		ResultSet rs = null;
		Statement stmt = null;
		String nome;
		try{
			String select="SELECT name FROM tss_position_mirstart WHERE id='"+id+"'";
			stmt = (Statement)conn.createStatement();
			rs=((java.sql.Statement)stmt).executeQuery(select);
			if(rs.next()){
				nome=rs.getString("name");
				stmt.close();
				return nome;
				}
				else{
					stmt.close();
					return null;
				}
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	public String getMirnaAccession(Integer id){
		ResultSet rs = null;
		Statement stmt = null;
		String nome;
		try{
			String select="SELECT accession FROM tss_position_mirstart WHERE id='"+id+"'";
			stmt = (Statement)conn.createStatement();
			rs=((java.sql.Statement)stmt).executeQuery(select);
			if(rs.next()){
				nome=rs.getString("accession");
				stmt.close();
				return nome;
				}
				else{
					stmt.close();
					return null;
				}
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	public String getMirnaType(Integer id){
		ResultSet rs = null;
		Statement stmt = null;
		String nome;
		try{
			String select="SELECT type FROM tss_position_mirstart WHERE id='"+id+"'";
			stmt = (Statement)conn.createStatement();
			rs=((java.sql.Statement)stmt).executeQuery(select);
			if(rs.next()){
				nome=rs.getString("type");
				stmt.close();
				return nome;
				}
				else{
					stmt.close();
					return null;
				}
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	/*
	 * return TRUE: strand 1(+)
	 */
	public Boolean getMirnaStrand(Integer id){
		ResultSet rs = null;
		Statement stmt = null;
		Boolean nome;
		try{
			String select="SELECT strand FROM mirna_upstream WHERE id='"+id+"'";
			stmt = (Statement)conn.createStatement();
			rs=((java.sql.Statement)stmt).executeQuery(select);
			if(rs.next()){
				nome=rs.getBoolean("strand");
				stmt.close();
				return nome;
				}
				else{
					stmt.close();
					return null;
				}
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	
	public String getMirnaLength(Integer id){
		ResultSet rs = null;
		Statement stmt = null;
		String nome;
		try{
			String select="SELECT lunghezza FROM tss_position_mirstart WHERE id='"+id+"'";
			stmt = (Statement)conn.createStatement();
			rs=((java.sql.Statement)stmt).executeQuery(select);
			if(rs.next()){
				nome=rs.getString("lunghezza");
				stmt.close();
				return nome;
				}
				else{
					stmt.close();
					return null;
				}
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	public String getMirnaHost(Integer id){
		ResultSet rs = null;
		Statement stmt = null;
		String nome;
		try{
			String select="SELECT host_gene FROM tss_position_mirstart WHERE id='"+id+"'";
			stmt = (Statement)conn.createStatement();
			rs=((java.sql.Statement)stmt).executeQuery(select);
			if(rs.next()){
				nome=rs.getString("host_gene");
				stmt.close();
				return nome;
				}
				else{
					stmt.close();
					return null;
				}
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	public String getMirnaLocation(Integer id){
		ResultSet rs = null;
		Statement stmt = null;
		String nome;
		try{
			String select="SELECT CONCAT(chr,': ',location,' [',IF(strand=1,'+','-'),']') AS loci FROM tss_position_mirstart t,mirna_upstream m WHERE t.id=m.id AND t.id='"+id+"'";
			stmt = (Statement)conn.createStatement();
			rs=((java.sql.Statement)stmt).executeQuery(select);
			if(rs.next()){
				nome=rs.getString("loci");
				stmt.close();
				return nome;
				}
				else{
					stmt.close();
					return null;
				}
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	public Integer getMirnaTSS(Integer id){
		ResultSet rs = null;
		Statement stmt = null;
		Integer nome=null;
		try{
			String select="SELECT tss FROM tss_position_mirstart WHERE id='"+id+"'";
			stmt = (Statement)conn.createStatement();
			rs=((java.sql.Statement)stmt).executeQuery(select);
			if(rs.next()){
				if(rs.getString("tss")!=null){
					nome=rs.getInt("tss");
					stmt.close();
					return nome;
				}else{
					stmt.close();
					return null;
				}
				
				}
				else{
					stmt.close();
					return null;
				}
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	public Integer getMirnaDistanceFromTSS(Integer id){
		ResultSet rs = null;
		Statement stmt = null;
		int nome;
		try{
			String select="SELECT tss, (IF(tss=null,null,ABS(location-tss))) AS distance FROM tss_position_mirstart WHERE id='"+id+"'";
			stmt = (Statement)conn.createStatement();
			rs=((java.sql.Statement)stmt).executeQuery(select);
			if(rs.next()){
				if(rs.getString("tss")!=null){
					nome=rs.getInt("distance");
					stmt.close();
					return nome;
				}else{
					stmt.close();
					return null;
				}
				
				}
				else{
					stmt.close();
					return null;
				}
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	
	
	public Object[] getLocation(Integer id){
		ResultSet rs = null;
		Statement stmt = null;
		Object[] ret=new Object[5];
		try{
			String select="SELECT location,tss,chr,strand,lunghezza FROM mirna_upstream m, tss_position_mirstart t WHERE t.id=m.id AND m.id='"+id+"'";
			stmt = (Statement)conn.createStatement();
			rs=((java.sql.Statement)stmt).executeQuery(select);
			if(rs.next()){
				ret[0]=rs.getString("location");
				ret[1]=rs.getString("tss");
				ret[2]=rs.getString("chr");
				if(rs.getInt("strand")==1) ret[3]='+';
				else ret[3]='-';
				//ret[3]=rs.getString("strand");
				ret[4]=rs.getInt("lunghezza");
				//for(int y=0;y<5;y++) if(ret[y]!=null)System.out.println(y+" "+ret[y].toString());
				stmt.close();
				return ret;
				}
				else{
					stmt.close();
					return null;
				}
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	
	
	public Object getDistanceFromPrecursor(Integer id){
		ResultSet rs = null;
		Statement stmt = null;
		try{
			String select="SELECT distance_from_precursor FROM  tss_position_mirstart WHERE id='"+id+"'";
			stmt = (Statement)conn.createStatement();
			rs=((java.sql.Statement)stmt).executeQuery(select);
			if(rs.next()){
				Object ret=rs.getObject("distance_from_precursor");
				stmt.close();
				return ret;
				}
				else{
					stmt.close();
					return null;
				}
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	
	
	public HashMap<Integer,Object> getIds(){
		ResultSet rs = null;
		Statement stmt = null;
		HashMap<Integer,Object> ids= new HashMap<Integer,Object>();
		try{
			String select="SELECT id FROM mirna_upstream";
			stmt = (Statement)conn.createStatement();
			rs=((java.sql.Statement)stmt).executeQuery(select);
			while(rs.next()){
				ids.put(rs.getInt("id"), null);
				}
			stmt.close();
		}catch(Exception e){e.printStackTrace();}
		 return ids;
		
	}
	
	
	public Boolean reset_temporary(){
		Statement stmt = null;
		try{
			String delete="DELETE FROM temp_stream WHERE id<>0";
			stmt = (Statement)conn.createStatement();
			if(stmt.executeUpdate(delete)!=-1){
						stmt.close();
						return true;
			}else{
				stmt.close();
				return false;
			}
		}catch(Exception e){e.printStackTrace();}
		 return false;
	}
	
	
	public Boolean temporary_stream(String stream, Integer id){
		Statement stmt = null;
		try{
			String select="INSERT INTO temp_stream(id,stream) VALUES('"+id+"','"+stream+"')";
			stmt = (Statement)conn.createStatement();
			stmt.executeUpdate(select,Statement.RETURN_GENERATED_KEYS);
			if(stmt.getGeneratedKeys().next()){
						stmt.close();
						return true;
			}else{
				stmt.close();
				return false;
			}
		}catch(Exception e){e.printStackTrace();}
		 return false;
	}
	
	
	/*public String get_temporary_stream(Integer id){
		Statement stmt = null;
		ResultSet rs = null;
		try{
			String select="SELECT stream FROM temp_stream WHERE id='"+id+"'";
			stmt = (Statement)conn.createStatement();
			rs=((java.sql.Statement)stmt).executeQuery(select);
			if(rs.next()){
				String temp=rs.getString("stream");
				stmt.close();
				return temp;
			}else{
				stmt.close();
				return null;
			}
		}catch(Exception e){e.printStackTrace();}
		 return null;
	}*/
	
	public void prepara_stream(Object[] opzioni, //lista opzioni: inizio,fine,posizione(tss - 2 /mirna - 1 / absolute - 3)
								Integer id){     //id del mirna
		
		String select="";
		String stream="";
		ResultSet rs = null;
		Statement stmt = null;
		int start;//contiene lunghezza promoter
		int end; //contiene lunhgezza downstream
		int len=0; //lunghezza della sequenza da ricercare
		//----> opzioni {upstream,downstream,locazione,hash id mirna}
			
		start=getPromoterLength(id);
		if(start==-1) return ;
		end=getDownstreamLength(id);
		if(opzioni[0]!=null){
			len=Integer.parseInt(opzioni[0].toString());
			if(start>=len){
				start=start-len;
			}
		}else start=0;
		//start ora contiene il punto di inizio della substring nel db
		
		if(opzioni[1]!=null){
			if(end<=Integer.parseInt(opzioni[1].toString())) len+=end;
			else len+=Integer.parseInt(opzioni[1].toString());
		}else end=0;
		//len ora contiene la lunghezza della stringa di nucleotidi da ricercare
		
		//int tss=-1;
		//if(opzioni[2].equals(1)) tss=1;
		//else if(opzioni[2].equals(2)) tss=2;
		int tss = Integer.parseInt(opzioni[2].toString());
		try{
			select="DELETE FROM temp_stream where id="+id;
			UpdateString(select);
			switch(tss){
			case 1:
				select="INSERT INTO temp_stream(id,stream) "
						+ "SELECT m.id,SUBSTRING(upstream FROM ("+start+"+1) FOR (t.lunghezza+"+len+")) AS stream " +
						"FROM mirna_upstream m,tss_position_mirstart t WHERE m.id=t.id AND m.id='"+id+"'";
				break;
			case 2:
				start=0;
				len=1;
				if(opzioni[0]!=null){
					if( Float.parseFloat(opzioni[0].toString()) < 0){
						System.out.println("float "+Float.parseFloat(opzioni[0].toString()));
					}
					start=Integer.parseInt(opzioni[0].toString());//inizio regione
				}
				if(opzioni[1]!=null){
					if( Float.parseFloat(opzioni[0].toString()) < 0 && 
							 Float.parseFloat(opzioni[1].toString()) < 0){
						start = Math.abs(start);
						len=start+Integer.parseInt(opzioni[1].toString()); //1+start regione+end regione
					}else len=1+start+Integer.parseInt(opzioni[1].toString()); //1+start regione+end regione
				}
				select="INSERT INTO temp_stream(id,stream) "
						+ "SELECT m.id, SUBSTRING(m.upstream,IF(m.strand='1',IF((t.tss-m.start-"+start+")<=0,(1),(t.tss-m.start-"+start+"))," +
																"IF((m.end-t.tss-"+start+")<=0,(1),(m.end-t.tss-"+start+"))),"+Math.abs(len)+") AS stream " +
						"FROM mirna_upstream m,tss_position_mirstart t WHERE (t.TSS IS NOT NULL) AND m.id=t.id AND m.id='"+id+"'";
				System.out.println(select);
																	
				break;
			case 3:
				start = Integer.parseInt(opzioni[0].toString());
				len = start - Integer.parseInt(opzioni[1].toString()); 
				select="INSERT INTO temp_stream(id,stream) "
						+ "SELECT m.id,SUBSTRING(upstream FROM ("+start+"+1) FOR ("+Math.abs(len)+")) AS stream " +
						"FROM mirna_upstream m,tss_position_mirstart t WHERE m.id=t.id AND m.id='"+id+"'";
				
				break;
			}
			stmt = (Statement)conn.createStatement();
			stmt.executeUpdate(select,Statement.RETURN_GENERATED_KEYS);
			stmt.close();
			return;
			
			
		}catch(Exception e){
				e.printStackTrace();
			}
		
		
	}
	
	
	public HashMap<String,Integer> getUpstreams(HashMap<String,Boolean> consensus, //hashmap delle consensus
									Object[] optione, //lista opzioni: inizio,fine,posizione(tss - 1 /mirna - 2)
									Integer id){ //id del mirna
		/*
		 * HashMap<String,Integer> temp -> [consensus.length+1][2]
		 * ------------------------------------------
		 * [consensus0][startOccurrencePosition]
		 * [consensus1][startOccurrencePosition]
		 * 	.
		 * 	.
		 * 	.
		 * [consensusN][startOccurrencePosition]
		 * 
		 * Valido per ogni miRNA
		 */
		ResultSet rs = null;
		Statement stmt = null;
		if(consensus==null || consensus.isEmpty()) return null;
		HashMap<String,Integer> temp=new HashMap<String,Integer>();
		String select="";
		String stream="";
		int start;//contiene lunghezza promoter
		int end; //contiene lunhgezza downstream
		int len=0; //lunghezza della sequenza da ricercare
		//----> opzioni {upstream,downstream,locazione,hash id mirna}
		
		start=getPromoterLength(id);
		if(start==-1) return null;
		end=getDownstreamLength(id);

		
		if(optione[0]!=null){
			len=Integer.parseInt(optione[0].toString());
			if(start>=len){
				start=start-len;
			}
		}else start=0;
		//start ora contiene il punto di inizio della substring nel db
		
		if(optione[1]!=null){
			if(end<=Integer.parseInt(optione[1].toString())) len+=end;
			else len+=Integer.parseInt(optione[1].toString());
		}else end=0;
		//len ora contiene la lunghezza della stringa di nucleotidi da ricercare
		
		int tss=-1;
		if(optione[2].equals(1)) tss=1;
		else tss=2;
		try{
			switch(tss){
			case 1:
				stream="INSERT INTO temp_stream(id,stream) "
						+ "SELECT m.id,SUBSTRING(upstream FROM ("+start+"+1) FOR (t.lunghezza+"+len+")) AS stream " +
						"FROM mirna_upstream m,tss_position_mirstart t WHERE m.id=t.id AND m.id='"+id+"'";
				break;
			case 2:
				start=0;
				len=1;
				if(optione[0]!=null) start=Integer.parseInt(optione[0].toString());//inizio regione
				if(optione[1]!=null) len=1+start+Integer.parseInt(optione[1].toString()); //1+start regione+end regione
				stream="INSERT INTO temp_stream(id,stream) "
						+ "SELECT m.id, SUBSTRING(m.upstream,IF(m.strand='1',IF((t.tss-m.start-"+start+")<=0,(1),(t.tss-m.start-"+start+"))," +
																"IF((m.end-t.tss-"+start+")<=0,(1),(m.end-t.tss-"+start+"))),"+len+") AS stream " +
						"FROM mirna_upstream m,tss_position_mirstart t WHERE (t.TSS IS NOT NULL) AND m.id=t.id AND m.id='"+id+"'";
				
																	
				break;
			}
			
			//System.out.println(stream);
			stmt = (Statement)conn.createStatement();
			try{
				stmt.executeUpdate(stream,Statement.RETURN_GENERATED_KEYS);
				if(stmt.getGeneratedKeys().next()){
					stmt.close();
					}else{
						stmt.close();
						return null;
					}
			}catch(Exception e){
				e.printStackTrace();
				//System.out.println("stream: "+stream);
			}
			
			/*String stream_estratto;
			if(rs.next()){
				//System.out.println("stream: "+stream+" length:"+rs.getString("stream").length());
				//temp[consensus.length][0]=id;
				//temp[consensus.length][1]=null;
				stream_estratto=rs.getString("stream");
				temporary_stream(stream_estratto, id);
				stmt.close();
			}else{
				//se non trovo nulla è inutile continuare. mi serve per i mirna senza TSS
				
				return null;
			}*/
			
			
			
			 int mirnaLen=consensus.entrySet().iterator().next().getKey().length();
			 select="SELECT stream FROM temp_stream WHERE id="+id;
			 stmt = (Statement)conn.createStatement();
			 rs=((java.sql.Statement)stmt).executeQuery(select);
			 if(rs.next()){
					String tempStream=rs.getString("stream");
					select="DELETE FROM fulltexttable where id<>0";
					((java.sql.Statement)stmt).executeUpdate(select);
					select="";
					len=tempStream.length();
					stmt.close();
					for(int y=0;y<len;y++){
						
						if((y+mirnaLen)>len){
							continue;
						}
						select="INSERT INTO fulltexttable(id,data) VALUES ('"+(y+1)+"','"+tempStream.substring(y, y+mirnaLen)+"')";
						stmt = (Statement)conn.createStatement();
						stmt.executeUpdate(select);
						stmt.close();
						}
					
				}else{
					stmt.close();
				}
			
			
			
			
			
			//String temp_consenus;
			option.setElaboraMirna("elaborating in "+getMirnaName(id));
			option.updatebarLen(consensus.size(), true);
			for(String temp_consensus:consensus.keySet()){
					/*switch(tss){
					case 1:
						//select rispetto al mirna

						select="SELECT LOCATE('"+temp_consensus+"',tm.stream) AS first "
								+ "FROM temp_stream tm WHERE tm.id='"+id+"'";
						break;
					case 2:
						//select intorno al tss
						select="SELECT LOCATE('"+temp_consensus+"',stream) AS first FROM temp_stream WHERE id='"+id+"'";
						break;
					}*/
					//select="SELECT LOCATE('"+temp_consensus+"',tm.stream) AS first "
					//		+ "FROM temp_stream tm WHERE tm.id='"+id+"'";
					//System.out.println(select);
				    select="SELECT id AS first FROM fulltexttable WHERE MATCH(data) "
				    			+ "AGAINST('"+temp_consensus+"' IN BOOLEAN MODE) ORDER BY id ASC";
					stmt = (Statement)conn.createStatement();
					try{
						rs=((java.sql.Statement)stmt).executeQuery(select);
					}catch(Exception e){
						e.printStackTrace();
						//System.out.println("select: "+select);
					}
					//temp[y][0]=consensus[y];
					if(rs.next()){
						//if(rs.getInt("first")!=0)
							temp.put(temp_consensus, rs.getInt("first")-1);
					//}
						}
					stmt.close();
					option.setProgress(true,1);
			}
		
		return temp;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	
	public String get_temporary_stream(String id){
		String temp=null;
		ResultSet rs = null;
		Statement stmt = null;
		try{
			String select="SELECT stream FROM temp_stream WHERE id='"+id+"'";
			stmt = (Statement)conn.createStatement();
			rs=((java.sql.Statement)stmt).executeQuery(select);
			if(rs.next()){
				temp=rs.getString("stream");
			}
			//System.out.println(temp);
			stmt.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return temp;
	}
	

	public Integer getMirnaStartStream(int accession){
		Statement stmt = null;
		ResultSet rs = null;
		String select="SELECT start FROM mirna_upstream WHERE id='"+accession+"'";
		try{
			stmt = (Statement) conn.createStatement();
			rs = ((java.sql.Statement) stmt).executeQuery(select);
			if(rs.next()){
				Integer s=rs.getInt("start");
				//System.out.println("id: "+accession+" getMirnaStartStream: "+s);
				stmt.close();
				return s;
			}else{
				stmt.close();
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public Integer getMirnaEndStream(int accession){
		Statement stmt = null;
		ResultSet rs = null;
		String select="SELECT end FROM mirna_upstream WHERE id='"+accession+"'";
		try{
			stmt = (Statement) conn.createStatement();
			rs = ((java.sql.Statement) stmt).executeQuery(select);
			if(rs.next()){
				Integer s=rs.getInt("end");
				stmt.close();
				return s;
			}else{
				stmt.close();
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public String getMirnaCluster(int accession){
		String result="\n";
		int control=0;
		Statement stmt = null;
		ResultSet rs = null;
		String select="SELECT host_gene,tss FROM tss_position_mirstart WHERE id='"+accession+"'";
		try{
			stmt = (Statement) conn.createStatement();
			rs = ((java.sql.Statement) stmt).executeQuery(select);
			if(rs.next()){
				if(rs.getString("host_gene")==null){
					select="SELECT name FROM tss_position_mirstart WHERE tss='"+rs.getString("tss")+"'";
				}else{
					select="SELECT name FROM tss_position_mirstart WHERE host_gene='"+rs.getString("host_gene")+"'";
				}
				stmt = (Statement) conn.createStatement();
				rs = ((java.sql.Statement) stmt).executeQuery(select);
				while(rs.next()){
					result+=rs.getString("name")+"\n";
					control++;
				}
				stmt.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		if(control<=1){result="----------------";}
		return result;
	}
	
	public void updateDb(String sequence,String info){
		String[] Split=info.split("=");
		ResultSet rs=null;
		String end=Split[1].toString().substring(Split[1].indexOf("-")+1, Split[1].indexOf(" "));
		String start=Split[1].toString().substring(Split[1].indexOf(":")+1, Split[1].indexOf("-"));
		String chr=Split[1].toString().substring(Split[1].indexOf("chr")+3, Split[1].indexOf(":"));
		String strand;
		if(Split[1].toString().contains("-")) strand="0";
		else strand="1";
		String name=Split[0].toString().substring(info.indexOf("hsa"), info.indexOf(" "));
		Statement stmt = null;
		try{
			String update="SELECT id FROM mirna_upstream WHERE mirna='"+name+"'";
			stmt = (Statement)conn.createStatement();
			rs=((java.sql.Statement)stmt).executeQuery(update);
			if(rs.next()){
				update="UPDATE mirna_upstream SET start='"+start+"',end='"+end+"',upstream='"+sequence+"' WHERE mirna='"+name+"'";
				System.out.println(name+" "+sequence.length());
				stmt = (Statement) conn.createStatement();
				stmt.execute(update);
				stmt.close();
			}else{
				update="INSERT INTO mirna_upstream(upstream,mirna,start,end,chr,strand) VALUES('"+sequence+"','"
																								+name+"','"
																								+start+"','"
																								+end+"','"
																								+chr+"','"
																								+strand+"')";
				stmt = (Statement) conn.createStatement();
				System.out.println(stmt.executeUpdate(update));
				stmt.close();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	public void updateMiRNAPosition(String info){
		String[] Split=info.split(";");
		//if(Split.length>5) return;
		Statement stmt = null;
		int t=-1;
		try{
			int lunghezza=Integer.parseInt(Split[2].toString())-Integer.parseInt(Split[1].toString())+1;
			String update="UPDATE tss_position_mirstart SET location='"+Split[1].toString().trim()+"',lunghezza='"+lunghezza+"' " +
					"WHERE accession='"+Split[0].toString().trim()+"'";
			//System.out.println(update);
			stmt = (Statement) conn.createStatement();
			t=stmt.executeUpdate(update);
			System.out.println(t);
			stmt.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
 public void fulltext_preprocessing(Integer id, HashMap<String,Boolean> hashConsensus,Object[] opzioni){
	 Statement stmt = null;
	 ResultSet rs = null;
	 HashMap<String,Boolean> pattern= new HashMap<String,Boolean>();
	 int mirnaLen=hashConsensus.entrySet().iterator().next().getKey().length();
	 option.setElaboraMirna("preprocessing "+getMirnaName(id));
	 try{
		 String select="";
		 option.updatebarLen(hashConsensus.size(), true);
		
		 int start;//contiene lunghezza promoter
			int end; //contiene lunhgezza downstream
			int len=0; //lunghezza della sequenza da ricercare
			//----> opzioni {upstream,downstream,locazione,hash id mirna}
			
			start=getPromoterLength(id);
			if(start==-1) return;
			end=getDownstreamLength(id);

			
			if(opzioni[0]!=null){
				len=Integer.parseInt(opzioni[0].toString());
				if(start>=len){
					start=start-len;
				}
			}else start=0;
			//start ora contiene il punto di inizio della substring nel db
			
			if(opzioni[1]!=null){
				if(end<=Integer.parseInt(opzioni[1].toString())) len+=end;
				else len+=Integer.parseInt(opzioni[1].toString());
			}else end=0;
			//len ora contiene la lunghezza della stringa di nucleotidi da ricercare
			
			int tss=-1;
			if(opzioni[2].equals(1)) tss=1;
			else tss=2;
				switch(tss){
				case 1:
					select="INSERT INTO temp_stream(id,stream) "
							+ "SELECT m.id,SUBSTRING(upstream FROM ("+start+"+1) FOR (t.lunghezza+"+len+")) AS stream " +
							"FROM mirna_upstream m,tss_position_mirstart t WHERE m.id=t.id AND m.id='"+id+"'";
					break;
				case 2:
					start=0;
					len=1;
					if(opzioni[0]!=null) start=Integer.parseInt(opzioni[0].toString());//inizio regione
					if(opzioni[1]!=null) len=1+start+Integer.parseInt(opzioni[1].toString()); //1+start regione+end regione
					select="INSERT INTO temp_stream(id,stream) "
							+ "SELECT m.id, SUBSTRING(m.upstream,IF(m.strand='1',IF((t.tss-m.start-"+start+")<=0,(1),(t.tss-m.start-"+start+"))," +
																	"IF((m.end-t.tss-"+start+")<=0,(1),(m.end-t.tss-"+start+"))),"+len+") AS stream " +
							"FROM mirna_upstream m,tss_position_mirstart t WHERE (t.TSS IS NOT NULL) AND m.id=t.id AND m.id='"+id+"'";
					
																		
					break;
				}
				
				//System.out.println(stream);
				
				try{
					stmt = (Statement)conn.createStatement();
					stmt.executeUpdate(select,Statement.RETURN_GENERATED_KEYS);
					if(stmt.getGeneratedKeys().next()){
						stmt.close();
						}else{
							stmt.close();
							return;
						}
				}catch(Exception e){
					e.printStackTrace();
					//System.out.println("stream: "+stream);
				}
		 
		 select="SELECT stream FROM temp_stream WHERE id="+id;
		 stmt = (Statement)conn.createStatement();
		 rs=((java.sql.Statement)stmt).executeQuery(select);
		 if(rs.next()){
				String temp=rs.getString("stream");
				select="DELETE FROM fulltexttable where id<>0";
				((java.sql.Statement)stmt).executeUpdate(select);
				select="";
				len=temp.length();
				stmt.close();
				for(int y=0;y<len;y++){
					
					if((y+mirnaLen)>len){
						continue;
					}
					select="INSERT INTO fulltexttable(id,data) VALUES ('"+(y+1)+"','"+temp.substring(y, y+mirnaLen)+"')";
					stmt = (Statement)conn.createStatement();
					stmt.executeUpdate(select);
					stmt.close();
					}
				
			}else{
				stmt.close();
				return;
			}
		
	 }catch(Exception e){e.printStackTrace();}
	 return;
 }

 
 public Vector<Integer> fulltext_search(String consensus,int id){
	 Vector<Integer> result = new Vector<Integer>();
	 Statement stmt = null;
	 ResultSet rs = null;
	 try{
				 
		 String select="SELECT id FROM fulltexttable WHERE MATCH(data) "
		 		+ "AGAINST('"+consensus+"' IN BOOLEAN MODE) ORDER BY id ASC";
				 stmt = (Statement)conn.createStatement();
				 rs=((java.sql.Statement)stmt).executeQuery(select);
				 while(rs.next()){
						result.add(rs.getInt("id")-1);
					}
				 stmt.close();
				 if(result.isEmpty())return null;
				 else return result;
			
		
	 }catch(Exception e){e.printStackTrace();}
	 return null;
 }
 
 
 public int insert(String query){
	 Statement stmt = null;
	 int rs = -1;
	 try{
		 //System.out.println("insert: "+query);
		 stmt = (Statement)conn.createStatement();
		 rs=stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);
		 stmt.close();
		 return rs;
	 }catch(Exception e){e.printStackTrace();}
	 return -1;
 }
 
 
 public Object selectString(String query,String column){
	 Statement stmt = null;
	 ResultSet rs = null;
	 Object temp=null;
	 try{
		 //System.out.println("query: "+query);
		 stmt = (Statement)conn.createStatement();
		 rs=((java.sql.Statement)stmt).executeQuery(query);
		 if(rs.next())
			 temp=rs.getString(column);
		 stmt.close();
		 return temp;
	 }catch(Exception e){e.printStackTrace();}
	 return null;
 }
	
 public void UpdateString(String query){
	 Statement stmt = null;
	 try{
		 //System.out.println("query: "+query);
		 stmt = (Statement)conn.createStatement();
		 ((java.sql.Statement)stmt).executeUpdate(query);
		 stmt.close();
	 }catch(Exception e){e.printStackTrace();}
 }
	
	
}
