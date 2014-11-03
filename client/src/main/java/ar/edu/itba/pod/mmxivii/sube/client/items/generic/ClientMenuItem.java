package ar.edu.itba.pod.mmxivii.sube.client.items.generic;

import ar.edu.itba.pod.mmxivii.sube.common.CardClient;
import ar.edu.itba.util.RunnableMenuItem;

/**
 * A menu item using the CardClient object.
 */
public abstract class ClientMenuItem implements RunnableMenuItem
{

	protected CardClient cardClient;

	/**
	 * Creates the menu item.
	 *
	 * @param cardClient the remote client object
	 */
	public ClientMenuItem(CardClient cardClient)
	{
		this.cardClient = cardClient;
	}

}
