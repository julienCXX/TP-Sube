package ar.edu.itba.pod.mmxivii.sube.client.exceptions;

import ar.edu.itba.pod.mmxivii.sube.common.Card;
import static ar.edu.itba.pod.mmxivii.sube.common.CardRegistry.CANNOT_PROCESS_REQUEST;
import static ar.edu.itba.pod.mmxivii.sube.common.CardRegistry.CARD_NOT_FOUND;
import static ar.edu.itba.pod.mmxivii.sube.common.CardRegistry.COMMUNICATIONS_FAILURE;
import static ar.edu.itba.pod.mmxivii.sube.common.CardRegistry.OPERATION_NOT_PERMITTED_BY_BALANCE;
import static ar.edu.itba.pod.mmxivii.sube.common.CardRegistry.SERVICE_TIMEOUT;

/**
 * Converts a return code to a CardOperationException.
 */
public class OperationReturnConverter
{

	private OperationReturnConverter()
	{
	}

	/**
	 * Check the return value of a card operation and throws the relevant
	 * exception, if needed.
	 *
	 * @param ret the return value determining the exception to be thrown
	 * @param isRecharge <code>true</code> if the operation was a card recharge
	 * operation, <code>false</code> otherwise
	 * @param card the card concerned by the operation
	 * @throws CardOperationException the exception that will be thrown if an
	 * error is detected
	 */
	public static void convertReturn(double ret, boolean isRecharge, Card card)
		throws CardOperationException
	{
		if (ret >= 0.0)
		{
			return;
		}
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
