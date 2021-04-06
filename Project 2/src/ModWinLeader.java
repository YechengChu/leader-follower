import comp34120.ex2.PlayerImpl;
import comp34120.ex2.PlayerType;
import comp34120.ex2.Record;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.lang.Math;
import java.util.Arrays;

final class ModWinLeader
	extends PlayerImpl
{
  private double[] leaderPrice = new double[130];
  private double[] followerPrice = new double[130];
	private double followerA;
	private double followerB;
	private int windowSize = 60;
	private double lambdaChosen = 0.99;


	ModWinLeader()
		throws RemoteException, NotBoundException
	{
		super(PlayerType.LEADER, "Modified Moving Window Leader");
	}

	@Override
	public void startSimulation(int p_steps)
		throws RemoteException
	{
		super.startSimulation(p_steps);
		initialization();
	}

	@Override
	public void proceedNewDay(int p_date)
		throws RemoteException
	{
		Record oldRecord = this.m_platformStub.query(this.m_type, p_date-1);
	  leaderPrice[p_date-2] = oldRecord.m_leaderPrice;
	  followerPrice[p_date-2] = oldRecord.m_followerPrice;
		float myPrice = getPrice(windowSize, p_date, lambdaChosen);
		this.m_platformStub.publishPrice(this.m_type, myPrice);
	}

	private void initialization()
		throws RemoteException
	{
		for(int i = 1; i <= 100; i++){
      Record oldRecord = this.m_platformStub.query(this.m_type, i);
			leaderPrice[i-1] = oldRecord.m_leaderPrice;
			followerPrice[i-1] = oldRecord.m_followerPrice;
    }
		double x_squre_sum = 0;
		double y_sum = 0;
		double x_sum = 0;
		double xy_sum = 0;
		for(int i = 1; i <= 100; i++){
			double x_value = leaderPrice[i-1];
			double y_value = followerPrice[i-1];
			x_sum += x_value;
			y_sum += y_value;
			xy_sum += (x_value * y_value);
			x_squre_sum += Math.pow(x_value,2);
    }
		followerA = (x_squre_sum * y_sum - x_sum * xy_sum) / (100 * x_squre_sum - Math.pow(x_sum,2));
		followerB = (100 * xy_sum - x_sum * y_sum) / (100 * x_squre_sum - Math.pow(x_sum,2));
	}

	private float getPrice(int window_size, int current_day, double lambda_chosen){
		double x_squre_sum = 0;
		double y_sum = 0;
		double x_sum = 0;
		double xy_sum = 0;
		for(int i = current_day - window_size; i < current_day; i++){
			double x_value = leaderPrice[i-1];
			double y_value = followerPrice[i-1];
			x_sum += x_value;
			y_sum += y_value;
			xy_sum += (x_value * y_value);
			x_squre_sum += Math.pow(x_value,2);
    }
		double m1 = window_size + lambda_chosen;
		double n1 = x_sum;
		double t1 = y_sum + lambda_chosen * followerA;
		double m2 = x_sum;
		double n2 = x_squre_sum + lambda_chosen;
		double t2 = xy_sum + lambda_chosen * followerB;
		followerA = (t1 * n2 - t2 * n1) / (n2 * m1 - n1 * m2);
		followerB = (t1 * m2 - t2 * m1) / (n1 * m2 - n2 * m1);
		double myPrice = - (3 + 0.3 * followerA - 0.3 * followerB) / (2 * (0.3 * followerB - 1));
		return (float)myPrice;
	}

	public static void main(final String[] p_args)
		throws RemoteException, NotBoundException
	{
		new ModWinLeader();
	}
}
