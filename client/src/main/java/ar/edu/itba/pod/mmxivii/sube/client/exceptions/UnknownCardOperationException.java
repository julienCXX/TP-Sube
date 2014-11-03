package ar.edu.itba.pod.mmxivii.sube.client.exceptions;

import ar.edu.itba.pod.mmxivii.sube.common.Card;

/**
 * Exception thrown when an unknown error code was returned.
 */
public class UnknownCardOperationException extends CardOperationException
{

	/**
	 * Creates the exception.
	 *
	 * @param card the card from which the error happened
	 * @param retCode the code returned by the faulty operation
	 */
	public UnknownCardOperationException(Card card, double retCode)
	{
		super("Unknown error, code: " + retCode, card);
	}

}
