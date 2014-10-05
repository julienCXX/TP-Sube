package ar.edu.itba.pod.mmxivii.sube.client.items.generic;

import ar.edu.itba.pod.mmxivii.sube.common.Card;
import ar.edu.itba.pod.mmxivii.sube.common.CardClient;
import java.util.Collection;

/**
 * A menu item using a list of existing cards and the CardClient object.
 */
public abstract class ClientRootMenuItem extends ClientMenuItem
{

	protected Collection<Card> cards;

	public ClientRootMenuItem(Collection<Card> cards, CardClient cardClient)
	{
		super(cardClient);
		this.cards = cards;
	}

}
