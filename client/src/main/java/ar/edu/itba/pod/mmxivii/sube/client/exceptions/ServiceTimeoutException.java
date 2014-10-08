package ar.edu.itba.pod.mmxivii.sube.client.exceptions;

import ar.edu.itba.pod.mmxivii.sube.common.Card;

/**
 * Exception thrown when the communication with the server timed out.
 */
public class ServiceTimeoutException extends CardOperationException
{

	public ServiceTimeoutException(Card card)
	{
		super("Connection to service timed out", card);
	}

}
