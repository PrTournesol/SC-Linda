package linda.shm;

/**
 * This class defines the different states a client can be in.
 * The server will act according to it
 *
 * @author dkleiber and Philippe Leleux
 * @version 1.1
 */
public enum ClientEtat {
        write,
        read,
        take,
        trytake,
        tryread,
        takeall,
        readall,
        register,
        nothing
}
