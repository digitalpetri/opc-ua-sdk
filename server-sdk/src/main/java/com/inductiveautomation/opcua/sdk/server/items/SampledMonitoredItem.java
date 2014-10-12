package com.inductiveautomation.opcua.sdk.server.items;

import com.inductiveautomation.opcua.sdk.server.api.SampledItem;
import com.inductiveautomation.opcua.sdk.server.util.DataChangeMonitoringFilter;
import com.inductiveautomation.opcua.stack.core.StatusCodes;
import com.inductiveautomation.opcua.stack.core.UaException;
import com.inductiveautomation.opcua.stack.core.types.builtin.DataValue;
import com.inductiveautomation.opcua.stack.core.types.builtin.DateTime;
import com.inductiveautomation.opcua.stack.core.types.builtin.ExtensionObject;
import com.inductiveautomation.opcua.stack.core.types.builtin.StatusCode;
import com.inductiveautomation.opcua.stack.core.types.builtin.Variant;
import com.inductiveautomation.opcua.stack.core.types.enumerated.DataChangeTrigger;
import com.inductiveautomation.opcua.stack.core.types.enumerated.DeadbandType;
import com.inductiveautomation.opcua.stack.core.types.enumerated.MonitoringMode;
import com.inductiveautomation.opcua.stack.core.types.enumerated.TimestampsToReturn;
import com.inductiveautomation.opcua.stack.core.types.structured.AggregateFilter;
import com.inductiveautomation.opcua.stack.core.types.structured.DataChangeFilter;
import com.inductiveautomation.opcua.stack.core.types.structured.EventFilter;
import com.inductiveautomation.opcua.stack.core.types.structured.MonitoredItemNotification;
import com.inductiveautomation.opcua.stack.core.types.structured.MonitoringFilter;
import com.inductiveautomation.opcua.stack.core.types.structured.MonitoringParameters;
import com.inductiveautomation.opcua.stack.core.types.structured.ReadValueId;

public class SampledMonitoredItem extends BaseMonitoredItem<DataValue> implements SampledItem {

    private static final DataChangeFilter DefaultFilter = new DataChangeFilter(
            DataChangeTrigger.StatusValue,
            (long) DeadbandType.None.getValue(),
            0.0
    );

    private volatile DataValue lastValue = null;
    private volatile DataChangeFilter filter = null;
    private volatile ExtensionObject filterResult = null;

    public SampledMonitoredItem(long id,
                                ReadValueId readValueId,
                                MonitoringMode monitoringMode,
                                TimestampsToReturn timestamps,
                                MonitoringParameters parameters) {

        super(id, readValueId, monitoringMode, timestamps, parameters);
    }

    @Override
    public synchronized void setValue(DataValue value) {
        boolean valuePassesFilter = DataChangeMonitoringFilter.filter(lastValue, value, filter);

        if (valuePassesFilter) {
            if (queue.size() < queue.maxSize()) {
                queue.add(value);
            } else {
                if (discardOldest) {
                    queue.add(value);
                } else {
                    queue.set(queue.maxSize() - 1, value);
                }
            }

            lastValue = value;
        }
    }

    @Override
    public synchronized void setQuality(StatusCode quality) {
        if (lastValue == null) {
            setValue(new DataValue(Variant.NullValue, quality, DateTime.now(), DateTime.now()));
        } else {
            DataValue value = new DataValue(
                    lastValue.getValue(),
                    quality,
                    DateTime.now(),
                    DateTime.now()
            );

            setValue(value);
        }
    }

    @Override
    public boolean isSamplingEnabled() {
        return monitoringMode != MonitoringMode.Disabled;
    }

    @Override
    protected void installFilter(ExtensionObject filterXo) throws UaException {
        if (filterXo == null || filterXo.getObject() == null) {
            this.filter = DefaultFilter;
        } else {
            Object filter = filterXo.getObject();

            if (filter instanceof MonitoringFilter) {
                if (filter instanceof DataChangeFilter) {
                    this.filter = ((DataChangeFilter) filter);
                } else if (filter instanceof AggregateFilter) {
                    throw new UaException(StatusCodes.Bad_MonitoredItemFilterUnsupported);
                } else if (filter instanceof EventFilter) {
                    throw new UaException(StatusCodes.Bad_FilterNotAllowed);
                }
            } else {
                throw new UaException(StatusCodes.Bad_MonitoredItemFilterInvalid);
            }
        }
    }

    @Override
    public ExtensionObject getFilterResult() {
        return filterResult;
    }

    @Override
    protected MonitoredItemNotification wrapQueueValue(DataValue value) {
        return new MonitoredItemNotification(getClientHandle(), value);
    }

    public static SampledMonitoredItem create(long id,
                                              ReadValueId readValueId,
                                              MonitoringMode monitoringMode,
                                              TimestampsToReturn timestamps,
                                              MonitoringParameters parameters) throws UaException {

        SampledMonitoredItem item = new SampledMonitoredItem(id, readValueId, monitoringMode, timestamps, parameters);
        item.installFilter(parameters.getFilter());
        return item;
    }

}
