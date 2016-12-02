
public class NNDriverGenome extends AbstractGenome {
    public NetworkWrapper network;

    public NNDriverGenome(String networkLocation, String networkType){
        switch (networkType){
            case "NEAT":
                network = new NEATNetworkWrapper(networkLocation);
                break;
            case "RNN":
                break;
            default:
                network = new NEATNetworkWrapper(networkLocation);
                break;
        }
    }

    public NNDriverGenome(String networkLocation){
        this(networkLocation, "NEAT");
    }

    public NNDriverGenome(){
        this(Const.ALL_NN_FNAME);
    }
}

