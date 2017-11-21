import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Player extends Role {
	private Timer timer;
	
    public Player(GameInt leader) {
        this.leader = leader;
    }

    public GameInt addPlayer(GameInt player) throws RemoteException {
        leader.addPlayer(player);
        return leader;
    }
    
    public void schedule() {
    	timer.cancel();
    	long delay, period;
        Calendar cal=Calendar.getInstance(); // get today
        int hr = cal.get(Calendar.HOUR_OF_DAY);
        int mn = cal.get(Calendar.MINUTE);
        mn = 5 * (mn / 5 + 1);	// round up to next 5 minute slot
        if (mn == 60) {
          hr = hr + 1; mn = 0;
        }
        cal.set(Calendar.HOUR_OF_DAY,hr);
        cal.set(Calendar.MINUTE,mn);
        cal.set(Calendar.SECOND,0);
        // milliseconds until next 5 minute boundary
        delay = cal.getTimeInMillis() - System.currentTimeMillis();
        Random rand = new Random();
        int randInt = rand.nextInt(500) + 1;
        period = 5 * 60 * 1000 + randInt; // 5 minutes in milliseconds plus a random extension from 1 to 500ms
        timer.schedule(new ElectionRaiser(), delay, period);
    	timer.purge();
    }
    
    /**
     * task that is scheduled by the timer
     */
    class ElectionRaiser extends TimerTask {
      public void run() {
        //raiseElection();
      }
    }
}
