package com.inductiveautomation.opcua.sdk.server.model.variables;

import java.util.Optional;

import com.inductiveautomation.opcua.sdk.core.model.variables.ServerVendorCapabilityType;
import com.inductiveautomation.opcua.sdk.server.api.UaNodeManager;
import com.inductiveautomation.opcua.sdk.server.util.UaVariableType;
import com.inductiveautomation.opcua.stack.core.types.builtin.DataValue;
import com.inductiveautomation.opcua.stack.core.types.builtin.LocalizedText;
import com.inductiveautomation.opcua.stack.core.types.builtin.NodeId;
import com.inductiveautomation.opcua.stack.core.types.builtin.QualifiedName;
import com.inductiveautomation.opcua.stack.core.types.builtin.unsigned.UByte;
import com.inductiveautomation.opcua.stack.core.types.builtin.unsigned.UInteger;

@UaVariableType(name = "ServerVendorCapabilityType")
public class ServerVendorCapabilityNode extends BaseDataVariableNode implements ServerVendorCapabilityType {

    public ServerVendorCapabilityNode(UaNodeManager nodeManager,
                                      NodeId nodeId,
                                      QualifiedName browseName,
                                      LocalizedText displayName,
                                      Optional<LocalizedText> description,
                                      Optional<UInteger> writeMask,
                                      Optional<UInteger> userWriteMask,
                                      DataValue value,
                                      NodeId dataType,
                                      Integer valueRank,
                                      Optional<UInteger[]> arrayDimensions,
                                      UByte accessLevel,
                                      UByte userAccessLevel,
                                      Optional<Double> minimumSamplingInterval,
                                      boolean historizing) {

        super(nodeManager, nodeId, browseName, displayName, description, writeMask, userWriteMask,
                value, dataType, valueRank, arrayDimensions, accessLevel, userAccessLevel, minimumSamplingInterval, historizing);

    }


    @Override
    public void atomicAction(Runnable runnable) {
        runnable.run();
    }

}
