package ar.edu.itba.pod.mmxivii.sube.client.items.generic;

import ar.edu.itba.pod.mmxivii.sube.common.CardClient;
import ar.edu.itba.util.RunnableMenuItem;
import javax.annotation.Nonnull;

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
	public ClientMenuItem(@Nonnull CardClient cardClient)
	{
		if (cardClient == null)
		{
			throw new NullPointerException("The card client cannot be null");
		}
		this.cardClient = cardClient;
	}

}
