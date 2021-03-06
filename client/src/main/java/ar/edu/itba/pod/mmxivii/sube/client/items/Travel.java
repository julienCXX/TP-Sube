package ar.edu.itba.pod.mmxivii.sube.client.items;

import ar.edu.itba.pod.mmxivii.sube.client.exceptions.CardNotFoundException;
import ar.edu.itba.pod.mmxivii.sube.client.exceptions.CardOperationException;
import ar.edu.itba.pod.mmxivii.sube.client.items.generic.CardMenuItem;
import ar.edu.itba.pod.mmxivii.sube.common.Card;
import ar.edu.itba.pod.mmxivii.sube.common.CardClient;
import ar.edu.itba.pod.mmxivii.sube.common.IO;
import javax.annotation.Nonnull;

/**
 * The menu option handling the credit removing from a card, when thu user
 * travels.
 */
public class Travel extends CardMenuItem
{

	/**
	 * Creates the menu item.
	 *
	 * @param cardClient the remote client object
	 * @param card the card involved in the operation
	 */
	public Travel(@Nonnull CardClient cardClient, @Nonnull Card card)
	{
		super(cardClient, card);
	}

	@Override
	public void runItem() throws Exception
	{
		double amount, newBalance;
		String description;
		IO.println("Choose the amount of the fare");
		amount = IO.readDouble();
		IO.println("Please put a description of the travel: ");
		description = IO.readLine();
		try
		{
			newBalance = cardClient.travel(card.getId(), description, amount);
			if (newBalance < 0.0)
			{
				try
				{
					checkAndThrowCardOperationError(newBalance, false);
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
				IO.println("Traveled successfully. New balance: " + newBalance);
			}
		} catch (IllegalArgumentException iae)
		{
			IO.printlnError(iae.getMessage()); // recharge too big
		}
	}

}
