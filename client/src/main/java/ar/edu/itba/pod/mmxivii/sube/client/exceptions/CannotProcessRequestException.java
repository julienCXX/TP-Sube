package ar.edu.itba.pod.mmxivii.sube.client.exceptions;

import ar.edu.itba.pod.mmxivii.sube.common.Card;

/**
 * Exception thrown when the server cannot process a request.
 */
public class CannotProcessRequestException extends CardOperationException
{

	/**
	 * Creates the exception.
	 *
	 * @param card the card from which the error happened
	 */
	public CannotProcessRequestException(Card card)
	{
		super("The service cannot process the request", card);
	}

}
