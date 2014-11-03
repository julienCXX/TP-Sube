package ar.edu.itba.pod.mmxivii.sube.client.exceptions;

import ar.edu.itba.pod.mmxivii.sube.common.Card;

/**
 * Exception thrown when a card is not found on a server.
 */
public class CardNotFoundException extends CardOperationException
{

	/**
	 * Creates the exception.
	 *
	 * @param card the card from which the error happened
	 */
	public CardNotFoundException(Card card)
	{
		super("This card does not exist", card);
	}

}
