package ar.edu.itba.pod.mmxivii.sube.service;

import ar.edu.itba.pod.mmxivii.sube.common.BaseMain;
import ar.edu.itba.pod.mmxivii.sube.common.CardRegistry;
import ar.edu.itba.pod.mmxivii.sube.common.CardServiceRegistry;
import ar.edu.itba.pod.mmxivii.sube.common.Utils;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.Scanner;

import org.jgroups.*;

import static ar.edu.itba.pod.mmxivii.sube.common.Utils.CARD_REGISTRY_BIND;
import static ar.edu.itba.pod.mmxivii.sube.common.Utils.CARD_SERVICE_REGISTRY_BIND;

public class Main extends BaseMain implements Receiver {

    public static final String CACHE_CARD_REGISTRY_BIND = "cacheCardRegistry";

    private CardRegistry cardRegistry;
	private CardServiceRegistry cardServiceRegistry;
	private CardServiceImpl cardService;

    private final static String CLUSTER_NAME = "cacheCluster";

    private JChannel channel;

    private Main(@Nonnull String[] args) throws RemoteException, NotBoundException
	{
		super(args, DEFAULT_CLIENT_OPTIONS);
        try{
            /**
             * me conecto en el cluste de caches
             */
            connectToCacheCluster();

            /**
             * obtengo mi instancia de cardRegistry, de las cache intento primero y
             * de no haber ninguna del server por RMI
             */
            obtainCardRegistryInstance();

            buildCardService();

            obtainCardServiceRegistryFromBalancer();

        }catch (EndOfProgramException eop){
            System.out.println(eop.getMessage());
            closeAllAndExit();
        }

	}

    private void obtainCardServiceRegistryFromBalancer() throws EndOfProgramException {
        try {
            cardServiceRegistry = Utils.lookupObject(CARD_SERVICE_REGISTRY_BIND);
        } catch (NotBoundException e) {
            throw new EndOfProgramException(
                    String.format("No se pudieron obtener %s desde el balancer", CARD_SERVICE_REGISTRY_BIND)
            );
        }
    }

    private void obtainCardRegistryInstance() throws EndOfProgramException {
        cardRegistry = getCardRegistryFromAnyCluster();
        if (cardRegistry == null) {
            try {
                obtainCardRegistryInstanceFromServer();
                bindCardRegistryInstanceForCaches();
            } catch (Exception e) {
                throw new EndOfProgramException(
                        String.format("No se pudieron obtener %s desde el server", CARD_REGISTRY_BIND)
                );
            }
        }
    }

    private void bindCardRegistryInstanceForCaches() {
        try{
            rmiRegistry.bind(CACHE_CARD_REGISTRY_BIND, (RemoteObject) cardRegistry);
        }catch (Exception e){
            /* ya ha sido bindeado */
        }
    }

    private void obtainCardRegistryInstanceFromServer() throws NotBoundException {
        getRegistry();
        cardRegistry = Utils.lookupObject(CARD_REGISTRY_BIND);
    }

    private void closeAllAndExit() {
        if(channel != null){
            channel.close();
        }
        System.exit(0);
    }

    private void buildCardService() throws EndOfProgramException {
        try{
            getRegistry();
            cardService = new CardServiceImpl(cardRegistry, channel);
        }catch (Exception e) {
            throw new EndOfProgramException("No se pudo instancia CardService");
        }
    }

    private void connectToCacheCluster() throws EndOfProgramException {
        try{
            channel = new JChannel();
            channel.setReceiver(this);
            channel.connect(CLUSTER_NAME);
        }catch (Exception e){
            throw new EndOfProgramException("No me pude conectar al cluster");
        }

    }

    public static void main(@Nonnull String[] args) throws Exception
	{
		final Main main = new Main(args);
		main.run();
	}

	private void run() throws RemoteException
	{
		cardServiceRegistry.registerService(cardService);
		System.out.println("Starting Service!");
		final Scanner scan = new Scanner(System.in);
		String line;
		do {
			line = scan.next();
			System.out.println("Service running");
		} while(!"x".equals(line));
		System.out.println("Service exit.");
		System.exit(0);

	}

    @Override
    public void viewAccepted(View addresses) {

    }

    @Override
    public void suspect(Address address) {

    }

    @Override
    public void block() {

    }

    @Override
    public void unblock() {

    }

    @Override
    public void receive(Message msg) {
        if (msg.getSrc() != this.channel.getAddress()) {
            OperationDTO dto = (OperationDTO) msg.getObject();

            if(dto.isAMessage()){
                if(dto.isClearLocalRegistry()){
                    /**
                     * Si es el mensaje clear balance entonces reinicio el mapa de balances local.
                     * Esto pasa cuando el cordinador da la señal a cada uno de sus caches para que reinicien
                     * el saldo una vez que sea sincronizado el labor de caches de un lapso con el server
                     */
                    cardService.clearLocalBalance();
                }
            }else{
                /**
                 * Actualizo el balance local de este cache con la operacion que otro cache a llevado a cabo
                 */
                try {
                    cardService.updateOperation(dto);
                } catch (RemoteException e) {
                    //TODO ver si hace falta controlar este error en el que no creo que se produzca
                }
                System.out.println(msg.getSrc() + ": " + dto );
            }


        }
    }

    @Override
    public void getState(OutputStream outputStream) throws Exception {

    }

    @Override
    public void setState(InputStream inputStream) throws Exception {

    }

    /**
     * Se obtiene la instancia de cardRegistry que fue generada por el server pero ahora
     * la tiene algun cache
     *
     * @return
     * @throws EndOfProgramException
     */
    public CardRegistry getCardRegistryFromAnyCluster() throws EndOfProgramException {
        try {
            getRegistry();
            cardRegistry = Utils.lookupObject(CACHE_CARD_REGISTRY_BIND);
        } catch ( Exception e) {
            return null;
        }
        return cardRegistry;
    }
}

