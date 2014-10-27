package ar.edu.itba.pod.mmxivii.sube.client.robot;

import ar.edu.itba.pod.mmxivii.sube.common.Card;
import ar.edu.itba.pod.mmxivii.sube.common.CardClient;
import ar.edu.itba.pod.mmxivii.sube.common.CardRegistry;
import ar.edu.itba.pod.mmxivii.sube.common.Utils;
import ar.edu.itba.util.IO;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
			mainLoop();
		} catch (RemoteException ex)
		{
			Logger.getLogger(RobotClientRunnable.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InterruptedException ex)
		{
			IO.printlnInfo("Robot interrupted");
		}
		IO.printlnInfo("End of robot client: " + cardOwnerName);
	}

	private void mainLoop() throws RemoteException, InterruptedException
	{
		boolean recharge = true; // the card is empty at the beginning
		while (true)
		{
			travelOrRechargeLoop(recharge);
			checkBalanceConsistency();
			recharge = !recharge;
		}
	}

	private void travelOrRechargeLoop(boolean recharge) throws RemoteException,
		InterruptedException
	{
		double amount;
		if (recharge)
		{
			IO.println(headerInfoLine("recharge loop"));
			while (currentBalance < CardRegistry.MAX_BALANCE - 1.0)
			{
				delay();
				amount = genRechargeAmount();
				client.recharge(card.getId(), "Charging" + amount, amount);
				currentBalance += amount;
				if (params.getCheckBalanceAfterEachOperation())
				{
					checkBalanceConsistency();
				}
			}
			return;
		}

		IO.println(headerInfoLine("travel loop"));
		while (currentBalance > 1.0)
		{
			delay();
			amount = genTravelAmount();
			client.travel(card.getId(), "Travelling" + amount, amount);
			currentBalance -= amount;
			if (params.getCheckBalanceAfterEachOperation())
			{
				checkBalanceConsistency();
			}
		}
	}

	private double genTravelAmount()
	{
		double amount = Utils.randomDouble(params.getMinOperationAmount(),
			params.getMaxOperationAmount());
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

	private void checkBalanceConsistency() throws RemoteException
	{
		double remoteBalance = client.getCardBalance(card.getId());
		if (remoteBalance != currentBalance)
		{
			// throw exception
		}
	}

	private String headerInfoLine(String message)
	{
		return cardOwnerName + ": " + message;
	}

}
