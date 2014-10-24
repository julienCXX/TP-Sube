package ar.edu.itba.pod.mmxivii.sube.balancer;

import ar.edu.itba.pod.mmxivii.sube.common.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static ar.edu.itba.pod.mmxivii.sube.common.Utils.CARD_REGISTRY_BIND;
import static ar.edu.itba.pod.mmxivii.sube.common.Utils.checkNotNull;
import static ar.edu.itba.pod.mmxivii.sube.common.Utils.delay;

public class CardClientImpl extends UnicastRemoteObject implements CardClient {
	private static final long serialVersionUID = 3498345765116694167L;
	private static final long TIME_OUT = 1000; // en milisegundos
	private CardRegistry cardRegistry;
	private final CardServiceRegistryImpl cardServiceRegistry;

	public CardClientImpl(@Nonnull CardRegistry cardRegistry,
			@Nonnull CardServiceRegistryImpl cardServiceRegistry)
			throws RemoteException {
		super();
		this.cardRegistry = cardRegistry;
		this.cardServiceRegistry = cardServiceRegistry;
	}

	@Nonnull
	@Override
	public Card newCard(@Nonnull String cardHolder, @Nonnull String label)
			throws RemoteException {
		delay();
		try {
			return cardRegistry.newCard(cardHolder, label);
		} catch (ConnectException e) {
			try {
				reconnectCardRegistry();
				return cardRegistry.newCard(cardHolder, label);
			} catch (NotBoundException e1) {
				// noinspection ConstantConditions (esto no deberia pasar, hay
				// que cambiar esto en el contrato para avisar)
				return null; // @ToDo cambiar a algo más representativo
			} catch (RemoteException e1) {
				return null;
			}
		}
	}

	private void reconnectCardRegistry() throws NotBoundException {
		cardRegistry = Utils.lookupObject(CARD_REGISTRY_BIND);
	}

	@Nullable
	@Override
	public Card getCard(@Nonnull UID id) throws RemoteException {
		delay();
		try {
			return cardRegistry.getCard(checkNotNull(id));
		} catch (ConnectException e) {
			try {
				reconnectCardRegistry();
				return cardRegistry.getCard(checkNotNull(id));
			} catch (NotBoundException e1) {
				return null; // @ToDo cambiar a algo más representativo
			} catch (RemoteException e1) {
				return null;
			}
		}
	}

	@Override
	public double getCardBalance(@Nonnull UID id) throws RemoteException {

		// CardService s = getCardService();
		// assignOperation(s);
		// double result;
		// try {
		// result = s.getCardBalance(id);
		// } catch (ConnectException e) {
		// cardServiceRegistry.troubleshootFailedService(s);
		// return CardRegistry.COMMUNICATIONS_FAILURE;
		// }
		// leaveOperation(s);

		ExecutorService es = Executors.newCachedThreadPool();
		CountDownLatch done = new CountDownLatch(1);
		TimeoutTravelChecker TimeoutCheck = new TimeoutTravelChecker(
				cardServiceRegistry, this, Action.GETCARDBALANCE, id, "", 0);

		es.execute(TimeoutCheck);
		try {
			if (es.awaitTermination(CardClientImpl.TIME_OUT,
					TimeUnit.MILLISECONDS)) {
				return CardRegistry.SERVICE_TIMEOUT;
			} else {
				return TimeoutCheck.getResult();
			}
		} catch (InterruptedException e) {
			return CardRegistry.CANNOT_PROCESS_REQUEST;
		}

	}

