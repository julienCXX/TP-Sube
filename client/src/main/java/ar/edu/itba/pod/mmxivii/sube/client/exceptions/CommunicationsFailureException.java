package ar.edu.itba.pod.mmxivii.sube.client.exceptions;

import ar.edu.itba.pod.mmxivii.sube.common.Card;

/**
 * Exception thrown when the server cannot be reached.
 */
public class CommunicationsFailureException extends CardOperationException
{

	public CommunicationsFailureException(Card card)
	{
		super("Communication with service failed", card);
	}

}
