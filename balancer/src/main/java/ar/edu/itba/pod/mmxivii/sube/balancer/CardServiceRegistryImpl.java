package ar.edu.itba.pod.mmxivii.sube.balancer;

import ar.edu.itba.pod.mmxivii.sube.common.CardService;
import ar.edu.itba.pod.mmxivii.sube.common.CardServiceRegistry;

import javax.annotation.Nonnull;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CardServiceRegistryImpl extends UnicastRemoteObject implements
		CardServiceRegistry {
	private static final long serialVersionUID = 2473638728674152366L;
	private static final int LEAVE_OPERATION = -1;
	private static final int ENTER_OPERATION = 1;

	private final List<CardService> serviceList = Collections
			.synchronizedList(new ArrayList<CardService>());
	private final Map<CardService, Integer> serviceConnections = Collections
			.synchronizedMap(new HashMap<CardService, Integer>());
	private final List<CardService> servicesToDisconnect = Collections
			.synchronizedList(new ArrayList<CardService>());

	protected CardServiceRegistryImpl() throws RemoteException {
	}

	@Override
	public void registerService(@Nonnull CardService service)
			throws RemoteException {
		if (!serviceList.contains(service)) {
			serviceList.add(service);
			serviceConnections.put(service, 0);
		}
	}

	@Override
	public void unRegisterService(@Nonnull CardService service)
			throws RemoteException {
		if (serviceList.contains(service)) {
			if (serviceConnections.get(service) <= 0) {
				//no esta haciendo ninguna operacion, entonces puede desconectarse tranquilamente
				serviceList.remove(service);
			} else {
				// se lo agrega para desconectar y, cuando termina todas las operaciones que tenia que hacer, se desconecta
				servicesToDisconnect.add(service);
			}
		}
	}

	@Override
	public Collection<CardService> getServices() throws RemoteException {

		return serviceList;

	}

	CardService getCardService() {
		CardService s = null;
		Integer connectionsQty = Integer.MAX_VALUE;
		for (Entry<CardService, Integer> e : serviceConnections.entrySet()) {
			//si no esta para desconectarse
			if (!servicesToDisconnect.contains(e.getKey())) {
				if (e.getValue() < connectionsQty) {

					s = e.getKey();
					connectionsQty = e.getValue();
				}
			}
		}
		return s;
	}

	void assignOperation(CardService service) {

		if (service != null && serviceList.contains(service)) {
			deltaOperation(service, ENTER_OPERATION);
		}
	}

	void leaveOperation(CardService service) {

		if (service != null && serviceList.contains(service)) {
			deltaOperation(service, LEAVE_OPERATION);
		}
	}

	private void deltaOperation(CardService service, int qty) {
		Integer operations = serviceConnections.get(service);
		operations = operations + qty;
		if (operations >= 0) {
			serviceConnections.put(service, operations);
		}
	}

}
