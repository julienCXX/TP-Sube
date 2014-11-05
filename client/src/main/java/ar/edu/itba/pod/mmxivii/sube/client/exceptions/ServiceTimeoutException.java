package ar.edu.itba.pod.mmxivii.sube.client.exceptions;

import ar.edu.itba.pod.mmxivii.sube.common.Card;

/**
 * Exception thrown when the communication with the server timed out.
 */
public class ServiceTimeoutException extends CardOperationException
{

	/**
	 * Creates the exception.
	 *
	 * @param card the card from which the error happened
	 */
	public ServiceTimeoutException(Card card)
	{
		super("Connection to service timed out", card);
	}

}
