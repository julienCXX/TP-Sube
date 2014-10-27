package ar.edu.itba.pod.mmxivii.sube.balancer;

import ar.edu.itba.pod.mmxivii.sube.common.BaseMain;
import ar.edu.itba.pod.mmxivii.sube.common.CardRegistry;
import ar.edu.itba.pod.mmxivii.sube.common.Utils;

import javax.annotation.Nonnull;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;

import static ar.edu.itba.pod.mmxivii.sube.common.Utils.*;

public class Main extends BaseMain {

	private static String SYNCHRONIZE = "sync";
	private static Main main = null;
	private CardServiceRegistryImpl cardServiceRegistry;

	private Main(@Nonnull String[] args) throws RemoteException,
			NotBoundException {
		super(args, DEFAULT_CLIENT_OPTIONS);
		getRegistryWithParamenters();
		System.out.println("s1");
		setDelay();
		System.out.println("s2");
		try{
            createRegistryWithParamenters();
		}catch(Exception e){
			
		}
		boolean waiting = true;
		CardRegistry cardRegistry = null;
		while (waiting) {
			try {
				cardRegistry = Utils.lookupObject(CARD_REGISTRY_BIND);
				waiting = false;
			} catch (Exception e) {
				waiting = true;
			}
		}
		System.out.println("s3");
		cardServiceRegistry = new CardServiceRegistryImpl();
		bindObject(CARD_SERVICE_REGISTRY_BIND, cardServiceRegistry);

		final CardClientImpl cardClient = new CardClientImpl(cardRegistry,
				cardServiceRegistry);
		bindObject(CARD_CLIENT_BIND, cardClient);
	}

	public static void main(@Nonnull String[] args) throws Exception {
		main = new Main(args);
		main.run();
	}

	private void run() {
		System.out.println("Starting Balancer!");
		Executors.newSingleThreadExecutor().execute(
				new SynchronizedThread(cardServiceRegistry));
		final Scanner scan = new Scanner(System.in);
		String line;
		do {
			line = scan.next();
			if (line.equals(Main.SYNCHRONIZE)) {

				try {

					ConcurrentHashMap<UID, Double> map = main.cardServiceRegistry
							.getCoordinator().synchronizeToServer();
					System.out.println("Usuarios");
					for (Entry<UID, Double> e : map.entrySet()) {
						System.out.println(e.getKey()+" Monto: "+e.getValue() );
					}
				} catch (RemoteException e) {
					System.out.println("Error - Reintente");
				}
			}
			System.out.println("Balancer running");
		} while (!"x".equals(line));
		shutdown();
		System.out.println("Balancer exit.");
		System.exit(0);

	}

	public static void shutdown() {
		main.unbindObject(CARD_SERVICE_REGISTRY_BIND);
		main.unbindObject(CARD_CLIENT_BIND);
	}

	private static class SynchronizedThread implements Runnable {

		private CardServiceRegistryImpl serviceRegistry;
		private static long TIME_TO_WAIT = 90000; // un minuto y medio

		public SynchronizedThread(CardServiceRegistryImpl service) {
			this.serviceRegistry = service;

		}

		public void run() {
			while (true) {
				try {
					Thread.sleep(TIME_TO_WAIT);
				} catch (InterruptedException e) {
					// e.printStackTrace();
				}
				try {
					serviceRegistry.getCoordinator().synchronizeToServer();
					System.out.println("Cache sincronizado con exito");
				} catch (RemoteException e) {
					// e.printStackTrace();
				}
			}
		}
	}
}
