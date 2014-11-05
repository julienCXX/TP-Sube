package ar.edu.itba.pod.mmxivii.sube.client.items;

import ar.edu.itba.pod.mmxivii.sube.client.exceptions.CardNotFoundException;
import ar.edu.itba.pod.mmxivii.sube.client.exceptions.CardOperationException;
import ar.edu.itba.pod.mmxivii.sube.client.items.generic.CardMenuItem;
import ar.edu.itba.pod.mmxivii.sube.common.Card;
import ar.edu.itba.pod.mmxivii.sube.common.CardClient;
import ar.edu.itba.pod.mmxivii.sube.common.IO;
import javax.annotation.Nonnull;

/**
 * The menu option handling the fetching of a card's balance.
 */
public class GetBalance extends CardMenuItem
{

	/**
	 * Creates the menu item.
	 *
	 * @param cardClient the remote client object
	 * @param card the card involved in the operation
	 */
	public GetBalance(@Nonnull CardClient cardClient, @Nonnull Card card)
	{
		super(cardClient, card);
	}

	@Override
	public void runItem() throws Exception
	{
		double balance = cardClient.getCardBalance(card.getId());
		if (balance < 0.0)
		{
			try
			{
				checkAndThrowCardOperationError(balance, false);
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
			IO.println("Current balance: " + balance);
		}
	}

}
