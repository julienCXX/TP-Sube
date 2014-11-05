package ar.edu.itba.pod.mmxivii.sube.client.items;

import ar.edu.itba.pod.mmxivii.sube.client.exceptions.CardNotFoundException;
import ar.edu.itba.pod.mmxivii.sube.client.exceptions.CardOperationException;
import ar.edu.itba.pod.mmxivii.sube.client.items.generic.CardMenuItem;
import ar.edu.itba.pod.mmxivii.sube.common.Card;
import ar.edu.itba.pod.mmxivii.sube.common.CardClient;
import ar.edu.itba.util.IO;

/**
 * The menu option handling the credit adding onto a card.
 */
public class Recharge extends CardMenuItem
{

	/**
	 * Creates the menu item.
	 *
	 * @param cardClient the remote client object
	 * @param card the card involved in the operation
	 */
	public Recharge(CardClient cardClient, Card card)
	{
		super(cardClient, card);
	}

	@Override
	public void runItem() throws Exception
	{
		double amount, newBalance;
		String description;
		IO.println("Choose the amount of the recharge");
		amount = IO.readDouble();
		IO.println("Please put a description of the recharge: ");
		description = IO.readLine();
		try
		{
			newBalance = cardClient.recharge(card.getId(), description, amount);
			if (newBalance < 0.0)
			{
				try
				{
					checkAndThrowCardOperationError(newBalance, true);
				} catch (CardOperationException coe)
				{
					if (coe instanceof CardNotFoundException)
					{
						throw coe;
					}
					IO.printlnError(coe.getMessage());
				}
			} else
			{
				IO.println("Recharged successfully. New balance: "
					+ newBalance);
			}
		} catch (IllegalArgumentException iae)
		{
			IO.printlnError(iae.getMessage()); // recharge too big
		}
	}

}
