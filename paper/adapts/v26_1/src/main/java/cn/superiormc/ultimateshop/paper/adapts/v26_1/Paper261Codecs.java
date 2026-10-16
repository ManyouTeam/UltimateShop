package cn.superiormc.ultimateshop.paper.adapts.v26_1;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodecRegistry;
import cn.superiormc.ultimateshop.paper.adapts.VersionCodecs;

public class Paper261Codecs implements VersionCodecs {

    @Override
    public void registerCodecs(ComponentCodecRegistry registry) {
        registry.register(new DamageResistantCodec());
        registry.register(new BlocksAttacksCodec());
    }

}
