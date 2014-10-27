package ar.edu.itba.pod.mmxivii.sube.client.exceptions.robot;

import ar.edu.itba.pod.mmxivii.sube.client.exceptions.CardOperationException;
import ar.edu.itba.pod.mmxivii.sube.common.Card;

/**
 * Exception thrown when the theoric card's balance does not match the one
 * provided by the service.
 */
public class InconsistantBalanceException extends CardOperationException
{

	/**
	 * Creates the exception.
	 *
	 * @param computedB the theoric balance, computed by the robot
	 * @param providedB the balance provided by the getBalance operation
	 * @param card the card from which the error happened
	 */
	public InconsistantBalanceException(double computedB,
		double providedB, Card card)
	{
		super("Inconsistant balance in " + card.getCardHolder() + "'s card ("
			+ card.getLabel() + ")\n"
			+ "computed from robot: " + computedB + ", got from service:"
			+ providedB, card);
	}

}
