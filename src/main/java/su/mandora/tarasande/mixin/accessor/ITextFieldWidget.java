package su.mandora.tarasande.mixin.accessor;

import java.awt.*;

public interface ITextFieldWidget {
    boolean invokeIsEditable();

    void setSelecting(boolean selecting);

    void eraseOffset(int offset);

    void setForceText(String text);

    void setColor(Color color);
}
