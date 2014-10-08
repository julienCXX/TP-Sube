package ar.edu.itba.pod.mmxivii.sube.client.items.generic;

import ar.edu.itba.pod.mmxivii.sube.client.exceptions.CannotProcessRequestException;
import ar.edu.itba.pod.mmxivii.sube.client.exceptions.CardNotFoundException;
import ar.edu.itba.pod.mmxivii.sube.client.exceptions.CardOperationException;
import ar.edu.itba.pod.mmxivii.sube.client.exceptions.CommunicationsFailureException;
import ar.edu.itba.pod.mmxivii.sube.client.exceptions.OperationNotPermittedByBalanceException;
import ar.edu.itba.pod.mmxivii.sube.client.exceptions.ServiceTimeoutException;
import ar.edu.itba.pod.mmxivii.sube.client.exceptions.UnknownCardOperationException;
import ar.edu.itba.pod.mmxivii.sube.common.Card;
import ar.edu.itba.pod.mmxivii.sube.common.CardClient;
import static ar.edu.itba.pod.mmxivii.sube.common.CardRegistry.CANNOT_PROCESS_REQUEST;
import static ar.edu.itba.pod.mmxivii.sube.common.CardRegistry.CARD_NOT_FOUND;
import static ar.edu.itba.pod.mmxivii.sube.common.CardRegistry.COMMUNICATIONS_FAILURE;
import static ar.edu.itba.pod.mmxivii.sube.common.CardRegistry.OPERATION_NOT_PERMITTED_BY_BALANCE;
import static ar.edu.itba.pod.mmxivii.sube.common.CardRegistry.SERVICE_TIMEOUT;

/**
 * A menu item using the CardClient object and a choosen card.
 */
public abstract class CardMenuItem extends ClientMenuItem
{

	protected Card card;

	public CardMenuItem(CardClient cardClient, Card card)
	{
		super(cardClient);
		this.card = card;
	}

	/**
	 * Prints a meaningful error message, depending on a return value of a card
	 * operation.
	 *
	 * @param ret the return value determining the exception to be thrown
	 * @param isRecharge true if the operation was a card recharge operation,
	 * false otherwise
	 * @throws
	 * ar.edu.itba.pod.mmxivii.sube.client.exceptions.CardOperationException the
	 * exception that will be thrown if an error is detected
	 */
	protected void checkAndThrowCardOperationError(double ret,
		boolean isRecharge)
		throws CardOperationException
	{
		switch ((int) ret)
		{
			case (int) CARD_NOT_FOUND:
				throw new CardNotFoundException(card);
			case (int) CANNOT_PROCESS_REQUEST:
				throw new CannotProcessRequestException(card);
			case (int) COMMUNICATIONS_FAILURE:
				throw new CommunicationsFailureException(card);
			case (int) OPERATION_NOT_PERMITTED_BY_BALANCE:
				throw new OperationNotPermittedByBalanceException(card,
					isRecharge);
			case (int) SERVICE_TIMEOUT:
				throw new ServiceTimeoutException(card);
			default:
				throw new UnknownCardOperationException(card, ret);
		}
	}

}
