package ar.edu.itba.pod.mmxivii.sube.client.items;

import ar.edu.itba.pod.mmxivii.sube.client.items.generic.CardMenuItem;
import ar.edu.itba.pod.mmxivii.sube.common.Card;
import ar.edu.itba.pod.mmxivii.sube.common.CardClient;
import ar.edu.itba.util.IO;

/**
 * The menu option handling the fetching of a card's balance.
 */
public class GetBalance extends CardMenuItem
{

	public GetBalance(CardClient cardClient, Card card)
	{
		super(cardClient, card);
	}

	@Override
	public void runItem() throws Exception
	{
		double balance = cardClient.getCardBalance(card.getId());
		if (balance < 0.0)
		{
			printCardOperationError(balance, false);
		} else
		{
			IO.println("Current balance: " + balance);
		}
	}

}
