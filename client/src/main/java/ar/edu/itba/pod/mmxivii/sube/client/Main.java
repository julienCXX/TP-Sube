package ar.edu.itba.pod.mmxivii.sube.client;

import ar.edu.itba.pod.mmxivii.sube.client.items.CreateNewCard;
import ar.edu.itba.pod.mmxivii.sube.client.items.UseExistingCard;
import ar.edu.itba.pod.mmxivii.sube.client.robot.RobotClient;
import ar.edu.itba.pod.mmxivii.sube.client.robot.RobotParameters;
import ar.edu.itba.pod.mmxivii.sube.common.BaseMain;
import ar.edu.itba.pod.mmxivii.sube.common.CardClient;
import ar.edu.itba.pod.mmxivii.sube.common.CardRegistry;
import ar.edu.itba.pod.mmxivii.sube.common.Utils;

import javax.annotation.Nonnull;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import static ar.edu.itba.pod.mmxivii.sube.common.Utils.*;
import ar.edu.itba.util.IO;
import ar.edu.itba.util.Menu;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends BaseMain
{

	private CardClient cardClient = null;

	private final String walletFile = "wallet.data";
	
	private static final String[][] localOptions =
	{
		new String[]
		{
			HOST_O_S, HOST_O_L, TRUE
		},
		new String[]
		{
			PORT_O_S, PORT_O_L, TRUE
		},
		new String[]
		{
			MAX_THREADS_O_S, MAX_THREADS_O_L, TRUE
		},
		new String[]
		{
			"a", "automatic", FALSE
		},
		new String[]
		{
			"md", "min-delay", TRUE,
			"Minimal duration of delay (ms) between 2 operations"
		},
		new String[]
		{
			"Md", "max-delay", TRUE,
			"Maximal duration of delay (ms) between 2 operations"
		},
		new String[]
		{
			"ma", "min-amount", TRUE,
			"Minimal amount of a recharge or travel "
			+ "operation (at least 1)"
		},
		new String[]
		{
			"Ma", "max-amount", TRUE,
			"Maximal amount of a recharge or travel operation "
			+ "(at most " + CardRegistry.MAX_BALANCE + ")"
		},
		new String[]
		{
			"t", "threads", TRUE,
			"The quantity of threads to use (at least 1): "
			+ "1 thread = 1 card and 1 connection"
		},
		new String[]
		{
			"c", "check-every-operation", FALSE,
			"Activates return-code-based balance consistency check, "
			+ "after each recharge or travel (no balance consistancy check "
			+ "would be performed otherwise)"
		}
	};

	private Main(@Nonnull String[] args) throws NotBoundException
	{
		super(args, localOptions);
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
		IO.printlnInfo("Welcome to the SUBE "
			+ "(System with Useless and Bothering Elements) client!");
		IO.println();
		if (cmdLine.hasOption("automatic"))
		{
			runAuto();
			return;
		}
		runManual();
	}

	private void runManual()
	{
		try
		{
			CardWallet cards = new CardWallet();

			IO.printInfo("Loading wallet from " + walletFile);
			cards.loadFromFile(walletFile);
			IO.println(" OK");

			Menu mainMenu = new Menu("Main menu");
			mainMenu.addMenuItem("1", "Obtain a new card",
				new CreateNewCard(cards, cardClient));
			mainMenu.addMenuItem("2", "Use an existing card",
				new UseExistingCard(cards, cardClient));

			try
			{
				mainMenu.run();
			} catch (Exception ex)
			{
				Logger.getLogger(Main.class.getName()).log(Level.SEVERE,
					null, ex);
			}

			IO.printInfo("Saving wallet to " + walletFile);
			cards.saveToFile(walletFile);
			IO.println(" OK");
		} catch (IOException ex)
		{
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void runAuto()
	{
		RobotParameters p = createRobotParametersFromCommandLine();
		RobotClient rc = new RobotClient(cardClient, p);
		rc.run();
	}

	private RobotParameters createRobotParametersFromCommandLine()
	{
		int minDelay = new Integer(cmdLine.getOptionValue("min-delay",
			Integer.toString(RobotParameters.MIN_DELAY))),
			maxDelay = new Integer(cmdLine.getOptionValue("max-delay",
					Integer.toString(RobotParameters.MAX_DELAY))),
			nbThreads = new Integer(cmdLine.getOptionValue("threads",
					Integer.toString(RobotParameters.NB_THREADS)));
		double minAmount = new Double(cmdLine.getOptionValue("min-amount",
			Double.toString(RobotParameters.MIN_OPERATION_AMOUNT))),
			maxAmount = new Double(cmdLine.getOptionValue("max-amount",
					Double.toString(RobotParameters.MAX_OPERATION_AMOUNT)));
		boolean checkEach = cmdLine.hasOption("check-every-operation");
		return new RobotParameters(minDelay, maxDelay, minAmount, maxAmount,
			nbThreads, checkEach);
	}

}
