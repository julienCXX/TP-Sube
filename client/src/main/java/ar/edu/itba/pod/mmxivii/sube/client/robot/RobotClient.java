package ar.edu.itba.pod.mmxivii.sube.client.robot;

import ar.edu.itba.pod.mmxivii.sube.common.CardClient;
import ar.edu.itba.pod.mmxivii.sube.common.IO;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Nonnull;

/**
 * Represents an automated client entity. It creates threads, which send batches
 * of recharge and travel operations, to generate load.
 */
public class RobotClient
{

	private final List<Runnable> robots;
	private final RobotParameters params;

	/**
	 * Creates an automated client, waiting for its execution.
	 *
	 * @param client the remote client object
	 * @param params the robot's parameters
	 */
	public RobotClient(@Nonnull CardClient client,
		@Nonnull RobotParameters params)
	{
		robots = new ArrayList<>();
		this.params = params;
		int nbThreads = params.getNumberOfThreads();
		for (int i = 0; i < nbThreads; i++)
		{
			robots.add(new RobotClientRunnable(client, params));
		}
	}

	/**
	 * Starts the automated client.
	 */
	public void run()
	{
		String line;
		IO.printlnInfo("Starting robot client, press “x <Return>” to stop it");
		IO.printlnInfo("Parameters:\n" + params);
		IO.println();
		ExecutorService executor = Executors.newFixedThreadPool(
			params.getNumberOfThreads());
		for (Runnable r : robots)
		{
			executor.execute(r);
		}
		do
		{
			line = IO.readLine();
		} while (!"x".equals(line));
		executor.shutdownNow();
	}

}
