package ar.edu.itba.pod.mmxivii.sube.client;

import ar.edu.itba.pod.mmxivii.sube.client.items.CreateNewCard;
import ar.edu.itba.pod.mmxivii.sube.client.items.UseExistingCard;
import ar.edu.itba.pod.mmxivii.sube.common.BaseMain;
import ar.edu.itba.pod.mmxivii.sube.common.Card;
import ar.edu.itba.pod.mmxivii.sube.common.CardClient;
import ar.edu.itba.pod.mmxivii.sube.common.Utils;

import javax.annotation.Nonnull;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import static ar.edu.itba.pod.mmxivii.sube.common.Utils.*;
import ar.edu.itba.util.IO;
import ar.edu.itba.util.Menu;
import java.util.Collection;
import java.util.LinkedList;

public class Main extends BaseMain
{

	private CardClient cardClient = null;

	private Main(@Nonnull String[] args) throws NotBoundException
	{
		super(args, DEFAULT_CLIENT_OPTIONS);
		getRegistry();
		cardClient = Utils.lookupObject(CARD_CLIENT_BIND);
	}

	public static void main(@Nonnull String[] args) throws Exception
	{
		final Main main = new Main(args);
		main.run();
	}

	private void run() throws RemoteException
	{
		Collection<Card> cards = new LinkedList<>();

		IO.printlnInfo("Welcome to the SUBE "
			+ "(System with Useless and Bothering Elements) client!");
		IO.println();
		Menu mainMenu = new Menu("Main menu");
		mainMenu.addMenuItem("1", "Obtain a new card",
			new CreateNewCard(cards, cardClient));
		mainMenu.addMenuItem("2", "Use an existing card",
			new UseExistingCard(cards, cardClient));

		mainMenu.run();
	}

}
