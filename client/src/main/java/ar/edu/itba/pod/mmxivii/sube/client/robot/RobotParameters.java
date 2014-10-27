package ar.edu.itba.pod.mmxivii.sube.client.robot;

import ar.edu.itba.pod.mmxivii.sube.common.Utils;

/**
 * Class containing the parameters of the robot client.
 */
public class RobotParameters
{

	// default values
	public static final double MIN_DELAY = 0.0;
	public static final double MAX_DELAY = 3.0;
	public static final double MIN_OPERATION_AMOUNT = 2.0;
	public static final double MAX_OPERATION_AMOUNT = 20.0;
	public static final int NB_THREADS = 1;
	public static final boolean CHECK_BALANCE_ON_EACH_OPERATION = false;

	// local values
	private int minDelay;
	private int maxDelay;
	private double minOperationAmount;
	private double maxOperationAmount;
	private int nbThreads;
	private boolean checkBalance;

	/**
	 * @param minDelay minimum delay between 2 operations (in milliseconds)
	 * @param maxDelay maximum delay between 2 operations (in milliseconds)
	 * @param minOpAmount minimum amount of an operation (travel and recharge)
	 * @param maxOpAmount maximum amount of an operation (travel and recharge)
	 * @param nbThreads number of threads to use (1 thread = 1 connection to
	 * balancer)
	 * @param checkBalanceEachOp true to ask and check th balance after each
	 * operation (of travelling or recharging), false to do it only when
	 * switching from travelling to recharging (and the opposite)
	 */
	public RobotParameters(int minDelay, int maxDelay,
		double minOpAmount, double maxOpAmount, int nbThreads,
		boolean checkBalanceEachOp)
	{
		// sanity checks
		if (minDelay < 0)
		{
			throw new IllegalArgumentException(
				"The minimal delay cannot be negative");
		}
		if (minDelay > maxDelay)
		{
			throw new IllegalArgumentException(
				"The minimal delay cannot be higher than the maximum delay");
		}
		Utils.assertAmount(minOpAmount);
		Utils.assertAmount(maxOpAmount);
		if (minOpAmount > maxOpAmount)
		{
			throw new IllegalArgumentException(
				"The minimal amount of an operation cannot be higher than "
				+ "the maximum amount");
		}
		if (nbThreads < 1)
		{
			throw new IllegalArgumentException(
				"The number of threads cannot be lower than 1");
		}

		// initializing the object
		this.minDelay = minDelay;
		this.maxDelay = maxDelay;
		this.minOperationAmount = minOpAmount;
		this.maxOperationAmount = maxOpAmount;
		this.nbThreads = nbThreads;
		this.checkBalance = checkBalanceEachOp;
	}

	public int getMinDelay()
	{
		return minDelay;
	}

	public int getMaxDelay()
	{
		return maxDelay;
	}

	public double getMinOperationAmount()
	{
		return minOperationAmount;
	}

	public double getMaxOperationAmount()
	{
		return maxOperationAmount;
	}

	public int getNumberOfThreads()
	{
		return nbThreads;
	}

	public boolean getCheckBalanceAfterEachOperation()
	{
		return checkBalance;
	}

}
