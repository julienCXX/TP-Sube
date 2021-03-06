package ar.edu.itba.pod.mmxivii.sube.client.items.generic;

import ar.edu.itba.pod.mmxivii.sube.client.CardWallet;
import ar.edu.itba.pod.mmxivii.sube.common.CardClient;
import javax.annotation.Nonnull;

/**
 * A menu item using a list of existing cards and the CardClient object.
 */
public abstract class ClientRootMenuItem extends ClientMenuItem
{

	protected CardWallet cards;

	/**
	 * Creates the menu item.
	 *
	 * @param cards the wallet containing the cards usable in this menu
	 * @param cardClient the remote client object
	 */
	public ClientRootMenuItem(@Nonnull CardWallet cards,
		@Nonnull CardClient cardClient)
	{
		super(cardClient);
		if (cards == null)
		{
			throw new NullPointerException("The wallet cannot be null");
		}
		this.cards = cards;
	}

}
