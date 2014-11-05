package ar.edu.itba.pod.mmxivii.sube.client.items.generic;

import ar.edu.itba.pod.mmxivii.sube.client.exceptions.CardOperationException;
import ar.edu.itba.pod.mmxivii.sube.client.exceptions.OperationReturnConverter;
import ar.edu.itba.pod.mmxivii.sube.common.Card;
import ar.edu.itba.pod.mmxivii.sube.common.CardClient;
import javax.annotation.Nonnull;

/**
 * A menu item using the CardClient object and a choosen card.
 */
public abstract class CardMenuItem extends ClientMenuItem
{

	protected Card card;

	/**
	 * Creates the menu item.
	 *
	 * @param cardClient the remote client object
	 * @param card the card involved in the operations
	 */
	public CardMenuItem(@Nonnull CardClient cardClient, @Nonnull Card card)
	{
		super(cardClient);
		if (card == null)
		{
			throw new NullPointerException("The card cannot be null");
		}
		this.card = card;
	}

	/**
	 * Checks the return value of a card operation and throws the relevant
	 * exception, if needed.
	 *
	 * @param ret the return value determining the exception to be thrown
	 * @param isRecharge <code>true</code> if the operation was a card recharge
	 * operation, <code>false</code> otherwise
	 * @throws CardOperationException the exception that will be thrown if an
	 * error is detected
	 */
	protected void checkAndThrowCardOperationError(double ret,
		boolean isRecharge)
		throws CardOperationException
	{
		OperationReturnConverter.convertReturn(ret, isRecharge, card);
	}

}
