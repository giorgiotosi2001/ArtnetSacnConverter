package com.coemar.bridge.rdm;

import static com.coemar.bridge.rdm.RdmConstants.*;

public class RdmHandler {

    private final RdmGetHandler getHandler = new RdmGetHandler();
    private final RdmSetHandler setHandler = new RdmSetHandler();
    private final RdmDiscoveryHandler discoveryHandler = new RdmDiscoveryHandler();


    public void handle(RdmFrame frame) {
        switch (frame.getCommandClass()) {
            case E120_GET_COMMAND: // GET_COMMAND
                getHandler.handleGet(frame);
                break;

            case E120_SET_COMMAND: // SET_COMMAND
                setHandler.handleSet(frame);
                break;

            case E120_DISCOVERY_COMMAND: // DISCOVERY_COMMAND
                discoveryHandler.handleDiscovery(frame);
                break;

            default:
                throw new IllegalArgumentException("Command class non supportata");
        }
    }







}
