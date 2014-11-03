package ar.edu.itba.pod.mmxivii.sube.client.robot;

import ar.edu.itba.pod.mmxivii.sube.client.exceptions.CardOperationException;
import ar.edu.itba.pod.mmxivii.sube.client.exceptions.OperationReturnConverter;
import ar.edu.itba.pod.mmxivii.sube.client.exceptions.robot.InconsistantBalanceException;
import ar.edu.itba.pod.mmxivii.sube.common.Card;
import ar.edu.itba.pod.mmxivii.sube.common.CardClient;
import ar.edu.itba.pod.mmxivii.sube.common.CardRegistry;
import ar.edu.itba.pod.mmxivii.sube.common.Utils;
import ar.edu.itba.util.IO;
import java.rmi.RemoteException;
import javax.annotation.Nonnull;

/**
 *
 */
public class RobotClientRunnable implements Runnable
{

	private static int threadId = 0;

	private final CardClient client;
	private final RobotParameters params;
	private final String cardOwnerName;
	private Card card;
	private double currentBalance;

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

	private void delay() throws InterruptedException
	{
		int delay = Utils.randomInt(params.getMinDelay(),
			params.getMaxDelay());
		Thread.sleep(delay);
	}

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

	private String headerInfoLine(String message)
	{
		return cardOwnerName + ": " + message;
	}

}
