package ar.edu.itba.pod.mmxivii.sube.client.robot;

import ar.edu.itba.pod.mmxivii.sube.client.exceptions.CardOperationException;
import ar.edu.itba.pod.mmxivii.sube.client.exceptions.OperationReturnConverter;
import ar.edu.itba.pod.mmxivii.sube.client.exceptions.robot.InconsistantBalanceException;
import ar.edu.itba.pod.mmxivii.sube.common.Card;
import ar.edu.itba.pod.mmxivii.sube.common.CardClient;
import ar.edu.itba.pod.mmxivii.sube.common.CardRegistry;
import ar.edu.itba.pod.mmxivii.sube.common.Utils;
import ar.edu.itba.pod.mmxivii.sube.common.IO;
import java.rmi.RemoteException;
import javax.annotation.Nonnull;

/**
 * Represents one thread of the automated client.
 */
public class RobotClientRunnable implements Runnable
{

	private static int threadId = 0;

	private final CardClient client;
	private final RobotParameters params;
	private final String cardOwnerName;
	private Card card;
	private double currentBalance;

	/**
	 * Creates a thread to be used with an automated client.
	 *
	 * @param client the remote client object
	 * @param params the robot's parameters
	 */
	public RobotClientRunnable(@Nonnull CardClient client,
		@Nonnull RobotParameters params)
	{
		this.client = client;
		this.params = params;
		cardOwnerName = "RobotClient" + threadId;
		threadId++;
		currentBalance = 0.0;
	}

	@Override
	public void run()
	{
		IO.printlnInfo("Starting robot client: " + cardOwnerName);
		try
		{
			card = client.newCard(cardOwnerName, "TestCard");
		} catch (RemoteException ex)
		{
			IO.printlnError(headerInfoLine(ex.getMessage()));
			IO.printlnError(headerInfoLine("server was unavailable when "
				+ "creating card, aborting"));
			return;
		}
		try
		{
			mainLoop();
		} catch (InterruptedException ex)
		{
			IO.printlnInfo(headerInfoLine("robot interrupted"));
		}
		IO.printlnInfo(headerInfoLine("end of execution"));
	}

	/**
	 * The thread's main loop, which performs batches of recharges and travels.
	 *
	 * @throws InterruptedException if the thread is stopped while waiting
	 */
	private void mainLoop() throws InterruptedException
	{
		boolean recharge = true; // the card is empty at the beginning
		while (true)
		{
			try
			{
				travelOrRechargeLoop(recharge);
				//checkBalanceConsistency();
				recharge = !recharge;
			} catch (InconsistantBalanceException ex)
			{
				IO.printlnError(headerInfoLine(ex.getMessage()));
				IO.printlnInfo(headerInfoLine(
					"waiting for service resynchronization ("
					+ RobotParameters.BALANCE_RESYNC_WAIT / 1000 + "s)"));
				Thread.sleep(RobotParameters.BALANCE_RESYNC_WAIT);
				reconnectLoop();
			} catch (CardOperationException ex)
			{
				IO.printlnError(headerInfoLine(ex.getMessage()));
				IO.printlnInfo(headerInfoLine(
					"waiting for service resynchronization ("
					+ RobotParameters.OPERATION_ERROR_RESYNC_WAIT / 1000
					+ "s)"));
				Thread.sleep(RobotParameters.OPERATION_ERROR_RESYNC_WAIT);
				reconnectLoop();
			} catch (RemoteException ex)
			{
				IO.printlnError(headerInfoLine(ex.getMessage()));
				IO.printlnInfo(headerInfoLine(
					"waiting for service resynchronization ("
					+ RobotParameters.REMOTE_ERROR_RESYNC_WAIT / 1000
					+ "s)"));
				Thread.sleep(RobotParameters.REMOTE_ERROR_RESYNC_WAIT);
				reconnectLoop();
			}
		}
	}

	/**
	 * Performs a batch of travel or recharge operations, for the current card.
	 *
	 * @param recharge <code>true</code> if all the operations are recharge
	 * operations, <code>false</code> if all the operations are travel
	 * operations
	 * @throws RemoteException if the connection with the balancer is broken
	 * @throws InterruptedException if the thread is stopped while waiting
	 * @throws InconsistantBalanceException if the locally computed balance
	 * differs from one operation's return value
	 * @throws CardOperationException if the service has issues (various types)
	 */
	private void travelOrRechargeLoop(boolean recharge) throws RemoteException,
		InterruptedException,
		InconsistantBalanceException,
		CardOperationException
	{
		double amount, ret;
		if (recharge)
		{
			IO.println(headerInfoLine("recharge loop"));
			while (currentBalance < RobotParameters.MAX_BALANCE_BEFORE_TRAVEL)
			{
				delay();
				amount = genRechargeAmount();
				//IO.println("Balance: " + currentBalance);
				//IO.println("Amount: " + amount);
				ret = client.recharge(card.getId(), "Charging", amount);
				OperationReturnConverter.convertReturn(ret, recharge, card);
				currentBalance += amount;
				if (params.getCheckBalanceAfterEachOperation())
				{
					checkBalanceConsistency(ret);
				}
			}
			return;
		}

		IO.println(headerInfoLine("travel loop"));
		while (currentBalance > RobotParameters.MIN_BALANCE_BEFORE_RECHARGE)
		{
			delay();
			amount = genTravelAmount();

			//IO.println("Balance: " + currentBalance);
			//IO.println("Amount: " + amount);
			ret = client.travel(card.getId(), "Travelling", amount);
			OperationReturnConverter.convertReturn(ret, recharge, card);
			currentBalance -= amount;
			if (params.getCheckBalanceAfterEachOperation())
			{
				checkBalanceConsistency(ret);
			}
		}
	}

