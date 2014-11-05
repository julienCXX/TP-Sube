package ar.edu.itba.pod.mmxivii.sube.client;

import ar.edu.itba.pod.mmxivii.sube.common.Card;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;

/**
 * The wallet containing a client's cards.
 */
public class CardWallet implements Iterable<Card>
{

	private List<Card> cards;

	/**
	 * Creates a new wallet.
	 */
	public CardWallet()
	{
		cards = new LinkedList<>();
	}

	/**
	 * Returns the card stored at the specified index.
	 *
	 * @param i the index form which retrieve the card
	 * @return the card
	 */
	public Card get(int i)
	{
		return cards.get(i);
	}

	/**
	 * Adds a card to the wallet.
	 *
	 * @param card the card to be added
	 */
	public void add(@Nonnull Card card)
	{
		if (card == null)
		{
			throw new NullPointerException("The card cannot be null");
		}
		cards.add(card);
	}

	/**
	 * Removes a card form the wallet.
	 *
	 * @param card the card to be removed
	 */
	public void remove(Card card)
	{
		cards.remove(card);
	}

	/**
	 * Loads a wallet from a file, replacing the wallet's content. If the file
	 * does not exists, nothing will be done.
	 *
	 * @param fileName the file from which the wallet should be loaded
	 * @throws IOException if the file can't be read, or is corrupted
	 */
	public void loadFromFile(String fileName) throws IOException
	{
		final File file = new File(fileName);
		if (!file.exists())
		{
			return;
		}
		final FileInputStream fin = new FileInputStream(fileName);
		final ObjectInputStream oos = new ObjectInputStream(fin);
		try
		{
			cards = (List<Card>) oos.readObject();
		} catch (ClassNotFoundException ex)
		{
			Logger.getLogger(CardWallet.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	/**
	 * Saves the wallet to a file, replacing its content.
	 *
	 * @param fileName the file onto the wallet should be written
	 * @throws FileNotFoundException
	 * @throws IOException if the file cannot be written
	 */
	public void saveToFile(String fileName) throws FileNotFoundException, IOException
	{
		final FileOutputStream fout = new FileOutputStream(fileName);
		final ObjectOutputStream oos = new ObjectOutputStream(fout);
		oos.writeObject(cards);
	}

	@Override
	public Iterator<Card> iterator()
	{
		return cards.iterator();
	}

}
