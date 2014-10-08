package ar.edu.itba.pod.mmxivii.sube.client.exceptions;

import ar.edu.itba.pod.mmxivii.sube.common.Card;

/**
 * Exception thrown when an error was created from a CardClient instance.
 */
public abstract class CardOperationException extends Exception
{

	private final Card card;

	/**
	 * Creates the exception.
	 *
	 * @param message a message describing the error
	 * @param card the card from which the error happened
	 */
	public CardOperationException(String message, Card card)
	{
		super(message);
		this.card = card;
	}

	/**
	 * Returns the card that caused this exception.
	 *
	 * @return
	 */
	public Card getCard()
	{
		return card;
	}
}
