package keystrokesmod.event.render;

import keystrokesmod.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class PreOrientCameraEvent extends Event {
    private float smooth;
}
