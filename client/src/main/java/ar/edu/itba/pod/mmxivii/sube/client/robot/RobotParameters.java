package ar.edu.itba.pod.mmxivii.sube.client.robot;

import ar.edu.itba.pod.mmxivii.sube.common.CardRegistry;
import ar.edu.itba.pod.mmxivii.sube.common.Utils;

/**
 * Class containing the parameters of the robot client.
 */
public class RobotParameters
{

	// default values
	public static final int MIN_DELAY = 0;
	public static final int MAX_DELAY = 1000;
	public static final double MIN_OPERATION_AMOUNT = 2.0;
	public static final double MAX_OPERATION_AMOUNT = 20.0;
	public static final int NB_THREADS = 1;
	public static final boolean CHECK_BALANCE_ON_EACH_OPERATION = false;

	/**
	 * Minimal balance before going from a sequence of travels to a sequence of
	 * recharges.
	 */
	public static final double MIN_BALANCE_BEFORE_RECHARGE
		= ((5.0 * CardRegistry.MAX_BALANCE) / 100.0);

	/**
	 * Maximal balance before going from a sequence of recharges to a sequence
	 * of travels.
	 */
	public static final double MAX_BALANCE_BEFORE_TRAVEL
		= CardRegistry.MAX_BALANCE
		- ((5.0 * CardRegistry.MAX_BALANCE) / 100.0);

	/**
	 * Waiting time (ms) after a balance inconsticency between the robot and the
	 * service.
	 */
	public static final int BALANCE_RESYNC_WAIT = 10000;

	/**
	 * Waiting time (ms) after an operation error (negative return code).
	 */
	public static final int OPERATION_ERROR_RESYNC_WAIT = 15000;

	/**
	 * Waiting time (ms) after a remote error (RemoteException).
	 */
	public static final int REMOTE_ERROR_RESYNC_WAIT = 20000;

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
	 * @param checkBalanceEachOp true to ask and check the balance after each
	 * operation (of travelling or recharging), false to disable all checks
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

	@Override
	public String toString()
	{
		return "Minimal delay duration: " + getMinDelay() + "ms\n"
			+ "Maximal delay duration: " + getMaxDelay() + "ms\n"
			+ "Minimal amount of an aperation: " + getMinOperationAmount() + "\n"
			+ "Maximal amount of an aperation: " + getMaxOperationAmount() + "\n"
			+ "Number of threads: " + getNumberOfThreads() + "\n"
			+ (getCheckBalanceAfterEachOperation()
				? "Check balance after each recharge or travel operation\n"
				: "Do not check balance");
	}

}
