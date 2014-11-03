package ar.edu.itba.pod.mmxivii.sube.service;

import ar.edu.itba.pod.mmxivii.sube.common.CardRegistry;
import ar.edu.itba.pod.mmxivii.sube.common.CardService;
import org.jgroups.Channel;
import org.jgroups.Message;

import javax.annotation.Nonnull;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

public class CardServiceImpl extends UnicastRemoteObject implements CardService
{
	private static final long serialVersionUID = 2919260533266908792L;

    private LocalCardRegistry localCardRegistry;

    @Nonnull
	private final CardRegistry cardRegistry;

    @Nonnull
    private final Channel channel;

	public CardServiceImpl(@Nonnull CardRegistry cardRegistry,@Nonnull Channel channel) throws RemoteException
	{
		super(0);
		this.cardRegistry = cardRegistry;
        this.channel = channel;
        localCardRegistry = new LocalCardRegistry(cardRegistry);
    }

	@Override
	public double getCardBalance(@Nonnull UID id) throws RemoteException
	{
		return localCardRegistry.getCardBalance(id);
	}

	@Override
	public double travel(@Nonnull UID id, @Nonnull String description, double amount) throws RemoteException
	{
        return doCardOperation(id, description, amount * -1);
	}

    @Override
    public double recharge(@Nonnull UID id, @Nonnull String description, double amount) throws RemoteException
    {
        return doCardOperation(id, description, amount );
    }


    /**
     * Hago la operacion localmente y luego de ser exitosa la distribuyo al resto de las caches
     *
     * @param id
     * @param description
     * @param amount
     * @return
     * @throws RemoteException
     */
    private double doCardOperation(UID id, String description, double amount) throws RemoteException {
        double balance = localCardRegistry.addCardOperation(id, description, amount);
        if( balance >= 0 ){
            OperationDTO dto = new OperationDTO(id, description, amount );
            sendMessageToClusters(dto);
        }
        return balance;
    }

    /**
     * Mando el mensaje de una operacion hacia las caches
     *
     * @param dto
     */
    private void sendMessageToClusters(OperationDTO dto) {
        Message msg = new Message(null, null, dto);
        try {
            channel.send(msg);
        } catch (Exception e) {
            //TODO: hacer algo con estos datos que no se replican
        }
    }


    /**
     * Operacion llevada a cabo por una cache una vez que le llega el mensaje de una operacion
     *
     * @param dto mensaje enviado por otras cache
     * @throws RemoteException
     */
    protected void updateOperation( OperationDTO dto ) throws RemoteException {
        localCardRegistry.addCardOperation( dto.id, dto.description, dto.amount );
    }

    protected void clearLocalBalance(){
        localCardRegistry.clearBalance();
    }


    @Override
    public ConcurrentHashMap<UID, Double> synchronizeToServer() throws RemoteException {
        //bajar al server
        ConcurrentHashMap<UID, Double> updatedBalance = localCardRegistry.synchronizeToSCardRegistry(cardRegistry);

        //mandar mensaje al resto de los caches para que reinicien sus localCardRegistry
        sendMessageToClusters( OperationDTO.buildClearLocalRegistryMessage() );

        return updatedBalance;
    }


}
