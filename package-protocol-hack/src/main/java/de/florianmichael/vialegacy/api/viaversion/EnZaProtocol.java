/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 6/24/22, 12:56 PM
 *
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.0--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license.
 */
/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.vialegacy.api.viaversion;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import de.florianmichael.vialegacy.api.material.MaterialReplacement;
import de.florianmichael.vialegacy.api.metadata.LegacyMetadataRewriter;
import de.florianmichael.vialegacy.api.sound.SoundRewriter;

public class EnZaProtocol<C1 extends ClientboundPacketType, C2 extends ClientboundPacketType, S1 extends ServerboundPacketType, S2 extends ServerboundPacketType> extends AbstractProtocol<C1, C2, S1, S2> {

    public EnZaProtocol() {
        super(null, null, null, null);
    }

    public EnZaProtocol(Class<C1> oldClientboundPacketEnum, Class<C2> clientboundPacketEnum, Class<S1> oldServerboundPacketEnum, Class<S2> serverboundPacketEnum) {
        super(oldClientboundPacketEnum, clientboundPacketEnum, oldServerboundPacketEnum, serverboundPacketEnum);
    }

    public SoundRewriter soundRewriter() {
        return null;
    }

    public MaterialReplacement materialReplacement() {
        return null;
    }

    public LegacyMetadataRewriter metadataRewriter() {
        return null;
    }
}