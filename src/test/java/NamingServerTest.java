import com.cs328.myrmi.Naming;
import com.cs328.myrmi.TestRemoteImpl;
import com.cs328.myrmi.TestRemoteInterface;
import com.cs328.myrmi.registry.LocateRegistry;
import com.cs328.myrmi.server.UnicastRemoteObject;
public class NamingServerTest {
    public static void main(String[] args) {
        LocateRegistry.createRegistry(8080);
        TestRemoteInterface impl = new TestRemoteImpl();
        UnicastRemoteObject.exportObject(impl);
        Naming.rebind("rmi://localhost:8080/test", impl);
    }
}