	@Override
	public double travel(@Nonnull UID id, @Nonnull String description,
			double amount) throws RemoteException {
		// CardService s = getCardService();
		// assignOperation(s);
		// double result;
		// try {
		// result = s.travel(id, description, amount);
		// } catch (ConnectException e) {
		// cardServiceRegistry.troubleshootFailedService(s);
		// return CardRegistry.COMMUNICATIONS_FAILURE;
		// }
		// leaveOperation(s);

		ExecutorService es = Executors.newCachedThreadPool();
		CountDownLatch done = new CountDownLatch(1);
		TimeoutTravelChecker TimeoutCheck = new TimeoutTravelChecker(
				cardServiceRegistry, this, Action.TRAVEL, id, "", 0);
		es.execute(TimeoutCheck);

		boolean timeout;
		try {
			timeout = done
					.await(CardClientImpl.TIME_OUT, TimeUnit.MILLISECONDS);
			if (!timeout) {
				return CardRegistry.SERVICE_TIMEOUT;
			}

			return TimeoutCheck.getResult();
		} catch (InterruptedException e) {
			return CardRegistry.COMMUNICATIONS_FAILURE;
		}
	}

	@Override
	public double recharge(@Nonnull UID id, @Nonnull String description,
			double amount) throws RemoteException {
		// CardService s = getCardService();
		// assignOperation(s);
		// // @ToDo catch de excepciones
		// double result;
		// try {
		// result = s.recharge(id, description, amount);
		// } catch (ConnectException e) {
		// cardServiceRegistry.troubleshootFailedService(s);
		// return CardRegistry.COMMUNICATIONS_FAILURE;
		// }
		// leaveOperation(s);
		ExecutorService es = Executors.newCachedThreadPool();
		CountDownLatch done = new CountDownLatch(1);
		TimeoutTravelChecker TimeoutCheck = new TimeoutTravelChecker(
				cardServiceRegistry, this, Action.RECHARGE, id, "", 0);
		es.execute(TimeoutCheck);

		boolean timeout;
		try {
			timeout = done
					.await(CardClientImpl.TIME_OUT, TimeUnit.MILLISECONDS);
			if (!timeout) {
				return CardRegistry.SERVICE_TIMEOUT;
			}

			return TimeoutCheck.getResult();
		} catch (InterruptedException e) {
			return CardRegistry.COMMUNICATIONS_FAILURE;
		}
	}

	private CardService getCardService() {
		return cardServiceRegistry.getCardService();
	}

	private void assignOperation(CardService service) {
		cardServiceRegistry.assignOperation(service);
	}

	private void leaveOperation(CardService service) {
		cardServiceRegistry.leaveOperation(service);
	}

	@Override
	public ConcurrentHashMap<UID, Double> synchronizeToServer()
			throws RemoteException {
		return null;
	}

	private static class TimeoutTravelChecker implements Runnable {

		private CardServiceRegistryImpl cardRegistry;
		private CardClientImpl c;
		private Action action;
		private UID id;
		private String description;
		private double amount;
		private double result = 0;

		TimeoutTravelChecker(CardServiceRegistryImpl cardRegistry,
				CardClientImpl c, Action action, @Nonnull UID id,
				@Nonnull String description, double amount) {
			this.cardRegistry = cardRegistry;
			this.c = c;
			this.action = action;
			this.id = id;
			this.description = description;
			this.amount = amount;
		}

		@Override
		public void run() {
			CardService s = c.getCardService();
			if (action.equals(Action.GETCARDBALANCE)) {
				c.assignOperation(s);

				try {
					result = s.getCardBalance(id);
				} catch (RemoteException e) {
					cardRegistry.troubleshootFailedService(s);
					result = CardRegistry.COMMUNICATIONS_FAILURE;
				}

				c.leaveOperation(s);
			} else if (action.equals(Action.TRAVEL)) {
				c.assignOperation(s);

				try {
					result = s.travel(id, description, amount);
				} catch (RemoteException e) {
					cardRegistry.troubleshootFailedService(s);
					result = CardRegistry.COMMUNICATIONS_FAILURE;
				}

				c.leaveOperation(s);
			} else if (action.equals(Action.RECHARGE)) {
				c.assignOperation(s);

				try {
					result = s.recharge(id, description, amount);
				} catch (RemoteException e) {
					cardRegistry.troubleshootFailedService(s);
					result = CardRegistry.COMMUNICATIONS_FAILURE;
				}

				c.leaveOperation(s);
			}

		}

		public double getResult() {
			return this.result;
		}

	}

	private enum Action {
		TRAVEL, RECHARGE, GETCARDBALANCE;
	}
}
