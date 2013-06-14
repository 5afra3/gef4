package org.eclipse.gef4.swt.canvas.ev;

import java.util.HashMap;
import java.util.Map;

public class EventHandlerManager extends AbstractEventDispatcher {

	private Map<EventType<? extends Event>, CompositeEventHandler<? extends Event>> handlers = new HashMap<EventType<? extends Event>, CompositeEventHandler<? extends Event>>();

	public <T extends Event> void addEventFilter(EventType<T> type,
			IEventHandler<T> filter) {
		handlers(type).addEventFilter(filter);
	}

	@Override
	public Event dispatchBubblingEvent(Event event) {
		return dispatchBubblingEvent(event.getEventType(), event);
	}

	private Event dispatchBubblingEvent(EventType type, Event event) {
		handlers(type).dispatchBubblingEvent(event);
		return event;
	}

	@Override
	public Event dispatchCapturingEvent(Event event) {
		return dispatchCapturingEvent(event.getEventType(), event);
	}

	private Event dispatchCapturingEvent(EventType type, Event event) {
		handlers(type).dispatchCapturingEvent(event);
		return event;
	}

	private CompositeEventHandler<? extends Event> handlers(EventType type) {
		if (!handlers.containsKey(type)) {
			handlers.put(type, new CompositeEventHandler<Event>());
		}
		return handlers.get(type);
	}

}
