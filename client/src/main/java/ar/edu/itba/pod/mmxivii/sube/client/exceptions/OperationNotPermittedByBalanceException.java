package ar.edu.itba.pod.mmxivii.sube.client.exceptions;

import ar.edu.itba.pod.mmxivii.sube.common.Card;
import static ar.edu.itba.pod.mmxivii.sube.common.CardRegistry.MAX_BALANCE;

/**
 * Exception thrown when the card's balance does not allow an operation or the
 * recharge would exceed the maximum allowed balance.
 */
public class OperationNotPermittedByBalanceException
	extends CardOperationException
{

	public OperationNotPermittedByBalanceException(Card card,
		boolean isRecharge)
	{
		super(isRecharge ? "The balance cannot be over " + MAX_BALANCE
			: "The balance is insufficient to perform this operation", card);
	}

}
