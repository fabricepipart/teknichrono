package org.trd.app.teknichrono.model.dto;

import fr.xebia.extras.selma.Selma;
import org.trd.app.teknichrono.model.dto.mapper.SessionMapper;
import org.trd.app.teknichrono.model.jpa.Session;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SessionDTO {

    private final static SessionMapper MAPPER = Selma.builder(SessionMapper.class).build();

    private long id;
    private int version;
    private Instant start;
    private long inactivity = 60000L;
    private Instant end;
    private String type;
    private boolean current = false;
    private List<NestedChronometerDTO> chronometers = new ArrayList<>();
    private String name;
    private NestedLocationDTO location;
    private NestedEventDTO event = null;
    private Set<NestedPilotDTO> pilots = new HashSet<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public long getInactivity() {
        return inactivity;
    }

    public void setInactivity(long inactivity) {
        this.inactivity = inactivity;
    }

    public Instant getEnd() {
        return end;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public List<NestedChronometerDTO> getChronometers() {
        return chronometers;
    }

    public void setChronometers(List<NestedChronometerDTO> chronometers) {
        this.chronometers = chronometers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NestedLocationDTO getLocation() {
        return location;
    }

    public void setLocation(NestedLocationDTO location) {
        this.location = location;
    }

    public NestedEventDTO getEvent() {
        return event;
    }

    public void setEvent(NestedEventDTO event) {
        this.event = event;
    }

    public Set<NestedPilotDTO> getPilots() {
        return pilots;
    }

    public void setPilots(Set<NestedPilotDTO> pilots) {
        this.pilots = pilots;
    }

    @Override
    public String toString() {
        return "SessionDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public static SessionDTO fromSession(Session session) {
        return MAPPER.asSessionDto(session);
    }
}