	/**
	 * Tries to reconnect at a regular interval, after a connection error from
	 * the balancer or an inconsistent balance error. Will wait again, while the
	 * balance can't synchronize well.
	 *
	 * @throws InterruptedException if the thread is stopped while waiting
	 */
	private void reconnectLoop() throws InterruptedException
	{
		boolean loop = true;
		double ret;
		while (loop)
		{
			try
			{
				ret = client.getCardBalance(card.getId());
				OperationReturnConverter.convertReturn(ret, false, card);
				IO.printlnInfo(headerInfoLine(
					"resynchronized balance: local (old) = "
					+ currentBalance + ", remote (new) = " + ret));
				currentBalance = ret;
				loop = false;
			} catch (RemoteException ex)
			{
				IO.printlnError(headerInfoLine(
					"Oops, service is unavailable, waiting again ("
					+ RobotParameters.REMOTE_ERROR_RESYNC_WAIT / 1000 + "s)"));
				Thread.sleep(RobotParameters.REMOTE_ERROR_RESYNC_WAIT);
			} catch (CardOperationException ex)
			{
				IO.printlnError(ex.getMessage());
				IO.printlnError(headerInfoLine(
					"Waiting again ("
					+ RobotParameters.REMOTE_ERROR_RESYNC_WAIT / 1000 + "s)"));
				Thread.sleep(RobotParameters.REMOTE_ERROR_RESYNC_WAIT);
			}
		}
	}

	/**
	 * Generates a valid amount (according to the current balance and
	 * parameters) to be used in a travel operation.
	 *
	 * @return the generated amount
	 */
	private double genTravelAmount()
	{
		double amount = Utils.randomDouble(params.getMinOperationAmount(),
			params.getMaxOperationAmount());
		amount = Math.rint(amount);
		if (currentBalance - amount < 0.0)
		{
			// prevents invalid amount errors
			return currentBalance;
		}
		return amount;
	}

	/**
	 * Generates a valid amount (according to the current balance and
	 * parameters) to be used in a recharge operation.
	 *
	 * @return the generated amount
	 */
	private double genRechargeAmount()
	{
		double amount = Utils.randomDouble(params.getMinOperationAmount(),
			params.getMaxOperationAmount());
		amount = Math.rint(amount);
		if (currentBalance + amount > CardRegistry.MAX_BALANCE)
		{
			// prevents invalid amount errors
			return CardRegistry.MAX_BALANCE - currentBalance;
		}
		return amount;
	}

	/**
	 * Interrupts this thread for a random duration (according to parameters).
	 *
	 * @throws InterruptedException if the thread is stopped while waiting
	 */
	private void delay() throws InterruptedException
	{
		int delay = Utils.randomInt(params.getMinDelay(),
			params.getMaxDelay());
		Thread.sleep(delay);
	}

	/**
	 * Checks if the effective balance (asked from server) is consistant with
	 * the theorical (computed) one. Throws the relevant exception if required.
	 * Not used because of the direct use of the server (without the service).
	 *
	 * @throws RemoteException if the connection with the server is broken
	 * @throws InconsistantBalanceException if the balance is inconsistant
	 */
	private void checkBalanceConsistency() throws RemoteException,
		InconsistantBalanceException
	{
		double remoteBalance = client.getCardBalance(card.getId());
		if (remoteBalance != currentBalance)
		{
			throw new InconsistantBalanceException(currentBalance,
				remoteBalance, card);
		}
	}

	/**
	 * Checks if the effective balance (from operation return code) is
	 * consistant with the theorical (computed) one. Throws the relevant
	 * exception if required.
	 *
	 * @param resultingBalance the balance obtained from the last operation (non
	 * negative return value)
	 * @throws RemoteException if the connection with the balancer is broken
	 * @throws InconsistantBalanceException if the balance is inconsistant
	 */
	private void checkBalanceConsistency(double resultingBalance)
		throws RemoteException,
		InconsistantBalanceException
	{
		if (resultingBalance != currentBalance)
		{
			throw new InconsistantBalanceException(currentBalance,
				resultingBalance, card);
		}
	}

	/**
	 * Prefixes a message with the name of this thread.
	 *
	 * @param message the message which the name will prepended
	 * @return the message with the robot's name prepended
	 */
	private String headerInfoLine(String message)
	{
		return cardOwnerName + ": " + message;
	}

}
