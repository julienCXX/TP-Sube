package ar.edu.itba.pod.mmxivii.sube.client.items;

import ar.edu.itba.pod.mmxivii.sube.client.exceptions.CardNotFoundException;
import ar.edu.itba.pod.mmxivii.sube.client.CardWallet;
import ar.edu.itba.pod.mmxivii.sube.client.items.generic.ClientRootMenuItem;
import ar.edu.itba.pod.mmxivii.sube.common.Card;
import ar.edu.itba.pod.mmxivii.sube.common.CardClient;
import ar.edu.itba.util.IO;
import ar.edu.itba.util.Menu;
import javax.annotation.Nonnull;

/**
 * The menu option handling the use of an existing card.
 */
public class UseExistingCard extends ClientRootMenuItem
{

	/**
	 * Creates the menu item.
	 *
	 * @param cards the wallet containing the cards usable in this menu
	 * @param cardClient the remote client object
	 */
	public UseExistingCard(@Nonnull CardWallet cards,
		@Nonnull CardClient cardClient)
	{
		super(cards, cardClient);
	}

	@Override
	public void runItem() throws Exception
	{
		Menu menu = new Menu("Choose the card you want to use", false);
		int i = 1;
		for (Card card : cards)
		{
			menu.addMenuItem(i + "", "owner: " + card.getCardHolder()
				+ ", label: " + card.getLabel(),
				new CardMenu(cardClient, card));
			i++;
		}
		try
		{
			menu.run();
		} catch (CardNotFoundException cnfe)
		{
			IO.printlnError(cnfe.getMessage());
			IO.print("Do you want to remove this card from the wallet? ");
			if (IO.readYesNo(IO.DefaultYesNo.YES))
			{
				cards.remove(cnfe.getCard());
			}
		}
	}

}
