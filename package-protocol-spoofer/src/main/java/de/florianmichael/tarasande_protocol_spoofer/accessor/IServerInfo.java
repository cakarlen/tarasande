package de.florianmichael.tarasande_protocol_spoofer.accessor;

import de.florianmichael.tarasande_protocol_spoofer.multiplayerfeature.forgefaker.payload.IForgePayload;

public interface IServerInfo {

    IForgePayload tarasande_getForgePayload();

    void tarasande_setForgePayload(final IForgePayload forgePayload);

}
