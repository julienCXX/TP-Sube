package ar.edu.itba.pod.mmxivii.sube.client.items;

import ar.edu.itba.pod.mmxivii.sube.client.CardWallet;
import ar.edu.itba.pod.mmxivii.sube.client.exceptions.CardNotFoundException;
import ar.edu.itba.pod.mmxivii.sube.client.items.generic.ClientRootMenuItem;
import ar.edu.itba.pod.mmxivii.sube.common.Card;
import ar.edu.itba.pod.mmxivii.sube.common.CardClient;
import ar.edu.itba.util.IO;
import javax.annotation.Nonnull;

/**
 * The menu option handling the creation of a new card.
 */
public class CreateNewCard extends ClientRootMenuItem
{

	/**
	 * Creates the menu item.
	 *
	 * @param cards the wallet containing to which the newly card will be added
	 * @param cardClient the remote client object
	 */
	public CreateNewCard(@Nonnull CardWallet cards,
		@Nonnull CardClient cardClient)
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
		try
		{
			card = cardClient.newCard(owner, label);
		} catch (IllegalArgumentException iae)
		{
			// bad characters in string
			IO.printlnError(iae.getMessage());
			return;
		}

		IO.printlnInfo("Card successfully created");
		IO.println();
		cards.add(card);
		try
		{
			new CardMenu(cardClient, card).runItem();
		} catch (CardNotFoundException cnfe)
		{
			// only if the server refuses the card creation
			IO.printlnError(cnfe.getMessage());
			IO.print("Do you want to remove this card from the wallet? ");
			if (IO.readYesNo(IO.DefaultYesNo.YES))
			{
				cards.remove(cnfe.getCard());
			}
		}
	}

}
