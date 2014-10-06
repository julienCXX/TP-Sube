package ar.edu.itba.pod.mmxivii.sube.client.items;

import ar.edu.itba.pod.mmxivii.sube.client.CardWallet;
import ar.edu.itba.pod.mmxivii.sube.client.items.generic.ClientRootMenuItem;
import ar.edu.itba.pod.mmxivii.sube.common.Card;
import ar.edu.itba.pod.mmxivii.sube.common.CardClient;
import ar.edu.itba.util.IO;

/**
 * The menu option handling the creation of a new card.
 */
public class CreateNewCard extends ClientRootMenuItem
{

	public CreateNewCard(CardWallet cards, CardClient cardClient)
	{
		super(cards, cardClient);
	}

	@Override
	public void runItem() throws Exception
	{
		String owner, label;
		Card card;
		IO.print("Enter the card owner's name: ");
		owner = IO.readLine();
		IO.print("Enter a label: ");
		label = IO.readLine();
		card = cardClient.newCard(owner, label);
		IO.printlnInfo("Card successfully created");
		IO.println();
		cards.add(card);
		new CardMenu(cardClient, card).runItem();
	}

}
