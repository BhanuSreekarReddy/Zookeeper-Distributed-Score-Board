//package com.bhanu.sreekar;
//import com.bhanu.sreekar.ScoreWatcher;
//import com.bhanu.sreekar.PlayerData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;



public class Player {

	private ZooKeeper zk;
	private List<String> players = new ArrayList<>();
	public static String name;
	public static String ip_port;
	
	public Player(String ip_port) throws Exception
	{	
		zk = new ZooKeeper(ip_port, 5000, null);
		Player.ip_port = ip_port;
	}
		

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		if(args.length==3)
		{
			if(args[0].equals("player"))
			{
			String ip_port = args[1];
			String playerName = args[2];
		
			Runtime.getRuntime().addShutdownHook(new Stop());
			name = "/bkarumu_masterznode/"+playerName;
			Player pl = new Player(ip_port);
			pl.join(playerName);
			// posting scores 
			pl.post(playerName);
			}
		}
		if(args.length==6)
		{
			if(args[0].equals("player"))
			{
			String ip_port = args[1];
			String playerName = args[2];
			int count = Integer.parseInt(args[3]);
			int u_delay = Integer.parseInt(args[4]);
			int u_score = Integer.parseInt(args[5]);
			Runtime.getRuntime().addShutdownHook(new Stop());
			name = "/bkarumu_masterznode/"+playerName;
			Player pl = new Player(ip_port);
			pl.join(playerName);
			// posting scores 
			pl.post(playerName, count,u_delay,u_score);
			
			}
		}
	}
	public void join(String playerName) throws Exception
	{
		players = zk.getChildren("/bkarumu_masterznode", true);
		if(players.contains(playerName))
		{
			System.err.println("Player with this name already exists");
			System.exit(0);
		}
		else{
			String path = "/bkarumu_masterznode/"+playerName;
			byte[] data1 = PlayerData.objToByte(new PlayerData(playerName, true, 100, System.currentTimeMillis()));
			zk.create(path, data1, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			players.add(playerName);
		}
		
	}
	public void post(String playerName,int count,int u_delay,int u_score) throws Exception
	{
		Random r=new Random();
		String path = "/bkarumu_masterznode/"+playerName;
		try{
		for(int i=0;i<count;i++)
		{
			int delay = (int) Math.round(r.nextGaussian()*10+u_delay); // 10 is standard deviation(multiplying number) and u_delay is mean(adding number)
			int score = (int) Math.round(r.nextGaussian()*100+u_score);
			if(score<0)
			{
				i--;
				continue;
			}
			byte[] data1 = PlayerData.objToByte(new PlayerData(playerName, true, score, System.currentTimeMillis()));
			TimeUnit.SECONDS.sleep(delay);
			zk.setData(path, data1, zk.exists(path, true).getVersion());
			//update master node value when there is a change
			byte[] update = ByteBuffer.allocate(4).putInt(1).array(); // have to assign to the watcher code
			zk.setData("/bkarumu_masterznode", update, zk.exists("/bkarumu_masterznode", ScoreWatcher.watcherObject()).getVersion());
		}
		}
		catch(Exception e)
		{
			System.out.println("Bad Version");
		}
	}
	
	public void post(String playerName) throws Exception
	{
		String path = "/bkarumu_masterznode/"+playerName;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try{
		while(true)
		{
			System.out.println("Enter the player score");
			int score = Integer.parseInt(br.readLine());
			byte[] data1 = PlayerData.objToByte(new PlayerData(playerName, true, score, System.currentTimeMillis()));
			zk.setData(path, data1, zk.exists(path, true).getVersion());
			//update master node value when there is a change
			byte[] update = ByteBuffer.allocate(4).putInt(1).array(); // have to assign to the watcher code
			zk.setData("/bkarumu_masterznode", update, zk.exists("/bkarumu_masterznode", ScoreWatcher.watcherObject()).getVersion());
		}
		}
		catch(Exception e)
		{
			System.out.println("Bad Version");
		}
	}
	
	
	public void leave(String path) throws Exception
	{
		try{
		zk.delete(path, zk.exists(path, true).getVersion());
		}
		catch(Exception e)
		{
			
		}
		System.out.println("Player Left");
	}
	

}

class Stop extends Thread{
	
	public void run()
	{
		try {
			new Player(Player.ip_port).leave(Player.name);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
} 


