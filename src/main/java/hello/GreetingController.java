package hello;

import jota.IotaAPI;
import jota.dto.response.GetBalancesAndFormatResponse;
import jota.dto.response.GetNewAddressResponse;
import jota.dto.response.GetNodeInfoResponse;
import jota.dto.response.SendTransferResponse;
import jota.error.ArgumentException;
import jota.model.Input;
import jota.model.Transfer;
import jota.utils.TrytesConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class GreetingController {

    @Autowired
    private IotaInputRepository repository;

    IotaAPI iotaAPI;
    String seed;
    int security = 2;
    String address = "GHLEMUXUIYJK9SKXOUMNEKZBRDHZVFUURXOYMQQODSWEGYRROOGGILVYTGTCCLOYDCETCKHUT9LRXJMMD";
    IotaInput currentInput;

    public GreetingController() {
        this.iotaAPI = new IotaAPI.Builder()
                .protocol("https")
                .host("nodes.devnet.thetangle.org")
                .port("443")
                .build();
        GetNodeInfoResponse response = null;
        try {
            response = this.iotaAPI.getNodeInfo();
        } catch (ArgumentException e) {
            e.printStackTrace();
        }
        //System.out.println(response);

        this.seed = System.getenv("SEED");
        System.out.println("SEED " + this.seed);

    }

    @PostConstruct
    void setup() {
        Optional<IotaInput> optionalInput = repository.findById("0");
        this.currentInput = optionalInput.orElse(new IotaInput(400));
    }


    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {

        GetNewAddressResponse getNewAddressResponse = iotaAPI.getNextAvailableAddress(seed, security, false);
        String remainderAddress = getNewAddressResponse.getAddresses().get(0);

        List<Input> inputs = new ArrayList<>();
        /*

        TODO
        stocker le key index
        trop long de récupérer 500 inputs

         */
        int endIndex = this.currentInput.getKeyIndex();
        GetBalancesAndFormatResponse rsp = iotaAPI.getInputs(seed, security, 0, endIndex + 10, 1);
        inputs.addAll(rsp.getInputs());

        this.currentInput.setKeyIndex(rsp.getInputs().get(0).getKeyIndex());
        repository.save(this.currentInput);

        String iotaMessage = "JUSTANOTHERIOTATEST";
        String tag = "ADRIEN";
        List<Transfer> transfers = new ArrayList<>();
        Transfer transfer = new Transfer(address,0, TrytesConverter.asciiToTrytes("un message"), TrytesConverter.asciiToTrytes("un tag"));
        transfers.add(transfer);
        SendTransferResponse rep = iotaAPI.sendTransfer(seed, security, 3, 9, transfers, inputs, remainderAddress, true, true, null);

        rep.getTransactions().forEach(transaction -> {
            System.out.println("http://tangle.glumb.de:8080/?hash=" + transaction.getHash());
        });

        //Thread.sleep(1000); // simulated delay
        //return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");

        return new Greeting("http://tangle.glumb.de:8080/?hash=" + rep.getTransactions().get(0).getHash());
    }

}