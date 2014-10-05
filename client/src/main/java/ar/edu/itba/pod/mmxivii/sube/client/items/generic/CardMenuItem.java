package ar.edu.itba.pod.mmxivii.sube.client.items.generic;

import ar.edu.itba.pod.mmxivii.sube.common.Card;
import ar.edu.itba.pod.mmxivii.sube.common.CardClient;
import static ar.edu.itba.pod.mmxivii.sube.common.CardRegistry.CANNOT_PROCESS_REQUEST;
import static ar.edu.itba.pod.mmxivii.sube.common.CardRegistry.CARD_NOT_FOUND;
import static ar.edu.itba.pod.mmxivii.sube.common.CardRegistry.COMMUNICATIONS_FAILURE;
import static ar.edu.itba.pod.mmxivii.sube.common.CardRegistry.MAX_BALANCE;
import static ar.edu.itba.pod.mmxivii.sube.common.CardRegistry.OPERATION_NOT_PERMITTED_BY_BALANCE;
import static ar.edu.itba.pod.mmxivii.sube.common.CardRegistry.SERVICE_TIMEOUT;
import ar.edu.itba.util.IO;

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
	 * @param ret the return value determining the message to be displayed
	 * @param isRecharge true if the operation was a card recharge operation,
	 * false otherwise
	 */
	protected static void printCardOperationError(double ret, boolean isRecharge)
	{
		if (ret >= 0.0)
		{
			IO.printlnInfo("No error, code: " + ret);
			return;
		}
		switch ((int) ret)
		{
			case (int) CARD_NOT_FOUND:
				IO.printlnError("This card does not exist");
				break;
			case (int) CANNOT_PROCESS_REQUEST:
				IO.printlnError("The service cannot process the request");
				break;
			case (int) COMMUNICATIONS_FAILURE:
				IO.printlnError("Communication with service failed");
				break;
			case (int) OPERATION_NOT_PERMITTED_BY_BALANCE:
				if (isRecharge)
				{
					IO.printlnError("The balance cannot be over "
						+ MAX_BALANCE);
				} else
				{
					IO.printlnError("The balance is insufficient to perform "
						+ "this operation");
				}
				break;
			case (int) SERVICE_TIMEOUT:
				IO.printlnError("Connection to service timed out");
				break;
			default:
				IO.printlnError("Unknown error, code: " + ret);
		}
	}

}
