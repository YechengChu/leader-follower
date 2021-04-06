import comp34120.ex2.PlayerImpl;
import comp34120.ex2.PlayerType;
import comp34120.ex2.Record;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A very simple leader implementation that only generates random prices
 * @author Xin
 */
final class HistoricalLeader
	extends PlayerImpl
{
	/* The randomizer used to generate random price */
	private float[] followerData = new float[131];
	private float[] leaderData = new float[131];


	private HistoricalLeader()
		throws RemoteException, NotBoundException
	{
		super(PlayerType.LEADER, "Leader using historical data");
	}

	@Override
	public void goodbye()
		throws RemoteException
	{
		ExitTask.exit(500);
	}

	/**
	 * To inform this instance to proceed to a new simulation day
	 * @param p_date The date of the new day
	 * @throws RemoteException
	 */
	@Override
	public void proceedNewDay(int p_date)
		throws RemoteException
	{
		//System.out.println("" + p_date);
		getHistoricalData(p_date);
		// for(int i = 1; i <= p_date - 1; i++)
		// {
		// 	Record record =  m_platformStub.query(m_type, i);
		// 	followerData[i] = record.m_followerPrice;
		// 	leaderData[i] = record.m_leaderPrice;
		// 	System.out.println("" + i + ": " + followerData[i] + " " + leaderData[i]);
		// }

		m_platformStub.publishPrice(m_type, calculatePrice(p_date));
	}

	/**
	 * Generate a random price based Gaussian distribution. The mean is p_mean,
	 * and the diversity is p_diversity
	 * @param p_mean The mean of the Gaussian distribution
	 * @param p_diversity The diversity of the Gaussian distribution
	 * @return The generated price
	 */

 // private float genPrice(final float p_mean, final float p_diversity)
 // {
	//  return (float) (p_mean + m_randomizer.nextGaussian() * p_diversity);
 // }

private float calculatePrice(int p_date)
{
  double x_2_sum = 0;
  double y_sum = 0;
  double x_sum = 0;
  double x_y_sum = 0;
  double a_star;
  double b_star;
  //double t = 30;
  double t = p_date - 1;
  for(int i = 1; i <= p_date - 1; i++)
  {
     x_2_sum = x_2_sum + leaderData[i] * leaderData[i];
     y_sum = y_sum + followerData[i];
     x_sum = x_sum + leaderData[i];
     x_y_sum = x_y_sum + leaderData[i] * followerData[i];
  }

  a_star = (x_2_sum * y_sum - x_sum * x_y_sum)/(t * x_2_sum - x_sum * x_sum);
  b_star = (t * x_y_sum - x_sum * y_sum)/(t * x_2_sum - x_sum * x_sum);
  double price = -(3 + 0.3 * a_star - 0.3 * b_star) / (2 * (0.3 * b_star - 1));
  return (float)price;
}

private void getHistoricalData(int p_date)
	throws RemoteException
{
	if(p_date - 1 == 100)
	{
		for(int i = 1; i <= 100; i++)
		{
			Record record = m_platformStub.query(m_type, i);
			followerData[i] = record.m_followerPrice;
			leaderData[i] = record.m_leaderPrice;
		}
	}
  else
  {
    Record record = m_platformStub.query(m_type, p_date - 1);
    followerData[p_date - 1] = record.m_followerPrice;
    leaderData[p_date - 1] = record.m_leaderPrice;
  }
}


public static void main(final String[] p_args)
	throws RemoteException, NotBoundException
{
	new HistoricalLeader();
}

	/**
	 * The task used to automatically exit the leader process
	 * @author Xin
	 */
	private static class ExitTask
		extends TimerTask
	{
		static void exit(final long p_delay)
		{
			(new Timer()).schedule(new ExitTask(), p_delay);
		}

		@Override
		public void run()
		{
			System.exit(0);
		}
	}
}
