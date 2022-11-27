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

package de.florianmichael.vialegacy;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialegacy.exception.ViaLegacyException;
import de.florianmichael.vialegacy.protocols.base.BaseProtocol1_6;
import de.florianmichael.vialegacy.protocols.protocol1_1to1_0_0_1.Protocol1_1to1_0_0_1;
import de.florianmichael.vialegacy.protocols.protocol1_2_1_3to1_1.Protocol1_2_1_3to1_1;
import de.florianmichael.vialegacy.protocols.protocol1_2_4_5to1_2_1_3.Protocol1_2_4_5to1_2_1_3;
import de.florianmichael.vialegacy.protocols.protocol1_3_1_2to1_2_4_5.Protocol1_3_1_2to1_2_4_5;
import de.florianmichael.vialegacy.protocols.protocol1_4_0_2to1_3_1_2.Protocol1_4_0_2to1_3_1_2;
import de.florianmichael.vialegacy.protocols.protocol1_4_3_preto1_4_0_2.Protocol1_4_3_preto1_4_0_2;
import de.florianmichael.vialegacy.protocols.protocol1_4_4_5to1_4_3_pre.Protocol1_4_5to1_4_3_pre;
import de.florianmichael.vialegacy.protocols.protocol1_4_6_7to1_4_4_5.Protocol1_4_6_7to1_4_4_5;
import de.florianmichael.vialegacy.protocols.protocol1_5_1to1_4_6_7.Protocol1_5_1to1_4_6_7;
import de.florianmichael.vialegacy.protocols.protocol1_5_2to1_5_1.Protocol1_5_2to1_5_1;
import de.florianmichael.vialegacy.protocols.protocol1_6_1to1_5_2.Protocol1_6_1to1_5_2;
import de.florianmichael.vialegacy.protocols.protocol1_6_2to1_6_1.Protocol1_6_2to1_6_1;
import de.florianmichael.vialegacy.protocols.protocol1_6_3to1_6_2.Protocol1_6_3_preto1_6_2;
import de.florianmichael.vialegacy.protocols.protocol1_6_4to1_6_3pre.Protocol1_6_4to1_6_3_pre;
import de.florianmichael.vialegacy.protocols.protocol1_7_6_10to1_7_0_5.Protocol1_7_6_10to1_7_0_5;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.Protocol1_7_5to1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.Protocol1_8_0_9to1_7_6_10;

import java.util.logging.Logger;

import static com.viaversion.viaversion.api.protocol.version.ProtocolVersion.*;
import static de.florianmichael.vialegacy.protocol.LegacyProtocolVersion.*;

/**
 * ViaLegacy is a rewrite of PrivateViaForge (2020 - 2021)
 
 * BUG TRACKER:
 *  - 1.1 Chunks needs a rewrite
 *  - 1.6.4 Login is totally broken and unclear
 *  - 1.5.2 Minecart logic is missing
 *  - 1.0 Chat filtering is missing
 *  - Biome data Remapping is missing (needs st. like AbstractChunkTracker)
 *  - 1.7.5 (?): Bed flag
 *  - 1.6.4 (?): Ender pearl
 *  - 1.6.4 (?): Book writing
 */
public class ViaLegacy {

    private static Logger logger;

    public static void init(final Logger logger) {
        if (ViaLegacy.logger != null) {
            throw new ViaLegacyException("ViaLegacy is already loaded!");
        }

        ViaLegacy.logger = logger;

        // Release Versions (1.8.x -> 1.0.0)
        //               From           Target        Protocol Implementation
        registerProtocol(v1_8,          r1_7_6_10,    new Protocol1_8_0_9to1_7_6_10());
        registerProtocol(r1_7_6_10,     r1_7_0_5,     new Protocol1_7_6_10to1_7_0_5());
        registerProtocol(r1_7_0_5,      r1_6_4,       new Protocol1_7_5to1_6_4());
        registerProtocol(r1_6_4,        r1_6_3_pre,   new Protocol1_6_4to1_6_3_pre());
        registerProtocol(r1_6_3_pre,    r1_6_2,       new Protocol1_6_3_preto1_6_2());
        registerProtocol(r1_6_2,        r1_6_1,       new Protocol1_6_2to1_6_1());
        registerProtocol(r1_6_1,        r1_5_2,       new Protocol1_6_1to1_5_2());
        registerProtocol(r1_5_2,        r1_5_1,       new Protocol1_5_2to1_5_1());
        registerProtocol(r1_5_1,        r1_4_6_7,     new Protocol1_5_1to1_4_6_7());
        registerProtocol(r1_4_6_7,      r1_4_4_5,     new Protocol1_4_6_7to1_4_4_5());
        registerProtocol(r1_4_4_5,      r1_4_3_pre,   new Protocol1_4_5to1_4_3_pre());
        registerProtocol(r1_4_3_pre,    r1_4_0_2,     new Protocol1_4_3_preto1_4_0_2());
        registerProtocol(r1_4_0_2,      r1_3_1_2,     new Protocol1_4_0_2to1_3_1_2());
        registerProtocol(r1_3_1_2,      r1_2_4_5,     new Protocol1_3_1_2to1_2_4_5());
        registerProtocol(r1_2_4_5,      r1_2_1_3,     new Protocol1_2_4_5to1_2_1_3());
        registerProtocol(r1_2_1_3,      r1_1,         new Protocol1_2_1_3to1_1());
        registerProtocol(r1_1,          r1_0_0_1,     new Protocol1_1to1_0_0_1());

        // Beta Versions (b1.8.1 - b1.0)

        // Alpha Versions (a1.2.6 - a1.0.15)

        // Classic Versions (c0.30 - c0.0.15a-1)

        final int newestVersion = PROTOCOL_VERSIONS.get(2).getVersion();
        final int lastVersion = PROTOCOL_VERSIONS.get(PROTOCOL_VERSIONS.size() - 1).getVersion();

        getLogger().info("Base Protocol from " + newestVersion + " to " + lastVersion + "!");

        Via.getManager().getProtocolManager().registerBaseProtocol(BaseProtocol1_6.INSTANCE, Range.range(newestVersion, BoundType.OPEN, lastVersion, BoundType.CLOSED));
    }

    private static void registerProtocol(final ProtocolVersion from, final ProtocolVersion to, final Protocol<?, ?, ?, ?> protocol) {
        try {
            Via.getManager().getProtocolManager().registerProtocol(protocol, from, to);
        } catch (Exception e) {
            e.printStackTrace();
        }

        getLogger().info("Loading " + from.getName() + " -> " + to.getName() + " mappings...");
    }

    public static Logger getLogger() {
        return logger;
    }
}
