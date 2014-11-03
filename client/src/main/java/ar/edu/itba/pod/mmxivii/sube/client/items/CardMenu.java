package ar.edu.itba.pod.mmxivii.sube.client.items;

import ar.edu.itba.pod.mmxivii.sube.client.items.generic.CardMenuItem;
import ar.edu.itba.pod.mmxivii.sube.common.Card;
import ar.edu.itba.pod.mmxivii.sube.common.CardClient;
import static ar.edu.itba.pod.mmxivii.sube.common.CardRegistry.MAX_BALANCE;
import ar.edu.itba.util.Menu;

/**
 * The menu allowing operations on a card.
 */
public class CardMenu extends CardMenuItem
{

	Menu menu;

	/**
	 * Creates the menu item.
	 *
	 * @param cardClient the remote client object
	 * @param card the card involved in the operations
	 */
	public CardMenu(CardClient cardClient, Card card)
	{
		super(cardClient, card);
		menu = new Menu("Management of " + card.getCardHolder()
			+ "'s card (" + card.getLabel() + ")", false);
		menu.addMenuItem("1", "Check balance",
			new GetBalance(cardClient, card));
		menu.addMenuItem("2", "Travel", new Travel(cardClient, card));
		menu.addMenuItem("3", "Recharge (max balance: " + MAX_BALANCE + ")",
			new Recharge(cardClient, card));
	}

	@Override
	public void runItem() throws Exception
	{
		menu.run();
	}

}
