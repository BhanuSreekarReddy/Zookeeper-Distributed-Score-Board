
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ScoreWatcher implements Watcher, StatCallback{

	private static ZooKeeper zk;
	Watcher chainedWatcher;
	static ScoreWatcher sw;
	public static int max;
	List<String> activeChildren = new ArrayList<>();
	LinkedList<PlayerData> recent  = new LinkedList<>();
	TreeSet<PlayerData> top = new TreeSet<PlayerData>(new Comparator<PlayerData>(){
		public int compare(PlayerData p1,PlayerData p2)
		{
			Integer score1 = p1.score;
			Integer score2 = p2.score;
			
			return score2.compareTo(score1);
		}
	});
	
	public ScoreWatcher(String ip_port) throws Exception
	{
		zk = new ZooKeeper(ip_port, 5000, null);
		chainedWatcher = null;
		String path = "/bkarumu_masterznode";
		byte[] data = ByteBuffer.allocate(4).putInt(0).array();
		try{
			zk.create(path,data, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			catch(Exception e){
				
			}
		
		activeChildren = zk.getChildren("/bkarumu_masterznode", this);
		try{
		zk.getData("/bkarumu_masterznode", this, null, zk.exists("/bkarumu_masterznode", this).getVersion());
		}
		catch(Exception e)
		{
			
		}
		System.out.println(activeChildren);
		printScoreBoard();
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		if(args[0].equals("watcher"))
		{
		String ip_port = args[1];
		max = Integer.parseInt(args[2]);
	
		sw = new ScoreWatcher(ip_port);
		
		while(true){}
		}
		
	}
	public static ScoreWatcher watcherObject()
	{
		return sw;
	}

	@Override
	public void process(WatchedEvent event) {

		String path = event.getPath();
		
		if(event.getType() == Watcher.Event.EventType.NodeChildrenChanged)
		{
			try {
				activeChildren = zk.getChildren("/bkarumu_masterznode", this);
				printScoreBoard();
			} catch (KeeperException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		if(event.getType() == Watcher.Event.EventType.NodeDataChanged)
		{
			try {
				byte[] arr = zk.getData("/bkarumu_masterznode", this, zk.exists("/bkarumu_masterznode", this));
				int flag = ByteBuffer.wrap(arr).getInt();
			} catch (KeeperException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// score of the children has changed  and we have to read data of children
			for(int i=0;i<activeChildren.size();i++)
			{
				try {
					byte[] data = zk.getData("/bkarumu_masterznode/"+activeChildren.get(i), true, zk.exists("/bkarumu_masterznode/"+activeChildren.get(i), true));
					try{
						PlayerData pd = PlayerData.byteToObj(data);
					boolean present = false;
					for(int j=0;j<recent.size();j++)
					{
						if(pd.score==recent.get(i).score)
						{
							present = true;
						}
							
					}
					
					if(!present)
					{
						recent.addFirst(pd);
					}
					if(!top.contains(pd))
					{
						top.add(pd);
						printScoreBoard();
						byte[] data1 = ByteBuffer.allocate(4).putInt(0).array();
						try{
						zk.setData(path, data1, zk.exists(path, this).getVersion()); //update
						}
						catch(Exception e)
						{
							
						}
					}
					}
					catch(Exception e)
					{
						
					}
					} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
		if(event.getType() == Watcher.Event.EventType.None)
		{
			switch(event.getState())
			{
			case SyncConnected:
				break;
				
			case Expired:
				System.out.println("Session Expired");
				break;
			}
			}
		else
		{
			if(path!=null && path.equals("/bkarumu_masterznode"))
			{
				// something has changed
				zk.exists("/bkarumu_masterznode", true, this, null);
			}
		}
		if(chainedWatcher!=null)
			chainedWatcher.process(event);
		
	}
	
	public void printScoreBoard()
	{
		System.out.println("Most Recent Scores");
		System.out.println("---------------------------");
		
		int j=0;
		Iterator<PlayerData> iter = recent.iterator();
		while(iter.hasNext() & j<max)
		{
			PlayerData pd = iter.next();
			System.out.println();
			System.out.print(pd.playerName+"\t"+pd.score+"\t");
			if(activeChildren.contains(pd.playerName))
				System.out.print("**");
			j++;
		}
		
		j=0;
		System.out.println("\n\n");
		System.out.println("Highest Scores");
		System.out.println("---------------------------");
		Iterator<PlayerData> ite = top.iterator();
		while(ite.hasNext() && j<max)
		{
			PlayerData pd = ite.next();
			System.out.println();
			System.out.print(pd.playerName+"\t"+pd.score+"\t");
			if(activeChildren.contains(pd.playerName))
				System.out.print("**");
			j++;
		}
		System.out.println();
 	}

	@Override
	public void processResult(int rc, String path, Object ctx, Stat stat) {
		// TODO Auto-generated method stub
		
	}

}

class PlayerData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String playerName;
	boolean isCurrent;
	int score;
	long timeStamp;
	
	public PlayerData(String playerName,boolean isCurrent, int score, long timeStamp)
	{
		this.playerName = playerName;
		this.isCurrent = isCurrent;
		this.score = score;
		this.timeStamp = timeStamp;
	}
	public static byte[] objToByte(PlayerData pd) throws Exception{
		try{
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
		objStream.writeObject(pd);
		
		return byteStream.toByteArray();
		}
		catch(Exception e)
		{
			System.out.println(" Not able to serialize");
		}
		return null;
		
		//return byteStream.toByteArray();
	}
	public static PlayerData byteToObj(byte[] bytes) throws Exception{
		try{
		ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
		ObjectInputStream objStream = new ObjectInputStream(byteStream);
		
		return (PlayerData)objStream.readObject();
		}
		catch(Exception e)
		{
			System.out.println("Not able to deserialize");
		}
		return null;
	}
}

