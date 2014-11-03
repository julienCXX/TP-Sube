package ar.edu.itba.pod.mmxivii.sube.client.exceptions;

import ar.edu.itba.pod.mmxivii.sube.common.Card;

/**
 * Exception thrown when the server cannot be reached.
 */
public class CommunicationsFailureException extends CardOperationException
{

	/**
	 * Creates the exception.
	 *
	 * @param card the card from which the error happened
	 */
	public CommunicationsFailureException(Card card)
	{
		super("Communication with service failed", card);
	}

}
