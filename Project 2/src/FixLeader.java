import comp34120.ex2.PlayerImpl;
import comp34120.ex2.PlayerType;
import comp34120.ex2.Record;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.lang.Math;
import java.util.Arrays;

final class FixLeader
	extends PlayerImpl
{
  private double[] leaderPrice = new double[130];
  private double[] followerPrice = new double[130];

	FixLeader()
		throws RemoteException, NotBoundException
	{
		super(PlayerType.LEADER, "Fixed Leader");
	}

	@Override
	public void startSimulation(int p_steps)
		throws RemoteException
	{
		super.startSimulation(p_steps);
		for(int i = 1; i <= 99; i++){
      Record oldRecord = this.m_platformStub.query(this.m_type, i);
			leaderPrice[i-1] = oldRecord.m_leaderPrice;
			followerPrice[i-1] = oldRecord.m_followerPrice;
    }
	}

	@Override
	public void proceedNewDay(int p_date)
		throws RemoteException
	{
		Record oldRecord = this.m_platformStub.query(this.m_type, p_date-1);
	  leaderPrice[p_date-2] = oldRecord.m_leaderPrice;
	  followerPrice[p_date-2] = oldRecord.m_followerPrice;
		float myPrice = getPrice(p_date);
		this.m_platformStub.publishPrice(this.m_type, myPrice);
	}

	private float getPrice(int current_day){
		double x_squre_sum = 0;
		double y_sum = 0;
		double x_sum = 0;
		double xy_sum = 0;
		double num = current_day - start;
		for(int i = 1; i < current_day; i++){
			double x_value = leaderPrice[i-1];
			double y_value = followerPrice[i-1];
			x_sum += x_value;
			y_sum += y_value;
			xy_sum += (x_value * y_value);
			x_squre_sum += Math.pow(x_value,2);
    }
		double followerA = (x_squre_sum * y_sum - x_sum * xy_sum) / (num * x_squre_sum - Math.pow(x_sum,2));
		double followerB = (num * xy_sum - x_sum * y_sum) / (num * x_squre_sum - Math.pow(x_sum,2));
		double myPrice = - (3 + 0.3 * followerA - 0.3 * followerB) / (2 * (0.3 * followerB - 1));
		return (float)myPrice;
	}

	public static void main(final String[] p_args)
		throws RemoteException, NotBoundException
	{
		new FixLeader();
	}
}
