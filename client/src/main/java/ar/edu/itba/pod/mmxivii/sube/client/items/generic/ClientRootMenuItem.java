package ar.edu.itba.pod.mmxivii.sube.client.items.generic;

import ar.edu.itba.pod.mmxivii.sube.client.CardWallet;
import ar.edu.itba.pod.mmxivii.sube.common.CardClient;

/**
 * A menu item using a list of existing cards and the CardClient object.
 */
public abstract class ClientRootMenuItem extends ClientMenuItem
{

	protected CardWallet cards;

	public ClientRootMenuItem(CardWallet cards, CardClient cardClient)
	{
		super(cardClient);
		this.cards = cards;
	}

}
