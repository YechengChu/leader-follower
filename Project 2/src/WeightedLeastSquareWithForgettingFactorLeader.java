import comp34120.ex2.PlayerImpl;
import comp34120.ex2.PlayerType;
import comp34120.ex2.Record;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.lang.Math;
import java.util.Arrays;

final class WeightedLeastSquareWithForgettingFactorLeader extends PlayerImpl {
    private double[] leaderPrice = new double[130];
    private double[] followerPrice = new double[130];
    private double followerA;
    private double followerB;
    private double lambdaChosen = 0.99;


    WeightedLeastSquareWithForgettingFactorLeader() throws RemoteException, NotBoundException {
        super(PlayerType.LEADER, "Weighted Least Square With Forgetting Factor Leader");
    }

    @Override
    public void startSimulation(int p_steps) throws RemoteException {
        super.startSimulation(p_steps);
        initialization();
    }

    @Override
    public void proceedNewDay(int p_date) throws RemoteException {
        Record oldRecord = this.m_platformStub.query(this.m_type, p_date-1);
        leaderPrice[p_date-2] = oldRecord.m_leaderPrice;
        followerPrice[p_date-2] = oldRecord.m_followerPrice;
        float myPrice = getPrice(p_date, lambdaChosen);
        this.m_platformStub.publishPrice(this.m_type, myPrice);
    }

    private void initialization() throws RemoteException {
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

    private float getPrice(int current_day, double lambda_chosen){
        double x_squre_sum = 0;
        double y_sum = 0;
        double x_sum = 0;
        double xy_sum = 0;
        double lambda_sum = 0;

        for(int i = 1; i < current_day; i++) {
            double x_value = leaderPrice[i-1];
            double y_value = followerPrice[i-1];
            double lambda = Math.pow(lambda_chosen, current_day - i);
            x_sum += x_value * lambda;
            y_sum += y_value * lambda;
            xy_sum += (x_value * y_value) * lambda;
            x_squre_sum += Math.pow(x_value,2) * lambda;
            lambda_sum += lambda;
        }
        double m1 = xy_sum;
        double m2 = lambda_sum;
        double n1 = x_sum;
        double n2 = x_squre_sum;
        double n3 = y_sum;

        followerA = (n1 * m1 - n2 * n3) / (Math.pow(n1,2) - m2 * n2);
        followerB = (n1 * n3 - m1 * m2) / (Math.pow(n1,2) - m2 * n2);

//        final float[][] m_paras = new float[4][4];
        //mk1
//        m_paras[0][0] = 2;
//        m_paras[0][1] = (float) 0.1;
//        m_paras[0][2] = (float) -0.5;
//        m_paras[1][0] = (float) 0.3;
//        m_paras[1][1] = (float) 0.05;
//        m_paras[1][2] = (float) -0.5;
//        m_paras[2][0] = -1;
//        m_paras[2][1] = (float) 0.05;
//        m_paras[2][2] = (float) -0.5;
        //mk2
//        m_paras[0][0] = 2;
//        m_paras[0][1] = (float) 0.1;
//        m_paras[0][2] = (float) -0.5;
//        m_paras[0][3] = (float) 0.005;
//        m_paras[1][0] = (float) 0.3;
//        m_paras[1][1] = (float) 0.03;
//        m_paras[1][2] = (float) -0.5;
//        m_paras[1][3] = (float) 0.003;
//        m_paras[2][0] = -1;
//        m_paras[2][1] = (float) 0.05;
//        m_paras[2][2] = (float) -0.5;
//        m_paras[2][3] = (float) -0.005;
//
//        final float [] l_rand_list = new float[30];
//        l_rand_list[0] = (float) 0.638459485903126;
//        l_rand_list[1] = (float) 0.696374869785359;
//        l_rand_list[2] = (float) 0.0744614157677872;
//        l_rand_list[3] = (float) 0.164718004013208;
//        l_rand_list[4] = (float) 0.203808378778635;
//        l_rand_list[5] = (float) 0.431034229842559;
//        l_rand_list[6] = (float) 0.904097089728637;
//        l_rand_list[7] = (float) 0.0979197394628279;
//        l_rand_list[8] = (float) 0.718338899111445;
//        l_rand_list[9] = (float) 0.413343791768758;
//        l_rand_list[10] = (float) 0.905495160258201;
//        l_rand_list[11] = (float) 0.93420290660712;
//        l_rand_list[12] = (float) 0.279773460561572;
//        l_rand_list[13] = (float) 0.633069455594228;
//        l_rand_list[14] = (float) 0.709531524582614;
//        l_rand_list[15] = (float) 0.361005025432196;
//        l_rand_list[16] = (float) 0.396388361915632;
//        l_rand_list[17] = (float) 0.600788731049059;
//        l_rand_list[18] = (float) 0.900644369373087;
//        l_rand_list[19] = (float) 0.50227400275634;
//        l_rand_list[20] = (float) 0.320688096894718;
//        l_rand_list[21] = (float) 0.350253595358581;
//        l_rand_list[22] = (float) 0.307134751370533;
//        l_rand_list[23] = (float) 0.0577223635523305;
//        l_rand_list[24] = (float) 0.0286727446812627;
//        l_rand_list[25] = (float) 0.760910525791076;
//        l_rand_list[26] = (float) 0.744006960342081;
//        l_rand_list[27] = (float) 0.928235165250738;
//        l_rand_list[28] = (float) 0.337112974377404;
//        l_rand_list[29] = (float) 0.661634983566095;
//
//        float l_rand = l_rand_list[current_day - 101];
        // mk1
//        float l_a = m_paras[0][0] + m_paras[0][1] * (l_rand + m_paras[0][2]);
//        float l_b = m_paras[1][0] + m_paras[1][1] * (l_rand + m_paras[1][2]);
//        float l_c = m_paras[2][0] + m_paras[2][1] * (l_rand + m_paras[2][2]);
        //mk2
//        float l_a = m_paras[0][0] + m_paras[0][1] * (l_rand + m_paras[0][2]) + m_paras[0][3] * (float)current_day;
//        float l_b = m_paras[1][0] + m_paras[1][1] * (l_rand + m_paras[1][2]) + m_paras[1][3] * (float)current_day;
//        float l_c = m_paras[2][0] + m_paras[2][1] * (l_rand + m_paras[2][2]) + m_paras[2][3] * (float)current_day;

//        followerA = (l_a - l_c) / (-2 * l_c);
//        followerB = (l_b) / (-2 * l_c);

        double myPrice = - (3 + 0.3 * followerA - 0.3 * followerB) / (2 * (0.3 * followerB - 1));
        return (float)myPrice;
    }

    public static void main(final String[] p_args)
            throws RemoteException, NotBoundException
    {
        new WeightedLeastSquareWithForgettingFactorLeader();
    }
}